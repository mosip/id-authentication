package io.mosip.authentication.common.service.integration;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.JsonPath;

import io.mosip.authentication.common.service.cache.MasterDataCache;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.LanguageComparator;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * MasterDataManager.
 *
 * @author Dinesh Karuppiah.T
 * @author Manoj SP
 * @author Nagarjuna
 */
@Component
public class MasterDataManager {

	/** The Constant TITLE_NAME_JSON_PATH. */
	private static final String TITLE_NAME_JSON_PATH = "$.response.titleList[?(@.langCode=='%s')].titleName";

	/** The Constant LANG_CODE_JSON_PATH. */
	private static final String LANG_CODE_JSON_PATH = "$.response.titleList.*.langCode";

	/** The Constant LANG_CODE. */
	private static final String LANG_CODE = "langCode";

	/** The Constant TEMPLATE_TYPE_CODE. */
	private static final String TEMPLATE_TYPE_CODE = "templateTypeCode";

	/** The Constant RESPONSE. */
	private static final String RESPONSE = "response";

	/** The Constant IS_ACTIVE. */
	private static final String IS_ACTIVE = "isActive";

	/** The Constant FILE_TEXT. */
	private static final String FILE_TEXT = "fileText";

	/** The Constant TEMPLATES. */
	private static final String TEMPLATES = "templates";

	/** The Constant NAME_PLACEHOLDER. */
	private static final String NAME_PLACEHOLDER = "$name";	

	/** IdTemplate Manager Logger. */
	private static Logger logger = IdaLogger.getLogger(MasterDataManager.class);

	/** The master data cache. */
	@Autowired
	private MasterDataCache masterDataCache;

	/**
	 * Fetch master data for provided languages.
	 *
	 * @param type               the type
	 * @param params             the params
	 * @param masterDataListName the master data list name
	 * @param keyAttribute       the key attribute
	 * @param valueAttribute     the value attribute
	 * @return the map
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@SuppressWarnings("unchecked")
	Map<String, Map<String, String>> fetchMasterData(RestServicesConstants type, Map<String, String> params,
			String masterDataListName, String keyAttribute, String valueAttribute)
			throws IdAuthenticationBusinessException {
		try {
			Map<String, Object> response = masterDataCache.getMasterDataTemplate(params.get(TEMPLATE_TYPE_CODE));

			Map<String, List<Map<String, Object>>> fetchResponse;
			if (response.get(RESPONSE) instanceof Map) {
				fetchResponse = (Map<String, List<Map<String, Object>>>) response.get(RESPONSE);
			} else {
				fetchResponse = Collections.emptyMap();
			}
			List<Map<String, Object>> masterDataList = fetchResponse.get(masterDataListName);
			Map<String, Map<String, String>> masterDataMap = new HashMap<>();
			for (Map<String, Object> map : masterDataList) {
				String langCode = String.valueOf(map.get(LANG_CODE));
				if (!params.containsKey(LANG_CODE)
						|| (params.containsKey(LANG_CODE) && langCode.contentEquals(params.get(LANG_CODE)))) {
					String key = String.valueOf(map.get(keyAttribute));
					String value = String.valueOf(map.get(valueAttribute));
					Object isActiveObj = map.get(IS_ACTIVE);
					if (isActiveObj instanceof Boolean && (Boolean) isActiveObj) {
						Map<String, String> valueMap = masterDataMap.computeIfAbsent(langCode,
								k -> new LinkedHashMap<String, String>());
						valueMap.put(key, value);
					}
				}
			}

			return masterDataMap;
		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), e.getErrorCode(),
					e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}
	}

	/**
	 * Fetch templates based on Language code and Template Name.
	 *
	 * @param langCode     the lang code
	 * @param templateName the template name
	 * @return the string
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public String fetchTemplate(String langCode, String templateName) throws IdAuthenticationBusinessException {
		Map<String, String> params = new HashMap<>();
		params.put(LANG_CODE, langCode);
		params.put(TEMPLATE_TYPE_CODE, templateName);
		Map<String, Map<String, String>> masterData = fetchMasterData(
				RestServicesConstants.ID_MASTERDATA_TEMPLATE_SERVICE, params, TEMPLATES, TEMPLATE_TYPE_CODE, FILE_TEXT);
		return Optional.ofNullable(masterData.get(langCode)).map(map -> map.get(templateName)).orElse("");
	}

	/**
	 * To fetch template from master data manager for all languages.
	 *
	 * @param templateName the template name
	 * @return the string
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public String fetchTemplate(String templateName, List<String> templateLanguages)
			throws IdAuthenticationBusinessException {
		Map<String, String> params = new HashMap<>();
		String finalTemplate = "";
		StringBuilder template = new StringBuilder();
		params.put(TEMPLATE_TYPE_CODE, templateName);
		Map<String, Map<String, String>> masterData = fetchMasterData(
				RestServicesConstants.ID_MASTERDATA_TEMPLATE_SERVICE_MULTILANG, params, TEMPLATES, TEMPLATE_TYPE_CODE,
				FILE_TEXT);

		// sorting the masterdata templates based on template languages
		List<String> masterDataTemplateKeys = masterData.keySet().stream().collect(Collectors.toList());
		Collections.sort(masterDataTemplateKeys, new LanguageComparator(templateLanguages));
		for (int i = 0; i < masterDataTemplateKeys.size(); i++) {
			String language = masterDataTemplateKeys.get(i);
			Map<String, String> value = masterData.get(language);
			if (templateLanguages.contains(language)) {
				finalTemplate = (String) value.get(templateName);
				if (finalTemplate != null) {
					finalTemplate = finalTemplate.replace(NAME_PLACEHOLDER, NAME_PLACEHOLDER + "_" + language);
					template.append(finalTemplate);
				} else {
					throw new IdAuthenticationBusinessException(
							IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
							IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage()
									+ " - template not found: " + templateName);
				}
			}
			if (i != (masterDataTemplateKeys.size() - 1)) {
				template.append("\n\n");
			}
		}

		return template.toString();
	}

	/**
	 * To fetch titles.
	 *
	 * @return the map
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<String>> fetchTitles() throws IdAuthenticationBusinessException {
		Map<String, Object> fetchMasterData = masterDataCache.getMasterDataTitles();
		List<String> langCodes = ((List<String>) JsonPath.compile(LANG_CODE_JSON_PATH).read(fetchMasterData));
		langCodes = langCodes.stream().collect(Collectors.toSet()).stream().collect(Collectors.toList());
		return langCodes.stream().map(langCode -> new AbstractMap.SimpleEntry<String, List<String>>(langCode,
				(List<String>) JsonPath.compile(String.format(TITLE_NAME_JSON_PATH, langCode)).read(fetchMasterData)))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

}