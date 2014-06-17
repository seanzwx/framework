package com.sean.index.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings("static-access")
public class SearcherCleaner
{
	private BlockingQueue<Searcher> queue;
	private Thread thread;

	public SearcherCleaner()
	{
		queue = new LinkedBlockingQueue<Searcher>();
		// 启动线程
		thread = new Thread(new CleanThread());
		thread.start();
	}

	public void addUnusedSearcher(Searcher searcher) throws Exception
	{
		queue.put(searcher);
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
			Searcher searcher = null;
			while (true)
			{
				try
				{
					if (searcher != null)
					{
						if (searcher.getReference() <= 0)
						{
							searcher.close();
						}
						// 如果还有线程在使用
						else
						{
							queue.put(searcher);
							Thread.currentThread().sleep(10);
						}
					}
					searcher = queue.take();
				}
				catch (InterruptedException e)
				{
					break;
				}
				catch (Exception e2)
				{
					e2.printStackTrace();
				}
			}
		}
	}
}
