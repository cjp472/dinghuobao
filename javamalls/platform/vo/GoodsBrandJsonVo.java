package com.javamalls.platform.vo;

import java.util.Date;


public class GoodsBrandJsonVo {
	    private Long              id;
	    private Date              createtime;
	    private boolean           disabled;
	    private String             name;
	    private int                userStatus;
	    private String             remark;
	    private String             first_word;
	    private Long storeId;
	    private String brandLogo;
	    
		public String getBrandLogo() {
			return brandLogo;
		}
		public void setBrandLogo(String brandLogo) {
			this.brandLogo = brandLogo;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Date getCreatetime() {
			return createtime;
		}
		public void setCreatetime(Date createtime) {
			this.createtime = createtime;
		}
		public boolean isDisabled() {
			return disabled;
		}
		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public int getUserStatus() {
			return userStatus;
		}
		public void setUserStatus(int userStatus) {
			this.userStatus = userStatus;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		public String getFirst_word() {
			return first_word;
		}
		public void setFirst_word(String first_word) {
			this.first_word = first_word;
		}
		public Long getStoreId() {
			return storeId;
		}
		public void setStoreId(Long storeId) {
			this.storeId = storeId;
		}
	    
}
