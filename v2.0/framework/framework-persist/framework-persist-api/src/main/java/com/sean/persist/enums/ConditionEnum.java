package com.sean.persist.enums;

/**
 * 关系运算符
 * @author sean
 */
public enum ConditionEnum
{
	/**
	 * 等于
	 */
	Equal("=", null),
	/**
	 * 不等于
	 */
	Not_Equal("!=", "$not"),
	/**
	 * 大于
	 */
	Greater(">", "$gt"),
	/**
	 * 大于等于
	 */
	Greater_Equal(">=", "$gte"),
	/**
	 * 小于
	 */
	Less("<", "$lt"),
	/**
	 * 小于等于
	 */
	Less_Equal("<=", "$lte"),
	/**
	 * 模糊查询
	 */
	Like("like", null);

	private String rdbVal;
	private String mongoVal;

	ConditionEnum(String rdbVal, String mongoVal)
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

	public boolean getMongoCurLevel()
	{
		return mongoVal == null;
	}
}
