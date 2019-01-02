package io.mosip.demo.authentication.service.dto;

import java.util.Map;

import lombok.Data;

@Data
public class CryptomanagerRequestDto {
	String applicationId;
	String data;
	String referenceId;
	String timeStamp;
}
