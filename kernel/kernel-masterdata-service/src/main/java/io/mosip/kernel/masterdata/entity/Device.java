package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
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

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device_master", schema = "master")
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
	
	 
	@Column(name = "validity_end_dtimes")
	private LocalDateTime validityDateTime;
	
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "dspec_id", referencedColumnName = "id", insertable = false, updatable = false) })
	private DeviceSpecification deviceSpecification;

}
