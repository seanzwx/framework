package com.sean.ds.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;

import com.sean.ds.constant.L;
import com.sean.ds.fail.FailStrategy;
import com.sean.ds.pool.PoolStrategy;
import com.sean.ds.route.RouteStrategy;
import com.sean.log.core.LogFactory;

/**
 * 本地服务管理
 * @author sean
 */
public final class LocalService
{
	private static final Logger logger = LogFactory.getLogger(L.Ds);

	private static Map<String, Service> services = new HashMap<>();

	/**
	 * 添加服务
	 * @param define
	 * @param route
	 * @param poolStrategy
	 */
	protected static synchronized void addService(ServiceDefine define, List<ServiceInstance> instances, PoolStrategy poolStrategy) throws Exception
	{
		if (getService(define.serviceName) == null)
		{
			define.reflectClass();

			Service service = new Service();
			service.define = define;
			service.instances = instances;
			service.routeStrategy = (RouteStrategy) define.getRouteClass().newInstance();
			service.poolStrategy = poolStrategy;
			service.failStrategy = (FailStrategy) define.getFailClass().newInstance();

			logger.debug("成功订阅服务:" + service);

			// 创建服务代理
			service.createProxy();
			// 初始化服务实例客户端连接池
			for (ServiceInstance it : instances)
			{
				logger.debug("创建" + define.serviceName + "服务实例: " + it);
				it.createClientPool(define.getServiceClass(), poolStrategy);
			}
			services.put(define.serviceName, service);
		}
	}

	/**
	 * 更新服务实例列表
	 * @param define
	 */
	protected static synchronized void updateServiceInstances(String serviceName, List<ServiceInstance> instances)
	{
		Service service = services.get(serviceName);
		if (service != null)
		{
			List<ServiceInstance> newList = new LinkedList<>();

			boolean exists = false;
			for (ServiceInstance it : instances)
			{
				exists = false;
				for (ServiceInstance it2 : service.instances)
				{
					// 已经存在
					if (it.hostname.equals(it2.hostname) && it.port == it2.port)
					{
						service.instances.remove(it2);
						newList.add(it2);
						exists = true;
						break;
					}
				}
				// 新服务实例
				if (!exists)
				{
					logger.debug("创建" + service.define.serviceName + "服务实例: " + it);
					it.createClientPool(service.define.getServiceClass(), service.poolStrategy);
					newList.add(it);
				}
			}
			// 销毁失效的服务实例
			for (ServiceInstance it : service.instances)
			{
				it.destory();
			}
			service.instances.clear();
			service.instances.addAll(newList);
		}
	}

	/**
	 * 读取服务
	 * @param serviceName
	 * @return
	 */
	protected static synchronized Service getService(String serviceName)
	{
		return services.get(serviceName);
	}

	/**
	 * 删除服务实例
	 * @param serviceName
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 */
	protected static synchronized void removeServiceInstance(String serviceName, ServiceInstance instance) throws KeeperException,
			InterruptedException
	{
		Service service = services.get(serviceName);
		if (service != null)
		{
			// 删除本地服务实例列表
			if (service.instances.remove(instance))
			{
				// 销毁服务实例
				instance.destory();
			}
			logger.debug("删除" + serviceName + "离线服务实例: " + instance);
		}
	}
}
