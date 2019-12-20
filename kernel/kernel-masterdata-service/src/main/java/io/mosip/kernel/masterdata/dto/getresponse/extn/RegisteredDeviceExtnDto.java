package io.mosip.kernel.masterdata.dto.getresponse.extn;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.mosip.kernel.masterdata.dto.DigitalIdDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Registered Device", description = "Registered Device resource")
public class RegisteredDeviceExtnDto extends BaseDto{
	
	@NotBlank
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;
	
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
	
	
	/**
	 * Field for Status Code
	 * Status should only have standard values - “Registered”, “Retired”, “Revoked”
	 */
	@NotBlank
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "statusCode", required = true, dataType = "java.lang.String")
	private String statusCode;

	/**
	 * Field for device name
	 */
	@NotBlank
	@Size(min = 1, max = 256)
	@ApiModelProperty(value = "deviceId", required = true, dataType = "java.lang.String")
	private String deviceId;

	/**
	 * Field for device name
	 */
	
	@Size(min = 0, max = 256)
	@ApiModelProperty(value = "deviceSubId", required = true, dataType = "java.lang.String")
	private String deviceSubId;
	/**
	 * Field for device name
	 * Purpose level should only accept two values, “Registration” or “Auth”.
	 */
	@NotBlank
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "purpose", required = true, dataType = "java.lang.String")
	private String purpose;

	/**
	 * Field for device name
	 */
	@NotBlank
	@Size(min = 1, max = 128)
	@ApiModelProperty(value = "firmware", required = true, dataType = "java.lang.String")
	private String firmware;

	/**
	 * Field for device name
	 */
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime expiryDate;

	/**
	 * Field for device name
	 * Certificate level should only accept two values. “L0” or “L1”
	 */
	@NotBlank
	@Size(min = 1, max = 3)
	@ApiModelProperty(value = "certificationLevel", required = true, dataType = "java.lang.String")
	private String certificationLevel;

	/**
	 * Field for device name
	 */
	
	@Size(min = 0, max = 36)
	@ApiModelProperty(value = "foundationalTPId", required = true, dataType = "java.lang.String")
	private String foundationalTPId;

	/**
	 * Field for device name
	 *//*
	
	@Size(min = 0, max = 512)
	@ApiModelProperty(value = "foundationalTrustSignature", required = true, dataType = "java.lang.String")
	private String foundationalTrustSignature;

	*//**
	 * Field for device name
	 *//*
	@ApiModelProperty(value = "foundationalTrustCertificate", required = true, dataType = "java.lang.String")
	private byte[] foundationalTrustCertificate;

	*//**
	 * Field for device name
	 *//*
	@NotBlank
	@Size(min = 1, max = 512)
	@ApiModelProperty(value = "dProviderSignature", required = true, dataType = "java.lang.String")
	private String deviceProviderSignature;*/
	
	
	private DigitalIdDto digitalIdDto;
	
	private String ecodedEntity;

}
