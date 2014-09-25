package com.sean.service.core;

import javax.servlet.ServletContextEvent;

import com.sean.service.entity.ApplicationContextListenerEntity;

/**
 * 应用程序上下文监听器
 * @author Sean
 */
public abstract class ApplicationContextListener
{
	private ApplicationContextListenerEntity contextListenerEntity;
	
	public void init(ApplicationContextListenerEntity contextListenerEntity)
	{
		if (this.contextListenerEntity == null)
		{
			this.contextListenerEntity = contextListenerEntity;
		}
	}
	
	public ApplicationContextListenerEntity getContextListenerEntity()
	{
		return this.contextListenerEntity;
	}
	
	/**
	 * 应用程序上下文初始化前调用
	 * @param ctxe
	 */
	public abstract void contextPreInitialized(ServletContextEvent ctxe);
	
	/**
	 * 应用程序上下文初始化后调用
	 * @param ctxe
	 */
	public abstract void contextInitialized(ServletContextEvent ctxe);
	
	/**
	 * 应用程序上下文销毁后调用
	 * @param ctx
	 */
	public abstract void contextDestroyed(ServletContextEvent ctx);
}
