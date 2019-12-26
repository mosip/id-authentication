package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.mosip.kernel.masterdata.validator.registereddevice.ValidCertificateLevel;
import io.mosip.kernel.masterdata.validator.registereddevice.ValidFoundational;
import io.mosip.kernel.masterdata.validator.registereddevice.ValidPurpose;
import io.mosip.kernel.masterdata.validator.registereddevice.ValidStatusCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Device", description = "Device Detail resource")
@ValidFoundational(baseField = "certificationLevel", matchField = { "foundationalTPId", "foundationalTrustSignature",
		"foundationalTrustCertificate" })
public class RegisteredDevicePostReqDto {

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
	@ApiModelProperty(value = "deviceSTypeCode", required = true, dataType = "java.lang.String")
	private String deviceSTypeCode;

	/**
	 * Field for Status Code Status should only have standard values - “Registered”,
	 * “Retired”, “Revoked”
	 */
	@NotBlank
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "statusCode", required = true, dataType = "java.lang.String")
	@ValidStatusCode(message = "Invalid Status received")
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
	 * Field for device name Purpose level should only accept two values,
	 * “Registration” or “Auth”.
	 */
	@NotBlank
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "purpose", required = true, dataType = "java.lang.String")
	@ValidPurpose(message = "Invalid Purpose received")
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
	 * Field for device name Certificate level should only accept two values. “L0”
	 * or “L1”
	 */
	@NotBlank
	@Size(min = 1, max = 3)
	@ApiModelProperty(value = "certificationLevel", required = true, dataType = "java.lang.String")
	@ValidCertificateLevel(message = "Invalid Certification level received")
	private String certificationLevel;

	/**
	 * Field for device name
	 */

	@Size(min = 0, max = 36)
	@ApiModelProperty(value = "foundationalTPId", required = true, dataType = "java.lang.String")
	private String foundationalTPId;

	/**
	 * Field for device name
	 */

	@Size(min = 0, max = 512)
	@ApiModelProperty(value = "foundationalTrustSignature", required = true, dataType = "java.lang.String")
	private String foundationalTrustSignature;

	/**
	 * Field for device name
	 */

	@ApiModelProperty(value = "foundationalTrustCertificate", required = true, dataType = "java.lang.byte")
	private byte[] foundationalTrustCertificate;

	/**
	 * Field for device name
	 */
	@NotBlank
	@Size(min = 1, max = 512)
	@ApiModelProperty(value = "dProviderSignature", required = true, dataType = "java.lang.String")
	private String deviceProviderSignature;
	
	@NotNull
	@Valid
	private DigitalIdDeviceRegisterDto digitalIdDto;

}
