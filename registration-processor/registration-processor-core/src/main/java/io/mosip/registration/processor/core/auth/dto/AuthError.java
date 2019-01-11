package io.mosip.registration.processor.core.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@code AuthError} is the sub-class of {@link RuntimeException}. Purpose of
 * this runtime-exception is throw exception with {@link AuthError#errorCode}
 * and {@link AuthError#errorMessage}.
 * 
 * @author Rakesh Roshan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthError {

	/**
	 * contain the errorcode i.e numeric like 100,101 etc.
	 */
	private String errorCode;

	/**
	 * {@link errorMessage} is text message which gives information of error or
	 * status which happened during code process.
	 */
	private String errorMessage;

}
