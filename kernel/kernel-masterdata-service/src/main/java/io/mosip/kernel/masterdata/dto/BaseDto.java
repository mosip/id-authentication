package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author Megha Tanga
 * @since 1.0.0
 */
@Data
public class BaseDto {

	private Boolean isActive;

	/**
	 * Field to hold date and time for Validity Date Time
	 */
	private LocalDateTime validityEndDateTime;

	/**
	 * Field to hold creator by name
	 */
	private String createdBy;

	/**
	 * Field to hold date and time of the creation
	 */
	private LocalDateTime createdDateTime;

	/**
	 * Field to hold updater name
	 */
	private String updatedBy;

	/**
	 * Field to hold updated date and time
	 */
	private LocalDateTime updatedDateTime;

	/**
	 * Field to hold boolean value of deleted or not
	 */
	private Boolean isDeleted;

	/**
	 * Field to hold date and time of the deletion
	 */
	private LocalDateTime deletedDateTime;

}