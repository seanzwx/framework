package com.sean.persist.rdb.core;

import java.util.List;

import javax.sql.DataSource;

import com.sean.persist.core.Entity;
import com.sean.persist.entity.EntityEntity;
import com.sean.persist.ext.Condition;
import com.sean.persist.ext.Order;

/**
 * 持久化Oracle实现
 * @author sean
 */
public final class EntityDaoRdbOracle<E extends Entity> extends EntityDaoRdb<E>
{	
	public EntityDaoRdbOracle(EntityEntity entity, DataSource dataSource, boolean showSql) throws Exception
	{
		super(entity, dataSource, showSql);
	}

	@Override
	public List<Object> getIdList(List<Condition> conds, List<Order> orders, int start, int amount)
	{
		return null;
	}

	@Override
	public void persistBatch(List<E> entitys)
	{
	}
}
