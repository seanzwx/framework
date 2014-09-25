package com.sean.persist.core;

/**
 * 分区策略定义(实际分表,表名字按照tablename_1递增)
 * @author sean
 */
public abstract class Partition
{
	private ThreadLocal<Object> partitionKeys;

	public Partition()
	{
		partitionKeys = new ThreadLocal<>();
	}

	/**
	 * 用户分区路由策略
	 * @return
	 */
	public abstract int partition(Object partitionKey);

	/**
	 * 定位分区
	 * @return
	 */
	public int getPartition()
	{
		Object partitionKey = this.partitionKeys.get();
		if (partitionKey != null)
		{
			int partition = this.partition(partitionKey);
			return partition;
		}
		else
		{
			throw new RuntimeException("partition key has not been set");
		}
	}

	/**
	 * 设置分区键
	 * @param partitionKey
	 */
	public void setPartitionKey(Object partitionKey)
	{
		if (this.partitionKeys.get() == null)
		{
			this.partitionKeys.set(partitionKey);
		}
		else
		{
			throw new RuntimeException("partition key has been setted, which can't be modified anyway");
		}
	}

	/**
	 * 清空分区键
	 */
	public void clearPartitionKey()
	{
		this.partitionKeys.remove();
	}
}
