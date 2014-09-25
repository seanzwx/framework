package com.sean.persist.mongo.util;

import com.mongodb.DBObject;
import com.sean.persist.ext.Condition;

public interface ConditionParser
{
	public void parser(DBObject bson, Condition cond);
}
