package com.sean.config.enums;

/**
 * 配置文件枚举
 * @author sean
 */
public enum ConfigEnum
{	
	// index配置项
	IndexRamSizeMB("index_ram_sizeMB"),
	IndexStaleSize("index_stale_size"),
	
	// persist配置项
	PersistCachePersist("persist.cache.persist"),				// 持久层缓存存储类型ehcache/redis
	PersistCacheRedisHostname("persist.cache.redis.hostname"),	// 持久层redis地址
	PersistCacheRedisPort("persist.cache.redis.port"),			// 持久层redis端口
	
	// service配置项
	ProjectName("project.name"),								// 应用名称
	ServicePackageProfix("service.package.prefix"),				// 扫描包前缀
	ServiceMode("service.mode"),								// 运行模式:dev/pseudo
	
	// push配置项
	PushQueueHostname("push.queue.hostname"),					// 推送消息队列地址
	PushQueuePort("push.queue.port"),							// 推送消息队列端口
	PushQueueUser("push.queue.user"),							// 推送消息队列用户
	PushQueuePassword("push.queue.password"),					// 推送消息队列密码
	PushConsumerAmount("push.consumer.amount"),					// 推送单台服务器消费者数量
	
	PushHostHostname("push.host.hostname"),						// 推送链接主机地址
	PushHostMsgPort("push.host.msg.port"),						// 推送链接主机端口
	PushHostSockPort("push.host.sock.port"),					// 推送主机socket端口
	PushHostWebsockPort("push.host.websock.port"),				// 推送主机websocket端口
	
	// msgcenter配置项
	MsgCenterQueueHostname("msgcenter.queue.hostname"),			// 消息中心消息队列地址
	MsgCenterQueuePort("msgcenter.queue.port"),					// 消息中心消息队列端口
	MsgCenterQueueUser("msgcenter.queue.user"),					// 消息中心消息队列用户
	MsgCenterQueuePassword("msgcenter.queue.password"),			// 消息中心消息队列密码
	MsgCenterConsumerAmount("msgcenter.consumer.amount"),		// 消息中心单台服务器消费者数量
	
	// registry注册服务配置项
	ZKHostname("zookeeper.hostname"),							// 服务注册地址
	ZKPort("zookeeper.port");									// 服务注册端口
	
	private String name;
	
	ConfigEnum(String name)
	{
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
