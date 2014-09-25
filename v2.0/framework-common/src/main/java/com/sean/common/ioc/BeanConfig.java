package com.sean.common.ioc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * bean config
 * @author sean
 */
@Documented 
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BeanConfig
{	
	/**
	 * description
	 */
	String value();
}
