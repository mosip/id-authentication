package io.mosip.authentication.core.autntxn.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AutnTxnDto {

	String transactionID;
	LocalDateTime requestdatetime;
	String authtypeCode;
	String statusCode;
	String statusComment;
	String referenceIdType;
	String entityName;
	String requestSignature;
	String responseSignature;
	String tokenId;
	String entityId;
	String individualId;
}
