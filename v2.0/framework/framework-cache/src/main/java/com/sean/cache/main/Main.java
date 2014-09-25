package com.sean.cache.main;

import java.io.Serializable;

import com.sean.cache.core.Cache;
import com.sean.cache.core.CacheFactory;
import com.sean.cache.core.CacheFactoryEhCache;

public class Main
{
	public static void main(String[] args)
	{
//		CacheFactory factory = new CacheFactoryRedis("localhost", 6379);
		CacheFactory factory = new CacheFactoryEhCache();
		
		Cache c = factory.createCache("test", 1000);
		c.clear();
		c.put("key1", 1);
		c.put("key2", 2);

		c.putArray(new String[] { "key3", "key4" }, new Serializable[] { 3, 4 });

		for (Object key : c.getKeys())
		{
			System.out.println(key + ":" + c.get(key));
		}

		System.out.println(c.get(new String[] { "key1", "key2", "key3", "key5" }));

		c.clear();

		c.put("key1", 1);
		c.put("key2", 2);
		for (Object key : c.getKeys())
		{
			System.out.println(key + ":" + c.get(key));
		}
		c.clear();
	}
}
