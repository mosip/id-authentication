package io.mosip.authentication.partnerdemo.service.dto;

import lombok.Data;

@Data
public class EncryptedRequest {
	String key;
	String data;

}
