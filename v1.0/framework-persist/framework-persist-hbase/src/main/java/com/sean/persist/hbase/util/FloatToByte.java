package com.sean.persist.hbase.util;

import org.apache.hadoop.hbase.util.Bytes;

public class FloatToByte implements ByteUtil
{
	private static ByteUtil instance = new FloatToByte();

	public static ByteUtil getInstance()
	{
		return instance;
	}

	@Override
	public byte[] toBytes(Object val)
	{
		return Bytes.toBytes((float) val);
	}

	@Override
	public Object parse(byte[] bytes)
	{
		return Bytes.toFloat(bytes);
	}
}
