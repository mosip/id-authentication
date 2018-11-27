package io.mosip.kernel.packetuploader.http.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error item bean class having error code and error message
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Error {

	/**
	 * The error code field
	 */

	private String code;

	/**
	 * The error message field
	 */
	private String message;

}