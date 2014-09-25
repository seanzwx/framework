package com.sean.common.ioc;

import java.util.LinkedList;
import java.util.List;

import com.sean.common.util.ClassScanner;

/**
 * bean工厂
 * @author sean
 */
public class BeanFactory
{
	private static boolean inited = false;
	private static BeanContainer container;

	public static void init(String[] packageNames) throws Exception
	{
		if (!inited)
		{
			// 扫描类
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			ClassScanner scanner = new ClassScanner();
			List<String> clsStrs = scanner.findClass(loader, packageNames);
			List<Class<?>> beanCls = new LinkedList<>();
			for (String it : clsStrs)
			{
				Class<?> cls = loader.loadClass(it);
				if (cls.getAnnotation(BeanConfig.class) != null)
				{
					beanCls.add(cls);
				}
			}

			container = new BeanContainer(beanCls);
			inited = true;
		}
	}

	public static <E> E getBean(Class<E> beanClass)
	{
		return container.getBean(beanClass.getName());
	}
}
