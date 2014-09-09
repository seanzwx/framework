package com.sean.ds.fail;

import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.sean.ds.constant.L;
import com.sean.ds.service.ServiceDefine;
import com.sean.ds.service.ServiceInstance;
import com.sean.log.core.LogFactory;

/**
 * 失败自动切换，当出现失败，随机重试其它服务器，通常用于读操作（推荐使用）
 * @author sean
 */
public class FailOver implements FailStrategy
{
	private static final Logger logger = LogFactory.getLogger(L.Ds);

	private Random random = new Random(System.currentTimeMillis());
	private ThreadLocal<Integer> retiesTimes = new ThreadLocal<>();

	@Override
	public ServiceInstance failover(ServiceDefine serviceDefine, ServiceInstance failInstance, List<ServiceInstance> onlineInstances,
			Throwable exception)
	{
		int retries = retiesTimes.get() + 1;
		// 超过重试次数
		if (retries <= serviceDefine.getFailRetries())
		{
			retiesTimes.set(retries);
			logger.error("调用服务" + serviceDefine + "失败, 重试第" + retries + "次", exception);

			int size = onlineInstances.size();
			switch (size)
			{
			case 0:
				return null;
			case 1:
				return onlineInstances.get(0);
			default:
				return onlineInstances.get(random.nextInt(size));
			}
		}
		else
		{
			throw new RuntimeException("调用服务" + serviceDefine + "失败超过重试次数", exception);
		}
	}

	@Override
	public void initThreadContext()
	{
		retiesTimes.set(0);
	}

	@Override
	public void cleanThreadContext()
	{
		retiesTimes.remove();
	}

}
