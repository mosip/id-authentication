package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @author Megha Tanga
 *
 */
@Data
@ApiModel(value = "Device Provider", description = "Device Provider Detail resource")
public class DeviceProviderDto {

	/** The id. */
	@NotBlank
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "id", required = true, dataType = "java.lang.String")
	private String id;

	/** The vendor name. */
	@NotBlank
	@Size(min = 1, max = 128)
	@ApiModelProperty(value = "vendorName", required = true, dataType = "java.lang.String")
	private String vendorName;

	/** The address. */
	@NotBlank
	@Size(min = 1, max = 512)
	@ApiModelProperty(value = "address", dataType = "java.lang.String")
	private String address;

	/** The email. */
	@NotBlank
	@Size(min = 1, max = 256)
	@ApiModelProperty(value = "email", dataType = "java.lang.String")
	private String email;

	/** The contact number. */
	@NotBlank
	@Size(min = 1, max = 16)
	@ApiModelProperty(value = "contactNumber", dataType = "java.lang.String")
	private String contactNumber;

	/** The certificate alias. */
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "certificateAlias", dataType = "java.lang.String")
	private String certificateAlias;
	
	/**
	 * Field for is active
	 */
	@NotNull
	private Boolean isActive;

}
