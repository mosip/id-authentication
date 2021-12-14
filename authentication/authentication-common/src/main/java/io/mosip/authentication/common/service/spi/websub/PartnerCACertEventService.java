package io.mosip.authentication.common.service.spi.websub;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.kernel.core.websub.model.EventModel;

/**
 * The Interface PartnerCACertEventService.
 * 
 * @author Loganathan Sekar
 */
public interface PartnerCACertEventService {

	/**
	 * Handle CA cert event.
	 *
	 * @param eventModel the event model
	 * @throws RestServiceException the rest service exception
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	void handleCACertEvent(EventModel eventModel) throws RestServiceException, IdAuthenticationBusinessException;
	
	/**
	 * Evict CA cert cache.
	 *
	 * @param eventModel the event model
	 * @throws RestServiceException the rest service exception
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	void evictCACertCache(EventModel eventModel) throws RestServiceException, IdAuthenticationBusinessException;

}