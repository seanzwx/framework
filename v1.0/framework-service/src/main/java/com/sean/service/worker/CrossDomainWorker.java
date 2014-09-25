package com.sean.service.worker;

import com.sean.service.core.Action;
import com.sean.service.core.Session;

/**
 * 跨域工作节点
 * @author sean
 */
public class CrossDomainWorker implements Worker
{
	private Worker nextWorker;

	public CrossDomainWorker(Worker nextWorker)
	{
		this.nextWorker = nextWorker;
	}

	@Override
	public void work(Session session, Action action) throws Exception
	{
		String origin = session.getHeader("Origin");
		if (origin != null && !origin.isEmpty())
		{
			session.addHeader("Access-Control-Allow-Origin", origin);
		}
		this.nextWorker.work(session, action);
	}
}
