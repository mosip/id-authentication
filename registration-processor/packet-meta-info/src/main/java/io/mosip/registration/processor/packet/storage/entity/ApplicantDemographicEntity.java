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
	private static final long serialVersionUID = 1L;

	@Column(name = "addr_line1")
	private String addrLine1;

	@Column(name = "addr_line2")
	private String addrLine2;

	@Column(name = "addr_line3")
	private String addrLine3;

	private int age;

	@Column(name = "applicant_type", nullable = false)
	private String applicantType;

	@Column(name = "cr_by", nullable = false)
	private String crBy = "MOSIP_SYSTEM";

	@Column(name = "cr_dtimesz", updatable = false, nullable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	@Column(name = "del_dtimesz")
	@UpdateTimestamp
	private LocalDateTime delDtimesz;

	@Temporal(TemporalType.DATE)
	private Date dob;

	private String email;

	@Column(name = "familyname")
	private String familyName;

	@Column(name = "firstname")
	private String firstName;

	@Column(name = "forename")
	private String foreName;

	@Column(name = "middleinitial")
	private String middleInitial;

	@Column(name = "fullname")
	private String fullName;

	@Column(name = "gender_code", nullable = false)
	private String genderCode;

	@Column(name = "givenname")
	private String givenName;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "lastname")
	private String lastName;

	@Column(name = "location_code", nullable = false)
	private String locationCode;

	@Column(name = "middlename")
	private String middleName;

	@Column(name = "mobile")
	private String mobile;

	@Column(name = "nationalid")
	private String nationalId;

	@Column(name = "parent_fullname")
	private String parentFullName;

	@Column(name = "parent_ref_id")
	private String parentRefId;

	@Column(name = "parent_ref_id_type")
	private String parentRefIdType;

	@Column(name = "prereg_id", nullable = false)
	private String preRegId;

	@Column(name = "status_code")
	private String statusCode;

	@Column(name = "surname")
	private String surName;

	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	@Column(name = "upd_dtimesz")
	@UpdateTimestamp
	private LocalDateTime updDtimesz;

	public ApplicantDemographicEntity() {
		super();
	}

	public String getAddrLine1() {
		return addrLine1;
	}

	public void setAddrLine1(String addrLine1) {
		this.addrLine1 = addrLine1;
	}

	public String getAddrLine2() {
		return addrLine2;
	}

	public void setAddrLine2(String addrLine2) {
		this.addrLine2 = addrLine2;
	}

	public String getAddrLine3() {
		return addrLine3;
	}

	public void setAddrLine3(String addrLine3) {
		this.addrLine3 = addrLine3;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getApplicantType() {
		return applicantType;
	}

	public void setApplicantType(String applicantType) {
		this.applicantType = applicantType;
	}

	public String getCrBy() {
		return crBy;
	}

	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	public LocalDateTime getCrDtimesz() {
		return crDtimesz;
	}

	public void setCrDtimesz(LocalDateTime crDtimesz) {
		this.crDtimesz = crDtimesz;
	}

	public LocalDateTime getDelDtimesz() {
		return delDtimesz;
	}

	public void setDelDtimesz(LocalDateTime delDtimesz) {
		this.delDtimesz = delDtimesz;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyname) {
		this.familyName = familyname;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstname) {
		this.firstName = firstname;
	}

	public String getForeName() {
		return foreName;
	}

	public void setForeName(String forename) {
		this.foreName = forename;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullname) {
		this.fullName = fullname;
	}

	public String getGenderCode() {
		return genderCode;
	}

	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenname) {
		this.givenName = givenname;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastname) {
		this.lastName = lastname;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middlename) {
		this.middleName = middlename;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getNationalId() {
		return nationalId;
	}

	public void setNationalId(String nationalid) {
		this.nationalId = nationalid;
	}

	public String getParentFullName() {
		return parentFullName;
	}

	public void setParentFullName(String parentFullname) {
		this.parentFullName = parentFullname;
	}

	public String getParentRefId() {
		return parentRefId;
	}

	public void setParentRefId(String parentRefId) {
		this.parentRefId = parentRefId;
	}

	public String getParentRefIdType() {
		return parentRefIdType;
	}

	public void setParentRefIdType(String parentRefIdType) {
		this.parentRefIdType = parentRefIdType;
	}

	public String getPreRegId() {
		return preRegId;
	}

	public void setPreRegId(String preregId) {
		this.preRegId = preregId;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getSurName() {
		return surName;
	}

	public void setSurName(String surname) {
		this.surName = surname;
	}

	public String getUpdBy() {
		return updBy;
	}

	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	public LocalDateTime getUpdDtimesz() {
		return updDtimesz;
	}

	public void setUpdDtimesz(LocalDateTime updDtimesz) {
		this.updDtimesz = updDtimesz;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getMiddleInitial() {
		return middleInitial;
	}
	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}
}