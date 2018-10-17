package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


/**
 * The persistent class for the biometric_exceptions database table.
 * 
 * @author Horteppa M1048399
 */
@Entity
@Table(name="biometric_exceptions", schema = "regprc")
public class BiometricExceptionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BiometricExceptionPKEntity id;

	@Column(name="bio_typ")
	private String bioTyp;

	@Column(name="cr_by")
	private String crBy = "MOSIP_SYSTEM";

	@Column(name="cr_dtimesz", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	@Column(name="del_dtimesz")
	@UpdateTimestamp
	private LocalDateTime delDtimesz;

	@Column(name="excp_descr")
	private String excpDescr;

	@Column(name="excp_typ")
	private String excpTyp;

	@Column(name="is_deleted")
	private Boolean isDeleted;

	@Column(name="prereg_id")
	private String preregId;

	@Column(name="status_code")
	private String statusCode;

	@Column(name="upd_by")
	private String updBy = "MOSIP_SYSTEM";

	@Column(name="upd_dtimesz")
	@UpdateTimestamp
	private LocalDateTime updDtimesz;

	public BiometricExceptionEntity() {
		super();
	}

	public BiometricExceptionPKEntity getId() {
		return this.id;
	}

	public void setId(BiometricExceptionPKEntity id) {
		this.id = id;
	}

	public String getBioTyp() {
		return this.bioTyp;
	}

	public void setBioTyp(String bioTyp) {
		this.bioTyp = bioTyp;
	}

	public String getCrBy() {
		return this.crBy;
	}

	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	public LocalDateTime getCrDtimesz() {
		return this.crDtimesz;
	}

	public void setCrDtimesz(LocalDateTime crDtimesz) {
		this.crDtimesz = crDtimesz;
	}

	public LocalDateTime getDelDtimesz() {
		return this.delDtimesz;
	}

	public void setDelDtimesz(LocalDateTime delDtimesz) {
		this.delDtimesz = delDtimesz;
	}

	public String getExcpDescr() {
		return this.excpDescr;
	}

	public void setExcpDescr(String excpDescr) {
		this.excpDescr = excpDescr;
	}

	public String getExcpTyp() {
		return this.excpTyp;
	}

	public void setExcpTyp(String excpTyp) {
		this.excpTyp = excpTyp;
	}

	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getPreregId() {
		return this.preregId;
	}

	public void setPreregId(String preregId) {
		this.preregId = preregId;
	}

	public String getStatusCode() {
		return this.statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getUpdBy() {
		return this.updBy;
	}

	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	public LocalDateTime getUpdDtimesz() {
		return this.updDtimesz;
	}

	public void setUpdDtimesz(LocalDateTime updDtimesz) {
		this.updDtimesz = updDtimesz;
	}

}