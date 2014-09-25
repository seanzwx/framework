package com.sean.service.entity;

/**
 * Interceptor实体
 * @author sean
 *
 */
public class InterceptorEntity
{
	private int index;
	private Class<?> cls;

	public InterceptorEntity(int index, Class<?> cls)
	{
		this.index = index;
		this.cls = cls;
	}

	public int getIndex()
	{
		return index;
	}

	public Class<?> getCls()
	{
		return cls;
	}

}
