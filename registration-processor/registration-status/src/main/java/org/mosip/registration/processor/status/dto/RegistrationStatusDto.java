package org.mosip.registration.processor.status.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

// TODO: Auto-generated Javadoc
/**
 * The Class RegistrationStatusDto.
 */
public class RegistrationStatusDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	/** The registration id. */
	private String registrationId;

	/** The registration type. */
	private String registrationType;

	/** The reference registration id. */
	private String referenceRegistrationId;

	/** The status code. */
	private String statusCode;

	/** The lang code. */
	private String langCode;

	/** The status comment. */
	private String statusComment;

	/** The latest registration transaction id. */
	private String latestRegistrationTransactionId;

	/** The latest transaction type code. */
	private String latestTransactionTypeCode;

	/** The latest transaction status code. */
	private String latestTransactionStatusCode;

	/** The latest transaction language code. */
	private String latestTransactionLanguageCode;

	/** The latest registration transaction date time. */
	private LocalDateTime latestRegistrationTransactionDateTime;

	/** The is active. */
	private Boolean isActive;

	/** The created by. */
	private String createdBy;

	/** The create date time. */
	private LocalDateTime createDateTime;

	/** The updated by. */
	private String updatedBy;

	/** The update date time. */
	private LocalDateTime updateDateTime;

	/** The is deleted. */
	private Boolean isDeleted;

	/** The deleted date time. */
	private LocalDateTime deletedDateTime;

	/** The retry count. */
	private Integer retryCount;

	/**
	 * Instantiates a new registration status dto.
	 */
	public RegistrationStatusDto() {
		super();
	}

	/**
	 * Gets the registration id.
	 *
	 * @return the registration id
	 */
	public String getRegistrationId() {
		return registrationId;
	}

	/**
	 * Sets the registration id.
	 *
	 * @param registrationId
	 *            the new registration id
	 */
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	/**
	 * Gets the registration type.
	 *
	 * @return the registration type
	 */
	public String getRegistrationType() {
		return registrationType;
	}

	/**
	 * Sets the registration type.
	 *
	 * @param registrationType
	 *            the new registration type
	 */
	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}

	/**
	 * Gets the reference registration id.
	 *
	 * @return the reference registration id
	 */
	public String getReferenceRegistrationId() {
		return referenceRegistrationId;
	}

	/**
	 * Sets the reference registration id.
	 *
	 * @param referenceRegistrationId
	 *            the new reference registration id
	 */
	public void setReferenceRegistrationId(String referenceRegistrationId) {
		this.referenceRegistrationId = referenceRegistrationId;
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
	 * Gets the latest registration transaction id.
	 *
	 * @return the latest registration transaction id
	 */
	public String getLatestRegistrationTransactionId() {
		return latestRegistrationTransactionId;
	}

	/**
	 * Sets the latest registration transaction id.
	 *
	 * @param latestRegistrationTransactionId
	 *            the new latest registration transaction id
	 */
	public void setLatestRegistrationTransactionId(String latestRegistrationTransactionId) {
		this.latestRegistrationTransactionId = latestRegistrationTransactionId;
	}

	/**
	 * Gets the latest transaction type code.
	 *
	 * @return the latest transaction type code
	 */
	public String getLatestTransactionTypeCode() {
		return latestTransactionTypeCode;
	}

	/**
	 * Sets the latest transaction type code.
	 *
	 * @param latestTransactionTypeCode
	 *            the new latest transaction type code
	 */
	public void setLatestTransactionTypeCode(String latestTransactionTypeCode) {
		this.latestTransactionTypeCode = latestTransactionTypeCode;
	}

	/**
	 * Gets the latest transaction status code.
	 *
	 * @return the latest transaction status code
	 */
	public String getLatestTransactionStatusCode() {
		return latestTransactionStatusCode;
	}

	/**
	 * Sets the latest transaction status code.
	 *
	 * @param latestTransactionStatusCode
	 *            the new latest transaction status code
	 */
	public void setLatestTransactionStatusCode(String latestTransactionStatusCode) {
		this.latestTransactionStatusCode = latestTransactionStatusCode;
	}

	/**
	 * Gets the latest transaction language code.
	 *
	 * @return the latest transaction language code
	 */
	public String getLatestTransactionLanguageCode() {
		return latestTransactionLanguageCode;
	}

	/**
	 * Sets the latest transaction language code.
	 *
	 * @param latestTransactionLanguageCode
	 *            the new latest transaction language code
	 */
	public void setLatestTransactionLanguageCode(String latestTransactionLanguageCode) {
		this.latestTransactionLanguageCode = latestTransactionLanguageCode;
	}

	/**
	 * Gets the latest registration transaction date time.
	 *
	 * @return the latest registration transaction date time
	 */
	public LocalDateTime getLatestRegistrationTransactionDateTime() {
		return latestRegistrationTransactionDateTime;
	}

	/**
	 * Sets the latest registration transaction date time.
	 *
	 * @param latestRegistrationTransactionDateTime
	 *            the new latest registration transaction date time
	 */
	public void setLatestRegistrationTransactionDateTime(LocalDateTime latestRegistrationTransactionDateTime) {
		this.latestRegistrationTransactionDateTime = latestRegistrationTransactionDateTime;
	}

	/**
	 * Checks if is active.
	 *
	 * @return the boolean
	 */
	public Boolean isActive() {
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
	 * Gets the created by.
	 *
	 * @return the created by
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Sets the created by.
	 *
	 * @param createdBy
	 *            the new created by
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Gets the creates the date time.
	 *
	 * @return the creates the date time
	 */
	public LocalDateTime getCreateDateTime() {
		return createDateTime;
	}

	/**
	 * Sets the creates the date time.
	 *
	 * @param createDateTime
	 *            the new creates the date time
	 */
	public void setCreateDateTime(LocalDateTime createDateTime) {
		this.createDateTime = createDateTime;
	}

	/**
	 * Gets the updated by.
	 *
	 * @return the updated by
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * Sets the updated by.
	 *
	 * @param updatedBy
	 *            the new updated by
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * Gets the update date time.
	 *
	 * @return the update date time
	 */
	public LocalDateTime getUpdateDateTime() {
		return updateDateTime;
	}

	/**
	 * Sets the update date time.
	 *
	 * @param updateDateTime
	 *            the new update date time
	 */
	public void setUpdateDateTime(LocalDateTime updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

	/**
	 * Checks if is deleted.
	 *
	 * @return the boolean
	 */
	public Boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * Sets the checks if is deleted.
	 *
	 * @param isDeleted
	 *            the new checks if is deleted
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * Gets the deleted date time.
	 *
	 * @return the deleted date time
	 */
	public LocalDateTime getDeletedDateTime() {
		return deletedDateTime;
	}

	/**
	 * Sets the deleted date time.
	 *
	 * @param deletedDateTime
	 *            the new deleted date time
	 */
	public void setDeletedDateTime(LocalDateTime deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}

	/**
	 * Gets the retry count.
	 *
	 * @return the retry count
	 */
	public Integer getRetryCount() {
		return retryCount;
	}

	/**
	 * Sets the retry count.
	 *
	 * @param retryCount
	 *            the new retry count
	 */
	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

}
