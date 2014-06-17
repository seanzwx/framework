package com.sean.persist.mongo.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sean.persist.ext.Condition;

/**
 * mongo and查询解析
 * @author sean
 *
 */
public class ConditionParserAnd implements ConditionParser
{
	@Override
	public void parser(DBObject bson, Condition cond)
	{
		if (cond.getCompare().getMongoCurLevel())
		{
			bson.put(cond.getColumn(), cond.getValue());
		}
		else
		{
			bson.put(cond.getColumn(), new BasicDBObject(cond.getCompare().getMongoValue(), cond.getValue()));
		}
	}
}
