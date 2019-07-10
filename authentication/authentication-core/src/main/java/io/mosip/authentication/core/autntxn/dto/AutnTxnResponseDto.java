package io.mosip.authentication.core.autntxn.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 
 * @author Dinesh Karuppiah.T
 *
 */
@Data
public class AutnTxnResponseDto {

	String transactionID;
	LocalDateTime requestdatetime;
	String authtypeCode;
	String statusCode;
	String statusComment;
	String referenceIdType;
	String partnerName;

}
