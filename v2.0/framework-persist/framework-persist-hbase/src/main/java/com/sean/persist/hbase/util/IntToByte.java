package com.sean.persist.hbase.util;

import org.apache.hadoop.hbase.util.Bytes;

public class IntToByte implements ByteUtil
{
	private static ByteUtil instance = new IntToByte();

	public static ByteUtil getInstance()
	{
		return instance;
	}

	@Override
	public byte[] toBytes(Object val)
	{
		return Bytes.toBytes((int) val);
	}

	@Override
	public Object parse(byte[] bytes)
	{
		return Bytes.toInt(bytes);
	}
}
