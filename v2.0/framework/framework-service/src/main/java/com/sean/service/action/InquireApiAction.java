package com.sean.service.action;

import com.sean.service.annotation.ActionConfig;
import com.sean.service.annotation.DescriptConfig;
import com.sean.service.annotation.MustParamsConfig;
import com.sean.service.annotation.ReturnParamsConfig;
import com.sean.service.core.Action;
import com.sean.service.core.DocManager;
import com.sean.service.core.Session;

@ActionConfig(authenticate = false, module = _M.class)
@MustParamsConfig({ _P.action, _P.version })
@ReturnParamsConfig({ _RP.api })
@DescriptConfig("读取api")
public final class InquireApiAction extends Action
{
	@Override
	public void execute(Session session) throws Exception
	{
		String action = session.getParameter(_P.action);
		String version = session.getParameter(_P.version);
		session.setReturnAttribute(_RP.api, DocManager.inquireAction(action, version));
		session.success();
	}
}
