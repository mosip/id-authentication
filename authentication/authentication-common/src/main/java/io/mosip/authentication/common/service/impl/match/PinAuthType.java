package io.mosip.authentication.common.service.impl.match;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.mosip.authentication.common.service.impl.AuthTypeImpl;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.ValidateOtpFunction;

/**
 * The Enum PinAuthType - used to construct the Auth type for pin based
 * authentication to determine whether particular Auth information is available
 * and to get respective match properties
 * 
 * @author Sanjay Murali
 */
public enum PinAuthType implements AuthType {

	SPIN("pin", AuthType.setOf(PinMatchType.SPIN), "PIN"),
	OTP("otp", AuthType.setOf(PinMatchType.OTP), "OTP") {
		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				 String language) {
			Map<String, Object> valueMap = new HashMap<>();
			if (isAuthTypeInfoAvailable(authRequestDTO)) {
				ValidateOtpFunction func = idInfoFetcher.getValidateOTPFunction();
				valueMap.put(ValidateOtpFunction.class.getSimpleName(), func);
				valueMap.put(IdAuthCommonConstants.IDVID, authRequestDTO.getIndividualId());
			}
			return valueMap;
		}

	};

	private AuthTypeImpl authTypeImpl;

	/**
	 * Instantiates a new demo auth type.
	 *
	 * @param type                 the type
	 * @param associatedMatchTypes the associated match types
	 * @param langType             the lang type
	 * @param authTypePredicate    the auth type predicate
	 * @param displayName          the display name
	 */
	private PinAuthType(String type, Set<MatchType> associatedMatchTypes,
			String displayName) {
		authTypeImpl = new AuthTypeImpl(type, associatedMatchTypes, displayName);
	}

	@Override
	public AuthType getAuthTypeImpl() {
		return authTypeImpl;
	}

}
