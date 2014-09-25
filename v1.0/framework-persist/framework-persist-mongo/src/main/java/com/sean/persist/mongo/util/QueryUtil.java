package com.sean.persist.mongo.util;

import java.util.HashMap;
import java.util.Map;

import com.sean.persist.enums.LogicEnum;
import com.sean.persist.ext.Condition;

public class QueryUtil
{
	private static Map<LogicEnum, ConditionParser> parsers = new HashMap<LogicEnum, ConditionParser>(2);
	static
	{
		parsers.put(LogicEnum.And, new ConditionParserAnd());
		parsers.put(LogicEnum.Or, new ConditionParserOr());
	}

	public static ConditionParser getConditionParser(Condition cond)
	{
		return parsers.get(cond.getLogic());
	}
}
