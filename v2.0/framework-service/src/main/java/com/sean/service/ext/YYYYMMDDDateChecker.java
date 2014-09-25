package com.sean.service.ext;

import java.util.regex.Pattern;

import com.sean.service.entity.ParameterEntity;

/**
 * yyyy-mm-dd日期参数验证
 * @author Sean
 *
 */
public class YYYYMMDDDateChecker implements ParameterChecker
{
	private final Pattern pattern = Pattern.compile("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}");

	@Override
	public boolean check(String val, ParameterEntity param)
	{
		if (val != null && !val.isEmpty())
		{
			return pattern.matcher(val).matches();
		}
		return false;
	}
}