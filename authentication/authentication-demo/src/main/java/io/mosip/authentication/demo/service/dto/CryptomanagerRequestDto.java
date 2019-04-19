package io.mosip.authentication.demo.service.dto;

import lombok.Data;

@Data
public class CryptomanagerRequestDto {
	String applicationId;
	String data;
	String referenceId;
	String timeStamp;
}
