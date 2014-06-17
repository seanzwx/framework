package com.sean.service.core;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求Session Servlet实现
 * @author Sean
 */
public final class SessionServletImpl extends Session
{
	private ServletRequest request;
	private ServletResponse response;

	public SessionServletImpl(Action action, ServletRequest request, ServletResponse response)
	{
		super();
		this.request = request;
		this.response = response;

		// 优先从参数种去sid
		this.sid = request.getParameter("sid");
		// 参数种不存在从cookie取
		if (sid == null)
		{
			HttpServletRequest r = (HttpServletRequest) request;
			Cookie[] cookies = r.getCookies();
			if (cookies != null)
			{
				for (Cookie c : cookies)
				{
					if ("sid".equals(c.getName()))
					{
						this.sid = c.getValue();
						break;
					}
				}	
			}
		}
	}

	@Override
	public String getParameter(final String name)
	{
		return request.getParameter(name);
	}

	@Override
	public String[] getParameters(final String name)
	{
		return request.getParameterValues(name);
	}

	@Override
	public long getLongParameter(String name)
	{
		return Long.parseLong(request.getParameter(name));
	}

	@Override
	public long[] getLongParameters(String name)
	{
		String[] tmp = request.getParameterValues(name);
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
		return Integer.parseInt(request.getParameter(name));
	}

	@Override
	public int[] getIntParameters(String name)
	{
		String[] tmp = request.getParameterValues(name);
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
		return Byte.parseByte(request.getParameter(name));
	}

	@Override
	public byte[] getByteParameters(String name)
	{
		String[] tmp = request.getParameterValues(name);
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
		return Float.parseFloat(request.getParameter(name));
	}

	@Override
	public float[] getFloatParameters(String name)
	{
		String[] tmp = request.getParameterValues(name);
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
		return Double.parseDouble(request.getParameter(name));
	}

	@Override
	public double[] getDoubleParameters(String name)
	{
		String[] tmp = request.getParameterValues(name);
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
		return request.getParameterMap();
	}

	@Override
	public String getHeader(String header)
	{
		return ((HttpServletRequest) request).getHeader(header);
	}

	@Override
	public void addHeader(String header, String value)
	{
		((HttpServletResponse) response).addHeader(header, value);
	}

	@Override
	public void addDateHeader(String header, long date)
	{
		((HttpServletResponse) response).addDateHeader(header, date);
	}

	@Override
	public String getRootPath()
	{
		return request.getServletContext().getRealPath("/");
	}

	@Override
	public String getRemoteAddress()
	{
		return request.getRemoteAddr();
	}

	@Override
	public int getRemotePort()
	{
		return request.getRemotePort();
	}
}
