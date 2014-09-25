package com.sean.persist.entity;

import com.sean.persist.core.PersistFactory;
import com.sean.persist.enums.DataBaseType;
import com.sean.persist.enums.ProviderType;

/**
 * 持久化上下文实体
 * @author sean
 */
public class DataSourceEntity
{
	private ProviderType providerType;
	private DataBaseType dbType;
	private String hostname;
	private int port;
	private String dbName;
	private String user;
	private String password;
	private String jndi;
	private boolean showSql;

	private PersistFactory persistFactory;

	/**
	 * 初始化数据源
	 * @param providerType
	 * @param dbType
	 * @param hostname
	 * @param port
	 * @param dbName
	 * @param user
	 * @param password
	 * @param jndi
	 * @param showSql					只对关系型数据库有效
	 */
	public DataSourceEntity(ProviderType providerType, DataBaseType dbType, String hostname, int port, String dbName, String user, String password,
			String jndi, boolean showSql)
	{
		try
		{
			this.providerType = providerType;
			this.dbType = dbType;
			this.hostname = hostname;
			this.port = port;
			this.dbName = dbName;
			this.user = user;
			this.password = password;
			this.jndi = jndi;
			this.showSql = showSql;

			// 初始化持久化工厂
			switch (dbType)
			{
			case MySQL:
				this.initPersistFactory("com.sean.persist.rdb.core.PersistFactoryRdb");
				break;
			case Oracle:
				this.initPersistFactory("com.sean.persist.rdb.core.PersistFactoryRdb");
				break;
			case Mongo:
				this.initPersistFactory("com.sean.persist.mongo.core.PersistFactoryMongo");
				break;
			case HBase:
				this.initPersistFactory("com.sean.persist.hbase.core.PersistFactoryHBase");
				break;
			default:
				break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void initPersistFactory(String className) throws Exception
	{
		Class<?> cls = Class.forName(className);
		persistFactory = (PersistFactory) cls.newInstance();
	}

	public ProviderType getProviderType()
	{
		return providerType;
	}

	public DataBaseType getDbType()
	{
		return dbType;
	}

	public String getHostname()
	{
		return hostname;
	}

	public int getPort()
	{
		return port;
	}

	public String getDbName()
	{
		return dbName;
	}

	public String getUser()
	{
		return user;
	}

	public String getPassword()
	{
		return password;
	}

	public String getJndi()
	{
		return jndi;
	}

	public boolean isShowSql()
	{
		return showSql;
	}

	public PersistFactory getPersistFactory()
	{
		return persistFactory;
	}

	@Override
	public String toString()
	{
		return "DataSourceEntity [providerType=" + providerType + ", dbType=" + dbType + ", hostname=" + hostname + ", port=" + port + ", dbName="
				+ dbName + ", user=" + user + ", password=" + password + ", jndi=" + jndi + ", showSql=" + showSql + ", persistFactory="
				+ persistFactory + "]";
	}

}
