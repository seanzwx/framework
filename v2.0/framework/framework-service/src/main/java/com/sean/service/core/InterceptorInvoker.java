package com.sean.service.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.sean.log.core.LogFactory;
import com.sean.service.entity.InterceptorEntity;
import com.sean.service.enums.L;
import com.sean.service.parser.InterceptorParser;

/**
 * 拦截器调度器
 * 
 * @author Sean
 * 
 */
public final class InterceptorInvoker
{
	private Interceptor chain;
	private static Logger logger = LogFactory.getLogger(L.Service);

	/**
	 * 初始化拦截器
	 * @param cls
	 */
	public InterceptorInvoker(List<Class<?>> cls) throws Exception
	{
		logger.info("InterceptorInvoker start initializing...");

		InterceptorParser parser = new InterceptorParser();
		List<Interceptor> interceptors = new ArrayList<Interceptor>();

		for (int i = 0; i < cls.size(); i++)
		{
			InterceptorEntity entity = parser.parse(cls.get(i));
			Interceptor itp = (Interceptor) entity.getCls().newInstance();
			itp.init(entity);
			interceptors.add(itp);

			logger.info("InterceptorInvoker load interceptor " + entity.getCls().getName() + " successfully,index is " + entity.getIndex());
		}
		// 排序
		Collections.sort(interceptors, new Comparator<Interceptor>()
		{
			public int compare(Interceptor o1, Interceptor o2)
			{
				if (o1.getInterceptorEntity().getIndex() > o2.getInterceptorEntity().getIndex())
				{
					return 1;
				}
				return 0;
			}
		});

		// 生成拦截器链
		Interceptor tmp = null;
		for (int i = 0; i < interceptors.size(); i++)
		{
			if (i == 0)
			{
				chain = interceptors.get(i);
				tmp = chain;
			}
			else
			{
				tmp.setNext(interceptors.get(i));
				tmp = interceptors.get(i);
			}
		}

		logger.info("All interceptors has been initialized successfully");
	}

	/**
	 * 执行拦截器
	 * @param session			请求会话
	 * @return					
	 */
	public boolean intercept(Session session)
	{
		// 如果没有拦截器
		if (chain == null)
		{
			return true;
		}
		return chain.intercept(session);
	}
}
