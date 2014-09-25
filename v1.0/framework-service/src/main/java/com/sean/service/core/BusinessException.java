package com.sean.service.core;

/**
 * 业务异常
 * @author sean
 */
public final class BusinessException extends Exception
{
	private static final long serialVersionUID = 1L;
	private String msg;
	private int code;

	public BusinessException(String msg, int code)
	{
		this.msg = msg;
		this.code = code;
	}

	@Override
	public String getMessage()
	{
		return msg;
	}

	public int getCode()
	{
		return code;
	}
}
