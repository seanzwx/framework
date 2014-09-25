package com.sean.persist.core;

import java.util.List;
import java.util.Map;

import com.sean.persist.ext.Condition;
import com.sean.persist.ext.Order;
import com.sean.persist.ext.Value;

/**
 * 持久层接口
 * @author sean
 */
public class Dao
{
	/**
	 * 查询ID列表
	 * @param conds
	 * @param orders
	 * @return
	 */
	public static <E extends Entity> List<Object> getIdList(Class<E> entityCls, List<Condition> conds, List<Order> orders)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.getIdList(conds, orders);
	}

	/**
	 * 查询ID列表
	 * @param conds
	 * @param orders
	 * @param start
	 * @param limit
	 * @return
	 */
	public static <E extends Entity> List<Object> getIdList(Class<E> entityCls, List<Condition> conds, List<Order> orders,
			int start, int limit)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.getIdList(conds, orders, start, limit);
	}

	/**
	 * 统计记录数
	 * @param conds
	 * @param orders
	 * @return
	 */
	public static <E extends Entity> int count(Class<E> entityCls, List<Condition> conds)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.count(conds);
	}

	/**
	 * 统计记录数
	 * @param conds
	 * @param orders
	 * @return
	 */
	public static <E extends Entity> int count(Class<E> entityCls, Condition cond)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.count(cond);
	}

	/**
	 * 持久化实体
	 * @param entity				实体对象
	 */
	public static <E extends Entity> void persist(Class<E> entityCls, E entity)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.persist(entity);
	}

	/**
	 * 批量持久化尸体
	 * @param entitys				实体集合
	 */
	public static <E extends Entity> void persistBatch(Class<E> entityCls, List<E> entitys)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.persistBatch(entitys);
	}

	/**
	 * 根据主键Id删除实体
	 * @param id					实体主键Id
	 */
	public static <E extends Entity> void remove(Class<E> entityCls, Object id)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.remove(id);
	}

	/**
	 * 根据列删除实体
	 * @param column				数据库表列名
	 * @param columnVal				列值
	 */
	public static <E extends Entity> void remove(Class<E> entityCls, String column, Object columnVal)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.remove(column, columnVal);
	}

	/**
	 * 根据条件删除实体
	 * @param conds					删除条件
	 */
	public static <E extends Entity> void remove(Class<E> entityCls, Condition cond)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.remove(cond);
	}

	/**
	 * 根据条件删除实体
	 * @param conds					删除条件
	 */
	public static <E extends Entity> void remove(Class<E> entityCls, List<Condition> conds)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.remove(conds);
	}

	/**
	 * 批量删除实体
	 * @param ids					实体主键Id集合
	 * @throws Exception
	 */
	public static <E extends Entity> void removeById(Class<E> entityCls, List<Object> ids)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.removeById(ids);
	}

	/**
	 * 更新实体部分字段
	 * @param id					实体主键Id
	 * @param val					实体更新参数
	 */
	public static <E extends Entity> void update(Class<E> entityCls, Object id, Value val)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.update(id, val);
	}

	/**
	 * 更新实体部分字段
	 * @param id					实体主键Id
	 * @param vals					实体更新参数
	 */
	public static <E extends Entity> void update(Class<E> entityCls, Object id, List<Value> vals)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.update(id, vals);
	}

	/**long
	 * 根据数据库表列名更新部分字段
	 * @param column				数据库表列名
	 * @param columnVal				列值
	 * @param val					实体更新参数
	 */
	public static <E extends Entity> void update(Class<E> entityCls, String column, Object columnVal, Value val)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.update(column, columnVal, val);
	}

	/**
	 * 根据数据库表列名更新部分字段
	 * @param column				数据库表列名
	 * @param columnVal				列值
	 * @param vals					实体更新参数
	 */
	public static <E extends Entity> void update(Class<E> entityCls, String column, Object columnVal, List<Value> vals)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.update(column, columnVal, vals);
	}

	/**
	 * 根据组合条件更新部分字段
	 * @param conds					更新条件
	 * @param val					实体更新参数
	 */
	public static <E extends Entity> void update(Class<E> entityCls, Condition cond, Value val)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.update(cond, val);
	}

	/**
	 * 根据组合条件更新部分字段
	 * @param conds					更新条件
	 * @param val					实体更新参数
	 */
	public static <E extends Entity> void update(Class<E> entityCls, List<Condition> conds, Value val)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.update(conds, val);
	}

	/**
	 * 根据组合条件更新部分字段
	 * @param conds					更新条件
	 * @param vals					实体更新参数
	 */
	public static <E extends Entity> void update(Class<E> entityCls, List<Condition> conds, List<Value> vals)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.update(conds, vals);
	}

	/**
	 * 更新实体全部字段
	 * @param entity				实体对象
	 */
	public static <E extends Entity> void update(Class<E> entityCls, E entity)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.update(entity);
	}

	/**
	 * 更新实体部分字段
	 * @param id					实体主键Id
	 * @param vals					实体更新参数
	 */
	public static <E extends Entity> void updateById(Class<E> entityCls, List<Object> ids, List<Value> vals)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		dao.updateById(ids, vals);
	}

	/**
	 * 根据主键Id查询实体全表字段
	 * @param id					主键Id
	 */
	public static <E extends Entity> E loadById(Class<E> entityCls, Object id)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.loadById(id);
	}

	/**
	 * 根据主键Id集合查询实体全表字段
	 * @param ids					主键Id集合
	 */
	public static <E extends Entity> List<E> loadByIds(Class<E> entityCls, List<Object> ids)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.loadByIds(ids);
	}

	/**
	 * 根据数据库表列名查询实体全表字段
	 * @param column				数据库表列名
	 * @param columnVal				列值
	 */
	public static <E extends Entity> E loadByColumn(Class<E> entityCls, String column, Object columnVal)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.loadByColumn(column, columnVal);
	}

	/**
	 * 根据数据库条件查询实体全表字段
	 * @param conds					条件
	 * @param columnVal				列值
	 */
	public static <E extends Entity> E loadByCond(Class<E> entityCls, Condition cond)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.loadByCond(cond);
	}

	/**
	 * 根据数据库条件查询实体全表字段
	 * @param conds					条件
	 * @param columnVal				列值
	 */
	public static <E extends Entity> E loadByCond(Class<E> entityCls, List<Condition> conds)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.loadByCond(conds);
	}

	/**
	 * 根据数据库表列名查询实体集合全表字段
	 * @param column				数据库表列名
	 * @param columnVal				列值
	 */
	public static <E extends Entity> List<E> getListByColumn(Class<E> entityCls, String column, Object columnVal)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.getListByColumn(column, columnVal);
	}

	/**
	 * 根据数据库表列名查询实体集合全表字段
	 * @param column				数据库表列名
	 * @param columnVal				列值
	 * @param order					排序
	 */
	public static <E extends Entity> List<E> getListByColumn(Class<E> entityCls, String column, Object columnVal, Order order)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.getListByColumn(column, columnVal, order);
	}

	/**
	 * 根据数据库表列名查询实体集合全表字段
	 * @param column				数据库表列名
	 * @param columnVal				列值
	 * @param orders				排序列表
	 */
	public static <E extends Entity> List<E> getListByColumn(Class<E> entityCls, String column, Object columnVal,
			List<Order> orders)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.getListByColumn(column, columnVal, orders);
	}

	/**
	 * 根据多条件查询实体集合全表字段
	 * @param conds					查询条件
	 */
	public static <E extends Entity> List<E> getListByCond(Class<E> entityCls, Condition cond)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.getListByCond(cond);
	}

	/**
	 * 根据多条件查询实体集合全表字段
	 * @param conds					查询条件
	 */
	public static <E extends Entity> List<E> getListByCond(Class<E> entityCls, List<Condition> conds)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.getListByCond(conds);
	}

	/**
	 * 根据多条件查询实体集合全表字段
	 * @param conds					查询条件
	 * @param order					排序
	 */
	public static <E extends Entity> List<E> getListByCond(Class<E> entityCls, List<Condition> conds, Order order)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.getListByCond(conds, order);
	}

	/**
	 * 根据多条件查询实体集合全表字段
	 * @param conds					查询条件
	 * @param orders				拍序列
	 */
	public static <E extends Entity> List<E> getListByCond(Class<E> entityCls, List<Condition> conds, List<Order> orders)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.getListByCond(conds, orders);
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
	public static <E extends Entity> PageData<E> getListByPage(Class<E> entityCls, String column, Object columnVal, Order order,
			int pageNo, int pageSize, int totalrecords)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.getListByPage(column, columnVal, order, pageNo, pageSize, totalrecords);
	}

	/**
	 * 根据多条件分页查询实体全部字段
	 * @param conds					查询条件
	 * @param order					排序
	 * @param pageNo				页码
	 * @param pageSize				页大小
	 * @param totalrecords			总记录数(如果不为-1，则不再进行统计)
	 */
	public static <E extends Entity> PageData<E> getListByPage(Class<E> entityCls, Condition cond, Order order, int pageNo,
			int pageSize, int totalrecords)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.getListByPage(cond, order, pageNo, pageSize, totalrecords);
	}

	/**
	 * 根据多条件分页查询实体全部字段
	 * @param conds					查询条件
	 * @param order					排序
	 * @param pageNo				页码
	 * @param pageSize				页大小
	 * @param totalrecords			总记录数(如果不为-1，则不再进行统计)
	 */
	public static <E extends Entity> PageData<E> getListByPage(Class<E> entityCls, List<Condition> conds, Order order,
			int pageNo, int pageSize, int totalrecords)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.getListByPage(conds, order, pageNo, pageSize, totalrecords);
	}

	/**
	 * 根据多条件分页查询实体全部字段
	 * @param conds					查询条件
	 * @param orders				拍序列
	 * @param pageNo				页码
	 * @param pageSize				页大小
	 * @param totalrecords			总记录数(如果不为-1，则不再进行统计)
	 */
	public static <E extends Entity> PageData<E> getListByPage(Class<E> entityCls, List<Condition> conds, List<Order> orders,
			int pageNo, int pageSize, int totalrecords)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.getListByPage(conds, orders, pageNo, pageSize, totalrecords);
	}

	/**
	 * 执行statement语句,返回单行单列，不能使用缓存，不建议使用
	 * @param statement					statement语句
	 */
	public static <E extends Entity> Object executeScalar(Class<E> entityCls, Object statement)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.executeScalar(statement);
	}

	/**
	 * 执行statement语句,返回一行记录,不能使用缓存，不建议使用
	 * @param statement					statement语句
	 */
	public static <E extends Entity> Map<String, Object> executeMap(Class<E> entityCls, Object statement)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.executeMap(statement);
	}

	/**
	 * 执行statement语句,返回List<Map>，不能使用缓存，不建议使用
	 * @param statement					statement语句
	 */
	public static <E extends Entity> List<Map<String, Object>> executeList(Class<E> entityCls, Object statement)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.executeList(statement);
	}

	/**
	 * 执行statement语句,返回List<Map>，不能使用缓存，不建议使用
	 * @param statement					statement语句
	 */
	public static <E extends Entity> List<E> executeEntityList(Class<E> entityCls, Object statement)
	{
		EntityDao<E> dao = PersistContext.CTX.getEntityDao(entityCls);
		return dao.executeEntityList(statement);
	}
}
