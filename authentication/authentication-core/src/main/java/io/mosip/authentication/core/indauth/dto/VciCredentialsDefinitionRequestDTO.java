package io.mosip.authentication.core.indauth.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * The Class VciCredentialsDefinitionRequestDTO for credential definition input.
 * 
 * @author Mahammed Taheer
 * 
 */
@Data
public class VciCredentialsDefinitionRequestDTO {
	
	/**  */
	private Map<String, Object> credentialSubject;

	/**  */
	private List<String> type;

	/** */
	private List<String> context;

}
