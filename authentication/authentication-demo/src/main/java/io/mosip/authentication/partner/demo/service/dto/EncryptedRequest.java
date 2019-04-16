package io.mosip.authentication.partner.demo.service.dto;

import lombok.Data;

@Data
public class EncryptedRequest {
	String key;
	String data;

}
