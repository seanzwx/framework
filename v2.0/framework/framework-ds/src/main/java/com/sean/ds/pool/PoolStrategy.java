package com.sean.ds.pool;

/**
 * 连接池策略
 * @author sean
 */
public interface PoolStrategy
{
	public <E> ClientPool<E> createPool(Class<E> resClass, String hostname, int port);
}
