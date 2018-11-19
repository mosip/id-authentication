package io.mosip.authentication.core.dto.indauth;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ActionableAuthError extends AuthError {

	private String actnCode;
	
	public ActionableAuthError() {
		super();
	}

	/**
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param actnCode
	 */
	public ActionableAuthError(String errorCode, String errorMessage, String actnCode) {
		super(errorCode, errorMessage);
		this.actnCode = actnCode;
	}

}
