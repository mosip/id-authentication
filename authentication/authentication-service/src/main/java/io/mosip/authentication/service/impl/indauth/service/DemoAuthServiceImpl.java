package io.mosip.authentication.service.impl.indauth.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.spi.id.service.IdInfoService;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.service.impl.indauth.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.service.impl.indauth.builder.AuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatcher;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchInput;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchOutput;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategyType;

/**
 * The implementation of Demographic Authentication service.
 * 
 * @author Arun Bose
 */
@Service
public class DemoAuthServiceImpl implements DemoAuthService {

	private static final String DEMO_DEFAULT_MATCH_VALUE = "demo.default.match.value";

	/** The Constant DEFAULT_EXACT_MATCH_VALUE. */
	private static final int DEFAULT_EXACT_MATCH_VALUE = AuthType.DEFAULT_EXACT_MATCH_VALUE;

	/** The environment. */
	@Autowired
	private Environment environment;

	/** The demo matcher. */
	@Autowired
	private DemoMatcher demoMatcher;

	@Autowired
	private IdInfoService idInfoService;

	@Autowired
	private DemoHelper demoHelper;

	/**
	 * Construct match input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the list
	 */
	private List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO) {
		return Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
				.map((IdentityDTO identity) -> Stream.of(DemoMatchType.values()).map((DemoMatchType demoMatchType) -> {
					Optional<AuthType> authTypeOpt = AuthType.getAuthTypeForMatchType(demoMatchType);
					Optional<Object> infoOpt = demoHelper.getIdentityInfo(demoMatchType, identity);
					if (infoOpt.isPresent() && authTypeOpt.isPresent()) {
						AuthType authType = authTypeOpt.get();
						if (authType.isAuthTypeEnabled(authRequestDTO)) {
							return contstructMatchInput(authRequestDTO, demoMatchType, authType);
						}
					}
					return null;
				}).filter(Objects::nonNull)).orElseGet(Stream::empty).collect(Collectors.toList());

	}

	private MatchInput contstructMatchInput(AuthRequestDTO authRequestDTO, DemoMatchType demoMatchType,
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
						demoHelper::getLanguageCode);
				int defaultMatchValue = Integer.parseInt(environment.getProperty(DEMO_DEFAULT_MATCH_VALUE));
				matchValue = matchThresholdOpt.orElse(defaultMatchValue);
			}
		}

		return new MatchInput(demoMatchType, matchingStrategy, matchValue);
	}

	/**
	 * Gets the match output.
	 *
	 * @param listMatchInput the list match input
	 * @param identitydto    the demo DTO
	 * @param demoEntity     the demo entity
	 * @return the match output
	 */
	public List<MatchOutput> getMatchOutput(List<MatchInput> listMatchInputs, IdentityDTO identitydto,
			Map<String, List<IdentityInfoDTO>> demoEntity) {
		return demoMatcher.matchDemoData(identitydto, demoEntity, listMatchInputs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.service.DemoAuthService#
	 * getDemoStatus(io.mosip.authentication.core.dto.indauth.AuthRequestDTO)
	 */
	public AuthStatusInfo getDemoStatus(AuthRequestDTO authRequestDTO, String refId, Map<String, List<IdentityInfoDTO>> demoEntity)
			throws IdAuthenticationBusinessException {

		if (demoEntity == null || demoEntity.isEmpty()) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
		}

		List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO);

		List<MatchOutput> listMatchOutputs = getMatchOutput(listMatchInputs, authRequestDTO.getRequest().getIdentity(),
				demoEntity);
		boolean demoMatched = listMatchOutputs.stream().allMatch(MatchOutput::isMatched);

		return buildStatusInfo(demoMatched, listMatchInputs, listMatchOutputs);

	}

	private AuthStatusInfo buildStatusInfo(boolean demoMatched, List<MatchInput> listMatchInputs,
			List<MatchOutput> listMatchOutputs) {
		AuthStatusInfoBuilder statusInfoBuilder = AuthStatusInfoBuilder.newInstance();

		statusInfoBuilder.setStatus(demoMatched);

		buildMatchInfos(listMatchInputs, statusInfoBuilder);

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

	private void buildMatchInfos(List<MatchInput> listMatchInputs, AuthStatusInfoBuilder statusInfoBuilder) {
		listMatchInputs.stream().forEach((MatchInput matchInput) -> {
			boolean hasPartialMatch = matchInput.getDemoMatchType()
					.getAllowedMatchingStrategy(MatchingStrategyType.PARTIAL).isPresent();
			if (hasPartialMatch && AuthType.getAuthTypeForMatchType(matchInput.getDemoMatchType())
					.map(AuthType::getType).isPresent()) {
				String ms = matchInput.getMatchStrategyType();
				if (ms == null || matchInput.getMatchStrategyType().trim().isEmpty()) {
					ms = MatchingStrategyType.DEFAULT_MATCHING_STRATEGY.getType();
				}
				Integer mt = matchInput.getMatchValue();
				if (mt == null) {
					mt = Integer.parseInt(environment.getProperty(DEMO_DEFAULT_MATCH_VALUE));
				}
				String authType = AuthType.getAuthTypeForMatchType(matchInput.getDemoMatchType()).map(AuthType::getType)
						.orElse("");

				statusInfoBuilder.addMessageInfo(authType, ms, mt,
						demoHelper.getLanguageCode(matchInput.getDemoMatchType().getLanguageType()));
			}

			statusInfoBuilder.addAuthUsageDataBits(matchInput.getDemoMatchType().getUsedBit());
		});
	}


}