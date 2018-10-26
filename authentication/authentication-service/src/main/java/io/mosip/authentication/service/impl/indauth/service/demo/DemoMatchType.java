package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;

/**
 * @author Arun Bose The Enum DemoMatchType.
 */

public enum DemoMatchType implements MatchType {

	// @formatter:off

	/** Primary Name Match Type */
	NAME_PRI(setOf(NameMatchingStrategy.EXACT, NameMatchingStrategy.PARTIAL), IdentityDTO::getName,
			LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_PI_NAME_PRI, AuthUsageDataBit.MATCHED_PI_NAME_PRI,
			(entity, locationInfoFetcher) -> concatDemo(entity.getFirstName(), entity.getMiddleName(),
					entity.getLastName()));

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

	/**
	 * Instantiates a new demo match type.
	 *
	 * @param allowedMatchingStrategy the allowed matching strategy
	 * @param demoInfo                the demo info
	 * @param entityInfo              the entity info
	 * @param usedBit                 the used bit
	 * @param matchedBit              the matched bit
	 */
	DemoMatchType(Set<MatchingStrategy> allowedMatchingStrategy,
			Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction, LanguageType langType,
			AuthUsageDataBit usedBit, AuthUsageDataBit matchedBit, DemoEntityInfoFetcher entityInfoFetcher) {
		this.identityInfoFunction = identityInfoFunction;
		this.langType = langType;
		this.allowedMatchingStrategy = Collections.unmodifiableSet(allowedMatchingStrategy);
		this.entityInfoFetcher = entityInfoFetcher;
		this.usedBit = usedBit;
		this.matchedBit = matchedBit;
	}

	public Optional<Object> getIdentityInfo(IdentityDTO identity, LanguageInfoFetcher languageFetcher) {
		String language = languageFetcher.getLanguage(this.getLanguageType());
		return Optional.of(identity).flatMap(identityDTO -> getInfo(identityInfoFunction.apply(identityDTO), language));
	}

	private static Optional<Object> getInfo(List<IdentityInfoDTO> identityInfos, String language) {
		return identityInfos.parallelStream()
				.filter(id -> id.getLanguage() != null && language.equals(id.getLanguage()))
				.<Object>map(IdentityInfoDTO::getValue)
				.findAny();
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
	public DemoEntityInfoFetcher getEntityInfoFetcher() {
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

}
