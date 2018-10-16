package io.mosip.registration.processor.core.spi.packetinfo.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the reg_osi database table.
 * 
 */
@Entity
@Table(name="reg_osi")
@NamedQuery(name="RegOsi.findAll", query="SELECT r FROM RegOsi r")
public class RegOsi implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="reg_id")
	private String regId;

	@Column(name="cr_by")
	private String crBy;

	@Column(name="cr_dtimesz")
	private Timestamp crDtimesz;

	@Column(name="del_dtimesz")
	private Timestamp delDtimesz;

	@Column(name="introducer_fingerp_image_name")
	private String introducerFingerpImageName;

	@Column(name="introducer_id")
	private String introducerId;

	@Column(name="introducer_iris_image_name")
	private String introducerIrisImageName;

	@Column(name="introducer_reg_id")
	private String introducerRegId;

	@Column(name="introducer_typ")
	private String introducerTyp;

	@Column(name="introducer_uin")
	private String introducerUin;

	@Column(name="is_active")
	private Boolean isActive;

	@Column(name="is_deleted")
	private Boolean isDeleted;

	@Column(name="officer_fingerp_image_name")
	private String officerFingerpImageName;

	@Column(name="officer_id")
	private String officerId;

	@Column(name="officer_iris_image_name")
	private String officerIrisImageName;

	@Column(name="prereg_id")
	private String preregId;

	@Column(name="supervisor_fingerp_image_name")
	private String supervisorFingerpImageName;

	@Column(name="supervisor_id")
	private String supervisorId;

	@Column(name="supervisor_iris_image_name")
	private String supervisorIrisImageName;

	@Column(name="upd_by")
	private String updBy;

	@Column(name="upd_dtimesz")
	private Timestamp updDtimesz;

	public RegOsi() {
		super();
	}

	public String getRegId() {
		return this.regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getCrBy() {
		return this.crBy;
	}

	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	public Timestamp getCrDtimesz() {
		return this.crDtimesz;
	}

	public void setCrDtimesz(Timestamp crDtimesz) {
		this.crDtimesz = crDtimesz;
	}

	public Timestamp getDelDtimesz() {
		return this.delDtimesz;
	}

	public void setDelDtimesz(Timestamp delDtimesz) {
		this.delDtimesz = delDtimesz;
	}

	public String getIntroducerFingerpImageName() {
		return this.introducerFingerpImageName;
	}

	public void setIntroducerFingerpImageName(String introducerFingerpImageName) {
		this.introducerFingerpImageName = introducerFingerpImageName;
	}

	public String getIntroducerId() {
		return this.introducerId;
	}

	public void setIntroducerId(String introducerId) {
		this.introducerId = introducerId;
	}

	public String getIntroducerIrisImageName() {
		return this.introducerIrisImageName;
	}

	public void setIntroducerIrisImageName(String introducerIrisImageName) {
		this.introducerIrisImageName = introducerIrisImageName;
	}

	public String getIntroducerRegId() {
		return this.introducerRegId;
	}

	public void setIntroducerRegId(String introducerRegId) {
		this.introducerRegId = introducerRegId;
	}

	public String getIntroducerTyp() {
		return this.introducerTyp;
	}

	public void setIntroducerTyp(String introducerTyp) {
		this.introducerTyp = introducerTyp;
	}

	public String getIntroducerUin() {
		return this.introducerUin;
	}

	public void setIntroducerUin(String introducerUin) {
		this.introducerUin = introducerUin;
	}

	public Boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getOfficerFingerpImageName() {
		return this.officerFingerpImageName;
	}

	public void setOfficerFingerpImageName(String officerFingerpImageName) {
		this.officerFingerpImageName = officerFingerpImageName;
	}

	public String getOfficerId() {
		return this.officerId;
	}

	public void setOfficerId(String officerId) {
		this.officerId = officerId;
	}

	public String getOfficerIrisImageName() {
		return this.officerIrisImageName;
	}

	public void setOfficerIrisImageName(String officerIrisImageName) {
		this.officerIrisImageName = officerIrisImageName;
	}

	public String getPreregId() {
		return this.preregId;
	}

	public void setPreregId(String preregId) {
		this.preregId = preregId;
	}

	public String getSupervisorFingerpImageName() {
		return this.supervisorFingerpImageName;
	}

	public void setSupervisorFingerpImageName(String supervisorFingerpImageName) {
		this.supervisorFingerpImageName = supervisorFingerpImageName;
	}

	public String getSupervisorId() {
		return this.supervisorId;
	}

	public void setSupervisorId(String supervisorId) {
		this.supervisorId = supervisorId;
	}

	public String getSupervisorIrisImageName() {
		return this.supervisorIrisImageName;
	}

	public void setSupervisorIrisImageName(String supervisorIrisImageName) {
		this.supervisorIrisImageName = supervisorIrisImageName;
	}

	public String getUpdBy() {
		return this.updBy;
	}

	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	public Timestamp getUpdDtimesz() {
		return this.updDtimesz;
	}

	public void setUpdDtimesz(Timestamp updDtimesz) {
		this.updDtimesz = updDtimesz;
	}

}