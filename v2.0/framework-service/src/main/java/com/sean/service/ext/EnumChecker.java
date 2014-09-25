package com.sean.service.ext;

import com.sean.service.entity.ParameterEntity;

/**
 * 枚举参数验证
 * @author Sean
 */
public class EnumChecker implements ParameterChecker
{
	@Override
	public boolean check(String val, ParameterEntity param)
	{
		if (val != null)
		{
			for (String it : param.getEnumVals())
			{
				if (it.equals(val))
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Object getValue(String value)
	{
		return Integer.parseInt(value);
	}
}
