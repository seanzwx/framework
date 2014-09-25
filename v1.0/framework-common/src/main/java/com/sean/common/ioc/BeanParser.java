package com.sean.common.ioc;

/**
 * Bean解析器
 * @author sean
 */
public class BeanParser
{
	public BeanEntity parse(Class<?> cls)
	{
		BeanEntity be = new BeanEntity(cls);
		return be;
	}
}
