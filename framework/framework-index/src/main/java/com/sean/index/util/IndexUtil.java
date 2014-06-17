package com.sean.index.util;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.sean.index.config.IndexConfig;

/**
 * 索引工具
 * @author sean
 */
public class IndexUtil
{
	/**
	 * 打开目录
	 * @param path
	 * @return
	 */
	public static Directory openDir(FileSys fileSys, String path) throws Exception
	{
		// 创建索引目录
		if (!fileSys.exists(path))
		{
			fileSys.mkdir(path);
		}
		return fileSys.openDir(path);
	}

	/**
	 * 打开内存目录
	 * @param path
	 * @return
	 */
	public static RAMDirectory openRAMDir() throws Exception
	{
		RAMDirectory dir = new RAMDirectory();
		return dir;
	}

	/**
	 * 创建IndexWriter
	 * @param path
	 * @return
	 */
	public static IndexWriter createIndexWriter(FileSys fileSys, String path) throws Exception
	{
		Directory dir = openDir(fileSys, path);
		return createIndexWriter(dir);
	}

	/**
	 * 创建IndexWriter
	 * @param path
	 * @return
	 */
	public static IndexWriter createIndexWriter(Directory dir) throws Exception
	{
		// 立即解锁，防止索引被锁
		if (IndexWriter.isLocked(dir))
		{
			IndexWriter.unlock(dir);
		}
		// 初始化writer配置
		IndexWriterConfig cfg = new IndexWriterConfig(IndexConfig.VERSION, new IKAnalyzer(true));
		return new IndexWriter(dir, cfg);
	}

	/**
	 * 创建IndexReader
	 * @param path
	 * @return
	 */
	public static IndexReader createIndexReader(FileSys fileSys, String path) throws Exception
	{
		Directory dir = openDir(fileSys, path);
		return createIndexReader(dir);
	}

	/**
	 * 创建IndexReader
	 * @param path
	 * @return
	 */
	public static IndexReader createIndexReader(Directory dir) throws Exception
	{
		try
		{
			IndexReader reader = DirectoryReader.open(dir);
			return reader;
		}
		catch (Exception e)
		{
			IndexWriter iw = createIndexWriter(dir);
			iw.commit();
			iw.close();
			IndexReader reader = DirectoryReader.open(dir);
			return reader;
		}
	}

	/**
	 * 创建IndexReader
	 * @param path
	 * @return
	 */
	public static IndexReader createIndexReader(IndexWriter writer) throws Exception
	{
		IndexReader reader = DirectoryReader.open(writer, true);
		return reader;
	}

	/**
	 * 文档转化String
	 * @param doc
	 * @return
	 */
	public static String documentToString(Document doc)
	{
		StringBuilder sb = new StringBuilder(1024);
		sb.append(IndexConfig.KEY).append(":").append(doc.get(IndexConfig.KEY));
		for (IndexableField field : doc.getFields())
		{
			if (!field.name().equals(IndexConfig.KEY))
			{
				sb.append(" | ").append(field.name()).append(":").append(field.stringValue());
			}
		}

		return sb.toString();
	}

	/**
	 * IndexReader转String
	 * @param reader
	 * @return
	 */
	public static String readerToString(IndexReader reader) throws Exception
	{
		StringBuilder sb = new StringBuilder(1024);
		int max = reader.maxDoc();
		for (int i = 0; i < max; i++)
		{
			sb.append(documentToString(reader.document(i))).append("\n");
		}
		return sb.toString();
	}
}
