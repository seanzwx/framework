package com.sean.index.test;

import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.sean.config.core.Config;
import com.sean.index.config.IndexConfig;
import com.sean.index.util.IndexUtil;

/**
 * 内存索引刷新测试
 *
 * @author sean
 */
public class RamCommitAndRefreshTest
{
	static Document doc = new Document();

	static
	{
		RamCommitAndRefreshTest.doc.add(new StringField("id", UUID.randomUUID().toString(), Store.YES));
		doc.add(new StringField("sex", "1sdfsdfaf", Store.NO));
		doc.add(new StringField("class", "1sdfsdfsdf", Store.NO));
		doc.add(new StringField("sig", "1sdfsdfsdfs", Store.NO));

		doc.add(new StringField("sex1", "1sdfsdfaf", Store.NO));
		doc.add(new StringField("class1", "1sdfsdfsdf", Store.NO));
		doc.add(new StringField("sig1", "1sdfsdfsdfs", Store.NO));

		doc.add(new StringField("sex2", "1sdfsdfaf", Store.NO));
		doc.add(new StringField("class2", "1sdfsdfsdf", Store.NO));
		doc.add(new StringField("sig2", "1sdfsdfsdfs", Store.NO));

		doc.add(new StringField("sex3", "1sdfsdfaf", Store.NO));
		doc.add(new StringField("class3", "1sdfsdfsdf", Store.NO));
		doc.add(new StringField("sig3", "1sdfsdfsdfs", Store.NO));
	}
	static long amount = 0;

	public static void main(String[] args) throws Exception
	{
		Config.readConfiguration();

		// Directory dir = FSDirectory.open(new
		// File("/home/sean/Desktop/index"));
		Directory dir = new RAMDirectory();
		IndexWriterConfig cfg = new IndexWriterConfig(IndexConfig.VERSION, new IKAnalyzer(true));
		cfg.setMaxBufferedDocs(Integer.MAX_VALUE);
		IndexWriter writer = new IndexWriter(dir, cfg);

		test(dir, writer);
		test(dir, writer);

		add(dir, writer, 100);
		test(dir, writer);
		test(dir, writer);

		add(dir, writer, 1000);
		test(dir, writer);
		test(dir, writer);

		add(dir, writer, 10000);
		test(dir, writer);
		test(dir, writer);

		add(dir, writer, 20000);
		test(dir, writer);
		test(dir, writer);

		add(dir, writer, 30000);
		test(dir, writer);
		test(dir, writer);

		add(dir, writer, 40000);
		test(dir, writer);
		test(dir, writer);

		add(dir, writer, 50000);
		test(dir, writer);
		test(dir, writer);

		// add(dir, writer, 100000);
		// test(dir, writer);
		// test(dir, writer);
		//
		// add(dir, writer, 500000);
		// test(dir, writer);
		// test(dir, writer);
		//
		// add(dir, writer, 1000000);
		// test(dir, writer);
		// test(dir, writer);
		//
		// add(dir, writer, 2000000);
		// test(dir, writer);test(dir, writer);
	}

	private static void add(Directory dir, IndexWriter writer, long size) throws Exception
	{
		while (amount < size)
		{
			StringField f = (StringField) doc.getField("id");
			f.setStringValue(UUID.randomUUID().toString());
			writer.addDocument(doc);
			amount++;
		}
		writer.commit();
	}

	public static void test(Directory dir, IndexWriter writer) throws Exception
	{
		System.out.println("===============内存大小" + (amount) + "=====================");
		long curr = System.currentTimeMillis();
		writer.addDocument(doc);
		writer.commit();
		System.out.println("commit:" + (System.currentTimeMillis() - curr));
		curr = System.currentTimeMillis();
		IndexUtil.createIndexReader(dir);
		System.out.println("refresh:" + (System.currentTimeMillis() - curr));
	}
}
