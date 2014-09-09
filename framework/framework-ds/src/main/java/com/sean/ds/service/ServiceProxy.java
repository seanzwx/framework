package com.sean.ds.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.avro.specific.SpecificExceptionBase;
import org.apache.log4j.Logger;

import com.sean.ds.constant.L;
import com.sean.ds.fail.FailStrategy;
import com.sean.ds.pool.AvroClient;
import com.sean.ds.pool.ClientPool;
import com.sean.ds.route.RouteStrategy;
import com.sean.log.core.LogFactory;

/**
 * 服务代理
 * @author sean
 */
public final class ServiceProxy<E> implements InvocationHandler
{
	private static final Logger logger = LogFactory.getLogger(L.Ds);

	private RouteStrategy routeStrategy;
	private FailStrategy failStrategy;
	private List<ServiceInstance> instances;
	private ServiceDefine serviceDefine;

	private static final ExecutorService exec = Executors.newFixedThreadPool(5);

	public ServiceProxy(ServiceDefine serviceDefine, List<ServiceInstance> instances, RouteStrategy routeStrategy, FailStrategy failStrategy)
	{
		this.serviceDefine = serviceDefine;
		this.instances = instances;
		this.routeStrategy = routeStrategy;
		this.failStrategy = failStrategy;
	}

	@Override
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
	{
		if (method.getName().equals("toString"))
		{
			return method.invoke(proxy, args);
		}
		FutureTask<Object> task = new FutureTask<>(new Callable<Object>()
		{
			@Override
			public Object call() throws Exception
			{
				// 根据路由策略选择服务实例
				ServiceInstance instance = routeStrategy.getInstance(instances);
				if (instance != null)
				{
					// 初始化线程上下文
					failStrategy.initThreadContext();
					try
					{
						while (true)
						{
							// 从实例客户端连接池中请求连接
							ClientPool<E> pool = instance.getClientPool();
							AvroClient<E> rs = null;
							long curr = 0L;
							try
							{
								rs = pool.openClient();
								curr = System.currentTimeMillis();
								logger.debug("请求" + serviceDefine.serviceName + "服务实例" + instance + ", 请求方法:" + method.getName() + "， 请求参数:"
										+ Arrays.toString(args));
								Object result = method.invoke(rs.proxy, args);
								curr = System.currentTimeMillis() - curr;
								logger.debug("调用时间:" + curr + "毫秒, 请求结果:" + result);
								return result;
							}
							catch (Exception e)
							{
								Throwable excpt = e.getCause();
								// 业务异常
								if (SpecificExceptionBase.class.isAssignableFrom(excpt.getClass()))
								{
									curr = System.currentTimeMillis() - curr;
									logger.debug("调用时间:" + curr + ", 业务异常: " + excpt);
									return excpt;
								}
								// 其他异常视为通信异常
								else
								{
									logger.error("ds服务通信异常", e);

									// 失败转移
									instance = failStrategy.failover(serviceDefine, instance, instances, e);
									if (instance != null)
									{
										continue;
									}
									// 没有在线服务实例
									else
									{
										break;
									}
								}
							}
							finally
							{
								if (rs != null)
								{
									// 归还连接
									pool.returnClient(rs);
								}
							}
						}
					}
					catch (Throwable e)
					{
						return e;
					}
					finally
					{
						// 清理线程上下文
						failStrategy.cleanThreadContext();
					}
				}
				return new RuntimeException("服务" + serviceDefine.serviceName + "没有在线的服务实例存在");
			}
		});
		exec.execute(task);
		Object rs = task.get();

		// 业务异常或者没有服务实例
		if (rs != null && Throwable.class.isAssignableFrom(rs.getClass()))
		{
			throw (Throwable) rs;
		}
		return rs;
	}
}
