package io.mosip.registration.processor.core.spi.packetinfo.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
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
@Table(name="applicant_demographic", schema = "regprc")
public class ApplicantDemographicEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ApplicantDemographicPKEntity id;


	@Column(name="addr_line1")
	private String addrLine1;


	@Column(name="addr_line2")
	private String addrLine2;


	@Column(name="addr_line3")
	private String addrLine3;

	private int age;


	@Column(name="applicant_type")
	private String applicantType;


	@Column(name="cr_by")
	private String crBy;


	@Column(name="cr_dtimesz",updatable=false,nullable=false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;


	@Column(name="del_dtimesz")
	@UpdateTimestamp
	private LocalDateTime delDtimesz;


	@Temporal(TemporalType.DATE)
	private Date dob;


	private String email;


	private String familyname;


	private String firstname;


	private String forename;

	private String fullname;


	@Column(name="gender_code")
	private String genderCode;


	private String givenname;


	@Column(name="is_deleted")
	private Boolean isDeleted;

	


	private String lastname;


	@Column(name="location_code")
	private String locationCode;



	private String middlename;


	private String mobile;


	private String nationalid;


	@Column(name="parent_fullname")
	private String parentFullname;


	@Column(name="parent_ref_id")
	private String parentRefId;


	@Column(name="parent_ref_id_type")
	private String parentRefIdType;


	@Column(name="prereg_id")
	private String preregId;


	@Column(name="status_code")
	private String statusCode;


	private String surname;

	@Column(name="upd_by")
	private String updBy;


	@Column(name="upd_dtimesz")
	@UpdateTimestamp
	private LocalDateTime updDtimesz;


	public ApplicantDemographicEntity() {
		super();
	}


	public ApplicantDemographicPKEntity getId() {
		return id;
	}


	public void setId(ApplicantDemographicPKEntity id) {
		this.id = id;
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


	public String getFamilyname() {
		return familyname;
	}


	public void setFamilyname(String familyname) {
		this.familyname = familyname;
	}


	public String getFirstname() {
		return firstname;
	}


	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}


	public String getForename() {
		return forename;
	}


	public void setForename(String forename) {
		this.forename = forename;
	}


	public String getFullname() {
		return fullname;
	}


	public void setFullname(String fullname) {
		this.fullname = fullname;
	}


	public String getGenderCode() {
		return genderCode;
	}


	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}


	public String getGivenname() {
		return givenname;
	}


	public void setGivenname(String givenname) {
		this.givenname = givenname;
	}


	public Boolean getIsDeleted() {
		return isDeleted;
	}


	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}




	public String getLastname() {
		return lastname;
	}


	public void setLastname(String lastname) {
		this.lastname = lastname;
	}


	public String getLocationCode() {
		return locationCode;
	}


	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}


	public String getMiddlename() {
		return middlename;
	}


	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}


	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public String getNationalid() {
		return nationalid;
	}


	public void setNationalid(String nationalid) {
		this.nationalid = nationalid;
	}


	public String getParentFullname() {
		return parentFullname;
	}


	public void setParentFullname(String parentFullname) {
		this.parentFullname = parentFullname;
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


	public String getPreregId() {
		return preregId;
	}


	public void setPreregId(String preregId) {
		this.preregId = preregId;
	}


	public String getStatusCode() {
		return statusCode;
	}


	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}


	public String getSurname() {
		return surname;
	}


	public void setSurname(String surname) {
		this.surname = surname;
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

	

}