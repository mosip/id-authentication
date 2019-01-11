package io.mosip.registration.processor.core.auth.dto;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 */

@Data
public class BaseAuthResponseDTO {
	private String status;
	private List<AuthError> err;
	private String txnID;
	private String resTime;
}
