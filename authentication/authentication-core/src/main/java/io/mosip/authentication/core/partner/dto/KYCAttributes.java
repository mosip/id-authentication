package io.mosip.authentication.core.partner.dto;

import lombok.Data;

/**
 *The Class KYCAttributes has attributes for KYC type which is mapped in auth policy json.
 *@author Arun Bose S
 */
@Data
public class KYCAttributes {
	
	/** The attribute name. */
	private String attributeName;
	
	/**  masked is for the attribute  name*/
	private boolean masked;

}
