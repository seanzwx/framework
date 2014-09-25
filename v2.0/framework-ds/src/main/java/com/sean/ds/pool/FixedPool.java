package com.sean.ds.pool;

public class FixedPool implements PoolStrategy
{
	private int maxSize;

	public FixedPool(int maxSize)
	{
		this.maxSize = maxSize;
	}

	@Override
	public <E> ClientPool<E> createPool(Class<E> resClass, String hostname, int port)
	{
		return Pools.createFixedSizePool(resClass, maxSize, hostname, port);
	}
}
