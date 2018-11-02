package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The persistent class for the reg_center_machine database table.
 * 
 * @author Horteppa M1048399
 * @author Girish Yarru
 */
@Entity
@Table(name = "reg_center_machine", schema = "regprc")
public class RegCenterMachineEntity extends BasePacketEntity<RegCenterMachinePKEntity> implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The prereg id. */
	@Column(name = "prereg_id", nullable = false)
	private String preregId;

	/** The machine id. */
	@Column(name = "machine_id", nullable = false)
	private String machineId;

	/** The cntr id. */
	@Column(name = "cntr_id", nullable = false)
	private String cntrId;

	/** The latitude. */
	private String latitude;

	/** The longitude. */
	private String longitude;

	/** The is active. */
	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	/** The cr by. */
	@Column(name = "cr_by")
	private String crBy = "MOSIP_SYSTEM";

	/** The cr dtimesz. */
	@Column(name = "cr_dtimesz", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	/** The upd by. */
	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	/** The upd dtimesz. */
	@Column(name = "upd_dtimesz")
	@UpdateTimestamp
	private LocalDateTime updDtimesz;

	/** The del dtimesz. */
	@Column(name = "del_dtimesz")
	@UpdateTimestamp
	private LocalDateTime delDtimesz;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/**
	 * Gets the prereg id.
	 *
	 * @return the prereg id
	 */
	public String getPreregId() {
		return preregId;
	}

	/**
	 * Sets the prereg id.
	 *
	 * @param preregId the new prereg id
	 */
	public void setPreregId(String preregId) {
		this.preregId = preregId;
	}

	/**
	 * Gets the machine id.
	 *
	 * @return the machine id
	 */
	public String getMachineId() {
		return machineId;
	}

	/**
	 * Sets the machine id.
	 *
	 * @param machineId the new machine id
	 */
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

	/**
	 * Gets the cntr id.
	 *
	 * @return the cntr id
	 */
	public String getCntrId() {
		return cntrId;
	}

	/**
	 * Sets the cntr id.
	 *
	 * @param cntrId the new cntr id
	 */
	public void setCntrId(String cntrId) {
		this.cntrId = cntrId;
	}

	/**
	 * Gets the latitude.
	 *
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * Sets the latitude.
	 *
	 * @param latitude the new latitude
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * Gets the longitude.
	 *
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}

	/**
	 * Sets the longitude.
	 *
	 * @param longitude the new longitude
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
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
	 * @param isActive the new checks if is active
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * Gets the cr by.
	 *
	 * @return the cr by
	 */
	public String getCrBy() {
		return crBy;
	}

	/**
	 * Sets the cr by.
	 *
	 * @param crBy the new cr by
	 */
	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	/**
	 * Gets the cr dtimesz.
	 *
	 * @return the cr dtimesz
	 */
	public LocalDateTime getCrDtimesz() {
		return crDtimesz;
	}

	/**
	 * Sets the cr dtimesz.
	 *
	 * @param crDtimesz the new cr dtimesz
	 */
	public void setCrDtimesz(LocalDateTime crDtimesz) {
		this.crDtimesz = crDtimesz;
	}

	/**
	 * Gets the upd by.
	 *
	 * @return the upd by
	 */
	public String getUpdBy() {
		return updBy;
	}

	/**
	 * Sets the upd by.
	 *
	 * @param updBy the new upd by
	 */
	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	/**
	 * Gets the upd dtimesz.
	 *
	 * @return the upd dtimesz
	 */
	public LocalDateTime getUpdDtimesz() {
		return updDtimesz;
	}

	/**
	 * Sets the upd dtimesz.
	 *
	 * @param updDtimesz the new upd dtimesz
	 */
	public void setUpdDtimesz(LocalDateTime updDtimesz) {
		this.updDtimesz = updDtimesz;
	}

	/**
	 * Gets the del dtimesz.
	 *
	 * @return the del dtimesz
	 */
	public LocalDateTime getDelDtimesz() {
		return delDtimesz;
	}

	/**
	 * Sets the del dtimesz.
	 *
	 * @param delDtimesz the new del dtimesz
	 */
	public void setDelDtimesz(LocalDateTime delDtimesz) {
		this.delDtimesz = delDtimesz;
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
	 * @param isDeleted the new checks if is deleted
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * Instantiates a new reg center machine entity.
	 */
	public RegCenterMachineEntity() {
		super();
	}

}