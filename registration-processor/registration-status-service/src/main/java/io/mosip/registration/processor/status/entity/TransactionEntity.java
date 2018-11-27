package io.mosip.registration.processor.status.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The Class TransactionEntity.
 */
@Entity
@Table(name = "registration_transaction", schema = "regprc")
public class TransactionEntity extends BaseRegistrationEntity {

	/** The registration id. */
	@Column(name = "reg_id")
	private String registrationId;

	/** The trntypecode. */
	@Column(name = "trn_type_code")
	private String trntypecode;

	/** The remarks. */
	@Column(name = "remarks")
	private String remarks;

	/** The parentid. */
	@Column(name = "parent_regtrn_id")
	private String parentid;

	/** The status code. */
	@Column(name = "status_code")
	private String statusCode;

	/** The lang code. */
	@Column(name = "lang_code")
	private String langCode = "eng";

	/** The status comment. */
	@Column(name = "status_comment")
	private String statusComment;

	/** The reference id. */
	@Column(name = "ref_id")
	private String referenceId;

	/** The reference id type. */
	@Column(name = "ref_id_type")
	private String referenceIdType;

	/** is deleted? */
	@Column(name = "is_deleted", length = 32)
	private Boolean isDeleted ;

	/** The update date time. */
	@Column(name = "del_dtimes")
	@UpdateTimestamp
	private LocalDateTime deleteDateTime;

	/** The created by. */
	@Column(name = "cr_by")
	private String createdBy = "MOSIP_SYSTEM";

	/** The create date time. */
	@Column(name = "cr_dtimes")
	@CreationTimestamp
	private LocalDateTime createDateTime;

	/** The updated by. */
	@Column(name = "upd_by", length = 32)
	private String updatedBy = "MOSIP_SYSTEM";

	/** The update date time. */
	@Column(name = "upd_dtimes")
	@UpdateTimestamp
	private LocalDateTime updateDateTime;

	/**
	 * Instantiates a new transaction entity.
	 */
	public TransactionEntity() {
		super();
	}

	/**
	 * Instantiates a new transaction entity.
	 *
	 * @param transactionId
	 *            the transaction id
	 * @param registrationId
	 *            the registration id
	 * @param parentid
	 *            the parentid
	 * @param trntypecode
	 *            the trntypecode
	 * @param statusCode
	 *            the status code
	 */
	public TransactionEntity(String transactionId, String registrationId, String parentid, String trntypecode,
			String statusCode, String statusComment) {
		id = transactionId;
		this.registrationId = registrationId;
		this.parentid = parentid;
		this.trntypecode = trntypecode;
		this.statusCode = statusCode;
		this.statusComment = statusComment;

	}

	/**
	 * Get Enrolment_Id from transaction table.
	 * 
	 * @return the enrolmentId
	 */
	public String getRegistrationId() {
		return registrationId;
	}

	/**
	 * Set RegistrationId to transaction table.
	 *
	 * @param registrationId
	 *            the new registration id
	 */
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	/**
	 * Get parent id from transaction table.
	 * 
	 * @return the parentid
	 */
	public String getParentid() {
		return parentid;
	}

	/**
	 * Set parent id to transaction table.
	 * 
	 * @param parentid
	 *            the parentid to set
	 */
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	/**
	 * Get trn_type_code from transaction table.
	 * 
	 * @return the trntypecode
	 */
	public String getTrntypecode() {
		return trntypecode;
	}

	/**
	 * Set trn_type_code to transaction table.
	 * 
	 * @param trntypecode
	 *            the trntypecode to set
	 */
	public void setTrntypecode(String trntypecode) {
		this.trntypecode = trntypecode;
	}

	/**
	 * Get remarks from transaction table.
	 * 
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * Set remarks to transaction table.
	 * 
	 * @param remarks
	 *            the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * Get Status Code from transaction table.
	 * 
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * Set status Code to transaction table.
	 * 
	 * @param statusCode
	 *            the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Get lang_Code from transaction table.
	 * 
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * Set lang Code to transaction table.
	 * 
	 * @param langCode
	 *            the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	/**
	 * Get referenceId from transaction table.
	 * 
	 * @return the referenceId
	 */
	public String getReferenceId() {
		return referenceId;
	}

	/**
	 * Set referenceId to transaction table.
	 * 
	 * @param referenceId
	 *            the referenceId to set
	 */
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	/**
	 * Get referenceIdType from transaction table.
	 * 
	 * @return the referenceIdType
	 */
	public String getReferenceIdType() {
		return referenceIdType;
	}

	/**
	 * Set referenceIdType to transaction table.
	 * 
	 * @param referenceIdType
	 *            the referenceIdType to set
	 */
	public void setReferenceIdType(String referenceIdType) {
		this.referenceIdType = referenceIdType;
	}

	/**
	 * Get status Comment from transaction table.
	 * 
	 * @return the statusComment
	 */
	public String getStatusComment() {
		return statusComment;
	}

	/**
	 * Set status comment to transaction table.
	 * 
	 * @param statusComment
	 *            the statusComment to set
	 */
	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}

	

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public LocalDateTime getDeleteDateTime() {
		return deleteDateTime;
	}

	public void setDeleteDateTime(LocalDateTime deleteDateTime) {
		this.deleteDateTime = deleteDateTime;
	}

	/**
	 * Get created By from transaction table.
	 * 
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Set created By to transaction table.
	 * 
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Get create Date Time from transaction table.
	 * 
	 * @return the createDateTime
	 */

	public LocalDateTime getCreateDateTime() {
		return createDateTime;
	}

	/**
	 * Set create Date Time to transaction table.
	 * 
	 * @param createDateTime
	 *            the createDateTime to set
	 */
	public void setCreateDateTime(LocalDateTime createDateTime) {
		this.createDateTime = createDateTime;
	}

	/**
	 * Get updated By from transaction table.
	 * 
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * Set updated By to transaction table.
	 * 
	 * @param updatedBy
	 *            the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * Get update Date Time By from transaction table.
	 * 
	 * @return the updateDateTime
	 */
	public LocalDateTime getUpdateDateTime() {
		return updateDateTime;
	}

	/**
	 * Set update Date Time to transaction table.
	 * 
	 * @param updateDateTime
	 *            the updateDateTime to set
	 */
	public void setUpdateDateTime(LocalDateTime updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

}
