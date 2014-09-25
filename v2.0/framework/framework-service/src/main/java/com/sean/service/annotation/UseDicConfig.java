package com.sean.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * use dic
 * @author sean
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UseDicConfig
{
	/**
	 * search field,this is the key to search in dic
	 */
	String[] field();

	/**
	 * dic name
	 */
	Class<?>[] dic();
}
