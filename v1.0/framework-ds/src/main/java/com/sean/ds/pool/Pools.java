package com.sean.ds.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * 连接池工具
 * @author sean
 */
public final class Pools
{
	/**
	 * 创建固定大小的连接池
	 * <p>
	 * 连接池的大小固定，由maxSize指定，当池中没有空闲的链接，线程将阻塞，直到有空闲的链接
	 * </p>
	 * @param maxSize
	 * @param timeout	创建thrift客户端连接超时
	 * @return
	 */
	public static <E> ClientPool<E> createFixedSizePool(Class<E> clazz, int maxSize, String hostname, int port)
	{
		AvroClientFactory<E> factory = new AvroClientFactory<>(clazz, hostname, port);
		GenericObjectPoolConfig cfg = getBaseConfig();
		cfg.setMaxTotal(maxSize);
		cfg.setMaxIdle(maxSize);
		return new AvroClientPool<E>(clazz.getSimpleName(), factory, cfg);
	}

	/**
	 * 创建可伸缩的连连接池
	 * <p>
	 * 连接池的大小可伸缩，链接最大值由maxSize指定， 最大空闲连接由maxIdle指定， 
	 * </p>
	 * <p>
	 * 例如：当maxSize=10, maxIdle=5， 当由11个线程同时申请资源，前10个线程不会阻塞，成功申请到资源，第11个线程将进入阻塞，当10个资源归还回连接池后，
	 * 会销毁5个资源， 即对多保存5个空闲资源。
	 * </p>
	 * @param regName
	 * @param maxSize
	 * @param maxIdle
	 * @param timeout 创建thrift客户端连接超时
	 * @return
	 */
	public static <E> ClientPool<E> createScalablePool(Class<E> clazz, int maxSize, int maxIdle, String hostname, int port)
	{
		AvroClientFactory<E> factory = new AvroClientFactory<E>(clazz, hostname, port);
		GenericObjectPoolConfig cfg = getBaseConfig();
		cfg.setMaxTotal(maxSize);
		cfg.setMaxIdle(maxIdle);
		return new AvroClientPool<E>(clazz.getSimpleName(), factory, cfg);
	}

	private static GenericObjectPoolConfig getBaseConfig()
	{
		GenericObjectPoolConfig cfg = new GenericObjectPoolConfig();
		// timeout 10s
		cfg.setMaxWaitMillis(1000 * 10);

		cfg.setTestWhileIdle(false);
		cfg.setTestOnBorrow(true);
		cfg.setTestOnReturn(false);
		cfg.setTestWhileIdle(false);

		// last in fist out / stack, default by fifo / queue
		cfg.setLifo(true);
		// disblae jmx
		cfg.setJmxEnabled(false);
		return cfg;
	}
}
