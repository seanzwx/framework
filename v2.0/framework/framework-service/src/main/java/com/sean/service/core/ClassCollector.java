package com.sean.service.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sean.common.util.ClassScanner;
import com.sean.service.annotation.ActionConfig;
import com.sean.service.annotation.ApplicationContextConfig;
import com.sean.service.annotation.ApplicationContextListenerConfig;
import com.sean.service.annotation.InterceptorConfig;
import com.sean.service.annotation.ParameterProviderConfig;
import com.sean.service.annotation.PermissionProviderConfig;
import com.sean.service.annotation.ReturnParameterProviderConfig;

/**
 * 类加载器
 * @author Sean
 */
public final class ClassCollector
{
	private String[] pkgPrefixs;

	/**
	 * 包名前缀
	 * @param pkgPrefix
	 */
	public ClassCollector(String[] pkgPrefixs)
	{
		this.pkgPrefixs = pkgPrefixs;
	}

	/**
	 * 扫描所有类
	 * 
	 * @return
	 */
	public Map<String, List<Class<?>>> collect() throws Exception
	{
		List<Class<?>> acts = new ArrayList<Class<?>>(50);
		List<Class<?>> ctxs = new ArrayList<Class<?>>(5);
		List<Class<?>> itps = new ArrayList<Class<?>>(10);
		List<Class<?>> listeners = new ArrayList<Class<?>>(5);
		List<Class<?>> permissionProviders = new ArrayList<Class<?>>(1);
		List<Class<?>> parameterProviders = new ArrayList<Class<?>>(10);
		List<Class<?>> returnParameterProviders = new ArrayList<Class<?>>(10);
		Map<String, List<Class<?>>> classes = new HashMap<String, List<Class<?>>>();
		classes.put("actions", acts);
		classes.put("context", ctxs);
		classes.put("interceptors", itps);
		classes.put("listeners", listeners);
		classes.put("permissionProviders", permissionProviders);
		classes.put("parameterProviders", parameterProviders);
		classes.put("returnParameterProviders", returnParameterProviders);

		this.list(classes);

		return classes;
	}

	/**
	 * 列出所有注解类
	 * @param classes
	 */
	private void list(Map<String, List<Class<?>>> classes) throws Exception
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ClassScanner scanner = new ClassScanner();
		List<String> clsStrs = scanner.findClass(loader, pkgPrefixs);
		int length = clsStrs.size();
		Class<?> cls = null;
		for (int i = 0; i < length; i++)
		{
			cls = loader.loadClass(clsStrs.get(i));

			if (cls != null)
			{
				// 如果是Action
				if (cls.getAnnotation(ActionConfig.class) != null)
				{
					if (cls.getSuperclass() == Action.class)
					{
						classes.get("actions").add(cls);
					}
					// Action定义不合法
					else
					{
						throw new RuntimeException("the action " + cls.getName() + " defined illegal,it must extends Action and comment ActionConfig");
					}
				}
				// 如果是上下文Context
				else if (cls.getAnnotation(ApplicationContextConfig.class) != null)
				{
					if (cls.getSuperclass() == FrameworkSpi.class)
					{
						classes.get("context").add(cls);
					}
					// Context定义不合法
					else
					{
						throw new RuntimeException("the applicationcontext " + cls.getName()
								+ " defined illegal,it must extends UserInterface and comment @ApplicationContextConfig");
					}
				}
				// 如果是拦截器Interceptor
				else if (cls.getAnnotation(InterceptorConfig.class) != null)
				{
					if (cls.getSuperclass() == Interceptor.class)
					{
						classes.get("interceptors").add(cls);
					}
					else
					{
						throw new RuntimeException("the interceptor " + cls.getName()
								+ " defined illegal,it must extends Interceptor and comment InterceptorConfig");
					}
				}
				// 如果是上下文监听器
				else if (cls.getAnnotation(ApplicationContextListenerConfig.class) != null)
				{
					if (cls.getSuperclass() == ApplicationContextListener.class)
					{
						classes.get("listeners").add(cls);
					}
					else
					{
						throw new RuntimeException("the applicationcontextlistener " + cls.getName()
								+ " defined illegal,it must extends ApplicationContextListener and comment ContextListenerConfig");
					}
				}
				// 如果是PermissionProvider
				else if (cls.getAnnotation(PermissionProviderConfig.class) != null)
				{
					if (cls.getSuperclass() == PermissionProvider.class)
					{
						classes.get("permissionProviders").add(cls);
					}
					else
					{
						throw new RuntimeException("the permission provider " + cls.getName()
								+ " defined illegal,it must extends PermissionProvider and comment PermissionProviderConfig");
					}
				}
				// 如果是ParameterProviderConfig
				else if (cls.getAnnotation(ParameterProviderConfig.class) != null)
				{
					classes.get("parameterProviders").add(cls);
				}
				// 如果是ReturnParameterProviderConfig
				else if (cls.getAnnotation(ReturnParameterProviderConfig.class) != null)
				{
					classes.get("returnParameterProviders").add(cls);
				}
			}
		}
	}
}
