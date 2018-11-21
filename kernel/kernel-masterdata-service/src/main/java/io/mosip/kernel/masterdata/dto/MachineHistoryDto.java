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
	private String mspecId;
	/**
	 * Field for language code
	 */
	private String langCode;
	/**
	 * Field for is active
	 */
	private Boolean isActive;

	/**
	 * Field to hold creator name
	 */
	private String createdBy;

	/**
	 * Field to hold created date and time
	 */
	private LocalDateTime createdtime;

	/**
	 * Field to hold updater name
	 */
	private String updatedBy;

	/**
	 * Field to hold updated date and time
	 */
	private LocalDateTime updatedtime;

	/**
	 * Field to hold is deleted
	 */
	private Boolean isDeleted;

	/**
	 * Field to hold deleted date and time
	 */
	private LocalDateTime deletedtime;

	/**
	 * Field to hold Effective Date and time
	 */
	private LocalDateTime effectDtimes;

}
