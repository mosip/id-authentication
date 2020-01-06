package io.mosip.authentication.common.service.integration.dto;

import lombok.Data;

/**
 * General-purpose of {@code OtpGeneratorResponseDto} class used to store Otp
 * Generation Info's
 * 
 * @author Dinesh Karuppiah.T
 */
@Data
public class OtpGeneratorResponseDto {

	String status;
	String message;

}
