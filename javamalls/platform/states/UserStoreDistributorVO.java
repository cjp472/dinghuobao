package com.javamalls.platform.states;

import com.javamalls.payment.weixin.vo.ResultMsg;
import com.javamalls.platform.domain.UserStoreDistributor;
import com.javamalls.platform.states.impl.UserStoreDistributorStateFailed;
import com.javamalls.platform.states.impl.UserStoreDistributorStatePassed;
import com.javamalls.platform.states.impl.UserStoreDistributorStateToAudit;

public class UserStoreDistributorVO extends UserStoreDistributor{
	
	private static final long serialVersionUID = 1L;
	private  UserStoreDistributorStateFailed failed;
	private  UserStoreDistributorStatePassed passed;
	private  UserStoreDistributorStateToAudit toAudit;
	
	//当前状态
	private UserStoreDistributorState userStoreDistributorState = toAudit;
	
	
	public UserStoreDistributorVO(){
		failed = new UserStoreDistributorStateFailed(this);
		passed = new UserStoreDistributorStatePassed(this); 
		toAudit= new UserStoreDistributorStateToAudit(this);
		
		userStoreDistributorState = toAudit;
		
	}
	
	
    public ResultMsg apply(){
    	userStoreDistributorState.apply();
    	return null;
    }
	
	public ResultMsg auditPass(){
		userStoreDistributorState.auditPass();
    	return null;
	}
	
	public ResultMsg auditFail(){
		userStoreDistributorState.auditFail();
    	return null;
	}


	public UserStoreDistributorState getUserStoreDistributorState() {
		return userStoreDistributorState;
	}


	public void setUserStoreDistributorState(UserStoreDistributorState userStoreDistributorState) {
		this.userStoreDistributorState = userStoreDistributorState;
	}

	public UserStoreDistributorStateFailed getFailed() {
		return failed;
	}

	public UserStoreDistributorStatePassed getPassed() {
		return passed;
	}

	public UserStoreDistributorStateToAudit getToAudit() {
		return toAudit;
	}
	
	
	

}
