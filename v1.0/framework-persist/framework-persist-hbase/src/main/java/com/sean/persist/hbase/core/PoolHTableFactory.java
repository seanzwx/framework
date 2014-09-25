package com.sean.persist.hbase.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTableInterfaceFactory;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Hbase Table 工厂
 * @author sean
 */
public class PoolHTableFactory implements HTableInterfaceFactory
{
	private int maxTable;
	private Map<String, TablePool> pools;

	public PoolHTableFactory(int maxTable)
	{
		this.maxTable = maxTable;
		this.pools = new HashMap<String, TablePool>();
	}
	
	private Object createTableLock = new Object();

	@Override
	public HTableInterface createHTableInterface(Configuration config, byte[] tableName)
	{
		String tname = Bytes.toString(tableName);
		TablePool pool = null;
		synchronized(createTableLock)
		{
			pool = this.pools.get(tname);
			if (pool == null)
			{
				pool = new TablePool(config, tname, maxTable);
			}
		}
		return pool.getTable();
	}

	@Override
	public void releaseHTableInterface(HTableInterface table) throws IOException
	{
		String tname = Bytes.toString(table.getTableName());
		TablePool pool = this.pools.get(tname);
		pool.returnTable(table);
	}
}
