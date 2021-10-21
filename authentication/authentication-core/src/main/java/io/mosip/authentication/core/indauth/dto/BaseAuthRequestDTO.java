package io.mosip.authentication.core.indauth.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class For holding id and version
 * 
 * @author Prem Kumar
 *
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class BaseAuthRequestDTO extends BaseRequestDTO {
	
	private String specVersion;
	
	private String thumbprint;
	
	private String domainUri;
	
	private String env;
	
}
