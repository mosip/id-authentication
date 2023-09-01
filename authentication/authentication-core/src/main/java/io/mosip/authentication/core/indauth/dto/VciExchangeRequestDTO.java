package io.mosip.authentication.core.indauth.dto;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import io.mosip.authentication.core.dto.ObjectWithMetadata;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The class VciExchangeRequestDTO to holds the request parameters 
 * for VCI Exchange.
 * 
 * @author Mahammed Taheer
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class VciExchangeRequestDTO extends BaseRequestDTO implements ObjectWithMetadata {

	/** The Variable to hold value of kyc Token */
	@NotNull
	private String vcAuthToken;

	/** The Variable to hold value of Credential Subject Id */
	@NotNull
	private String credSubjectId;

	/** The Variable to hold value of VC Format type */
	@NotNull
	private String vcFormat;
	
	/** The Variable to hold value of credential definition */
	private VciCredentialsDefinitionRequestDTO credentialsDefinition;

	/** The Variable to hold value of list of user selected locales */
	private List<String> locales;

	private Map<String, Object> metadata;
}
