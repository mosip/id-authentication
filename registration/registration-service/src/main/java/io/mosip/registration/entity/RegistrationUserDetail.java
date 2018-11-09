package io.mosip.registration.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * RegistrationUserDetail entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "user_detail")
public class RegistrationUserDetail extends RegistrationCommonFields implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id", length = 64, nullable = false, updatable = false)
	private String id;
	@Column(name = "name", length = 64, nullable = false, updatable = false)
	private String name;
	@Column(name = "email", length = 64, nullable = true, updatable = false)
	private String email;
	@Column(name = "mobile", length = 16, nullable = true, updatable = false)
	private String mobile;
	@Column(name = "cntr_id", length = 28, nullable = false, updatable = false)
	private String cntrId;
	@Column(name = "lang_code", length = 3, nullable = false, updatable = false)
	private String langCode;
	@Column(name = "last_login_dtimes", nullable = true, updatable = false)
	private Timestamp lastLoginDtimes;
	@Column(name = "last_login_method", length = 64, nullable = true, updatable = false)
	private String lastLoginMethod;
	@Column(name = "is_deleted", nullable = true, updatable = false)
	@Type(type = "true_false")
	private Boolean isDeleted;
	@Column(name = "del_dtimes", nullable = true, updatable = false)
	private Timestamp delDtimes;
	@Column(name = "user_status", length = 64, nullable = true, updatable = false)

	private String userStatus;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "registrationUserDetail")
	private Set<RegistrationUserRole> userRole;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "registrationUserDetail")
	private Set<UserMachineMapping> userMachineMapping;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "registrationUserDetail")
	private Set<UserBiometric> userBiometric;

	/**
	 * @return the userRole
	 */
	public Set<RegistrationUserRole> getUserRole() {
		return userRole;
	}

	/**
	 * @param userRole
	 *            the userRole to set
	 */
	public void setUserRole(Set<RegistrationUserRole> userRole) {
		this.userRole = userRole;
	}

	/**
	 * @return the userMachineMapping
	 */
	public Set<UserMachineMapping> getUserMachineMapping() {
		return userMachineMapping;
	}

	/**
	 * @param userMachineMapping
	 *            the userMachineMapping to set
	 */
	public void setUserMachineMapping(Set<UserMachineMapping> userMachineMapping) {
		this.userMachineMapping = userMachineMapping;

	}

	/**
	 * @return the userBiometric
	 */
	public Set<UserBiometric> getUserBiometric() {
		return userBiometric;
	}

	/**
	 * @param userBiometric
	 *            the userBiometric to set
	 */
	public void setUserBiometric(Set<UserBiometric> userBiometric) {
		this.userBiometric = userBiometric;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
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
	 * @param name
	 *            the name to set
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
	 * @param email
	 *            the email to set
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
	 * @param mobile
	 *            the mobile to set
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
	 * @param cntrId
	 *            the cntrId to set
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
	 * @param langCode
	 *            the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	/**
	 * @return the lastLoginDtimes
	 */
	public Timestamp getLastLoginDtimes() {
		return lastLoginDtimes;
	}

	/**
	 * @param lastLoginDtimes
	 *            the lastLoginDtimes to set
	 */
	public void setLastLoginDtimes(Timestamp lastLoginDtimes) {
		this.lastLoginDtimes = lastLoginDtimes;
	}

	/**
	 * @return the lastLoginMethod
	 */
	public String getLastLoginMethod() {
		return lastLoginMethod;
	}

	/**
	 * @param lastLoginMethod
	 *            the lastLoginMethod to set
	 */
	public void setLastLoginMethod(String lastLoginMethod) {
		this.lastLoginMethod = lastLoginMethod;
	}

	/**
	 * @return the isDeleted
	 */
	public Boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted
	 *            the isDeleted to set
	 */
	public void setDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the delDtimes
	 */
	public Timestamp getDelDtimes() {
		return delDtimes;
	}

	/**
	 * @param delDtimes
	 *            the delDtimes to set
	 */
	public void setDelDtimes(Timestamp delDtimes) {
		this.delDtimes = delDtimes;
	}

	/**
	 * @return the userStatus
	 */
	public String getUserStatus() {
		return userStatus;
	}

	/**
	 * @param userStatus
	 *            the userStatus to set
	 */
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

}
