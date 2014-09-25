package com.sean.persist.dictionary;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * dictionary key config
 * @author sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(DictionaryKeyArrayConfig.class)
public @interface DictionaryKeyConfig
{
	/**
	 * key
	 */
	String key();

	/**
	 * description
	 */
	String descr();
}
