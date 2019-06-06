/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptosignature.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Crypto-Manager-Request model
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Model representing a Crypto-Manager-Service Request")
public class SignatureRequestDto {
	/**
	 * Application id of decrypting module
	 */
	@ApiModelProperty(notes = "Application id of decrypting module", example = "REGISTRATION", required = true)
	//@NotBlank(message = KeymanagerConstant.SIGNATURE_ALGORITHM)
	private String applicationId;
	/**
	 * Refrence Id
	 */
	@ApiModelProperty(notes = "Refrence Id", example = "REF01")
	private String referenceId;
	/**
	 * Timestamp
	 */
	@ApiModelProperty(notes = "Timestamp as metadata", example = "2018-12-10T06:12:52.994Z", required = true)
	@NotNull
	private String timeStamp;
	/**
	 * Data in BASE64 encoding to encrypt/decrypt
	 */
	@ApiModelProperty(notes = "Data to sign", required = true)
	//@NotBlank(message = CryptomanagerConstant.INVALID_REQUEST)
	private String data;

}
