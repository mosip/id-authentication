package io.mosip.registration.processor.status.dto;

import java.io.Serializable;
	

/**
 * The Class TransactionDto.
 */
public class TransactionDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8907302024886152312L;

	/** The transaction id. */
	private String transactionId;

	/** The registration id. */
	private String registrationId;

	/** The parentid. */
	private String parentid;

	/** The trntypecode. */
	private String trntypecode;

	/** The remarks. */
	private String remarks;

	/** The status code. */
	private String statusCode;

	/** The lang code. */
	private String langCode;

	/** The status comment. */
	private String statusComment;

	/** The is active. */
	private Boolean isActive;

	/** The reference id. */
	private String referenceId;

	/** The reference id type. */
	private String referenceIdType;

	/**
	 * Instantiates a new transaction dto.
	 */
	public TransactionDto() {
		super();
	}

	/**
	 * Instantiates a new transaction dto.
	 *
	 * @param transactionId
	 *            the transaction id
	 * @param registrationId
	 *            the registration id
	 * @param parentid
	 *            the parentid
	 * @param trntypecode
	 *            the trntypecode
	 * @param remarks
	 *            the remarks
	 * @param statusCode
	 *            the status code
	 * @param statusComment
	 *            the status comment
	 */
	public TransactionDto(String transactionId, String registrationId, String parentid, String trntypecode,
			String remarks, String statusCode, String statusComment) {
		super();
		this.transactionId = transactionId;
		this.registrationId = registrationId;
		this.parentid = parentid;
		this.trntypecode = trntypecode;
		this.remarks = remarks;
		this.statusCode = statusCode;
		this.statusComment = statusComment;
	}

	/**
	 * Gets the transaction id.
	 *
	 * @return the transaction id
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * Sets the transaction id.
	 *
	 * @param transactionId
	 *            the new transaction id
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * Gets the enrolment id.
	 *
	 * @return the enrolment id
	 */
	public String getRegistrationId() {
		return registrationId;
	}

	/**
	 * Sets the enrolment id.
	 *
	 * @param registrationId
	 *            the new enrolment id
	 */
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	/**
	 * Gets the parentid.
	 *
	 * @return the parentid
	 */
	public String getParentid() {
		return parentid;
	}

	/**
	 * Sets the parentid.
	 *
	 * @param parentid
	 *            the new parentid
	 */
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	/**
	 * Gets the trntypecode.
	 *
	 * @return the trntypecode
	 */
	public String getTrntypecode() {
		return trntypecode;
	}

	/**
	 * Sets the trntypecode.
	 *
	 * @param trntypecode
	 *            the new trntypecode
	 */
	public void setTrntypecode(String trntypecode) {
		this.trntypecode = trntypecode;
	}

	/**
	 * Gets the remarks.
	 *
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * Sets the remarks.
	 *
	 * @param remarks
	 *            the new remarks
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * Gets the status code.
	 *
	 * @return the status code
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * Sets the status code.
	 *
	 * @param statusCode
	 *            the new status code
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Gets the lang code.
	 *
	 * @return the lang code
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * Sets the lang code.
	 *
	 * @param langCode
	 *            the new lang code
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	/**
	 * Gets the status comment.
	 *
	 * @return the status comment
	 */
	public String getStatusComment() {
		return statusComment;
	}

	/**
	 * Sets the status comment.
	 *
	 * @param statusComment
	 *            the new status comment
	 */
	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}

	/**
	 * Gets the checks if is active.
	 *
	 * @return the checks if is active
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * Sets the checks if is active.
	 *
	 * @param isActive
	 *            the new checks if is active
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * Gets the reference id.
	 *
	 * @return the reference id
	 */
	public String getReferenceId() {
		return referenceId;
	}

	/**
	 * Sets the reference id.
	 *
	 * @param referenceId
	 *            the new reference id
	 */
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	/**
	 * Gets the reference id type.
	 *
	 * @return the reference id type
	 */
	public String getReferenceIdType() {
		return referenceIdType;
	}

	/**
	 * Sets the reference id type.
	 *
	 * @param referenceIdType
	 *            the new reference id type
	 */
	public void setReferenceIdType(String referenceIdType) {
		this.referenceIdType = referenceIdType;
	}

}
