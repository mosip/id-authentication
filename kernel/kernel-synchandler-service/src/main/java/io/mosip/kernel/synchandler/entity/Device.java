package io.mosip.kernel.synchandler.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * Entity for Device Details
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "device_master", schema = "master")
public class Device  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5585825705521742941L;

	/**
	 * Field for device ID
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	private String id;

	/**
	 * Field for device name
	 */
	@Column(name = "name", nullable = false, length = 64)
	private String name;

	/**
	 * Field for device serial number
	 */
	@Column(name = "serial_num", nullable = false, length = 64)
	private String serialNum;

	/**
	 * Field for device ip address
	 */
	@Column(name = "ip_address", length = 17)
	private String ipAddress;

	/**
	 * Field for device mac address
	 */
	@Column(name = "mac_address", nullable = false, length = 64)
	private String macAddress;

	/**
	 * Field for device specific id
	 */
	@Column(name = "dspec_id", nullable = false, length = 36)
	private String deviceSpecId;

	/**
	 * Field for language code
	 */
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;


	
	/**
	 * Field to hold date and time for Validity of the Device
	 */
	@Column(name="validity_end_dtimes")
	private LocalDateTime validityEndDateTime; 
	
	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "cr_by", nullable = false, length = 24)
	private String createdBy;

	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdDateTime;

	@Column(name = "upd_by")
	private String updatedBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDateTime;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime deletedDateTime;

}
