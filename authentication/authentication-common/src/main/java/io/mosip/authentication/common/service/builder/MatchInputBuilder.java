package io.mosip.authentication.common.service.builder;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.ID_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MAPPING_CONFIG;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
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
 * Builder class to match Inputs.
 *
 * @author Dinesh Karuppiah.T
 */
@Component
public class MatchInputBuilder {

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(MatchInputBuilder.class);

	/** The id info fetcher. */
	@Autowired
	private IdInfoFetcher idInfoFetcher;
	
	/** The environment. */
	@Autowired
	private EnvUtil environment;

	/** The Constant DEFAULT_MATCH_VALUE. */
	public static final String DEFAULT_MATCH_VALUE = "demo.threshold";

	/**
	 * Construct match input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param authTypes      the auth types
	 * @param matchTypes     the match types
	 * @param demoEntity the demo entity
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
						String propName = getMappedPropertyName(entry.getKey(), matchType, authRequestDTO, languages).orElseGet(entry::getKey);
						if (matchType.isMultiLanguage(propName, demoEntity, idInfoFetcher.getMappingConfig())) {
							validateDynamicAttributeLanguage(propName, matchType, authRequestDTO,
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
		})
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
	}
	
	/**
	 * Validates dynamic attribute language details.
	 *
	 * @param propName the prop name
	 * @param matchType the match type
	 * @param authRequestDTO the auth request DTO
	 * @param supportedLanguages the supported languages
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
	 *  for a given dynamic attribute.
	 *
	 * @param identityInfos the identity infos
	 * @param propertyName the property name
	 * @param supportedLanguages the supported languages
	 */
    private void checkIdentityInfoLanguage(List<IdentityInfoDTO> identityInfos, String propertyName, List<String> supportedLanguages) {
        Set<String> languageSet = new HashSet<>(supportedLanguages);

        for (IdentityInfoDTO identityInfoDTO : identityInfos) {
            String lang = identityInfoDTO.getLanguage();

            if (lang == null || lang.isBlank()) {
                mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                        String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
                                propertyName + " language"), "language cannot be null or empty or blank");
                throw new IdAuthUncheckedException(
                        IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
                        String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
                                propertyName + ": language"));
            }

            if (!languageSet.contains(identityInfoDTO.getLanguage())) {
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
	 * Gets mapped attribute name .
	 *
	 * @param inAttribute the in attribute
	 * @param matchType the match type
	 * @param authRequestDTO the auth request DTO
	 * @param languages the languages
	 * @return the mapped property name
	 */
    private Optional<String> getMappedPropertyName(String inAttribute, MatchType matchType, AuthRequestDTO authRequestDTO, List<String> languages) {
        Set<String> dynamicAttributes = idInfoFetcher.getMappingConfig().getDynamicAttributes().keySet();
        if (!dynamicAttributes.contains(inAttribute)) {
            return Optional.empty();
        }
        if (languages != null && !languages.isEmpty()) {
            for (String language : languages) {
                if (!idInfoFetcher.getIdentityRequestInfo(matchType, inAttribute,
                        authRequestDTO.getRequest(), language).isEmpty()) {
                    return Optional.of(inAttribute);
                }
            }
        } else {
            if (!idInfoFetcher.getIdentityRequestInfo(matchType, inAttribute,
                    authRequestDTO.getRequest(), null).isEmpty()) {
                return Optional.of(inAttribute);
            }
        }

        return Optional.empty();
    }

	/**
	 * Add MatchInput.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param authTypes the auth types
	 * @param matchType the match type
	 * @param matchInputs the match inputs
	 * @param language the language
	 */
    private void addMatchInput(AuthRequestDTO authRequestDTO, AuthType[] authTypes, MatchType matchType,
                               List<MatchInput> matchInputs, String language) {
        Map<String, String> infoFromAuthRequest = matchType.getReqestInfoFunction().apply(authRequestDTO);
        Optional<AuthType> authTypeOpt = AuthType.getAuthTypeForMatchType(matchType, authTypes);
        authTypeOpt.ifPresent(authType -> matchInputs
                .addAll(buildMatchInput(authRequestDTO, matchType, infoFromAuthRequest, authType, language)));
    }

	/**
	 * Build Match Input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param matchType the match type
	 * @param infoFromAuthRequest the info from auth request
	 * @param authType the auth type
	 * @param language the language
	 * @return the list
	 */
    private List<MatchInput> buildMatchInput(AuthRequestDTO authRequestDTO, MatchType matchType,
                                             Map<String, String> infoFromAuthRequest, AuthType authType, String language) {

        // For Identity
        Optional<RequestDTO> identityOpt = Optional.ofNullable(authRequestDTO.getRequest());
        if (infoFromAuthRequest == null || infoFromAuthRequest.isEmpty()) {
            RequestDTO identity = identityOpt.get();
            if (authType.isAuthTypeEnabled(authRequestDTO, idInfoFetcher)) {
                IdMapping idMapping = matchType.getIdMapping();
                if (IdaIdMapping.DYNAMIC.equals(idMapping)) {
                    Map<String, List<String>> mappedDynamicAttributes = idInfoFetcher.getMappingConfig().getDynamicAttributes();
                    List<MatchInput> matchInputs = new ArrayList<>();

                    mappedDynamicAttributes.keySet().forEach(idName -> {
                        if (!idInfoFetcher.getIdentityRequestInfo(matchType, idName, identity, language).isEmpty()) {
                            MatchInput input = contstructMatchInput(authRequestDTO, idName, matchType, authType, language);
                            if (input != null) matchInputs.add(input);
                        }
                    });

                    Map<String, Object> metadata = identity.getDemographics() != null ? identity.getDemographics().getMetadata() : null;
                    if (metadata != null && !metadata.isEmpty()) {
                        List<MatchInput> unmapped = getMatchInputsForUnmappedDynamicAttributes(
                                metadata,
                                mappedDynamicAttributes,
                                language,
                                authRequestDTO,
                                matchType,
                                authType,
                                identity
                        );
                        if (!unmapped.isEmpty()) {
                            matchInputs.addAll(unmapped);
                        }
                    }
                    return matchInputs;
                } else {
                    if (!idInfoFetcher.getIdentityRequestInfo(matchType, identity, language).isEmpty()) {
                        MatchInput matchInput = contstructMatchInput(authRequestDTO, idMapping.getIdname(), matchType, authType, language);
                        return matchInput != null ? Collections.singletonList(matchInput) : Collections.emptyList();
                    }
                }
            }
        } else if (authType.isAuthTypeEnabled(authRequestDTO, idInfoFetcher) && authType.isAuthTypeInfoAvailable(authRequestDTO)) {
            MatchInput matchInput = contstructMatchInput(authRequestDTO, matchType.getIdMapping().getIdname(), matchType, authType, null);
            return matchInput != null ? Collections.singletonList(matchInput) : Collections.emptyList();
        }
        return Collections.emptyList();
    }

	/**
	 * Gets the match inputs for unmapped dynamic attributes.
	 *
	 * @param metadata the metadata
	 * @param mappedDynamicAttributes the mapped dynamic attributes
	 * @param language the language
	 * @param authRequestDTO the auth request DTO
	 * @param matchType the match type
	 * @param authType the auth type
	 * @param identity 
	 * @return the match inputs for unmapped dynamic attributes
	 */
    private List<MatchInput> getMatchInputsForUnmappedDynamicAttributes(Map<String, Object> metadata,
                                                                        Map<String, List<String>> mappedDynamicAttributes, String language, AuthRequestDTO authRequestDTO, MatchType matchType, AuthType authType, RequestDTO identity) {
        if (metadata == null || metadata.isEmpty()) {
            return Collections.emptyList();
        }
        List<MatchInput> matchInputs = new ArrayList<>(metadata.size());

        for (String demoAttrib : metadata.keySet()) {
            // Skip already mapped attributes
            if (mappedDynamicAttributes.containsKey(demoAttrib)) {
                continue;
            }

            // Retrieve identity info for the attribute
            if (!idInfoFetcher.getIdentityRequestInfo(matchType, demoAttrib, identity, language).isEmpty()) {
                MatchInput input = contstructMatchInput(authRequestDTO, demoAttrib, matchType, authType, language);
                if (input != null) {
                    matchInputs.add(input);
                }
            }
        }
        return matchInputs;
    }


	/**
	 * Construct match input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param idName the id name
	 * @param matchType      TODO
	 * @param authType       TODO
	 * @param language       the language
	 * @return the list
	 */
    private MatchInput contstructMatchInput(AuthRequestDTO authRequestDTO, String idName, MatchType matchType, AuthType authType,
                                            String language) {

        if (matchType.getCategory() == Category.BIO && !authType.isAuthTypeInfoAvailable(authRequestDTO)) {
            return null;
        }

        Integer matchValue = IdAuthCommonConstants.DEFAULT_EXACT_MATCH_VALUE;
        String matchingStrategy = MatchingStrategyType.DEFAULT_MATCHING_STRATEGY.getType();

        Optional<String> matchingStrategyOpt = authType.getMatchingStrategy(authRequestDTO, language);
        if (matchingStrategyOpt.isPresent()) {
            String strategy = matchingStrategyOpt.get();
            matchingStrategy = strategy;

            if (MatchingStrategyType.PARTIAL.getType().equals(strategy) ||
                    MatchingStrategyType.PHONETICS.getType().equals(strategy)) {
                matchValue = authType.getMatchingThreshold(authRequestDTO, language,
                                environment.getEnvironment(), idInfoFetcher)
                        .orElseGet(EnvUtil::getDefaultMatchValue);
            }
        }
        Map<String, Object> matchProperties = new HashMap<>(authType.getMatchProperties(authRequestDTO, idInfoFetcher, language));
        matchProperties.put(ID_NAME, idName);
        matchProperties.put(MAPPING_CONFIG, idInfoFetcher.getMappingConfig());

        return new MatchInput(authType, idName, matchType, matchingStrategy, matchValue, matchProperties, language);
    }
}
