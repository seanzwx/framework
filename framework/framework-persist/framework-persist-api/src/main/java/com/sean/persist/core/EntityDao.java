package com.sean.persist.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sean.log.core.LogFactory;
import com.sean.persist.enums.L;
import com.sean.persist.ext.Condition;
import com.sean.persist.ext.Order;
import com.sean.persist.ext.Value;

/**
 * 数据持久层接口
 * @author Sean
 */
public abstract class EntityDao<E extends Entity>
{
	protected static final Logger logger = LogFactory.getLogger(L.Persist);
	protected Partition partition;// 分却策略

	/**
	 * 查询ID列表
	 * @param conds
	 * @param orders
	 * @return
	 */
	public abstract List<Object> getIdList(List<Condition> conds, List<Order> orders);

	/**
	 * 查询ID列表
	 * @param conds
	 * @param orders
	 * @param start
	 * @param limit
	 * @return
	 */
	public abstract List<Object> getIdList(List<Condition> conds, List<Order> orders, int start, int limit);

	/**
	 * 统计记录数
	 * @param conds
	 * @param orders
	 * @return
	 */
	public abstract int count(List<Condition> conds);

	/**
	 * 统计记录数
	 * @param conds
	 * @param orders
	 * @return
	 */
	public int count(Condition cond)
	{
		List<Condition> conds = new ArrayList<>(1);
		conds.add(cond);
		return this.count(conds);
	}

	/**
	 * 持久化实体
	 * @param entity				实体对象
	 */
	public void persist(E entity)
	{
		List<E> entitys = new ArrayList<E>(1);
		entitys.add(entity);
		this.persistBatch(entitys);
	}

	/**
	 * 批量持久化尸体
	 * @param entitys				实体集合
	 */
	public abstract void persistBatch(List<E> entitys);

	/**
	 * 根据主键Id删除实体
	 * @param id					实体主键Id
	 */
	public void remove(Object id)
	{
		List<Object> ids = new ArrayList<>(1);
		ids.add(id);
		this.removeById(ids);
	}

	/**
	 * 根据列删除实体
	 * @param column				数据库表列名
	 * @param columnVal				列值
	 */
	public void remove(String column, Object columnVal)
	{
		remove(new Condition(column, columnVal));
	}

	/**
	 * 根据条件删除实体
	 * @param conds					删除条件
	 */
	public void remove(Condition cond)
	{
		List<Condition> conds = new ArrayList<Condition>(1);
		conds.add(cond);
		this.remove(conds);
	}

	/**
	 * 根据条件删除实体
	 * @param conds					删除条件
	 */
	public void remove(List<Condition> conds)
	{
		List<Object> ids = this.getIdList(conds, null);
		this.removeById(ids);
	}

	/**
	 * 批量删除实体
	 * @param ids					实体主键Id集合
	 * @throws Exception
	 */
	public abstract void removeById(List<Object> ids);

	/**
	 * 更新实体部分字段
	 * @param id					实体主键Id
	 * @param val					实体更新参数
	 */
	public void update(Object id, Value val)
	{
		List<Value> vals = new ArrayList<>(1);
		vals.add(val);
		update(id, vals);
	}

	/**
	 * 更新实体部分字段
	 * @param id					实体主键Id
	 * @param vals					实体更新参数
	 */
	public void update(Object id, List<Value> vals)
	{
		List<Object> ids = new ArrayList<>(1);
		ids.add(id);
		this.updateById(ids, vals);
	}

	/**long
	 * 根据数据库表列名更新部分字段
	 * @param column				数据库表列名
	 * @param columnVal				列值
	 * @param val					实体更新参数
	 */
	public void update(String column, Object columnVal, Value val)
	{
		List<Value> vals = new ArrayList<>(1);
		vals.add(val);
		update(column, columnVal, vals);
	}

	/**
	 * 根据数据库表列名更新部分字段
	 * @param column				数据库表列名
	 * @param columnVal				列值
	 * @param vals					实体更新参数
	 */
	public void update(String column, Object columnVal, List<Value> vals)
	{
		List<Condition> conds = new ArrayList<>(1);
		conds.add(new Condition(column, columnVal));
		update(conds, vals);
	}

	/**
	 * 根据组合条件更新部分字段
	 * @param conds					更新条件
	 * @param val					实体更新参数
	 */
	public void update(Condition cond, Value val)
	{
		List<Condition> conds = new ArrayList<>(1);
		conds.add(cond);
		update(conds, val);
	}

	/**
	 * 根据组合条件更新部分字段
	 * @param conds					更新条件
	 * @param val					实体更新参数
	 */
	public void update(List<Condition> conds, Value val)
	{
		List<Value> vals = new ArrayList<>(1);
		vals.add(val);
		update(conds, vals);
	}

	/**
	 * 根据组合条件更新部分字段
	 * @param conds					更新条件
	 * @param vals					实体更新参数
	 */
	public void update(List<Condition> conds, List<Value> vals)
	{
		List<Object> ids = this.getIdList(conds, null);
		this.updateById(ids, vals);
	}

	/**
	 * 更新实体全部字段
	 * @param entity				实体对象
	 */
	public void update(E entity)
	{
		Object id = entity.getKey();
		Map<String, Object> obj = entity.getValues();
		int length = obj.size();
		List<Value> vals = new ArrayList<Value>(length);
		for (String key : obj.keySet())
		{
			vals.add(new Value(key, obj.get(key)));
		}
		this.update(id, vals);
	}

	/**
	 * 更新实体部分字段
	 * @param id					实体主键Id
	 * @param vals					实体更新参数
	 */
	public abstract void updateById(List<Object> ids, List<Value> vals);

	/**
	 * 根据主键Id查询实体全表字段
	 * @param id					主键Id
	 */
	public E loadById(Object id)
	{
		List<Object> ids = new ArrayList<>(1);
		ids.add(id);
		List<E> items = this.loadByIds(ids);
		if (items.size() > 0)
		{
			return items.get(0);
		}
		return null;
	}

	/**
	 * 根据主键Id集合查询实体全表字段
	 * @param ids					主键Id集合
	 */
	public abstract List<E> loadByIds(List<Object> ids);

	/**
	 * 根据数据库表列名查询实体全表字段
	 * @param column				数据库表列名
	 * @param columnVal				列值
	 */
	public E loadByColumn(String column, Object columnVal)
	{
		Condition cond = new Condition(column, columnVal);
		return loadByCond(cond);
	}

	/**
	 * 根据数据库条件查询实体全表字段
	 * @param conds					条件
	 * @param columnVal				列值
	 */
	public E loadByCond(Condition cond)
	{
		List<Condition> conds = new ArrayList<>(1);
		conds.add(cond);
		return this.loadByCond(conds);
	}

	/**
	 * 根据数据库条件查询实体全表字段
	 * @param conds					条件
	 * @param columnVal				列值
	 */
	public E loadByCond(List<Condition> conds)
	{
		List<Object> ids = this.getIdList(conds, null);
		if (ids.size() > 0)
		{
			return this.loadById(ids.get(0));
		}
		return null;
	}

	/**
	 * 根据数据库表列名查询实体集合全表字段
	 * @param column				数据库表列名
	 * @param columnVal				列值
	 */
	public List<E> getListByColumn(String column, Object columnVal)
	{
		List<Order> orders = null;
		return getListByColumn(column, columnVal, orders);
	}

	/**
	 * 根据数据库表列名查询实体集合全表字段
	 * @param column				数据库表列名
	 * @param columnVal				列值
	 * @param order					排序
	 */
	public List<E> getListByColumn(String column, Object columnVal, Order order)
	{
		List<Order> orders = null;
		if (order != null)
		{
			orders = new ArrayList<>(1);
			orders.add(order);
		}
		return getListByColumn(column, columnVal, orders);
	}

	/**
	 * 根据数据库表列名查询实体集合全表字段
	 * @param column				数据库表列名
	 * @param columnVal				列值
	 * @param orders				排序列表
	 */
	public List<E> getListByColumn(String column, Object columnVal, List<Order> orders)
	{
		List<Condition> conds = new ArrayList<>(1);
		conds.add(new Condition(column, columnVal));
		return getListByCond(conds, orders);
	}

	/**
	 * 根据多条件查询实体集合全表字段
	 * @param conds					查询条件
	 */
	public List<E> getListByCond(Condition cond)
	{
		List<Condition> conds = new ArrayList<>(1);
		conds.add(cond);
		return getListByCond(conds);
	}

	/**
	 * 根据多条件查询实体集合全表字段
	 * @param conds					查询条件
	 */
	public List<E> getListByCond(List<Condition> conds)
	{
		Order order = null;
		return getListByCond(conds, order);
	}

	/**
	 * 根据多条件查询实体集合全表字段
	 * @param conds					查询条件
	 * @param order					排序
	 */
	public List<E> getListByCond(List<Condition> conds, Order order)
	{
		List<Order> orders = null;
		if (order != null)
		{
			orders = new ArrayList<>(1);
			orders.add(order);
		}
		return getListByCond(conds, orders);
	}

	/**
	 * 根据多条件查询实体集合全表字段
	 * @param conds					查询条件
	 * @param orders				拍序列
	 */
	public List<E> getListByCond(List<Condition> conds, List<Order> orders)
	{
		List<Object> ids = this.getIdList(conds, orders);
		return this.loadByIds(ids);
	}

	/**
	 * 根据多条件分页查询实体全部字段
	 * @param column				数据库列
	 * @param columnVal				列值
	 * @param order					排序
	 * @param pageNo				页码
	 * @param pageSize				页大小
	 * @param totalrecords			总记录数(如果不为-1，则不再进行统计)
	 */
	public PageData<E> getListByPage(String column, Object columnVal, Order order, int pageNo, int pageSize, int totalrecords)
	{
		Condition cond = new Condition(column, columnVal);
		return getListByPage(cond, order, pageNo, pageSize, totalrecords);
	}

	/**
	 * 根据多条件分页查询实体全部字段
	 * @param conds					查询条件
	 * @param order					排序
	 * @param pageNo				页码
	 * @param pageSize				页大小
	 * @param totalrecords			总记录数(如果不为-1，则不再进行统计)
	 */
	public PageData<E> getListByPage(Condition cond, Order order, int pageNo, int pageSize, int totalrecords)
	{
		List<Order> orders = null;
		if (order != null)
		{
			orders = new ArrayList<Order>(1);
			orders.add(order);
		}
		List<Condition> conds = new ArrayList<>(1);
		conds.add(cond);
		return getListByPage(conds, orders, pageNo, pageSize, totalrecords);
	}

	/**
	 * 根据多条件分页查询实体全部字段
	 * @param conds					查询条件
	 * @param order					排序
	 * @param pageNo				页码
	 * @param pageSize				页大小
	 * @param totalrecords			总记录数(如果不为-1，则不再进行统计)
	 */
	public PageData<E> getListByPage(List<Condition> conds, Order order, int pageNo, int pageSize, int totalrecords)
	{
		List<Order> orders = null;
		if (order != null)
		{
			orders = new ArrayList<Order>(1);
			orders.add(order);
		}
		return getListByPage(conds, orders, pageNo, pageSize, totalrecords);
	}

	/**
	 * 根据多条件分页查询实体部分字段
	 * @param conds					查询条件
	 * @param orders				拍序列
	 * @param pageNo				页码
	 * @param pageSize				页大小
	 * @param totalrecords			总记录数(如果不为-1，则不再进行统计)
	 */
	public PageData<E> getListByPage(List<Condition> conds, List<Order> orders, int pageNo, int pageSize, int totalrecords)
	{
		int start = (pageNo - 1) * pageSize;
		List<Object> ids = this.getIdList(conds, orders, start, pageSize);

		PageData<E> pd = new PageData<E>();
		pd.setPageNo(pageNo);
		pd.setDatas(this.loadByIds(ids));
		if (totalrecords != -1)
		{
			pd.setTotalrecords(totalrecords);
		}
		else
		{
			pd.setTotalrecords(this.count(conds));
		}
		return pd;
	}

	/**
	 * 执行statement语句,返回单行单列，不能使用缓存，不建议使用
	 * @param statement					statement语句
	 */
	public abstract Object executeScalar(Object statement);

	/**
	 * 执行statement语句,返回一行记录,不能使用缓存，不建议使用
	 * @param statement					statement语句
	 */
	public abstract Map<String, Object> executeMap(Object statement);

	/**
	 * 执行statement语句,返回List<Map>，不能使用缓存，不建议使用
	 * @param statement					statement语句
	 */
	public abstract List<Map<String, Object>> executeList(Object statement);

	/**
	 * 执行statement语句,返回List<Map>，不能使用缓存，不建议使用
	 * @param statement					statement语句
	 */
	public abstract List<E> executeEntityList(Object statement);
}
