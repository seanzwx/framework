package com.sean.persist.core;

import java.util.Map;

/**
 * 实体值
 * @author sean
 */
public class EntityValue
{
	private Map<String, Object> vals;

	public EntityValue(Map<String, Object> vals)
	{
		this.vals = vals;
	}

	public int getInt(String column, int defaultValue)
	{
		Object tmp = vals.get(column);
		return tmp == null ? defaultValue : (int) tmp;
	}

	public int getInt(String column)
	{
		return this.getInt(column, 0);
	}

	public float getFloat(String column, float defaultValue)
	{
		Object tmp = vals.get(column);
		return tmp == null ? defaultValue : (float) tmp;
	}

	public float getFloat(String column)
	{
		return this.getFloat(column, 0);
	}

	public long getLong(String column, long defaultValue)
	{
		Object tmp = vals.get(column);
		return tmp == null ? defaultValue : (long) tmp;
	}

	public long getLong(String column)
	{
		return this.getLong(column, 0);
	}

	public double getDouble(String column, double defaultValue)
	{
		Object tmp = vals.get(column);
		return tmp == null ? defaultValue : (double) tmp;
	}

	public double getDouble(String column)
	{
		return this.getDouble(column, 0);
	}

	public char getChar(String column, char defaultValue)
	{
		Object tmp = vals.get(column);
		return tmp == null ? defaultValue : (char) tmp;
	}

	public char getChar(String column)
	{
		return this.getChar(column, (char) 0);
	}

	public String getString(String column, String defaultValue)
	{
		Object tmp = vals.get(column);
		return tmp == null ? defaultValue : (String) tmp;
	}

	public String getString(String column)
	{
		return this.getString(column, null);
	}

	public byte getByte(String column, byte defaultValue)
	{
		Object tmp = vals.get(column);
		return tmp == null ? defaultValue : Byte.parseByte(tmp.toString());
	}

	public byte getByte(String column)
	{
		return this.getByte(column, (byte) 0);
	}
}
