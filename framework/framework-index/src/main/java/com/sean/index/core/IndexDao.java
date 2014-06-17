package com.sean.index.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import com.sean.index.config.IndexConfig;

/**
 * 索引访问对象
 * @author sean
 */
public final class IndexDao
{
	private IndexSession session;
	private String keyField;

	public IndexDao(String path, String keyField, BackupReader backupReader) throws Exception
	{
		this(path, keyField, IndexConfig.DEFAULT_RAM_SIZE, IndexConfig.DEFAULT_STALE_SIZE, backupReader);
	}

	public IndexDao(String path, String keyField, int ramSizeMB, int staleSize, BackupReader backupReader) throws Exception
	{
		if (backupReader == null)
		{
			throw new RuntimeException(
					"backupReader is used to restore the ram index backup, the index data will lost all ram index when mechine poweroff or broken without this");
		}
		this.session = new IndexSession(path, (long) ramSizeMB * 1024L * 1024L, staleSize, backupReader);
		this.keyField = keyField;
	}

	/**
	 * 添加文档
	 * @param doc
	 */
	public void addDocument(Document doc) throws Exception
	{
		doc.add(new StringField(IndexConfig.KEY, UUID.randomUUID().toString(), Store.YES));
		session.addDocument(doc);
		session.commit();
	}

	/**
	 * 批量添加文档
	 * @param docs
	 */
	public void addDocuments(List<Document> docs) throws Exception
	{
		for (Document doc : docs)
		{
			doc.add(new StringField(IndexConfig.KEY, UUID.randomUUID().toString(), Store.YES));
		}
		session.addDocuments(docs);
		session.commit();
	}

	/**
	 * 删除文档
	 * @param query
	 */
	public void deleteDocument(Query query) throws Exception
	{
		Searcher searcher = session.getSearcher();
		try
		{
			IndexSearcher is = searcher.acquireSearcher();
			TopDocs td = is.search(query, Integer.MAX_VALUE);
			Document doc = null;
			List<String> docIds = new ArrayList<String>(td.scoreDocs.length);
			for (ScoreDoc sd : td.scoreDocs)
			{
				doc = is.doc(sd.doc);
				docIds.add(doc.get(IndexConfig.KEY));
			}
			if (docIds.size() > 0)
			{
				session.addStaleDocs(docIds);
			}
		}
		finally
		{
			searcher.releaseSearcher();
		}
	}

	/**
	 * 删除文档
	 * @param docId
	 */
	public void deleteDocument(long docId) throws Exception
	{
		Searcher searcher = session.getSearcher();
		try
		{
			IndexSearcher is = searcher.acquireSearcher();
			TopDocs td = is.search(NumericRangeQuery.newLongRange(keyField, docId, docId, true, true), 1);
			if (td.scoreDocs.length > 0)
			{
				Document doc = is.doc(td.scoreDocs[0].doc);
				session.addStaleDoc(doc.get(IndexConfig.KEY));
			}
		}
		finally
		{
			searcher.releaseSearcher();
		}
	}

	/**
	 * 更新文档
	 * @param doc
	 */
	public void updateDocument(Document doc) throws Exception
	{
		StringField key = (StringField) doc.getField(IndexConfig.KEY);
		if (key != null)
		{
			// 删除该文档
			this.session.addStaleDoc(key.stringValue());
			// 设置新的ID
			key.setStringValue(UUID.randomUUID().toString());
			this.addDocument(doc);
		}
	}

	/**
	 * 搜索
	 * @param query
	 * @param limit
	 */
	public List<Document> searchDocument(Query query, int limit) throws Exception
	{
		return this.searchDocument(query, 1, limit).getData();
	}

	/**
	 * 分页搜索
	 * @param query
	 * @param pageNo
	 * @param pageSize
	 */
	public PageDocument searchDocument(Query query, int pageNo, int pageSize) throws Exception
	{
		Searcher searcher = session.getSearcher();
		try
		{
			IndexSearcher is = searcher.acquireSearcher();
			int start = (pageNo - 1) * pageSize;
			int total = pageNo * pageSize + session.getStaleDocSize();
			int staleCount = 0;
			Document doc = null;
			List<Document> data = new LinkedList<Document>();

			TopDocs td = is.search(query, total);
			ScoreDoc[] sd = td.scoreDocs;
			// 过滤过时文档
			for (int i = 0; i < sd.length; i++)
			{
				doc = is.doc(sd[i].doc);
				if (!session.isStale(doc.get(IndexConfig.KEY)))
				{
					data.add(doc);
				}
				else
				{
					// 统计结果集中的过时文档数量
					staleCount++;
				}
			}

			// 删除结果集种start前的所有文档
			if (data.size() < start)
			{
				data.clear();
			}
			else
			{
				for (int i = 0; i < start; i++)
				{
					data.remove(0);
				}
			}

			return new PageDocument(pageNo, td.totalHits - staleCount, data);
		}
		finally
		{
			searcher.releaseSearcher();
		}
	}

	/**
	 * 搜索
	 * @param query
	 */
	public Document loadDocument(long id) throws Exception
	{
		Searcher searcher = session.getSearcher();
		try
		{
			IndexSearcher is = searcher.acquireSearcher();

			TopDocs td = is.search(NumericRangeQuery.newLongRange(keyField, id, id, true, true), 1);
			if (td.scoreDocs.length > 0)
			{
				Document doc = is.doc(td.scoreDocs[0].doc);
				if (!session.isStale(doc.get(IndexConfig.KEY)))
				{
					return doc;
				}
			}
			return null;
		}
		finally
		{
			searcher.releaseSearcher();
		}
	}

	/**
	 * 清空所有索引
	 */
	public void clear() throws Exception
	{
		this.session.clearAllDocs();
	}
}
