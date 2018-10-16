package io.mosip.registration.processor.core.spi.packetinfo.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the biometric_exceptions database table.
 * 
 */
@Entity
@Table(name="biometric_exceptions")
@NamedQuery(name="BiometricExceptionEntity.findAll", query="SELECT b FROM BiometricException b")
public class BiometricException implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BiometricExceptionPK id;

	@Column(name="bio_typ")
	private String bioTyp;

	@Column(name="cr_by")
	private String crBy;

	@Column(name="cr_dtimesz")
	private Timestamp crDtimesz;

	@Column(name="del_dtimesz")
	private Timestamp delDtimesz;

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
	private String updBy;

	@Column(name="upd_dtimesz")
	private Timestamp updDtimesz;

	public BiometricException() {
		super();
	}

	public BiometricExceptionPK getId() {
		return this.id;
	}

	public void setId(BiometricExceptionPK id) {
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

	public Timestamp getUpdDtimesz() {
		return this.updDtimesz;
	}

	public void setUpdDtimesz(Timestamp updDtimesz) {
		this.updDtimesz = updDtimesz;
	}

}