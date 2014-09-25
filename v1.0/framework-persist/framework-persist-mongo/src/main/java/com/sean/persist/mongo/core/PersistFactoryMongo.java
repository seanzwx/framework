package com.sean.persist.mongo.core;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.sean.persist.core.Entity;
import com.sean.persist.core.EntityChecker;
import com.sean.persist.core.EntityDao;
import com.sean.persist.core.EntityDaoCache;
import com.sean.persist.core.EntityDaoNoCache;
import com.sean.persist.core.PersistFactory;
import com.sean.persist.entity.DataSourceEntity;
import com.sean.persist.entity.EntityEntity;

/**
 * Mongo数据库工厂
 * @author sean
 */
public class PersistFactoryMongo extends PersistFactory
{
	private Map<DataSourceEntity, MongoClient> clients = new HashMap<>();

	@Override
	public <E extends Entity> EntityDao<E> createEntityDao(EntityEntity entity, DataSourceEntity dataSource) throws Exception
	{
		MongoClient client = clients.get(dataSource);
		if (client == null)
		{
			MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
			
			builder.socketKeepAlive(true);
//			builder.autoConnectRetry(true);
//			builder.maxAutoConnectRetryTime(3);
			builder.writeConcern(WriteConcern.UNACKNOWLEDGED);
			client = new MongoClient(new ServerAddress(dataSource.getHostname(), dataSource.getPort()), builder.build());
			clients.put(dataSource, client);
		}
		if (entity.isCache())
		{
			return new EntityDaoCache<E>(entity, new EntityDaoMongo<E>(entity, dataSource, client), dataSource);
		}
		else
		{
			return new EntityDaoNoCache<E>(entity, new EntityDaoMongo<E>(entity, dataSource, client));
		}
	}

	@Override
	public EntityChecker createEntityChecker()
	{
		return null;
	}
}
