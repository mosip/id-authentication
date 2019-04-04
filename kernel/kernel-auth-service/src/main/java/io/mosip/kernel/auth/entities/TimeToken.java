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
public class TimeToken {

	private String token;
	private long expTime;

}
