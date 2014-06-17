package com.sean.index.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.AlreadyClosedException;

public class WriterCommiter
{
	private BlockingQueue<IndexWriter> queue;
	private Thread thread;
	private IndexSession session;

	public WriterCommiter(IndexSession session)
	{
		this.session = session;
		queue = new LinkedBlockingQueue<IndexWriter>();
		// 启动线程
		thread = new Thread(new CleanThread());
		thread.start();
	}

	public void addCommitWriter(IndexWriter writer) throws Exception
	{
		queue.put(writer);
	}

	public void destory() throws Exception
	{
		thread.interrupt();
	}

	private class CleanThread implements Runnable
	{
		@Override
		public void run()
		{
			IndexWriter writer = null;
			while (true)
			{
				try
				{
					writer = queue.take();
					// 清空其余所有commiter
					queue.clear();
					writer.commit();
					// 立即刷新内存索引
					session.refreshRAMReader();
				}
				catch (InterruptedException e)
				{
					break;
				}
				catch (AlreadyClosedException ace)
				{
					continue;
				}
				catch (Exception e2)
				{
					e2.printStackTrace();
				}
			}
		}
	}
}
