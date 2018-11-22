package io.mosip.authentication.service.impl.indauth.service.demo;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.IdentityValue;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.service.config.IDAMappingConfig;

/**
 * @author Arun Bose The Enum DemoMatchType.
 */

public enum DemoMatchType implements MatchType {

	// @formatter:off

	/** Primary Name Match Type */
	NAME_PRI(IdMapping.NAME, setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL), IdentityDTO::getName,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_PI_NAME_PRI, AuthUsageDataBit.MATCHED_PI_NAME_PRI,
			Function.identity()),

	NAME_SEC(IdMapping.NAME, setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL), IdentityDTO::getName,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_PI_NAME_SEC, AuthUsageDataBit.MATCHED_PI_NAME_SEC,
			Function.identity()),

	DOB(IdMapping.DOB, setOf(DOBMatchingStrategy.EXACT), IdentityDTO::getDateOfBirth, LanguageType.PRIMARY_LANG,
			AuthUsageDataBit.USED_PI_DOB, AuthUsageDataBit.MATCHED_PI_DOB, Function.identity()),

	DOBTYPE(IdMapping.DOBTYPE, setOf(DOBTypeMatchingStrategy.EXACT), IdentityDTO::getDateOfBirthType,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_DOBTYPE, AuthUsageDataBit.MATCHED_DOB_TYPE,
			Function.identity()),

	AGE(IdMapping.AGE, setOf(AgeMatchingStrategy.EXACT), IdentityDTO::getAge, LanguageType.PRIMARY_LANG,
			AuthUsageDataBit.USED_PI_AGE, AuthUsageDataBit.MATCHED_PI_AGE, demoValue -> {
				int age = -1;
				try {
					age = Period.between(DOBMatchingStrategy.DATE_FORMAT.parse(demoValue).toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears();
				} catch (ParseException e) {
					// FIXME log the exception
				}
				return String.valueOf(Math.abs(age));
			}),

	GENDER(IdMapping.GENDER, setOf(GenderMatchingStrategy.EXACT), IdentityDTO::getGender, LanguageType.PRIMARY_LANG,
			AuthUsageDataBit.USED_PI_GENDER, AuthUsageDataBit.MATCHED_PI_GENDER, Function.identity()),

	PHONE(IdMapping.PHONE, setOf(PhoneNoMatchingStrategy.EXACT), IdentityDTO::getPhoneNumber, LanguageType.PRIMARY_LANG,
			AuthUsageDataBit.USED_PI_PHONE, AuthUsageDataBit.MATCHED_PI_PHONE, Function.identity()),

	EMAIL(IdMapping.EMAIL, setOf(EmailMatchingStrategy.EXACT), IdentityDTO::getEmailId, LanguageType.PRIMARY_LANG,
			AuthUsageDataBit.USED_PI_EMAIL, AuthUsageDataBit.MATCHED_PI_EMAIL, Function.identity()),

	ADDR_LINE1_PRI(IdMapping.ADDRESSLINE1, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine1,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_AD_ADDR_LINE1_PRI,
			AuthUsageDataBit.MATCHED_AD_ADDR_LINE1_PRI, Function.identity()),

	/** The addr line1 pri. */
	ADDR_LINE1_SEC(IdMapping.ADDRESSLINE1, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine1,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_AD_ADDR_LINE1_SEC,
			AuthUsageDataBit.MATCHED_AD_ADDR_LINE1_SEC, Function.identity()),

	/** The addr line2 pri. */
	ADDR_LINE2_PRI(IdMapping.ADDRESSLINE2, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine2,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_AD_ADDR_LINE2_PRI,
			AuthUsageDataBit.MATCHED_AD_ADDR_LINE2_PRI, Function.identity()),

	/** The addr line2 pri. */
	ADDR_LINE2_SEC(IdMapping.ADDRESSLINE2, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine2,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_AD_ADDR_LINE2_SEC,
			AuthUsageDataBit.MATCHED_AD_ADDR_LINE2_SEC, Function.identity()),

	/** The addr line3 pri. */
	ADDR_LINE3_PRI(IdMapping.ADDRESSLINE3, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine3,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_AD_ADDR_LINE3_PRI,
			AuthUsageDataBit.MATCHED_AD_ADDR_LINE3_PRI, Function.identity()),

	/** The addr line3 pri. */
	ADDR_LINE3_SEC(IdMapping.ADDRESSLINE3, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getAddressLine3,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_AD_ADDR_LINE3_SEC,
			AuthUsageDataBit.MATCHED_AD_ADDR_LINE3_SEC, Function.identity()),

	LOCATION1_PRI(IdMapping.LOCATION1, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation1,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_LOCATION1_PRI, AuthUsageDataBit.MATCHED_LOCATION1_PRI,
			Function.identity()),

	LOCATION1_SEC(IdMapping.LOCATION1, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation1,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_LOCATION1_SEC, AuthUsageDataBit.MATCHED_LOCATION1_SEC,
			Function.identity()),

	LOCATION2_PRI(IdMapping.LOCATION2, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation2,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_LOCATION2_PRI, AuthUsageDataBit.MATCHED_LOCATION2_PRI,
			Function.identity()),

	LOCATION2_SEC(IdMapping.LOCATION2, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation2,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_LOCATION2_SEC, AuthUsageDataBit.MATCHED_LOCATION2_SEC,
			Function.identity()),

	LOCATION3_PRI(IdMapping.LOCATION3, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation3,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_LOCATION3_PRI, AuthUsageDataBit.MATCHED_LOCATION3_PRI,
			Function.identity()),

	LOCATION3_SEC(IdMapping.LOCATION3, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getLocation3,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_LOCATION3_SEC, AuthUsageDataBit.MATCHED_LOCATION3_SEC,
			Function.identity()),

	/** The pincode pri. */
	PINCODE_PRI(IdMapping.PINCODE, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getPinCode,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_AD_ADDR_PINCODE_PRI,
			AuthUsageDataBit.MATCHED_AD_ADDR_PINCODE_PRI, Function.identity()),

	/** The pincode pri. */
	PINCODE_SEC(IdMapping.PINCODE, setOf(AddressMatchingStrategy.EXACT), IdentityDTO::getPinCode,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_AD_ADDR_PINCODE_SEC,
			AuthUsageDataBit.MATCHED_AD_ADDR_PINCODE_SEC, Function.identity()),

	/** Primary Address MatchType */
	ADDR_PRI(IdMapping.FULLADDRESS, setOf(FullAddressMatchingStrategy.EXACT, FullAddressMatchingStrategy.PARTIAL),
			IdentityDTO::getFullAddress, LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_FAD_ADDR_PRI,
			AuthUsageDataBit.MATCHED_FAD_ADDR_PRI, Function.identity()),

	ADDR_SEC(IdMapping.FULLADDRESS, setOf(FullAddressMatchingStrategy.EXACT, FullAddressMatchingStrategy.PARTIAL),
			IdentityDTO::getFullAddress, LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_FAD_ADDR_SEC,
			AuthUsageDataBit.MATCHED_FAD_ADDR_SEC, Function.identity()),

	// @formatter:on
	;

	/** The allowed matching strategy. */
	private Set<MatchingStrategy> allowedMatchingStrategy;

	/** The entity info. */
	private Function<String, String> entityInfoFetcher;

	/** The used bit. */
	private AuthUsageDataBit usedBit;

	/** The matched bit. */
	private AuthUsageDataBit matchedBit;

	private LanguageType langType;

	private Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction;

	private IdMapping idMapping;

	/**
	 * Instantiates a new demo match type.
	 *
	 * @param allowedMatchingStrategy the allowed matching strategy
	 * @param demoInfo                the demo info
	 * @param entityInfo              the entity info
	 * @param usedBit                 the used bit
	 * @param matchedBit              the matched bit
	 */
	DemoMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy,
			Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction, LanguageType langType,
			AuthUsageDataBit usedBit, AuthUsageDataBit matchedBit, Function<String, String> entityInfoFetcher) {
		this.idMapping = idMapping;
		this.identityInfoFunction = identityInfoFunction;
		this.langType = langType;
		this.allowedMatchingStrategy = Collections.unmodifiableSet(allowedMatchingStrategy);
		this.entityInfoFetcher = entityInfoFetcher;
		this.usedBit = usedBit;
		this.matchedBit = matchedBit;
	}

	public Optional<Object> getIdentityInfo(IdentityDTO identity, Function<LanguageType, String> languageFetcher) {
		String language = languageFetcher.apply(this.getLanguageType());
		return Optional.of(identity).flatMap(identityDTO -> getInfo(identityInfoFunction.apply(identityDTO), language));
	}

	private static Optional<Object> getInfo(List<IdentityInfoDTO> identityInfos, String language) {
		if(identityInfos == null) {
			return Optional.empty();
		}
		return identityInfos.parallelStream()
				.filter(id -> id.getLanguage() != null && language.equalsIgnoreCase(id.getLanguage()))
				.<Object>map(IdentityInfoDTO::getValue).findAny();
	}

	public LanguageType getLanguageType() {
		return langType;
	}

	/**
	 * Gets the allowed matching strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @return the allowed matching strategy
	 */
	public Optional<MatchingStrategy> getAllowedMatchingStrategy(MatchingStrategyType matchStrategyType) {
		return allowedMatchingStrategy.stream().filter(ms -> ms.getType().equals(matchStrategyType)).findAny();
	}

	/**
	 * Gets the entity info.
	 *
	 * @return the entity info
	 */
	private Function<String, String> getEntityInfoFetcher() {
		return entityInfoFetcher;
	}

	/**
	 * Gets the used bit.
	 *
	 * @return the used bit
	 */
	public AuthUsageDataBit getUsedBit() {
		return usedBit;
	}

	/**
	 * Gets the matched bit.
	 *
	 * @return the matched bit
	 */
	public AuthUsageDataBit getMatchedBit() {
		return matchedBit;
	}

	/**
	 * Sets the of.
	 *
	 * @param matchingStrategies the matching strategies
	 * @return the sets the
	 */
	public static Set<MatchingStrategy> setOf(MatchingStrategy... matchingStrategies) {
		return Stream.of(matchingStrategies).collect(Collectors.toSet());

	}

	public IdMapping getIdMapping() {
		return idMapping;
	}

	public static String concatDemo(String... demoValues) {
		StringBuilder demoBuilder = new StringBuilder();
		for (int i = 0; i < demoValues.length; i++) {
			String demo = demoValues[i];
			if (null != demo && demo.length() > 0) {
				demoBuilder.append(demo);
				if (i < demoValues.length - 1) {
					demoBuilder.append(" ");
				}
			}
		}
		return demoBuilder.toString();
	}

	private static Optional<IdentityValue> getIdentityValue(String name, String languageCode,
			Map<String, List<IdentityInfoDTO>> demoInfo) {
		List<IdentityInfoDTO> identityInfoList = demoInfo.get(name);
		if (identityInfoList != null && !identityInfoList.isEmpty()) {
			return identityInfoList.stream().filter(
					idinfo -> idinfo.getLanguage() != null && idinfo.getLanguage().equalsIgnoreCase(languageCode))
					.map(idInfo -> new IdentityValue(languageCode, idInfo.getValue())).findAny();
		}

		return Optional.empty();
	}

	private static List<String> getIdMappingValue(IdMapping idMapping, IDAMappingConfig idMappingConfig) {
		return idMapping.getMappingFunction().apply(idMappingConfig);
	}

	private static List<IdentityValue> getDemoValue(List<String> propertyNames, String languageCode,
			Map<String, List<IdentityInfoDTO>> demoEntity) {
		return propertyNames.stream().map(propName -> getIdentityValue(propName, languageCode, demoEntity))
				.filter(val -> val.isPresent()).<IdentityValue>map(Optional::get).collect(Collectors.toList());
	}

	public IdentityValue getEntityInfo(Map<String, List<IdentityInfoDTO>> demoEntity,
			Function<LanguageType, String> languageCodeFetcher, Function<String, Optional<String>> languageNameFetcher,
			LocationInfoFetcher locationInfoFetcher, IDAMappingConfig idMappingConfig) {
		String languageCode = languageCodeFetcher.apply(getLanguageType());
		Optional<String> languageName = languageNameFetcher.apply(languageCode);
		List<String> propertyNames = getIdMappingValue(getIdMapping(), idMappingConfig);
		List<IdentityValue> demoValues = getDemoValue(propertyNames, languageCode, demoEntity);
		String[] demoValuesStr = demoValues.stream().map(IdentityValue::getValue).toArray(size -> new String[size]);
		String demoValue = concatDemo(demoValuesStr);
		String entityInfo = getEntityInfoFetcher().apply(demoValue);
		return new IdentityValue(languageName.orElse(""), entityInfo);
	}
	
	public Function<IdentityDTO, List<IdentityInfoDTO>> getIdentityInfoFunction() {
		return identityInfoFunction;
	}

}
