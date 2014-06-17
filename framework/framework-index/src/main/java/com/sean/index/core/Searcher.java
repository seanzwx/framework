package com.sean.index.core;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.IndexSearcher;

public class Searcher
{
	private IndexReader ramReader;
	private int reference;
	private IndexSearcher searcher;

	public Searcher(IndexReader diskReader, IndexReader ramReader)
	{
		this.ramReader = ramReader;
		this.reference = 0;
		if (diskReader != null && ramReader != null)
		{
			searcher = new IndexSearcher(new MultiReader(diskReader, ramReader));
		}
	}

	private Lock lock = new ReentrantLock();

	public IndexSearcher acquireSearcher()
	{
		try
		{
			lock.lock();
			reference++;
		}
		finally
		{
			lock.unlock();
		}
		return searcher;
	}

	public void releaseSearcher()
	{
		try
		{
			lock.lock();
			reference--;
		}
		finally
		{
			lock.unlock();
		}
	}

	public int getReference()
	{
		return this.reference;
	}

	public void close() throws Exception
	{
		ramReader.close();
	}
}
