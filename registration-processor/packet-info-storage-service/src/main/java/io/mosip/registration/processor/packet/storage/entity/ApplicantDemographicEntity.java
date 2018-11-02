package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The persistent class for the applicant_demographic database table.
 * 
 */
@Entity
@Table(name = "applicant_demographic", schema = "regprc")
public class ApplicantDemographicEntity extends BasePacketEntity<ApplicantDemographicPKEntity> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The addr line 1. */
	@Column(name = "addr_line1")
	private String addrLine1;

	/** The addr line 2. */
	@Column(name = "addr_line2")
	private String addrLine2;

	/** The addr line 3. */
	@Column(name = "addr_line3")
	private String addrLine3;

	/** The age. */
	private int age;

	/** The applicant type. */
	@Column(name = "applicant_type", nullable = false)
	private String applicantType;

	/** The cr by. */
	@Column(name = "cr_by", nullable = false)
	private String crBy = "MOSIP_SYSTEM";

	/** The cr dtimesz. */
	@Column(name = "cr_dtimesz", updatable = false, nullable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	/** The del dtimesz. */
	@Column(name = "del_dtimesz")
	@UpdateTimestamp
	private LocalDateTime delDtimesz;

	/** The dob. */
	@Temporal(TemporalType.DATE)
	private Date dob;

	/** The email. */
	private String email;

	/** The family name. */
	@Column(name = "familyname")
	private String familyName;

	/** The first name. */
	@Column(name = "firstname")
	private String firstName;

	/** The fore name. */
	@Column(name = "forename")
	private String foreName;

	/** The middle initial. */
	@Column(name = "middleinitial")
	private String middleInitial;

	/** The full name. */
	@Column(name = "fullname")
	private String fullName;

	/** The gender code. */
	@Column(name = "gender_code", nullable = false)
	private String genderCode;

	/** The given name. */
	@Column(name = "givenname")
	private String givenName;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The is active. */
	@Column(name = "is_active")
	private Boolean isActive;

	/** The last name. */
	@Column(name = "lastname")
	private String lastName;

	/** The location code. */
	@Column(name = "location_code", nullable = false)
	private String locationCode;

	/** The middle name. */
	@Column(name = "middlename")
	private String middleName;

	/** The mobile. */
	@Column(name = "mobile")
	private String mobile;

	/** The national id. */
	@Column(name = "nationalid")
	private String nationalId;

	/** The parent full name. */
	@Column(name = "parent_fullname")
	private String parentFullName;

	/** The parent ref id. */
	@Column(name = "parent_ref_id")
	private String parentRefId;

	/** The parent ref id type. */
	@Column(name = "parent_ref_id_type")
	private String parentRefIdType;

	/** The pre reg id. */
	@Column(name = "prereg_id", nullable = false)
	private String preRegId;

	/** The status code. */
	@Column(name = "status_code")
	private String statusCode;

	/** The sur name. */
	@Column(name = "surname")
	private String surName;

	/** The upd by. */
	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	/** The upd dtimesz. */
	@Column(name = "upd_dtimesz")
	@UpdateTimestamp
	private LocalDateTime updDtimesz;

	/**
	 * Instantiates a new applicant demographic entity.
	 */
	public ApplicantDemographicEntity() {
		super();
	}

	/**
	 * Gets the addr line 1.
	 *
	 * @return the addr line 1
	 */
	public String getAddrLine1() {
		return addrLine1;
	}

	/**
	 * Sets the addr line 1.
	 *
	 * @param addrLine1 the new addr line 1
	 */
	public void setAddrLine1(String addrLine1) {
		this.addrLine1 = addrLine1;
	}

	/**
	 * Gets the addr line 2.
	 *
	 * @return the addr line 2
	 */
	public String getAddrLine2() {
		return addrLine2;
	}

	/**
	 * Sets the addr line 2.
	 *
	 * @param addrLine2 the new addr line 2
	 */
	public void setAddrLine2(String addrLine2) {
		this.addrLine2 = addrLine2;
	}

	/**
	 * Gets the addr line 3.
	 *
	 * @return the addr line 3
	 */
	public String getAddrLine3() {
		return addrLine3;
	}

	/**
	 * Sets the addr line 3.
	 *
	 * @param addrLine3 the new addr line 3
	 */
	public void setAddrLine3(String addrLine3) {
		this.addrLine3 = addrLine3;
	}

	/**
	 * Gets the age.
	 *
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Sets the age.
	 *
	 * @param age the new age
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * Gets the applicant type.
	 *
	 * @return the applicant type
	 */
	public String getApplicantType() {
		return applicantType;
	}

	/**
	 * Sets the applicant type.
	 *
	 * @param applicantType the new applicant type
	 */
	public void setApplicantType(String applicantType) {
		this.applicantType = applicantType;
	}

	/**
	 * Gets the cr by.
	 *
	 * @return the cr by
	 */
	public String getCrBy() {
		return crBy;
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
		return crDtimesz;
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
		return delDtimesz;
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
	 * Gets the dob.
	 *
	 * @return the dob
	 */
	public Date getDob() {
		return dob;
	}

	/**
	 * Sets the dob.
	 *
	 * @param dob the new dob
	 */
	public void setDob(Date dob) {
		this.dob = dob;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
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
	 * Gets the family name.
	 *
	 * @return the family name
	 */
	public String getFamilyName() {
		return familyName;
	}

	/**
	 * Sets the family name.
	 *
	 * @param familyname the new family name
	 */
	public void setFamilyName(String familyname) {
		this.familyName = familyname;
	}

	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name.
	 *
	 * @param firstname the new first name
	 */
	public void setFirstName(String firstname) {
		this.firstName = firstname;
	}

	/**
	 * Gets the fore name.
	 *
	 * @return the fore name
	 */
	public String getForeName() {
		return foreName;
	}

	/**
	 * Sets the fore name.
	 *
	 * @param forename the new fore name
	 */
	public void setForeName(String forename) {
		this.foreName = forename;
	}

	/**
	 * Gets the full name.
	 *
	 * @return the full name
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Sets the full name.
	 *
	 * @param fullname the new full name
	 */
	public void setFullName(String fullname) {
		this.fullName = fullname;
	}

	/**
	 * Gets the gender code.
	 *
	 * @return the gender code
	 */
	public String getGenderCode() {
		return genderCode;
	}

	/**
	 * Sets the gender code.
	 *
	 * @param genderCode the new gender code
	 */
	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

	/**
	 * Gets the given name.
	 *
	 * @return the given name
	 */
	public String getGivenName() {
		return givenName;
	}

	/**
	 * Sets the given name.
	 *
	 * @param givenname the new given name
	 */
	public void setGivenName(String givenname) {
		this.givenName = givenname;
	}

	/**
	 * Gets the checks if is deleted.
	 *
	 * @return the checks if is deleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
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
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastname the new last name
	 */
	public void setLastName(String lastname) {
		this.lastName = lastname;
	}

	/**
	 * Gets the location code.
	 *
	 * @return the location code
	 */
	public String getLocationCode() {
		return locationCode;
	}

	/**
	 * Sets the location code.
	 *
	 * @param locationCode the new location code
	 */
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	/**
	 * Gets the middle name.
	 *
	 * @return the middle name
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * Sets the middle name.
	 *
	 * @param middlename the new middle name
	 */
	public void setMiddleName(String middlename) {
		this.middleName = middlename;
	}

	/**
	 * Gets the mobile.
	 *
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
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
	 * Gets the national id.
	 *
	 * @return the national id
	 */
	public String getNationalId() {
		return nationalId;
	}

	/**
	 * Sets the national id.
	 *
	 * @param nationalid the new national id
	 */
	public void setNationalId(String nationalid) {
		this.nationalId = nationalid;
	}

	/**
	 * Gets the parent full name.
	 *
	 * @return the parent full name
	 */
	public String getParentFullName() {
		return parentFullName;
	}

	/**
	 * Sets the parent full name.
	 *
	 * @param parentFullname the new parent full name
	 */
	public void setParentFullName(String parentFullname) {
		this.parentFullName = parentFullname;
	}

	/**
	 * Gets the parent ref id.
	 *
	 * @return the parent ref id
	 */
	public String getParentRefId() {
		return parentRefId;
	}

	/**
	 * Sets the parent ref id.
	 *
	 * @param parentRefId the new parent ref id
	 */
	public void setParentRefId(String parentRefId) {
		this.parentRefId = parentRefId;
	}

	/**
	 * Gets the parent ref id type.
	 *
	 * @return the parent ref id type
	 */
	public String getParentRefIdType() {
		return parentRefIdType;
	}

	/**
	 * Sets the parent ref id type.
	 *
	 * @param parentRefIdType the new parent ref id type
	 */
	public void setParentRefIdType(String parentRefIdType) {
		this.parentRefIdType = parentRefIdType;
	}

	/**
	 * Gets the pre reg id.
	 *
	 * @return the pre reg id
	 */
	public String getPreRegId() {
		return preRegId;
	}

	/**
	 * Sets the pre reg id.
	 *
	 * @param preregId the new pre reg id
	 */
	public void setPreRegId(String preregId) {
		this.preRegId = preregId;
	}

	/**
	 * Gets the status code.
	 *
	 * @return the status code
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * Sets the status code.
	 *
	 * @param statusCode the new status code
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Gets the sur name.
	 *
	 * @return the sur name
	 */
	public String getSurName() {
		return surName;
	}

	/**
	 * Sets the sur name.
	 *
	 * @param surname the new sur name
	 */
	public void setSurName(String surname) {
		this.surName = surname;
	}

	/**
	 * Gets the upd by.
	 *
	 * @return the upd by
	 */
	public String getUpdBy() {
		return updBy;
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
		return updDtimesz;
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
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Gets the checks if is active.
	 *
	 * @return the checks if is active
	 */
	public Boolean getIsActive() {
		return isActive;
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
	 * Gets the middle initial.
	 *
	 * @return the middle initial
	 */
	public String getMiddleInitial() {
		return middleInitial;
	}

	/**
	 * Sets the middle initial.
	 *
	 * @param middleInitial the new middle initial
	 */
	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}
}