package com.sean.service.core;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.sean.service.entity.ActionEntity;

/**
 * http writer 负责写入客户端
 * @author sean
 */
public interface HttpWriter
{
	public void write(ServletRequest request, ServletResponse response, Session session, ActionEntity action) throws Exception;
	
	public void writeJson(ServletRequest request, ServletResponse response, String json) throws Exception;
}
