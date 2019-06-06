package io.mosip.authentication.core.indauth.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * Actionable error class
 * 
 * @author Dinesh Karuppiah.T
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ActionableAuthError extends AuthError {

	/** variable holds Action message */
	private String actionMessage;

	public ActionableAuthError() {
		super();
	}

	/**
	 * Actionable Auth Error
	 * 
	 * @param errorCode    - error code
	 * @param errorMessage - error message
	 * @param actnCode     - action code
	 */
	public ActionableAuthError(String errorCode, String errorMessage, String actionMessage) {
		super(errorCode, errorMessage);
		this.actionMessage = actionMessage;
	}

}
