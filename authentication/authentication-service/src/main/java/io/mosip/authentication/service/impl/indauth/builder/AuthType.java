package io.mosip.authentication.service.impl.indauth.builder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.DemoDTO;
import io.mosip.authentication.core.dto.indauth.PersonalFullAddressDTO;
import io.mosip.authentication.core.dto.indauth.PersonalIdentityDTO;
import io.mosip.authentication.core.dto.indauth.PersonalIdentityDataDTO;
import io.mosip.authentication.service.impl.indauth.service.demo.AuthTypeTester;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchThresholdInfoFetcher;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategyInfoFetcher;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategyType;

/**
 * The Enum AuthType.
 */
public enum AuthType {
	
	// @formatter:off
	
	/**  The pi*/
	PI("pi", setOf(DemoMatchType.GENDER,
					DemoMatchType.AGE,
					DemoMatchType.DOB,
					DemoMatchType.MOBILE,
					DemoMatchType.EMAIL),
			authReq -> Optional.of(authReq)
							.map(AuthRequestDTO::getAuthType)
							.map(AuthTypeDTO::isPi)
							.orElse(false),
			authReq -> Optional.of(MatchingStrategyType.EXACT.getType()),
			authReq -> Optional.of(getDefaultExactMatchValue())),
	
	/**  The pi*/
	AD("ad", setOf(DemoMatchType.ADDR_LINE1_PRI,
					DemoMatchType.ADDR_LINE2_PRI,
					DemoMatchType.ADDR_LINE3_PRI,
					DemoMatchType.CITY_PRI,
					DemoMatchType.STATE_PRI,
					DemoMatchType.COUNTRY_PRI,
					DemoMatchType.PINCODE_PRI),
			authReq -> Optional.of(authReq)
							.map(AuthRequestDTO::getAuthType)
							.map(AuthTypeDTO::isAd)
							.orElse(false),
			authReq -> Optional.of(MatchingStrategyType.EXACT.getType()),
			authReq -> Optional.of(getDefaultExactMatchValue())), 
	 
	/** The pi pri. */
	PI_PRI("piPri", setOf(DemoMatchType.NAME_PRI), 
			authReq -> Optional.of(authReq)
							.map(AuthRequestDTO::getAuthType)
							.map(AuthTypeDTO::isPi)
							.orElse(false),
			authReq -> Optional.of(authReq)
							.map(AuthRequestDTO::getPii)
							.map(PersonalIdentityDataDTO::getDemo)
							.map(DemoDTO::getPi)
							.map(PersonalIdentityDTO::getMsPri),
			authReq -> Optional.of(authReq)
							.map(AuthRequestDTO::getPii)
							.map(PersonalIdentityDataDTO::getDemo)
							.map(DemoDTO::getPi)
							.map(PersonalIdentityDTO::getMtPri)), 
	
	/** The fad pri. */
	FAD_PRI("fadPri", setOf(DemoMatchType.ADDR_PRI), 
			authReq -> Optional.of(authReq)
						.map(AuthRequestDTO::getAuthType)
						.map(AuthTypeDTO::isFad)
						.orElse(false),
			authReq -> Optional.of(authReq)
						.map(AuthRequestDTO::getPii)
						.map(PersonalIdentityDataDTO::getDemo)
						.map(DemoDTO::getFad)
						.map(PersonalFullAddressDTO::getMsPri),
			authReq -> Optional.of(authReq)
						.map(AuthRequestDTO::getPii)
						.map(PersonalIdentityDataDTO::getDemo)
						.map(DemoDTO::getFad)
						.map(PersonalFullAddressDTO::getMtPri)),
	
//	/** The bio. */
//	BIO("bio", Collections.emptySet())
	
	
	/**  */
// @formatter:on
	;
	public static final int DEFAULT_EXACT_MATCH_VALUE = 100;
	
	/** The type. */
	private String type;
	
	/**  */
	private Set<MatchType> associatedMatchTypes;
	
	/**  */
	private AuthTypeTester authTypeTester;
	
	/**  */
	private MatchingStrategyInfoFetcher msInfoFetcher;
	
	/**  */
	private MatchThresholdInfoFetcher mtInfoFetcher;


	/**
	 * 
	 *
	 * @param type 
	 * @param associatedMatchTypes 
	 * @param authTypeTester 
	 * @param msInfoFetcher 
	 * @param mtInfoFetcher 
	 */
	private AuthType(String type, Set<MatchType> associatedMatchTypes, AuthTypeTester authTypeTester, MatchingStrategyInfoFetcher msInfoFetcher, MatchThresholdInfoFetcher mtInfoFetcher) {
		this.type = type;
		this.authTypeTester = authTypeTester;
		this.msInfoFetcher = msInfoFetcher;
		this.mtInfoFetcher = mtInfoFetcher;
		this.associatedMatchTypes = Collections.unmodifiableSet(associatedMatchTypes);
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * 
	 *
	 * @return 
	 */
	public static int getDefaultExactMatchValue() {
		return DEFAULT_EXACT_MATCH_VALUE;
	}
	
	/**
	 * Sets the of.
	 *
	 * @param supportedMatchTypes 
	 * @return the sets the
	 */
	public static Set<MatchType> setOf(MatchType... supportedMatchTypes) {
		return Stream.of(supportedMatchTypes).collect(Collectors.toSet());
	}
	
	/**
	 * 
	 *
	 * @param matchType 
	 * @return 
	 */
	public boolean isAssociatedMatchType(MatchType matchType) {
		return associatedMatchTypes.contains(matchType);
	}
	
	/**
	 * 
	 *
	 * @return 
	 */
	public AuthTypeTester getAuthTypeTester() {
		return authTypeTester;
	}
	
	/**
	 * 
	 *
	 * @return 
	 */
	public MatchingStrategyInfoFetcher getMsInfoFetcher() {
		return msInfoFetcher;
	}
	
	/**
	 * 
	 *
	 * @return 
	 */
	public MatchThresholdInfoFetcher getMtInfoFetcher() {
		return mtInfoFetcher;
	}
	
	/**
	 * 
	 *
	 * @param matchType 
	 * @return 
	 */
	public static Optional<AuthType> getAuthTypeForMatchType(MatchType matchType) {
		return Stream.of(values())
				.filter(at -> at.isAssociatedMatchType(matchType))
				.findAny();
	}
	
	public boolean isExactMatchOnly() {
		return associatedMatchTypes.stream()
									.noneMatch(matchType -> matchType.getAllowedMatchingStrategy(MatchingStrategyType.PARTIAL).isPresent());
	}
}
