package io.mosip.authentication.service.impl.indauth.service.demo;

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
			(List<IdentityValue> idValues) -> {
				String[] demoValues = idValues.stream().map(IdentityValue::getValue).toArray(size -> new String[size]);
				return concatDemo(demoValues);
			}),
	
	NAME_SEC(IdMapping.NAME, setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL), IdentityDTO::getName,
			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_PI_NAME_SEC, AuthUsageDataBit.MATCHED_PI_NAME_SEC,
			(List<IdentityValue> idValues) -> {
				String[] demoValues = idValues.stream().map(IdentityValue::getValue).toArray(size -> new String[size]);
				return concatDemo(demoValues);
			}),

//	NAME_SEC(setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL), IdentityDTO::getName,
//			LanguageType.SECONDARY_LANG, AuthUsageDataBit.USED_PI_NAME_SEC, AuthUsageDataBit.MATCHED_PI_NAME_SEC,
//			(entity, locationInfoFetcher) -> concatDemo(entity.getFirstName(), entity.getMiddleName(),
//					entity.getLastName())),
//
//	DOB(setOf(DOBMatchingStrategy.EXACT), IdentityDTO::getDateOfBirth, LanguageType.PRIMARY_LANG,
//			AuthUsageDataBit.USED_PI_DOB, AuthUsageDataBit.MATCHED_PI_DOB,
//			(entity, locationInfoFetcher) -> entity.getDob()),
//
//	AGE(setOf(AgeMatchingStrategy.EXACT), IdentityDTO::getAge, LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_PI_AGE,
//			AuthUsageDataBit.MATCHED_PI_AGE, (DemoEntity entity, LocationInfoFetcher locationInfoFetcher) -> {
//				int age = Period.between(entity.getDob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
//						LocalDate.now()).getYears();
//				return Math.abs(age);
//			}),

	// @formatter:on
	;

	/** The allowed matching strategy. */
	private Set<MatchingStrategy> allowedMatchingStrategy;

	/** The entity info. */
	private DemoEntityInfoFetcher entityInfoFetcher;

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
			AuthUsageDataBit usedBit, AuthUsageDataBit matchedBit, DemoEntityInfoFetcher entityInfoFetcher) {
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
	private DemoEntityInfoFetcher getEntityInfoFetcher() {
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
		String entityInfo = getEntityInfoFetcher().getInfo(demoValues);
		return new IdentityValue(languageName.orElse(""), entityInfo);
	}

}
