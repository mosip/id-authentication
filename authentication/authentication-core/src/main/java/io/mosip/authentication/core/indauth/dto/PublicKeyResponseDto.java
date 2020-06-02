package io.mosip.authentication.core.indauth.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response class for Public Key
 * 
 * @author Nagarjuna
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Class representing a Public Key Response")
public class PublicKeyResponseDto<T> {

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
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	@ApiModelProperty(notes = "Timestamp of issuance of public key", required = true)
	private LocalDateTime issuedAt;

	/**
	 * Key expiry time
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	@ApiModelProperty(notes = "Timestamp of expiry of public key", required = true)
	private LocalDateTime expiryAt;

}
