package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.mosip.kernel.masterdata.validator.registereddevice.ValidType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Innner Json for DigitalId
 * 
 * @author Srinivasan
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Data
public class DigitalIdDto {

	/** The serial no. */
	@NotBlank
	@Size(min=1, max=64)
	@ApiModelProperty(value = "serialNo", required = true, dataType = "java.lang.String")
	private String serialNo;
	
	
	/** The Device Provider Name. */
	@NotBlank
	@Size(min=1, max=128)
	@ApiModelProperty(value = "dp", required = true, dataType = "java.lang.String")
	private String dp;
	

	/** The Device Provider id. */
	@NotBlank
	@Size(min=1, max=36)
	@ApiModelProperty(value = "dpId", required = true, dataType = "java.lang.String")
	private String dpId;
	
	
	/** The make. */
	@NotBlank
	@Size(min=1, max=36)
	@ApiModelProperty(value = "make", required = true, dataType = "java.lang.String")
	private String make;

	/** The model. */
	@NotBlank
	@Size(min=1, max=36)
	@ApiModelProperty(value = "mpdel", required = true, dataType = "java.lang.String")
	private String model;

	/** type *//*
	@ApiModelProperty(value = "type", dataType = "java.lang.String")
	//@ValidType(message = "Type Value is Invalid")
	private String type;*/

	/** The date time. */
	private String dateTime;
	
	
	/**
	 * Field for deviceTypeCode
	 */
	@NotBlank
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "deviceTypeCode", required = true, dataType = "java.lang.String")
	private String deviceTypeCode;

	/**
	 * Field for deviceSubTypeCode
	 */
	@NotBlank
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "devicesTypeCode", required = true, dataType = "java.lang.String")
	private String deviceSTypeCode;
}
