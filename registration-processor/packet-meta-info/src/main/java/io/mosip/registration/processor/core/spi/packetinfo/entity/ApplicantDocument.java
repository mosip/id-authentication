package io.mosip.registration.processor.core.spi.packetinfo.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the applicant_document database table.
 * 
 */
@Entity
@Table(name="applicant_document")
@NamedQuery(name="ApplicantDocument.findAll", query="SELECT a FROM ApplicantDocument a")
public class ApplicantDocument implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ApplicantDocumentPKEntity id;

	@Column(name="cr_by")
	private String crBy;

	@Column(name="cr_dtimesz")
	private Timestamp crDtimesz;

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

	@Column(name="status_code")
	private String statusCode;

	@Column(name="upd_by")
	private String updBy;

	@Column(name="upd_dtimesz")
	private Timestamp updDtimesz;

	public ApplicantDocument() {
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

	public Timestamp getCrDtimesz() {
		return this.crDtimesz;
	}

	public void setCrDtimesz(Timestamp crDtimesz) {
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