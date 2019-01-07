package io.mosip.registration.processor.core.packet.dto.regcentermachine;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Data	

/**
 * Instantiates a new machine history dto.
 *
 * @param id the id
 * @param name the name
 * @param serialNum the serial num
 * @param ipAddress the ip address
 * @param macAddress the mac address
 * @param mspecId the mspec id
 * @param langCode the lang code
 * @param isActive the is active
 * @param createdBy the created by
 * @param createdtime the createdtime
 * @param updatedBy the updated by
 * @param updatedtime the updatedtime
 * @param isDeleted the is deleted
 * @param deletedtime the deletedtime
 * @param effectDtimes the effect dtimes
 */
@AllArgsConstructor

/**
 * Instantiates a new machine history dto.
 */
@NoArgsConstructor
public class MachineHistoryDto {

	/** Field for machine id. */
	private String id;
	
	/** Field for machine name. */
	private String name;
	
	/** Field for machine serial number. */
	private String serialNum;
	
	/** Field for machine ip address. */
	private String ipAddress;
	
	/** Field for machine mac address. */
	private String macAddress;
	
	/** Field for machine specific id. */
	private String mspecId;
	
	/** Field for language code. */
	private String langCode;
	
	/** Field for is active. */
	private Boolean isActive;

	/** Field to hold creator name. */
	private String createdBy;

	/** Field to hold created date and time. */
	private LocalDateTime createdtime;

	/** Field to hold updater name. */
	private String updatedBy;

	/** Field to hold updated date and time. */
	private LocalDateTime updatedtime;

	/** Field to hold is deleted. */
	private Boolean isDeleted;

	/** Field to hold deleted date and time. */
	private LocalDateTime deletedtime;

	/** Field to hold Effective Date and time. */
	private LocalDateTime effectDtimes;

}

