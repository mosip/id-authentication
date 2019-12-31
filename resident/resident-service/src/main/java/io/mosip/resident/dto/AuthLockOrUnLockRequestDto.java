/**
 * 
 */
package io.mosip.resident.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.mosip.resident.constant.IdType;
import lombok.Data;

/**
 * @author M1022006
 *
 */
@Data
@JsonPropertyOrder({ "transactionID", "individualId", "individualIdType", "otp", "authType" })
public class AuthLockOrUnLockRequestDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@NotBlank(message = "transactionID should not be empty")
	@NotNull(message = "transactionID should not be null")
	private String transactionID;
	@NotBlank(message = "individualId should not be empty")
	@NotNull(message = "individualId should not be null")
	private String individualId;

	@NotNull(message = "individualIdType should not be null")
	private IdType individualIdType;
	@NotBlank(message = "otp should not be empty")
	@NotNull(message = "otp should not be null")
	private String otp;
	private List<String> authType;

}
