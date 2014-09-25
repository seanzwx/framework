package com.sean.service.core;

import org.apache.log4j.Logger;

import com.sean.log.core.LogFactory;
import com.sean.service.entity.ApplicationContextEntity;
import com.sean.service.enums.L;

/**
 * application context
 * @author sean
 */
public final class ApplicationContext
{
	public static ApplicationContext CTX;

	private ApplicationContextEntity entity;
	private long launchTime;

	protected static Logger logger = LogFactory.getLogger(L.Service);

	public ApplicationContext(ApplicationContextEntity entity, long launchTime)
	{
		this.entity = entity;
		this.launchTime = launchTime;
	}

	/**
	 * 读取系统启动时间
	 */
	public long getLaunchTime()
	{
		return this.launchTime;
	}

	/**
	 * 获取上下文实体
	 * @return
	 */
	public ApplicationContextEntity getApplicationContextEntity()
	{
		return entity;
	}
}
