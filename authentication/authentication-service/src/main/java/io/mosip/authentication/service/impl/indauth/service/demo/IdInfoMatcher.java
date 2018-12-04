package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.service.impl.id.service.impl.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.service.impl.indauth.builder.AuthType;

/**
 * @author Arun Bose The Class IdInfoMatcher.
 */
@Component
public class IdInfoMatcher {

	public static final int DEFAULT_EXACT_MATCH_VALUE = 100;

	public static final String DEFAULT_MATCH_VALUE = "demo.default.match.value";

	@Autowired
	private IdInfoHelper demoHelper;

	/** The environment. */
	@Autowired
	public Environment environment;

	/**
	 * Match demo data.
	 *
	 * @param demoDTO             the demo DTO
	 * @param demoEntity          the demo entity
	 * @param locationInfoFetcher
	 * @param matchInput          the match input
	 * @return the list
	 */
	public List<MatchOutput> matchDemoData(IdentityDTO identityDTO, Map<String, List<IdentityInfoDTO>> demoEntity,
			Collection<MatchInput> listMatchInputs) {
		return listMatchInputs.parallelStream().map(input -> matchType(identityDTO, demoEntity, input))
				.filter(Objects::nonNull).collect(Collectors.toList());
	}

	/**
	 * Match type.
	 *
	 * @param identityDTO the demo DTO
	 * @param demoEntity  the demo entity
	 * @param input       the input
	 * @return the match output
	 */
	private MatchOutput matchType(IdentityDTO identityDTO, Map<String, List<IdentityInfoDTO>> demoEntity,
			MatchInput input) {
		String matchStrategyTypeStr = input.getMatchStrategyType();
		if (matchStrategyTypeStr == null) {
			matchStrategyTypeStr = MatchingStrategyType.EXACT.getType();
		}

		Map<String, Object> matchProperties = input.getMatchProperties();

		Optional<MatchingStrategyType> matchStrategyType = MatchingStrategyType
				.getMatchStrategyType(matchStrategyTypeStr);
		if (matchStrategyType.isPresent()) {
			MatchingStrategyType strategyType = matchStrategyType.get();
			Optional<MatchingStrategy> matchingStrategy = input.getDemoMatchType()
					.getAllowedMatchingStrategy(strategyType);
			if (matchingStrategy.isPresent()) {
				MatchingStrategy strategy = matchingStrategy.get();
				Optional<Object> reqInfoOpt = demoHelper.getIdentityInfo(input.getDemoMatchType(), identityDTO);
				if (reqInfoOpt.isPresent()) {
					Object reqInfo = reqInfoOpt.get();
					Object entityInfo = demoHelper.getEntityInfo(input.getDemoMatchType(), demoEntity);
					MatchFunction matchFunction = strategy.getMatchFunction();
					int mtOut = matchFunction.match(reqInfo, entityInfo, matchProperties);
					boolean matchOutput = mtOut >= input.getMatchValue();
					return new MatchOutput(mtOut, matchOutput, input.getMatchStrategyType(), input.getDemoMatchType());
				}
			}

		}
		return null;
	}

	/**
	 * Construct match input.
	 *
	 * @param idInfoHelper        TODO
	 * @param demoAuthServiceImpl TODO
	 * @param authRequestDTO      the auth request DTO
	 * @param matchTypes
	 * @param authTypes
	 * @return the list
	 */
	public List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO, AuthType[] authTypes,
			MatchType[] matchTypes) {
		return Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
				.map((IdentityDTO identity) -> Stream.of(matchTypes).map((MatchType demoMatchType) -> {
					Optional<AuthType> authTypeOpt = AuthType.getAuthTypeForMatchType(demoMatchType, authTypes);
					Optional<Object> infoOpt = demoHelper.getIdentityInfo(demoMatchType, identity);
					if (infoOpt.isPresent() && authTypeOpt.isPresent()) {
						AuthType demoAuthType = authTypeOpt.get();
						if (demoAuthType.isAuthTypeEnabled(authRequestDTO)) {
							return contstructMatchInput(authRequestDTO, demoMatchType, demoAuthType);
						}
					}
					return null;
				}).filter(Objects::nonNull)).orElseGet(Stream::empty).collect(Collectors.toList());

	}

	/**
	 * Construct match input.
	 *
	 * @param demoAuthServiceImpl TODO
	 * @param authRequestDTO      the auth request DTO
	 * @param matchType       TODO
	 * @param authType        TODO
	 * @return the list
	 */
	public MatchInput contstructMatchInput(AuthRequestDTO authRequestDTO, MatchType matchType,
			AuthType authType) {
		Integer matchValue = DEFAULT_EXACT_MATCH_VALUE;
		String matchingStrategy = MatchingStrategyType.DEFAULT_MATCHING_STRATEGY.getType();
		Optional<String> matchingStrategyOpt = authType.getMatchingStrategy(authRequestDTO,
				demoHelper::getLanguageCode);
		if (matchingStrategyOpt.isPresent()) {
			matchingStrategy = matchingStrategyOpt.get();
			if (matchingStrategyOpt.get().equals(MatchingStrategyType.PARTIAL.getType())
					|| matchingStrategyOpt.get().equals(MatchingStrategyType.PHONETICS.getType())) {
				Optional<Integer> matchThresholdOpt = authType.getMatchingThreshold(authRequestDTO,
						demoHelper::getLanguageCode, environment);
				int defaultMatchValue = Integer.parseInt(environment.getProperty(DEFAULT_MATCH_VALUE));
				matchValue = matchThresholdOpt.orElse(defaultMatchValue);
			}
		}
		Map<String, Object> matchProperties = authType.getMatchProperties(authRequestDTO,
				demoHelper::getLanguageCode);

		return new MatchInput(authType, matchType, matchingStrategy, matchValue, matchProperties);
	}

	public AuthStatusInfo buildStatusInfo(boolean demoMatched, List<MatchInput> listMatchInputs,
			List<MatchOutput> listMatchOutputs, AuthType[] authTypes) {
		AuthStatusInfoBuilder statusInfoBuilder = AuthStatusInfoBuilder.newInstance();

		statusInfoBuilder.setStatus(demoMatched);

		buildMatchInfos(listMatchInputs, statusInfoBuilder, authTypes);

		buildUsageDataBits(listMatchOutputs, statusInfoBuilder);

		return statusInfoBuilder.build();
	}

	private void buildUsageDataBits(List<MatchOutput> listMatchOutputs, AuthStatusInfoBuilder statusInfoBuilder) {
		listMatchOutputs.forEach((MatchOutput matchOutput) -> {
			if (matchOutput.isMatched()) {
				statusInfoBuilder.addAuthUsageDataBits(matchOutput.getDemoMatchType().getMatchedBit());
			}
		});
	}

	public void buildMatchInfos(List<MatchInput> listMatchInputs, AuthStatusInfoBuilder statusInfoBuilder,
			AuthType[] authTypes) {
		listMatchInputs.stream().forEach((MatchInput matchInput) -> {
			boolean hasPartialMatch = matchInput.getDemoMatchType()
					.getAllowedMatchingStrategy(MatchingStrategyType.PARTIAL).isPresent();

			if (hasPartialMatch) {
				String ms = matchInput.getMatchStrategyType();
				if (ms == null || matchInput.getMatchStrategyType().trim().isEmpty()) {
					ms = MatchingStrategyType.DEFAULT_MATCHING_STRATEGY.getType();
				}
				Integer mt = matchInput.getMatchValue();
				if (mt == null) {
					mt = Integer.parseInt(environment.getProperty(DEFAULT_MATCH_VALUE));
				}
				AuthType authType = matchInput.getAuthType();
				String authTypeStr = authType.getType();

				statusInfoBuilder.addMessageInfo(authTypeStr, ms, mt,
						demoHelper.getLanguageCode(matchInput.getDemoMatchType().getLanguageType()));
			}

			statusInfoBuilder.addAuthUsageDataBits(matchInput.getDemoMatchType().getUsedBit());
		});
	}

}
