package com.sean.persist.mongo.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.sean.log.core.LogFactory;
import com.sean.persist.core.Entity;
import com.sean.persist.core.EntityDao;
import com.sean.persist.core.EntityValue;
import com.sean.persist.entity.ColumnEntity;
import com.sean.persist.entity.DataSourceEntity;
import com.sean.persist.entity.EntityEntity;
import com.sean.persist.ext.Condition;
import com.sean.persist.ext.Order;
import com.sean.persist.ext.Value;
import com.sean.persist.mongo.util.CustomQuery;
import com.sean.persist.mongo.util.QueryUtil;

/**
 * 持久化Mongo数据库实现
 * @author sean
 */
@SuppressWarnings("unchecked")
public final class EntityDaoMongo<E extends Entity> extends EntityDao<E>
{
	private static final Logger logger = LogFactory.getLogger(L.Mongo);
	private String primaryKey;
	private String[] allColumns;
	private Class<?> cls;

	private DBCollection collection;

	private static final String ID = "_id";

	public EntityDaoMongo(EntityEntity entity, DataSourceEntity dataSource, MongoClient client) throws Exception
	{
		// 获取实体信息
		this.primaryKey = entity.getPrimaryKey().getColumn();
		this.cls = entity.getCls();

		// 获取collection
		this.collection = this.getCollection(client, dataSource, entity.getTableName());

		// 生成所有列，不包含主键，主键作为_id
		int length = entity.getColumns().size();
		allColumns = new String[length - 1];
		int i = 0;
		for (ColumnEntity c : entity.getColumns())
		{
			if (c.isPrimaryKey())
			{
				continue;
			}
			allColumns[i] = c.getColumn();
			i++;
		}
	}

	@Override
	public List<Object> getIdList(List<Condition> conds, List<Order> orders)
	{
		DBObject query = this.getCondition(conds);
		DBCursor cur = this.collection.find(query, new BasicDBObject(ID, 1));
		if (orders != null && !orders.isEmpty())
		{
			cur.sort(this.getOrder(orders));
		}

		List<Object> list = new LinkedList<>();
		while (cur.hasNext())
		{
			list.add(cur.next().get(ID));
		}

		this.showStatement("select", query.toString());
		return list;
	}

	@Override
	public List<Object> getIdList(List<Condition> conds, List<Order> orders, int start, int limit)
	{
		DBObject query = this.getCondition(conds);
		DBCursor cur = this.collection.find(query, new BasicDBObject(ID, 1)).skip(start).limit(limit);
		if (orders != null && !orders.isEmpty())
		{
			cur.sort(this.getOrder(orders));
		}

		List<Object> list = new LinkedList<>();
		while (cur.hasNext())
		{
			list.add(cur.next().get(ID));
		}

		this.showStatement("select", query.toString());
		return list;
	}

	@Override
	public int count(List<Condition> conds)
	{
		DBObject query = this.getCondition(conds);
		long count = this.collection.count(query);

		this.showStatement("count", query.toString());
		return (int) count;
	}

	@Override
	public void persistBatch(List<E> entitys)
	{
		List<DBObject> objs = new ArrayList<>(entitys.size());
		DBObject obj = null;
		Map<String, Object> vals = null;
		Object key = null;
		for (Entity ent : entitys)
		{
			vals = ent.getValues();
			vals.remove(primaryKey);

			// 生成主键
			key = ent.generateRowKey();
			vals.put(ID, key);
			ent.setKey(key);

			obj = new BasicDBObject(vals);
			objs.add(obj);

			this.showStatement("insert", obj.toString());
		}
		this.collection.insert(objs);
	}

	@Override
	public void removeById(List<Object> ids)
	{
		DBObject query = new BasicDBObject(ID, new BasicDBObject("$in", ids));
		this.collection.remove(query);

		this.showStatement("remove", query.toString());
	}

	@Override
	public void updateById(List<Object> ids, List<Value> vals)
	{
		DBObject query = new BasicDBObject(ID, new BasicDBObject("$in", ids));
		DBObject updateValue = new BasicDBObject(vals.size());
		for (Value val : vals)
		{
			updateValue.put(val.getColumn(), val.getValue());
		}
		this.collection.update(query, updateValue);

		this.showStatement("update", query.toString());
	}

	@Override
	public List<E> loadByIds(List<Object> ids)
	{
		List<E> list = new ArrayList<>(ids.size());

		DBObject query = new BasicDBObject(ID, new BasicDBObject("$in", ids));
		DBCursor cur = this.collection.find(query, this.getReturnField(allColumns));
		while (cur.hasNext())
		{
			list.add(this.getEntity(cur.next()));
		}

		this.showStatement("select", query.toString());
		return list;
	}

	@Override
	public Object executeScalar(Object statement)
	{
		CustomQuery query = (CustomQuery) statement;
		DBObject rs = this.collection.findOne(query.getQuery(), query.getFields());

		this.showStatement("select", query.getQuery().toString());

		for (String key : rs.keySet())
		{
			return rs.get(key);
		}
		return null;
	}

	@Override
	public Map<String, Object> executeMap(Object statement)
	{
		CustomQuery query = (CustomQuery) statement;
		DBObject rs = this.collection.findOne(query.getQuery(), query.getFields());

		this.showStatement("select", query.getQuery().toString());
		return rs.toMap();
	}

	@Override
	public List<Map<String, Object>> executeList(Object statement)
	{
		List<Map<String, Object>> list = new LinkedList<>();
		CustomQuery query = (CustomQuery) statement;
		DBCursor rs = this.collection.find(query.getQuery(), query.getFields());
		while (rs.hasNext())
		{
			list.add(rs.next().toMap());
		}

		this.showStatement("select", query.getQuery().toString());
		return list;
	}

	@Override
	public List<E> executeEntityList(Object statement)
	{
		List<E> list = new LinkedList<>();

		CustomQuery query = (CustomQuery) statement;
		DBCursor rs = this.collection.find(query.getQuery(), query.getFields());
		while (rs.hasNext())
		{
			list.add(this.getEntity(rs.next()));
		}

		this.showStatement("select", query.getQuery().toString());
		return list;
	}

	/**
	 * 获取collection
	 * @param client
	 * @param ds
	 * @param collectionName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private DBCollection getCollection(MongoClient client, DataSourceEntity ds, String collectionName)
	{
		DB db = client.getDB(ds.getDbName());
		if (db.authenticate(ds.getUser(), ds.getPassword().toCharArray()))
		{
			return db.getCollection(collectionName);
		}
		else
		{
			logger.error("mongodb login fail : db=" + ds.getDbName() + ", username=" + ds.getUser() + ", password=" + ds.getPassword());
			throw new RuntimeException("mongodb login fail : db=" + ds.getDbName() + ", username=" + ds.getUser() + ", password=" + ds.getPassword());
		}
	}

	/**
	 * 从DBCursor读取Entity
	 */
	private E getEntity(DBObject rs)
	{
		Map<String, Object> data = new HashMap<>(allColumns.length);
		for (String col : allColumns)
		{
			data.put(col, rs.get(col));
		}
		E ent = null;
		try
		{
			ent = (E) cls.newInstance();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		ent.setValues(new EntityValue(data));
		ent.setKey(rs.get(ID));
		return ent;
	}

	/**
	 * 获取排序对象
	 * @param orders
	 * @return
	 */
	private DBObject getOrder(List<Order> orders)
	{
		DBObject obj = new BasicDBObject(orders.size());
		for (Order order : orders)
		{
			obj.put(order.getColumn(), order.getOrder().getMongoValue());
		}
		return obj;
	}

	/**
	 * 获取条件对象
	 */
	private DBObject getCondition(List<Condition> conds)
	{
		int length = conds.size();
		DBObject bson = new BasicDBObject(length);
		Condition cond = null;
		for (int i = 0; i < length; i++)
		{
			cond = conds.get(i);
			QueryUtil.getConditionParser(cond).parser(bson, cond);
		}
		return bson;
	}

	/**
	 * 获取返回的列
	 */
	private DBObject getReturnField(String[] columns)
	{
		DBObject obj = new BasicDBObject(columns.length + 1);
		for (int i = 0; i < columns.length; i++)
		{
			obj.put(columns[i], 1);
		}
		obj.put(ID, 1);
		return obj;
	}

	private void showStatement(String operator, String statement)
	{
		StringBuilder sb = new StringBuilder(1024);
		sb.append("mongo ").append(operator).append(" : ").append(statement);
		logger.debug(sb.toString());
	}
}
