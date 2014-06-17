package com.sean.ds.fail;

import java.util.List;

import com.sean.ds.service.ServiceInstance;

public interface FailStrategy
{
	/**
	 * 失败转移
	 * @param onlineInstances			当前在线服务实例
	 * @return							返回转移服务实例
	 */
	public ServiceInstance failover(List<ServiceInstance> onlineInstances);
}
