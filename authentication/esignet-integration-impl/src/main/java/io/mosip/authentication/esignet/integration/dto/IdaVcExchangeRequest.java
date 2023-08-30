package io.mosip.authentication.esignet.integration.dto;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IdaVcExchangeRequest {

	@NotNull
	private String vcAuthToken;

	/** The Variable to hold value of Credential Subject Id */
	@NotNull
	private String credSubjectId;

	/** The Variable to hold value of VC Format type */
	@NotNull
	private String vcFormat;

	/** The Variable to hold value of list of user selected locales */
	private List<String> locales;

	private Map<String, Object> metadata;

	private String id;
	
	private String version;
	
	private String individualId;
	
	private String transactionID;
	
	private String requestTime;
	
	private VciCredentialsDefinitionRequestDTO credentialsDefinition;

}
