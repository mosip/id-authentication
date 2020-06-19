package io.mosip.authentication.core.spi.idevent.service;

import java.util.List;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.dto.EventDTO;

/**
 * ID Change Event handler service interface.
 *
 * @author Loganathan Sekar
 */
public interface IdChangeEventHandlerService {
	
	/**
	 * Handle id event.
	 *
	 * @param events the events
	 * @return true, if successful
	 */
	void handleIdEvent(List<EventDTO> events) throws IdAuthenticationBusinessException;
	
}
