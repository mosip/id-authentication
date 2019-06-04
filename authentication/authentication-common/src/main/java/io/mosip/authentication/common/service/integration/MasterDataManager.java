package io.mosip.authentication.common.service.integration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.indauth.dto.LanguageType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.kernel.core.logger.spi.Logger;

/*
 * MasterDataManager
 * 
 * @author Dinesh Karuppiah.T
 */
@Component
public class MasterDataManager {

	/** The Constant GENDER_NAME. */
	private static final String GENDER_NAME = "genderName";

	/** The Constant GENDER_TYPE. */
	private static final String GENDER_TYPE = "genderType";

	/** The Constant TITLE_NAME. */
	private static final String TITLE_NAME = "titleName";

	/** The Constant CODE. */
	private static final String CODE = "code";

	/** The Constant TITLE_LIST. */
	private static final String TITLE_LIST = "titleList";

	/** The Constant IS_ACTIVE. */
	private static final String IS_ACTIVE = "isActive";

	/** The Constant LANG_CODE. */
	private static final String LANG_CODE = "langCode";

	/** The Constant FILE_TEXT. */
	private static final String FILE_TEXT = "fileText";

	/** The Constant TEMPLATE_TYPE_CODE. */
	private static final String TEMPLATE_TYPE_CODE = "templateTypeCode";

	/** The Constant TEMPLATES. */
	private static final String TEMPLATES = "templates";

	/** The Constant NAME_PLACEHOLDER. */
	private static final String NAME_PLACEHOLDER = "$name";

	/**
	 * The Rest Helper
	 */
	@Autowired
	private RestHelper restHelper;

	/**
	 * The Rest request factory
	 */
	@Autowired
	private RestRequestFactory restFactory;

	@Autowired
	private IdInfoFetcher idInfoFetcher;

	/**
	 * IdTemplate Manager Logger
	 */
	private static Logger logger = IdaLogger.getLogger(MasterDataManager.class);

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, String>> fetchMasterData(RestServicesConstants type, Map<String, String> params,
			String masterDataListName, String keyAttribute, String valueAttribute)
			throws IdAuthenticationBusinessException {
		try {
			RestRequestDTO buildRequest = restFactory.buildRequest(type, null, Map.class);
			if (params != null && !params.isEmpty()) {
				buildRequest.setPathVariables(params);
			}
			Map<String, Object> response = restHelper.requestSync(buildRequest);

			Map<String, List<Map<String, Object>>> fetchResponse;
			if (response.get("response") instanceof Map) {
				fetchResponse = (Map<String, List<Map<String, Object>>>) response.get("response");
			} else {
				fetchResponse = Collections.emptyMap();
			}
			List<Map<String, Object>> masterDataList = fetchResponse.get(masterDataListName);
			Map<String, Map<String, String>> masterDataMap = new HashMap<>();
			for (Map<String, Object> map : masterDataList) {
				String langCode = String.valueOf(map.get(LANG_CODE));
				String key = String.valueOf(map.get(keyAttribute));
				String value = String.valueOf(map.get(valueAttribute));
				Object isActiveObj = map.get(IS_ACTIVE);
				if (isActiveObj instanceof Boolean && (Boolean) isActiveObj) {
					Map<String, String> valueMap = masterDataMap.computeIfAbsent(langCode,
							k -> new LinkedHashMap<String, String>());
					valueMap.put(key, value);
				}
			}

			return masterDataMap;
		} catch (IDDataValidationException | RestServiceException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), e.getErrorCode(),
					e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}
	}

	/**
	 * Fetch templates based on Language code and Template Name
	 * 
	 * @param langCode
	 * @param templateName
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	public String fetchTemplate(String langCode, String templateName) throws IdAuthenticationBusinessException {
		Map<String, String> params = new HashMap<>();
		params.put("langcode", langCode);
		params.put("templatetypecode", templateName);
		Map<String, Map<String, String>> masterData = fetchMasterData(
				RestServicesConstants.ID_MASTERDATA_TEMPLATE_SERVICE, params, TEMPLATES, TEMPLATE_TYPE_CODE, FILE_TEXT);
		return Optional.ofNullable(masterData.get(langCode)).map(map -> map.get(templateName)).orElse("");
	}

	/**
	 * To fetch template from master data manager
	 * 
	 * @param langCode
	 * @param templateName
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	public String fetchTemplate(String templateName) throws IdAuthenticationBusinessException {
		Map<String, String> params = new HashMap<>();
		String finalTemplate = "";
		StringBuilder template = new StringBuilder();
		params.put(CODE, templateName);
		Map<String, Map<String, String>> masterData = fetchMasterData(
				RestServicesConstants.ID_MASTERDATA_TEMPLATE_SERVICE_MULTILANG, params, TEMPLATES, TEMPLATE_TYPE_CODE,
				FILE_TEXT);
		for (Iterator<Entry<String, Map<String, String>>> iterator = masterData.entrySet().iterator(); iterator
				.hasNext();) {
			Entry<String, Map<String, String>> value = iterator.next();
			Map<String, String> valueMap = value.getValue();
			String lang = value.getKey();
			if (lang.equals(idInfoFetcher.getLanguageCode(LanguageType.PRIMARY_LANG))
					|| lang.equals(idInfoFetcher.getLanguageCode(LanguageType.SECONDARY_LANG))) {
				finalTemplate = (String) valueMap.get(templateName);
				finalTemplate = finalTemplate.replace(NAME_PLACEHOLDER, NAME_PLACEHOLDER + "_" + lang);
				template.append(finalTemplate);
			}
			if (iterator.hasNext()) {
				template.append("\n\n");
			}
		}

		return template.toString();

	}

	/**
	 * To fetch titles
	 * 
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	public Map<String, List<String>> fetchTitles() throws IdAuthenticationBusinessException {
		return fetchMasterdataList(RestServicesConstants.TITLE_SERVICE, TITLE_LIST, CODE, TITLE_NAME);
	}

	/**
	 * To fetch gender type
	 * 
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	public Map<String, List<String>> fetchGenderType() throws IdAuthenticationBusinessException {
		return fetchMasterdataList(RestServicesConstants.GENDER_TYPE_SERVICE, GENDER_TYPE, CODE, GENDER_NAME);
	}

	/**
	 * To fetch Master Data
	 * 
	 * @param type
	 * @param masterDataName
	 * @param keyAttribute
	 * @param valueAttribute
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private Map<String, List<String>> fetchMasterdataList(RestServicesConstants type, String masterDataName,
			String keyAttribute, String valueAttribute) throws IdAuthenticationBusinessException {
		Map<String, Map<String, String>> fetchMasterData = fetchMasterData(type, null, masterDataName, keyAttribute,
				valueAttribute);
		if (fetchMasterData != null && !fetchMasterData.isEmpty()) {
			return fetchMasterData.entrySet().stream()
					.collect(Collectors.toMap(Entry<String, Map<String, String>>::getKey,
							(Entry<String, Map<String, String>> entry) -> entry.getValue().values().stream()
									.collect(Collectors.toList())));
		}
		return Collections.emptyMap();
	}

}
