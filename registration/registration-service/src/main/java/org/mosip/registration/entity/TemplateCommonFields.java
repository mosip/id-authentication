package org.mosip.registration.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;

@MappedSuperclass
public class TemplateCommonFields {
	
	protected String descr;
	@Column(name="is_active")
	@Type(type= "true_false")
	protected boolean isActive;
	protected String cr_by;
	protected Date cr_dtimes;
	protected String upd_by;
	protected Date upd_dtimes;
	protected boolean is_deleted;
	protected Date del_dtimes;
	
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public String getCr_by() {
		return cr_by;
	}
	public void setCr_by(String cr_by) {
		this.cr_by = cr_by;
	}
	public Date getCr_dtimes() {
		return cr_dtimes;
	}
	public void setCr_dtimes(Date cr_dtimes) {
		this.cr_dtimes = cr_dtimes;
	}
	public String getUpd_by() {
		return upd_by;
	}
	public void setUpd_by(String upd_by) {
		this.upd_by = upd_by;
	}
	public Date getUpd_dtimes() {
		return upd_dtimes;
	}
	public void setUpd_dtimes(Date upd_dtimes) {
		this.upd_dtimes = upd_dtimes;
	}
	public boolean isIs_deleted() {
		return is_deleted;
	}
	public void setIs_deleted(boolean is_deleted) {
		this.is_deleted = is_deleted;
	}
	public Date getDel_dtimes() {
		return del_dtimes;
	}
	public void setDel_dtimes(Date del_dtimes) {
		this.del_dtimes = del_dtimes;
	}
	
	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
}
