package com.sean.service.worker;

import com.sean.service.core.Action;
import com.sean.service.core.FrameworkSpi;
import com.sean.service.core.Session;

/**
 * 请求处理工作节点
 * @author sean
 */
public class ActionWorker implements Worker
{
	private FrameworkSpi frameworkSpi;

	public ActionWorker(FrameworkSpi frameworkSpi)
	{
		this.frameworkSpi = frameworkSpi;
	}

	@Override
	public void work(Session session, Action action) throws Exception
	{
		frameworkSpi.preAction(session, action.getActionEntity());
		long curr = System.currentTimeMillis();
		
		action.execute(session);
		
		long time = System.currentTimeMillis() - curr;
		frameworkSpi.afterAction(session, action.getActionEntity(), time);
	}
}
