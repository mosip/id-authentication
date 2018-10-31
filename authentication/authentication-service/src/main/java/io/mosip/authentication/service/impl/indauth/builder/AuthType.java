package io.mosip.authentication.service.impl.indauth.builder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.dto.indauth.MatchInfo;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategyType;

/**
 * The Enum AuthType.
 */
public enum AuthType {

	// @formatter:off

//	/** The pi */
//	PI("pi", setOf(DemoMatchType.GENDER, DemoMatchType.AGE, DemoMatchType.DOB, DemoMatchType.MOBILE,
//			DemoMatchType.EMAIL),
//			authReq -> Optional.of(authReq).map(AuthRequestDTO::getAuthType).map(AuthTypeDTO::isPi).orElse(false),
//			authReq -> Optional.of(MatchingStrategyType.EXACT.getType()),
//			authReq -> Optional.of(getDefaultExactMatchValue())),
//
//	/** The pi */
//	AD("ad", setOf(DemoMatchType.ADDR_LINE1_PRI, DemoMatchType.ADDR_LINE2_PRI, DemoMatchType.ADDR_LINE3_PRI,
//			DemoMatchType.CITY_PRI, DemoMatchType.STATE_PRI, DemoMatchType.COUNTRY_PRI, DemoMatchType.PINCODE_PRI),
//			authReq -> Optional.of(authReq).map(AuthRequestDTO::getAuthType).map(AuthTypeDTO::isAd).orElse(false),
//			authReq -> Optional.of(MatchingStrategyType.EXACT.getType()),
//			authReq -> Optional.of(getDefaultExactMatchValue())),

	/** The pi pri. */
	PI_PRI("personalIdentity", setOf(DemoMatchType.NAME_PRI), LanguageType.PRIMARY_LANG,
			AuthTypeDTO::isPersonalIdentity),

	PI_SEC("personalIdentity", setOf(DemoMatchType.NAME_SEC), LanguageType.SECONDARY_LANG,
			AuthTypeDTO::isPersonalIdentity),
//	/** The fad pri. *
//	FAD_PRI("fadPri", setOf(DemoMatchType.ADDR_PRI),
//			authReq -> Optional.of(authReq).map(AuthRequestDTO::getAuthType).map(AuthTypeDTO::isFad).orElse(false),
//			authReq -> Optional.of(authReq).map(AuthRequestDTO::getPii).map(PersonalIdentityDataDTO::getDemo)
//					.map(DemoDTO::getFad).map(PersonalFullAddressDTO::getMsPri),
//			authReq -> Optional.of(authReq).map(AuthRequestDTO::getPii).map(PersonalIdentityDataDTO::getDemo)
//					.map(DemoDTO::getFad).map(PersonalFullAddressDTO::getMtPri)),

	// /** The bio. */
	// BIO("bio", Collections.emptySet())

	/**  */
	// @formatter:on
	;

	public static final int DEFAULT_EXACT_MATCH_VALUE = 100;

	/** The type. */
	private String type;

	/**  */
	private Set<MatchType> associatedMatchTypes;

	private Predicate<? super AuthTypeDTO> authTypePredicate;

	private LanguageType langType;


	/**
	 * 
	 *
	 * @param type
	 * @param associatedMatchTypes
	 * @param authTypeTester
	 * @param msInfoFetcher
	 * @param mtInfoFetcher
	 */
	private AuthType(String type, Set<MatchType> associatedMatchTypes, LanguageType langType,
			Predicate<? super AuthTypeDTO> authTypePredicate) {
		this.type = type;
		this.langType = langType;
		this.authTypePredicate = authTypePredicate;
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

	public LanguageType getLangType() {
		return langType;
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

	public boolean isAuthTypeEnabled(AuthRequestDTO authReq) {
		return Optional.of(authReq).map(AuthRequestDTO::getAuthType).filter(authTypePredicate).isPresent();
	}

	public Optional<String> getMatchingStrategy(AuthRequestDTO authReq, Function<LanguageType, String> languageInfoFetcher) {
		return getMatchInfo(authReq, languageInfoFetcher, MatchInfo::getMatchingStrategy);

	}
	
	public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, Function<LanguageType, String> languageInfoFetcher) {
		return getMatchInfo(authReq, languageInfoFetcher, MatchInfo::getMatchingThreshold);
	}
	
	private <T> Optional<T> getMatchInfo(AuthRequestDTO authReq, Function<LanguageType, String> languageInfoFetcher, Function<? super MatchInfo, ? extends T> infoFunction) {
		return Optional.of(authReq)
						.flatMap(authReqDTO -> getMatchInfo(
													authReqDTO.getMatchInfo(), 
													languageInfoFetcher, 
													infoFunction)
								);
	}
	
	private <T> Optional<T> getMatchInfo(List<MatchInfo> matchInfos, Function<LanguageType, String> languageInfoFetcher, Function<? super MatchInfo, ? extends T> infoFunction) {
		String language = languageInfoFetcher.apply(langType);
		return matchInfos.parallelStream()
				.filter(id -> id.getLanguage() != null && language.equalsIgnoreCase(id.getLanguage()) && getType().equals(id.getAuthType()))
				.<T>map(infoFunction)
				.filter(Objects::nonNull)
				.findAny();
	}

	/**
	 * 
	 *
	 * @param matchType
	 * @return
	 */
	public static Optional<AuthType> getAuthTypeForMatchType(MatchType matchType) {
		return Stream.of(values()).filter(at -> at.isAssociatedMatchType(matchType)).findAny();
	}

	public boolean isExactMatchOnly() {
		return associatedMatchTypes.stream()
				.noneMatch(matchType -> matchType.getAllowedMatchingStrategy(MatchingStrategyType.PARTIAL).isPresent());
	}
}
