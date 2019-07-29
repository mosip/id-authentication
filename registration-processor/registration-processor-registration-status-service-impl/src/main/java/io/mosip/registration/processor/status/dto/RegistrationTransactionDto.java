package io.mosip.registration.processor.status.dto;

import java.time.LocalDateTime;

public class RegistrationTransactionDto {
	
	/** The transaction id. */
	private String id;

	/** The registration id. */
	private String registrationId;
	
	/** The trntypecode. */
	private String transactionTypeCode;
	
	/** The parentid. */
	private String parentTransactionId;

	/** The status code. */
	private String statusCode;

	/** The status comment. */
	private String statusComment;

	/** The reference id. */
	private LocalDateTime createdDateTimes;

	public RegistrationTransactionDto(String id, String registrationId, String transactionTypeCode,
			String parentTransactionId, String statusCode, String statusComment, LocalDateTime createdDateTimes) {
		
		this.id = id;
		this.registrationId = registrationId;
		this.transactionTypeCode = transactionTypeCode;
		this.parentTransactionId = parentTransactionId;
		this.statusCode = statusCode;
		this.statusComment = statusComment;
		this.createdDateTimes = createdDateTimes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public String getTransactionTypeCode() {
		return transactionTypeCode;
	}

	public void setTransactionTypeCode(String transactionTypeCode) {
		this.transactionTypeCode = transactionTypeCode;
	}

	public String getParentTransactionId() {
		return parentTransactionId;
	}

	public void setParentTransactionId(String parentTransactionId) {
		this.parentTransactionId = parentTransactionId;
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

	public LocalDateTime getCreatedDateTimes() {
		return createdDateTimes;
	}

	public void setCreatedDateTimes(LocalDateTime createdDateTimes) {
		this.createdDateTimes = createdDateTimes;
	}

	
}
