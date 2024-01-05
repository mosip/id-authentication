package io.mosip.authentication.core.spi.indauth.match;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * To match Password against Stored Hashed Password and Salt

 */
public interface ComparePasswordFunction {

	/**
	 * To Match Password.
	 *
	 * @param passwordValue the password value
	 * @param passwordHashValue the stored password hash value 
	 * @param salt the stored salt value 
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public boolean matchPasswordFunction(String passwordValue, String passwordHashValue, String salt) throws IdAuthenticationBusinessException;

}
