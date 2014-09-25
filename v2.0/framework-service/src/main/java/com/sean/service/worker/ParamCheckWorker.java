package com.sean.service.worker;

import com.sean.service.core.Action;
import com.sean.service.core.RequestChecker;
import com.sean.service.core.Session;

/**
 * 参数验证工作节点
 * @author sean
 */
public class ParamCheckWorker implements Worker
{
	private RequestChecker checker;
	private Worker nextWorker;
	
	public ParamCheckWorker(Worker nextWorker)
	{
		this.nextWorker = nextWorker;
		checker = new RequestChecker();
	}
	
	@Override
	public void work(Session session, Action action) throws Exception
	{		
		// 验证参数合法性
		if (action.checkParams(session, checker))
		{
			this.nextWorker.work(session, action);
		}
	}
}
