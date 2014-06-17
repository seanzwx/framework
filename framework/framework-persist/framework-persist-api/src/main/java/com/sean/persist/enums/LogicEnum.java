package com.sean.persist.enums;

/**
 * 逻辑关系
 * @author Sean
 *
 */
public enum LogicEnum
{
	/**
	 * 且
	 */
	And("and", null),
	/**
	 * 或
	 */
	Or("or", "$or");

	private String rdbVal;
	private String mongoVal;

	LogicEnum(String rdbVal, String mongoVal)
	{
		this.rdbVal = rdbVal;
		this.mongoVal = mongoVal;
	}

	public String getRdbValue()
	{
		return rdbVal;
	}

	public String getMongoValue()
	{
		return mongoVal;
	}
}
