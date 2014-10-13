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
			// 验证接口密码
			String actPwd = action.getActionEntity().getPassword();
			String pwd = session.getParameter("password");
			if (actPwd != null && !actPwd.isEmpty())
			{
				// 密码不正确
				if (pwd == null || !pwd.equals(actPwd))
				{
					session.denied("access denied for password error");
					return;
				}
			}
			
			this.nextWorker.work(session, action);
		}
	}
}
