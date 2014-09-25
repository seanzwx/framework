package com.sean.persist.rdb.core;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.sean.persist.core.Entity;
import com.sean.persist.core.EntityChecker;
import com.sean.persist.core.EntityDao;
import com.sean.persist.core.EntityDaoCache;
import com.sean.persist.core.EntityDaoNoCache;
import com.sean.persist.core.PersistFactory;
import com.sean.persist.entity.DataSourceEntity;
import com.sean.persist.entity.EntityEntity;
import com.sean.persist.enums.DataBaseType;
import com.sean.persist.enums.ProviderType;

/**
 * 关系型数据库工厂
 * @author sean
 */
public class PersistFactoryRdb extends PersistFactory
{
	@Override
	public <E extends Entity> EntityDao<E> createEntityDao(EntityEntity entity, DataSourceEntity datasource) throws Exception
	{
		if (datasource.getDbType() == DataBaseType.MySQL)
		{
			if (entity.isCache())
			{
				return new EntityDaoCache<E>(entity, new EntityDaoRdbMysql<E>(entity, this.getDataSource(datasource), datasource.isShowSql()),
						datasource);
			}
			else
			{
				return new EntityDaoNoCache<E>(entity, new EntityDaoRdbMysql<E>(entity, this.getDataSource(datasource), datasource.isShowSql()));
			}
		}
		else if (datasource.getDbType() == DataBaseType.Oracle)
		{
			if (entity.isCache())
			{
				return new EntityDaoCache<E>(entity, new EntityDaoRdbOracle<E>(entity, this.getDataSource(datasource), datasource.isShowSql()),
						datasource);
			}
			else
			{
				return new EntityDaoNoCache<E>(entity, new EntityDaoRdbOracle<E>(entity, this.getDataSource(datasource), datasource.isShowSql()));
			}
		}
		return null;
	}

	@Override
	public EntityChecker createEntityChecker()
	{
		return null;
	}

	/**
	 * 获取关系型数据库数据源
	 * @param datasource
	 * @return
	 */
	private DataSource getDataSource(DataSourceEntity ds) throws Exception
	{
		if (ds.getProviderType() == ProviderType.Dbcp)
		{
			BasicDataSource bds = new BasicDataSource();
			bds.setDriverClassName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://" + ds.getHostname() + ":" + ds.getPort() + "/" + ds.getDbName()
					+ "?useUnicode=true&characterEncoding=utf-8&autoReconnect=true";
			bds.setUrl(url);
			bds.setUsername(ds.getUser());
			bds.setPassword(ds.getPassword());

			bds.setValidationQuery("select 1");
			bds.setTestWhileIdle(true);
			return bds;
		}
		else
		{
			InitialContext ctx = new InitialContext();
			DataSource datasource = (DataSource) ctx.lookup(ds.getJndi());
			return datasource;
		}
	}
}
