package com.sean.ds.fail;

import java.util.List;

import com.sean.ds.service.ServiceInstance;

/**
 * 快速失败，只发起一次调用，失败立即报错,通常用于非幂等性的写操作
 * @author sean
 */
public class FailFast implements FailStrategy
{
	@Override
	public ServiceInstance failover(List<ServiceInstance> onlineInstances)
	{
		throw new RuntimeException("RPC调用失败");
	}

}
