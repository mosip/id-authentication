package io.mosip.authentication.core.dto.spinstore;

import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 */
@Data
public class StaticPinIdentityDTO {
	
	/** The value UIN */
	private String uin;
	
	/** The value VID */
	private String vid;
}
