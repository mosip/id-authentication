package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 
 * @author Girish Yarru
 *
 */
@Entity
@Table(name = "individual_demographic_dedup", schema = "regprc")
public class IndividualDemographicDedupeEntity extends BasePacketEntity<IndividualDemographicDedupePKEntity>
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "name")
	private String name;

	@Column(name = "dob")
	private String dob;

	@Column(name = "gender")
	private String gender;

	@Column(name = "mobile_number")
	private String phone;

	@Column(name = "email")
	private String email;

	@Column(name = "pincode")
	private String postalCode;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "cr_by", nullable = false)
	private String crBy = "SYSTEM";

	@Column(name = "cr_dtimes", updatable = false)
	private LocalDateTime crDtimes;

	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	@Column(name = "upd_dtimes")
	private LocalDateTime updDtimes;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime delDtimes;

	public IndividualDemographicDedupeEntity() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String fullName) {
		this.name = fullName;
	}

	public String getDob() {
		return dob; // new Date(dob.getTime());
	}

	public void setDob(String dob) {
		this.dob = dob; // new Date(dob.getTime());
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String genderCode) {
		this.gender = genderCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getCrBy() {
		return crBy;
	}

	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	public LocalDateTime getCrDtimes() {
		return crDtimes;
	}

	public void setCrDtimes(LocalDateTime crDtimes) {
		this.crDtimes = crDtimes;
	}

	public String getUpdBy() {
		return updBy;
	}

	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	public LocalDateTime getUpdDtimes() {
		return updDtimes;
	}

	public void setUpdDtimes(LocalDateTime updDtimes) {
		this.updDtimes = updDtimes;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public LocalDateTime getDelDtimes() {
		return delDtimes;
	}

	public void setDelDtimes(LocalDateTime delDtimes) {
		this.delDtimes = delDtimes;
	}

}
