package com.javamalls.platform.vo;

import java.util.Date;

public class GoodsSpecPropertyJsonVo {
	    private Long              id;
	    private Date              createtime;
	    private boolean           disabled;
	    private int                sequence;
	    private String             value;
	    private Long spec_id;
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
		public int getSequence() {
			return sequence;
		}
		public void setSequence(int sequence) {
			this.sequence = sequence;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public Long getSpec_id() {
			return spec_id;
		}
		public void setSpec_id(Long spec_id) {
			this.spec_id = spec_id;
		}
	    
}
