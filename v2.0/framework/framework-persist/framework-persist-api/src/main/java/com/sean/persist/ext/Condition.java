package com.sean.persist.ext;

import com.sean.persist.enums.ConditionEnum;
import com.sean.persist.enums.LogicEnum;

/**
 * 条件对象
 * @author sean
 */
public class Condition
{
	private String column;
	private ConditionEnum compare;
	private Object value;
	private LogicEnum logic;

	private Condition()
	{
	}
	
	/**
	 * and关系
	 * @param column
	 * @param value
	 */
	public Condition(String column, Object value)
	{
		this(column, ConditionEnum.Equal, value);
	}

	/**
	 * 条件构造
	 * @param column					数据库列
	 * @param compare					比较符号
	 * @param value						数据库列值
	 */
	public Condition(String column, ConditionEnum compare, Object value)
	{
		this();
		this.column = column;
		this.compare = compare;
		this.value = value;
		this.logic = LogicEnum.And;
	}

	/**
	 * 条件构造
	 * @param logic						与上一个条件到逻辑关系
	 * @param column					数据库列
	 * @param compare					比较符号
	 * @param value						数据库列值
	 */
	public Condition(LogicEnum logic, String column, ConditionEnum compare, Object value)
	{
		this(column, compare, value);
		this.logic = logic;
	}

	public String getColumn()
	{
		return column;
	}

	public ConditionEnum getCompare()
	{
		return compare;
	}

	public Object getValue()
	{
		return value;
	}

	public LogicEnum getLogic()
	{
		return logic;
	}

}
