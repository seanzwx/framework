package com.sean.service.core;

public class V1 implements Version
{
	@Override
	public String getVersion()
	{
		return "v1";
	}

	@Override
	public int getVersionIndex()
	{
		return 1;
	}
}
