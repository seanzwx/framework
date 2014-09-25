package com.sean.service.ext;

import java.util.regex.Pattern;

import com.sean.service.entity.ParameterEntity;

/**
 * 小数参数验证
 * @author Sean
 *
 */
public class FloatChecker implements ParameterChecker
{
	private final Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
	
	@Override
	public boolean check(String val, ParameterEntity param)
	{
		if (val != null && !val.isEmpty())
		{
			return pattern.matcher(val).matches();
		}
		return false;
	}

	@Override
	public Object getValue(String value)
	{
		return Float.parseFloat(value);
	}
}
