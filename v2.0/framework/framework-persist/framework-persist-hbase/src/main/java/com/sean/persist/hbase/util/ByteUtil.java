package com.sean.persist.hbase.util;

public interface ByteUtil
{
	public byte[] toBytes(Object val);
	
	public Object parse(byte[] bytes);
}
