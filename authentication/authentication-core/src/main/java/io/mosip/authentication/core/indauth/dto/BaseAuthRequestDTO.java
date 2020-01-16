package io.mosip.authentication.core.indauth.dto;

import lombok.Data;

/**
 * The Class For holding id and version
 * 
 * @author Prem Kumar
 *
 *
 */
@Data
public class BaseAuthRequestDTO {
	
	/** The value for Id*/
	private String id;
	
	/** The value for version*/
	private String version;
	
}
