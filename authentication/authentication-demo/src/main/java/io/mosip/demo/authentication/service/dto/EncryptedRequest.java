package io.mosip.demo.authentication.service.dto;

import lombok.Data;

@Data
public class EncryptedRequest {
	String key;
	String data;

}
