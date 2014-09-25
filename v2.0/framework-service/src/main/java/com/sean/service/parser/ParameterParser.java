package com.sean.service.parser;

import com.sean.service.annotation.ParameterConfig;
import com.sean.service.entity.ParameterEntity;

/**
 * Parameter解析器
 * @author sean
 */
public class ParameterParser
{
	public ParameterEntity parse(ParameterConfig pc, String name)
	{
		ParameterEntity pe = new ParameterEntity(name, pc.dataType(), pc.type(), pc.regex(), pc.length(), pc.enumVals(), pc.descr(),
				pc.errormsg());
		return pe;
	}
}
