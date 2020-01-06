package io.mosip.kernel.keymanagerservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncryptDataRequestDto {

	/**
	 * The string applicationID
	 */
	@ApiModelProperty(notes = "Application id of decrypting module", example = "REGISTRATION", required = true)
	private String applicationId;

	/**
	 * The field for timestamp
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	@ApiModelProperty(notes = "Timestamp", example = "2018-12-10T06:12:52.994Z", required = true)
	private String timeStamp;

	/**
	 * The string reference id
	 */

	@ApiModelProperty(notes = "Reference Id", example = "REF01")
	private String referenceId;

	/**
	 * The string encryptedSymmetricKey
	 */
	@ApiModelProperty(notes = "Hashed data in BASE64 encoding to encrypt", required = true)
	private String hashedData;
}