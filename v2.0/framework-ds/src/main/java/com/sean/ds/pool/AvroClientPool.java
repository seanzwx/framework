package com.sean.ds.pool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;

import com.sean.ds.constant.L;
import com.sean.log.core.LogFactory;

/**
 * 连接池
 * @author sean
 * @param <E> avro client
 */
public class AvroClientPool<E> extends GenericObjectPool<AvroClient<E>> implements ClientPool<E>
{
	private static final Logger logger = LogFactory.getLogger(L.Ds);
	private String name;

	public AvroClientPool(String name, PooledObjectFactory<AvroClient<E>> factory, GenericObjectPoolConfig cfg)
	{
		super(factory, cfg);
		this.name = name;
		logger.debug("创建"+ name +"服务客户端连接池");
	}

	@Override
	public AvroClient<E> openClient()
	{
		try
		{
			return this.borrowObject();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void returnClient(AvroClient<E> client)
	{
		this.returnObject(client);	
	}

	@Override
	public void clearAll()
	{
		this.clear();
	}

	@Override
	public void destory()
	{
		this.clear();
		if (!this.isClosed())
		{
			this.close();
		}
		logger.debug("销毁连接池" + name);
	}
}
