package io.mosip.authentication.core.indauth.dto;

import lombok.Data;

/**
 * The Class For holding base attributes
 * 
 * @author Loganathan Sekar
 *
 *
 */
@Data
public class BaseRequestDTO {
	
	/** The value for Id*/
	private String id;
	
	/** The value for version*/
	private String version;
	
	/** The value for individualId*/
	private String individualId;

	/** The value for individualIdType*/
	private String individualIdType;
	
	/** The value for transactionID*/
	private String transactionID;

	/** The value for requestTime*/
	private String requestTime;
	
	
}
