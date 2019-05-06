package io.mosip.authentication.partnerdemo.service.dto;

import lombok.Data;

@Data
public class CryptomanagerRequestDto {
	String applicationId;
	String data;
	String referenceId;
	String salt;
	String timeStamp;
}
