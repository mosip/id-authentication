
package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * Response dto for Machine Detail
 * 
 * @author Sowmya
 * 
 *
 */

@Data
public class MachineDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5757105331008865504L;
	private String id;
	/**
	 * Field for machine name
	 */

	private String name;
	/**
	 * Field for machine serial number
	 */

	private String serialNum;
	/**
	 * Field for machine mac address
	 */

	private String macAddress;
	/**
	 * Field for machine IP address
	 */

	private String ipAddress;
	/**
	 * Field for machine specification Id
	 */

	private String machineSpecId;
	/**
	 * Field for language code
	 */

	private String langCode;
	/**
	 * Field for is active
	 */

	private Boolean isActive;
	/**
	 * Field for is validity of the Device
	 */

	private LocalDateTime validityDateTime;

}
