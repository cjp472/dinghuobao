package com.javamalls.platform.vo;

import java.util.Date;

public class UserGoodsClassJsonVo {
	 	private Long              id;
	    private Date              createtime;
	    private boolean           disabled;
	    private String className;
	    private boolean              display;
	    private int                  level;
	    private int                  sequence;
	    private Long parent_id;
	    private Long user_id;
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
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public boolean isDisplay() {
			return display;
		}
		public void setDisplay(boolean display) {
			this.display = display;
		}
		public int getLevel() {
			return level;
		}
		public void setLevel(int level) {
			this.level = level;
		}
		public int getSequence() {
			return sequence;
		}
		public void setSequence(int sequence) {
			this.sequence = sequence;
		}
		public Long getParent_id() {
			return parent_id;
		}
		public void setParent_id(Long parent_id) {
			this.parent_id = parent_id;
		}
		public Long getUser_id() {
			return user_id;
		}
		public void setUser_id(Long user_id) {
			this.user_id = user_id;
		}
	    
}
