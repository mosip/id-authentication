/**
 * 
 */
package io.mosip.preregistration.auth.dto;

import lombok.Data;

/**
 * @author Ramadurai Pandian
 *
 */
@Data
public class BaseRequestResponseDTO {
	
	/** The id. */
	private String id;
	
	/** The ver. */
	private String version;
	
	/** The timestamp. */
	private String timestamp;
}
