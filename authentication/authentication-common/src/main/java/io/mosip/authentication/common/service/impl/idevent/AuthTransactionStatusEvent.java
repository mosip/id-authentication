package io.mosip.authentication.common.service.impl.idevent;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AuthTransactionStatusEvent {
	
	private String id;
	
	private String transactionId;
	
	private LocalDateTime timestamp;

}
