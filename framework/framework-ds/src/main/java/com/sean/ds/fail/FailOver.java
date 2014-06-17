package com.sean.ds.fail;

import java.util.List;
import java.util.Random;

import com.sean.ds.service.ServiceInstance;

/**
 * 失败自动切换，当出现失败，随机重试其它服务器，通常用于读操作（推荐使用）
 * @author sean
 */
public class FailOver implements FailStrategy
{
	private Random random = new Random(System.currentTimeMillis());

	@Override
	public ServiceInstance failover(List<ServiceInstance> onlineInstances)
	{
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

}
