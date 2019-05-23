package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.mosip.kernel.masterdata.validator.ValidLangCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MachineRegistrationCenterDto {
	
	@NotBlank
	@Size(min = 1, max = 10)
	private String regCentId;
	/**
	 * Field for machine id
	 */
	@NotBlank
	@Size(min = 1, max = 10)
	@ApiModelProperty(value = "id", required = true, dataType = "java.lang.String")
	private String machineId;
	/**
	 * Field for machine name
	 */
	@NotBlank
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;
	/**
	 * Field for machine serial number
	 */
	@NotBlank
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "serialNum", required = true, dataType = "java.lang.String")
	private String serialNum;
	/**
	 * Field for machine mac address
	 */
	@NotBlank
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "macAddress", required = true, dataType = "java.lang.String")
	private String macAddress;
	/**
	 * Field for machine IP address
	 */

	@Size(min = 1, max = 17)
	@ApiModelProperty(value = "ipAddress", required = true, dataType = "java.lang.String")
	private String ipAddress;
	/**
	 * Field for machine specification Id
	 */
	@NotBlank
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "machineSpecId", required = true, dataType = "java.lang.String")
	private String machineSpecId;
	/**
	 * Field for language code
	 */
	@ValidLangCode
	@NotBlank
	@Size(min = 1, max = 3)
	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;
	/**
	 * Field for is active
	 */
	@NotNull
	private Boolean isActive;
	
	/**
	 * Field to hold date and time for Validity of the Machine
	 */
	private LocalDateTime validityEndDateTime;
		
	/**
	 * Field to hold Machine creator by name
	 */
	private String createdBy;

	/**
	 * Field to hold date and time for creation of the Machine
	 */
	private LocalDateTime createdDateTime;

	/**
	 * Field to hold Machine updater by name
	 */
	private String updatedBy;

	/**
	 * Field to hold updated date and time of the Machine
	 */
	private LocalDateTime updatedDateTime;

	/**
	 * Field to hold Machine is deleted or not
	 *//*
	private Boolean isDeleted;

	*//**
	 * Field to hold date and time for deletion of the Machine
	 *//*
	private LocalDateTime deletedDateTime;*/
}
