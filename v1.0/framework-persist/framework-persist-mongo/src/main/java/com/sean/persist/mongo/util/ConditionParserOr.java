package com.sean.persist.mongo.util;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sean.persist.ext.Condition;

public class ConditionParserOr implements ConditionParser
{
	@Override
	public void parser(DBObject bson, Condition cond)
	{
		if (cond.getCompare().getMongoCurLevel())
		{
			List<DBObject> list = new ArrayList<DBObject>(1);
			list.add(new BasicDBObject(cond.getColumn(), cond.getValue()));
			bson.put(cond.getLogic().getMongoValue(), list);
		}
		else
		{
			List<DBObject> list = new ArrayList<DBObject>(1);
			list.add(new BasicDBObject(cond.getColumn(), new BasicDBObject(cond.getCompare().getMongoValue(), cond.getValue())));
			bson.put(cond.getLogic().getMongoValue(), list);
		}
	}
}
