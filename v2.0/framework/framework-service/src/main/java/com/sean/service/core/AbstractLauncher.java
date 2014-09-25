package com.sean.service.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;

import com.sean.common.ioc.BeanFactory;
import com.sean.config.core.Config;
import com.sean.config.enums.ConfigEnum;
import com.sean.log.core.LogFactory;
import com.sean.service.entity.ApplicationContextEntity;
import com.sean.service.entity.ApplicationContextListenerEntity;
import com.sean.service.enums.L;
import com.sean.service.parser.ApplicationContextListenerParser;
import com.sean.service.parser.ApplicationContextParser;

/**
 * 抽象启动类
 * @author sean
 */
public abstract class AbstractLauncher
{
	protected List<ApplicationContextListener> listeners;

	/**
	 * 构建框架
	 * @param ctxe
	 */
	protected ApplicationContextEntity build(ServletContextEvent ctxe) throws Exception
	{
		Logger logger = LogFactory.getLogger(L.Service);
		long current = System.currentTimeMillis();

		String pkgs = Config.getProperty(ConfigEnum.ServicePackageProfix);
		String[] packages = pkgs.split(",");

		// 初始化beanfactory
		BeanFactory.init(packages);

		// 扫描所有框架内置元素
		ClassCollector cc = new ClassCollector(packages);
		Map<String, List<Class<?>>> cls = cc.collect();

		// 创建Context
		List<Class<?>> context = cls.get("context");
		// 如果Context未定义
		if (context.size() <= 0)
		{
			throw new RuntimeException("application context not found");
		}
		else
		{
			// 加载Listener
			ApplicationContextListenerParser parser = new ApplicationContextListenerParser();
			List<Class<?>> liscls = cls.get("listeners");
			listeners = new ArrayList<ApplicationContextListener>(liscls.size());
			for (int i = 0; i < liscls.size(); i++)
			{
				ApplicationContextListenerEntity cle = parser.parse(liscls.get(i));
				ApplicationContextListener listener = (ApplicationContextListener) cle.getCls().newInstance();
				listener.init(cle);
				listeners.add(listener);
			}

			// 执行预监听
			this.preInitializedListrner(ctxe);

			// 开始构建上下文
			ApplicationContextEntity entity = new ApplicationContextParser().parse(context.get(0));
			ApplicationContextBuilder ctx = this.getBuildContext();
			ctx.build(cls, entity);

			// 执行监听器
			this.initializedListrner(ctxe);

			// // 开发模式下开启开发文档服务,监听10003端口
			// if (ServiceConfig.RunningMode_Develop)
			// {
			// docServer = new NettyServer(new
			// SpecificResponder(DocManager.class, new DocManagerRemote()), new
			// InetSocketAddress(10003));
			// docServer.start();
			// }

			logger.info("started in " + (System.currentTimeMillis() - current) + " milliseconds");

			StringBuilder sb = new StringBuilder();
			String projectName = Config.getProperty(ConfigEnum.ProjectName);
			sb.append("\n***********************************************************\n");
			sb.append("*               " + projectName + " started... \n");
			sb.append("***********************************************************");
			logger.info(sb.toString());

			return entity;
		}
	}

	/**
	 * 获取要构建的上下文
	 * @return
	 */
	protected abstract ApplicationContextBuilder getBuildContext();

	/**
	 * 执行预初始化监听器
	 * @param ctxe
	 */
	protected void preInitializedListrner(ServletContextEvent ctxe)
	{
		Collections.sort(this.listeners, new Comparator<ApplicationContextListener>()
		{
			@Override
			public int compare(ApplicationContextListener o1, ApplicationContextListener o2)
			{
				if (o1.getContextListenerEntity().getInitializedIndex() > o2.getContextListenerEntity().getInitializedIndex())
				{
					return 1;
				}
				return 0;
			}
		});
		for (int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).contextPreInitialized(ctxe);
		}
	}

	/**
	 * 执行初始化监听器
	 * @param ctxe
	 */
	protected void initializedListrner(ServletContextEvent ctxe)
	{
		Collections.sort(this.listeners, new Comparator<ApplicationContextListener>()
		{
			@Override
			public int compare(ApplicationContextListener o1, ApplicationContextListener o2)
			{
				if (o1.getContextListenerEntity().getInitializedIndex() > o2.getContextListenerEntity().getInitializedIndex())
				{
					return 1;
				}
				return 0;
			}
		});
		for (int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).contextInitialized(ctxe);
		}
	}

	/**
	 * 执行销毁监听器
	 * @param ctxe
	 */
	protected void destroyedListrner(ServletContextEvent ctxe)
	{
		Collections.sort(this.listeners, new Comparator<ApplicationContextListener>()
		{
			@Override
			public int compare(ApplicationContextListener o1, ApplicationContextListener o2)
			{
				if (o1.getContextListenerEntity().getDestroyedIndex() > o2.getContextListenerEntity().getDestroyedIndex())
				{
					return 1;
				}
				return 0;
			}
		});
		for (int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).contextDestroyed(ctxe);
		}
	}

	public static void showStartInfo()
	{
		Logger logger = LogFactory.getLogger(L.Service);

		StringBuilder sb = new StringBuilder();
		String projectName = Config.getProperty(ConfigEnum.ProjectName);
		sb.append("\n***********************************************************\n");
		sb.append("*               " + projectName + " starting...\n");
		sb.append("***********************************************************");
		logger.info(sb.toString());
	}
}
