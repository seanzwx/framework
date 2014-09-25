package com.sean.ds.fail;

import java.util.List;

import com.sean.ds.service.ServiceDefine;
import com.sean.ds.service.ServiceInstance;

public interface FailStrategy
{
	/**
	 * 初始化线程上下文
	 */
	public void initThreadContext();
	
	/**
	 * 失败转移
	 * @param serviceDefine				服务定义
	 * @param failInstance				失败的服务实例
	 * @param onlineInstances			当前在线服务实例
	 * @return							返回转移服务实例
	 */
	public ServiceInstance failover(ServiceDefine serviceDefine, ServiceInstance failInstance, List<ServiceInstance> onlineInstances,
			Throwable exception);
	
	/**
	 * 清理线程上下文
	 */
	public void cleanThreadContext();
}
