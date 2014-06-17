package com.sean.cache.core;

import java.util.List;

/**
 * 缓存接口
 * @author sean
 */
public abstract class Cache
{
	/**
	 * 添加缓存对象
	 * @param key
	 * @param val
	 */
	public void put(Object key, Object val)
	{
		this.putArray(new Object[] { key }, new Object[] { val });
	}

	/**
	 * 批量添加缓存对象
	 * @param items
	 */
	public abstract void putArray(Object[] keys, Object[] vals);

	/**
	 * 批量添加缓存对象
	 * @param items
	 */
	public abstract void putList(List<Object> keys, List<Object> vals);

	/**
	 * 获取缓存对象
	 * @param key
	 * @return
	 */
	public Object get(Object key)
	{
		List<Object> objs = this.get(new Object[] { key });
		return objs.isEmpty() ? null : objs.get(0);
	}

	/**
	 * 获取缓存对象
	 * @param keys
	 * @return
	 */
	public abstract List<Object> get(Object[] keys);

	/**
	 * 获取缓存对象
	 * @param keys
	 * @return
	 */
	public abstract List<Object> get(List<Object> keys);

	/**
	 * 删除缓存对象
	 * @param key
	 */
	public void remove(Object key)
	{
		this.remove(new Object[] { key });
	}

	/**
	 * 批量删除缓存对象
	 * @param key
	 */
	public abstract void remove(Object[] key);

	/**
	 * 清空缓存
	 */
	public abstract void clear();

	/**
	 * 获取所有Key
	 * @return
	 */
	public abstract List<Object> getKeys();
}
