package com.sean.cache.core;

import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfiguration.TransactionalMode;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.apache.log4j.Logger;

import com.sean.log.core.LogFactory;

/**
 * 缓存工厂ehcache实现
 * @author sean
 */
public class CacheFactoryEhCache implements CacheFactory
{
	private final static Logger logger = LogFactory.getLogger(L.Cache);
	private CacheManager ehcacheManager;
	private Map<String, Cache> cacheManager;

	public CacheFactoryEhCache()
	{
		logger.info("EhCache CacheFactory start initializing...");

		cacheManager = new HashMap<>();
		// 初始化缓存管理器
		Configuration ehcacheConfig = new Configuration();
		ehcacheConfig.setName("cache");
		ehcacheConfig.setDynamicConfig(false);
		ehcacheConfig.setUpdateCheck(false);
		ehcacheConfig.setMonitoring("OFF");
		ehcacheManager = CacheManager.create(ehcacheConfig);

		logger.info("The EhCache CacheFactory initailized successfully");
	}

	@Override
	public Cache getCache(String name)
	{
		Cache cache = cacheManager.get(name);
		if (cache == null)
		{
			cache = this.createCache(name, 10000);
		}
		return cache;
	}

	@Override
	public Cache createCache(String name, int maxElementsInMemory)
	{
		return this.createCache(name, maxElementsInMemory, 3600, 1200);
	}

	@Override
	public Cache createCache(String name, int maxElementsInMemory, int timeToLiveSeconds)
	{
		return this.createCache(name, maxElementsInMemory, timeToLiveSeconds, 1200);
	}

	@Override
	public Cache createCache(String name, int maxElementsInMemory, int timeToLiveSeconds, int timeToIdleSeconds)
	{
		Cache cache = cacheManager.get(name);
		if (cache == null)
		{
			CacheConfiguration cfg = getDefaultConfig();
			cfg.name(name);
			cfg.maxEntriesLocalHeap(maxElementsInMemory);
			cfg.timeToLiveSeconds(timeToLiveSeconds);
			cfg.timeToIdleSeconds(timeToIdleSeconds);

			net.sf.ehcache.Cache ehcache = ehcacheManager.getCache(name);
			if (ehcache == null)
			{
				ehcache = new net.sf.ehcache.Cache(cfg);
				ehcacheManager.addCache(ehcache);
			}

			displayCache(cfg);
			cache = new CacheEhCacheImpl(ehcache);
			cacheManager.put(name, cache);
		}
		return cache;
	}

	/**
	 * 获取默认的缓存配置
	 * @return
	 */
	private static CacheConfiguration getDefaultConfig()
	{
		CacheConfiguration cfg = new CacheConfiguration();
		cfg.name("");
		cfg.maxEntriesLocalHeap(3000);
		cfg.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU);
		cfg.eternal(false);
		cfg.timeToIdleSeconds(60 * 20);
		cfg.timeToLiveSeconds(60 * 60);
		cfg.overflowToOffHeap(false);
		cfg.statistics(false);
		cfg.transactionalMode(TransactionalMode.OFF);
		return cfg;
	}

	private void displayCache(CacheConfiguration cfg)
	{
		logger.debug("EhCache CacheFactory create a cache " + cfg.getName() + " , it's maxElementInMemory is "
				+ cfg.getMaxEntriesLocalHeap() + " timeToLiveSeconds is " + cfg.getTimeToLiveSeconds() + " timeToIdleSeconds is "
				+ cfg.getTimeToIdleSeconds());
	}
}
