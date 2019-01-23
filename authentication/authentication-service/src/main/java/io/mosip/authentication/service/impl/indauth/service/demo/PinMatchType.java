package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.service.impl.indauth.match.IdaIdMapping;

public enum PinMatchType implements MatchType {

	// @formatter:off

	/** Primary Name Match Type. */
	SPIN(IdaIdMapping.PIN,
			setOf(NameMatchingStrategy.EXACT),
			authReqDTO -> {
				return authReqDTO.getPinInfo().stream().filter(type -> type.getType().equals("pin")).findFirst().map(PinInfo::getValue).orElse("");
			}, LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_PI_NAME_PRI,
			AuthUsageDataBit.MATCHED_PI_NAME_PRI),
	;

	/** The allowed matching strategy. */
	private Set<MatchingStrategy> allowedMatchingStrategy;
	
	private Function<AuthRequestDTO, Map<String, String>> requestInfoFunction;

	/** The used bit. */
	private AuthUsageDataBit usedBit;

	/** The matched bit. */
	private AuthUsageDataBit matchedBit;

	/**  */
	private LanguageType langType;

	/**  */
	private IdMapping idMapping;

	/**
	 * Instantiates a new demo match type.
	 *
	 * @param idMapping
	 * @param allowedMatchingStrategy
	 * @param identityInfoFunction
	 * @param langType
	 * @param usedBit
	 * @param matchedBit
	 * @param entityInfoFetcher
	 */
	private PinMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy,
			Function<AuthRequestDTO, String> requestInfoFunction, LanguageType langType,
			AuthUsageDataBit usedBit, AuthUsageDataBit matchedBit) {
		this.idMapping = idMapping;
		this.requestInfoFunction = (AuthRequestDTO authReq) -> {
			Map<String, String> map = new HashMap<>();
			map.put(idMapping.getIdname(), requestInfoFunction.apply(authReq));
			return map;
		};
		this.langType = langType;
		this.allowedMatchingStrategy = Collections.unmodifiableSet(allowedMatchingStrategy);
		this.usedBit = usedBit;
		this.matchedBit = matchedBit;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.match.MatchType#getLanguageType()
	 */
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
	public Function<Map<String, String>, Map<String, String>> getEntityInfoMapper() {
		return Function.identity();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.service.demo.MatchType#
	 * getIdMapping()
	 */
	public IdMapping getIdMapping() {
		return idMapping;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.service.demo.MatchType#
	 * getIdentityInfoFunction()
	 */
	@Override
	public Function<IdentityDTO, Map<String, List<IdentityInfoDTO>>> getIdentityInfoFunction() {
		return id -> Collections.emptyMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchType#getCategory()
	 */
	@Override
	public Category getCategory() {
		return Category.DEMO;
	}

	@Override
	public Function<AuthRequestDTO, Map<String, String>> getReqestInfoFunction() {
		return requestInfoFunction;
	}

}
