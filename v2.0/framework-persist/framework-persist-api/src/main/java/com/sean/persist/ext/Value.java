package com.sean.persist.ext;

/**
 * 数据库更新值对象
 * @author sean
 *
 */
public class Value
{
	private String column;
	private Object value;

	/**
	 * 更新值对象
	 * @param column				数据库列
	 * @param value					数据库列值
	 */
	public Value(String column, Object value)
	{
		this.column = column;
		this.value = value;
	}

	public String getColumn()
	{
		return column;
	}

	public Object getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return "column=" + column + ", value=" + value;
	}

}
