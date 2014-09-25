package com.sean.ds.service;

import org.jboss.netty.channel.Channel;

/**
 * 客户端监听事件
 * @author sean
 */
public interface ClientListener
{
	/**
	 * 新客户端连接
	 * @param client
	 */
	public void onConnected(Channel client);

	/**
	 * 客户端断开
	 * @param client
	 */
	public void onDisconnected(Channel client);
}
