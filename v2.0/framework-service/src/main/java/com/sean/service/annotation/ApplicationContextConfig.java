package com.sean.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sean.common.enums.AppServerType;

/**
 * application context config
 * @author sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApplicationContextConfig
{
	/**
	 * the url pattern that framework will intercept, default by /*
	 */
	String urlPattern() default "/*";

	/**
	 * application server type
	 */
	AppServerType appServer();
}
