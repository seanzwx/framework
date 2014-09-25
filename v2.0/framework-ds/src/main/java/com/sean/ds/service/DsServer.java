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
			String config = rc.config();

			Class<?> iface = c.getInterfaces()[0];
			String hostname = Config.getProperty("service." + config + ".hostname");
			int port = Integer.parseInt(Config.getProperty("service." + config + ".port"));
			Class<?> proxy = rc.proxy();
			Class<?> route = Class.forName(Config.getProperty("service." + config + ".route"));
			Class<?> fail = Class.forName(Config.getProperty("service." + config + ".fail"));
			float weight = 1.0f;
			if (Config.getProperty("service." + config + ".route.weight") != null)
			{
				weight = Float.parseFloat(Config.getProperty("service." + config + ".route.weight"));
			}
			int retries = 1;
			if (Config.getProperty("service." + config + ".fail.retries") != null)
			{
				retries = Integer.parseInt(Config.getProperty("service." + config + ".fail.retries"));
			}

			// 发布服务
			Object serviceImpl = c.newInstance();
			ServiceDefine define = new ServiceDefine(iface, proxy, route, fail, retries);
			ServiceInstance instance = new ServiceInstance(hostname, port, weight);
			ServicePublisher.publishService(define, serviceImpl, instance);
		}

		// 显示gc
		System.gc();
	}
}
