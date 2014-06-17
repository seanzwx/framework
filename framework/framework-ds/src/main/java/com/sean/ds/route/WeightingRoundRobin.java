package com.sean.ds.route;

import java.util.List;
import java.util.Random;

import com.sean.ds.service.ServiceInstance;

/**
 * 带权重轮询的路由策略
 * @author sean
 */
public class WeightingRoundRobin implements RouteStrategy
{
	private Random random = new Random(System.currentTimeMillis());

	@Override
	public ServiceInstance getInstance(List<ServiceInstance> instances)
	{
		if (instances != null && !instances.isEmpty())
		{
			if (instances.size() == 1)
			{
				return instances.get(0);
			}
			else
			{
				// 放大权重值
				int factor = 1000;
				float probablity = 0;
				for (ServiceInstance it : instances)
				{
					probablity += it.weight * factor;
				}

				// 权重区间为0-maxRndNumber
				int maxRndNumber = (int) Math.ceil(probablity);
				// 生成随机数
				int rndNumber = this.random.nextInt(maxRndNumber);

				float currVal = 0;
				float lastVal = 0;
				for (ServiceInstance it : instances)
				{
					currVal += it.weight * factor;
					// 如果生成的随机数小于当前概率区间，则认为命中
					if (rndNumber >= lastVal && rndNumber < currVal)
					{
						return it;
					}
					lastVal = currVal;
				}
			}
		}
		return null;
	}
}
