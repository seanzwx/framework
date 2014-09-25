package com.sean.service.action;

import com.sean.service.annotation.ActionConfig;
import com.sean.service.annotation.DescriptConfig;
import com.sean.service.annotation.ReturnParamsConfig;
import com.sean.service.core.Action;
import com.sean.service.core.DocManager;
import com.sean.service.core.Session;

@ActionConfig(authenticate = false, module = _M.class)
@ReturnParamsConfig({ _RP.apiList })
@DescriptConfig("读取api列表")
public final class InquireApiListAction extends Action
{
	@Override
	public void execute(Session session) throws Exception
	{
		session.setReturnAttribute(_RP.apiList, DocManager.inquireActionList());
		session.success();
	}
}
