/**
 * 
 */
package io.mosip.kernel.auth.dto;

import lombok.Data;

/**
 * @author Ramadurai Pandian
 *
 */
@Data
public class TimeToken {

	private String token;
	private long expTime;

}
