package io.mosip.authentication.common.service.integration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import io.mosip.kernel.core.util.CryptoUtil;

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
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private Environment environment;

	/**
	 * IdTemplate Manager Logger
	 */
	private static Logger logger = IdaLogger.getLogger(MasterDataManager.class);

	/**
	 * Fetch master data.
	 *
	 * @param type the type
	 * @param params the params
	 * @param masterDataListName the master data list name
	 * @param keyAttribute the key attribute
	 * @param valueAttribute the value attribute
	 * @return the map
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@SuppressWarnings("unchecked") Map<String, Map<String, String>> fetchMasterData(RestServicesConstants type, Map<String, String> params,
			String masterDataListName, String keyAttribute, String valueAttribute)
			throws IdAuthenticationBusinessException {
		try {
			Map<String, Object> response = getMasterDataInternal(type, params);

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
		} catch (IOException e) {
			// logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), e.getErrorCode(),
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), e.getClass().getName(),
					e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}
	}

	private Map<String, Object> getMasterDataInternal(RestServicesConstants type, Map<String, String> params)
			throws IDDataValidationException, RestServiceException, IOException {
		try {
			switch (type) {
				case GENDER_TYPE_SERVICE:
					return getMasterDataFromConfig("master.data.genders");
				case TITLE_SERVICE:
					return getMasterDataFromConfig("maste.data.titles");
				case ID_MASTERDATA_TEMPLATE_SERVICE:
					return getTemplates(params);
				case ID_MASTERDATA_TEMPLATE_SERVICE_MULTILANG:
					return getTemplates(params);
				default: {
					RestRequestDTO buildRequest = restFactory.buildRequest(type, null, Map.class);
					if (params != null && !params.isEmpty()) {
						buildRequest.setPathVariables(params);
					}
					Map<String, Object> response = restHelper.requestSync(buildRequest);
					return response;
				}
			}
		} catch (IOException e) {
			throw e;
		}
	}

	private Map<String, Object> getTemplates(Map<String, String> filterParams) throws IOException, JsonParseException, JsonMappingException {
		Map<String, Object> templateData = getMasterDataFromConfig("master.data.ida-templates");
		if(filterParams != null && !filterParams.isEmpty()) {
		//((List<Map<String, Object>>)((Map<String, Object>)templateData.get("response")).get("templates")).get(0)
			List<Map<String, Object>> filteredTempates = Optional.ofNullable(templateData.get("response"))
					.filter(res -> res instanceof Map)
					.map(res -> ((Map<String, Object>)res).get("templates"))
					.filter(obj -> obj instanceof List)
					.map(obj -> ((List<Map<String, Object>>) obj).stream())
					.orElse(Stream.empty())
					.filter(templateMap -> 
								filterParams.keySet()
											.stream()
											.filter(templateMap::containsKey)
											.allMatch(key -> templateMap.get(key).equals(filterParams.get(key))))
					.collect(Collectors.toCollection(() -> new ArrayList<>()));
			
			if(!filteredTempates.isEmpty()) {
				((Map<String, Object>)templateData.get("response")).put("templates", filteredTempates);
			}
		}
		
		return templateData;
	}

	private Map<String, Object> getMasterDataFromConfig(String masterDataProperty)
			throws IOException, JsonParseException, JsonMappingException {
		String encodedMasterData = environment.getProperty(masterDataProperty);
		Map<String, Object> templateData = mapper.readValue(CryptoUtil.decodeBase64(encodedMasterData), Map.class);
		return templateData;
	}

	/**
	 * Fetch templates based on Language code and Template Name.
	 *
	 * @param langCode the lang code
	 * @param templateName the template name
	 * @return the string
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public String fetchTemplate(String langCode, String templateName) throws IdAuthenticationBusinessException {
		Map<String, String> params = new HashMap<>();
//		params.put("langcode", langCode);
//		params.put("templatetypecode", templateName);
		params.put("langCode", langCode);
		params.put("templateTypeCode", templateName);
		Map<String, Map<String, String>> masterData = fetchMasterData(
				RestServicesConstants.ID_MASTERDATA_TEMPLATE_SERVICE, params, TEMPLATES, TEMPLATE_TYPE_CODE, FILE_TEXT);
		return Optional.ofNullable(masterData.get(langCode)).map(map -> map.get(templateName)).orElse("");
	}

	/**
	 * To fetch template from master data manager.
	 *
	 * @param templateName the template name
	 * @return the string
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public String fetchTemplate(String templateName) throws IdAuthenticationBusinessException {
		Map<String, String> params = new HashMap<>();
		String finalTemplate = "";
		StringBuilder template = new StringBuilder();
		params.put("templateTypeCode", templateName);
		Map<String, Map<String, String>> masterData = fetchMasterData(
				RestServicesConstants.ID_MASTERDATA_TEMPLATE_SERVICE_MULTILANG, params, TEMPLATES, TEMPLATE_TYPE_CODE,
				FILE_TEXT);
		// Sort the list of entries based on primary lang/secondary lang order. 
		// Here entry of primary lang should occur before secondary lang entry.
		List<Entry<String, Map<String, String>>> entries =
				masterData.entrySet()
					.stream()
					.sorted((o1,o2) -> {
						String lang1 = o1.getKey();
						String lang2 = o2.getKey();
						boolean lang1IsPrimary = lang1.equals(idInfoFetcher.getLanguageCode(LanguageType.PRIMARY_LANG));
						boolean lang2IsPrimary = lang2.equals(idInfoFetcher.getLanguageCode(LanguageType.PRIMARY_LANG));
						int val;
						if(lang1IsPrimary == lang2IsPrimary) {
							val = 0;
						} else {
							val = lang1IsPrimary ? -1 : 1;
						}
						return val;
					})
					.collect(Collectors.toList());
		for (Iterator<Entry<String, Map<String, String>>> iterator = entries.iterator(); iterator
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
	 * To fetch titles.
	 *
	 * @return the map
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public Map<String, List<String>> fetchTitles() throws IdAuthenticationBusinessException {
		return fetchMasterdataList(RestServicesConstants.TITLE_SERVICE, TITLE_LIST, CODE, TITLE_NAME);
	}

	/**
	 * To fetch gender type.
	 *
	 * @return the map
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public Map<String, List<String>> fetchGenderType() throws IdAuthenticationBusinessException {
		return fetchMasterdataList(RestServicesConstants.GENDER_TYPE_SERVICE, GENDER_TYPE, CODE, GENDER_NAME);
	}

	/**
	 * To fetch Master Data.
	 *
	 * @param type the type
	 * @param masterDataName the master data name
	 * @param keyAttribute the key attribute
	 * @param valueAttribute the value attribute
	 * @return the map
	 * @throws IdAuthenticationBusinessException the id authentication business exception
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
