/*
 * 
 */
package io.mosip.authentication.service.helper;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.core.dto.indauth.DeviceInfo;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider;
import io.mosip.authentication.core.spi.fingerprintauth.provider.FingerprintProvider;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.authentication.core.spi.irisauth.provider.IrisProvider;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.factory.BiometricProviderFactory;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.CogentFingerprintProvider;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.MantraFingerprintProvider;
import io.mosip.authentication.service.impl.indauth.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.service.impl.indauth.match.IdaIdMapping;
import io.mosip.authentication.service.impl.iris.CogentIrisProvider;
import io.mosip.authentication.service.impl.iris.MorphoIrisProvider;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class IdInfoHelper.
 *
 * @author Dinesh Karuppiah.T
 */

@Component
public class IdInfoHelper implements IdInfoFetcher {

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(IdInfoHelper.class);

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String SESSION_ID = "sessionId";

	/** The Constant PRIMARY_LANG_CODE. */
	private static final String PRIMARY_LANG_CODE = "mosip.primary.lang-code";

	/** The Constant SECONDARY_LANG_CODE. */
	private static final String SECONDARY_LANG_CODE = "mosip.secondary.lang-code";

	/** The Constant DEFAULT_EXACT_MATCH_VALUE. */
	public static final int DEFAULT_EXACT_MATCH_VALUE = 100;

	/** The Constant DEFAULT_MATCH_VALUE. */
	public static final String DEFAULT_MATCH_VALUE = "demo.min.match.value";

	/** The id mapping config. */
	@Autowired
	private IDAMappingConfig idMappingConfig;

	/** The environment. */
	@Autowired
	private Environment environment;

	@Autowired
	private BiometricProviderFactory biometricProviderFactory;

	/**
	 * The environment.
	 *
	 * @param languageCode the language code
	 * @return the language name
	 */

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher#getLanguageCode(
	 * io.mosip.authentication.core.dto.indauth.LanguageType)
	 */
	public String getLanguageCode(LanguageType langType) {
		if (langType == LanguageType.PRIMARY_LANG) {
			return environment.getProperty(PRIMARY_LANG_CODE);
		} else {
			return environment.getProperty(SECONDARY_LANG_CODE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher#getIdentityInfo(
	 * io.mosip.authentication.core.spi.indauth.match.MatchType,
	 * io.mosip.authentication.core.dto.indauth.IdentityDTO)
	 */
	public Map<String, String> getIdentityInfo(MatchType matchType, IdentityDTO identity) {
		String language = getLanguageCode(matchType.getLanguageType());
		return getInfo(matchType.getIdentityInfoFunction().apply(identity), language);
	}

	/**
	 * Gets the info.
	 *
	 * @param idInfosMap           the id infos map
	 * @param languageForMatchType the language for match type
	 * @return the info
	 */
	private Map<String, String> getInfo(Map<String, List<IdentityInfoDTO>> idInfosMap, String languageForMatchType) {
		if (idInfosMap != null && !idInfosMap.isEmpty()) {
			return idInfosMap.entrySet().parallelStream()

					.map(entry -> new SimpleEntry<String, String>(entry.getKey(),
							Optional.ofNullable(entry.getValue()).flatMap(value -> value.stream()
									.filter(idInfo -> checkLanguageType(languageForMatchType, idInfo.getLanguage()))
									.map(IdentityInfoDTO::getValue).findAny()).orElse("")))
					.filter(entry -> entry.getValue().length() > 0)
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		}
		return Collections.emptyMap();
	}

	/**
	 * Gets the identity value.
	 *
	 * @param name                 the name
	 * @param languageForMatchType the language for match type
	 * @param demoInfo             the demo info
	 * @return the identity value
	 */
	private Stream<String> getIdentityValue(String name, String languageForMatchType,
			Map<String, List<IdentityInfoDTO>> demoInfo) {
		List<IdentityInfoDTO> identityInfoList = demoInfo.get(name);
		if (identityInfoList != null && !identityInfoList.isEmpty()) {
			return identityInfoList.stream()
					.filter(idinfo -> checkLanguageType(languageForMatchType, idinfo.getLanguage()))
					.map(idInfo -> idInfo.getValue());
		}

		return Stream.empty();
	}

	/**
	 * Check language type.
	 *
	 * @param languageForMatchType the language for match type
	 * @param languageFromReq      the language from req
	 * @return true, if successful
	 */
	private boolean checkLanguageType(String languageForMatchType, String languageFromReq) {
		if (languageFromReq == null || languageFromReq.isEmpty() || languageFromReq.equalsIgnoreCase("null")) {
			return getLanguageCode(LanguageType.PRIMARY_LANG).equalsIgnoreCase(languageForMatchType);
		} else {
			return languageForMatchType.equalsIgnoreCase(languageFromReq);
		}
	}

	/**
	 * Gets the id mapping value.
	 *
	 * @param idMapping the id mapping
	 * @return the id mapping value
	 */
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

	/**
	 * Gets the entity info as string.
	 *
	 * @param matchType  the match type
	 * @param demoEntity the demo entity
	 * @return the entity info as string
	 */
	public String getEntityInfoAsString(MatchType matchType, Map<String, List<IdentityInfoDTO>> demoEntity) {
		Map<String, String> entityInfoMap = getEntityInfoMap(matchType, demoEntity);
		return concatValues(entityInfoMap.values().toArray(new String[entityInfoMap.size()]));
	}

	/**
	 * Gets the identity values map.
	 *
	 * @param propertyNames the property names
	 * @param languageCode  the language code
	 * @param demoEntity    the demo entity
	 * @return the identity values map
	 */
	private Map<String, String> getIdentityValuesMap(List<String> propertyNames, String languageCode,
			Map<String, List<IdentityInfoDTO>> demoEntity) {
		return propertyNames.stream()
				.collect(Collectors.toMap(Function.identity(),
						propName -> getIdentityValue(propName, languageCode, demoEntity).findAny().orElse(""),
						(p1, p2) -> p1, () -> new LinkedHashMap<String, String>()));
	}

	/**
	 * Gets the entity info map.
	 *
	 * @param matchType  the match type
	 * @param demoEntity the demo entity
	 * @return the entity info map
	 */
	public Map<String, String> getEntityInfoMap(MatchType matchType, Map<String, List<IdentityInfoDTO>> demoEntity) {
		String languageCode = getLanguageCode(matchType.getLanguageType()).toLowerCase();
		List<String> propertyNames = getIdMappingValue(matchType.getIdMapping());
		Map<String, String> identityValuesMap = getIdentityValuesMap(propertyNames, languageCode, demoEntity);
		Map<String, String> entityInfo = matchType.getEntityInfoMapper().apply(identityValuesMap);
		return entityInfo;
	}

	/**
	 * Match demo data.
	 *
	 * @param identityDTO     the identity DTO
	 * @param identityEntity  the demo entity
	 * @param listMatchInputs the list match inputs
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public List<MatchOutput> matchIdentityData(IdentityDTO identityDTO,
			Map<String, List<IdentityInfoDTO>> identityEntity, Collection<MatchInput> listMatchInputs)
			throws IdAuthenticationBusinessException {
		List<MatchOutput> matchOutputList = new ArrayList<>();
		for (MatchInput matchInput : listMatchInputs) {
			MatchOutput matchOutput = matchType(identityDTO, identityEntity, matchInput);
			if (matchOutput != null) {
				matchOutputList.add(matchOutput);
			}
		}
		return matchOutputList;
	}

	/**
	 * Match type.
	 *
	 * @param identityDTO the demo DTO
	 * @param demoEntity  the demo entity
	 * @param input       the input
	 * @return the match output
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private MatchOutput matchType(IdentityDTO identityDTO, Map<String, List<IdentityInfoDTO>> demoEntity,
			MatchInput input) throws IdAuthenticationBusinessException {
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
				Map<String, String> reqInfo = getIdentityInfo(matchType, identityDTO);
				if (reqInfo.size() > 0) {
					Map<String, String> entityInfo = getEntityInfoMap(matchType, demoEntity);
					Map<String, Object> matchProperties = input.getMatchProperties();
					int mtOut = strategy.match(reqInfo, entityInfo, matchProperties);
					boolean matchOutput = mtOut >= input.getMatchValue();
					return new MatchOutput(mtOut, matchOutput, input.getMatchStrategyType(), matchType);
				}
			} else {
				// FIXME Log that matching strategy is not allowed for the match type.
				logger.info(SESSION_ID, "Matching strategy >>>>>: " + strategyType, " is not allowed for - ",
						matchType + " MatchType");
			}

		}
		return null;
	}

	/**
	 * Construct match input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param authTypes      the auth types
	 * @param matchTypes     the match types
	 * @return the list
	 */
	public List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO, AuthType[] authTypes,
			MatchType[] matchTypes) {
		return Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
				.map((IdentityDTO identity) -> {
					return Stream.of(matchTypes).map((MatchType matchType) -> {
						Optional<AuthType> authTypeOpt = AuthType.getAuthTypeForMatchType(matchType, authTypes);
						if (authTypeOpt.isPresent()) {
							AuthType demoAuthType = authTypeOpt.get();
							if (demoAuthType.isAuthTypeEnabled(authRequestDTO, this)
									&& getIdentityInfo(matchType, identity).size() > 0) {
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
	 * @param authRequestDTO the auth request DTO
	 * @param matchType      TODO
	 * @param authType       TODO
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
			Map<String, Object> matchProperties = authType.getMatchProperties(authRequestDTO, this);
			DeviceInfo deviceInfo = new DeviceInfo();
			if (authRequestDTO.getAuthType().isBio()) {
				Optional<DeviceInfo> deviceInfoOptional = getDeviceInfo(authRequestDTO.getBioInfo());
				deviceInfo = deviceInfoOptional.orElse(null);
			}

			return new MatchInput(authType, matchType, matchingStrategy, matchValue, matchProperties, deviceInfo);
		}
	}

	private Optional<DeviceInfo> getDeviceInfo(List<BioInfo> bioInfo) {
		return bioInfo.stream().findAny().map(BioInfo::getDeviceInfo);
	}

	/**
	 * Builds the status info.
	 *
	 * @param demoMatched      the demo matched
	 * @param listMatchInputs  the list match inputs
	 * @param listMatchOutputs the list match outputs
	 * @param authTypes        the auth types
	 * @return the auth status info
	 */
	public AuthStatusInfo buildStatusInfo(boolean demoMatched, List<MatchInput> listMatchInputs,
			List<MatchOutput> listMatchOutputs, AuthType[] authTypes) {
		AuthStatusInfoBuilder statusInfoBuilder = AuthStatusInfoBuilder.newInstance();

		statusInfoBuilder.setStatus(demoMatched);

		buildMatchInfos(listMatchInputs, statusInfoBuilder, authTypes);

		buildBioInfos(listMatchInputs, statusInfoBuilder, authTypes);

		buildUsageDataBits(listMatchOutputs, statusInfoBuilder);

		return statusInfoBuilder.build();
	}

	/**
	 * Builds the Bio info
	 * 
	 * @param listMatchInputs
	 * @param statusInfoBuilder
	 * @param authTypes
	 */
	private void buildBioInfos(List<MatchInput> listMatchInputs, AuthStatusInfoBuilder statusInfoBuilder,
			AuthType[] authTypes) {
		listMatchInputs.stream().forEach((MatchInput matchInput) -> {
			MatchType matchType = matchInput.getMatchType();
			boolean hasPartialMatch = matchType.getAllowedMatchingStrategy(MatchingStrategyType.PARTIAL).isPresent();
			Category category = matchType.getCategory();
			if (hasPartialMatch && category.equals(Category.BIO)) {
				AuthType authType = matchInput.getAuthType();
				String bioTypeStr = authType.getType();
				DeviceInfo deviceInfo = matchInput.getDeviceInfo();
				statusInfoBuilder.addBioInfo(bioTypeStr, deviceInfo);
			}

			statusInfoBuilder.addAuthUsageDataBits(matchType.getUsedBit());
		});

	}

	/**
	 * Builds the usage data bits.
	 *
	 * @param listMatchOutputs  the list match outputs
	 * @param statusInfoBuilder the status info builder
	 */
	private void buildUsageDataBits(List<MatchOutput> listMatchOutputs, AuthStatusInfoBuilder statusInfoBuilder) {
		listMatchOutputs.forEach((MatchOutput matchOutput) -> {
			if (matchOutput.isMatched()) {
				statusInfoBuilder.addAuthUsageDataBits(matchOutput.getMatchType().getMatchedBit());
			}
		});
	}

	/**
	 * Builds the match infos.
	 *
	 * @param listMatchInputs   the list match inputs
	 * @param statusInfoBuilder the status info builder
	 * @param authTypes         the auth types
	 */
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

				statusInfoBuilder.addMatchInfo(authTypeStr, ms, mt, getLanguageCode(matchType.getLanguageType()));
			}

			statusInfoBuilder.addAuthUsageDataBits(matchType.getUsedBit());
		});
	}

	/**
	 * Concat values.
	 *
	 * @param values the values
	 * @return the string
	 */
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

	/**
	 * Gets the finger print provider.
	 *
	 * @param bioinfovalue the bioinfovalue
	 * @return the finger print provider
	 */
	public MosipBiometricProvider getFingerPrintProvider(BioInfo bioinfovalue) {
		return biometricProviderFactory.getBiometricProvider(bioinfovalue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher#getIrisProvider(
	 * io.mosip.authentication.core.dto.indauth.BioInfo)
	 */
	public MosipBiometricProvider getIrisProvider(BioInfo bioinfovalue) {
		return biometricProviderFactory.getBiometricProvider(bioinfovalue);
	}

}
