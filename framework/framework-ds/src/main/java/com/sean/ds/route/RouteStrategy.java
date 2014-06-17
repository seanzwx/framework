package com.sean.ds.route;

import java.util.List;

import com.sean.ds.service.ServiceInstance;

/**
 * 路由策略
 * @author sean
 */
public interface RouteStrategy
{
	/**
	 * 读取服务实例
	 * @param service
	 * @return
	 */
	public ServiceInstance getInstance(List<ServiceInstance> instances);
}
