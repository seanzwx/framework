package com.sean.unittest.core;

import java.util.Map;

import com.sean.service.core.Action;
import com.sean.service.core.Session;

/**
 * request session unittest implementation
 * @author sean
 */
public final class SessionTesterImpl extends Session
{
	private Map<String, String[]> parameters;

	public SessionTesterImpl(Action action, Map<String, Object> httpsession, Map<String, String[]> parameters)
	{
		super(action);
		this.parameters = parameters;
	}

	@Override
	public String getParameter(final String name)
	{
		String[] tmp = parameters.get(name);
		return tmp == null ? null : tmp[0];
	}

	@Override
	public String[] getParameters(String name)
	{
		return parameters.get(name);
	}

	@Override
	public long getLongParameter(String name)
	{
		return Long.parseLong(parameters.get(name)[0]);
	}

	@Override
	public long[] getLongParameters(String name)
	{
		String[] tmp = parameters.get(name);
		long[] list = new long[tmp.length];
		for (int i = 0; i < list.length; i++)
		{
			list[i] = Long.parseLong(tmp[i]);
		}
		return list;
	}

	@Override
	public int getIntParameter(String name)
	{
		return Integer.parseInt(parameters.get(name)[0]);
	}

	@Override
	public int[] getIntParameters(String name)
	{
		String[] tmp = parameters.get(name);
		int[] list = new int[tmp.length];
		for (int i = 0; i < list.length; i++)
		{
			list[i] = Integer.parseInt(tmp[i]);
		}
		return list;
	}
	
	@Override
	public byte getByteParameter(String name)
	{
		return Byte.parseByte(parameters.get(name)[0]);
	}

	@Override
	public byte[] getByteParameters(String name)
	{
		String[] tmp = parameters.get(name);
		byte[] list = new byte[tmp.length];
		for (int i = 0; i < list.length; i++)
		{
			list[i] = Byte.parseByte(tmp[i]);
		}
		return list;
	}

	@Override
	public float getFloatParameter(String name)
	{
		return Float.parseFloat(parameters.get(name)[0]);
	}

	@Override
	public float[] getFloatParameters(String name)
	{
		String[] tmp = parameters.get(name);
		float[] list = new float[tmp.length];
		for (int i = 0; i < list.length; i++)
		{
			list[i] = Float.parseFloat(tmp[i]);
		}
		return list;
	}

	@Override
	public double getDoubleParameter(String name)
	{
		return Double.parseDouble(parameters.get(name)[0]);
	}

	@Override
	public double[] getDoubleParameters(String name)
	{
		String[] tmp = parameters.get(name);
		double[] list = new double[tmp.length];
		for (int i = 0; i < list.length; i++)
		{
			list[i] = Double.parseDouble(tmp[i]);
		}
		return list;
	}

	@Override
	public Map<String, String[]> getParameterMap()
	{
		return parameters;
	}

	@Override
	public String getHeader(String header)
	{
		throw new UnsupportedOperationException("not supported for unit test");
	}

	@Override
	public void addHeader(String header, String value)
	{
		throw new UnsupportedOperationException("not supported for unit test");
	}

	@Override
	public void addDateHeader(String header, long date)
	{
		throw new UnsupportedOperationException("not supported for unit test");
	}

	@Override
	public String getRootPath()
	{
		throw new UnsupportedOperationException("not supported for unit test");
	}

	@Override
	public String getRemoteAddress()
	{
		return "127.0.0.1";
	}

	@Override
	public int getRemotePort()
	{
		return 0;
	}
}
