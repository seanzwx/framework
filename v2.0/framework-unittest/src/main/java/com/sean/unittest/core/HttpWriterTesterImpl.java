package com.sean.unittest.core;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.sean.common.util.JsonUtil;
import com.sean.service.core.HttpWriter;
import com.sean.service.core.JsonWriter;
import com.sean.service.core.Session;
import com.sean.service.entity.ActionEntity;
import com.sean.service.enums.ReturnType;

public final class HttpWriterTesterImpl implements HttpWriter
{
	@Override
	public void write(ServletRequest request, ServletResponse response, Session session, ActionEntity action) throws Exception
	{
		if (action.getReturnType() == ReturnType.Json)
		{
			System.out.println("write json to client : ");
			System.out.println(JsonUtil.formatJson(JsonWriter.toJson(session, action), "   "));
		}
		else if (action.getReturnType() == ReturnType.Js)
		{
			System.out.println("write js to client : " + session.getReturnAttributeMap().get("js"));
		}
		else if (action.getReturnType() == ReturnType.Css)
		{
			System.out.println("write css to client : " + session.getReturnAttributeMap().get("css"));
		}
		else if (action.getReturnType() == ReturnType.File)
		{
		}
	}

	@Override
	public void writeJson(ServletRequest request, ServletResponse response, String json) throws Exception
	{
		System.out.println("write json to client : " + json);
	}
}
