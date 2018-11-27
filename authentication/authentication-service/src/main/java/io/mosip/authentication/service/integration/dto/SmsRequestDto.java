package io.mosip.authentication.service.integration.dto;

import lombok.Data;
/**
 * 
 * @author Dinesh Karuppiah.T
 */
@Data
public class SmsRequestDto {

	String message;
	String number;
}
