package io.mosip.authentication.core.spi.indauth.service;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * This interface is extended by all the all the Auth Type  for authentication.
 *
 * @author Arun Bose S
 */
public interface AuthService {
	
	/**
	 *  This method is used to authenticate the individual based on the auth type.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param uin the uin
	 * @param idInfo the id info
	 * @return the auth status info
	 */
	public AuthStatusInfo authenticate(AuthRequestDTO authRequestDTO,String uin,Map<String,List<IdentityInfoDTO>> idInfo,String partnerId)throws IdAuthenticationBusinessException;
}
