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
public class CenterMachine extends RegistrationCommonFields {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4546131262621540483L;

	@EmbeddedId
	@Column(name = "pk_cntrmac_usr_id")
	private CenterMachineId centerMachineId;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp deletedDateTime;
	/**
	 * @return the centerMachineId
	 */
	public CenterMachineId getCenterMachineId() {
		return centerMachineId;
	}
	/**
	 * @param centerMachineId the centerMachineId to set
	 */
	public void setCenterMachineId(CenterMachineId centerMachineId) {
		this.centerMachineId = centerMachineId;
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
	public Timestamp getDeletedDateTime() {
		return deletedDateTime;
	}
	/**
	 * @param deletedDateTime the deletedDateTime to set
	 */
	public void setDeletedDateTime(Timestamp deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	

}
