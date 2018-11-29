
package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response dto for Machine Detail
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MachineDto {

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

}
