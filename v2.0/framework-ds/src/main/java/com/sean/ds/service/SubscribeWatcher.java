package com.sean.ds.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

import com.alibaba.fastjson.JSON;
import com.sean.ds.constant.L;
import com.sean.log.core.LogFactory;

public final class SubscribeWatcher implements Watcher
{
	private static final Logger logger = LogFactory.getLogger(L.Ds);
	private ZooKeeper zk;

	public void setZooKeeper(ZooKeeper zk)
	{
		this.zk = zk;
	}

	@Override
	public void process(WatchedEvent event)
	{
		EventType type = event.getType();
		// 新增节点， 即新服务实例发布
		if (type == EventType.NodeCreated)
		{
			System.out.println(event.getPath() + "---NodeCreated");
		}
		// 节点删除， 即服务实例失效
		else if (type == EventType.NodeDeleted)
		{
			System.out.println(event.getPath() + "---NodeDeleted");
		}
		// 节点数据改变， 即服务实例状态改变
		else if (type == EventType.NodeDataChanged)
		{
			System.out.println(event.getPath() + "---NodeDataChanged");
		}
		// 子节点改变, 即新服务实例发布/服务实例失效
		else if (type == EventType.NodeChildrenChanged)
		{
			String path = event.getPath();
			try
			{
				String json = new String(zk.getData(path, false, null));
				ServiceDefine define = JSON.parseObject(json, ServiceDefine.class);

				List<String> paths = zk.getChildren(path, true);
				List<ServiceInstance> instances = new ArrayList<>(paths.size());
				for (String p : paths)
				{
					json = new String(zk.getData(path + "/" + p, false, null));
					ServiceInstance instance = JSON.parseObject(json, ServiceInstance.class);
					// 如果服务在线
					if (instance.status == ServiceInstance.Status_Online)
					{
						instances.add(instance);
					}
				}

				// 更新服务实例列表
				LocalService.updateServiceInstances(define.serviceName, instances);
				logger.debug("更新服务成功: " + define + ", instances: " + instances);
			}
			catch (Exception e)
			{
				logger.error("更新服务" + path + "异常: " + e.getMessage(), e);
			}
		}
	}
}
