package com.sean.service.ext;

import com.sean.service.entity.ParameterEntity;

/**
 * 参数验证接口
 * @author Sean
 *
 */
public interface ParameterChecker
{
	/**
	 * 验证参数
	 * @param val						参数值
	 * @param param						参数定义
	 * @return
	 */
	public boolean check(String val, ParameterEntity param);
}
