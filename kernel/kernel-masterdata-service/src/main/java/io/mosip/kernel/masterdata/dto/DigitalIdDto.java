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
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "serialNo", required = true, dataType = "java.lang.String")
	private String serialNo;

	/** The Device Provider Name. */
	@NotBlank
	@Size(min = 1, max = 128)
	@ApiModelProperty(value = "dp", required = true, dataType = "java.lang.String")
	private String dp;

	/** The Device Provider id. */
	@NotBlank
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "dpId", required = true, dataType = "java.lang.String")
	private String dpId;

	/** The make. */
	@NotBlank
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "make", required = true, dataType = "java.lang.String")
	private String make;

	/** The model. */
	@NotBlank
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "mpdel", required = true, dataType = "java.lang.String")
	private String model;

	/** type */
	@ApiModelProperty(value = "type", dataType = "java.lang.String")
	private String type;
	
	/** type */
	@ApiModelProperty(value = "subType", dataType = "java.lang.String")
	private String subType;

	/** The date time. */
	private String dateTime;
}
