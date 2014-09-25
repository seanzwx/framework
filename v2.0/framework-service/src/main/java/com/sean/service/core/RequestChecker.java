package com.sean.service.core;

import com.sean.service.entity.ParameterEntity;
import com.sean.service.enums.ParameterType;
import com.sean.service.ext.EnumChecker;
import com.sean.service.ext.FloatChecker;
import com.sean.service.ext.IntegerChecker;
import com.sean.service.ext.LongChecker;
import com.sean.service.ext.ParameterChecker;
import com.sean.service.ext.StringChecker;
import com.sean.service.ext.YYYYMMDDDateChecker;
import com.sean.service.ext.YYYYMMDDHHMMSSDateChecker;

/**
 * 请求验证
 * @author sean
 */
public final class RequestChecker
{
	public static final ParameterChecker[] checkers = new ParameterChecker[7];

	/**
	 * 初始化验证器
	 */
	public RequestChecker()
	{
		checkers[0] = new IntegerChecker();
		checkers[1] = new LongChecker();
		checkers[2] = new FloatChecker();
		checkers[3] = new StringChecker();
		checkers[4] = new YYYYMMDDDateChecker();
		checkers[5] = new YYYYMMDDHHMMSSDateChecker();
		checkers[6] = new EnumChecker();
	}

	/**
	 * 验证
	 * @param val				参数	
	 * @param param				参数定义
	 */
	public boolean check(String val[], ParameterEntity param)
	{
		// 如果是单个参数
		if (param.getType() == ParameterType.Single)
		{
			return checkers[param.getDataType().getValue()].check(val[0], param);
		}
		// 如果是批量参数
		else
		{
			boolean rs = false;
			for (int i = 0; i < val.length; i++)
			{
				rs = checkers[param.getDataType().getValue()].check(val[i], param);
				if (!rs)
				{
					return false;
				}
			}
		}
		return true;
	}
}
