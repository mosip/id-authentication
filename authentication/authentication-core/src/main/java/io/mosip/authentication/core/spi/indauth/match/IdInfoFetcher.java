package io.mosip.authentication.core.spi.indauth.match;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PASSWORD;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SEMI_COLON;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.COLON;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.env.Environment;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.util.DemoMatcherUtil;
import io.mosip.authentication.core.util.DemoNormalizer;

/**
 * The IdInfoFetcher interface that provides the helper methods invoked by the
 * classes involved in ID Info matching.
 *
 * @author Loganathan.Sekar
 * @author Nagarjuna
 */
public interface IdInfoFetcher {	

	/**
	 * Gets the template default language codes
	 * @return
	 */
	public List<String> getTemplatesDefaultLanguageCodes();
	
	/**
	 * Gets the system supported languages.
	 * Combination of Mandatory and optional languages.
	 * @return
	 */
	public List<String> getSystemSupportedLanguageCodes();

	/**
	 * To check language type.
	 *
	 * @param languageForMatchType the language for match type
	 * @param languageFromReq the language from req
	 * @return true, if successful
	 */
	public boolean checkLanguageType(String languageForMatchType, String languageFromReq);

	/**
	 * Get language name for Match Properties based on language code.
	 *
	 * @param languageCode language code
	 * @return language name
	 */
	public Optional<String> getLanguageName(String languageCode);

	/**
	 * Gets the identity info for the MatchType from the IdentityDTO.
	 *
	 * @param matchType the match type
	 * @param idName 
	 * @param identity  the identity
	 * @param language the language
	 * @return the identity info
	 */
	public Map<String, String> getIdentityRequestInfo(MatchType matchType, String idName, RequestDTO identity, String language);
	
	/**
	 * Gets the identity info.
	 *
	 * @param matchType the match type
	 * @param idName the id name
	 * @param identity the identity
	 * @return the identity info
	 */
	public Map<String, List<IdentityInfoDTO>> getIdentityInfo(MatchType matchType, String idName, RequestDTO identity);
	
	/**
	 * Gets the identity info for the MatchType from the IdentityDTO.
	 *
	 * @param matchType the match type
	 * @param idName 
	 * @param identity  the identity
	 * @param language the language
	 * @return the identity info
	 */
	public Map<String, String> getIdentityRequestInfo(MatchType matchType, RequestDTO identity, String language);

	/**
	 * Get the Validate Otp function.
	 *
	 * @return the ValidateOtpFunction
	 */
	public ValidateOtpFunction getValidateOTPFunction();

	/**
	 * To fetch cbeff values.
	 *
	 * @param idEntity the id entity
	 * @param cbeffDocTypes the cbeff doc types
	 * @param matchType the match type
	 * @return the cbeff values
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public Map<String, Entry<String, List<IdentityInfoDTO>>> getCbeffValues(Map<String, List<IdentityInfoDTO>> idEntity,
			CbeffDocType[] cbeffDocTypes, MatchType matchType) throws IdAuthenticationBusinessException;

	/**
	 * To get EnvPropertyResolver.
	 *
	 * @return the environment
	 */
	public Environment getEnvironment();

	/**
	 * Title info fetcher from Master data manager.
	 *
	 * @return the title fetcher
	 */
	public MasterDataFetcher getTitleFetcher();

	/**
	 * Gets the matching threshold.
	 *
	 * @param key the key
	 * @return the matching threshold
	 */
	public Optional<Integer> getMatchingThreshold(String key);
	
	
	/**
	 * Gets the demo normalizer object to normalise the 
	 * corresponding(address/name) used for demographic authentication 
	 * .
	 *
	 * @return the demo normalizer
	 */
	public DemoNormalizer getDemoNormalizer();
	
	/**
	 * Gets the user preferred language attribute 
	 * @return
	 */
	public List<String> getUserPreferredLanguages(Map<String, List<IdentityInfoDTO>> idInfo);
	
	
	/**
	 * Gets the match function.
	 *
	 * @param authType the auth type
	 * @return the match function
	 */
	public TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double> getMatchFunction(AuthType authType);
	
	
	/**
	 * Gets the type for id name.
	 *
	 * @param idName the id name
	 * @param idMappings the id mappings
	 * @return the type for id name
	 */
	public Optional<String> getTypeForIdName(String idName, IdMapping[] idMappings);
	
	/**
	 * Gets the mapping config.
	 *
	 * @return the mapping config
	 */
	public MappingConfig getMappingConfig();
	
	/**
	 * 
	 * @return
	 */
	public DemoMatcherUtil getDemoMatcherUtil();


	/**
	 * Gets the available dynamic attributes names.
	 *
	 * @param request the request
	 * @return the available dynamic attributes names
	 */
	Set<String> getAvailableDynamicAttributesNames(RequestDTO request);
	
	/**
	 * Fetch data from Identity info value based on Identity response.
	 *
	 * @param idResponseDTO the id response DTO
	 * @return the id info
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, List<IdentityInfoDTO>> getIdInfo(Map<String, Object> idResponseDTO) {
		return idResponseDTO.entrySet().stream().flatMap(entry -> {
			if (entry.getValue() instanceof Map) {
				return ((Map<String, Object>) entry.getValue()).entrySet().stream();
			} else {
				return Stream.of(entry);
			}
		}).collect(Collectors.toMap(t -> t.getKey(), entry -> {
			Object val = entry.getValue();
			if (val instanceof List) {
				List<? extends Object> arrayList = (List) val;
				if (!arrayList.isEmpty()) {
					Object object = arrayList.get(0);
					if (object instanceof Map) {
						return arrayList.stream().filter(elem -> elem instanceof Map)
								.map(elem -> (Map<String, Object>) elem).map(map1 -> {
									String value = String.valueOf(map1.get("value"));
									IdentityInfoDTO idInfo = new IdentityInfoDTO();
									if (map1.containsKey("language")) {
										idInfo.setLanguage(String.valueOf(map1.get("language")));
									}
									idInfo.setValue(value);
									return idInfo;
								}).collect(Collectors.toList());

					} else if (object instanceof String) {
						return arrayList.stream().map(string -> {
							String value = (String) string;
							IdentityInfoDTO idInfo = new IdentityInfoDTO();
							idInfo.setValue(value);
							return idInfo;
						}).collect(Collectors.toList());
					}
				}
			} else if (val instanceof Boolean || val instanceof String || val instanceof Long || val instanceof Integer
					|| val instanceof Double || val instanceof Float) {
				IdentityInfoDTO idInfo = new IdentityInfoDTO();
				idInfo.setValue(String.valueOf(val));
				return Stream.of(idInfo).collect(Collectors.toList());
			} else if (entry.getKey().equals(PASSWORD) && val instanceof Map) {
				Map<String, String> map = (Map<String, String>) val;
				String passwordData = map.entrySet().stream()
										 .map(mapEntry -> mapEntry.getKey().trim() + String.valueOf(COLON) + mapEntry.getValue().trim())
										 .collect(Collectors.joining(SEMI_COLON));
				IdentityInfoDTO idInfo = new IdentityInfoDTO();
				idInfo.setValue(String.valueOf(passwordData));
				return Stream.of(idInfo).collect(Collectors.toList());
			}

			return Collections.emptyList();
		}));
	}

	/**
	 * To Get match Password function.
	 *
	 * @return the ComparePasswordFunction
	 */
	public ComparePasswordFunction getMatchPasswordFunction();
}
