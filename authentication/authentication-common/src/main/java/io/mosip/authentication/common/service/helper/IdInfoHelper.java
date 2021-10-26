/*
 * 
 */
package io.mosip.authentication.common.service.helper;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_DEFAULT_IDENTITY_FILTER_ATTRIBUTES;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.util.AuthTypeUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.EntityValueFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.SingleAnySubtypeType;
import io.mosip.kernel.core.logger.spi.Logger;

// TODO: Auto-generated Javadoc
/**
 * Helper class to build Authentication request.
 *
 * @author Dinesh Karuppiah.T
 */

@Component
public class IdInfoHelper {

	/** The id mapping config. */
	@Autowired
	private IDAMappingConfig idMappingConfig;

	/** The id info fetcher. */
	@Autowired
	private IdInfoFetcher idInfoFetcher;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdInfoHelper.class);
	
	/** The default identiy filter attributes. */
	@Value("${" + IDA_DEFAULT_IDENTITY_FILTER_ATTRIBUTES + ":#{null}" + "}")
	private String defaultIdentiyFilterAttributes;
	
	/** The object mapper. */
	@Autowired
	private ObjectMapper objectMapper;
	
	/** The env. */
	@Autowired
	private Environment env;

	/**
	 * Get Authrequest Info.
	 *
	 * @param matchType the match type
	 * @param authRequestDTO the auth request DTO
	 * @return the auth reqest info
	 */
	public Map<String, String> getAuthReqestInfo(MatchType matchType, AuthRequestDTO authRequestDTO) {
		return matchType.getReqestInfoFunction().apply(authRequestDTO);
	}

	/**
	 * Fetch the identity value.
	 *
	 * @param name                 the name
	 * @param languageForMatchType the language for match type
	 * @param identityInfo         the demo info
	 * @param matchType the match type
	 * @return the identity value
	 */
	private Stream<String> getIdentityValueFromMap(String name, String languageForMatchType,
			Map<String, Entry<String, List<IdentityInfoDTO>>> identityInfo, MatchType matchType) {
		List<IdentityInfoDTO> identityInfoList = identityInfo.get(name).getValue();
		if (identityInfoList != null && !identityInfoList.isEmpty()) {
			return identityInfoList.stream()
					.filter(idinfo -> !matchType.isPropMultiLang(name, idMappingConfig)
							|| idInfoFetcher.checkLanguageType(languageForMatchType, idinfo.getLanguage()))
					.map(idInfo -> idInfo.getValue());
		}
		return Stream.empty();
	}

	/**
	 * Gets the id mapping value.
	 *
	 * @param idMapping the id mapping
	 * @param matchType the match type
	 * @return the id mapping value
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public List<String> getIdMappingValue(IdMapping idMapping, MatchType matchType)
			throws IdAuthenticationBusinessException {
		String type = matchType.getCategory().getType();
		List<String> mappings = idMapping.getMappingFunction().apply(idMappingConfig, matchType);
		if (mappings != null && !mappings.isEmpty()) {
			List<String> fullMapping = new ArrayList<>();
			for (String mappingStr : mappings) {
				if (!Objects.isNull(mappingStr) && !mappingStr.isEmpty()) {
					Optional<IdMapping> mappingInternal = IdMapping.getIdMapping(mappingStr, IdaIdMapping.values(), idMappingConfig);
					if (mappingInternal.isPresent() && !idMapping.equals(mappingInternal.get())) {
						List<String> internalMapping = getIdMappingValue(mappingInternal.get(), matchType);
						fullMapping.addAll(internalMapping);
					} else {
						fullMapping.add(mappingStr);
					}
				} else {
					mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
							IdAuthCommonConstants.VALIDATE, "IdMapping config is Invalid for Type -" + type);
					throw new IdAuthenticationBusinessException(
							IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
							IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
				}
			}
			return fullMapping;
		} else {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, "IdMapping config is Invalid for Type -" + type);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
		}
	}

	/**
	 * To check Whether Match type is Enabled.
	 *
	 * @param matchType the match type
	 * @return true, if is matchtype enabled
	 */
	public boolean isMatchtypeEnabled(MatchType matchType) {
		List<String> mappings = matchType.getIdMapping().getMappingFunction().apply(idMappingConfig, matchType);
		return mappings != null && !mappings.isEmpty();
	}

	/**
	 * Gets the entity info as string.
	 *
	 * @param matchType  the match type
	 * @param idEntity the id entity
	 * @return the entity info as string
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public String getEntityInfoAsString(MatchType matchType, Map<String, List<IdentityInfoDTO>> idEntity)
			throws IdAuthenticationBusinessException {
		return getEntityInfoAsString(matchType, null, idEntity);
	}

	/**
	 * Gets the entity info as string.
	 *
	 * @param matchType  the match type
	 * @param langCode the lang code
	 * @param idEntity the id entity
	 * @return the entity info as string
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public String getEntityInfoAsString(MatchType matchType, String langCode,
			Map<String, List<IdentityInfoDTO>> idEntity) throws IdAuthenticationBusinessException {
		Map<String, String> entityInfoMap = getIdEntityInfoMap(matchType, idEntity, langCode);
		if(entityInfoMap == null) {
			entityInfoMap = Map.of();
		}
		return concatValues(entityInfoMap.values().toArray(new String[entityInfoMap.size()]));
	}

	/**
	 * Gets the identity values map.
	 *
	 * @param matchType the match type
	 * @param propertyNames the property names
	 * @param languageCode  the language code
	 * @param idEntity the id entity
	 * @return the identity values map
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private Map<String, String> getIdentityValuesMap(MatchType matchType, List<String> propertyNames,
			String languageCode, Map<String, List<IdentityInfoDTO>> idEntity) throws IdAuthenticationBusinessException {
		Map<String, Entry<String, List<IdentityInfoDTO>>> mappedIdEntity = matchType.mapEntityInfo(idEntity,
				idInfoFetcher);
		Function<? super String, ? extends String> keyMapper = propName -> {
			String key = mappedIdEntity.get(propName).getKey();
			if (languageCode != null) {
				key = key + "_" + languageCode;
			}
			return key;
		};
		Function<? super String, ? extends String> valueMapper = propName -> getIdentityValueFromMap(propName,
				languageCode, mappedIdEntity, matchType).findAny().orElse("");
		
		return propertyNames.stream()
						.filter(propName -> mappedIdEntity.containsKey(propName))
						.collect(
				Collectors.toMap(keyMapper, valueMapper, (p1, p2) -> p1, () -> new LinkedHashMap<String, String>()));
	}

	/**
	 * Gets the entity info map.
	 *
	 * @param matchType     the match type
	 * @param identityInfos the id entity
	 * @param language the language
	 * @return the entity info map
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public Map<String, String> getIdEntityInfoMap(MatchType matchType, Map<String, List<IdentityInfoDTO>> identityInfos,
			String language) throws IdAuthenticationBusinessException {
		return getIdEntityInfoMap(matchType, identityInfos, language, null);
	}
	
	/**
	 * Gets the entity info map.
	 *
	 * @param matchType     the match type
	 * @param identityInfos the id entity
	 * @param language the language
	 * @param idName the id name
	 * @return the entity info map
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public Map<String, String> getIdEntityInfoMap(MatchType matchType, Map<String, List<IdentityInfoDTO>> identityInfos,
			String language, String idName) throws IdAuthenticationBusinessException {
		List<String> propertyNames = getPropertyNamesForMatchType(matchType, idName);
		Map<String, String> identityValuesMap = new LinkedHashMap<>();
		Map<String, String> identityValuesMapWithLang = getIdentityValuesMap(matchType, propertyNames, language, identityInfos);
		Map<String, String> identityValuesMapWithoutLang = getIdentityValuesMap(matchType, propertyNames, null, identityInfos);
		mergeNonNullValues(identityValuesMap, identityValuesMapWithLang);
		mergeNonNullValues(identityValuesMap, identityValuesMapWithoutLang);
		Map<String, Object> props = Map.of(IdInfoFetcher.class.getSimpleName(), idInfoFetcher);
		return matchType.getEntityInfoMapper().apply(identityValuesMap, props);
	}

	/**
	 * Merge non null values.
	 *
	 * @param identityValuesMap the identity values map
	 * @param identityValuesMapWithoutLang the identity values map without lang
	 */
	private void mergeNonNullValues(Map<String, String> identityValuesMap, Map<String, String> identityValuesMapWithoutLang) {
		identityValuesMapWithoutLang.entrySet()
			.stream()
			.filter(entry -> entry.getValue() != null && !entry.getValue().trim().isEmpty())
			.forEach(entry -> identityValuesMap.put(entry.getKey(), entry.getValue()));
	}
	
	/**
	 * This method returns the list of  data capture languages.
	 * These are used to send the notifications in data capture languages.
	 *
	 * @param matchType the match type
	 * @param identityInfos the identity infos
	 * @return the data captured languages
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public List<String> getDataCapturedLanguages(MatchType matchType, Map<String, List<IdentityInfoDTO>> identityInfos)
			throws IdAuthenticationBusinessException {
		List<String> propertyNames = getIdMappingValue(matchType.getIdMapping(), matchType);
		Map<String, Entry<String, List<IdentityInfoDTO>>> mappedIdEntity = matchType.mapEntityInfo(identityInfos,
				idInfoFetcher);
		return mappedIdEntity.get(propertyNames.get(0)).getValue().stream().map(IdentityInfoDTO::getLanguage)
				.collect(Collectors.toList());
	}
	
	/**
	 * Get the ID Entity Info for all languages for the given match type from the
	 * identity infos map.
	 *
	 * @param matchType the match type
	 * @param identityInfos the identity infos
	 * @return the id entity info
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public Map<String, List<IdentityInfoDTO>> getIdEntityInfo(MatchType matchType, Map<String, List<IdentityInfoDTO>> identityInfos)
			throws IdAuthenticationBusinessException {
		List<String> propertyNames = getIdMappingValue(matchType.getIdMapping(), matchType);
		Map<String, Entry<String, List<IdentityInfoDTO>>> mappedIdEntity = matchType.mapEntityInfo(identityInfos,
				idInfoFetcher);
		return propertyNames.stream().collect(Collectors.toMap(Function.identity(), prop -> mappedIdEntity.get(prop).getValue()));
	}


	/**
	 * Match id data.
	 *
	 * @param authRequestDTO  the identity DTO
	 * @param identityEntity  the id entity
	 * @param listMatchInputs the list match inputs
	 * @param partnerId the partner id
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public List<MatchOutput> matchIdentityData(AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> identityEntity, Collection<MatchInput> listMatchInputs, String partnerId)
			throws IdAuthenticationBusinessException {
		List<MatchOutput> matchOutputList = new ArrayList<>();
		for (MatchInput matchInput : listMatchInputs) {
			MatchOutput matchOutput = matchType(authRequestDTO, identityEntity, matchInput, partnerId);
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
	 * @param partnerId the partner id
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public List<MatchOutput> matchIdentityData(AuthRequestDTO authRequestDTO, String uin,
			Collection<MatchInput> listMatchInputs, EntityValueFetcher entityValueFetcher, String partnerId)
			throws IdAuthenticationBusinessException {
		List<MatchOutput> matchOutputList = new ArrayList<>();
		for (MatchInput matchInput : listMatchInputs) {
			MatchOutput matchOutput = matchType(authRequestDTO, uin, matchInput, entityValueFetcher, partnerId);
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
	 * @param partnerId the partner id
	 * @return the match output
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private MatchOutput matchType(AuthRequestDTO authRequestDTO, String uin, MatchInput input,
			EntityValueFetcher entityValueFetcher, String partnerId) throws IdAuthenticationBusinessException {
		return matchType(authRequestDTO, Collections.emptyMap(), uin, input, entityValueFetcher, partnerId);
	}

	/**
	 * Match type.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param idEntity     the id entity
	 * @param input          the input
	 * @param partnerId the partner id
	 * @return the match output
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private MatchOutput matchType(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idEntity,
			MatchInput input, String partnerId) throws IdAuthenticationBusinessException {
		return matchType(authRequestDTO, idEntity, "", input, (t, m, p) -> null, partnerId);
	}

	/**
	 * Match type.
	 *
	 * @param authRequestDTO the id DTO
	 * @param idEntity     the id entity
	 * @param uin the uin
	 * @param input          the input
	 * @param entityValueFetcher the entity value fetcher
	 * @param partnerId the partner id
	 * @return the match output
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private MatchOutput matchType(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idEntity,
			String uin, MatchInput input, EntityValueFetcher entityValueFetcher, String partnerId)
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
				String idName = input.getIdName();
				if (null == reqInfo || reqInfo.isEmpty()) {
					reqInfo = idInfoFetcher.getIdentityRequestInfo(matchType, idName, authRequestDTO.getRequest(),
							input.getLanguage());
				}
				if (null != reqInfo && reqInfo.size() > 0) {

					Map<String, String> entityInfo = getEntityInfo(idEntity, uin, authRequestDTO, input,
							entityValueFetcher, matchType, strategy, idName, partnerId);

					Map<String, Object> matchProperties = input.getMatchProperties();
					
					int mtOut = strategy.match(reqInfo, entityInfo, matchProperties);
					boolean matchOutput = mtOut >= input.getMatchValue();
					return new MatchOutput(mtOut, matchOutput, input.getMatchStrategyType(), matchType,
							input.getLanguage(), idName);
				}
			} else {
				// FIXME Log that matching strategy is not allowed for the match type.
				mosipLogger.info(IdAuthCommonConstants.SESSION_ID, "Matching strategy >>>>>: " + strategyType,
						" is not allowed for - ", matchType + " MatchType");
			}

		}
		return null;
	}

	/**
	 * Construct match type.
	 *
	 * @param idEntity         the id entity
	 * @param uin                the uin
	 * @param req the req
	 * @param input              the input
	 * @param entityValueFetcher the entity value fetcher
	 * @param matchType          the match type
	 * @param strategy           the strategy
	 * @param idName the id name
	 * @param partnerId the partner id
	 * @return the match output
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private Map<String, String> getEntityInfo(Map<String, List<IdentityInfoDTO>> idEntity, 
			String uin,
			AuthRequestDTO req, 
			MatchInput input, 
			EntityValueFetcher entityValueFetcher, 
			MatchType matchType,
			MatchingStrategy strategy, 
			String idName, 
			String partnerId)
			throws IdAuthenticationBusinessException {

		Map<String, String> entityInfo = null;
		if (matchType.hasRequestEntityInfo()) {
			entityInfo = entityValueFetcher.fetch(uin, req, partnerId);
		} else if (matchType.hasIdEntityInfo()) {
			entityInfo = getIdEntityInfoMap(matchType, idEntity, input.getLanguage(), idName);
		} else {
			entityInfo = Collections.emptyMap();
		}

		if (null == entityInfo || entityInfo.isEmpty()
				|| entityInfo.entrySet().stream().anyMatch(value -> value.getValue() == null
						|| value.getValue().isEmpty() || value.getValue().trim().length() == 0)) {
			Category category = matchType.getCategory();
			if (category == Category.BIO) {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorMessage(),
								input.getAuthType().getType()));

			} else if (category == Category.DEMO) {
				if (null == input.getLanguage()) {
					throw new IdAuthenticationBusinessException(
							IdAuthenticationErrorConstants.DEMO_MISSING.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.DEMO_MISSING.getErrorMessage(),
									idName));

				} else {
					throw new IdAuthenticationBusinessException(
							IdAuthenticationErrorConstants.DEMO_MISSING_LANG.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.DEMO_MISSING_LANG.getErrorMessage(),
									idName, input.getLanguage()));
				}

			}
		}
		return entityInfo;
	}

	/**
	 * Concat values.
	 *
	 * @param values the values
	 * @return the string
	 */
	private static String concatValues(String... values) {
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
	 * Gets the dynamic entity info for ID Name.
	 *
	 * @param filteredIdentityInfo the filtered identity info
	 * @param langCode the lang code
	 * @param idName the id name
	 * @return the entity for match type
	 */
	public String getDynamicEntityInfo(Map<String, List<IdentityInfoDTO>> filteredIdentityInfo, String langCode, String idName) {
		try {
			return getIdEntityInfoMap(DemoMatchType.DYNAMIC, filteredIdentityInfo, langCode, idName).entrySet()
					.stream()
					.findFirst()
					.map(Entry::getValue)
					.orElse(null);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getEntityForMatchType",
					e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
		}
		return null;
	}
	
	/**
	 * Gets the default identity filter attributes from configuration.
	 *
	 * @return the default filter attributes
	 */
	public Set<String> getDefaultFilterAttributes() {
		return Optional.ofNullable(defaultIdentiyFilterAttributes).stream()
				.flatMap(str -> Stream.of(str.split(",")))
				.filter(str -> !str.isEmpty())
				.collect(Collectors.toSet());
	}
	
	/**
	 * Builds the demo attribute filters.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the sets the
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@SuppressWarnings("unchecked")
	public Set<String> buildDemoAttributeFilters(AuthRequestDTO authRequestDTO)
			throws IdAuthenticationBusinessException {
		Set<String> defaultFilterAttributes = getDefaultFilterAttributes();
		Set<String> demoFilterAttributes = new HashSet<>(defaultFilterAttributes);
		if (AuthTypeUtil.isDemo(authRequestDTO)) {
			IdentityDTO demographics = authRequestDTO.getRequest().getDemographics();
			Set<String> demoAttributesFromReq = new HashSet<>();
			//Add mapped attributes
			Map<String, Object> demoMap = objectMapper.convertValue(demographics, Map.class);
			List<String> inputMappedAttributes = demoMap.entrySet()
														.stream()
														.filter(entry -> entry.getValue() != null)
														.map(Entry::getKey)
														.collect(Collectors.toList());
			for (String attrib : inputMappedAttributes) {
				if(!attrib.equals(IdAuthCommonConstants.METADATA)) {
					demoAttributesFromReq.addAll(getPropertyNamesForIdName(attrib, false));
				}
			}
			
			//Add dynamic attributes
			Map<String, Object> dynamicAttributes = demographics.getMetadata();
			if(dynamicAttributes != null && !dynamicAttributes.isEmpty()) {
				Set<String> inputUnmappedAttributes = dynamicAttributes.keySet();
				for (String attrib : inputUnmappedAttributes) {
					if(dynamicAttributes.get(attrib) != null) {
						demoAttributesFromReq.addAll(getPropertyNamesForIdName(attrib, true));
					}
				}
			}
			
			demoFilterAttributes.addAll(demoAttributesFromReq);
		}
		return demoFilterAttributes;
	}
	
	/**
	 * To build the bio filters. 
	 * These are used to decrypt only required bio attributes
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the sets the
	 */
	public Set<String> buildBioFilters(AuthRequestDTO authRequestDTO) {
		Set<String> bioFilters = new HashSet<String>();
		if (AuthTypeUtil.isBio(authRequestDTO)) {
			if (AuthTransactionHelper.isFingerAuth(authRequestDTO, env)) {
				List<BioIdentityInfoDTO> bioFingerInfo = getBioIds(authRequestDTO, BioAuthType.FGR_IMG.getType());
				if (!bioFingerInfo.isEmpty()) {
					List<DataDTO> bioFingerData = bioFingerInfo.stream().map(BioIdentityInfoDTO::getData)
							.collect(Collectors.toList());
					// for UNKNOWN getting all the subtypes
					if(bioFingerData.stream().anyMatch(bio->bio.getBioSubType().equals(IdAuthCommonConstants.UNKNOWN_BIO))) {
						bioFilters.addAll(getBioSubTypes(BiometricType.FINGER));
					}else {
						bioFilters.addAll(
								bioFingerData.stream().map(bio -> (bio.getBioType() + "_" + bio.getBioSubType()))
										.collect(Collectors.toList()));
					}
				}
			}

			if (AuthTransactionHelper.isIrisAuth(authRequestDTO, env)) {
				List<BioIdentityInfoDTO> bioIrisInfo = getBioIds(authRequestDTO, BioAuthType.IRIS_IMG.getType());
				if (!bioIrisInfo.isEmpty()) {
					List<DataDTO> bioIrisData = bioIrisInfo.stream().map(BioIdentityInfoDTO::getData)
							.collect(Collectors.toList());
					// for UNKNOWN getting all the subtypes
					if(bioIrisData.stream().anyMatch(bio->bio.getBioSubType().equals(IdAuthCommonConstants.UNKNOWN_BIO))) {
						bioFilters.addAll(getBioSubTypes(BiometricType.IRIS));
					}else {
						bioFilters.addAll(
								bioIrisData.stream().map(bio -> (bio.getBioType() + "_" + bio.getBioSubType()))
										.collect(Collectors.toList()));
					}
				}
			}
			if (AuthTransactionHelper.isFaceAuth(authRequestDTO, env)) {
				List<BioIdentityInfoDTO> bioFaceInfo = getBioIds(authRequestDTO, BioAuthType.FACE_IMG.getType());
				List<DataDTO> bioFaceData = bioFaceInfo.stream().map(BioIdentityInfoDTO::getData)
						.collect(Collectors.toList());
				if (!bioFaceData.isEmpty()) {
					bioFilters.addAll(bioFaceData.stream().map(bio -> (bio.getBioType()))
							.collect(Collectors.toList()));
				}
			}
			return bioFilters;
		}
		return Collections.emptySet();
	}

	/**
	 * Gets the bio ids.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param type the type
	 * @return the bio ids
	 */
	private List<BioIdentityInfoDTO> getBioIds(AuthRequestDTO authRequestDTO, String type) {
		List<BioIdentityInfoDTO> identity = Optional.ofNullable(authRequestDTO.getRequest())
				.map(RequestDTO::getBiometrics).orElseGet(Collections::emptyList);
		if (!identity.isEmpty()) {
			return identity.stream().filter(Objects::nonNull)
					.filter(bioId -> bioId.getData().getBioType().equalsIgnoreCase(type)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
	
	/**
	 * to get all bio subtypes for given type.
	 *
	 * @param type the type
	 * @return the bio sub types
	 */
	public List<String> getBioSubTypes(BiometricType type) {
		switch (type) {
		case FINGER:
			return getFingerSubTypes(type);
		case IRIS:
			return getIrisSubTypes(type);
		default:
			return Collections.emptyList();
		}
	}
	
	/**
	 * Construct and returns finger type along with all the sub types.
	 *
	 * @param type the type
	 * @return the finger sub types
	 */
	private List<String> getFingerSubTypes(BiometricType type){
		return List.of(type.value() + "_" + SingleAnySubtypeType.LEFT.value() + " " + SingleAnySubtypeType.THUMB.value(),
				type.value() + "_" + SingleAnySubtypeType.LEFT.value() + " " + SingleAnySubtypeType.INDEX_FINGER.value(),
				type.value() + "_" + SingleAnySubtypeType.LEFT.value() + " " + SingleAnySubtypeType.MIDDLE_FINGER.value(),
				type.value() + "_" + SingleAnySubtypeType.LEFT.value() + " " + SingleAnySubtypeType.RING_FINGER.value(),
				type.value() + "_" + SingleAnySubtypeType.LEFT.value() + " " + SingleAnySubtypeType.LITTLE_FINGER.value(),
				type.value() + "_" + SingleAnySubtypeType.RIGHT.value() + " " + SingleAnySubtypeType.THUMB.value(),
				type.value() + "_" + SingleAnySubtypeType.RIGHT.value() + " " + SingleAnySubtypeType.INDEX_FINGER.value(),
				type.value() + "_" + SingleAnySubtypeType.RIGHT.value() + " " + SingleAnySubtypeType.MIDDLE_FINGER.value(),
				type.value() + "_" + SingleAnySubtypeType.RIGHT.value() + " " + SingleAnySubtypeType.RING_FINGER.value(),
				type.value() + "_" + SingleAnySubtypeType.RIGHT.value() + " " + SingleAnySubtypeType.LITTLE_FINGER.value());
	}
	
	/**
	 * Construct and returns finger type along with all the sub types.
	 *
	 * @param type the type
	 * @return the iris sub types
	 */
	private List<String> getIrisSubTypes(BiometricType type){
		return List.of(type.value() + "_" + SingleAnySubtypeType.LEFT.value(),
				type.value() + "_" + SingleAnySubtypeType.RIGHT.value());
	}
	
	/**
	 * Gets the property names for match type.
	 *
	 * @param matchType the match type
	 * @param idName the id name
	 * @return the property names for match type
	 */
	public List<String> getPropertyNamesForMatchType(MatchType matchType, String idName) {
		String propertyName = idName != null ? idName : matchType.getIdMapping().getIdname();
		List<String> propertyNames;
		if (!matchType.isDynamic()) {
			if(matchType.getIdMapping().getIdname().equals(idName)) {
				try {
					propertyNames = getIdMappingValue(matchType.getIdMapping(), matchType);
				} catch (IdAuthenticationBusinessException e) {
					mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
							IdAuthCommonConstants.VALIDATE, "Ignoring : IdMapping config is Invalid for Type -" + matchType);
					propertyNames = List.of();
				}
			} else {
				propertyNames = List.of();
			}

		} else {
			if (idMappingConfig.getDynamicAttributes().containsKey(propertyName)) {
				propertyNames = idMappingConfig.getDynamicAttributes().get(propertyName);
			} else {
				propertyNames = List.of(idName);
			}
		}
		return propertyNames;
	}
	
	/**
	 * Gets the property names for id name.
	 *
	 * @param idName the id name
	 * @param isDynamic the is dynamic
	 * @return the property names for id name
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public List<String> getPropertyNamesForIdName(String idName, boolean isDynamic)
			throws IdAuthenticationBusinessException {
		DemoMatchType[] demoMatchTypes = DemoMatchType.values();
		List<String> propNames = new ArrayList<>();
		for (DemoMatchType demoMatchType : demoMatchTypes) {
			if(isDynamic == demoMatchType.isDynamic()) {
				List<String> propertyNamesForMatchType = this.getPropertyNamesForMatchType(demoMatchType, idName);
				if(!propertyNamesForMatchType.isEmpty()) {
					propNames.addAll(propertyNamesForMatchType);
				}
			}
		}
		return propNames;
	}
}
