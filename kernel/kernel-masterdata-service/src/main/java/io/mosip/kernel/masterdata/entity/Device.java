package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * Entity for Device Details
 * 
 */
/**
 * @author Sidhant Agarwal
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Table(name = "device_master", schema = "master")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Device extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5585825705521742941L;

	/**
	 * Field for device ID
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	private String code;

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

}
