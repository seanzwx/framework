package com.sean.service.parser;

import com.sean.service.annotation.InterceptorConfig;
import com.sean.service.entity.InterceptorEntity;

/**
 * Interceptor解析器
 * @author sean
 *
 */
public class InterceptorParser
{
	public InterceptorEntity parse(Class<?> cls)
	{
		InterceptorConfig ic = cls.getAnnotation(InterceptorConfig.class);
		InterceptorEntity ie = new InterceptorEntity(ic.index(), cls);
		return ie;
	}
}
