package io.mosip.authentication.core.spi.idevent.service;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.dto.EventModel;

/**
 * ID Change Event handler service interface.
 *
 * @author Loganathan Sekar
 */
public interface CredentialStoreService {
	
	/**
	 * Handle id event.
	 *
	 * @param events the events
	 * @return true, if successful
	 */
	void handleIdEvent(EventModel events) throws IdAuthenticationBusinessException;
	
}
