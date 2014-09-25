package com.sean.service.core;

import java.util.Map;

import com.sean.service.entity.ActionEntity;
import com.sean.service.entity.ReturnParameterEntity;
import com.sean.service.enums.ResultState;

/**
 * json转换工具
 * @author sean
 */
public final class JsonWriter
{
	public static String toJson(Session session, ActionEntity action) throws Exception
	{
		StringBuilder json = new StringBuilder(4096);

		if (session.getState() == ResultState.Success)
		{
			json.append("{\"state\":\"Success\"").append(",\"data\":{");
			Map<String, Object> retMap = session.getReturnAttributeMap();
			for (ReturnParameterEntity param : action.getReturnParams())
			{
				param.getFieldWriter().write(json, retMap, param);
				json.append(',');
			}
			if (json.charAt(json.length() - 1) == ',')
			{
				json.setCharAt(json.length() - 1, '}');
			}
			else
			{
				json.append('}');
			}
			json.append('}');
		}
		else if (session.getState() == ResultState.BusinessException)
		{
			json.append("{\"state\":\"").append(session.getState()).append("\",\"msg\":\"").append(session.getMsg())
					.append("\",\"code\":").append(session.getCode()).append("}");
		}
		else
		{
			json.append("{\"state\":\"").append(session.getState()).append("\",\"msg\":\"").append(session.getMsg())
					.append("\"}");
		}
		return json.toString();
	}

}
