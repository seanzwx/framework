package com.sean.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * application context listener config
 * @author sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApplicationContextListenerConfig
{
	/**
	 * invoke sequence when context initialize
	 */
	int initializedIndex();

	/**
	 * invoke sequence when context destory
	 */
	int destroyedIndex();

	/**
	 * description
	 */
	String description();
}
