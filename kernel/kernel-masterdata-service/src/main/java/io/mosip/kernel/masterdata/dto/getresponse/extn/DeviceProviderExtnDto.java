package io.mosip.kernel.masterdata.dto.getresponse.extn;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Megha Tanga
 *
 */

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Device Provider", description = "Device Provider resource")
public class DeviceProviderExtnDto extends BaseDto {

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
	@Size(min = 1, max = 512)
	@ApiModelProperty(value = "address", dataType = "java.lang.String")
	private String address;

	/** The email. */
	@Size(min = 1, max = 256)
	@ApiModelProperty(value = "email", dataType = "java.lang.String")
	private String email;

	/** The contact number. */
	@Size(min = 1, max = 16)
	@ApiModelProperty(value = "contactNumber", dataType = "java.lang.String")
	private String contactNumber;

	/** The certificate alias. */
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "certificateAlias", dataType = "java.lang.String")
	private String certificateAlias;

}
