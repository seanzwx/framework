package com.sean.ds.pool;

public class ScalablePool implements PoolStrategy
{
	private int maxSize;
	private int maxIdle;

	public ScalablePool(int maxSize, int maxIdle)
	{
		this.maxSize = maxSize;
		this.maxIdle = maxIdle;
	}

	@Override
	public <E> ClientPool<E> createPool(Class<E> resClass, String hostname, int port)
	{
		return Pools.createScalablePool(resClass, maxSize, maxIdle, hostname, port);
	}

}
