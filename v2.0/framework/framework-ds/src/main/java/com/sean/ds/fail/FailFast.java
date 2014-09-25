package com.sean.ds.fail;

import java.util.List;

import com.sean.ds.service.ServiceDefine;
import com.sean.ds.service.ServiceInstance;

/**
 * 快速失败，只发起一次调用，失败立即报错,通常用于非幂等性的写操作
 * @author sean
 */
public class FailFast implements FailStrategy
{
	@Override
	public ServiceInstance failover(ServiceDefine serviceDefine, ServiceInstance failInstance, List<ServiceInstance> onlineInstances,
			Throwable exception)
	{
		throw new RuntimeException("调用服务" + serviceDefine + "失败", exception);
	}

	@Override
	public void initThreadContext()
	{
	}

	@Override
	public void cleanThreadContext()
	{
	}

}
