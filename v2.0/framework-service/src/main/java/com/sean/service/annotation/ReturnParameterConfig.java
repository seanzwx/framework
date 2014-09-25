package com.sean.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sean.persist.core.Entity;
import com.sean.service.enums.Format;

/**
 * return parameter
 * @author sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ReturnParameterConfig
{
	/**
	 * parameter format
	 */
	Format format();

	/**
	 * class of entity this is aimed at when format is Entity or EntityList
	 */
	Class<? extends Entity> entity() default Entity.class;

	/**
	 * description
	 */
	String descr();
}
