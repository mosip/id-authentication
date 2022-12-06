package io.mosip.authentication.core.indauth.dto;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import io.mosip.authentication.core.dto.ObjectWithMetadata;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The class KycExchangeRequestDTO to holds the request parameters 
 * for Kyc Exchange.
 * 
 * @author Mahammed Taheer
 *
 */

@Data
@EqualsAndHashCode(callSuper=true)
public class KycExchangeRequestDTO extends BaseRequestDTO implements ObjectWithMetadata {

	/** The Variable to hold value of kyc Token */
	@NotNull
	private String kycToken;

	/** The Variable to hold value of list of consents (UserClaims) */
	private List<String> consentObtained;

	/** The Variable to hold value of list of user selected locales */
	private List<String> locales;

	/** The Variable to hold value of response type */
	private String respType;

	private Map<String, Object> metadata;
	
}
