package com.sean.ds.service;

import java.util.List;
import java.util.Map;

import com.sean.config.core.Config;
import com.sean.ds.annotation.ServiceConfig;

/**
 * 分布式服务
 * @author sean
 */
public final class DsServer
{
	/**
	 * 启动分布式服务
	 * @param packageNames					包名前缀数组
	 * @throws Exception 
	 */
	public static void start(String[] packageNames) throws Exception
	{
		// 扫描所有服务类
		ClassCollector cc = new ClassCollector(packageNames);
		Map<String, List<Class<?>>> classes = cc.collect();

		// 顺序启动并发布服务
		List<Class<?>> servers = classes.get("services");
		for (Class<?> c : servers)
		{
			ServiceConfig rc = c.getAnnotation(ServiceConfig.class);
			Class<?> iface = c.getInterfaces()[0];
			Class<?> proxy = rc.proxy();
			Class<?> route = rc.route();
			Class<?> fail = rc.fail();

			// 发布服务
			String addrs = Config.getProperty(iface.getName());
			// 使用配置文件发布服务, hostname1:port1,hostname2:port2， 多个实例运行与同一个jvm进程中
			if (addrs != null)
			{
				String[] items = addrs.split(",");
				for (String it : items)
				{
					String[] addr = it.split(":");

					Object serviceImpl = c.newInstance();
					ServiceDefine define = new ServiceDefine(iface, proxy, route, fail);
					ServiceInstance instance = new ServiceInstance(addr[0].trim(), Integer.parseInt(addr[1]), rc.weight());
					ServicePublisher.publishService(define, serviceImpl, instance);
				}
			}
			// 直接使用annocation发布服务
			else
			{
				Object serviceImpl = c.newInstance();
				ServiceDefine define = new ServiceDefine(iface, proxy, route, fail);
				ServiceInstance instance = new ServiceInstance(rc.hostname(), rc.port(), rc.weight());
				ServicePublisher.publishService(define, serviceImpl, instance);
			}
		}

		// 显示gc
		System.gc();
	}
}
