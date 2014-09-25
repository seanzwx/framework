package com.sean.ds.service;

import java.lang.reflect.Proxy;
import java.util.List;

import org.apache.log4j.Logger;

import com.sean.ds.constant.L;
import com.sean.ds.fail.FailStrategy;
import com.sean.ds.pool.PoolStrategy;
import com.sean.ds.route.RouteStrategy;
import com.sean.log.core.LogFactory;

/**
 * 服务对象
 * @author sean
 */
@SuppressWarnings("unchecked")
public final class Service
{
	private static final Logger logger = LogFactory.getLogger(L.Ds);

	protected ServiceDefine define;
	protected List<ServiceInstance> instances;
	protected RouteStrategy routeStrategy;
	protected PoolStrategy poolStrategy;
	protected FailStrategy failStrategy;

	private Object proxy;
	private ServiceProxy<?> serviceProxy;

	/**
	 * 创建服务代理
	 */
	protected void createProxy()
	{
		try
		{
			serviceProxy = new ServiceProxy<>(this.define, this.instances, this.routeStrategy, this.failStrategy);
			Object target = Class.forName(define.proxyClass).newInstance();
			this.proxy = Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), serviceProxy);
			logger.debug("创建" + define.serviceName + "服务代理: " + target);
		}
		catch (Exception e)
		{
			logger.error("创建" + define.serviceName + "服务代理异常:" + e.getMessage(), e);
		}
	}

	/**
	 * 获取服务代理
	 * @return
	 */
	protected <E> E getServiceProxy()
	{
		return (E) proxy;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(1024);
		sb.append("{").append(define).append(", instances:").append(instances).append(", route:").append(routeStrategy.getClass().getName())
				.append("}");
		return sb.toString();
	}
}
