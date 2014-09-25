package com.sean.persist.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sean.persist.entity.EntityEntity;
import com.sean.persist.ext.Condition;
import com.sean.persist.ext.Order;
import com.sean.persist.ext.Value;

/**
 * 数据持久层接口无缓存实现
 * @author Sean
 */
public class EntityDaoNoCache<E extends Entity> extends EntityDao<E>
{
	private EntityDao<E> dao;

	public EntityDaoNoCache(EntityEntity entity, EntityDao<E> dao) throws Exception
	{
		this.dao = dao;
	}

	@Override
	public void persistBatch(List<E> entitys)
	{
		if (entitys.size() > 0)
		{
			dao.persistBatch(entitys);
		}
	}

	@Override
	public void removeById(List<Object> ids)
	{
		if (ids.size() > 0)
		{
			dao.removeById(ids);
		}
	}

	@Override
	public void updateById(List<Object> ids, List<Value> vals)
	{
		if (ids.size() > 0)
		{
			dao.updateById(ids, vals);
		}
	}

	@Override
	public List<E> loadByIds(List<Object> ids)
	{
		if (ids.size() > 0)
		{
			return dao.loadByIds(ids);
		}
		return new ArrayList<E>(0);
	}

	@Override
	public List<Object> getIdList(List<Condition> conds, List<Order> orders)
	{
		return dao.getIdList(conds, orders);
	}

	@Override
	public List<Object> getIdList(List<Condition> conds, List<Order> orders, int start, int amount)
	{
		return dao.getIdList(conds, orders, start, amount);
	}

	@Override
	public int count(List<Condition> conds)
	{
		return dao.count(conds);
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
}
