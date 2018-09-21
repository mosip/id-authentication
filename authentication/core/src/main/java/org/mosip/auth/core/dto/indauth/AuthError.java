package org.mosip.auth.core.dto.indauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * {@code AuthError} is the sub-class of {@link RuntimeException}. Purpose of
 * this runtime-exception is throw exception with {@link AuthError#errorCode}
 * and {@link AuthError#errorMessage}.
 * 
 * @author Rakesh Roshan
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -411323366730664228L;

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
