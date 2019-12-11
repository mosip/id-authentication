package io.mosip.resident.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Getter
@Setter
public class AuthTxnDetailsDTO {
	private int serialNumber;
	private String idUsed;
	private String authModality;
	private LocalDate date;
	private LocalTime time;
	private String partnerName;
	private String partnerTransactionId;
	private String authResponse;
	private String responseCode;
}

