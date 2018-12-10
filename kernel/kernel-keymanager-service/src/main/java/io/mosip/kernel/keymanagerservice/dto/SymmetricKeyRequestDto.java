package io.mosip.kernel.keymanagerservice.dto;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for Symmetric Key
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Class representing a Decrypt Symmetric Key Request")
public class SymmetricKeyRequestDto {

	/**
	 * The string applicationID
	 */
	@ApiModelProperty(notes = "Application id of decrypting module", example = "REGISTRATION", required = true)
	private String applicationId;

	/**
	 * The field for timestamp
	 */
	@ApiModelProperty(notes = "Timestamp", example = "2018-12-10T06:12:52.994Z", required = true)
	private LocalDateTime timeStamp;

	/**
	 * The string reference id
	 */
	
	@ApiModelProperty(notes = "Refrence Id", example = "REF01")
	private String referenceId;

	/**
	 * The string encryptedSymmetricKey
	 */
	@ApiModelProperty(notes = "Encrypted Symmetric key in BASE64 encoding to decrypt", required = true)
	private String encryptedSymmetricKey;

}
