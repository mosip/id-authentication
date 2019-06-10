/*
 * 
 */
package io.mosip.authentication.common.service.helper;

import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.LanguageType;
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
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class IdInfoHelper.
 *
 * @author Dinesh Karuppiah.T
 */

/**
 * 
 * Helper class to build Authentication request
 *
 */

@Component
public class IdInfoHelper {

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(IdInfoHelper.class);

	/** The id mapping config. */
	@Autowired
	private IDAMappingConfig idMappingConfig;

	/** The environment. */
	@Autowired
	private Environment environment;

	@Autowired
	private IdInfoFetcher idInfoFetcher;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdInfoHelper.class);

	/**
	 * Get Authrequest Info
	 * 
	 * @param matchType
	 * @param authRequestDTO
	 * @return
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
	 * @param matchType
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
	 * @param matchType
	 * @return the id mapping value
	 * @throws IdAuthenticationBusinessException
	 */
	public List<String> getIdMappingValue(IdMapping idMapping, MatchType matchType)
			throws IdAuthenticationBusinessException {
		String type = matchType.getCategory().getType();
		List<String> mappings = idMapping.getMappingFunction().apply(idMappingConfig, matchType);
		if (mappings != null && !mappings.isEmpty()) {
			List<String> fullMapping = new ArrayList<>();
			for (String mappingStr : mappings) {
				if (!Objects.isNull(mappingStr) && !mappingStr.isEmpty()) {
					Optional<IdMapping> mappingInternal = IdMapping.getIdMapping(mappingStr, IdaIdMapping.values());
					if (mappingInternal.isPresent() && idMapping != mappingInternal.get()) {
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
	 * To check Whether Match type is Enabled
	 * 
	 * @param idMapping
	 * @param matchType
	 * @return
	 */
	public boolean isMatchtypeEnabled(MatchType matchType) {
		List<String> mappings = matchType.getIdMapping().getMappingFunction().apply(idMappingConfig, matchType);
		return mappings != null && !mappings.isEmpty();
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
		String langCode = idInfoFetcher.getLanguageCode(LanguageType.PRIMARY_LANG);
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
		Map<String, Entry<String, List<IdentityInfoDTO>>> mappedIdEntity = matchType.mapEntityInfo(idEntity,
				idInfoFetcher);
		return propertyNames.stream().filter(propName -> mappedIdEntity.containsKey(propName))
				.collect(
						Collectors.toMap(propName -> mappedIdEntity.get(propName).getKey(),
								propName -> getIdentityValueFromMap(propName, languageCode, mappedIdEntity, matchType)
										.findAny().orElse(""),
								(p1, p2) -> p1, () -> new LinkedHashMap<String, String>()));
	}

	/**
	 * Gets the entity info map.
	 *
	 * @param matchType     the match type
	 * @param identityInfos the demo entity
	 * @param language
	 * @return the entity info map
	 * @throws IdAuthenticationBusinessException
	 * @throws Exception
	 */
	public Map<String, String> getIdEntityInfoMap(MatchType matchType, Map<String, List<IdentityInfoDTO>> identityInfos,
			String language) throws IdAuthenticationBusinessException {
		List<String> propertyNames = getIdMappingValue(matchType.getIdMapping(), matchType);
		Map<String, String> identityValuesMap = getIdentityValuesMap(matchType, propertyNames, language, identityInfos);
		return matchType.getEntityInfoMapper().apply(identityValuesMap);
	}

	/**
	 * Match demo data.
	 *
	 * @param authRequestDTO  the identity DTO
	 * @param identityEntity  the demo entity
	 * @param listMatchInputs the list match inputs
	 * @param partnerId
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
	 * @param partnerId
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
	 * @param partnerId
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
	 * @param demoEntity     the demo entity
	 * @param input          the input
	 * @param partnerId
	 * @return the match output
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private MatchOutput matchType(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> demoEntity,
			MatchInput input, String partnerId) throws IdAuthenticationBusinessException {
		return matchType(authRequestDTO, demoEntity, "", input, (t, m, p) -> null, partnerId);
	}

	/**
	 * Match type.
	 *
	 * @param authRequestDTO the demo DTO
	 * @param demoEntity     the demo entity
	 * @param input          the input
	 * @param partnerId
	 * @return the match output
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private MatchOutput matchType(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> demoEntity,
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
				if (null == reqInfo || reqInfo.isEmpty()) {
					reqInfo = idInfoFetcher.getIdentityRequestInfo(matchType, authRequestDTO.getRequest(),
							input.getLanguage());
				}
				if (null != reqInfo && reqInfo.size() > 0) {
					Map<String, String> entityInfo = getEntityInfo(demoEntity, uin, authRequestDTO, input,
							entityValueFetcher, matchType, strategy, reqInfo, partnerId);

					Map<String, Object> matchProperties = input.getMatchProperties();
					int mtOut = strategy.match(reqInfo, entityInfo, matchProperties);
					boolean matchOutput = mtOut >= input.getMatchValue();
					return new MatchOutput(mtOut, matchOutput, input.getMatchStrategyType(), matchType,
							input.getLanguage());
				}
			} else {
				// FIXME Log that matching strategy is not allowed for the match type.
				logger.info(IdAuthCommonConstants.SESSION_ID, "Matching strategy >>>>>: " + strategyType,
						" is not allowed for - ", matchType + " MatchType");
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
	 * @param partnerId
	 * @return the match output
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private Map<String, String> getEntityInfo(Map<String, List<IdentityInfoDTO>> demoEntity, String uin,
			AuthRequestDTO req, MatchInput input, EntityValueFetcher entityValueFetcher, MatchType matchType,
			MatchingStrategy strategy, Map<String, String> reqInfo, String partnerId)
			throws IdAuthenticationBusinessException {
		Map<String, String> entityInfo = null;
		if (matchType.hasRequestEntityInfo()) {
			entityInfo = entityValueFetcher.fetch(uin, req, partnerId);
		} else if (matchType.hasIdEntityInfo()) {
			entityInfo = getIdEntityInfoMap(matchType, demoEntity, input.getLanguage());
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
									matchType.getIdMapping().getIdname()));

				} else {
					throw new IdAuthenticationBusinessException(
							IdAuthenticationErrorConstants.DEMO_MISSING_LANG.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.DEMO_MISSING_LANG.getErrorMessage(),
									matchType.getIdMapping().getIdname(), input.getLanguage()));
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
	 * Extract allowed lang.
	 *
	 * @return the sets the
	 */
	public Set<String> getAllowedLang() {
		Set<String> allowedLang;
		String languages = environment.getProperty(IdAuthConfigKeyConstants.MOSIP_SUPPORTED_LANGUAGES);
		if (null != languages && languages.contains(",")) {
			allowedLang = Arrays.stream(languages.split(",")).collect(Collectors.toSet());
		} else {
			allowedLang = new HashSet<>();
			allowedLang.add(languages);
		}
		return allowedLang;
	}

}
