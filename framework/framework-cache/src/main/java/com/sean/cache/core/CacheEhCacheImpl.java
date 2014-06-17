package com.sean.cache.core;

import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Element;

/**
 * 缓存ehcache实现
 * @author sean
 */
@SuppressWarnings("unchecked")
public final class CacheEhCacheImpl extends Cache
{
	private net.sf.ehcache.Cache cache;

	public CacheEhCacheImpl(net.sf.ehcache.Cache cache)
	{
		this.cache = cache;
	}

	@Override
	public void putArray(Object[] keys, Object[] vals)
	{
		if (keys.length == vals.length && keys.length > 0)
		{
			for (int i = 0; i < vals.length; i++)
			{
				cache.putQuiet(new Element(keys[i], vals[i]));
			}
		}
	}

	@Override
	public void putList(List<Object> keys, List<Object> vals)
	{
		this.putArray(keys.toArray(new Object[keys.size()]), vals.toArray(new Object[vals.size()]));
	}

	@Override
	public List<Object> get(Object[] keys)
	{
		if (keys.length > 0)
		{
			List<Object> list = new ArrayList<>(keys.length);
			Element el = null;
			for (int i = 0; i < keys.length; i++)
			{
				el = cache.getQuiet(keys[i]);
				if (el != null)
				{
					list.add((el.getObjectValue()));
				}
			}
			return list;
		}
		return new ArrayList<>(0);
	}

	@Override
	public List<Object> get(List<Object> keys)
	{
		return this.get(keys.toArray(new Object[keys.size()]));
	}

	@Override
	public void remove(Object[] keys)
	{
		if (keys.length > 0)
		{
			for (int i = 0; i < keys.length; i++)
			{
				cache.removeQuiet(keys[i]);
			}
		}
	}

	@Override
	public void clear()
	{
		cache.removeAll();
	}

	@Override
	public List<Object> getKeys()
	{
		return cache.getKeys();
	}

}
