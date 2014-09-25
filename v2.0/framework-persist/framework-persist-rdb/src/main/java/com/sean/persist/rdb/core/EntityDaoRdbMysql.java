package com.sean.persist.rdb.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.sean.log.core.LogFactory;
import com.sean.persist.core.Entity;
import com.sean.persist.entity.ColumnEntity;
import com.sean.persist.entity.EntityEntity;
import com.sean.persist.ext.Condition;
import com.sean.persist.ext.Order;
import com.sean.persist.util.EntityDaoUtil;

/**
 * 持久化Mysql实现
 * @author sean
 */
public final class EntityDaoRdbMysql<E extends Entity> extends EntityDaoRdb<E>
{
	private static final Logger logger = LogFactory.getLogger(L.PersistRdb);
	private String insert;

	public EntityDaoRdbMysql(EntityEntity entity, DataSource dataSource, boolean showSql) throws Exception
	{
		super(entity, dataSource, showSql);

		// 生成插入语句
		StringBuilder tmp = new StringBuilder();
		tmp.append(" (");
		for (ColumnEntity c : entity.getColumns())
		{
			tmp.append(c.getColumn()).append(',');
		}
		tmp.setCharAt(tmp.length() - 1, ')');
		tmp.append(" values(");
		for (int i = 0; i < entity.getColumns().size(); i++)
		{
			tmp.append("?,");
		}
		tmp.setCharAt(tmp.length() - 1, ')');
		this.insert = tmp.toString();
	}

	@Override
	public List<Object> getIdList(List<Condition> conds, List<Order> orders, int start, int limit)
	{
		Connection conn = null;
		try
		{
			conn = this.dataSource.getConnection();

			StringBuilder sql = new StringBuilder(sqlLength);
			sql.append("select ").append(key).append(" from ").append(table);

			if (this.partition != null)
			{
				sql.append('_').append(String.valueOf(this.partition.getPartition()));
			}

			sql.append(" where ");
			sql.append(EntityDaoUtil.getConditionStr(conds));
			if (orders != null && !orders.isEmpty())
			{
				sql.append("order by ");
				sql.append(EntityDaoUtil.getOrdersStr(orders));
			}
			sql.append(" limit ?,?");
			showSql(sql.toString());

			PreparedStatement ps = conn.prepareStatement(sql.toString());
			int index = 1;
			for (Condition cond : conds)
			{
				ps.setObject(index, cond.getValue());
				index++;
			}
			ps.setObject(index, start);
			ps.setObject(index + 1, limit);

			ResultSet rs = ps.executeQuery();
			List<Object> ids = new LinkedList<Object>();
			while (rs.next())
			{
				ids.add(rs.getObject(1));
			}
			return ids;
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
				if (conn != null && !conn.isClosed())
				{
					conn.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
				logger.error(e2.getMessage(), e2);
			}
		}
		return null;
	}

	@Override
	public void persistBatch(List<E> entitys)
	{
		Connection conn = null;
		try
		{
			conn = this.dataSource.getConnection();
			StringBuilder sql = new StringBuilder(128);
			sql.append("insert into ").append(table);

			if (this.partition != null)
			{
				sql.append('_').append(String.valueOf(this.partition.getPartition()));
			}

			sql.append(this.insert);

			PreparedStatement ps = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);

			int length = entitys.size();
			int j;
			for (int i = 0; i < length; i++)
			{
				j = 1;
				Map<String, Object> vals = entitys.get(i).getValues();
				for (int k = 0; k < allColumns.length; k++)
				{
					ps.setObject(j, vals.get(allColumns[k]));
					j++;
				}
				ps.addBatch();
				showSql(sql.toString());
			}
			ps.executeBatch();
			ResultSet rs = ps.getGeneratedKeys();
			int i = 0;
			while (rs.next())
			{
				entitys.get(i).setKey(rs.getObject(1));
			}
			rs.close();
			ps.close();
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
				if (conn != null && !conn.isClosed())
				{
					conn.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
				logger.error(e2.getMessage(), e2);
			}
		}
	}
}
