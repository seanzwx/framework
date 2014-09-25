package com.sean.service.enums;

/**
 * data type enum
 * @author sean
 */
public enum DataType
{
	/**
	 * int
	 */
	Int(0),
	/**
	 * long
	 */
	Long(1),
	/**
	 * float
	 */
	Float(2),
	/**
	 * string
	 */
	String(3),
	/**
	 * yyyy-MM-dd date
	 */
	YYYYMMDDDate(4),
	/**
	 * yy-MM-dd hh:mm:ss date
	 */
	YYYYMMDDHHMMSSDate(5),
	/**
	 * 枚举
	 */
	Enum(6);

	private int code;

	DataType(int code)
	{
		this.code = code;
	}

	public int getValue()
	{
		return code;
	}
}
