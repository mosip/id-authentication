package io.mosip.authentication.core.dto.indauth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ActionableAuthError extends AuthError {

	private String actnCode;

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
