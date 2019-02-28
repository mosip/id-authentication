package io.mosip.authentication.core.dto.indauth;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * General-purpose of {@code AuthRequestDTO} class used to communicate with
 * core-kernel API. This class is picked request and send to core-kernel. Where
 * core-kernal API processed operation with {@code AuthRequestDTO} attributes.
 * 
 * @author Rakesh Roshan
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRequestDTO extends BaseAuthRequestDTO {


	private AuthTypeDTO requestedAuth;

	private String transactionID;

	private String requestTime;
	
	private String sessionKey;

	private List<BioInfo> bioMetadata;

	private RequestDTO request;
	
	private String partnerID;
	
	private String policyID;

}
