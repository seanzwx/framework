package com.sean.persist.enums;

/**
 * 排序
 * @author Sean
 *
 */
public enum OrderEnum
{
	/**
	 * 升序
	 */
	Asc("asc", 1),
	/**
	 * 降序
	 */
	Desc("desc", -1);

	private String rdbVal;
	private int mongoVal;

	OrderEnum(String rdbVal, int mongoVal)
	{
		this.rdbVal = rdbVal;
		this.mongoVal = mongoVal;
	}

	public String getRdbValue()
	{
		return rdbVal;
	}

	public int getMongoValue()
	{
		return mongoVal;
	}
}
