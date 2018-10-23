package io.mosip.authentication.service.impl.indauth.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.DemoDTO;
import io.mosip.authentication.core.dto.indauth.PersonalIdentityDataDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.service.impl.indauth.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.service.impl.indauth.builder.AuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoEntity;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatcher;
import io.mosip.authentication.service.impl.indauth.service.demo.LocationEntity;
import io.mosip.authentication.service.impl.indauth.service.demo.LocationInfoFetcher;
import io.mosip.authentication.service.impl.indauth.service.demo.LocationLevel;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchInput;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchOutput;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategyType;
import io.mosip.authentication.service.repository.DemoRepository;
import io.mosip.authentication.service.repository.LocationRepository;

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
	private DemoRepository demoRepository;

	@Autowired
	private LocationRepository locationRepository;

	/**
	 * Construct match input.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @return the list
	 */
	private List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO) {
		 return Optional.ofNullable(authRequestDTO.getPii())
				.map(PersonalIdentityDataDTO::getDemo)
				.map(demo -> Stream.of(DemoMatchType.values())
				.map(demoMatchType -> {
					Optional<AuthType> authTypeOpt = AuthType.getAuthTypeForMatchType(demoMatchType);
					Optional<Object> infoOpt = demoMatchType.getDemoInfoFetcher().getInfo(demo);
					if (infoOpt.isPresent() && authTypeOpt.isPresent()) {
						AuthType authType = authTypeOpt.get();
						if(authType.getAuthTypeTester().testAuthType(authRequestDTO)) {
							return contstructMatchInput(authRequestDTO, demoMatchType, authType);
						}
					}
					return null;
				})
				.filter(Objects::nonNull))
				.orElseGet(Stream::empty)
				.collect(Collectors.toList());
		 
				

	}

	private MatchInput contstructMatchInput(AuthRequestDTO authRequestDTO, DemoMatchType demoMatchType,
			AuthType authType) {
		Integer matchValue = DEFAULT_EXACT_MATCH_VALUE;
		String matchingStrategy = MatchingStrategyType.DEFAULT_MATCHING_STRATEGY.getType();
		Optional<String> matchingStrategyOpt = authType.getMsInfoFetcher().getMatchingStratogy(authRequestDTO);
		if (matchingStrategyOpt.isPresent()) {
			matchingStrategy = matchingStrategyOpt.get();
			if (matchingStrategyOpt.get().equals(MatchingStrategyType.PARTIAL.getType())) {
				Optional<Integer> matchThresholdOpt = authType.getMtInfoFetcher().getMatchThreshold(authRequestDTO);
				int defaultMatchValue = Integer.parseInt(environment.getProperty(DEMO_DEFAULT_MATCH_VALUE));
				matchValue = matchThresholdOpt.orElse(defaultMatchValue);
			}
		}

		return new MatchInput(demoMatchType, matchingStrategy, matchValue);
	}

		/**
	 * Gets the match output.
	 *
	 * @param listMatchInput
	 *            the list match input
	 * @param demoDTO
	 *            the demo DTO
	 * @param demoEntity
	 *            the demo entity
	 * @return the match output
	 */
	public List<MatchOutput> getMatchOutput(List<MatchInput> listMatchInputs, DemoDTO demoDTO, DemoEntity demoEntity,
			LocationInfoFetcher locationInfoFetcher) {

		return demoMatcher.matchDemoData(demoDTO, demoEntity, listMatchInputs, locationInfoFetcher);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.service.DemoAuthService#
	 * getDemoStatus(io.mosip.authentication.core.dto.indauth.AuthRequestDTO)
	 */
	public AuthStatusInfo getDemoStatus(AuthRequestDTO authRequestDTO, String refId)
			throws IdAuthenticationBusinessException {
		boolean demoMatched = false;
		List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO);

		DemoEntity demoEntity = getDemoEntity(refId, environment.getProperty("mosip.primary.lang-code"));

		if (demoEntity == null) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
		}
		
		List<MatchOutput> listMatchOutputs = getMatchOutput(listMatchInputs, authRequestDTO.getPii().getDemo(),
				demoEntity, this::getLocation);
		demoMatched = listMatchOutputs.stream().allMatch(MatchOutput::isMatched);
		
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
			if (AuthType.getAuthTypeForMatchType(matchInput.getDemoMatchType())
					.filter(authType -> !authType.isExactMatchOnly())
					.map(AuthType::getType)
					.isPresent()) {

				String ms = matchInput.getMatchStrategyType();
				if (ms == null || matchInput.getMatchStrategyType().trim().isEmpty()) {
					ms = MatchingStrategyType.DEFAULT_MATCHING_STRATEGY.getType();
				}

				Integer mt = matchInput.getMatchValue();
				if (mt == null) {
					mt = Integer.parseInt(environment.getProperty(DEMO_DEFAULT_MATCH_VALUE));
				}

				String authType = AuthType.getAuthTypeForMatchType(matchInput.getDemoMatchType())
						.map(AuthType::getType).orElse("");

				statusInfoBuilder.addMessageInfo(authType, ms, mt);
			}

			statusInfoBuilder.addAuthUsageDataBits(matchInput.getDemoMatchType().getUsedBit());
		});
	}

	/**
	 * Gets the demo entity.
	 *
	 * @param uniqueId
	 *            the unique id
	 * @return the demo entity
	 */
	public DemoEntity getDemoEntity(String refId, String langCode) {
		return demoRepository.findByUinRefIdAndLangCode(refId, langCode.toUpperCase());// Assuming keeping Langcode
																						// Upper case in DB
	}

	public Optional<String> getLocation(LocationLevel targetLocationLevel, String locationCode) {
		Optional<LocationEntity> locationEntity = locationRepository.findByCodeAndIsActive(locationCode, true);
		if (locationEntity.isPresent()) {
			LocationEntity entity = locationEntity.get();
			String entitylocname = entity.getName();
			String entityparentcode = entity.getParentloccode();
			String entityloclevel = entity.getHierarchylevelname();
			if (targetLocationLevel.getName().equalsIgnoreCase(entityloclevel)) {
				return Optional.of(entitylocname);
			} else {
				return getLocation(targetLocationLevel, entityparentcode);
			}
		}
		return Optional.empty();

	}

}