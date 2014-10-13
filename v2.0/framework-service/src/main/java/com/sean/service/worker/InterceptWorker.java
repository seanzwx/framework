package com.sean.service.worker;

import com.sean.service.core.Action;
import com.sean.service.core.InterceptorInvoker;
import com.sean.service.core.Session;

/**
 * 拦截机工作节点
 * @author sean
 */
public class InterceptWorker implements Worker
{
	private InterceptorInvoker invoker;
	private Worker nextWorker;
	
	public InterceptWorker(InterceptorInvoker invoker, Worker nextWorker)
	{
		this.invoker = invoker;
		this.nextWorker = nextWorker;
	}
	
	@Override
	public void work(Session session, Action action) throws Exception
	{		
		if(this.invoker.intercept(session))
		{
			this.nextWorker.work(session, action);	
		}
		else
		{
			session.denied("access denied for intercept");
		}
	}
}
