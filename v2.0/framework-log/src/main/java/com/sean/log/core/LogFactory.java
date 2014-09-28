package com.sean.log.core;

import org.apache.log4j.Logger;

/**
 * logger factory
 * @author sean
 */
public class LogFactory
{
	public static synchronized Logger getLogger(String name)
	{
		return Logger.getLogger(name);
	}
	
	public static synchronized Logger getLogger(Class<?> clazz)
	{
		return Logger.getLogger(clazz.getName());
	}
}
