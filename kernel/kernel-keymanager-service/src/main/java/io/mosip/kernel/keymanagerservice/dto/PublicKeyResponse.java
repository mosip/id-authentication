package io.mosip.kernel.keymanagerservice.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response class for Public Key
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Class representing a Public Key Response")
public class PublicKeyResponse<T> {

	/**
	 * The string alias
	 */
	@JsonIgnore
	private String alias;

	/**
	 * Field for public key
	 */
	@ApiModelProperty(notes = "Public key in BASE64 encoding format", required = true)
	private T publicKey;

	/**
	 * Key creation time
	 */
	@ApiModelProperty(notes = "Timestamp of issuance of public key", required = true)
	private LocalDateTime issuedAt;

	/**
	 * Key expiry time
	 */
	@ApiModelProperty(notes = "Timestamp of expiry of public key", required = true)
	private LocalDateTime expiryAt;

}
