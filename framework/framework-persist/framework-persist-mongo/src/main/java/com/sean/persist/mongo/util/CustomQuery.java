package com.sean.persist.mongo.util;

import com.mongodb.DBObject;

public class CustomQuery
{
	private DBObject query;
	private DBObject fields;

	public CustomQuery(DBObject query)
	{
		this(query, null);
	}

	public CustomQuery(DBObject query, DBObject fields)
	{
		this.query = query;
		this.fields = fields;
	}

	public DBObject getQuery()
	{
		return query;
	}

	public DBObject getFields()
	{
		return fields;
	}

}
