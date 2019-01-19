package io.mosip.registration.processor.quality.check.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
		
/**
 * The Class UserDetailEntity.
 *
 * @author M1048399 The persistent class for the user_detail database table.
 */
@Entity
@Table(name = "user_detail", schema = "master")
public class UserDetailEntity extends BaseQcuserEntity<UserDetailPKEntity> implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The cr by. */
	@Column(name = "cr_by", nullable = false)
	private String crBy = "MOSIP_SYSTEM";

	/** The cr dtimesz. */
	@Column(name = "cr_dtimesz", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	/** The del dtimesz. */
	@Column(name = "del_dtimesz")
	@CreationTimestamp
	private LocalDateTime delDtimesz;

	/** The email. */
	private String email;

	/** The is active. */
	@Column(name = "is_active")
	private Boolean isActive;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The lang code. */
	@Column(name = "lang_code")
	private String langCode;

	/** The mobile. */
	private String mobile;

	/** The name. */
	private String name;

	/** The uin ref id. */
	@Column(name = "uin_ref_id")
	private String uinRefId;

	/** The upd by. */
	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	/** The upd dtimesz. */
	@Column(name = "upd_dtimesz")
	@CreationTimestamp
	private LocalDateTime updDtimesz;

	/** The user status. */
	@Column(name = "user_status")
	private String userStatus;

	/**
	 * Instantiates a new user detail entity.
	 */
	public UserDetailEntity() {
		super();
	}

	/**
	 * Gets the cr by.
	 *
	 * @return the cr by
	 */
	public String getCrBy() {
		return this.crBy;
	}

	/**
	 * Sets the cr by.
	 *
	 * @param crBy the new cr by
	 */
	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	/**
	 * Gets the cr dtimesz.
	 *
	 * @return the cr dtimesz
	 */
	public LocalDateTime getCrDtimesz() {
		return this.crDtimesz;
	}

	/**
	 * Sets the cr dtimesz.
	 *
	 * @param crDtimesz the new cr dtimesz
	 */
	public void setCrDtimesz(LocalDateTime crDtimesz) {
		this.crDtimesz = crDtimesz;
	}

	/**
	 * Gets the del dtimesz.
	 *
	 * @return the del dtimesz
	 */
	public LocalDateTime getDelDtimesz() {
		return this.delDtimesz;
	}

	/**
	 * Sets the del dtimesz.
	 *
	 * @param delDtimesz the new del dtimesz
	 */
	public void setDelDtimesz(LocalDateTime delDtimesz) {
		this.delDtimesz = delDtimesz;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the checks if is active.
	 *
	 * @return the checks if is active
	 */
	public Boolean getIsActive() {
		return this.isActive;
	}

	/**
	 * Sets the checks if is active.
	 *
	 * @param isActive the new checks if is active
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * Gets the checks if is deleted.
	 *
	 * @return the checks if is deleted
	 */
	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	/**
	 * Sets the checks if is deleted.
	 *
	 * @param isDeleted the new checks if is deleted
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * Gets the lang code.
	 *
	 * @return the lang code
	 */
	public String getLangCode() {
		return this.langCode;
	}

	/**
	 * Sets the lang code.
	 *
	 * @param langCode the new lang code
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	/**
	 * Gets the mobile.
	 *
	 * @return the mobile
	 */
	public String getMobile() {
		return this.mobile;
	}

	/**
	 * Sets the mobile.
	 *
	 * @param mobile the new mobile
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the uin ref id.
	 *
	 * @return the uin ref id
	 */
	public String getUinRefId() {
		return this.uinRefId;
	}

	/**
	 * Sets the uin ref id.
	 *
	 * @param uinRefId the new uin ref id
	 */
	public void setUinRefId(String uinRefId) {
		this.uinRefId = uinRefId;
	}

	/**
	 * Gets the upd by.
	 *
	 * @return the upd by
	 */
	public String getUpdBy() {
		return this.updBy;
	}

	/**
	 * Sets the upd by.
	 *
	 * @param updBy the new upd by
	 */
	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	/**
	 * Gets the upd dtimesz.
	 *
	 * @return the upd dtimesz
	 */
	public LocalDateTime getUpdDtimesz() {
		return this.updDtimesz;
	}

	/**
	 * Sets the upd dtimesz.
	 *
	 * @param updDtimesz the new upd dtimesz
	 */
	public void setUpdDtimesz(LocalDateTime updDtimesz) {
		this.updDtimesz = updDtimesz;
	}

	/**
	 * Gets the user status.
	 *
	 * @return the user status
	 */
	public String getUserStatus() {
		return this.userStatus;
	}

	/**
	 * Sets the user status.
	 *
	 * @param userStatus the new user status
	 */
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

}