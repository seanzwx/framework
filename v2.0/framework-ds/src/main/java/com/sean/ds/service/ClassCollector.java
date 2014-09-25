package com.sean.ds.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sean.common.util.ClassScanner;
import com.sean.ds.annotation.ServiceConfig;

/**
 * 类加载器
 * @author sean
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
	 * @return
	 */
	protected Map<String, List<Class<?>>> collect() throws Exception
	{
		List<Class<?>> services = new ArrayList<Class<?>>(5);
		Map<String, List<Class<?>>> classes = new HashMap<>();
		classes.put("services", services);
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
				if (cls.getAnnotation(ServiceConfig.class) != null)
				{
					classes.get("services").add(cls);
				}
			}
		}
	}
}
