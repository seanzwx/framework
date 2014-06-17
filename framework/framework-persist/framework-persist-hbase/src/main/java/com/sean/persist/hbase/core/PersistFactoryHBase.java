package com.sean.persist.hbase.core;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import com.sean.persist.core.Entity;
import com.sean.persist.core.EntityChecker;
import com.sean.persist.core.EntityDao;
import com.sean.persist.core.EntityDaoCache;
import com.sean.persist.core.EntityDaoNoCache;
import com.sean.persist.core.PersistFactory;
import com.sean.persist.entity.DataSourceEntity;
import com.sean.persist.entity.EntityEntity;

/**
 * HBase数据库工厂
 * @author sean
 */
public class PersistFactoryHBase extends PersistFactory
{
	@Override
	public <E extends Entity> EntityDao<E> createEntityDao(EntityEntity entity, DataSourceEntity datasource) throws Exception
	{
		Configuration conf = HBaseConfiguration.create();
		if (entity.isCache())
		{
			return new EntityDaoCache<E>(entity, new EntityDaoHBase<E>(entity, conf, 32), datasource);
		}
		else
		{
			return new EntityDaoNoCache<E>(entity, new EntityDaoHBase<E>(entity, conf, 32));
		}
	}

	@Override
	public EntityChecker createEntityChecker()
	{
		return null;
	}
}
