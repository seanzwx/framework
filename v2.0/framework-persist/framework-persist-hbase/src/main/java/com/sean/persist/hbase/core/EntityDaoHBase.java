package com.sean.persist.hbase.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.sean.log.core.LogFactory;
import com.sean.persist.core.Entity;
import com.sean.persist.core.EntityDao;
import com.sean.persist.core.EntityValue;
import com.sean.persist.entity.ColumnEntity;
import com.sean.persist.entity.EntityEntity;
import com.sean.persist.ext.Condition;
import com.sean.persist.ext.Order;
import com.sean.persist.ext.Value;
import com.sean.persist.hbase.util.ByteUtil;
import com.sean.persist.hbase.util.FloatToByte;
import com.sean.persist.hbase.util.IntToByte;
import com.sean.persist.hbase.util.LongToByte;
import com.sean.persist.hbase.util.StringToByte;

/**
 * 持久化HBase实现
 * @author sean
 */
@SuppressWarnings("unchecked")
public final class EntityDaoHBase<E extends Entity> extends EntityDao<E>
{
	private HTablePool hTablePool;
	private byte[] tableName;
	private static final byte[] columnFamily = Bytes.toBytes("c");
	private Map<String, ByteUtil> byteUtils = new HashMap<String, ByteUtil>();
	private ByteUtil keyByteUtil;
	private String[] allColumns;
	private String primaryKey;
	private Class<?> cls;

	private Logger logger = LogFactory.getLogger(L.Hbase);

	public EntityDaoHBase(EntityEntity entity, Configuration config, int maxTable)
	{
		this.hTablePool = new HTablePool(config, maxTable, new PoolHTableFactory(maxTable));
		this.tableName = Bytes.toBytes(entity.getTableName());
		this.primaryKey = entity.getPrimaryKey().getColumn();

		this.cls = entity.getCls();

		// 生成所有字节转换器
		for (ColumnEntity c : entity.getColumns())
		{
			Class<?> type = c.getField().getType();
			if (type == int.class || type == Integer.class)
			{
				byteUtils.put(c.getColumn(), IntToByte.getInstance());
			}
			else if (type == long.class || type == Long.class)
			{
				byteUtils.put(c.getColumn(), LongToByte.getInstance());
			}
			else if (type == float.class || type == Float.class)
			{
				byteUtils.put(c.getColumn(), FloatToByte.getInstance());
			}
			else if (type == String.class)
			{
				byteUtils.put(c.getColumn(), StringToByte.getInstance());
			}
		}
		keyByteUtil = byteUtils.get(primaryKey);

		// 生成所有列，不包含主键，主键作为rowkey
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
		throw new UnsupportedOperationException("hbase only supports rowKey search");
	}

	@Override
	public List<Object> getIdList(List<Condition> conds, List<Order> orders, int start, int limit)
	{
		throw new UnsupportedOperationException("hbase only supports rowKey search");
	}

	@Override
	public int count(List<Condition> conds)
	{
		throw new UnsupportedOperationException("hbase only supports rowKey search");
	}

	@Override
	public void persistBatch(List<E> entitys)
	{
		HTableInterface table = this.hTablePool.getTable(tableName);
		try
		{
			List<Put> puts = new ArrayList<Put>(entitys.size());
			Put put = null;
			Map<String, Object> data = null;
			Object rowKey;
			for (Entity ent : entitys)
			{
				rowKey = ent.generateRowKey();
				ent.setKey(rowKey);
				put = new Put(keyByteUtil.toBytes(rowKey));
				data = ent.getValues();
				for (String col : allColumns)
				{
					put.add(columnFamily, Bytes.toBytes(col), byteUtils.get(col).toBytes(data.get(col)));
				}
				puts.add(put);

				showStatement("insert", rowKey);
			}
			table.put(puts);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			try
			{
				table.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void removeById(List<Object> ids)
	{
		HTableInterface table = this.hTablePool.getTable(tableName);
		try
		{
			List<Delete> deletes = new ArrayList<Delete>(ids.size());
			Delete del = null;
			for (Object id : ids)
			{
				del = new Delete(this.keyByteUtil.toBytes(id));
				deletes.add(del);
			}
			table.delete(deletes);
			showStatement("remove", ids);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			try
			{
				table.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void updateById(List<Object> ids, List<Value> vals)
	{
		HTableInterface table = this.hTablePool.getTable(tableName);
		try
		{
			List<Put> puts = new ArrayList<Put>(ids.size());
			Put put = null;
			for (Object id : ids)
			{
				put = new Put(this.keyByteUtil.toBytes(id));
				for (Value val : vals)
				{
					put.add(columnFamily, Bytes.toBytes(val.getColumn()), byteUtils.get(val.getColumn()).toBytes(val.getValue()));
				}
				puts.add(put);
			}
			table.put(puts);
			showStatement("update", ids);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			try
			{
				table.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public List<E> loadByIds(List<Object> ids)
	{
		List<E> list = new ArrayList<E>(ids.size());
		HTableInterface table = this.hTablePool.getTable(tableName);
		try
		{
			List<Get> gets = new ArrayList<>(ids.size());
			Get get = null;
			for (Object id : ids)
			{
				get = new Get(keyByteUtil.toBytes(id));
				for (int i = 0; i < allColumns.length; i++)
				{
					get.addColumn(columnFamily, Bytes.toBytes(allColumns[i]));
				}
				gets.add(get);
			}
			Result[] rs = table.get(gets);
			showStatement("select", ids);

			for (Result r : rs)
			{
				if (!r.isEmpty())
				{
					list.add(this.getEntity(r));
				}
			}
			return list;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			try
			{
				table.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
		}
		return null;
	}

	@Override
	public Object executeScalar(Object statement)
	{
		throw new UnsupportedOperationException("hbase only supports rowKey search");
	}

	@Override
	public Map<String, Object> executeMap(Object statement)
	{
		throw new UnsupportedOperationException("hbase only supports rowKey search");
	}

	@Override
	public List<Map<String, Object>> executeList(Object statement)
	{
		throw new UnsupportedOperationException("hbase only supports rowKey search");
	}
	
	@Override
	public List<E> executeEntityList(Object statement)
	{
		throw new UnsupportedOperationException("hbase only supports rowKey search");
	}

	private E getEntity(Result rs)
	{
		Map<String, Object> data = new HashMap<>(allColumns.length);
		for (String col : allColumns)
		{
			data.put(col, byteUtils.get(col).parse(rs.getValue(columnFamily, Bytes.toBytes(col))));
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
		ent.setKey(this.keyByteUtil.parse(rs.getRow()));
		return ent;
	}

	private void showStatement(String operator, List<Object> ids)
	{
		logger.debug("hbase " + operator + ":" + ids.toString());
	}

	private void showStatement(String operator, Object id)
	{
		logger.debug("hbase " + operator + ":[" + id + "]");
	}
}
