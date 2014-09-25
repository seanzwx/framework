package com.sean.persist.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sean.log.core.LogFactory;
import com.sean.persist.entity.EntityEntity;
import com.sean.persist.enums.L;
import com.sean.persist.parser.EntityParser;

/**
 * 实体管理器
 * @author Sean
 */
public final class EntityManager
{
	private Map<String, EntityEntity> entityContainer;
	private static final Logger logger = LogFactory.getLogger(L.Persist);

	public EntityManager(List<Class<?>> entitys) throws Exception
	{
		// 日志
		logger.info("EntityManager start initializing...");

		// 解析Entity实体，初始化Entity容器
		EntityParser parser = new EntityParser();
		this.entityContainer = new HashMap<String, EntityEntity>();
		for (int i = 0; i < entitys.size(); i++)
		{
			EntityEntity entity = parser.parse(entitys.get(i));
			if (this.entityContainer.get(entity.getCls().getName()) != null)
			{
				throw new RuntimeException("the entity " + entity.getCls().getName() + " defined repeated, please try to define in other class names");
			}
			this.entityContainer.put(entity.getCls().getName(), entity);
		}

//		EntityChecker entityChecker = persistContextManager.getPersistContext("").getPersistFactory().createEntityChecker();
//		entityChecker.checkMapping(entityContainer);

		logger.info("The EntityManager initailized successfully");
	}

	/**
	 * 获取Entity容器
	 * @return
	 */
	public Map<String, EntityEntity> getEntityContainer()
	{
		return this.entityContainer;
	}
}
