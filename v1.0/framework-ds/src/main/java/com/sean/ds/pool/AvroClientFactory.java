package com.sean.ds.pool;

import java.net.InetSocketAddress;

import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.log4j.Logger;

import com.sean.ds.constant.L;
import com.sean.log.core.LogFactory;

/**
 * avro rpc 客户端工厂
 * @author sean
 */
public final class AvroClientFactory<E> extends BasePooledObjectFactory<AvroClient<E>>
{
	private static final Logger logger = LogFactory.getLogger(L.Ds);

	private String hostname;
	private int port;
	private Class<E> clazz;

	public AvroClientFactory(Class<E> clazz, String hostname, int port)
	{
		this.hostname = hostname;
		this.port = port;
		this.clazz = clazz;
	}

	@Override
	public AvroClient<E> create() throws Exception
	{
		try
		{
			NettyTransceiver client = new NettyTransceiver(new InetSocketAddress(hostname, port));
			E proxy = SpecificRequestor.getClient(clazz, client);

			AvroClient<E> ac = new AvroClient<>();
			ac.transceiver = client;
			ac.proxy = proxy;

			logger.debug(clazz.getSimpleName() + "客户端连接池创建实例" + ac);
			return ac;
		}
		catch (Exception e)
		{
			logger.error(clazz.getSimpleName() + "客户端连接池创建客户端错误:" + e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public boolean validateObject(PooledObject<AvroClient<E>> p)
	{
		return true;
	}

	@Override
	public void destroyObject(PooledObject<AvroClient<E>> p) throws Exception
	{
		p.getObject().transceiver.close();
		logger.debug(clazz.getSimpleName() + "客户端连接池销毁实例" + p.getObject());
	}

	@Override
	public PooledObject<AvroClient<E>> wrap(AvroClient<E> obj)
	{
		return new DefaultPooledObject<AvroClient<E>>(obj);
	}
}
