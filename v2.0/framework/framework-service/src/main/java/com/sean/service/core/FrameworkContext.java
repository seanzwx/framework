package com.sean.service.core;

import java.util.List;

import org.apache.log4j.Logger;

import com.sean.log.core.LogFactory;
import com.sean.service.entity.ApplicationContextEntity;
import com.sean.service.enums.L;

/**
 * 框架上下文
 * @author sean
 */
public final class FrameworkContext
{
	public static FrameworkContext CTX;

	private ApplicationContextEntity context;
	private ActionContainer actionContainer;
	private InterceptorInvoker itpInvoker;
	private PermissionProviderManager permissionProviderManager;
	private FrameworkSpi userInterface;
	private long launchTime;// 启动服务器时间

	protected static Logger logger = LogFactory.getLogger(L.Service);

	protected FrameworkContext(ApplicationContextEntity context, ActionContainer actionContainer, InterceptorInvoker itpInvoker,
			PermissionProviderManager permissionProviderManager, FrameworkSpi userInterface, long launchTime)
	{
		this.context = context;
		this.actionContainer = actionContainer;
		this.itpInvoker = itpInvoker;
		this.permissionProviderManager = permissionProviderManager;
		this.userInterface = userInterface;
		this.launchTime = launchTime;
	}

	/**
	 * 获取Action
	 * @param actName
	 * @return
	 */
	protected Action getAction(String actVersion, String actName)
	{
		return this.actionContainer.getAction(actVersion, actName);
	}

	/**
	 * 获取全部Action
	 * @return
	 */
	protected List<Action> getAllActions()
	{
		return this.actionContainer.getAllActions();
	}

	/**
	 * 执行拦截器
	 * @param request
	 * @param response
	 * @return					全部通过返回true，否则返回false
	 */
	protected boolean intercept(Session session)
	{
		return this.itpInvoker.intercept(session);
	}

	/**
	 * 获取拦截器调度器
	 * @return
	 */
	protected InterceptorInvoker getInterceptorInvoker()
	{
		return this.itpInvoker;
	}

	/**
	 * 获取权限管理类
	 * @return
	 */
	protected PermissionProviderManager getPermissionProviderManager()
	{
		return permissionProviderManager;
	}

	/**
	 * 获取启动时间
	 * @return
	 */
	protected long getLaunchTime()
	{
		return launchTime;
	}

	/**
	 * 读取上下文配置信息
	 * @return
	 */
	protected ApplicationContextEntity getApplicationContextEntity()
	{
		return this.context;
	}

	/**
	 * 获取用户接口
	 * @return
	 */
	protected FrameworkSpi getUserInterface()
	{
		return userInterface;
	}

}
