package com.sean.ds.fail;

import java.util.List;

import com.sean.ds.service.ServiceDefine;
import com.sean.ds.service.ServiceInstance;

/**
 * 失败自动切换，当出现失败，不做任何操作
 * @author sean
 */
public class FailSafe implements FailStrategy
{
	@Override
	public ServiceInstance failover(ServiceDefine serviceDefine, ServiceInstance failInstance, List<ServiceInstance> onlineInstances,
			Throwable exception)
	{
		return null;
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
