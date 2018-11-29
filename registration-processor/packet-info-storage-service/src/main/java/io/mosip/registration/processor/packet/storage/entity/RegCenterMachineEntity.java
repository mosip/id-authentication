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
	@Column(name = "regcntr_id", nullable = false)
	private String regcntr_id;

	/** The latitude. */
	private String latitude;

	/** The longitude. */
	private String longitude;
	
	/** The packet creation time. */
	private LocalDateTime packetCreationDate;
	
	/** The is active. */
	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	/** The cr by. */
	@Column(name = "cr_by")
	private String crBy = "MOSIP_SYSTEM";

	/** The cr dtimes. */
	@Column(name = "cr_dtimes", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimes;

	/** The upd by. */
	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	/** The upd dtimes. */
	@Column(name = "upd_dtimes")
	@UpdateTimestamp
	private LocalDateTime updDtimes;

	/** The del dtimes. */
	@Column(name = "del_dtimes")
	@UpdateTimestamp
	private LocalDateTime delDtimes;

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
	 * @param preregId
	 *            the new prereg id
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
	 * @param machineId
	 *            the new machine id
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
		return regcntr_id;
	}

	/**
	 * Sets the cntr id.
	 *
	 * @param cntrId
	 *            the new cntr id
	 */
	public void setCntrId(String cntrId) {
		this.regcntr_id = cntrId;
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
	 * @param latitude
	 *            the new latitude
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
	 * @param longitude
	 *            the new longitude
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
	 * @param isActive
	 *            the new checks if is active
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
	 * @param crBy
	 *            the new cr by
	 */
	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	/**
	 * Gets the cr dtimes.
	 *
	 * @return the cr dtimes
	 */
	public LocalDateTime getCrDtimes() {
		return crDtimes;
	}

	/**
	 * Sets the cr dtimes.
	 *
	 * @param crDtimes the new cr dtimes
	 */
	public void setCrDtimes(LocalDateTime crDtimes) {
		this.crDtimes = crDtimes;
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
	 * @param updBy
	 *            the new upd by
	 */
	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	/**
	 * Gets the upd dtimes.
	 *
	 * @return the upd dtimes
	 */
	public LocalDateTime getUpdDtimes() {
		return updDtimes;
	}

	/**
	 * Sets the upd dtimes.
	 *
	 * @param updDtimes the new upd dtimes
	 */
	public void setUpdDtimes(LocalDateTime updDtimes) {
		this.updDtimes = updDtimes;
	}

	/**
	 * Gets the del dtimes.
	 *
	 * @return the del dtimes
	 */
	public LocalDateTime getDelDtimes() {
		return delDtimes;
	}

	/**
	 * Sets the del dtimes.
	 *
	 * @param delDtimes the new del dtimes
	 */
	public void setDelDtimes(LocalDateTime delDtimes) {
		this.delDtimes = delDtimes;
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
	 * Instantiates a new reg center machine entity.
	 */
	public RegCenterMachineEntity() {
		super();
	}

	public LocalDateTime getPacketCreationDate() {
		return packetCreationDate;
	}

	public void setPacketCreationDate(LocalDateTime packetCreationDate) {
		this.packetCreationDate = packetCreationDate;
	}

}