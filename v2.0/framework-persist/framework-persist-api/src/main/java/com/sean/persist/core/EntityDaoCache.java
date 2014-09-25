package com.sean.persist.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.sean.cache.core.Cache;
import com.sean.cache.core.CacheFactory;
import com.sean.cache.core.CacheFactoryEhCache;
import com.sean.cache.core.CacheFactoryRedis;
import com.sean.config.core.Config;
import com.sean.config.enums.ConfigEnum;
import com.sean.persist.entity.DataSourceEntity;
import com.sean.persist.entity.EntityEntity;
import com.sean.persist.ext.Condition;
import com.sean.persist.ext.Order;
import com.sean.persist.ext.Value;
import com.sean.persist.util.EntityDaoUtil;

/**
 * 数据持久层接口提供缓存实现
 * @author Sean
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class EntityDaoCache<E extends Entity> extends EntityDao<E>
{
	private static final int statementLength = 1024;

	private Cache entityCache;// 实体缓存
	private CachePolicy cachePolicy;// 缓存策略
	private String dbName, tableName;
	private static CacheFactory cacheFactory;

	// 数据库的具体实现
	private EntityDao<E> dao;

	public EntityDaoCache(EntityEntity entity, EntityDao<E> dao, DataSourceEntity ds) throws Exception
	{
		this.dao = dao;
		this.dbName = ds.getDbName();
		this.tableName = entity.getTableName();

		String persist = Config.getProperty(ConfigEnum.PersistCachePersist);

		// 创建redis缓存工厂
		if ("redis".equals(persist))
		{
			if (cacheFactory == null)
			{
				String hostname = Config.getProperty(ConfigEnum.PersistCacheRedisHostname);
				int port = Integer.parseInt(Config.getProperty(ConfigEnum.PersistCacheRedisPort));
				cacheFactory = new CacheFactoryRedis(hostname, port);
			}
			entityCache = cacheFactory.createCache(dbName + "_" + tableName + "_entity", 0);
		}
		// 默认创建ehcache
		else
		{
			if (cacheFactory == null)
			{
				cacheFactory = new CacheFactoryEhCache();
			}
			entityCache = cacheFactory.createCache(dbName + "_" + tableName + "_entity", 10000, 3600, 3600);
		}

		// 创建缓存策略
		String cacheName = entity.getCachePolicy().getSimpleName();
		cachePolicy = PersistContext.CTX.cachePolicys.get(cacheName);
		if (cachePolicy == null)
		{
			cachePolicy = entity.getCachePolicy().newInstance();
			PersistContext.CTX.cachePolicys.put(cacheName, cachePolicy);
		}
	}

	@Override
	public void persistBatch(List<E> entitys)
	{
		if (entitys.size() > 0)
		{
			dao.persistBatch(entitys);
			// 刷新语句缓存
			String cacheName = this.cachePolicy.getStatementCacheName(dbName, tableName);
			Cache cache = cacheFactory.getCache(cacheName);
			cache.clear();
		}
	}

	@Override
	public void removeById(List<Object> ids)
	{
		if (!ids.isEmpty())
		{
			dao.removeById(ids);

			// 删除实体缓存
			Object[] keys = new Object[ids.size()];
			for (int i = 0; i < keys.length; i++)
			{
				keys[i] = ids.get(i);
			}
			this.entityCache.remove(keys);
			// 刷新语句缓存
			String cacheName = this.cachePolicy.getStatementCacheName(dbName, tableName);
			Cache cache = cacheFactory.getCache(cacheName);
			cache.clear();
		}
	}

	@Override
	public void updateById(List<Object> ids, List<Value> vals)
	{
		if (!ids.isEmpty())
		{
			dao.updateById(ids, vals);

			// 删除实体缓存
			Object[] keys = new Object[ids.size()];
			for (int i = 0; i < keys.length; i++)
			{
				keys[i] = ids.get(i);
			}
			this.entityCache.remove(keys);
			// 刷新语句缓存
			String cacheName = this.cachePolicy.getStatementCacheName(dbName, tableName);
			Cache cache = cacheFactory.getCache(cacheName);
			cache.clear();
		}
	}

	@Override
	public List<E> loadByIds(List<Object> ids)
	{
		if (ids.isEmpty())
		{
			return new ArrayList<E>(0);
		}

		int idLen = ids.size();
		List<Object> cached = this.entityCache.get(ids);
		int cacheLen = cached.size();
		Map<Object, E> hit = new HashMap<Object, E>(cacheLen);
		E it = null;
		for (Object o : cached)
		{
			it = (E) o;
			hit.put(it.getKey(), it);
		}
		int lostLen = idLen - cacheLen;
		List<Object> lost = new ArrayList<>(lostLen);
		for (Object id : ids)
		{
			it = hit.get(id);
			if (it == null)
			{
				lost.add(id);
			}
		}

		Map<Object, E> records = null;
		if (lostLen > 0)
		{
			List<E> tmp = dao.loadByIds(lost);

			List<Object> keys = new ArrayList<>(tmp.size());
			records = new HashMap<>(lostLen);
			for (E item : tmp)
			{
				keys.add(item.getKey());
				records.put(item.getKey(), item);
			}

			// 保存到缓存中
			this.entityCache.putList(keys, (List) tmp);
		}

		List<E> datas = new ArrayList<E>(idLen);
		for (Object id : ids)
		{
			it = hit.get(id);
			if (it != null)
			{
				datas.add(it);
			}
			else
			{
				it = records.get(id);
				if (it != null)
				{
					datas.add(it);
				}
			}
		}
		return datas;
	}

	@Override
	public List<Object> getIdList(List<Condition> conds, List<Order> orders)
	{
		StringBuilder tmp = new StringBuilder(statementLength);
		tmp.append(this.statementToString(conds, orders)).append('_');
		for (Condition cond : conds)
		{
			tmp.append(cond.getValue()).append(',');
		}
		String key = DigestUtils.md5Hex(tmp.toString());

		// 获取语句缓存
		String cacheName = this.cachePolicy.getStatementCacheName(dbName, tableName);
		Cache cache = cacheFactory.getCache(cacheName);

		Object item = cache.get(key);
		if (item != null)
		{
			return (List<Object>) item;
		}
		else
		{
			List<Object> ids = dao.getIdList(conds, orders);
			cache.put(key, ids);
			if (ids.isEmpty())
			{
				return new ArrayList<>(0);
			}
			return ids;
		}
	}

	@Override
	public List<Object> getIdList(List<Condition> conds, List<Order> orders, int start, int limit)
	{
		StringBuilder tmp = new StringBuilder(statementLength);
		tmp.append(this.limitStatementToString(conds, orders)).append('_');
		for (Condition cond : conds)
		{
			tmp.append(cond.getValue()).append(',');
		}
		tmp.append(start).append(',').append(limit);
		String key = DigestUtils.md5Hex(tmp.toString());

		// 获取语句缓存
		String cacheName = this.cachePolicy.getStatementCacheName(dbName, tableName);
		Cache cache = cacheFactory.getCache(cacheName);

		Object item = cache.get(key);
		if (item != null)
		{
			return (List<Object>) item;
		}
		else
		{
			List<Object> ids = dao.getIdList(conds, orders, start, limit);
			cache.put(key, ids);
			if (ids.isEmpty())
			{
				return new ArrayList<>(0);
			}
			return ids;
		}
	}

	@Override
	public int count(List<Condition> conds)
	{
		StringBuilder tmp = new StringBuilder(statementLength);
		tmp.append(this.countStatementToString(conds)).append('_');
		for (Condition cond : conds)
		{
			tmp.append(cond.getValue()).append(',');
		}
		String key = DigestUtils.md5Hex(tmp.toString());

		// 获取语句缓存
		String cacheName = this.cachePolicy.getStatementCacheName(dbName, tableName);
		Cache cache = cacheFactory.getCache(cacheName);

		Object item = cache.get(key);
		if (item != null)
		{
			return (Integer) item;
		}
		else
		{
			int count = dao.count(conds);
			cache.put(key, count);
			return count;
		}
	}

	@Override
	public Object executeScalar(Object statement)
	{
		return dao.executeScalar(statement);
	}

	@Override
	public Map<String, Object> executeMap(Object statement)
	{
		return dao.executeMap(statement);
	}

	@Override
	public List<Map<String, Object>> executeList(Object statement)
	{
		return dao.executeList(statement);
	}

	@Override
	public List<E> executeEntityList(Object statement)
	{
		return dao.executeEntityList(statement);
	}

	/**
	 * 查询语句转化成String
	 * @param statement					数据库查询语句
	 * @return
	 */
	private String statementToString(List<Condition> conds, List<Order> orders)
	{
		StringBuilder stat = new StringBuilder(statementLength);
		stat.append("select id").append(" where ");
		stat.append(EntityDaoUtil.getConditionStr(conds));
		if (orders != null && !orders.isEmpty())
		{
			stat.append(EntityDaoUtil.getOrdersStr(orders));
		}
		return stat.toString();
	}

	/**
	 * 查询语句转化成String
	 * @param statement					数据库查询语句
	 * @return
	 */
	private String limitStatementToString(List<Condition> conds, List<Order> orders)
	{
		StringBuilder stat = new StringBuilder(statementLength);
		stat.append(this.statementToString(conds, orders));
		stat.append(" limit");
		return stat.toString();
	}

	/**
	 * 统计语句转化成String
	 * @param conds
	 * @return
	 */
	private String countStatementToString(List<Condition> conds)
	{
		StringBuilder stat = new StringBuilder(statementLength);
		stat.append("select count ").append(" where ");
		stat.append(EntityDaoUtil.getConditionStr(conds));
		return stat.toString();
	}
}
