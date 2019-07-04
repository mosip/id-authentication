package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.mosip.kernel.masterdata.dto.getresponse.extn.BaseDto;
import io.mosip.kernel.masterdata.validator.ValidLangCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
/**
 * 
 * @author M1047717
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MachineRegistrationCenterDto extends BaseDto{
	/**
	 * Field for registration Center Id
	 */
	@NotBlank
	@Size(min = 1, max = 10)
	@ApiModelProperty(value = "id", required = true, dataType = "java.lang.String")
	private String regCentId;
	/**
	 * Field for machine id
	 */
	@NotBlank
	@Size(min = 1, max = 10)
	@ApiModelProperty(value = "id", required = true, dataType = "java.lang.String")
	private String id;
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
	


}
