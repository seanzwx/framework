package com.sean.persist.entity;

import java.util.List;

import com.sean.persist.core.CachePolicy;
import com.sean.persist.core.Partition;

/**
 * Entity实体
 * @author sean
 */
public class EntityEntity
{
	private ColumnEntity primaryKey;
	private String tableName;
	private String dataSource;
	private boolean cache;
	private List<ColumnEntity> columns;
	private Class<? extends CachePolicy> cachePolicy;
	private Class<? extends Partition> partition;
	private Class<?> cls;

	public EntityEntity(String tableName, String dataSource, boolean cache, List<ColumnEntity> columns, Class<? extends CachePolicy> cachePolicy,
			Class<? extends Partition> partition, Class<?> cls)
	{
		this.tableName = tableName;
		this.dataSource = dataSource;
		this.cache = cache;
		this.columns = columns;
		this.cachePolicy = cachePolicy;
		this.partition = partition;
		this.cls = cls;

		for (ColumnEntity c : columns)
		{
			if (c.isPrimaryKey())
			{
				this.primaryKey = c;
				break;
			}
		}
	}

	public String getTableName()
	{
		return tableName;
	}

	public String getDataSource()
	{
		return dataSource;
	}

	public List<ColumnEntity> getColumns()
	{
		return columns;
	}

	public boolean isCache()
	{
		return cache;
	}

	public ColumnEntity getPrimaryKey()
	{
		return primaryKey;
	}

	public Class<? extends CachePolicy> getCachePolicy()
	{
		return cachePolicy;
	}

	public Class<? extends Partition> getPartition()
	{
		return partition;
	}

	public Class<?> getCls()
	{
		return cls;
	}

}
