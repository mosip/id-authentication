package io.mosip.authentication.common.service.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * 
 * Builder class to match Inputs
 *
 * @author Dinesh Karuppiah.T
 *
 */
@Component
public class MatchInputBuilder {

	private static Logger mosipLogger = IdaLogger.getLogger(MatchInputBuilder.class);
	
	/** The Constant DEFAULT_EXACT_MATCH_VALUE. */
	public static final int DEFAULT_EXACT_MATCH_VALUE = 100;
	public static final int DEFAULT_PARTIAL_MATCH_VALUE = DEFAULT_EXACT_MATCH_VALUE;

	@Autowired
	private IdInfoFetcher idInfoFetcher;
	
	/** The environment. */
	@Autowired
	private Environment environment;

	/** The Constant DEFAULT_MATCH_VALUE. */
	public static final String DEFAULT_MATCH_VALUE = "demo.threshold";

	/**
	 * Construct match input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param authTypes      the auth types
	 * @param matchTypes     the match types
	 * @return the list
	 */
	public List<MatchInput> buildMatchInput(AuthRequestDTO authRequestDTO, AuthType[] authTypes, MatchType[] matchTypes,
			Map<String, List<IdentityInfoDTO>> demoEntity) {
		List<String> languages = idInfoFetcher.getSystemSupportedLanguageCodes();
		return Stream.of(matchTypes).flatMap(matchType -> {
			List<MatchInput> matchInputs = new ArrayList<>();			
			if (matchType.isDynamic()) {
				if (authRequestDTO.getRequest().getDemographics() != null
						&& authRequestDTO.getRequest().getDemographics().getMetadata() != null) {
					for (Entry<String, Object> entry : authRequestDTO.getRequest().getDemographics().getMetadata()
							.entrySet()) {
						Optional<String> propNameOpt = getMappedPropertyName(entry.getKey(), matchType, authRequestDTO, languages);
						if (propNameOpt.isPresent() && matchType.isMultiLanguage(propNameOpt.get(), demoEntity, idInfoFetcher.getMappingConfig())) {
							validateDynamicAttributeLanguage(propNameOpt.get(), matchType, authRequestDTO,
									languages);
							for (String language : languages) {
								addMatchInput(authRequestDTO, authTypes, matchType, matchInputs, language);
							}
						} else {
							addMatchInput(authRequestDTO, authTypes, matchType, matchInputs, null);
						}
					}
				}
			} else {
				if (matchType.isMultiLanguage()) {
					for (String language : languages) {
						addMatchInput(authRequestDTO, authTypes, matchType, matchInputs, language);
					}
				} else {
					addMatchInput(authRequestDTO, authTypes, matchType, matchInputs, null);
				}

			}
			return matchInputs.stream();
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	/**
	 * Construct match input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param authTypes      the auth types
	 * @param matchTypes     the match types
	 * @return the list
	 */
	public List<MatchInput> buildMatchInput(AuthRequestDTO authRequestDTO, AuthType[] authTypes,
			MatchType[] matchTypes) {		
		return Stream.of(matchTypes).flatMap(matchType -> {
			List<MatchInput> matchInputs = new ArrayList<>();
			addMatchInput(authRequestDTO, authTypes, matchType, matchInputs, null);
			return matchInputs.stream();
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	/**
	 * Validates dynamic attribute language details
	 * @param propName
	 * @param matchType
	 * @param authRequestDTO
	 * @param supportedLanguages
	 */
	private void validateDynamicAttributeLanguage(String propName, MatchType matchType,
			AuthRequestDTO authRequestDTO, List<String> supportedLanguages) {		
		Map<String, List<IdentityInfoDTO>> identityInfosMap = idInfoFetcher.getIdentityInfo(matchType, propName,
				authRequestDTO.getRequest());
		for (List<IdentityInfoDTO> identityInfos : identityInfosMap.values()) {
			checkIdentityInfoLanguage(identityInfos, propName, supportedLanguages);
		}		
	}
	
	/**
	 *  Checks for identityInfoDto object will have value for language or not 
	 *  for a given dynamic attribute
	 * @param identityInfos
	 * @param propertyName
	 */
	private void checkIdentityInfoLanguage(List<IdentityInfoDTO> identityInfos, String propertyName, List<String> supportedLanguages) {
		for (IdentityInfoDTO identityInfoDTO : identityInfos) {
			if (Objects.isNull(identityInfoDTO.getLanguage())) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
								propertyName + " language"), "language cannot be null");
				throw new IdAuthUncheckedException(
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
								propertyName + ": language"));
			}
			if(identityInfoDTO.getLanguage().isEmpty() || identityInfoDTO.getLanguage().isBlank()) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
								propertyName + " language"), "language cannot be empty");
				throw new IdAuthUncheckedException(
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
								propertyName + ": language"));				
			}
			if(!supportedLanguages.contains(identityInfoDTO.getLanguage())){
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
								propertyName + " language"), "language cannot be null");
				throw new IdAuthUncheckedException(
						IdAuthenticationErrorConstants.UNSUPPORTED_LANGUAGE.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.UNSUPPORTED_LANGUAGE.getErrorMessage(),
								identityInfoDTO.getLanguage() + " for attribute " + propertyName));				
			}
		}
	}
	
	/**
	 * Gets mapped attribute name 
	 * @param matchType
	 * @param authRequestDTO
	 * @param languages 
	 * @return
	 */
	private Optional<String> getMappedPropertyName(String inAttribute, MatchType matchType, AuthRequestDTO authRequestDTO, List<String> languages) {
		return idInfoFetcher.getMappingConfig().getDynamicAttributes().keySet().stream()
				.filter(idName -> idName.equals(inAttribute))
				.filter(idName -> {
					if(languages != null && !languages.isEmpty()) {
						return languages.stream()
									.anyMatch(language -> idInfoFetcher
								.getIdentityRequestInfo(matchType, idName, authRequestDTO.getRequest(), language).size() > 0);
					} else {
						return idInfoFetcher
								.getIdentityRequestInfo(matchType, idName, authRequestDTO.getRequest(), null).size() > 0;
					}
				})
				.findFirst();
	}

	/**
	 * Add MatchInput
	 * 
	 * @param authRequestDTO
	 * @param authTypes
	 * @param matchType
	 * @param matchInputs
	 * @param language
	 */
	private void addMatchInput(AuthRequestDTO authRequestDTO, AuthType[] authTypes, MatchType matchType,
			List<MatchInput> matchInputs, String language) {
		Map<String, String> infoFromAuthRequest = matchType.getReqestInfoFunction().apply(authRequestDTO);
		Optional<AuthType> authTypeOpt = AuthType.getAuthTypeForMatchType(matchType, authTypes);
		if (authTypeOpt.isPresent()) {
			matchInputs
					.addAll(buildMatchInput(authRequestDTO, matchType, infoFromAuthRequest, authTypeOpt.get(), language));
		}
	}

	/**
	 * Build Match Input
	 * 
	 * @param authRequestDTO
	 * @param matchType
	 * @param infoFromAuthRequest
	 * @param authType
	 * @param language
	 * @return
	 */
	private List<MatchInput> buildMatchInput(AuthRequestDTO authRequestDTO, MatchType matchType,
			Map<String, String> infoFromAuthRequest, AuthType authType, String language) {
		if (infoFromAuthRequest.isEmpty()) {
			// For Identity
			Optional<RequestDTO> identityOpt = Optional.ofNullable(authRequestDTO.getRequest());
			if (identityOpt.isPresent()) {
				RequestDTO identity = identityOpt.get();
				if (authType.isAuthTypeEnabled(authRequestDTO, idInfoFetcher)) {
					IdMapping idMapping = matchType.getIdMapping();
					if(idMapping.equals(IdaIdMapping.DYNAMIC)) {
						Map<String, List<String>> dynamicAttributes = idInfoFetcher.getMappingConfig().getDynamicAttributes();
						return dynamicAttributes
								.keySet()
								.stream()
								.filter(idName -> 
						idInfoFetcher.getIdentityRequestInfo(matchType, idName, identity, language).size() > 0)
								.map(idName -> contstructMatchInput(authRequestDTO, idName, matchType, authType, language))
									.collect(Collectors.toList());
					} else {
						if(idInfoFetcher.getIdentityRequestInfo(matchType, identity, language).size() > 0) {
							return List.of(contstructMatchInput(authRequestDTO, matchType.getIdMapping().getIdname(), matchType, authType, language));
						}
					}
				}
			}
		} else {
			// For non-identity
			if (authType.isAuthTypeEnabled(authRequestDTO, idInfoFetcher)
					&& authType.isAuthTypeInfoAvailable(authRequestDTO)) {
				return List.of(contstructMatchInput(authRequestDTO, matchType.getIdMapping().getIdname(), matchType , authType, null));
			}
		}
		return List.of();
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
	private MatchInput contstructMatchInput(AuthRequestDTO authRequestDTO, String idName, MatchType matchType, AuthType authType,
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
							environment, idInfoFetcher);
					matchValue = matchThresholdOpt.orElseGet(() -> environment.getProperty(IdAuthConfigKeyConstants.DEFAULT_MATCH_VALUE, int.class, DEFAULT_PARTIAL_MATCH_VALUE));
				}
			}
			Map<String, Object> matchProperties = authType.getMatchProperties(authRequestDTO, idInfoFetcher, language);
			return new MatchInput(authType, idName, matchType, matchingStrategy, matchValue, matchProperties, language);
		}
	}
}
