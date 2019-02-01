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

/**
 * The Enum PinMatchType.
 * 
 * @author Sanjay Murali
 */
public enum PinMatchType implements MatchType {

	// @formatter:off

	/** Primary Pin Match Type. */
	SPIN(IdaIdMapping.PIN, setOf(PinMatchingStrategy.EXACT), authReqDTO -> {
		return authReqDTO.getPinInfo().stream().filter(type -> type.getType().equals("pin")).findFirst()
				.map(PinInfo::getValue).orElse("");
	}, LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_STATIC_PIN, AuthUsageDataBit.MATCHED_STATIC_PIN),
	OTP(IdaIdMapping.OTP, setOf(OtpMatchingStrategy.EXACT), 
		authReqDTO -> {
			return authReqDTO.getPinInfo().stream().filter(type -> type.getType().equalsIgnoreCase("otp")).findFirst()
					.map(PinInfo::getValue).orElse("");
		}, 
		LanguageType.PRIMARY_LANG, AuthUsageDataBit.USED_OTP, AuthUsageDataBit.MATCHED_OTP);

	/** The allowed matching strategy. */
	private Set<MatchingStrategy> allowedMatchingStrategy;

	/** The request info function. */
	private Function<AuthRequestDTO, Map<String, String>> requestInfoFunction;

	/** The used bit. */
	private AuthUsageDataBit usedBit;

	/** The matched bit. */
	private AuthUsageDataBit matchedBit;

	/** The lang type. */
	private LanguageType langType;

	/** The id mapping. */
	private IdMapping idMapping;

	/**
	 * Instantiates a new demo match type.
	 *
	 * @param idMapping               the id mapping
	 * @param allowedMatchingStrategy the allowed matching strategy
	 * @param requestInfoFunction     the request info function
	 * @param langType                the lang type
	 * @param usedBit                 the used bit
	 * @param matchedBit              the matched bit
	 */
	private PinMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy,
			Function<AuthRequestDTO, String> requestInfoFunction, LanguageType langType, AuthUsageDataBit usedBit,
			AuthUsageDataBit matchedBit) {
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
	@Override
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
		return Category.SPIN;
	}

	@Override
	public Function<AuthRequestDTO, Map<String, String>> getReqestInfoFunction() {
		return requestInfoFunction;
	}
	
	@Override
	public boolean hasIdEntityInfo() {
		return false;
	}

	@Override
	public boolean hasRequestEntityInfo() {
		return true;
	}

}
