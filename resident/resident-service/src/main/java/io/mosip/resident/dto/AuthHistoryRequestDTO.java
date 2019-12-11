package io.mosip.resident.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthHistoryRequestDTO {
	@NotBlank(message = "transactionID should not be empty")
	@NotNull(message = "transactionID should not be null")
	private String transactionID;
	@NotBlank(message = "individualId should not be empty")
	@NotNull(message = "individualId should not be null")
	private String individualId;
	@NotBlank(message = "individualIdType should not be empty")
	@NotNull(message = "individualIdType should not be null")
	private String individualIdType;
	@NotBlank(message = "otp should not be empty")
	@NotNull(message = "otp should not be null")
	private String otp;
	@Positive
	private Integer pageStart;
	@Positive
	private Integer pageFetch;
	
}
