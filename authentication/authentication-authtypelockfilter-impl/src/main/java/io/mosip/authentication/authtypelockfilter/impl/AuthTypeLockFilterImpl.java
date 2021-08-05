package io.mosip.authentication.authtypelockfilter.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.authfilter.spi.IMosipAuthFilter;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.authtype.status.service.AuthtypeStatusService;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.idrepository.core.dto.AuthtypeStatus;

/**
 * The Class AuthTypeLockFilterImpl - implementation of auth filter for
 * validating AuthType locked/unlocked status for an individual in the
 * authentication request.
 * 
 * @author Loganathan Sekar
 */
public class AuthTypeLockFilterImpl implements IMosipAuthFilter {
	
	@Autowired
	private AuthtypeStatusService authTypeStatusService;
	
	@Autowired
	private IdInfoFetcher idInfoFetcher;

	/**
	 * Inits the.
	 */
	public void init() throws IdAuthenticationFilterException {
	}

	/**
	 * Test method that executes predicate test condition on the given arguments
	 *
	 * @param authRequest  the auth request
	 * @param identityData the identity data
	 * @param properties   the properties
	 * @throws IdAuthenticationBusinessException 
	 */
	public void validate(AuthRequestDTO authRequest, Map<String, List<IdentityInfoDTO>> identityData,
			Map<String, Object> properties) throws IdAuthenticationFilterException {
		validateAuthTypeStatus(authRequest, (String)properties.get(IdAuthCommonConstants.TOKEN));
	}
	
	private void validateAuthTypeStatus(AuthRequestDTO authRequestDTO, String token) throws IdAuthenticationFilterException {
		try {
			List<AuthtypeStatus> authtypeStatusList = authTypeStatusService
					.fetchAuthtypeStatus(token);
			if (Objects.nonNull(authtypeStatusList) && !authtypeStatusList.isEmpty()) {
				for (AuthtypeStatus authTypeStatus : authtypeStatusList) {
					validateAuthTypeStatus(authRequestDTO, authTypeStatus);
				}
			}
		} catch (IdAuthenticationFilterException e) {
			throw e;
		} catch (IdAuthenticationBusinessException e) {
			throw new IdAuthenticationFilterException(e.getErrorCode(), e.getErrorText(), e.getCause());
		}
	}

	private void validateAuthTypeStatus(AuthRequestDTO authRequestDTO, AuthtypeStatus authTypeStatus)
			throws IdAuthenticationFilterException {
		if (authTypeStatus.getLocked()) {
			if (authRequestDTO.getRequestedAuth().isDemo()
					&& authTypeStatus.getAuthType().equalsIgnoreCase(MatchType.Category.DEMO.getType())) {
				throw new IdAuthenticationFilterException(
						IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
								MatchType.Category.DEMO.getType()));
			}

			else if (authRequestDTO.getRequestedAuth().isBio()
					&& authTypeStatus.getAuthType().equalsIgnoreCase(MatchType.Category.BIO.getType())) {
				for (AuthType authType : BioAuthType.getSingleBioAuthTypes().toArray(s -> new AuthType[s])) {
					if (authType.getType().equalsIgnoreCase(authTypeStatus.getAuthSubType())) {
						if (authType.isAuthTypeEnabled(authRequestDTO, idInfoFetcher)) {
							throw new IdAuthenticationFilterException(
									IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
									String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
											MatchType.Category.BIO.getType() + "-" + authType.getType()));
						} else {
							break;
						}
					}
				}
			}

			else if (authRequestDTO.getRequestedAuth().isOtp()
					&& authTypeStatus.getAuthType().equalsIgnoreCase(MatchType.Category.OTP.getType())) {
				throw new IdAuthenticationFilterException(
						IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
								MatchType.Category.OTP.getType()));
			}

			else if (authRequestDTO.getRequestedAuth().isPin()
					&& authTypeStatus.getAuthType().equalsIgnoreCase(MatchType.Category.SPIN.getType())) {
				throw new IdAuthenticationFilterException(
						IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
								MatchType.Category.SPIN.getType()));
			}
		}
	}
}
