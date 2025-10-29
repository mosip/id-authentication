package io.mosip.authentication.authtypelockfilter.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.authfilter.spi.IMosipAuthFilter;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.util.AuthTypeUtil;
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
					validateAuthTypeStatus(authRequestDTO, authTypeStatus,authtypeStatusList);
				}
			}
		} catch (IdAuthenticationFilterException e) {
			throw e;
		} catch (IdAuthenticationBusinessException e) {
			throw new IdAuthenticationFilterException(e.getErrorCode(), e.getErrorText(), e.getCause());
		}
	}

    private void validateAuthTypeStatus(AuthRequestDTO authRequestDTO, AuthtypeStatus authTypeStatus,
                                        List<AuthtypeStatus> authtypeStatusList)
            throws IdAuthenticationFilterException {

        System.out.println("---- Entered validateAuthTypeStatus ----");
        System.out.println("authTypeStatus = " + authTypeStatus);
        System.out.println("authTypeStatus.getAuthType() = " + authTypeStatus.getAuthType());
        System.out.println("authTypeStatus.getAuthSubType() = " + authTypeStatus.getAuthSubType());
        System.out.println("authTypeStatus.getLocked() = " + authTypeStatus.getLocked());
        System.out.println("AuthRequestDTO = " + authRequestDTO);

        if (authTypeStatus.getLocked()) {
            System.out.println("Auth type is locked, proceeding to validation...");

            // DEMO
            if (AuthTypeUtil.isDemo(authRequestDTO)
                    && authTypeStatus.getAuthType().equalsIgnoreCase(MatchType.Category.DEMO.getType())) {
                System.out.println("DEMO auth type locked condition matched.");
                throw new IdAuthenticationFilterException(
                        IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
                        String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
                                MatchType.Category.DEMO.getType()));
            }

            // BIO
            else if (AuthTypeUtil.isBio(authRequestDTO)
                    && authTypeStatus.getAuthType().equalsIgnoreCase(MatchType.Category.BIO.getType())) {
                System.out.println("BIO auth type locked condition matched.");
                for (AuthType authType : BioAuthType.getSingleBioAuthTypes().toArray(s -> new AuthType[s])) {
                    System.out.println("Checking BIO sub-type: " + authType.getType());
                    if (authType.getType().equalsIgnoreCase(authTypeStatus.getAuthSubType())) {
                        System.out.println("Matched BIO subtype: " + authTypeStatus.getAuthSubType());
                        if (authType.isAuthTypeEnabled(authRequestDTO, idInfoFetcher)) {
                            System.out.println("BIO auth type enabled and locked. Throwing exception.");
                            throw new IdAuthenticationFilterException(
                                    IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
                                    String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
                                            MatchType.Category.BIO.getType() + "-" + authType.getType()));
                        } else {
                            System.out.println("BIO subtype found but not enabled. Breaking loop.");
                            break;
                        }
                    }
                }
            }

            // OTP
            else if (AuthTypeUtil.isOtp(authRequestDTO)) {
                System.out.println("OTP authentication request detected.");
                if (authTypeStatus.getAuthType().equalsIgnoreCase(MatchType.Category.OTP.getType())
                        && (authTypeStatus.getAuthSubType() == null || authTypeStatus.getAuthSubType().isEmpty())) {
                    System.out.println("OTP locked without subtype. Throwing exception.");
                    throw new IdAuthenticationFilterException(
                            IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
                            String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
                                    MatchType.Category.OTP.getType()));
                } else {
                    System.out.println("OTP subtype = " + authTypeStatus.getAuthSubType());
                    if (authTypeStatus.getAuthSubType() != null && !authTypeStatus.getAuthSubType().isEmpty()
                            && (authTypeStatus.getAuthSubType().equalsIgnoreCase(IdAuthCommonConstants.PHONE_NUMBER)
                            || authTypeStatus.getAuthSubType().equalsIgnoreCase(IdAuthCommonConstants.EMAIL))
                            && authTypeStatus.getLocked().equals(true)) {

                        System.out.println("Checking if other OTP subtype is also locked...");
                        Optional<AuthtypeStatus> otherSubOtpTypeLocked = authtypeStatusList.stream()
                                .filter(authTypeStatus1 -> authTypeStatus1.getAuthType() != null
                                        && authTypeStatus1.getAuthType().equalsIgnoreCase(MatchType.Category.OTP.getType())
                                        && authTypeStatus1.getAuthSubType() != null
                                        && !authTypeStatus1.getAuthSubType()
                                        .equalsIgnoreCase(authTypeStatus.getAuthSubType())
                                        && authTypeStatus1.getLocked())
                                .findAny();

                        if (otherSubOtpTypeLocked.isPresent()) {
                            System.out.println("Both OTP subtypes locked. Throwing exception.");
                            throw new IdAuthenticationFilterException(
                                    IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
                                    String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
                                            MatchType.Category.OTP.getType()));
                        } else {
                            System.out.println("Other OTP subtype not locked.");
                        }
                    }
                }
            }

            // SPIN
            else if (AuthTypeUtil.isPin(authRequestDTO)
                    && authTypeStatus.getAuthType().equalsIgnoreCase(MatchType.Category.SPIN.getType())) {
                System.out.println("SPIN auth type locked condition matched. Throwing exception.");
                throw new IdAuthenticationFilterException(
                        IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
                        String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
                                MatchType.Category.SPIN.getType()));
            }

        } else {
            System.out.println("Auth type is not locked, skipping validation.");
        }

        System.out.println("---- Exiting validateAuthTypeStatus ----");
    }

}
