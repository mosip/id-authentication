
package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Response dto for Machine Detail
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@Data
@ApiModel(value = "Machine", description = "Machine Detail resource")
public class MachineDto {

	/**
	 * Field for machine id
	 */
	@NotNull
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "id", required = true, dataType = "java.lang.String")
	private String id;
	/**
	 * Field for machine name
	 */
	@NotNull
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;
	/**
	 * Field for machine serial number
	 */
	@NotNull
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "serialNum", required = true, dataType = "java.lang.String")
	private String serialNum;
	/**
	 * Field for machine mac address
	 */
	@NotNull
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "macAddress", required = true, dataType = "java.lang.String")
	private String macAddress;
	/**
	 * Field for machine IP address
	 */
	
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "ipAddress", required = true, dataType = "java.lang.String")
	private String ipAddress;
	/**
	 * Field for machine specification Id
	 */
	@NotNull
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "machineSpecId", required = true, dataType = "java.lang.String")
	private String machineSpecId;
	/**
	 * Field for language code
	 */
	@NotNull
	@Size(min = 1, max = 3)
	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;
	/**
	 * Field for is active
	 */
	@NotNull	
	private Boolean isActive;
	/**
	 * Field for is validity of the Device
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime validityDateTime;
	
	private ZonedDateTime validityDateTimeZ;
	

}
