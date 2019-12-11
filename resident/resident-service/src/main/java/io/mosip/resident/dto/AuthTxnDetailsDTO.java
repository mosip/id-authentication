package io.mosip.resident.dto;

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
	private String date;
	private String time;
	private String partnerName;
	private String partnerTransactionId;
	private String authResponse;
	private String responseCode;
}

