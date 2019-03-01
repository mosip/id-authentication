/**
 * 
 */
package io.mosip.kernel.auth.exception;

/**
 * @author M1049825
 *
 */
public class AuthManagerException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4060346018688709387L;
	
	private String errCode;

    public AuthManagerException(String errCode, String message) {
        super(message);
        this.errCode = errCode;
    }

}
