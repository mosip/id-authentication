package io.mosip.authentication.common.service.impl.match;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import io.mosip.authentication.common.service.impl.AuthTypeImpl;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.spi.bioauth.util.BioMatcherUtil;
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

	SPIN("pin", AuthType.setOf(PinMatchType.SPIN), AuthTypeDTO::isPin, "PIN") {
		/*
		 * (non-Javadoc)
		 * 
		 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#
		 * isAuthTypeInfoAvailable(io.mosip.authentication.core.dto.indauth.
		 * AuthRequestDTO)
		 */
		@Override
		public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
			return Objects.nonNull(authRequestDTO.getRequest().getStaticPin());
		}
	},
	OTP("otp", AuthType.setOf(PinMatchType.OTP), AuthTypeDTO::isOtp, "OTP") {
		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				BioMatcherUtil bioMatcherUtil, String language) {
			Map<String, Object> valueMap = new HashMap<>();
			if (authRequestDTO.getRequestedAuth().isOtp()) {
				ValidateOtpFunction func = idInfoFetcher.getValidateOTPFunction();
				valueMap.put(ValidateOtpFunction.class.getSimpleName(), func);
			}
			return valueMap;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#
		 * isAuthTypeInfoAvailable(io.mosip.authentication.core.dto.indauth.
		 * AuthRequestDTO)
		 */
		@Override
		public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
			return Objects.nonNull(authRequestDTO.getRequest().getOtp());
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
			Predicate<? super AuthTypeDTO> authTypePredicate, String displayName) {
		authTypeImpl = new AuthTypeImpl(type, associatedMatchTypes, authTypePredicate, displayName);
	}

	@Override
	public AuthType getAuthTypeImpl() {
		return authTypeImpl;
	}

}
