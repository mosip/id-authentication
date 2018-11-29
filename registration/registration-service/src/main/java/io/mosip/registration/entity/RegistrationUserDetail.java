package io.mosip.registration.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
	@Column(name = "id")
	private String id;
	
	@Column(name = "uin_ref_id")
	private String uinRefId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "mobile")
	private String mobile;
	
	@Column(name = "status_code")
	private String statusCode;
	
	@Column(name = "lang_code")
	private String langCode;
	
	@Column(name = "last_login_dtimes")
	private Timestamp lastLoginDtimes;
	
	@Column(name = "last_login_method")
	private String lastLoginMethod;
	
	@Column(name = "unsuccessful_login_count")
	private Integer unsuccessfulLoginCount;
	
	@Column(name = "userlock_till_dtimes")
	private Timestamp userlockTillDtimes;
	
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "registrationUserDetail")
	private Set<RegistrationUserRole> userRole;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "registrationUserDetail")
	private Set<UserMachineMapping> userMachineMapping;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "registrationUserDetail")
	private Set<UserBiometric> userBiometric;

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "registrationUserDetail")
	private RegistrationUserPassword registrationUserPassword;

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "registrationUserDetail")
	private RegistrationCenterUser registrationCenterUser;

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
	 * @return the uinRefId
	 */
	public String getUinRefId() {
		return uinRefId;
	}

	/**
	 * @param uinRefId
	 *            the uinRefId to set
	 */
	public void setUinRefId(String uinRefId) {
		this.uinRefId = uinRefId;
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
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode
	 *            the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
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
	 * @return the unsuccessfulLoginCount
	 */
	public Integer getUnsuccessfulLoginCount() {
		return unsuccessfulLoginCount;
	}

	/**
	 * @param unsuccessfulLoginCount
	 *            the unsuccessfulLoginCount to set
	 */
	public void setUnsuccessfulLoginCount(Integer unsuccessfulLoginCount) {
		this.unsuccessfulLoginCount = unsuccessfulLoginCount;
	}

	/**
	 * @return the userlockTillDtimes
	 */
	public Timestamp getUserlockTillDtimes() {
		return userlockTillDtimes;
	}

	/**
	 * @param userlockTillDtimes
	 *            the userlockTillDtimes to set
	 */
	public void setUserlockTillDtimes(Timestamp userlockTillDtimes) {
		this.userlockTillDtimes = userlockTillDtimes;
	}

	/**
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted
	 *            the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
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
	 * @return the registrationUserPassword
	 */
	public RegistrationUserPassword getRegistrationUserPassword() {
		return registrationUserPassword;
	}

	/**
	 * @param registrationUserPassword
	 *            the registrationUserPassword to set
	 */
	public void setRegistrationUserPassword(RegistrationUserPassword registrationUserPassword) {
		this.registrationUserPassword = registrationUserPassword;
	}

	/**
	 * @return the registrationCenterUser
	 */
	public RegistrationCenterUser getRegistrationCenterUser() {
		return registrationCenterUser;
	}

	/**
	 * @param registrationCenterUser
	 *            the registrationCenterUser to set
	 */
	public void setRegistrationCenterUser(RegistrationCenterUser registrationCenterUser) {
		this.registrationCenterUser = registrationCenterUser;
	}

}
