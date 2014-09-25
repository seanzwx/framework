package com.sean.service.core;

import com.sean.service.entity.InterceptorEntity;

/**
 * 拦截器接口，只拦截Action请求
 * @author Sean
 *
 */
public abstract class Interceptor
{	
	private InterceptorEntity interceptorEntity;
	private Interceptor nextInterceptor;
	private boolean isInit = false;
	private boolean isSetNext = false;
	
	/**
	 * 拦截器执行方法
	 * @param session				请求会话
	 * @return						通过返回true，不同过返回false
	 */
	public abstract boolean intercept(Session session);

	/**
	 * 初始化拦截器
	 * @param name
	 * @param index
	 */
	protected void init(InterceptorEntity interceptorEntity)
	{
		if (!isInit)
		{
			isInit = true;
			this.interceptorEntity = interceptorEntity;
		}
	}
	
	/**
	 * 设置下一个拦截器
	 * @param nextInterceptor
	 */
	public void setNext(Interceptor nextInterceptor)
	{
		if (!isSetNext)
		{
			isSetNext = true;
			this.nextInterceptor = nextInterceptor;
		}
	}
	
	/**
	 * 读取拦截器实体
	 * @return
	 */
	public InterceptorEntity getInterceptorEntity()
	{
		return this.interceptorEntity;
	}
	
	/**
	 * 下一个拦截器
	 * @param session
	 * @return
	 */
	public boolean nextChain(Session session)
	{
		// 是否到最后一个拦截器
		if (this.nextInterceptor == null)
		{
			return true;
		}
		// 否则，继续下一个拦截器
		else
		{
			return this.nextInterceptor.intercept(session);
		}
	}
}
