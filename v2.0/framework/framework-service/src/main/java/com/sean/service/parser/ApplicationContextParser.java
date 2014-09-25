package com.sean.service.parser;

import com.sean.service.annotation.ApplicationContextConfig;
import com.sean.service.entity.ApplicationContextEntity;

/**
 * ApplicationContext解析器
 * @author sean
 */
public class ApplicationContextParser
{
	public ApplicationContextEntity parse(Class<?> cls)
	{
		ApplicationContextConfig cc = cls.getAnnotation(ApplicationContextConfig.class);
		ApplicationContextEntity ce = new ApplicationContextEntity(cc.urlPattern(), cc.appServer(), cls);
		return ce;
	}
}
