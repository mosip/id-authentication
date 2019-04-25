package io.mosip.registration.processor.rest.client.audit.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class SecretKeyRequest {
	public String clientId;
	public String secretKey;
	public String appId;
}
