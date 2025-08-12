package io.mosip.authentication.core.indauth.dto;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import io.mosip.authentication.core.dto.ObjectWithMetadata;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The class KycExchangeRequestDTOV2 to holds the request parameters 
 * for Kyc Exchange Request Object for OIDC4IDA.
 * 
 * @author Mahammed Taheer
 *
 */

@Data
@EqualsAndHashCode(callSuper=true)
public class KycExchangeRequestDTOV2 extends BaseRequestDTO implements ObjectWithMetadata {

	/** The Variable to hold value of kyc Token */
	@NotNull
	private String kycToken;

	/** The Variable to hold value of list of user selected locales */
	private List<String> locales;

	private Map<String, Object> metadata;

	/*
	 * Support for OIDC4IDA claims. 
	 * one "verification", and "claims".
	 * Eg: 
	 * 
	    "verified_claims":{
			"verification":{
				"trust_framework":{
					"value":"abd" or
					"values": ["tf1", "tf2"]
				},
				"time":{
					"max_age":"1933"
				}
			},
			"claims":{ // Verified Claims
				"given_name":null,
				"family_name":null,
				"birthdate":null
			}
		}
	 * Support for multiple objects contains array of "verification" and "claims".
	 * 
	 * Eg: 
	 * 
	    "verified_claims":[
			{
				"verification":{
					"trust_framework":{
						"value":"abd" or
						"values": ["tf1", "tf2"]
					},
					"time":{
						"max_age":"1933"
					}
				},
				"claims":{ // Verified Claims
					"given_name":null,
					"family_name":null
				}
			},
			{
				"verification":{
					"trust_framework":{
						"value":"xyz" or
						"values": ["tf3", "tf4"]
					},
					"time":{
						"max_age":"1933"
					}
				},
				"claims":{ // Verified Claims
					"birthdate":null
				}
			}
		]
	 * 
	 */
	List<Map<String, Object>> verifiedConsentedClaims;

	/*
	 * User consented unverified claims list. 
	 */
	Map<String, Object> unVerifiedConsentedClaims;

	/** The Variable to hold value of response type */
	private String respType;
}
