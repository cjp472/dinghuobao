package com.javamalls.platform.states.impl;

import com.javamalls.payment.weixin.vo.ResultMsg;
import com.javamalls.platform.states.UserStoreDistributorState;
import com.javamalls.platform.states.UserStoreDistributorVO;

public class UserStoreDistributorStateFailed implements UserStoreDistributorState{

	public  UserStoreDistributorVO vo;
	public UserStoreDistributorStateFailed(UserStoreDistributorVO para){
		vo = para;
	}
	
	@Override
	public ResultMsg apply() {
		System.out.println("申请已经拒绝，不要重复申请");
		return null;
	}

	@Override
	public ResultMsg auditPass() {
		System.out.println("申请已经拒绝，不要重复审核");
		return null;
	}

	@Override
	public ResultMsg auditFail() {
		System.out.println("申请已经拒绝，不要重复审核");
		return null;
	}

}
