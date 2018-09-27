package org.mosip.auth.core.spi.idauth.service;

import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.exception.IdValidationFailedException;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Arun Bose
 *  The code {@IdAuthService} validates UIN
 * 
 */
@Service
public interface IdAuthService {

	/**
	 * validates the UIN
	 * @param UIN
	 * @return
	 * @throws IdValidationFailedException
	 */
	 String validateUIN(String UIN)  throws IdAuthenticationBusinessException;
	 
	/**
	 * validates the VID
	 * @param VID
	 * @return
	 * @throws IdValidationFailedException
	 */
	String validateVID(String VID)  throws IdAuthenticationBusinessException;
}
