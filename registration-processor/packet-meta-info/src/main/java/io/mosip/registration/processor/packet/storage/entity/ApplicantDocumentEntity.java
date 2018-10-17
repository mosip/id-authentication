package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * 
 * @author Horteppa M1048399 The persistent class for the applicant_document
 *         database table.
 * 
 */
@Entity
@Table(name = "applicant_document", schema = "regprc")
public class ApplicantDocumentEntity extends BasePacketEntity<ApplicantDocumentPKEntity> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "cr_by")
	private String crBy = "MOSIP_SYSTEM";

	@Column(name = "cr_dtimesz", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	@Column(name = "doc_file_format", nullable = false)
	private String docFileFormat;

	@Column(name = "doc_name", nullable = false)
	private String docName;

	@Column(name = "doc_owner")
	private String docOwner;

	@Column(name = "doc_store")
	private byte[] docStore;

	@Column(name = "prereg_id", nullable = false)
	private String preRegId;

	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	@Column(name = "upd_dtimesz")
	@UpdateTimestamp
	private LocalDateTime updDtimesz;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public ApplicantDocumentEntity() {
		super();
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

	public String getDocFileFormat() {
		return this.docFileFormat;
	}

	public void setDocFileFormat(String docFileFormat) {
		this.docFileFormat = docFileFormat;
	}

	public String getDocName() {
		return this.docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocOwner() {
		return this.docOwner;
	}

	public void setDocOwner(String docOwner) {
		this.docOwner = docOwner;
	}

	public byte[] getDocStore() {
		return this.docStore;
	}

	public void setDocStore(byte[] docStore) {
		this.docStore = docStore;
	}

	public String getPreRegId() {
		return this.preRegId;
	}

	public void setPreRegId(String preregId) {
		this.preRegId = preregId;
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