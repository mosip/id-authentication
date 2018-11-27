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

/**
 * 
 * @author Dinesh Karuppiah.T
 */

/**
 * The Enum AuthType.
 */
public enum AuthType {

	// @formatter:off


	AD_PRI("address",
			setOf(DemoMatchType.ADDR_LINE1_PRI, DemoMatchType.ADDR_LINE2_PRI, DemoMatchType.ADDR_LINE3_PRI,
					DemoMatchType.LOCATION1_PRI, DemoMatchType.LOCATION2_PRI, DemoMatchType.LOCATION3_PRI,
					DemoMatchType.PINCODE_PRI),
			LanguageType.PRIMARY_LANG, AuthTypeDTO::isAddress,"Address"),
	AD_SEC("address",
			setOf(DemoMatchType.ADDR_LINE1_SEC, DemoMatchType.ADDR_LINE2_SEC, DemoMatchType.ADDR_LINE3_SEC,
					DemoMatchType.LOCATION1_SEC, DemoMatchType.LOCATION2_SEC, DemoMatchType.LOCATION3_SEC,
					DemoMatchType.PINCODE_SEC),
			LanguageType.SECONDARY_LANG, AuthTypeDTO::isAddress,"Address"),

	/** The pi pri. */
	PI_PRI("personalIdentity",
			setOf(DemoMatchType.NAME_PRI, DemoMatchType.DOB, DemoMatchType.DOBTYPE, DemoMatchType.AGE,
					DemoMatchType.EMAIL, DemoMatchType.PHONE, DemoMatchType.GENDER),
			LanguageType.PRIMARY_LANG, AuthTypeDTO::isPersonalIdentity, "Personal Identity"),

	PI_SEC("personalIdentity", setOf(DemoMatchType.NAME_SEC), LanguageType.SECONDARY_LANG,
			AuthTypeDTO::isPersonalIdentity, "Personal Identity"),

	FAD_PRI("fullAddress", setOf(DemoMatchType.ADDR_PRI), LanguageType.PRIMARY_LANG, AuthTypeDTO::isFullAddress,"Full Address"),

	FAD_SEC("fullAddress", setOf(DemoMatchType.ADDR_SEC), LanguageType.SECONDARY_LANG, AuthTypeDTO::isFullAddress,"Full Address"),

	OTP("otp", Collections.emptySet(), LanguageType.PRIMARY_LANG, AuthTypeDTO::isOtp, "OTP") 


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

	private String displayName;

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
			Predicate<? super AuthTypeDTO> authTypePredicate, String displayName) {
		this.type = type;
		this.langType = langType;
		this.authTypePredicate = authTypePredicate;
		this.displayName = displayName;
		this.associatedMatchTypes = Collections.unmodifiableSet(associatedMatchTypes);
	}
	
	public String getDisplayName() {
		return displayName;
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

	public Optional<String> getMatchingStrategy(AuthRequestDTO authReq,
			Function<LanguageType, String> languageInfoFetcher) {
		return getMatchInfo(authReq, languageInfoFetcher, MatchInfo::getMatchingStrategy);

	}

	public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq,
			Function<LanguageType, String> languageInfoFetcher) {
		return getMatchInfo(authReq, languageInfoFetcher, MatchInfo::getMatchingThreshold);
	}

	private <T> Optional<T> getMatchInfo(AuthRequestDTO authReq, Function<LanguageType, String> languageInfoFetcher,
			Function<? super MatchInfo, ? extends T> infoFunction) {
		return Optional.of(authReq)
				.flatMap(authReqDTO -> getMatchInfo(authReqDTO.getMatchInfo(), languageInfoFetcher, infoFunction));
	}

	private <T> Optional<T> getMatchInfo(List<MatchInfo> matchInfos, Function<LanguageType, String> languageInfoFetcher,
			Function<? super MatchInfo, ? extends T> infoFunction) {
		String language = languageInfoFetcher.apply(langType);
		return matchInfos.parallelStream().filter(id -> id.getLanguage() != null
				&& language.equalsIgnoreCase(id.getLanguage()) && getType().equals(id.getAuthType()))
				.<T>map(infoFunction).filter(Objects::nonNull).findAny();
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
	
	public Set<MatchType> getAssociatedMatchTypes() {
		return Collections.unmodifiableSet(associatedMatchTypes);
	}

}
