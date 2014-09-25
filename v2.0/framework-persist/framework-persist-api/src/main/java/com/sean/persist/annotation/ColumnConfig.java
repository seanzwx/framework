package com.sean.persist.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * column config
 * @author sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ColumnConfig
{
	/**
	 * column name, default by entity field name
	 */
	String column() default "";

	/**
	 * is a primary key , default by false, must be defined only one in a single entity
	 */
	boolean primaryKey() default false;

	/**
	 * sequence name, which is use by oracle
	 */
	String sequenceName() default "";

	/**
	 * description
	 */
	String description();
}
