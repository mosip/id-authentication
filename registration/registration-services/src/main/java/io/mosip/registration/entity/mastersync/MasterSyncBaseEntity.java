package io.mosip.registration.entity.mastersync;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@MappedSuperclass
public class MasterSyncBaseEntity {

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "cr_by")
	private String createdBy;

	@Column(name = "cr_dtimes")
	private LocalDateTime createdDateTime;

	@Column(name = "upd_by")
	private String updatedBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDateTime;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime deletedDateTime;

	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDateTime
	 */
	public LocalDateTime getCreatedDateTime() {
		return createdDateTime;
	}

	/**
	 * @param createdDateTime the createdDateTime to set
	 */
	public void setCreatedDateTime(LocalDateTime createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return the updatedDateTime
	 */
	public LocalDateTime getUpdatedDateTime() {
		return updatedDateTime;
	}

	/**
	 * @param updatedDateTime the updatedDateTime to set
	 */
	public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	/**
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the deletedDateTime
	 */
	public LocalDateTime getDeletedDateTime() {
		return deletedDateTime;
	}

	/**
	 * @param deletedDateTime the deletedDateTime to set
	 */
	public void setDeletedDateTime(LocalDateTime deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}

}
