package com.sean.persist.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sean.config.core.Config;
import com.sean.log.core.LogFactory;
import com.sean.persist.entity.DataSourceEntity;
import com.sean.persist.entity.EntityEntity;
import com.sean.persist.enums.DataBaseType;
import com.sean.persist.enums.L;
import com.sean.persist.enums.ProviderType;

/**
 * 数据库访问接口管理器
 * @author sean
 */
@SuppressWarnings("unchecked")
public final class EntityDaoManager
{
	private Map<String, EntityDao<?>> entityDaos;
	private static final Logger logger = LogFactory.getLogger(L.Persist);

	public EntityDaoManager(EntityManager entityManager) throws Exception
	{
		// 日志
		logger.info("EntityDaoManager start initializing...");

		this.entityDaos = new HashMap<>();

		Map<String, DataSourceEntity> dsMap = new HashMap<>();

		// 为所有实体创建EntityDao
		for (EntityEntity entity : entityManager.getEntityContainer().values())
		{
			String name = entity.getCls().getName();

			// 创建数据源
			DataSourceEntity ds = dsMap.get(entity.getDataSource());
			if (ds == null)
			{
				String dsname = entity.getDataSource();
				String hostname = Config.getProperty("db." + dsname + ".hostname");
				int port = Integer.parseInt(Config.getProperty("db." + dsname + ".port"));
				String dbname = Config.getProperty("db." + dsname + ".dbname");
				String user = Config.getProperty("db." + dsname + ".user");
				String passwd = Config.getProperty("db." + dsname + ".passwd");
				String jndi = Config.getProperty("db." + dsname + ".jndi");
				boolean showsql = Boolean.parseBoolean(Config.getProperty("db." + dsname + ".showsql"));
				// mysql | oracle | mongo | hbase
				String dbtype = Config.getProperty("db." + dsname + ".dbtype");
				// jndi | dbcp
				String datasource = Config.getProperty("db." + dsname + ".datasource");

				DataBaseType dbType = DataBaseType.MySQL;
				switch (dbtype)
				{
				case "mysql":
					dbType = DataBaseType.MySQL;
					break;
				case "oracle":
					dbType = DataBaseType.Oracle;
					break;
				case "mongo":
					dbType = DataBaseType.Mongo;
					break;
				case "hbase":
					dbType = DataBaseType.HBase;
					break;
				default:
					break;
				}

				ProviderType providerType = ProviderType.Dbcp;
				switch (datasource)
				{
				case "dbcp":
					providerType = ProviderType.Dbcp;
					break;
				case "jndi":
					providerType = ProviderType.Jndi;
					break;
				default:
					break;
				}

				ds = new DataSourceEntity(providerType, dbType, hostname, port, dbname, user, passwd, jndi, showsql);
				dsMap.put(entity.getDataSource(), ds);
			}

			EntityDao<?> dao = ds.getPersistFactory().createEntityDao(entity, ds);
			this.entityDaos.put(name, dao);
			// 打印创建日志
			logger.debug("create EntityDao named " + name + " for entity " + name + " successfully");
		}

		// 日志
		logger.info("The EntityDaoManager initailized successfully");
	}

	/**
	 * 获取数据库访问接口
	 * @param dataSource					数据源
	 * @return
	 */
	public <E extends Entity> EntityDao<E> getEntityDao(Class<E> entity)
	{
		return (EntityDao<E>) this.entityDaos.get(entity.getName());
	}
}
