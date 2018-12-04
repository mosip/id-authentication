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

import lombok.Data;
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
	
	@Column(name = "uin_ref_id")
	private String uinRefId ;
	
	@Column(name = "name")
	private String name;

	@Temporal(TemporalType.DATE)
	private Date dob;

	@Column(name = "gender", nullable = false)
	private String gender;
	
	@Column(name = "phonetic_name")
	private String phoneticName;

	
	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "cr_by", nullable = false)
	private String crBy = "SYSTEM";

	@Column(name = "cr_dtimes", updatable = false, nullable = false)
	@CreationTimestamp
	private LocalDateTime crDtimes;

	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	@Column(name = "upd_dtimes")
	@UpdateTimestamp
	private LocalDateTime updDtimes;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimes")
	@UpdateTimestamp
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

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String genderCode) {
		this.gender = genderCode;
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

	public String getPhoneticName() {
		return phoneticName;
	}

	public void setPhoneticName(String pheoniticName) {
		this.phoneticName = pheoniticName;
	}

	public String getUinRefId() {
		return uinRefId;
	}

	public void setUinRefId(String uinRefId) {
		this.uinRefId = uinRefId;
	}

	
	
}
