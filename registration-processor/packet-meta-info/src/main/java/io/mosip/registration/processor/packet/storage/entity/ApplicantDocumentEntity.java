package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;


/**
 * 
 * @author Horteppa M1048399
 * The persistent class for the applicant_document database table.
 * 
 */
@Entity
@Table(name="applicant_document", schema = "regprc")
public class ApplicantDocumentEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ApplicantDocumentPKEntity id;

	@Column(name="cr_by")
	private String crBy = "MOSIP_SYSTEM";

	@Column(name="cr_dtimesz", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	@Column(name="doc_file_format")
	private String docFileFormat;

	@Column(name="doc_name")
	private String docName;

	@Column(name="doc_owner")
	private String docOwner;

	@Column(name="doc_store")
	private byte[] docStore;

	@Column(name="prereg_id")
	private String preregId;


	@Column(name="upd_by")
	private String updBy = "MOSIP_SYSTEM";

	@Column(name="upd_dtimesz")
	@UpdateTimestamp
	private LocalDateTime updDtimesz;

	public ApplicantDocumentEntity() {
		super();
	}

	public ApplicantDocumentPKEntity getId() {
		return this.id;
	}

	public void setId(ApplicantDocumentPKEntity id) {
		this.id = id;
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

	public String getPreregId() {
		return this.preregId;
	}

	public void setPreregId(String preregId) {
		this.preregId = preregId;
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