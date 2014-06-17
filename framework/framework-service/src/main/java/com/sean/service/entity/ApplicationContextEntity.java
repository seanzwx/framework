package com.sean.service.entity;

import com.sean.common.enums.AppServerType;

/**
 * 上下文实体
 * 
 * @author sean
 * 
 */
public class ApplicationContextEntity
{
	private String urlPattern;
	private AppServerType appServer;
	private Class<?> cls;

	public ApplicationContextEntity(String urlPattern, AppServerType appServer, Class<?> cls)
	{
		this.urlPattern = urlPattern;
		this.appServer = appServer;
		this.cls = cls;
	}

	public String getUrlPattern()
	{
		return urlPattern;
	}

	public AppServerType getAppServer()
	{
		return appServer;
	}

	public Class<?> getCls()
	{
		return cls;
	}

}
