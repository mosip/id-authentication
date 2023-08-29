package io.mosip.authentication.esignet.integration.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class VciCredentialsDefinitionRequestDTO {
	
	/**  */
	private Map<String, Object> credentialRequest;

	/**  */
	private String credentialType;

	/** */
	private List<String> credentialContexts;

}
