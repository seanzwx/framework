package com.sean.persist.core;

import com.sean.persist.entity.DataSourceEntity;
import com.sean.persist.entity.EntityEntity;

/**
 * 持久化工厂
 * @author sean
 */
public abstract class PersistFactory
{
	/**
	 * 创建EntityDao
	 * @param entity
	 * @param datasource
	 * @return
	 */
	public abstract <E extends Entity> EntityDao<E> createEntityDao(EntityEntity entity, DataSourceEntity datasource) throws Exception;
	
	/**
	 * 创建实体映射检查
	 * @return
	 */
	public abstract EntityChecker createEntityChecker();
}
