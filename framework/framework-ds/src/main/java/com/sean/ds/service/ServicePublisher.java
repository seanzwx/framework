package com.sean.ds.service;

import java.net.InetSocketAddress;

import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.alibaba.fastjson.JSON;
import com.sean.config.core.Config;
import com.sean.config.enums.ConfigEnum;
import com.sean.ds.constant.L;
import com.sean.log.core.LogFactory;

/**
 * 服务发布者
 * @author sean
 */
public final class ServicePublisher
{
	private static final Logger logger = LogFactory.getLogger(L.Ds);
	private static final String root = "services";
	private static ZooKeeper zk;
	static
	{
		try
		{
			String hostname = Config.getProperty(ConfigEnum.ZKHostname);
			int port = Integer.parseInt(Config.getProperty(ConfigEnum.ZKPort));
			zk = new ZooKeeper(hostname + ":" + port, 3000, new Watcher()
			{
				@Override
				public void process(WatchedEvent event)
				{
				}
			});
		}
		catch (Exception e)
		{
			logger.error("连接ZooKeeper异常:" + e.getMessage(), e);
		}
	}

	/**
	 * 发布服务
	 * @param service
	 * @param instance
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 */
	protected static void publishService(ServiceDefine define, Object serviceImpl, ServiceInstance instance) throws KeeperException,
			InterruptedException
	{
		String path = "/" + root;
		if (zk.exists(path, false) == null)
		{
			zk.create(path, root.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		path += "/" + define.serviceName;
		if (zk.exists(path, false) == null)
		{
			zk.create(path, define.toJson().getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		else
		{
			Stat stat = new Stat();
			zk.getData(path, false, stat);
			zk.setData(path, define.toJson().getBytes(), stat.getVersion());
		}
		path = define.getInstancePath(instance);

		if (zk.exists(path, false) == null)
		{
			// 初始化数据
			instance.status = ServiceInstance.Status_Online;
			define.reflectClass();

			// 启动服务
			Server server = new NettyServer(new SpecificResponder(define.getServiceClass(), serviceImpl), new InetSocketAddress(
					instance.hostname, instance.port));
			server.start();
			logger.debug(define.serviceName + " remote server started, listen on " + instance.hostname + ":" + instance.port);

			// 注册服务
			zk.create(path, instance.toJson().getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			logger.debug("成功发布服务实例" + path);

			zk.getData(path, true, null);
		}
		else
		{
			Stat stat = new Stat();
			byte[] data = zk.getData(path, true, stat);
			String json = new String(data);
			ServiceInstance tmp = JSON.parseObject(json, ServiceInstance.class);
			// 服务实例在线
			if (tmp.status == ServiceInstance.Status_Online)
			{
				throw new RuntimeException("创建服务实例" + path + "失败，原因重复发布已经存在的服务实例");
			}
			// 服务实例已经离线
			else
			{
				instance.status = ServiceInstance.Status_Online;
				define.reflectClass();

				// 启动服务
				Server server = new NettyServer(new SpecificResponder(define.getServiceClass(), serviceImpl), new InetSocketAddress(
						instance.hostname, instance.port));
				server.start();
				logger.info(define.serviceName + " remote server started, listen on " + instance.hostname + ":" + instance.port);

				// 修改服务实例状态
				zk.setData(path, instance.toJson().getBytes(), stat.getVersion());
				logger.debug("成功发布服务实例" + path);
			}
		}
	}
}
