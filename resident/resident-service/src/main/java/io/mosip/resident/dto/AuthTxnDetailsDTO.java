package io.mosip.resident.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;
@Data
public class AuthTxnDetailsDTO {
	String serialNumber;
	String idUsed;
	String authModality;
	LocalDate date;
	LocalTime time;
	String partnerName;
	String partnerTransactionId;
	String authResponse;
	String responseCode;
}
