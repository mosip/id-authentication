package io.mosip.authentication.demo.service.dto;

import lombok.Data;

@Data
public class EncryptedRequest {
	String key;
	String data;

}
