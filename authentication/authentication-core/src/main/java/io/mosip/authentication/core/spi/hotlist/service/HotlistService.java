package io.mosip.authentication.core.spi.hotlist.service;

import java.time.LocalDateTime;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.hotlist.dto.HotlistDTO;
import io.mosip.kernel.core.websub.model.EventModel;
/**
 * 
 * @author Mamta A
 *
 */
public interface HotlistService {

	void updateHotlist(String id, String idType, String status, LocalDateTime expiryTimestamp) throws IdAuthenticationBusinessException;
	
	void unblock(String id, String idType) throws IdAuthenticationBusinessException;

	/**
	 * Method used to retrieve the Hotlist Status information for given id, idType
	 * @param id 			the id_hash
	 * @param idType 		the id_type
	 * @return HotlistDTO 	consist of hotlisting information
	 * @throws IdAuthenticationBusinessException id auth business exception
	 */
	HotlistDTO getHotlistStatus(String id, String idType);

	void handlingHotlistingEvent(EventModel eventModel) throws IdAuthenticationBusinessException;
}
