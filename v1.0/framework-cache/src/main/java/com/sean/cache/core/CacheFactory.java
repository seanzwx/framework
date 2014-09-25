package com.sean.cache.core;

/**
 * 该类获取的缓存的所有参数仅仅只对ehcache有效，对redis无效
 * @author sean
 */
public interface CacheFactory
{
	/**
	 * 获取缓存
	 * @param name						缓存名称cache
	 * @return
	 */
	public Cache getCache(String name);

	/**
	 * 创建缓存
	 * @param name						缓存名称
	 * @param maxElementsInMemory		缓存最大元素数量
	 */
	public Cache createCache(String name, int maxElementsInMemory);

	/**
	 * 创建缓存
	 * @param name						缓存名称
	 * @param maxElementsInMemory		缓存最大元素数量
	 * @param timeToLiveSeconds			缓存时间
	 */
	public Cache createCache(String name, int maxElementsInMemory, int timeToLiveSeconds);

	/**
	 * 创建缓存
	 * @param name						缓存名称
	 * @param maxElementsInMemory		缓存最大元素数量
	 * @param timeToLiveSeconds			缓存时间
	 * @param timeToIdleSeconds			距上次访问或修改删除时间
	 */
	public Cache createCache(String name, int maxElementsInMemory, int timeToLiveSeconds, int timeToIdleSeconds);
}
