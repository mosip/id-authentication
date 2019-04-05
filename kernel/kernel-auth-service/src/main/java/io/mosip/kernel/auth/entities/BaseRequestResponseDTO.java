/**
 * 
 */
package io.mosip.kernel.auth.entities;

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
