package io.mosip.authentication.service.helper;

import java.util.ArrayList;
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
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.impl.indauth.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.service.impl.indauth.match.IdaIdMapping;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@Component
public class IdInfoHelper {

	private static final String PRIMARY_LANG_CODE = "mosip.primary.lang-code";
	private static final String SECONDARY_LANG_CODE = "mosip.secondary.lang-code";

	public static final int DEFAULT_EXACT_MATCH_VALUE = 100;
	public static final String DEFAULT_MATCH_VALUE = "demo.default.match.value";

	@Autowired
	private IDAMappingConfig idMappingConfig;

	/** The environment. */
	@Autowired
	private Environment environment;

	/** The environment. */

	public Optional<String> getLanguageName(String languageCode) {
		String languagName = null;
		String key = null;
		if (languageCode != null) {
			key = "mosip.phonetic.lang.".concat(languageCode.toLowerCase()); // mosip.phonetic.lang.
			String property = environment.getProperty(key);
			if (property != null && !property.isEmpty()) {
				String[] split = property.split("-");
				languagName = split[0];
			}
		}
		return Optional.ofNullable(languagName);
	}

	public String getLanguageCode(LanguageType langType) {
		if (langType == LanguageType.PRIMARY_LANG) {
			return environment.getProperty(PRIMARY_LANG_CODE);
		} else {
			return environment.getProperty(SECONDARY_LANG_CODE);
		}
	}

	public Optional<Object> getIdentityInfo(MatchType matchType, IdentityDTO identity) {
		String language = getLanguageCode(matchType.getLanguageType());
		return Optional.of(identity)
				.flatMap(identityDTO -> getInfo(matchType.getIdentityInfoFunction().apply(identityDTO), language));
	}

	private Optional<Object> getInfo(List<IdentityInfoDTO> identityInfos, String languageForMatchType) {
		if (identityInfos != null && !identityInfos.isEmpty()) {
			return identityInfos.parallelStream()
					.filter((IdentityInfoDTO id) -> {
						return checkLanguageType(languageForMatchType, id.getLanguage());
					})
					.<Object>map(IdentityInfoDTO::getValue).findAny();
		}
		return Optional.empty();
	}

	private Optional<String> getIdentityValue(String name, String languageForMatchType,
			Map<String, List<IdentityInfoDTO>> demoInfo) {
		List<IdentityInfoDTO> identityInfoList = demoInfo.get(name);
		if (identityInfoList != null && !identityInfoList.isEmpty()) {
			return identityInfoList.stream()
					.filter(idinfo -> checkLanguageType(languageForMatchType, idinfo.getLanguage()))
					.map(idInfo -> idInfo.getValue()).findAny();
		}

		return Optional.empty();
	}

	private boolean checkLanguageType(String languageForMatchType, String languageFromReq) {
		if(languageFromReq == null || languageFromReq.isEmpty()) {
			return getLanguageCode(LanguageType.PRIMARY_LANG).equalsIgnoreCase(languageForMatchType);
		} else {
			return languageForMatchType.equalsIgnoreCase(languageFromReq);
		}
	}

	public List<String> getIdMappingValue(IdMapping idMapping) {
		List<String> mappings = idMapping.getMappingFunction().apply(idMappingConfig);
		List<String> fullMapping = new ArrayList<>();
		for (String mappingStr : mappings) {
			Optional<IdMapping> mappingInternal = IdMapping.getIdMapping(mappingStr, IdaIdMapping.values());
			if (mappingInternal.isPresent() && idMapping != mappingInternal.get()) {
				List<String> internalMapping = getIdMappingValue(mappingInternal.get());
				fullMapping.addAll(internalMapping);
			} else {
				fullMapping.add(mappingStr);
			}
		}
		return fullMapping;
	}

	private List<String> getIdentityValue(List<String> propertyNames, String languageCode,
			Map<String, List<IdentityInfoDTO>> demoEntity) {
		return propertyNames.stream().map(propName -> getIdentityValue(propName, languageCode, demoEntity))
				.filter(val -> val.isPresent()).map(Optional::get).collect(Collectors.toList());
	}

	public String getEntityInfo(MatchType matchType, Map<String, List<IdentityInfoDTO>> demoEntity) {
		String languageCode = getLanguageCode(matchType.getLanguageType()).toLowerCase();
		List<String> propertyNames = getIdMappingValue(matchType.getIdMapping());
		List<String> identityValues = getIdentityValue(propertyNames, languageCode, demoEntity);
		String[] demoValuesStr = identityValues.stream().toArray(size -> new String[size]);
		String demoValue = concatValues(demoValuesStr);
		String entityInfo = matchType.getEntityInfoMapper().apply(demoValue);
		return entityInfo;
	}

	/**
	 * Match demo data.
	 *
	 * @param demoDTO             the demo DTO
	 * @param identityEntity      the demo entity
	 * @param locationInfoFetcher
	 * @param matchInput          the match input
	 * @return the list
	 */
	public List<MatchOutput> matchIdentityData(IdentityDTO identityDTO,
			Map<String, List<IdentityInfoDTO>> identityEntity, Collection<MatchInput> listMatchInputs) {
		return listMatchInputs.parallelStream().map(input -> matchType(identityDTO, identityEntity, input))
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

		Optional<MatchingStrategyType> matchStrategyType = MatchingStrategyType
				.getMatchStrategyType(matchStrategyTypeStr);
		if (matchStrategyType.isPresent()) {
			MatchingStrategyType strategyType = matchStrategyType.get();
			MatchType matchType = input.getMatchType();
			Optional<MatchingStrategy> matchingStrategy = matchType.getAllowedMatchingStrategy(strategyType);
			if (matchingStrategy.isPresent()) {
				MatchingStrategy strategy = matchingStrategy.get();
				Optional<Object> reqInfoOpt = getIdentityInfo(matchType, identityDTO);
				if (reqInfoOpt.isPresent()) {
					Object reqInfo = reqInfoOpt.get();
					Object entityInfo = getEntityInfo(matchType, demoEntity);
					MatchFunction matchFunction = strategy.getMatchFunction();
					Map<String, Object> matchProperties = input.getMatchProperties();
					int mtOut = matchFunction.match(reqInfo, entityInfo, matchProperties);
					boolean matchOutput = mtOut >= input.getMatchValue();
					return new MatchOutput(mtOut, matchOutput, input.getMatchStrategyType(), matchType);
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
				.map((IdentityDTO identity) -> {
					return Stream.of(matchTypes).map((MatchType matchType) -> {
						Optional<AuthType> authTypeOpt = AuthType.getAuthTypeForMatchType(matchType, authTypes);
						Optional<Object> infoOpt = getIdentityInfo(matchType, identity);
						if (infoOpt.isPresent() && authTypeOpt.isPresent()) {
							AuthType demoAuthType = authTypeOpt.get();
							if (demoAuthType.isAuthTypeEnabled(authRequestDTO)) {
								return contstructMatchInput(authRequestDTO, matchType, demoAuthType);
							}
						}
						return null;
					}).filter(Objects::nonNull);
				}).orElseGet(Stream::empty).collect(Collectors.toList());

	}

	/**
	 * Construct match input.
	 *
	 * @param demoAuthServiceImpl TODO
	 * @param authRequestDTO      the auth request DTO
	 * @param matchType           TODO
	 * @param authType            TODO
	 * @return the list
	 */
	public MatchInput contstructMatchInput(AuthRequestDTO authRequestDTO, MatchType matchType, AuthType authType) {

		if (matchType.getCategory() == Category.BIO && !authType.isAuthTypeInfoAvailable(authRequestDTO)) {
			return null;
		} else {
			Integer matchValue = DEFAULT_EXACT_MATCH_VALUE;
			String matchingStrategy = MatchingStrategyType.DEFAULT_MATCHING_STRATEGY.getType();
			Optional<String> matchingStrategyOpt = authType.getMatchingStrategy(authRequestDTO, this::getLanguageCode);
			if (matchingStrategyOpt.isPresent()) {
				matchingStrategy = matchingStrategyOpt.get();
				if (matchingStrategyOpt.get().equals(MatchingStrategyType.PARTIAL.getType())
						|| matchingStrategyOpt.get().equals(MatchingStrategyType.PHONETICS.getType())) {
					Optional<Integer> matchThresholdOpt = authType.getMatchingThreshold(authRequestDTO,
							this::getLanguageCode, environment);
					matchValue = matchThresholdOpt
							.orElseGet(() -> Integer.parseInt(environment.getProperty(DEFAULT_MATCH_VALUE)));
				}
			}
			Map<String, Object> matchProperties = authType.getMatchProperties(authRequestDTO, this::getLanguageCode);

			return new MatchInput(authType, matchType, matchingStrategy, matchValue, matchProperties);
		}
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
			MatchType matchType = matchInput.getMatchType();
			boolean hasPartialMatch = matchType.getAllowedMatchingStrategy(MatchingStrategyType.PARTIAL).isPresent();
			Category category = matchType.getCategory();
			if (hasPartialMatch && category.equals(Category.DEMO)) {
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

				statusInfoBuilder.addMessageInfo(authTypeStr, ms, mt, getLanguageCode(matchType.getLanguageType()));
			}

			statusInfoBuilder.addAuthUsageDataBits(matchType.getUsedBit());
		});
	}

	public static String concatValues(String... values) {
		StringBuilder demoBuilder = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			String demo = values[i];
			if (null != demo && demo.length() > 0) {
				demoBuilder.append(demo);
				if (i < values.length - 1) {
					demoBuilder.append(" ");
				}
			}
		}
		return demoBuilder.toString();
	}

}
