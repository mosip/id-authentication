package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Center Machine entity details
 * 
 * @author Yaswanth S
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "reg_center_machine")
public class CenterMachine {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4546131262621540483L;

	@EmbeddedId
	@Column(name = "pk_cntrm_usr_id")
	private CenterMachineId centerMachineId;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;
	@Column(name = "cr_by", length = 24, nullable = false)
	private String createdBy;
	@Column(name = "cr_dtimesz", nullable = false)
	private Timestamp createdDateTime;
	@Column(name = "upd_by", length = 24)
	private String updatedBy;
	@Column(name = "upd_dtimesz")
	private Timestamp updatedDateTime;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimesz")
	private Timestamp deletedDateTime;

	public CenterMachineId getCenterMachineId() {
		return centerMachineId;
	}

	public void setCenterMachineId(CenterMachineId centerMachineId) {
		this.centerMachineId = centerMachineId;
	}

	public Boolean isActive() {
		return isActive;
	}

	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Timestamp createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Timestamp getUpdatedDateTime() {
		return updatedDateTime;
	}

	public void setUpdatedDateTime(Timestamp updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	public Boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Timestamp getDeletedDateTime() {
		return deletedDateTime;
	}

	public void setDeletedDateTime(Timestamp deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}

}
