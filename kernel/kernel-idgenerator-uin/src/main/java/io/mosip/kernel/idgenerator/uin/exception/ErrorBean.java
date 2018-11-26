package io.mosip.kernel.idgenerator.uin.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error item bean class having error code and error message
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorBean {

	/**
	 * The error code field
	 */
	private String errorCode;

	/**
	 * The error message field
	 */
	private String errorMessage;

}