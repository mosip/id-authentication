package io.mosip.authentication.core.spi.indauth.service;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;

/**
 * 
 * This interface is used to authenticate Individual based on Biometric
 * attributes.
 * 
 * @author Dinesh Karuppiah.T
 * @author Manoj SP
 */

public interface BioAuthService extends AuthService {

	/**
	 * Authenticate.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param uin the uin
	 * @param idInfo the id info
	 * @param partnerId the partner id
	 * @param isAuth the is auth
	 * @return the auth status info
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public AuthStatusInfo authenticate(AuthRequestDTO authRequestDTO, String token,
			Map<String, List<IdentityInfoDTO>> idInfo, String partnerId, boolean isAuth)
			throws IdAuthenticationBusinessException;

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.service.AuthService#authenticate(io.mosip.authentication.core.indauth.dto.AuthRequestDTO, java.lang.String, java.util.Map, java.lang.String)
	 */
	@Override
	public default AuthStatusInfo authenticate(AuthRequestDTO authRequestDTO, String token,
			Map<String, List<IdentityInfoDTO>> idInfo, String partnerId) throws IdAuthenticationBusinessException {
		return this.authenticate(authRequestDTO, token, idInfo, partnerId, true);
	}
}