/**
 * 
 *
 */

package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Response dto for Machine History Detail
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@Data
public class MachineHistoryDto {

	/**
	 * Field for machine id
	 */
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
	 * Field for machine ip address
	 */
	private String ipAddress;
	/**
	 * Field for machine mac address
	 */
	private String macAddress;
	/**
	 * Field for machine specific id
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
	 * Field to hold Effective Date and time
	 */
	private LocalDateTime effectDateTime;

	private LocalDateTime validityDateTime;

}
