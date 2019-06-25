package io.mosip.authentication.core.spi.indauth.match;

import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;

/**
 * 
 * Fetch Entity values based on request
 *
 * @author Dinesh Karuppiah.T
 */
public interface EntityValueFetcher {

	/**
	 * Fetch Entity Value.
	 *
	 * @param uin the uin
	 * @param authReq the auth req
	 * @param partnerID the partner ID
	 * @return the map
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public Map<String, String> fetch(String uin, AuthRequestDTO authReq, String partnerID)
			throws IdAuthenticationBusinessException;

}
