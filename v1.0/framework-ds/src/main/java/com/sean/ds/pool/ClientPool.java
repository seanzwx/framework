package com.sean.ds.pool;

/**
 * Avro连接池
 * @author sean
 * @param <E>
 */
public interface ClientPool<E>
{
	/**
	 * 申请对象
	 * @return
	 */
	public AvroClient<E> openClient();
	
	/**
	 * 归还对象
	 * @param client
	 */
	public void returnClient(AvroClient<E> client);
	
	/**
	 * 清空连接池
	 */
	public void clearAll();
	
	/**
	 * 销毁连接池
	 */
	public void destory();
}
