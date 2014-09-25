package com.sean.service.ext;

import java.util.regex.Pattern;

import com.sean.service.entity.ParameterEntity;

/**
 * 字符串参数验证
 * @author Sean
 *
 */
public class StringChecker implements ParameterChecker
{
	@Override
	public boolean check(String val, ParameterEntity param)
	{
		if (val != null)
		{
			// 如果有长度限制
			if (param.getLength() != 0)
			{
				if (val.length() > param.getLength())
				{
					return false;
				}
			}
			// 如果有配置正则表达式
			if (!param.getRegex().equals(""))
			{
				Pattern pattern = Pattern.compile(param.getRegex());
				return pattern.matcher(val).matches();
			}
			else
			{
				return true;
			}
		}
		return false;
	}
}
