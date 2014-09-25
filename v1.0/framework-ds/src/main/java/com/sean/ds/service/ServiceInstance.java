package com.sean.ds.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.sean.ds.constant.L;
import com.sean.ds.pool.ClientPool;
import com.sean.ds.pool.PoolStrategy;
import com.sean.log.core.LogFactory;

/**
 * 服务实例
 * @author sean
 */
@SuppressWarnings("unchecked")
public final class ServiceInstance
{
	private static final Logger logger = LogFactory.getLogger(L.Ds);

	public String hostname;
	public int port;
	public byte status = Status_Offline;
	public float weight = 1.0f; // 权重, 主要针对带权重的路由策略

	private ClientPool<?> pool;

	public static final byte Status_Online = 1;
	public static final byte Status_Offline = 0;

	public ServiceInstance()
	{
	}

	public ServiceInstance(String hostname, int port, float weight)
	{
		this.hostname = hostname;
		this.port = port;
		this.weight = weight;
	}

	/**
	 * 创建客户端连接池
	 * @param poolStrategy
	 */
	protected <E> void createClientPool(Class<E> serviceClass, PoolStrategy poolStrategy)
	{
		if (pool == null)
		{
			this.pool = poolStrategy.createPool(serviceClass, hostname, port);
		}
	}

	protected <E> ClientPool<E> getClientPool()
	{
		return (ClientPool<E>) pool;
	}

	/**
	 * 销毁服务实例
	 */
	protected void destory()
	{
		if (pool != null)
		{
			pool.destory();
		}
		logger.debug("销毁服务实例: " + this);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(128);
		sb.append("hostname:").append(hostname).append(", port:").append(port);
		return sb.toString();
	}

	public String toJson()
	{
		Map<String, String> json = new LinkedHashMap<>(4);
		json.put("hostname", hostname);
		json.put("port", String.valueOf(port));
		json.put("weight", String.valueOf(weight));
		json.put("status", String.valueOf(status));
		return JSON.toJSONString(json);
	}
}
