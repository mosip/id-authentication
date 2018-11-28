package io.mosip.registration.processor.status.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

// TODO: Auto-generated Javadoc
/**
 * The Class RegistrationStatusEntity.
 */
@Entity
@Table(name = "registration", schema = "regprc")
public class RegistrationStatusEntity extends BaseRegistrationEntity {

	/** The registration type. */
	@Column(name = "reg_type", nullable = false)
	private String registrationType;

	/** The reference registration id. */
	@Column(name = "ref_reg_id")
	private String referenceRegistrationId;

	/** The status code. */
	@Column(name = "status_code", nullable = false)
	private String statusCode;

	/** The lang code. */
	@Column(name = "lang_code", nullable = false)
	private String langCode;

	/** The status comment. */
	@Column(name = "status_comment")
	private String statusComment;

	/** The latest registration transaction id. */
	@Column(name = "latest_trn_id")
	private String latestRegistrationTransactionId;

	/** The is active. */
	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	/** The created by. */
	@Column(name = "cr_by")
	private String createdBy;

	/** The create date time. */
	@Column(name = "cr_dtimes")
	@CreationTimestamp
	private LocalDateTime createDateTime;

	/** The updated by. */
	@Column(name = "upd_by")
	private String updatedBy;

	/** The update date time. */
	@Column(name = "upd_dtimes")
	@UpdateTimestamp
	private LocalDateTime updateDateTime;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The deleted date time. */
	@Column(name = "del_dtimes")
	@UpdateTimestamp
	private LocalDateTime deletedDateTime;

	/** The retry count. */
	@Column(name = "trn_retry_count")
	private Integer retryCount;

	/** The applicant type. */
	@Column(name = "applicant_type")
	private String applicantType;
	
	/**
	 * Instantiates a new registration status entity.
	 */
	public RegistrationStatusEntity() {
		super();

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

	/**
	 * Gets the applicant type.
	 *
	 * @return the applicant type
	 */
	public String getApplicantType() {
		return applicantType;
	}

	/**
	 * Sets the applicant type.
	 *
	 * @param applicantType the new applicant type
	 */
	public void setApplicantType(String applicantType) {
		this.applicantType = applicantType;
	}

}
