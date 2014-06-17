package com.sean.persist.hbase.util;

import org.apache.hadoop.hbase.util.Bytes;

public class StringToByte implements ByteUtil
{
	private static ByteUtil instance = new StringToByte();

	public static ByteUtil getInstance()
	{
		return instance;
	}

	@Override
	public byte[] toBytes(Object val)
	{
		return Bytes.toBytes(val.toString());
	}

	@Override
	public Object parse(byte[] bytes)
	{
		return Bytes.toString(bytes);
	}
}
