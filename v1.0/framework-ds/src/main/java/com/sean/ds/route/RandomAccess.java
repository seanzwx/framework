package com.sean.ds.route;

import java.util.List;
import java.util.Random;

import com.sean.ds.service.ServiceInstance;

/**
 * 随机保持策略，在有效的服务中随机选择一个
 * @author sean
 */
public class RandomAccess implements RouteStrategy
{
	private Random random;

	public RandomAccess()
	{
		random = new Random(System.currentTimeMillis());
	}

	@Override
	public ServiceInstance getInstance(List<ServiceInstance> instances)
	{
		if (instances != null && !instances.isEmpty())
		{
			int size = instances.size();
			if (size > 0)
			{
				return instances.get(random.nextInt(size));
			}
		}
		return null;
	}

}
