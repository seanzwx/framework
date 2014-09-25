package com.sean.index.core;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.sean.common.util.CollectionUtil;
import com.sean.index.config.IndexConfig;
import com.sean.index.config.L;
import com.sean.index.util.FileSys;
import com.sean.index.util.IndexUtil;
import com.sean.index.util.LocalFileSys;
import com.sean.log.core.LogFactory;

/**
 * Lucene会话，针对一个索引目录
 * @author sean
 */
@SuppressWarnings("unchecked")
public final class IndexSession
{
	private static final Logger logger = LogFactory.getLogger(L.Index);

	// 索引的目录
	private String path;
	// 硬盘索引
	private volatile Directory diskDir;
	private volatile IndexReader diskReader;
	// 内存索引
	private volatile RAMDirectory ramDir;
	private volatile IndexWriter ramWriter;

	// 过时文档列表
	private volatile Set<String> stale;
	// 在合并内存索引期间，用于保存过时文档的临时列表
	private volatile Set<String> staleTemp;
	// 过时文档硬盘备份，存储过时文档的Id，每一行一条记录
	private volatile File staleFile;

	// 内存索引硬盘备份，存储内存索引的序列化索引
	private volatile File ramFile;

	private volatile Searcher searcher;
	private volatile SearcherCleaner searcherCleaner;
	private volatile WriterCommiter writerCommiter;
	// 是否正在合并内存索引
	private volatile boolean isMerging = false;

	// 所有的线程同步锁
	private Object addRamDocBackupLock = new Object();
	private Object mergeRAMLock = new Object();
	private Object addStaleDocLock = new Object();

	private BackupReader backupReader;

	private static long RAM_SIZE;
	private static int STALE_SIZE;

	private FileSys fs;

	public IndexSession(String path, long ramSizeBytes, int staleSize, BackupReader backupReader) throws Exception
	{
		logger.info("the index of path=" + path + " start initlizing...");

		if (path.endsWith("/"))
		{
			this.path = path.substring(0, path.length() - 2);
		}
		else
		{
			this.path = path;
		}

		RAM_SIZE = ramSizeBytes;
		STALE_SIZE = staleSize;
		this.backupReader = backupReader;

		// 如果使用本地文件系统
		fs = new LocalFileSys();

		// 开始初始化
		this.init();

		logger.info("the index of path=" + path + " initlized successfully");
	}

	/**
	 * 检查索引目录结构
	 */
	private void checkDir() throws IOException
	{
		// 检查索引目录
		if (!fs.exists(path))
		{
			fs.mkdir(path);
		}
		if (!fs.exists(path + "/stale/"))
		{
			fs.mkdir(path + "/stale/");
		}
		if (!fs.exists(path + "/ram/"))
		{
			fs.mkdir(path + "/ram/");
		}
		if (fs.exists(path + "/write.lock"))
		{
			fs.deleteFile(path + "/write.lock");
		}
	}

	/**
	 * 启动时合并尚未合并的内存硬盘备份
	 * @param writer
	 * @return
	 */
	private void mergeRamDocBackupOnStartup(IndexWriter writer) throws Exception
	{
		logger.info("the index of path=" + path + " start merging ram document backup...");

		// 合并尚未合并的硬盘备份索引
		for (File ram : fs.listFiles(path + "/ram/"))
		{
			ObjectInputStream input = new ObjectInputStream(fs.openInputStream(path + "/ram/" + ram.getName()));
			Object obj = null;
			try
			{
				while ((obj = input.readObject()) != null)
				{
					// 重新添加文档
					Map<String, String> map = (Map<String, String>) obj;
					String docId = map.get(IndexConfig.KEY);
					map.remove(IndexConfig.KEY);
					Document doc = backupReader.read(map);
					if (doc != null)
					{
						doc.removeField(IndexConfig.KEY);
						doc.add(new StringField(IndexConfig.KEY, docId, Store.YES));
						writer.addDocument(doc);
						logger.debug("the index of path=" + path + " add document from ram backup : " + IndexUtil.documentToString(doc));
					}
				}
			}
			catch (EOFException ee)
			{
				continue;
			}
		}

		logger.info("the index of path=" + path + " merged ram document backup complete");
	}

	/**
	 * 启动时候删除尚未删除的过时文档
	 * @param writer
	 * @return
	 */
	private void deleteStaleDocBackupOnStartup(IndexWriter writer) throws Exception
	{
		logger.info("the index of path=" + path + " start to delete stale document backup...");

		for (File stale : fs.listFiles(path + "/stale/"))
		{
			List<String> ids = fs.readLines(path + "/stale/" + stale.getName());
			if (ids != null && !ids.isEmpty())
			{
				for (String id : ids)
				{
					writer.deleteDocuments(new Term(IndexConfig.KEY, id.replace("\r\n", "")));
					logger.debug("the index of path=" + path + " delete a stale document docId=" + id);
				}
			}
		}

		logger.info("the index of path=" + path + " delete all stale document backup complete");
	}

	/**
	 * 预初始化，做一些检查上次销毁后的工作
	 */
	private void preInit() throws Exception
	{
		logger.info("the index of path=" + path + " start preInitlizing...");

		// 检查目录结构
		checkDir();

		IndexWriter writer = IndexUtil.createIndexWriter(fs, path);

		// 第一次添加一个空文档
		if (!new File(path + "/segments.gen").exists())
		{
			writer.addDocument(new Document());
			writer.commit();
		}

		// 合并尚未合并的内存硬盘备份
		mergeRamDocBackupOnStartup(writer);

		// 删除尚未删除的文档
		deleteStaleDocBackupOnStartup(writer);

		writer.forceMergeDeletes();
		writer.commit();
		writer.close();

		// 删除过期文件
		fs.cleanDir(path + "/ram/");
		fs.cleanDir(path + "/stale/");

		logger.info("the index of path=" + path + " preInitlized successfully");
	}

	/**
	 * 初始化
	 */
	private void init() throws Exception
	{
		// 预初始化
		preInit();

		// 初始化主索引(read only)
		diskDir = IndexUtil.openDir(fs, path);
		diskReader = IndexUtil.createIndexReader(diskDir);

		// 初始化过时文档列表
		stale = new HashSet<String>(STALE_SIZE);
		// 初始化过时文档临时列表
		staleTemp = new HashSet<String>(STALE_SIZE);

		// 初始化过时文档硬盘备份
		staleFile = new File(path + "/stale/" + System.currentTimeMillis());
		// 初始化内存文档硬盘备份
		ramFile = new File(path + "/ram/" + System.currentTimeMillis());

		// 初始化内存索引(read write)
		ramDir = IndexUtil.openRAMDir();
		ramWriter = IndexUtil.createIndexWriter(ramDir);

		// 初始化搜索器
		searcher = new Searcher(diskReader, IndexUtil.createIndexReader(ramDir));

		// 初始化搜索器清理
		searcherCleaner = new SearcherCleaner();

		// 初始化writer提交
		writerCommiter = new WriterCommiter(this);
	}

	/**
	 * 请求IndexSearcher
	 */
	public Searcher getSearcher()
	{
		return searcher;
	}

	/**
	 * 刷新内存Reader
	 */
	protected void refreshRAMReader() throws Exception
	{
		synchronized (mergeRAMLock)
		{
			logger.debug("the index of path=" + path + " start refreshing ram reader...");

			// 打开新的reader，创建新的Searcher, 替换原来的Searcher
			IndexReader newReader = IndexUtil.createIndexReader(ramDir);

			Searcher newSearcher = new Searcher(diskReader, newReader);

			// 替换searcher
			Searcher tmpSearcher = this.searcher;
			this.searcher = newSearcher;

			// 清除无用searcher
			this.searcherCleaner.addUnusedSearcher(tmpSearcher);

			logger.debug("the index of path=" + path + " refreshed ram reader complete");
		}
	}

	/**
	 * 提交writer
	 */
	public void commit() throws Exception
	{
		writerCommiter.addCommitWriter(ramWriter);
	}

	/**
	 * 添加内存文档备份
	 * @param doc
	 */
	private void addRamDocBackup(Document doc) throws Exception
	{
		List<IndexableField> fields = doc.getFields();
		int length = fields.size();
		Map<String, String> map = new HashMap<String, String>(length);
		IndexableField field = null;
		for (int i = 0; i < length; i++)
		{
			field = fields.get(i);
			map.put(field.name(), field.stringValue());
		}
		// 排队写入硬盘
		synchronized (addRamDocBackupLock)
		{
			fs.writeObjectToFile(path + "/ram/" + ramFile.getName(), map, true);
		}
		// 内存达到64M，企图合并索引
		if (ramDir.sizeInBytes() >= RAM_SIZE)
		{
			this.mergeRAM();
		}
	}

	/**
	 * 添加内存文档备份
	 * @param docs
	 */
	private void addRamDocsBackup(List<Document> docs) throws Exception
	{
		List<Map<String, String>> list = new ArrayList<Map<String, String>>(docs.size());
		for (Document doc : docs)
		{
			List<IndexableField> fields = doc.getFields();
			int length = fields.size();
			Map<String, String> map = new HashMap<String, String>(length);
			IndexableField field = null;
			for (int i = 0; i < length; i++)
			{
				field = fields.get(i);
				map.put(field.name(), field.stringValue());
			}
			list.add(map);
		}
		// 排队写入硬盘
		synchronized (addRamDocBackupLock)
		{
			fs.writeObjectsToFile(path + "/ram/" + ramFile.getName(), list, true);
		}
		// 内存达到64M，企图合并索引
		if (ramDir.sizeInBytes() >= RAM_SIZE)
		{
			this.mergeRAM();
		}
	}

	/**
	 * 合并内存索引，需要耗一定时间
	 */
	private void mergeRAM() throws Exception
	{
		if (!isMerging)
		{
			synchronized (mergeRAMLock)
			{
				logger.info("the index of path=" + path + " start to merge RAM...");

				isMerging = true;
				// 创建临时数据
				RAMDirectory tmpDir = this.ramDir;
				IndexWriter tmpRamWriter = this.ramWriter;
				Set<String> tmpStale = this.stale;
				File tmpStaleFile = this.staleFile;
				File tmpRamFile = this.ramFile;
				// 将stale内的所有过时文档复制到staleTemp中
				CollectionUtil.copySet(stale, staleTemp);

				logger.debug("the index of path=" + path + " created temp data in merging RAM...");

				// 创建新的内存索引，此时，新添加或者删除的文档全部添加到这里
				ramDir = IndexUtil.openRAMDir();
				ramWriter = IndexUtil.createIndexWriter(ramDir);
				stale = new HashSet<String>(STALE_SIZE);
				staleFile = new File(path + "/stale/" + System.currentTimeMillis());
				ramFile = new File(path + "/ram/" + System.currentTimeMillis());

				logger.debug("the index of path=" + path + " initlized new data in merging RAM");

				// 将内存索引合并到主索引中
				IndexWriter mainWriter = IndexUtil.createIndexWriter(fs, path);
				IndexReader reader = IndexUtil.createIndexReader(tmpRamWriter);
				mainWriter.addIndexes(reader);
				logger.debug("the index of path=" + path + " added all ram document to main index in merging RAM");
				reader.close();

				// 清空内存
				tmpRamWriter.close();
				tmpDir.close();

				// 删除所有过时文档
				for (String docId : tmpStale)
				{
					mainWriter.deleteDocuments(new Term(IndexConfig.KEY, docId));
				}
				mainWriter.forceMergeDeletes();

				logger.debug("the index of path=" + path + " deleted all stale document in mergeing RAM");

				// 提交并关闭主索引
				mainWriter.commit();
				mainWriter.close();

				logger.debug("the index of path=" + path + " committed and closed in mergeing RAM");

				// 删除内存索引的磁盘备份
				fs.deleteFile(path + "/ram/" + tmpRamFile.getName());
				// 清空过时文档硬盘备份列表
				fs.deleteFile(path + "/stale/" + tmpStaleFile.getName());

				// 重新打开主索引，并替换主索引引用
				IndexReader newReader = IndexUtil.createIndexReader(diskDir);
				IndexReader tmp = diskReader;
				diskReader = newReader;
				tmp.close();

				logger.debug("the index of path=" + path + " reopened main index in mergeing RAM");

				// 清空搜索器
				Searcher tmpSearcher = this.searcher;
				this.searcher = new Searcher(diskReader, IndexUtil.createIndexReader(ramDir));
				searcherCleaner.addUnusedSearcher(tmpSearcher);

				// 清楚复制的过时文档列表
				staleTemp.clear();

				isMerging = false;

				logger.info("the index path=" + path + " merged RAM complete");
			}
		}
	}

	/**
	 * 关闭session，释放资源
	 */
	public void close() throws Exception
	{
		// 关闭硬盘索引
		diskReader.close();
		diskDir.close();

		// 关闭内存索引
		ramWriter.close();
		ramDir.close();

		// 关闭searcherCleaner
		searcherCleaner.destory();
		// 关闭writerCommiter
		writerCommiter.destory();
	}

	/**
	 * 添加文档
	 * @param doc
	 */
	public void addDocument(Document doc) throws Exception
	{
		this.ramWriter.addDocument(doc);
		// 添加内存文档备份
		this.addRamDocBackup(doc);
	}

	/**
	 * 添加文档
	 * @param docs
	 */
	public void addDocuments(List<Document> docs) throws Exception
	{
		for (Document doc : docs)
		{
			this.ramWriter.addDocument(doc);
		}
		this.addRamDocsBackup(docs);
	}

	/**
	 * 添加过期文档
	 * @param docId
	 */
	public void addStaleDoc(String docId) throws Exception
	{
		if (!stale.contains(docId))
		{
			this.stale.add(docId);

			// 排队写入硬盘作备份
			synchronized (addStaleDocLock)
			{
				fs.writeStringToFile(path + "/stale/" + staleFile.getName(), docId + "\r\n", true);
			}

			// 过时文档超过，企图合并索引
			if (stale.size() > STALE_SIZE)
			{
				this.mergeRAM();
			}
		}
	}

	/**
	 * 批量添加过期文档
	 * @param docIds
	 */
	public void addStaleDocs(List<String> docIds) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		for (String docId : docIds)
		{
			if (!stale.contains(docId))
			{
				this.stale.add(docId);
				sb.append(docId).append("\r\n");
			}
		}

		// 排队写入删除文档硬盘备份
		if (sb.length() > 0)
		{
			synchronized (addStaleDocLock)
			{
				// 保存到硬盘作备份
				fs.writeStringToFile(path + "/stale/" + staleFile.getName(), sb.toString(), true);
			}
		}

		// 过时文档超过，企图合并索引
		if (stale.size() > STALE_SIZE)
		{
			this.mergeRAM();
		}
	}

	/**
	 * 清空所有文档
	 * @throws Exception
	 */
	public synchronized void clearAllDocs() throws Exception
	{
		// 关闭会话
		this.close();
		// 删除物理文件
		fs.cleanDir(path);
		// 重新初始化
		this.init();
	}

	/**
	 * 合并索引段
	 * @param segments
	 */
	public synchronized void forceMerge(int segments) throws Exception
	{
		IndexWriter writer = IndexUtil.createIndexWriter(fs, path);
		writer.forceMerge(segments);
		writer.commit();
	}

	/**
	 * 文档是否已经过时
	 * @param docId
	 * @return
	 */
	public boolean isStale(String docId)
	{
		return this.stale.contains(docId) || this.staleTemp.contains(docId);
	}

	/**
	 * 获取过时文档数量
	 * @return
	 */
	public int getStaleDocSize()
	{
		return this.stale.size() + this.staleTemp.size();
	}

	/**
	 * 获取索引路径
	 * @return
	 */
	public String getPath()
	{
		return path;
	}
}
