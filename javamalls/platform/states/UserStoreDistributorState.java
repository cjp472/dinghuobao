package com.javamalls.platform.states;
//申请店铺分销商状态接口

import com.javamalls.payment.weixin.vo.ResultMsg;

public interface UserStoreDistributorState {
	
	public ResultMsg apply();
	
	public ResultMsg auditPass();
	
	public ResultMsg auditFail();

}
