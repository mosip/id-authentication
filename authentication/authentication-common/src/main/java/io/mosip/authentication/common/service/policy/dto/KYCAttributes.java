package io.mosip.authentication.common.service.policy.dto;

import lombok.Data;

/**
 *The Class KYCAttributes which has attributes for KYC type.
 *@author Arun Bose S
 */
@Data
public class KYCAttributes {
	
	/** The attribute name. */
	private String attributeName;
	
	/** required is for the attribute name */
	private boolean required;
	
	/**  masked is for the attribute  name*/
	private boolean masked;

}
