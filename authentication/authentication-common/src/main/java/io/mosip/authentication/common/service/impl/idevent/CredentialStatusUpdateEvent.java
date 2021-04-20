package io.mosip.authentication.common.service.impl.idevent;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CredentialStatusUpdateEvent {
	
	private String id;
	
	private String requestId;
	
	private String status;
	
	private LocalDateTime timestamp;

}
