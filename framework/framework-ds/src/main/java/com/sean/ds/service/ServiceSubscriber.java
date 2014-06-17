package com.sean.ds.service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.alibaba.fastjson.JSON;
import com.sean.config.core.Config;
import com.sean.config.enums.ConfigEnum;
import com.sean.ds.constant.L;
import com.sean.ds.pool.PoolStrategy;
import com.sean.log.core.LogFactory;

/**
 * 服务订阅者
 * @author sean
 */
public final class ServiceSubscriber
{
	private static final Logger logger = LogFactory.getLogger(L.Ds);
	private static final String root = "services";
	private static ZooKeeper zk;
	static
	{
		try
		{
			String hostname = Config.getProperty(ConfigEnum.ZKHostname);
			int port = Integer.parseInt(Config.getProperty(ConfigEnum.ZKPort));
			SubscribeWatcher sw = new SubscribeWatcher();
			zk = new ZooKeeper(hostname + ":" + port, 3000, sw);
			sw.setZooKeeper(zk);
		}
		catch (IOException e)
		{
			logger.error("连接ZooKeeper异常:" + e.getMessage(), e);
		}
	}

	/**
	 * 订阅服务 / 使用者-客户端
	 * @param serviceName					服务名称
	 * @param route							路由策略
	 * @param pool							客户端连接池策略
	 * @param fail							失败转移策略
	 * @return
	 * @throws Exception 
	 */
	public static void subscribeService(Class<?> serviceClass, PoolStrategy pool) throws Exception
	{
		String path = "/" + root + "/" + serviceClass.getSimpleName();
		// 如果服务存在
		if (zk.exists(path, false) != null)
		{
			// 读取服务定义
			byte[] data = zk.getData(path, false, null);
			String json = new String(data);
			ServiceDefine define = JSON.parseObject(json, ServiceDefine.class);

			// 读取服务实例
			List<String> paths = zk.getChildren(path, true);
			List<ServiceInstance> instances = new LinkedList<>();
			for (String it : paths)
			{
				data = zk.getData(path + "/" + it, false, null);
				json = new String(data);
				ServiceInstance si = JSON.parseObject(json, ServiceInstance.class);
				// 如果服务实例在线则加入返回服务实例列表
				if (si.status == ServiceInstance.Status_Online)
				{
					instances.add(si);
				}
			}
			// 保存到本地服务库
			LocalService.addService(define, instances, pool);
		}
		else
		{
			throw new RuntimeException("订阅:服务" + serviceClass.getSimpleName() + "不存在");
		}
	}

	/**
	 * 读取服务客户端
	 * @param serviceClass
	 * @return
	 */
	public static <E> E getServiceClient(Class<E> serviceClass)
	{
		// 获取服务
		Service service = LocalService.getService(serviceClass.getSimpleName());
		if (service != null)
		{
			// 获取服务代理
			return service.getServiceProxy();
		}
		throw new RuntimeException("获取客户端:服务" + serviceClass.getSimpleName() + "不存在");
	}

	/**
	 * 服务实例下线
	 * @param instancePath
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 */
	protected static void offlineServiceInstance(ServiceDefine define, ServiceInstance instance) throws KeeperException, InterruptedException
	{
		instance.status = ServiceInstance.Status_Offline;
		String path = define.getInstancePath(instance);
		Stat stat = new Stat();
		byte[] data = zk.getData(path, false, stat);
		if (data != null && data.length > 0)
		{
			zk.setData(path, instance.toJson().getBytes(), stat.getVersion());
			logger.debug(define.serviceName + "服务实例" + instance + "离线");

			// 删除本地服务实例
			LocalService.removeSerrviceInstance(define.serviceName, instance);
		}
	}
}
