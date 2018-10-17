package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The persistent class for the biometric_exceptions database table.
 * 
 * @author Horteppa M1048399
 */
@Entity
@Table(name = "biometric_exceptions", schema = "regprc")
public class BiometricExceptionEntity extends BasePacketEntity<BiometricExceptionPKEntity> implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name = "prereg_id", nullable = false)
	private String preregId;

	@Column(name = "bio_typ", nullable = false)
	private String bioTyp;

	@Column(name = "excp_descr")
	private String excpDescr;

	@Column(name = "excp_typ")
	private String excpTyp;

	@Column(name = "status_code")
	private String statusCode;

	@Column(name = "cr_by", nullable = false)
	private String crBy = "MOSIP_SYSTEM";

	@Column(name = "cr_dtimesz", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	@Column(name = "del_dtimesz")
	@UpdateTimestamp
	private LocalDateTime delDtimesz;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	@Column(name = "upd_dtimesz")
	@UpdateTimestamp
	private LocalDateTime updDtimesz;

	public BiometricExceptionEntity() {
		super();
	}

	public String getPreregId() {
		return preregId;
	}

	public void setPreregId(String preregId) {
		this.preregId = preregId;
	}

	public String getBioTyp() {
		return bioTyp;
	}

	public void setBioTyp(String bioTyp) {
		this.bioTyp = bioTyp;
	}

	public String getExcpDescr() {
		return excpDescr;
	}

	public void setExcpDescr(String excpDescr) {
		this.excpDescr = excpDescr;
	}

	public String getExcpTyp() {
		return excpTyp;
	}

	public void setExcpTyp(String excpTyp) {
		this.excpTyp = excpTyp;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
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

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
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

}