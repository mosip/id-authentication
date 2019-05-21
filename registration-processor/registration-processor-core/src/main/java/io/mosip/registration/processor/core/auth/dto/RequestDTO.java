package io.mosip.registration.processor.core.auth.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
@Data
public class RequestDTO {
/*
	*//** variable to hold identity value *//*
	private IdentityDTO identity;*/
	
	private List<DataInfoDTO> biometrics;
	
	private String otp;
	
	private LocalDateTime timestamp;

}
