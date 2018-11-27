package io.mosip.registration.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class TemplateCommonFields {
	
	protected String descr;
	@Column(name="is_active")
	protected Boolean isActive;
	@Column(name="cr_by")
	protected String crBy;
	@Column(name="cr_dtimes")
	protected Date crDtimes;
	@Column(name="upd_by")
	protected String updBy;
	@Column(name="upd_dtimes")
	protected Date updDtimes;
	@Column(name="is_deleted")
	protected Boolean isDeleted;
	@Column(name="del_dtimes")
	protected Date delDtimes;
	
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public Boolean isActive() {
		return isActive;
	}
	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public String getCrBy() {
		return crBy;
	}
	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}
	public Date getCrDtimes() {
		return crDtimes;
	}
	public void setCrDtimes(Date crDtimes) {
		this.crDtimes = crDtimes;
	}
	public String getUpdBy() {
		return updBy;
	}
	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}
	public Date getUpdDtimes() {
		return updDtimes;
	}
	public void setUpdDtimes(Date updDtimes) {
		this.updDtimes = updDtimes;
	}
	public Boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public Date getDelDtimes() {
		return delDtimes;
	}
	public void setDelDtimes(Date delDtimes) {
		this.delDtimes = delDtimes;
	}
	
}