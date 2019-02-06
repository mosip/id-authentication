/*
 * 
 */
package io.mosip.authentication.service.helper;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthError;
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
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.EntityValueFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.ValidateOtpFunction;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.factory.BiometricProviderFactory;
import io.mosip.authentication.service.impl.indauth.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.service.impl.indauth.match.IdaIdMapping;
import io.mosip.authentication.service.impl.indauth.service.bio.BioAuthType;
import io.mosip.authentication.service.impl.indauth.service.pin.PinAuthType;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;

/**
 * The Class IdInfoHelper.
 *
 * @author Dinesh Karuppiah.T
 */

@Component
public class IdInfoHelper implements IdInfoFetcher {

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(IdInfoHelper.class);

	private static final String INDIVIDUAL_BIOMETRICS = "individualBiometrics";

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

	/** The BiometricProviderFactory value */
	@Autowired
	private BiometricProviderFactory biometricProviderFactory;

	/** The OTPManager */
	@Autowired
	private OTPManager otpManager;

	@Autowired
	private CbeffUtil cbeffUtil;

	/*
	 * Fetch language Name based on language code
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

	/**
	 * Fetch language code from properties
	 *
	 * @param langType - the language code
	 * @return the language code
	 */
	public String getLanguageCode(LanguageType langType) {
		if (langType == LanguageType.PRIMARY_LANG) {
			return environment.getProperty(PRIMARY_LANG_CODE);
		} else {
			return environment.getProperty(SECONDARY_LANG_CODE);
		}
	}

	public Map<String, String> getAuthReqestInfo(MatchType matchType, AuthRequestDTO authRequestDTO) {
		return matchType.getReqestInfoFunction().apply(authRequestDTO);
	}

	/**
	 * Fetch Identity info based on Match type and Identity
	 *
	 * 
	 * @return Map
	 */
	public Map<String, String> getIdentityRequestInfo(MatchType matchType, IdentityDTO identity, String language) {
		return getInfo(matchType.getIdentityInfoFunction().apply(identity), language);
	}

	/**
	 * Fetch the Identity info based on Identity Info map and Language.
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
	 * Fetch the identity value.
	 *
	 * @param name                 the name
	 * @param languageForMatchType the language for match type
	 * @param demoInfo             the demo info
	 * @return the identity value
	 */
	private Stream<String> getIdentityValueFromMap(String name, String languageForMatchType,
			Map<String, Entry<String, List<IdentityInfoDTO>>> demoInfo) {
		List<IdentityInfoDTO> identityInfoList = demoInfo.get(name).getValue();
		if (identityInfoList != null && !identityInfoList.isEmpty()) {
			return identityInfoList.stream()
					.filter(idinfo -> checkLanguageType(languageForMatchType, idinfo.getLanguage()))
					.map(idInfo -> idInfo.getValue());
		}
		return Stream.empty();
	}

	/**
	 * Fetch the identity value.
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
		if (languageForMatchType == null || languageFromReq == null || languageFromReq.isEmpty()
				|| languageFromReq.equalsIgnoreCase("null")) {
			return languageForMatchType == null
					|| getLanguageCode(LanguageType.PRIMARY_LANG).equalsIgnoreCase(languageForMatchType);
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
	 * @throws IdAuthenticationBusinessException
	 * @throws Exception
	 */
	public String getEntityInfoAsString(MatchType matchType, Map<String, List<IdentityInfoDTO>> demoEntity)
			throws IdAuthenticationBusinessException {
		String langCode = getLanguageCode(LanguageType.PRIMARY_LANG);
		return getEntityInfoAsString(matchType, langCode, demoEntity);
	}

	/**
	 * Gets the entity info as string.
	 *
	 * @param matchType  the match type
	 * @param demoEntity the demo entity
	 * @return the entity info as string
	 * @throws IdAuthenticationBusinessException
	 */
	public String getEntityInfoAsString(MatchType matchType, String langCode,
			Map<String, List<IdentityInfoDTO>> demoEntity) throws IdAuthenticationBusinessException {
		Map<String, String> entityInfoMap = getIdEntityInfoMap(matchType, demoEntity, langCode);
		return concatValues(entityInfoMap.values().toArray(new String[entityInfoMap.size()]));
	}

	/**
	 * Gets the identity values map.
	 *
	 * @param propertyNames the property names
	 * @param languageCode  the language code
	 * @param demoEntity    the demo entity
	 * @return the identity values map
	 * @throws IdAuthenticationBusinessException
	 * @throws Exception
	 */
	private Map<String, String> getIdentityValuesMap(MatchType matchType, List<String> propertyNames,
			String languageCode, Map<String, List<IdentityInfoDTO>> idEntity) throws IdAuthenticationBusinessException {
		Map<String, Entry<String, List<IdentityInfoDTO>>> mappedIdEntity = matchType.mapEntityInfo(idEntity, this);
		return propertyNames.stream().filter(propName -> mappedIdEntity.containsKey(propName)).collect(Collectors.toMap(
				propName -> mappedIdEntity.get(propName).getKey(),
				propName -> getIdentityValueFromMap(propName, languageCode, mappedIdEntity).findAny().orElse(""),
				(p1, p2) -> p1, () -> new LinkedHashMap<String, String>()));
	}

	/**
	 * Gets the entity info map.
	 *
	 * @param matchType  the match type
	 * @param demoEntity the demo entity
	 * @param language
	 * @return the entity info map
	 * @throws IdAuthenticationBusinessException
	 * @throws Exception
	 */
	public Map<String, String> getIdEntityInfoMap(MatchType matchType, Map<String, List<IdentityInfoDTO>> demoEntity,
			String language) throws IdAuthenticationBusinessException {
		List<String> propertyNames = getIdMappingValue(matchType.getIdMapping());
		Map<String, String> identityValuesMap = getIdentityValuesMap(matchType, propertyNames, language, demoEntity);
		return matchType.getEntityInfoMapper().apply(identityValuesMap);
	}

	/**
	 * Match demo data.
	 *
	 * @param authRequestDTO  the identity DTO
	 * @param identityEntity  the demo entity
	 * @param listMatchInputs the list match inputs
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public List<MatchOutput> matchIdentityData(AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> identityEntity, Collection<MatchInput> listMatchInputs)
			throws IdAuthenticationBusinessException {
		List<MatchOutput> matchOutputList = new ArrayList<>();
		for (MatchInput matchInput : listMatchInputs) {
			MatchOutput matchOutput = matchType(authRequestDTO, identityEntity, matchInput);
			if (matchOutput != null) {
				matchOutputList.add(matchOutput);
			}
		}
		return matchOutputList;
	}

	/**
	 * Match identity data.
	 *
	 * @param authRequestDTO     the auth request DTO
	 * @param uin                the uin
	 * @param listMatchInputs    the list match inputs
	 * @param entityValueFetcher the entity value fetcher
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public List<MatchOutput> matchIdentityData(AuthRequestDTO authRequestDTO, String uin,
			Collection<MatchInput> listMatchInputs, EntityValueFetcher entityValueFetcher)
			throws IdAuthenticationBusinessException {
		List<MatchOutput> matchOutputList = new ArrayList<>();
		for (MatchInput matchInput : listMatchInputs) {
			MatchOutput matchOutput = matchType(authRequestDTO, uin, matchInput, entityValueFetcher);
			if (matchOutput != null) {
				matchOutputList.add(matchOutput);
			}
		}
		return matchOutputList;
	}

	/**
	 * Match type.
	 *
	 * @param authRequestDTO     the auth request DTO
	 * @param uin                the uin
	 * @param input              the input
	 * @param entityValueFetcher the entity value fetcher
	 * @return the match output
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private MatchOutput matchType(AuthRequestDTO authRequestDTO, String uin, MatchInput input,
			EntityValueFetcher entityValueFetcher) throws IdAuthenticationBusinessException {
		return matchType(authRequestDTO, Collections.emptyMap(), uin, input, entityValueFetcher);
	}

	/**
	 * Match type.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param demoEntity     the demo entity
	 * @param input          the input
	 * @return the match output
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private MatchOutput matchType(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> demoEntity,
			MatchInput input) throws IdAuthenticationBusinessException {
		return matchType(authRequestDTO, demoEntity, "", input, (t, m) -> null);
	}

	/**
	 * Match type.
	 *
	 * @param authRequestDTO the demo DTO
	 * @param demoEntity     the demo entity
	 * @param input          the input
	 * @return the match output
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private MatchOutput matchType(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> demoEntity,
			String uin, MatchInput input, EntityValueFetcher entityValueFetcher)
			throws IdAuthenticationBusinessException {
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
				Map<String, String> reqInfo = null;
				reqInfo = getAuthReqestInfo(matchType, authRequestDTO);
				if (null == reqInfo || reqInfo.isEmpty()) {
					reqInfo = getIdentityRequestInfo(matchType, authRequestDTO.getRequest().getIdentity(),
							input.getLanguage());
				}
				if (null != reqInfo && reqInfo.size() > 0) {
					Map<String, String> entityInfo = getEntityInfo(demoEntity, uin, authRequestDTO, input,
							entityValueFetcher, matchType, strategy, reqInfo);

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
	 * Construct match type.
	 *
	 * @param demoEntity         the demo entity
	 * @param uin                the uin
	 * @param input              the input
	 * @param entityValueFetcher the entity value fetcher
	 * @param matchType          the match type
	 * @param strategy           the strategy
	 * @param reqInfo            the req info
	 * @return the match output
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private Map<String, String> getEntityInfo(Map<String, List<IdentityInfoDTO>> demoEntity, String uin,
			AuthRequestDTO req, MatchInput input, EntityValueFetcher entityValueFetcher, MatchType matchType,
			MatchingStrategy strategy, Map<String, String> reqInfo) throws IdAuthenticationBusinessException {
		Map<String, String> entityInfo = null;
		if (matchType.hasRequestEntityInfo()) {
			entityInfo = entityValueFetcher.fetch(uin, req);
		} else if (matchType.hasIdEntityInfo()) {
			entityInfo = getIdEntityInfoMap(matchType, demoEntity, input.getLanguage());
		} else {
			entityInfo = Collections.emptyMap();
		}
		if (null == entityInfo || entityInfo.isEmpty()) {
			Category category = matchType.getCategory();
			if (category == Category.BIO) {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BIOMETRIC_MISSING);
			}
		}

		return entityInfo;
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
		return Stream.of(matchTypes).flatMap(matchType -> {
			Set<String> languages = extractAllowedLang();
			List<MatchInput> matchInputs = new ArrayList<>();
			if (matchType.isMultiLanguage()) {
				for (String language : languages) {
					addMatchInput(authRequestDTO, authTypes, matchType, matchInputs, language);
				}
			} else {
				addMatchInput(authRequestDTO, authTypes, matchType, matchInputs, null);
			}
			return matchInputs.stream();
		}).filter(Objects::nonNull).collect(Collectors.toList());

	}

	private void addMatchInput(AuthRequestDTO authRequestDTO, AuthType[] authTypes, MatchType matchType,
			List<MatchInput> matchInputs, String language) {
		Map<String, String> infoFromAuthRequest = matchType.getReqestInfoFunction().apply(authRequestDTO);
		Optional<AuthType> authTypeOpt = AuthType.getAuthTypeForMatchType(matchType, authTypes);
		if (authTypeOpt.isPresent()) {
			matchInputs.add(buildMatchInput(authRequestDTO, matchType, infoFromAuthRequest, authTypeOpt, language));
		}
	}

	private MatchInput buildMatchInput(AuthRequestDTO authRequestDTO, MatchType matchType,
			Map<String, String> infoFromAuthRequest, Optional<AuthType> authTypeOpt, String language) {
		AuthType authType = authTypeOpt.get();
		if (infoFromAuthRequest.isEmpty()) {
			// For Identity
			Optional<IdentityDTO> identityOpt = Optional.ofNullable(authRequestDTO.getRequest())
					.map(RequestDTO::getIdentity);
			if (identityOpt.isPresent()) {
				IdentityDTO identity = identityOpt.get();
				if (authType.isAuthTypeEnabled(authRequestDTO, this)
						&& getIdentityRequestInfo(matchType, identity, language).size() > 0) {
					return contstructMatchInput(authRequestDTO, matchType, authType, language);
				}
			}
		} else {
			// For non-identity
			if (authType.isAuthTypeEnabled(authRequestDTO, this) && authType.isAuthTypeInfoAvailable(authRequestDTO)) {
				return contstructMatchInput(authRequestDTO, matchType, authType, null);
			}
		}
		return null;
	}

	/**
	 * Construct match input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param matchType      TODO
	 * @param authType       TODO
	 * @param language       the language
	 * @return the list
	 */
	public MatchInput contstructMatchInput(AuthRequestDTO authRequestDTO, MatchType matchType, AuthType authType,
			String language) {

		if (matchType.getCategory() == Category.BIO && !authType.isAuthTypeInfoAvailable(authRequestDTO)) {
			return null;
		} else {
			Integer matchValue = DEFAULT_EXACT_MATCH_VALUE;
			String matchingStrategy = MatchingStrategyType.DEFAULT_MATCHING_STRATEGY.getType();

			Optional<String> matchingStrategyOpt = authType.getMatchingStrategy(authRequestDTO, language);
			if (matchingStrategyOpt.isPresent()) {
				matchingStrategy = matchingStrategyOpt.get();
				if (matchingStrategyOpt.get().equals(MatchingStrategyType.PARTIAL.getType())
						|| matchingStrategyOpt.get().equals(MatchingStrategyType.PHONETICS.getType())) {
					Optional<Integer> matchThresholdOpt = authType.getMatchingThreshold(authRequestDTO, language,
							environment);
					matchValue = matchThresholdOpt
							.orElseGet(() -> Integer.parseInt(environment.getProperty(DEFAULT_MATCH_VALUE)));
				}
			}
			Map<String, Object> matchProperties = authType.getMatchProperties(authRequestDTO, this, language);
			DeviceInfo deviceInfo = new DeviceInfo();
			if (authRequestDTO.getAuthType().isBio()) {
				Optional<DeviceInfo> deviceInfoOptional = getDeviceInfo(authRequestDTO.getBioInfo());
				deviceInfo = deviceInfoOptional.orElse(null);
			}

			return new MatchInput(authType, matchType, matchingStrategy, matchValue, matchProperties, deviceInfo,
					language);
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
			} else {
				prepareErrorList(matchOutput, statusInfoBuilder);
			}
		});
	}

	private void prepareErrorList(MatchOutput matchOutput, AuthStatusInfoBuilder statusInfoBuilder) {

		if (matchOutput != null && !matchOutput.isMatched()) {
			String category = matchOutput.getMatchType().getCategory().getType();
			if (category.equalsIgnoreCase(Category.BIO.getType())) {
				constructBioError(matchOutput, statusInfoBuilder);
			} else if (category.equalsIgnoreCase(Category.SPIN.getType())) {
				constructPinError(matchOutput, statusInfoBuilder);
			}
			// TODO to be applied for DEMO and OTP authentications

		}
	}

	/**
	 * Construct pin error.
	 *
	 * @param matchOutput       the match output
	 * @param statusInfoBuilder the status info builder
	 */
	private void constructPinError(MatchOutput matchOutput, AuthStatusInfoBuilder statusInfoBuilder) {
		Optional<AuthType> authTypeForMatchType;
		AuthType authType;
		AuthType[] authTypes;
		authTypes = PinAuthType.values();
		authTypeForMatchType = AuthType.getAuthTypeForMatchType(matchOutput.getMatchType(), authTypes);

		if (authTypeForMatchType.isPresent()) {
			authType = authTypeForMatchType.get();
			AuthError errors = null;

			if (authType.getDisplayName().equals(PinAuthType.SPIN.getDisplayName())) {
				errors = new AuthError(IdAuthenticationErrorConstants.PIN_MISMATCH.getErrorCode(),
						IdAuthenticationErrorConstants.PIN_MISMATCH.getErrorMessage());
			}
			statusInfoBuilder.addErrors(errors);
		}
	}

	/**
	 * Construct bio error.
	 *
	 * @param matchOutput       the match output
	 * @param statusInfoBuilder the status info builder
	 */
	private void constructBioError(MatchOutput matchOutput, AuthStatusInfoBuilder statusInfoBuilder) {
		Optional<AuthType> authTypeForMatchType;
		AuthType authType;
		AuthType[] authTypes;
		authTypes = BioAuthType.values();
		authTypeForMatchType = AuthType.getAuthTypeForMatchType(matchOutput.getMatchType(), authTypes);

		if (authTypeForMatchType.isPresent()) {
			authType = authTypeForMatchType.get();
			AuthError errors = null;

			if (authType.getDisplayName().equals(BioAuthType.FGR_MIN.getDisplayName())) {
				errors = new AuthError(IdAuthenticationErrorConstants.FGRMIN_MISMATCH.getErrorCode(),
						IdAuthenticationErrorConstants.FGRMIN_MISMATCH.getErrorMessage());
			}

			else if (authType.getDisplayName().equals(BioAuthType.IRIS_IMG.getDisplayName())) {
				errors = new AuthError(IdAuthenticationErrorConstants.IRISIMG_MISMATCH.getErrorCode(),
						IdAuthenticationErrorConstants.IRISIMG_MISMATCH.getErrorMessage());

			}
			statusInfoBuilder.addErrors(errors);
		}
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

				statusInfoBuilder.addMatchInfo(authTypeStr, ms, mt, matchInput.getLanguage());
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

	@Override
	public ValidateOtpFunction getValidateOTPFunction() {
		return otpManager::validateOtp;
	}

	/**
	 * Extract allowed lang.
	 *
	 * @return the sets the
	 */
	private Set<String> extractAllowedLang() {
		Set<String> allowedLang;
		String languages = environment.getProperty("mosip.supported-languages");
		if (null != languages && languages.contains(",")) {
			allowedLang = Arrays.stream(languages.split(",")).collect(Collectors.toSet());
		} else {
			allowedLang = new HashSet<>();
			allowedLang.add(languages);
		}
		return allowedLang;
	}

	@Override
	public Map<String, Entry<String, List<IdentityInfoDTO>>> getCbeffValues(Map<String, List<IdentityInfoDTO>> idEntity,
			String type) throws IdAuthenticationBusinessException {
		Optional<String> identityValue = getIdentityValue("documents." + INDIVIDUAL_BIOMETRICS, null, idEntity)
				.findAny();
		if (identityValue.isPresent()) {
			Map<String, String> bdbBasedOnType = new HashMap<>();
			try {
				bdbBasedOnType = cbeffUtil.getBDBBasedOnType(CryptoUtil.decodeBase64(identityValue.get()), type, null);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new IdAuthenticationBusinessException("Inside getCbeffValues", "", e);
			}
			return bdbBasedOnType.entrySet().stream()
					.collect(Collectors.toMap(Entry<String, String>::getKey, (Entry<String, String> entry) -> {
						IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
						identityInfoDTO.setValue(entry.getValue());
						List<IdentityInfoDTO> idenityList = new ArrayList<>(1);
						idenityList.add(identityInfoDTO);
						return new SimpleEntry<>(getNameForCbeffName(entry.getKey()), idenityList);
					}));
		} else {
			return Collections.emptyMap();
		}
	}

	private String getNameForCbeffName(String cbeffName) {
		return Stream.of(IdaIdMapping.values())
				.map(cfg -> new SimpleEntry<>(cfg.getIdname(), cfg.getMappingFunction().apply(idMappingConfig)))
				.filter(entry -> entry.getValue().stream().anyMatch(v -> v.equalsIgnoreCase(cbeffName)))
				.map(Entry::getKey).findAny().orElse("");
	}

	@Override
	public Environment getEnvironment() {
		return environment;
	}
}
