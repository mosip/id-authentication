package org.mosip.registration.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * RegistrationUserDetail entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema="reg", name = "user_detail")
public class RegistrationUserDetail extends RegistrationCommonFields {	
	@Id
	@Column(name="id", length=64, nullable=false, updatable=false)
	private String id;
	@Column(name="name", length=64, nullable=false, updatable=false)
	private String name;
	@Column(name="email", length=64, nullable=true, updatable=false)
	private String email;
	@Column(name="mobile", length=16, nullable=true, updatable=false)
	private String mobile;
	@Column(name="cntr_id", length=28, nullable=false, updatable=false)
	private String cntrId;
	@Column(name="lang_code", length=3, nullable=false, updatable=false)
	private String langCode;
	@Column(name="last_login_dtimes", nullable=true, updatable=false)
	private OffsetDateTime lastLoginDtimes;
	@Column(name="last_login_method", length=64, nullable=true, updatable=false)
	private String lastLoginMethod;	
	@Column(name="is_deleted", nullable=false, updatable=false)
	@Type(type= "true_false")
	private boolean isDeleted;
	@Column(name="del_dtimes", nullable=true, updatable=false)
	private OffsetDateTime delDtimes;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCntrId() {
		return cntrId;
	}
	public void setCntrId(String cntrId) {
		this.cntrId = cntrId;
	}
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	public OffsetDateTime getLastLoginDtimes() {
		return lastLoginDtimes;
	}
	public void setLastLoginDtimes(OffsetDateTime lastLoginDtimes) {
		this.lastLoginDtimes = lastLoginDtimes;
	}
	public String getLastLoginMethod() {
		return lastLoginMethod;
	}
	public void setLastLoginMethod(String lastLoginMethod) {
		this.lastLoginMethod = lastLoginMethod;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public OffsetDateTime getDelDtimes() {
		return delDtimes;
	}
	public void setDelDtimes(OffsetDateTime delDtimes) {
		this.delDtimes = delDtimes;
	}
	
}
