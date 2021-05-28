package io.mosip.authentication.core.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class IdAuthFraudAnalysisEventDTO {

	private String individualIdHash;
	private String transactionId;
	private String partnerId;
	private String authType;
	private LocalDateTime requestTime;
	private String authStatus;
	private String comment;
}
