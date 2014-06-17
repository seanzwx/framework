package com.sean.persist.hbase.util;

import org.apache.hadoop.hbase.util.Bytes;

public class LongToByte implements ByteUtil
{
	private static ByteUtil instance = new LongToByte();

	public static ByteUtil getInstance()
	{
		return instance;
	}

	@Override
	public byte[] toBytes(Object val)
	{
		return Bytes.toBytes((long) val);
	}

	@Override
	public Object parse(byte[] bytes)
	{
		return Bytes.toLong(bytes);
	}
}
