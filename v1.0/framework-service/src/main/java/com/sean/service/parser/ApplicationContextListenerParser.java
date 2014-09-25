package com.sean.service.parser;

import com.sean.service.annotation.ApplicationContextListenerConfig;
import com.sean.service.entity.ApplicationContextListenerEntity;

/**
 * Listener解析器
 * @author sean
 *
 */
public class ApplicationContextListenerParser
{
	public ApplicationContextListenerEntity parse(Class<?> cls)
	{
		ApplicationContextListenerConfig blc = cls.getAnnotation(ApplicationContextListenerConfig.class);
		ApplicationContextListenerEntity ble = new ApplicationContextListenerEntity(blc.initializedIndex(), blc.destroyedIndex(), cls);
		return ble;
	}
}
