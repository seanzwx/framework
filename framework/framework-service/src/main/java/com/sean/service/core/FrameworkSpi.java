package com.sean.service.core;

import com.sean.service.entity.ActionEntity;

/**
 * 框架spi 
 * @author sean
 */
public abstract class FrameworkSpi
{
	/**
	 * global exception handle
	 * @param session
	 * @param action
	 * @param e
	 */
	public abstract void exceptionHandle(Session session, ActionEntity action, Exception e);

	/**
	 * check permission
	 * @param session					request session
	 * @param permissionId				permission id
	 * @return
	 */
	public abstract boolean checkPermission(Session session, int permissionId);
	
	/**
	 * 获取会话私钥
	 * @param sid
	 * @return
	 */
	public abstract String getEncryptKey(String sid);
	
	/**
	 * 初始化用户上下文
	 * @param userId
	 */
	public abstract void initUserContext(long userId);
	
	/**
	 * 销毁用户上下文
	 */
	public abstract void destoryUserContext();

	/**
	 * execute before action run
	 * @param session							request session
	 * @param action							action entity
	 */
	public abstract void preAction(Session session, ActionEntity action);

	/**
	 * execute after action run finished
	 * @param session							request session
	 * @param action							action entity
	 * @param milliSeconds						the time that action spend					
	 */
	public abstract void afterAction(Session session, ActionEntity action, long millSeconds);
}
