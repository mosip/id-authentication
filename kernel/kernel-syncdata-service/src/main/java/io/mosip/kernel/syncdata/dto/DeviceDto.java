package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response dto for Device Detail
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDto {

	/**
	 * Field for device id
	 */
	private String id;
	/**
	 * Field for device name
	 */
	private String name;
	/**
	 * Field for device serial number
	 */
	private String serialNum;
	/**
	 * Field for device device specification Id
	 */
	private String deviceSpecId;
	/**
	 * Field for device mac address
	 */
	private String macAddress;
	/**
	 * Field for device ip address
	 */
	private String ipAddress;
	/**
	 * Field for language code
	 */
	private String langCode;
	/**
	 * Field for is active
	 */
	private boolean isActive;

}
