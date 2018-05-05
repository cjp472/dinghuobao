package com.javamalls.platform.states;

public class Test {
	
	
	public static void main(String[] args) {
		UserStoreDistributorVO vo = new UserStoreDistributorVO();
		
		vo.apply();
		vo.auditPass();
		vo.auditPass();
		vo.auditFail();
		
	}

}
