package io.mosip.authentication.core.spi.indauth.match;

import java.util.Map;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
public interface EntityValueFetcher {

	/**
	 * Fetch Entity Value
	 * 
	 * @param uin
	 * @param authReq
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	public Map<String, String> fetch(String uin, AuthRequestDTO authReq) throws IdAuthenticationBusinessException;

}
