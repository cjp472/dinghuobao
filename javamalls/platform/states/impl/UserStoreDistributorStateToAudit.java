package com.javamalls.platform.states.impl;

import com.javamalls.payment.weixin.vo.ResultMsg;
import com.javamalls.platform.states.UserStoreDistributorState;
import com.javamalls.platform.states.UserStoreDistributorVO;

public class UserStoreDistributorStateToAudit implements UserStoreDistributorState{
	
	public  UserStoreDistributorVO vo;
	
	public UserStoreDistributorStateToAudit(UserStoreDistributorVO para){
		vo = para;
	}

	@Override
	public ResultMsg apply() {
		System.out.println("已经提交过申请，请等待审核结果");
		return null;
	}

	@Override
	public ResultMsg auditPass() {
		vo.setUserStoreDistributorState(vo.getPassed());;//设置通过
		System.out.println("审核通过成功");
		return null;
	}

	@Override
	public ResultMsg auditFail() {
		vo.setUserStoreDistributorState(vo.getFailed());;//设置拒绝
		System.out.println("审核拒绝成功");
		return null;
	}

}
