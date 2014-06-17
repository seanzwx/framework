package com.sean.persist.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sean.log.core.LogFactory;
import com.sean.persist.entity.DataSourceEntity;
import com.sean.persist.entity.EntityEntity;
import com.sean.persist.enums.L;

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
		
		Map<Class<?>, DataSourceEntity> dsMap = new HashMap<>();

		// 为所有实体创建EntityDao
		for (EntityEntity entity : entityManager.getEntityContainer().values())
		{
			String name = entity.getCls().getName();
			
			// 创建数据源
			DataSourceEntity ds = dsMap.get(entity.getDataSource());
			if (ds == null)
			{
				if (PersistLaucher.isUnitTest)
				{
					ds = entity.getDataSource().newInstance().providerUnitTestDs();
				}
				else
				{
					ds = entity.getDataSource().newInstance().providerDs();
				}
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
