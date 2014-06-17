package com.sean.common.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sean.common.enums.L;
import com.sean.log.core.LogFactory;

/**
 * Bean容器
 * @author Sean
 */
@SuppressWarnings("unchecked")
public final class BeanContainer
{
	private Map<String, Object> beans;
	private static final Logger logger = LogFactory.getLogger(L.Common);

	public BeanContainer(List<Class<?>> classes) throws Exception
	{
		// 日志
		logger.info("BeanContainer start initializing...");

		this.beans = new HashMap<String, Object>(16, 1);
		BeanParser parser = new BeanParser();

		// 检查重复定义
		List<String> beanNames = new ArrayList<String>();
		for (int i = 0; i < classes.size(); i++)
		{
			BeanEntity be = parser.parse(classes.get(i));
			// 如果已经定义
			if (beanNames.contains(be.getCls().getName()))
			{
				throw new RuntimeException("the bean " + be.getCls().getName() + " defined repeat, try in other class name");
			}
			else
			{
				beanNames.add(be.getCls().getName());
			}
		}

		// 开始初始化
		for (int i = 0; i < classes.size(); i++)
		{
			BeanEntity be = parser.parse(classes.get(i));
			createBean(be);
		}
		// 开始注入resource
		for (String beanName : beans.keySet())
		{
			this.inject(beanName);
		}
		logger.info("BeanContainer initailized successfully");
	}

	/**
	 * 获取Bean
	 * @param beanName
	 * @return
	 */
	public <E> E getBean(String beanName)
	{
		Object o = this.beans.get(beanName);
		if (o == null)
		{
			return null;
		}
		return (E) o;
	}

	/**
	 * 创建bean
	 */
	private void createBean(BeanEntity be) throws Exception
	{
		// 如果bean容器还没该bean，则创建
		Object bean = this.beans.get(be.getCls().getName());
		if (bean == null)
		{
			// 创建对象，以类名和所有接口名为key
			bean = be.getCls().newInstance();
			Class<?>[] inters = be.getCls().getInterfaces();
			for (int i = 0; i < inters.length; i++)
			{
				this.beans.put(inters[i].getName(), bean);
				// 打印创建日志
				logger.debug("create bean " + be.getCls().getName() + " to BeanContainer named " + inters[i].getName());
			}
			this.beans.put(be.getCls().getName(), bean);
			// 打印创建日志
			logger.debug("create bean " + be.getCls().getName() + " to BeanContainer named " + be.getCls().getName());
		}
	}

	/**
	 * 注入resource
	 */
	private void inject(String beanName)
	{
		// 检查所有属性是否有ResourceConfig依赖注入
		Object bean = beans.get(beanName);
		Field[] fs = bean.getClass().getDeclaredFields();
		Field f = null;
		for (int i = 0; i < fs.length; i++)
		{
			f = fs[i];
			// 跳过static和final属性
			if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()))
			{
				ResourceConfig rc = f.getAnnotation(ResourceConfig.class);
				// 如果有依赖注入
				if (rc != null)
				{
					Class<?> cls = f.getType();
					Object res = beans.get(cls.getName());
					if (res == null)
					{
						throw new RuntimeException("the @ResourceConfig defined over field " + f.getName() + " in class " + bean.getClass().getName()
								+ " have not found any implementation bean");
					}
					f.setAccessible(true);
					try
					{
						f.set(bean, res);
						logger.debug("inject " + res.getClass().getName() + " into field " + f.getName() + " in the bean " + beanName);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
}
