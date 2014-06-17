package com.sean.service.core;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sean.config.core.Config;
import com.sean.config.enums.ConfigEnum;
import com.sean.log.core.LogFactory;
import com.sean.service.entity.ApplicationContextEntity;
import com.sean.service.enums.L;

/**
 * 抽象上下文
 * @author sean
 */
public class ApplicationContextBuilder
{
	protected static Logger logger = LogFactory.getLogger(L.Service);

	/**
	 * 构建上下文
	 * @param cls
	 * @param ctx
	 */
	public void build(Map<String, List<Class<?>>> cls, ApplicationContextEntity ctx) throws Exception
	{
		long launchTime = System.currentTimeMillis();

		logger.info("ApplicationContext " + ctx.getCls().getName() + " start initializing...");

		// 创建用户接口对象
		FrameworkSpi userInterface = (FrameworkSpi) ctx.getCls().newInstance();

		// 初始化拦截器
		InterceptorInvoker itpInvoker = new InterceptorInvoker(cls.get("interceptors"));

		// 初始化权限管理
		PermissionProviderManager permissionProviderManager = new PermissionProviderManager(cls.get("permissionProviders"));

		// 初始化action容器
		ActionContainer actionContainer = new ActionContainer(cls.get("actions"), cls.get("parameterProviders"), cls.get("returnParameterProviders"),
				userInterface, itpInvoker);

		// 创建用户上下文
		ApplicationContext.CTX = new ApplicationContext(ctx, launchTime);
		// 创建框架上下文
		FrameworkContext.CTX = new FrameworkContext(ctx, actionContainer, itpInvoker, permissionProviderManager, userInterface, launchTime);

		// 上下文初始化完毕
		String msg = "ApplicationContext " + ctx.getCls().getName() + " initialized successfully";
		msg += ", RunningMode is " + Config.getProperty(ConfigEnum.ServiceMode) + ", AppServer is " + ctx.getAppServer();
		logger.info(msg);
	}
}
