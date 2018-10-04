package io.mosip.registration.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import io.mosip.registration.entity.RegistrationCommonFields;

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
	@Column(name="user_status", nullable=true, updatable=false)
	private String userStatus;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	/**
	 * @return the cntrId
	 */
	public String getCntrId() {
		return cntrId;
	}
	/**
	 * @param cntrId the cntrId to set
	 */
	public void setCntrId(String cntrId) {
		this.cntrId = cntrId;
	}
	/**
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}
	/**
	 * @param langCode the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	/**
	 * @return the lastLoginDtimes
	 */
	public OffsetDateTime getLastLoginDtimes() {
		return lastLoginDtimes;
	}
	/**
	 * @param lastLoginDtimes the lastLoginDtimes to set
	 */
	public void setLastLoginDtimes(OffsetDateTime lastLoginDtimes) {
		this.lastLoginDtimes = lastLoginDtimes;
	}
	/**
	 * @return the lastLoginMethod
	 */
	public String getLastLoginMethod() {
		return lastLoginMethod;
	}
	/**
	 * @param lastLoginMethod the lastLoginMethod to set
	 */
	public void setLastLoginMethod(String lastLoginMethod) {
		this.lastLoginMethod = lastLoginMethod;
	}
	/**
	 * @return the isDeleted
	 */
	public boolean isDeleted() {
		return isDeleted;
	}
	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	/**
	 * @return the delDtimes
	 */
	public OffsetDateTime getDelDtimes() {
		return delDtimes;
	}
	/**
	 * @param delDtimes the delDtimes to set
	 */
	public void setDelDtimes(OffsetDateTime delDtimes) {
		this.delDtimes = delDtimes;
	}
	/**
	 * @return the userStatus
	 */
	public String getUserStatus() {
		return userStatus;
	}
	/**
	 * @param userStatus the userStatus to set
	 */
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
		
}
