package com.sean.cache.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.sean.log.core.LogFactory;

/**
 * 缓存工厂redis实现
 * @author sean
 */
public class CacheFactoryRedis implements CacheFactory
{
	private final static Logger logger = LogFactory.getLogger(L.Cache);
	private Map<String, Cache> cacheManager;
	private JedisPool pool;

	public CacheFactoryRedis(String hostname, int port)
	{
		logger.info("Redis CacheFactory start initializing...");

		cacheManager = new HashMap<>();
		JedisPoolConfig conf = new JedisPoolConfig();
		pool = new JedisPool(conf, hostname, port);

		logger.info("The Redis CacheFactory initailized successfully");
	}

	@Override
	public Cache getCache(String name)
	{
		Cache cache = this.cacheManager.get(name);
		if (cache == null)
		{
			cache = this.createCache(name, 0);
		}
		return cache;
	}

	@Override
	public Cache createCache(String name, int maxElementsInMemory)
	{
		Cache cache = this.cacheManager.get(name);
		if (cache == null)
		{
			cache = new CacheRedisImpl(name, pool);
			displayCache(name);
			cacheManager.put(name, cache);
		}
		return cache;
	}

	@Override
	public Cache createCache(String name, int maxElementsInMemory, int timeToLiveSeconds)
	{
		return createCache(name, 0);
	}

	@Override
	public Cache createCache(String name, int maxElementsInMemory, int timeToLiveSeconds, int timeToIdleSeconds)
	{
		return createCache(name, 0);
	}

	private void displayCache(String name)
	{
		logger.debug("Redis CacheFactory create a cache " + name);
	}
}
