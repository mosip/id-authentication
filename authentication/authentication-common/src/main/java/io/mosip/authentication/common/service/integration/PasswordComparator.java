 package io.mosip.authentication.common.service.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;


/**
 * This Class will compare the input password value with the stored Hash value & salt
 *
 */
@Component
public class PasswordComparator {

	@Autowired(required = false)
	private IdAuthSecurityManager securityManager;

	/**
	 * Logger
	 */
	private static Logger logger = IdaLogger.getLogger(PasswordComparator.class);

	public boolean matchPasswordFunction(String passwordValue, String passwordHashValue, String salt) throws IdAuthenticationBusinessException {
			
		try {
			String inputPasswordHash = securityManager.generateArgon2Hash(passwordValue, salt);
			return inputPasswordHash.equals(passwordHashValue);
		} catch (Exception e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), e.getLocalizedMessage(),e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}
	}
}
