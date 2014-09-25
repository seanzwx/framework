package com.sean.log.test;

import org.apache.log4j.Logger;

import com.sean.log.core.LogFactory;

public class MyApp
{
	private static Logger logger1 = LogFactory.getLogger("logger1");
	private static Logger logger2 = LogFactory.getLogger("logger2");
	
	public static void main(String[] args)
	{
		logger1.debug("debug");
		logger1.info("info");
		logger1.warn("warn");
		logger1.error("error");
		logger1.fatal("fatal");
		
		logger2.debug("debug");
		logger2.info("info");
		logger2.warn("warn");
		logger2.error("error");
		logger2.fatal("fatal");
	}
}