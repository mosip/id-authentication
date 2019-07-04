/**
 * 
 */
package io.mosip.registration.processor.status.entity;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The Class SyncRegistrationEntity.
 *
 * @author M1047487
 */
@Entity
@Table(name = "registration_list", schema = "regprc")
public class SyncRegistrationEntity extends BaseRegistrationEntity {

	/** The registration id. */
	@Column(name = "reg_id", nullable = false)
	private String registrationId;

	/** The registration type. */
	@Column(name = "reg_type")
	private String registrationType;

	/** The lang code. */
	@Column(name = "packet_checksum", nullable = false)
	private String packetHashValue;

	/** The lang code. */
	@Column(name = "packet_size", nullable = false)
	private BigInteger packetSize;

	/** The status code. */
	@Column(name = "client_status_code")
	private String supervisorStatus;

	/** The status comment. */
	@Column(name = "client_status_comment")
	private String supervisorComment;

	/** The doc store. */
	@Column(name = "additional_info")
	private byte[] optionalValues;

	/** The status code. */
	@Column(name = "status_code")
	private String statusCode;

	/** The status comment. */
	@Column(name = "status_comment")
	private String statusComment;

	/** The lang code. */
	@Column(name = "lang_code", nullable = false)
	private String langCode;

	/** The created by. */
	@Column(name = "cr_by", nullable = false)
	private String createdBy;

	/** The create date time. */
	@Column(name = "cr_dtimes",updatable=false )
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
	private LocalDateTime deletedDateTime;

	/**
	 * Instantiates a new sync registration entity.
	 */
	public SyncRegistrationEntity() {
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
	 * Gets the packet hash value.
	 *
	 * @return the packet hash value
	 */
	public String getPacketHashValue() {
		return packetHashValue;
	}

	/**
	 * Sets the packet hash value.
	 *
	 * @param packetHashValue
	 *            the new packet hash value
	 */
	public void setPacketHashValue(String packetHashValue) {
		this.packetHashValue = packetHashValue;
	}

	/**
	 * Gets the supervisor status.
	 *
	 * @return the supervisor status
	 */
	public String getSupervisorStatus() {
		return supervisorStatus;
	}

	/**
	 * Sets the supervisor status.
	 *
	 * @param supervisorStatus
	 *            the new supervisor status
	 */
	public void setSupervisorStatus(String supervisorStatus) {
		this.supervisorStatus = supervisorStatus;
	}

	/**
	 * Gets the supervisor comment.
	 *
	 * @return the supervisor comment
	 */
	public String getSupervisorComment() {
		return supervisorComment;
	}

	/**
	 * Sets the supervisor comment.
	 *
	 * @param supervisorComment
	 *            the new supervisor comment
	 */
	public void setSupervisorComment(String supervisorComment) {
		this.supervisorComment = supervisorComment;
	}

	/**
	 * Gets the packet size.
	 *
	 * @return the packet size
	 */
	public BigInteger getPacketSize() {
		return packetSize;
	}

	/**
	 * Sets the packet size.
	 *
	 * @param packetSize
	 *            the new packet size
	 */
	public void setPacketSize(BigInteger packetSize) {
		this.packetSize = packetSize;
	}

	/**
	 * Gets the optional values.
	 *
	 * @return the optional values
	 */
	public byte[] getOptionalValues() {
		return optionalValues;
	}

	/**
	 * Sets the optional values.
	 *
	 * @param optionalValues
	 *            the new optional values
	 */
	public void setOptionalValues(byte[] optionalValues) {
		this.optionalValues = optionalValues!=null?optionalValues:null;
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
	 * Gets the checks if is deleted.
	 *
	 * @return the checks if is deleted
	 */
	public Boolean getIsDeleted() {
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

}
