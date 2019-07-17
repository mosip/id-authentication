package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The persistent class for the abis_request database table.
 * 
 */
@Entity
@Table(name = "abis_request", schema = "regprc")
public class AbisRequestEntity extends BasePacketEntity<AbisRequestPKEntity> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "abis_app_code")
	private String abisAppCode;

	@Column(name = "bio_ref_id")
	private String bioRefId;

	@Column(name = "cr_by")
	private String crBy;

	@Column(name = "cr_dtimes", updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimes;

	@Column(name = "del_dtimes")
	@UpdateTimestamp
	private LocalDateTime delDtimes;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "lang_code")
	private String langCode;

	@Column(name = "ref_regtrn_id")
	private String refRegtrnId;

	@Column(name = "req_batch_id")
	private String reqBatchId;

	@Column(name = "req_text")
	private byte[] reqText;

	@UpdateTimestamp
	@Column(name = "request_dtimes")
	private LocalDateTime requestDtimes;

	@Column(name = "request_type")
	private String requestType;

	@Column(name = "status_code")
	private String statusCode;

	@Column(name = "status_comment")
	private String statusComment;

	@Column(name = "upd_by")
	private String updBy;

	@Column(name = "upd_dtimes")
	@UpdateTimestamp
	private LocalDateTime updDtimes;

	public AbisRequestEntity() {

	}

	public String getAbisAppCode() {
		return this.abisAppCode;
	}

	public void setAbisAppCode(String abisAppCode) {
		this.abisAppCode = abisAppCode;
	}

	public String getBioRefId() {
		return this.bioRefId;
	}

	public void setBioRefId(String bioRefId) {
		this.bioRefId = bioRefId;
	}

	public String getCrBy() {
		return this.crBy;
	}

	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	public LocalDateTime getCrDtimes() {
		return this.crDtimes;
	}

	public void setCrDtimes(LocalDateTime crDtimes) {
		this.crDtimes = crDtimes;
	}

	public LocalDateTime getDelDtimes() {
		return this.delDtimes;
	}

	public void setDelDtimes(LocalDateTime delDtimes) {
		this.delDtimes = delDtimes;
	}

	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getLangCode() {
		return this.langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public String getRefRegtrnId() {
		return this.refRegtrnId;
	}

	public void setRefRegtrnId(String refRegtrnId) {
		this.refRegtrnId = refRegtrnId;
	}

	public String getReqBatchId() {
		return this.reqBatchId;
	}

	public void setReqBatchId(String reqBatchId) {
		this.reqBatchId = reqBatchId;
	}

	public byte[] getReqText() {
		return this.reqText;
	}

	public void setReqText(byte[] reqText) {
		this.reqText = reqText!=null?reqText:null;
	}

	public LocalDateTime getRequestDtimes() {
		return this.requestDtimes;
	}

	public void setRequestDtimes(LocalDateTime requestDtimes) {
		this.requestDtimes = requestDtimes;
	}

	public String getRequestType() {
		return this.requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getStatusCode() {
		return this.statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusComment() {
		return this.statusComment;
	}

	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}

	public String getUpdBy() {
		return this.updBy;
	}

	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	public LocalDateTime getUpdDtimes() {
		return this.updDtimes;
	}

	public void setUpdDtimes(LocalDateTime updDtimes) {
		this.updDtimes = updDtimes;
	}


}