
package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Table(name = "machine_master_h", schema = "master")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MachineHistory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5585825705521742941L;

	/**
	 * Field for machine ID
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	private String id;

	/**
	 * Field for machine name
	 */
	@Column(name = "name", nullable = false, length = 64)
	private String name;

	/**
	 * Field for machine mac address
	 */
	@Column(name = "mac_address", nullable = false, length = 64)
	private String macAddress;

	/**
	 * Field for machine serial number
	 */
	@Column(name = "serial_num", nullable = false)
	private String serialNum;

	/**
	 * Field for machine ip address
	 */
	@Column(name = "ip_address", length = 64)
	private String ipAddress;

	/**
	 * Field for machine specific id
	 */
	@Column(name = "mspec_id", nullable = false, length = 36)
	private String mspecId;

	/**
	 * Field for language code
	 */
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

	/**
	 * Field for is active
	 */
	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	/**
	 * Field to hold creator name
	 */
	@Column(name = "cr_by", nullable = false, length = 32)
	private String createdBy;

	/**
	 * Field to hold created date and time
	 */
	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdtime;
	/**
	 * Field to hold updater name
	 */
	@Column(name = "upd_by", length = 32)
	private String updatedBy;
	/**
	 * Field to hold updated date and time
	 */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedTime;

	/**
	 * Field to hold updated date and time
	 */
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	/**
	 * Field to hold deleted date and time
	 */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedtime;
	/**
	 * Field to hold effected date and time
	 */
	@Column(name = "eff_dtimes", nullable = false)
	private LocalDateTime effectDtimes;

}
