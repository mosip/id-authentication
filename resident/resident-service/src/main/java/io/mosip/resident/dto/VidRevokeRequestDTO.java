package io.mosip.resident.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class VidRevokeRequestDTO implements Serializable {

	private String transactionID;
	private String individualId;
	private String individualIdType;
	private String otp;
	private String vidStatus;
}
