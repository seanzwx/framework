package com.sean.service.worker;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import com.sean.service.core.Action;
import com.sean.service.core.ApplicationContext;
import com.sean.service.core.Session;

/**
 * 事务工作节点
 * @author sean
 */
public class TransactionWorker implements Worker
{
	private Worker nextWorker;

	public TransactionWorker(Worker nextWorker)
	{
		this.nextWorker = nextWorker;
	}

	@Override
	public void work(Session session, Action action) throws Exception
	{
		// 标记为可以提交事务
		boolean canCommit = true;
		UserTransaction transaction = null;
		try
		{
			String jndi = ApplicationContext.CTX.getApplicationContextEntity().getAppServer().getUserTranaction();
			InitialContext ctx = new InitialContext();
			transaction = (UserTransaction) ctx.lookup(jndi);
			transaction.begin();
			this.nextWorker.work(session, action);
		}
		catch (Exception e)
		{
			transaction.rollback();
			// 回滚后不可再提交事务
			canCommit = false;
			throw e;
		}
		finally
		{
			// 提交事务
			if (canCommit)
			{
				transaction.commit();
			}
		}
	}
}
