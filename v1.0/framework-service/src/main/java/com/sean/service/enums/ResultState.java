package com.sean.service.enums;

/**
 * request result state enum
 * @author sean
 */
public enum ResultState
{
	/***
	 * success
	 */
	Success("Success"),

	/**
	 * unlogin
	 */
	Unlogin("Unlogin"),

	/**
	 * parameter invalid
	 */
	Invalid("Invalid"),

	/**
	 * access denied
	 */
	Denied("Denied"),

	/**
	 * system exception
	 */
	Exception("Exception"),
	
	BusinessException("BusinessException");

	private String code;

	ResultState(String code)
	{
		this.code = code;
	}

	@Override
	public String toString()
	{
		return code;
	}
}
