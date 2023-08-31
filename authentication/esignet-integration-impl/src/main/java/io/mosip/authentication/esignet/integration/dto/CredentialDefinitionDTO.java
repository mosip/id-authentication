package io.mosip.authentication.esignet.integration.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class CredentialDefinitionDTO {
	
	/**  */
	private Map<String, Object> credentialSubject;

	/**  */
	private List<String> type;

	/** */
	private List<String> context;

}
