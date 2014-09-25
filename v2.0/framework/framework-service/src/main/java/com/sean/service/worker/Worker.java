package com.sean.service.worker;

import com.sean.service.core.Action;
import com.sean.service.core.Session;

/**
 * 工作节点
 * @author sean
 */
public interface Worker
{
	public void work(Session session, Action action) throws Exception;
}
