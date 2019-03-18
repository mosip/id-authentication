/**
 * 
 */
package io.mosip.kernel.auth.adapter;

import org.springframework.security.core.AuthenticationException;

/**
 * @author M1049825
 *
 */
public class AuthManagerException extends AuthenticationException{

	/**
	 * 
	 */
	
	private String errorCode;
	private static final long serialVersionUID = 4060346018688709387L;
	
	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public AuthManagerException(String errorCode, String errorMessage) {
		super(errorMessage);
		this.errorCode = errorCode;
	}
}
