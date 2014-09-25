package com.sean.service.ext;

import java.util.regex.Pattern;

import com.sean.service.entity.ParameterEntity;

/**
 * 长整形参数验证
 * @author Sean
 *
 */
public class LongChecker implements ParameterChecker
{
	private final Pattern pattern = Pattern.compile("[0-9]+");

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
