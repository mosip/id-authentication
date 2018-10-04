package io.mosip.core.keysupplier.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonInclude;

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
public class ErrorItem {

	/**
	 * The error code field
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String code;

	/**
	 * The error message field
	 */
	private String message;

}