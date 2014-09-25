package com.sean.service.entity;

/**
 * 上下文监听器实体
 * @author sean
 *
 */
public class ApplicationContextListenerEntity
{
	private int initializedIndex;
	private int destroyedIndex;
	private Class<?> cls;

	public ApplicationContextListenerEntity(int initializedIndex, int destroyedIndex, Class<?> cls)
	{
		this.initializedIndex = initializedIndex;
		this.destroyedIndex = destroyedIndex;
		this.cls = cls;
	}

	public int getInitializedIndex()
	{
		return initializedIndex;
	}

	public int getDestroyedIndex()
	{
		return destroyedIndex;
	}

	public Class<?> getCls()
	{
		return cls;
	}

}
