package com.sean.persist.dictionary;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * dictionary config
 * @author sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DictionaryConfig
{
	/**
	 * description
	 */
	String description();

	/**
	 * this define all fields that the dictionary include
	 */
	DictionaryKeyConfig[] keys();
}
