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
 * Entity for Machine Details
 * 
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "machine_master", schema = "master")
public class MachineDetail extends BaseEntity implements Serializable {

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
	 * Field for machine serial number
	 */
	@Column(name = "serial_num", nullable = false, length = 64)
	private String serialNum;

	/**
	 * Field for machine ip address
	 */
	@Column(name = "ip_address", length = 17)
	private String ipAddress;
	/**
	 * Field for machine mac address
	 */
	@Column(name = "mac_address", nullable = false, length = 64)
	private String macAddress;

	/**
	 * Field for machine specific id
	 */
	@Column(name = "mspec_id", nullable = false, length = 36)
	private String machineSpecId;

	/**
	 * Field for language code
	 */
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

}
