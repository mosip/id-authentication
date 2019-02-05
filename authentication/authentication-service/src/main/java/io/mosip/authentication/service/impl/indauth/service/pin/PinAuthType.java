package io.mosip.authentication.service.impl.indauth.service.pin;

import static io.mosip.authentication.core.spi.indauth.match.AuthType.setOf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.ValidateOtpFunction;

/**
 * The Enum PinAuthType.
 * 
 * @author Sanjay Murali
 */
public enum PinAuthType implements AuthType {

	// @formatter:off

	SPIN("pin", setOf(PinMatchType.SPIN), AuthTypeDTO::isPin, "PIN"),
	OTP("otp", setOf(PinMatchType.OTP), AuthTypeDTO::isOtp, "OTP") {
		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher, String language) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getPinInfo().stream().filter(pininfo -> pininfo.getType().equalsIgnoreCase(this.getType()))
					.forEach((PinInfo pininfovalue) -> {
						ValidateOtpFunction func = idInfoFetcher
								.getValidateOTPFunction();
						valueMap.put(ValidateOtpFunction.class.getSimpleName(), func);
					});
			return valueMap;
		}
	};

	/** The type. */
	private String type;

	/** The associated match types. */
	private Set<MatchType> associatedMatchTypes;

	/** The auth type predicate. */
	private Predicate<? super AuthTypeDTO> authTypePredicate;

	/** The display name. */
	private String displayName;

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
		this.type = type;
		this.authTypePredicate = authTypePredicate;
		this.displayName = displayName;
		this.associatedMatchTypes = Collections.unmodifiableSet(associatedMatchTypes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.impl.indauth.builder.AuthType#getDisplayName(
	 * )
	 */
	@Override
	public String getDisplayName() {
		return displayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.builder.AuthType#getType()
	 */
	@Override
	public String getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.builder.AuthType#
	 * isAuthTypeEnabled(io.mosip.authentication.core.dto.indauth.AuthRequestDTO)
	 */
	@Override
	public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher idInfoFetcher) {
		return Optional.of(authReq).map(AuthRequestDTO::getAuthType).filter(authTypePredicate).isPresent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.builder.AuthType#
	 * getMatchingStrategy(io.mosip.authentication.core.dto.indauth.AuthRequestDTO,
	 * java.util.function.Function)
	 */
	@Override
	public Optional<String> getMatchingStrategy(AuthRequestDTO authReq,
			String languageInfoFetcher) {
		return Optional.of(MatchingStrategyType.EXACT.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.builder.AuthType#
	 * getAssociatedMatchTypes()
	 */
	@Override
	public Set<MatchType> getAssociatedMatchTypes() {
		return Collections.unmodifiableSet(associatedMatchTypes);
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
		return Optional.ofNullable(authRequestDTO.getPinInfo()).flatMap(
				list -> list.stream().filter(pinInfo -> pinInfo.getType().equalsIgnoreCase(getType())).findAny())
				.isPresent();
	}

}
