package io.mosip.registration.processor.status.dto;

import java.time.LocalDateTime;

public class RegistrationTransactionDto {
	
	/** The transaction id. */
	private String transactionId;

	/** The registration id. */
	private String registrationId;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getTrntypecode() {
		return trntypecode;
	}

	public void setTrntypecode(String trntypecode) {
		this.trntypecode = trntypecode;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusComment() {
		return statusComment;
	}

	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}

	public String getCr_by() {
		return cr_by;
	}

	public void setCr_by(String cr_by) {
		this.cr_by = cr_by;
	}

	public LocalDateTime getCr_dtimes() {
		return cr_dtimes;
	}

	public void setCr_dtimes(LocalDateTime cr_dtimes) {
		this.cr_dtimes = cr_dtimes;
	}
	
	public RegistrationTransactionDto(String transactionId, String registrationId, String parentid, String trntypecode,
			String remarks, String statusCode, String statusComment, String cr_by, LocalDateTime cr_dtimes) {
		super();
		this.parentid = parentid;
		this.remarks = remarks;
		this.trntypecode = trntypecode;
		this.statusCode = statusCode;
		this.statusComment = statusComment;

		this.transactionId = transactionId;
		this.registrationId = registrationId;
		this.cr_by = cr_by;
		this.cr_dtimes = cr_dtimes;
	}

	/** The parentid. */
	private String parentid;
	
	/** The remarks. */
	private String remarks;

	/** The trntypecode. */
	private String trntypecode;

	/** The status code. */
	private String statusCode;

	/** The status comment. */
	private String statusComment;

	/** The is active. */
	private String cr_by;

	/** The reference id. */
	private LocalDateTime cr_dtimes;

	
}
