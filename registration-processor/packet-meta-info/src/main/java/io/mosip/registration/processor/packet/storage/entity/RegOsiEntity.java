package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The persistent class for the reg_osi database table.
 * 
 * @author Horteppa M1048399
 * @author Girish Yarru
 */
@Entity
@Table(name = "reg_osi", schema = "regprc")
public class RegOsiEntity extends BasePacketEntity<RegOsiPkEntity> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "prereg_id", nullable = false)
	private String preregId;

	@Column(name = "officer_id", nullable = false)
	private String officerId;

	@Column(name = "officer_iris_image_name")
	private String officerIrisImageName;

	@Column(name = "officer_fingerp_image_name")
	private String officerFingerpImageName;

	@Column(name = "supervisor_id", nullable = false)
	private String supervisorId;

	@Column(name = "supervisor_fingerp_image_name")
	private String supervisorFingerpImageName;

	@Column(name = "supervisor_iris_image_name")
	private String supervisorIrisImageName;

	@Column(name = "introducer_id")
	private String introducerId;

	@Column(name = "introducer_typ")
	private String introducerTyp;

	@Column(name = "introducer_reg_id")
	private String introducerRegId;

	@Column(name = "introducer_iris_image_name")
	private String introducerIrisImageName;

	@Column(name = "introducer_fingerp_image_name")
	private String introducerFingerpImageName;

	@Column(name = "introducer_uin")
	private String introducerUin;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@Column(name = "cr_by", nullable = false)
	private String crBy = "MOSIP_SYSTEM";

	@Column(name = "cr_dtimesz", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	@Column(name = "upd_dtimesz")
	@UpdateTimestamp
	private LocalDateTime updDtimesz;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimesz")
	@UpdateTimestamp
	private LocalDateTime delDtimesz;

	public RegOsiEntity() {
		super();
	}

	public String getPreregId() {
		return preregId;
	}

	public void setPreregId(String preregId) {
		this.preregId = preregId;
	}

	public String getOfficerId() {
		return officerId;
	}

	public void setOfficerId(String officerId) {
		this.officerId = officerId;
	}

	public String getOfficerIrisImageName() {
		return officerIrisImageName;
	}

	public void setOfficerIrisImageName(String officerIrisImageName) {
		this.officerIrisImageName = officerIrisImageName;
	}

	public String getOfficerFingerpImageName() {
		return officerFingerpImageName;
	}

	public void setOfficerFingerpImageName(String officerFingerpImageName) {
		this.officerFingerpImageName = officerFingerpImageName;
	}

	public String getSupervisorId() {
		return supervisorId;
	}

	public void setSupervisorId(String supervisorId) {
		this.supervisorId = supervisorId;
	}

	public String getSupervisorFingerpImageName() {
		return supervisorFingerpImageName;
	}

	public void setSupervisorFingerpImageName(String supervisorFingerpImageName) {
		this.supervisorFingerpImageName = supervisorFingerpImageName;
	}

	public String getSupervisorIrisImageName() {
		return supervisorIrisImageName;
	}

	public void setSupervisorIrisImageName(String supervisorIrisImageName) {
		this.supervisorIrisImageName = supervisorIrisImageName;
	}

	public String getIntroducerId() {
		return introducerId;
	}

	public void setIntroducerId(String introducerId) {
		this.introducerId = introducerId;
	}

	public String getIntroducerTyp() {
		return introducerTyp;
	}

	public void setIntroducerTyp(String introducerTyp) {
		this.introducerTyp = introducerTyp;
	}

	public String getIntroducerRegId() {
		return introducerRegId;
	}

	public void setIntroducerRegId(String introducerRegId) {
		this.introducerRegId = introducerRegId;
	}

	public String getIntroducerIrisImageName() {
		return introducerIrisImageName;
	}

	public void setIntroducerIrisImageName(String introducerIrisImageName) {
		this.introducerIrisImageName = introducerIrisImageName;
	}

	public String getIntroducerFingerpImageName() {
		return introducerFingerpImageName;
	}

	public void setIntroducerFingerpImageName(String introducerFingerpImageName) {
		this.introducerFingerpImageName = introducerFingerpImageName;
	}

	public String getIntroducerUin() {
		return introducerUin;
	}

	public void setIntroducerUin(String introducerUin) {
		this.introducerUin = introducerUin;
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

	public LocalDateTime getCrDtimesz() {
		return crDtimesz;
	}

	public void setCrDtimesz(LocalDateTime crDtimesz) {
		this.crDtimesz = crDtimesz;
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

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public LocalDateTime getDelDtimesz() {
		return delDtimesz;
	}

	public void setDelDtimesz(LocalDateTime delDtimesz) {
		this.delDtimesz = delDtimesz;
	}

}