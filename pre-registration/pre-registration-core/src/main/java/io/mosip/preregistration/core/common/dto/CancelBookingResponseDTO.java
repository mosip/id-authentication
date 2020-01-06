package io.mosip.preregistration.core.common.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CancelBookingResponseDTO {
	private String transactionId;
	private String message;
}
