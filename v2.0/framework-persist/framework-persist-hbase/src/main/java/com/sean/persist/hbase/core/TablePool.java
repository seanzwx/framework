package com.sean.persist.hbase.core;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.log4j.Logger;

import com.sean.log.core.LogFactory;

/**
 * HBase链接池
 * @author Sean
 */
public class TablePool
{
	private String tableName;
	private Configuration conf;
	private int maxResources; // 资源池最大限度
	private int currentResources; // 当前资源数量
	private boolean quit; // 资源池是否被销毁
	private List<HTableInterface> takeResources; // 所取的资源
	private List<HTableInterface> waitResources; // 所有的等待资源
	
	private final Logger logger = LogFactory.getLogger(L.Hbase);

	/**
	 * 资源池构造
	 * @param conf			
	 * @param tableName					表名			
	 * @param maxResources				最大资源限度
	 */
	public TablePool(Configuration conf, String tableName, int maxResources)
	{
		this.conf = conf;
		this.tableName = tableName;
		this.maxResources = maxResources;

		this.currentResources = 0;
		this.takeResources = new LinkedList<HTableInterface>();
		this.waitResources = new LinkedList<HTableInterface>();
	}

	/**
	 * 获取表 
	 * @return							返回表
	 */
	public synchronized HTableInterface getTable()
	{
		while (!this.quit)
		{
			// 首先查找等待资源
			if (!this.waitResources.isEmpty())
			{
				HTableInterface table = this.waitResources.remove(0);
				this.takeResources.add(table);
				return table;
			}

			// 如果是未超出最大资源池限度,创建新的资源
			if (this.currentResources < this.maxResources)
			{
				HTableInterface table = null;
				try
				{
					table = new HTable(conf, tableName);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					logger.error(e.getMessage(), e);
				}
				if (table != null)
				{
					this.currentResources++;
					this.takeResources.add(table);
				}
				return table;
			}

			// 如果等待资源，并且已经达到资源池的最大限度，一直处于等待状态
			try
			{
				this.wait();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		// 资源池被销毁
		return null;
	}

	/**
	 * 返回表
	 * @param table						表
	 */
	public synchronized void returnTable(HTableInterface table)
	{
		if (!this.takeResources.remove(table))
		{
			throw new RuntimeException(table + " is not in takeresources");
		}
		this.waitResources.add(table);
		// 唤醒一个等待线程
		this.notify();
	}

	/**
	 * 销毁资源池
	 */
	public synchronized void destory()
	{
		this.quit = true;
		try
		{
			// 销毁所有资源
			for (HTableInterface table : takeResources)
			{
				table.close();
			}
			for (HTableInterface table : waitResources)
			{
				table.close();
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		// 唤醒所有等待线程
		this.notifyAll();
	}
}
