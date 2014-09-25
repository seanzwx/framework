package com.sean.cache.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.sean.common.util.BytesUtil;
import com.sean.common.util.ObjectUtil;
import com.sean.log.core.LogFactory;

/**
 * 缓存redis实现
 * @author sean
 */
public final class CacheRedisImpl extends Cache
{
	private final static Logger logger = LogFactory.getLogger(L.Cache);
	private JedisPool pool;
	private byte[] id;

	public CacheRedisImpl(String name, JedisPool pool)
	{
		this.id = name.getBytes();
		this.pool = pool;
	}

	@Override
	public void putArray(Object[] keys, Object[] vals)
	{
		if (keys.length == vals.length && keys.length > 0)
		{
			Jedis jedis = null;
			try
			{
				Map<byte[], byte[]> map = new HashMap<>(keys.length);
				for (int i = 0; i < keys.length; i++)
				{
					if (keys[i] instanceof Long)
					{
						map.put(BytesUtil.toBytes((Long) keys[i]), ObjectUtil.serialize((Serializable) vals[i]));
					}
					else if (keys[i] instanceof String)
					{
						map.put(BytesUtil.toBytes((String) keys[i]), ObjectUtil.serialize((Serializable) vals[i]));
					}
					else if (keys[i] instanceof Integer)
					{
						map.put(BytesUtil.toBytes((Integer) keys[i]), ObjectUtil.serialize((Serializable) vals[i]));
					}
					else
					{
						throw new RuntimeException("cache key can not support except long and String");
					}
				}

				jedis = pool.getResource();
				jedis.hmset(id, map);
				pool.returnResource(jedis);
				jedis = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
			finally
			{
				if (jedis != null)
				{
					pool.returnResource(jedis);
				}
			}
		}
	}

	@Override
	public void putList(List<Object> keys, List<Object> vals)
	{
		int keyLen = keys.size();
		int valLen = vals.size();
		if (keyLen == valLen && valLen > 0)
		{
			Jedis jedis = null;
			try
			{
				Map<byte[], byte[]> map = new HashMap<>(keyLen);
				Object it = null;
				for (int i = 0; i < keyLen; i++)
				{
					it = keys.get(i);
					if (it instanceof Long)
					{
						map.put(BytesUtil.toBytes((Long) it), ObjectUtil.serialize((Serializable) vals.get(i)));
					}
					else if (it instanceof String)
					{
						map.put(BytesUtil.toBytes((String) it), ObjectUtil.serialize((Serializable) vals.get(i)));
					}
					else if (it instanceof Integer)
					{
						map.put(BytesUtil.toBytes((Integer) it), ObjectUtil.serialize((Serializable) vals.get(i)));
					}
					else
					{
						throw new RuntimeException("cache key can not support except long and String");
					}
				}

				jedis = pool.getResource();
				jedis.hmset(id, map);
				pool.returnResource(jedis);
				jedis = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
			finally
			{
				if (jedis != null)
				{
					pool.returnResource(jedis);
				}
			}
		}
	}

	@Override
	public List<Object> get(Object[] keys)
	{
		if (keys.length > 0)
		{
			Jedis jedis = null;
			try
			{
				byte[][] fields = new byte[keys.length][];
				for (int i = 0; i < keys.length; i++)
				{
					if (keys[i] instanceof Long)
					{
						fields[i] = BytesUtil.toBytes((Long) keys[i]);
					}
					else if (keys[i] instanceof String)
					{
						fields[i] = BytesUtil.toBytes((String) keys[i]);
					}
					else if (keys[i] instanceof Integer)
					{
						fields[i] = BytesUtil.toBytes((Integer) keys[i]);
					}
					else
					{
						throw new RuntimeException("cache key can not support except long and String");
					}
				}

				jedis = pool.getResource();
				List<byte[]> objs = jedis.hmget(id, fields);
				pool.returnResource(jedis);
				jedis = null;

				if (!objs.isEmpty())
				{
					List<Object> ret = new ArrayList<>(objs.size());
					for (byte[] o : objs)
					{
						if (o != null)
						{
							ret.add((Object) ObjectUtil.unSerialize(o));
						}
					}
					return ret;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
			finally
			{
				if (jedis != null)
				{
					pool.returnResource(jedis);
				}
			}
		}
		return new ArrayList<>(0);
	}

	@Override
	public List<Object> get(List<Object> keys)
	{
		int length = keys.size();
		if (length > 0)
		{
			Jedis jedis = null;
			try
			{
				byte[][] fields = new byte[length][];
				Object it = null;
				for (int i = 0; i < length; i++)
				{
					it = keys.get(i);
					if (it instanceof Long)
					{
						fields[i] = BytesUtil.toBytes((Long) it);
					}
					else if (it instanceof String)
					{
						fields[i] = BytesUtil.toBytes((String) it);
					}
					else if (it instanceof Integer)
					{
						fields[i] = BytesUtil.toBytes((Integer) it);
					}
					else
					{
						throw new RuntimeException("cache key can not support except long and String");
					}
				}

				jedis = pool.getResource();
				List<byte[]> objs = jedis.hmget(id, fields);
				pool.returnResource(jedis);
				jedis = null;

				if (!objs.isEmpty())
				{
					List<Object> ret = new ArrayList<>(objs.size());
					for (byte[] o : objs)
					{
						if (o != null)
						{
							ret.add(ObjectUtil.unSerialize(o));
						}
					}
					return ret;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
			finally
			{
				if (jedis != null)
				{
					pool.returnResource(jedis);
				}
			}
		}
		return new ArrayList<>(0);
	}

	@Override
	public void remove(Object[] keys)
	{
		if (keys.length > 0)
		{
			byte[][] fields = new byte[keys.length][];
			for (int i = 0; i < fields.length; i++)
			{
				if (keys[i] instanceof Long)
				{
					fields[i] = BytesUtil.toBytes((Long) keys[i]);
				}
				else if (keys[i] instanceof String)
				{
					fields[i] = BytesUtil.toBytes((String) keys[i]);
				}
				else if (keys[i] instanceof Integer)
				{
					fields[i] = BytesUtil.toBytes((Integer) keys[i]);
				}
				else
				{
					throw new RuntimeException("cache key can not support except long and String");
				}
			}

			Jedis jedis = null;
			try
			{
				jedis = pool.getResource();
				jedis.hdel(id, fields);
				pool.returnResource(jedis);
				jedis = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
			finally
			{
				if (jedis != null)
				{
					pool.returnResource(jedis);
				}
			}
		}
	}

	@Override
	public void clear()
	{
		Jedis jedis = null;
		try
		{
			jedis = pool.getResource();
			jedis.del(id);

			pool.returnResource(jedis);
			jedis = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			if (jedis != null)
			{
				pool.returnResource(jedis);
			}
		}
	}

	@Override
	public List<Object> getKeys()
	{
		Jedis jedis = null;
		try
		{
			jedis = pool.getResource();
			Set<byte[]> set = jedis.hkeys(id);
			pool.returnResource(jedis);
			jedis = null;

			List<Object> keys = new ArrayList<>(set.size());
			if (set != null && !set.isEmpty())
			{
				for (byte[] b : set)
				{
					keys.add(new String(b));
				}
			}
			return keys;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			if (jedis != null)
			{
				pool.returnResource(jedis);
			}
		}
		return new ArrayList<>(0);
	}
}
