package com.sean.service.core;

import javax.servlet.ServletContextEvent;

import com.sean.config.core.Config;
import com.sean.persist.core.PersistLaucher;

/**
 * 单元测试上下文
 * @author Sean
 */
public final class TestContext extends AbstractLauncher
{
	private Tester tester = new Tester(this);

	public TestContext()
	{
		try
		{
			// 标记为单元测试
			PersistLaucher.isUnitTest = true;
			Config.readConfiguration();
			this.build(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * 设置登录用户
	 * @param userId					用户id
	 */
	public void setLoginUser(long userId)
	{
		tester.setLoginUser(userId);
	}

	/**
	 * 设置Session属性
	 * @param name
	 * @param val
	 */
	public void setSessionAttribute(String name, Object val)
	{
		this.tester.setSessionAttribute(name, val);
	}

	/**
	 * 获取测试器
	 * @return
	 */
	public Tester getTester()
	{
		return tester;
	}

	/**
	 * 运行结束销毁上下文
	 * @param ctxe
	 * @param appCtx
	 */
	protected void destoryed(ServletContextEvent ctxe)
	{
		destroyedListrner(ctxe);
	}

	@Override
	protected ApplicationContextBuilder getBuildContext()
	{
		return new ApplicationContextBuilder();
	}
}
