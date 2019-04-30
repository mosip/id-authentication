/**
 * 
 */
package io.mosip.kernel.auth.adapter.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author M1049825
 *
 */
public class AuthException extends AuthenticationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3759673611080040633L;

	public AuthException(String msg) {
		super(msg);
	}

}
