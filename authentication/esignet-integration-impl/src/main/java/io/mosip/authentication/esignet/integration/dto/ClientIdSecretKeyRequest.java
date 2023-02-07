package io.mosip.authentication.esignet.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientIdSecretKeyRequest {
	
	private String clientId;
	private String secretKey;
	private String appId;

}
