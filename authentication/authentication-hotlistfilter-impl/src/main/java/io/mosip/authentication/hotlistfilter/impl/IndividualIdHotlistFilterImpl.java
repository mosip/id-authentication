package io.mosip.authentication.hotlistfilter.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.authfilter.spi.IMosipAuthFilter;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.hotlist.dto.HotlistDTO;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.kernel.core.hotlist.constant.HotlistStatus;

/**
 * The Class IndividualIdHotlistFilterImpl - implementation of auth filter for
 * validating hotlisted UIN/VID in the authentication request.
 * 
 * @author Loganathan Sekar
 */
public class IndividualIdHotlistFilterImpl implements IMosipAuthFilter {
	
	/** The hotlist service. */
	@Autowired
	private HotlistService hotlistService;	

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
		validateHotlistedIds(authRequest);
	}
	
	/**
	 * Checks if is individual id hotlisted.
	 *
	 * @param individualId     the individual id
	 * @param individualIdType the individual id type
	 * @param errors           the errors
	 * @throws IdAuthenticationFilterException 
	 */
	private void isIndividualIdHotlisted(String individualId, String individualIdType) throws IdAuthenticationFilterException {
		if (Objects.nonNull(individualId) && Objects.nonNull(individualIdType)) {
			HotlistDTO hotlistStatus = hotlistService.getHotlistStatus(
					IdAuthSecurityManager.generateHashAndDigestAsPlainText(individualId.getBytes()), IdAuthCommonConstants.INDIVIDUAL_ID);
			if (hotlistStatus.getStatus().contentEquals(HotlistStatus.BLOCKED)) {
				throw new IdAuthenticationFilterException(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorCode(), String
						.format(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorMessage(), IdAuthCommonConstants.INDIVIDUAL_ID));
			}
		}
	}
	
	/**
	 * Validate hotlisted ids.
	 *
	 * @param errors         the errors
	 * @param authRequestDto the auth request dto
	 * @throws IdAuthenticationFilterException 
	 */
	private void validateHotlistedIds(AuthRequestDTO authRequestDto) throws IdAuthenticationFilterException {
		isIndividualIdHotlisted(authRequestDto.getIndividualId(), authRequestDto.getIndividualIdType());
	}
	
}
