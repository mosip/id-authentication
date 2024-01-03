package io.mosip.authentication.common.service.impl.match;

import static io.mosip.authentication.core.spi.indauth.match.MatchType.setOf;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;

public enum PasswordMatchType implements MatchType {

	/** Primary password Match Type. */
	PASSWORD(IdaIdMapping.PASSWORD, Category.PWD, setOf(PasswordMatchingStrategy.EXACT), authReq -> {
        KycAuthRequestDTO kycAuthRequestDTO =  (KycAuthRequestDTO)authReq;
		return (Objects.nonNull(kycAuthRequestDTO.getRequest())  && 
				Objects.nonNull(kycAuthRequestDTO.getRequest().getPassword()))? kycAuthRequestDTO.getRequest().getPassword() : "";
	});

	/** The allowed matching strategy. */
	private Set<MatchingStrategy> allowedMatchingStrategy;

	/** The request info function. */
	private Function<AuthRequestDTO, Map<String, String>> requestInfoFunction;

	/** The id mapping. */
	private IdMapping idMapping;
	
	private Category category;

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
	private PasswordMatchType(IdMapping idMapping, Category category, Set<MatchingStrategy> allowedMatchingStrategy,
			Function<AuthRequestDTO, String> requestInfoFunction) {
		this.idMapping = idMapping;
		this.category = category;
		this.requestInfoFunction = (AuthRequestDTO authReq) -> {
			Map<String, String> map = new HashMap<>();
			map.put(idMapping.getIdname(), requestInfoFunction.apply(authReq));
			return map;
		};
		this.allowedMatchingStrategy = Collections.unmodifiableSet(allowedMatchingStrategy);
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
	public BiFunction<Map<String, String>, Map<String, Object>, Map<String, String>> getEntityInfoMapper() {
		return (entity, props) -> entity;
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
	public Function<RequestDTO, Map<String, List<IdentityInfoDTO>>> getIdentityInfoFunction() {
		return id -> Collections.emptyMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchType#getCategory()
	 */
	@Override
	public Category getCategory() {
		return category;
	}

	@Override
	public Function<AuthRequestDTO, Map<String, String>> getReqestInfoFunction() {
		return requestInfoFunction;
	}
	
	@Override
	public boolean hasIdEntityInfo() {
		return true;
	}

	@Override
	public boolean hasRequestEntityInfo() {
		return false;
	}

}
