package com.sean.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * permission provider config, this is used to generate permission menu
 * @author sean
 */
@Documented 
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PermissionProviderConfig
{	
	/**
	 * description
	 */
	String description();
}
