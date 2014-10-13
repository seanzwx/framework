package com.sean.service.worker;

import com.sean.service.core.Action;
import com.sean.service.core.FrameworkSpi;
import com.sean.service.core.Session;

/**
 * 权限验证工作节点
 * @author sean
 */
public class PermissionWorker implements Worker
{
	private FrameworkSpi userInterface;
	private Worker nextWorker;

	public PermissionWorker(FrameworkSpi userInterface, Worker nextWorker)
	{
		this.userInterface = userInterface;
		this.nextWorker = nextWorker;
	}

	@Override
	public void work(Session session, Action action) throws Exception
	{
		if (!userInterface.checkPermission(session, action.getActionEntity().getPermission()))
		{
			session.denied("access denied for none permission");
		}
		else
		{
			this.nextWorker.work(session, action);
		}
	}
}
