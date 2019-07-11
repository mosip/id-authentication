package io.mosip.kernel.idobjectvalidator.impl;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.SetValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorDocumentMapping;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorLocationMapping;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.idobjectvalidator.constant.IdObjectValidatorConstant;
import net.minidev.json.JSONArray;

/**
 * The Class IdObjectMasterDataValidator.
 *
 * @author Manoj SP
 */
@Component("masterdata")
@Lazy
@RefreshScope
public class IdObjectMasterDataValidator implements IdObjectValidator {
	
	private static final String LOCATIONS = "locations";

	private static final String LOCATION_HIERARCHY_NAME = "locationHierarchyName";

	private static final String LOCATION_HIERARCHYLEVEL = "locationHierarchylevel";

	private static final String NAME = "name";

	private static final String DOCUMENTS = "documents";

	private static final String DOCUMENTCATEGORIES = "documentcategories";

	private static final String LANG_CODE = "langCode";

	private static final String GENDER_TYPE = "genderType";

	private static final String CODE = "code";

	private static final String IS_ACTIVE = "isActive";

	@Autowired
	private Environment env;
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;
	
	/** The Constant READ_OPTIONS. */
	private static final Configuration READ_OPTIONS = Configuration.defaultConfiguration()
			.addOptions(Option.SUPPRESS_EXCEPTIONS);
	
	/** The Constant READ_LIST_OPTIONS. */
	private static final Configuration READ_LIST_OPTIONS = Configuration.defaultConfiguration()
			.addOptions(Option.SUPPRESS_EXCEPTIONS, Option.ALWAYS_RETURN_LIST);
	
	/** The Constant PATH_LIST_OPTIONS. */
	private static final Configuration PATH_LIST_OPTIONS = Configuration.defaultConfiguration()
			.addOptions(Option.SUPPRESS_EXCEPTIONS, Option.AS_PATH_LIST, Option.ALWAYS_RETURN_LIST);

	/** The rest template. */
	@Autowired
	private RestTemplate restTemplate;
	
	/** The language list. */
	private List<String> languageList;
	
	/** The gender map. */
	private SetValuedMap<String, String> genderMap;
	
	/** The doc cat map. */
	private SetValuedMap<String, String> docCatMap;
	
	/** The doc type map. */
	private SetValuedMap<String, String> docTypeMap;
	
	/** The location hierarchy details. */
	private SetValuedMap<String, String> locationHierarchyDetails;
	
	/** The location details. */
	private Map<String, SetValuedMap<String, String>> locationDetails;
	
	/**
	 * Load data.
	 * @throws IdObjectIOException 
	 */
	@PostConstruct
	public void loadData() {
		mapper.registerModule(new Jdk8Module())
		   .registerModule(new JavaTimeModule()); 
		loadLanguages();
		loadGenderTypes();
		loadLocationDetails();
		loadDocCategories();
		loadDocTypes();
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator#validateIdObject(java.lang.Object)
	 */
	@Override
	public boolean validateIdObject(Object identityObject, IdObjectValidatorSupportedOperations operation)
			throws IdObjectIOException, IdObjectValidationFailedException {
		try {
			String identityString = mapper.writeValueAsString(identityObject);
			List<ServiceError> errorList = new ArrayList<>();
			validateLanguage(identityString, errorList);
			validateGender(identityString, errorList);
			validateRegion(identityString, errorList);
			validateProvince(identityString, errorList);
			validateCity(identityString, errorList);
			validatePostalCode(identityString, errorList);
			validateLocalAdministrativeAuthority(identityString, errorList);
			validateDocuments(identityString, errorList);
			if (errorList.isEmpty()) {
				return true;
			} else {
				throw new IdObjectValidationFailedException(
						IdObjectValidatorErrorConstant.ID_OBJECT_VALIDATION_FAILED, errorList);
			}
		} catch (JsonProcessingException e) {
			throw new IdObjectIOException(IdObjectValidatorErrorConstant.ID_OBJECT_PARSING_FAILED, e);
		}
	}
	
	/**
	 * Load languages.
	 */
	@SuppressWarnings("unchecked")
	private void loadLanguages() {
		ObjectNode responseBody = restTemplate.getForObject(
				env.getProperty(IdObjectValidatorConstant.MASTERDATA_LANGUAGE_URI.getValue()), ObjectNode.class);
		JsonPath jsonPath = JsonPath.compile(IdObjectValidatorConstant.MASTERDATA_LANGUAGE_PATH.getValue());
		JSONArray response = jsonPath.read(responseBody.toString(), READ_LIST_OPTIONS);
		languageList = Optional
				.ofNullable(response)
				.filter(data -> !data.isEmpty())
				.orElse(new JSONArray())
				.stream()
				.map(obj -> ((LinkedHashMap<String, Object>) obj))
				.filter(obj -> (Boolean) obj.get(IS_ACTIVE))
				.map(obj -> String.valueOf(obj.get(CODE)))
				.collect(Collectors.toList());
	}

	/**
	 * Load gender types.
	 */
	@SuppressWarnings("unchecked")
	private void loadGenderTypes() {
		ResponseWrapper<LinkedHashMap<String, ArrayList<LinkedHashMap<String, Object>>>> responseBody = restTemplate
				.getForObject(env.getProperty(IdObjectValidatorConstant.MASTERDATA_GENDERTYPES_URI.getValue()),
						ResponseWrapper.class);
		if (Objects.isNull(responseBody.getErrors()) || responseBody.getErrors().isEmpty()) {
			ArrayList<LinkedHashMap<String, Object>> response = responseBody.getResponse().get(GENDER_TYPE);
			genderMap = new HashSetValuedHashMap<>(response.size());
			IntStream.range(0, response.size())
					.filter(index -> (Boolean) response.get(index).get(IS_ACTIVE))
					.forEach(index -> 
					{
						genderMap.put(String.valueOf(response.get(index).get(LANG_CODE)),
							String.valueOf(response.get(index).get(CODE)));
						genderMap.put(String.valueOf(response.get(index).get(LANG_CODE)),
								String.valueOf(response.get(index).get("genderName")));
					});
		}
	}
	
	/**
	 * Load doc categories.
	 */
	@SuppressWarnings("unchecked")
	private void loadDocCategories() {
		ResponseWrapper<LinkedHashMap<String, ArrayList<LinkedHashMap<String, Object>>>> responseBody = restTemplate
				.getForObject(env.getProperty(IdObjectValidatorConstant.MASTERDATA_DOCUMENT_CATEGORIES_URI.getValue()),
						ResponseWrapper.class);
		if (Objects.isNull(responseBody.getErrors()) || responseBody.getErrors().isEmpty()) {
			ArrayList<LinkedHashMap<String, Object>> response = responseBody.getResponse().get(DOCUMENTCATEGORIES);
			docCatMap = new HashSetValuedHashMap<>(response.size());
			IntStream.range(0, response.size())
					.filter(index -> (Boolean) response.get(index).get(IS_ACTIVE))
					.forEach(index -> 
						docCatMap.put(String.valueOf(response.get(index).get(LANG_CODE)),
							String.valueOf(response.get(index).get(CODE))));
		}
	}
	
	/**
	 * Load doc types.
	 */
	@SuppressWarnings("unchecked")
	private void loadDocTypes() {
		docTypeMap = new HashSetValuedHashMap<>();
		if (Objects.nonNull(docCatMap) && !docCatMap.isEmpty()) {
			docCatMap.keySet().stream().forEach(langCode ->
			docCatMap.get(langCode).stream().forEach(docCat -> {
				String uri = UriComponentsBuilder
						.fromUriString(env.getProperty(IdObjectValidatorConstant.MASTERDATA_DOCUMENT_TYPES_URI.getValue()))
						.buildAndExpand(docCat, langCode).toUriString();
					ResponseWrapper<LinkedHashMap<String, ArrayList<LinkedHashMap<String, Object>>>> responseBody = restTemplate
							.getForObject(uri, ResponseWrapper.class);
					if (Objects.isNull(responseBody.getErrors()) || responseBody.getErrors().isEmpty()) {
						ArrayList<LinkedHashMap<String, Object>> response = responseBody.getResponse().get(DOCUMENTS);
						IntStream.range(0, response.size())
							.filter(index -> (Boolean) response.get(index).get(IS_ACTIVE))
							.forEach(index -> {
								docTypeMap.put(docCat, String.valueOf(response.get(index).get(NAME)));
								docTypeMap.put(docCat, String.valueOf(response.get(index).get("code")));
							});
					}
				})
			);
		}
	}
	
	/**
	 * Load location details.
	 */
	@SuppressWarnings({ "unchecked" })
	private void loadLocationDetails() {
		locationHierarchyDetails = new HashSetValuedHashMap<>();
		locationDetails = new LinkedHashMap<>();
		languageList.stream().forEach(langCode -> {
			String uri = UriComponentsBuilder
					.fromUriString(env.getProperty(IdObjectValidatorConstant.MASTERDATA_LOCATIONS_URI.getValue()))
					.buildAndExpand(langCode).toUriString();
			ResponseWrapper<ObjectNode> responseBody = restTemplate
					.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<ResponseWrapper<ObjectNode>>() {
					}).getBody();
			if (Objects.isNull(responseBody.getErrors()) || responseBody.getErrors().isEmpty()) {
				JsonPath jsonPath = JsonPath.compile(IdObjectValidatorConstant.MASTERDATA_LOCATIONS_PATH.getValue());
				JSONArray response = jsonPath.read(responseBody.getResponse().toString(), READ_LIST_OPTIONS);
				response.stream()
					.map(obj -> ((LinkedHashMap<String, Object>) obj))
					.filter(obj -> (Boolean) obj.get(IS_ACTIVE))
					.forEach(obj -> {
							locationHierarchyDetails.put(String.valueOf(obj.get(LOCATION_HIERARCHYLEVEL)),
									String.valueOf(obj.get(LOCATION_HIERARCHY_NAME)));
						locationDetails.put(String.valueOf(obj.get(LOCATION_HIERARCHY_NAME)), null);
					});
			}
		});
		
		Set<String> locationHierarchyNames = locationDetails.keySet().stream().collect(Collectors.toSet());
		locationHierarchyNames.stream().forEach(hierarchyName -> {
			String uri = UriComponentsBuilder
					.fromUriString(
							env.getProperty(IdObjectValidatorConstant.MASTERDATA_LOCATION_HIERARCHY_URI.getValue()))
					.buildAndExpand(hierarchyName).toUriString();
			ResponseWrapper<LinkedHashMap<String, ArrayList<LinkedHashMap<String, Object>>>> responseBody = restTemplate
					.getForObject(uri, ResponseWrapper.class);
			if (Objects.isNull(responseBody.getErrors()) || responseBody.getErrors().isEmpty()) {
				ArrayList<LinkedHashMap<String, Object>> response = responseBody.getResponse().get(LOCATIONS);
				SetValuedMap<String, String> locations = new HashSetValuedHashMap<>(response.size());
				IntStream.range(0, response.size())
				.filter(index -> (Boolean) response.get(index).get(IS_ACTIVE))
				.forEach(index -> {
					locations.put(String.valueOf(response.get(index).get(LANG_CODE)),
						String.valueOf(response.get(index).get(NAME)));
					locations.put(String.valueOf(response.get(index).get(LANG_CODE)),
							String.valueOf(response.get(index).get("code")));
				});
				locationDetails.put(hierarchyName, locations);
			}
		});
	}

	/**
	 * Validate language.
	 *
	 * @param identityString the identity string
	 * @param errorList the error list
	 */
	private void validateLanguage(String identityString, List<ServiceError> errorList) {
		JsonPath jsonPath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_LANGUAGE_PATH.getValue());
		JSONArray pathList = jsonPath.read(identityString, PATH_LIST_OPTIONS);
		Map<String, String> dataMap = IntStream.range(0, pathList.size())
				.boxed()
				
				.collect(
						Collectors.toMap(i -> String.valueOf(pathList.get(i)), i -> JsonPath
								.compile(String.valueOf(pathList.get(i))).read(identityString, READ_OPTIONS)));
		dataMap.entrySet().stream().filter(entry -> !languageList.contains(entry.getValue()))
				.forEach(entry -> errorList
						.add(new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
								String.format(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
										convertToPath(entry.getKey())))));
	}

	/**
	 * Validate gender.
	 *
	 * @param identityString the identity string
	 * @param errorList the error list
	 */
	private void validateGender(String identityString, List<ServiceError> errorList) {
		JsonPath genderLangPath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_GENDER_LANGUAGE_PATH.getValue());
		List<String> genderLangPathList = genderLangPath.read(identityString, PATH_LIST_OPTIONS);
		JsonPath genderValuePath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_GENDER_VALUE_PATH.getValue());
		List<String> genderValuePathList = genderValuePath.read(identityString, PATH_LIST_OPTIONS);
		Map<String, Map.Entry<String, String>> dataMap = IntStream.range(0, genderLangPathList.size())
			.filter(index -> languageList
					.contains(JsonPath.compile(genderLangPathList.get(index)).read(identityString, READ_OPTIONS)))
			.boxed()
			.collect(Collectors.toMap(genderLangPathList::get,
					i -> new AbstractMap.SimpleImmutableEntry<String, String>(genderValuePathList.get(i),
							JsonPath.compile(genderValuePathList.get(i)).read(identityString, READ_OPTIONS))));
		dataMap.entrySet().stream()
			.filter(entry -> {
				String lang = JsonPath.compile(entry.getKey()).read(identityString, READ_OPTIONS);
				return genderMap.containsKey(lang) && !genderMap.get(lang).contains(entry.getValue().getValue());
			})
			.forEach(entry -> errorList
					.add(new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
									convertToPath(entry.getValue().getKey())))));
	}
	
	/**
	 * Validate region.
	 *
	 * @param identityString the identity string
	 * @param errorList the error list
	 */
	private void validateRegion(String identityString, List<ServiceError> errorList) {
		SetValuedMap<String, String> regionMap = new HashSetValuedHashMap<>();
		Set<String> regionNameList = locationHierarchyDetails.get(IdObjectValidatorLocationMapping.REGION.getLevel());
		Optional.ofNullable(regionNameList).orElse(Collections.emptySet()).stream()
				.forEach(hierarchyName -> Optional.ofNullable(locationDetails.get(hierarchyName))
						.ifPresent(regionMap::putAll));
		JsonPath langPath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_REGION_LANGUAGE_PATH.getValue());
		List<String> langPathList = langPath.read(identityString, PATH_LIST_OPTIONS);
		JsonPath valuePath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_REGION_VALUE_PATH.getValue());
		List<String> valuePathList = valuePath.read(identityString, PATH_LIST_OPTIONS);
		Map<String, Map.Entry<String, String>> dataMap = IntStream.range(0, langPathList.size())
			.filter(index -> languageList
					.contains(JsonPath.compile(langPathList.get(index)).read(identityString, READ_OPTIONS)))
			.boxed()
			.collect(Collectors.toMap(langPathList::get,
					i -> new AbstractMap.SimpleImmutableEntry<String, String>(valuePathList.get(i),
							JsonPath.compile(valuePathList.get(i)).read(identityString, READ_OPTIONS))));
		dataMap.entrySet().stream()
			.filter(entry -> {
				String lang = JsonPath.compile(entry.getKey()).read(identityString, READ_OPTIONS);
				return regionMap.containsKey(lang) && !regionMap.get(lang).contains(entry.getValue().getValue());
			})
			.forEach(entry -> errorList
					.add(new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
									convertToPath(entry.getValue().getKey())))));
	}
	
	/**
	 * Validate province.
	 *
	 * @param identityString the identity string
	 * @param errorList the error list
	 */
	private void validateProvince(String identityString, List<ServiceError> errorList) {
		SetValuedMap<String, String> provinceMap = new HashSetValuedHashMap<>();
		Set<String> provinceNameList = locationHierarchyDetails
				.get(IdObjectValidatorLocationMapping.PROVINCE.getLevel());
		Optional.ofNullable(provinceNameList).orElse(Collections.emptySet()).stream()
				.forEach(hierarchyName -> Optional.ofNullable(locationDetails.get(hierarchyName))
						.ifPresent(provinceMap::putAll));
		JsonPath langPath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_PROVINCE_LANGUAGE_PATH.getValue());
		List<String> langPathList = langPath.read(identityString, PATH_LIST_OPTIONS);
		JsonPath valuePath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_PROVINCE_VALUE_PATH.getValue());
		List<String> valuePathList = valuePath.read(identityString, PATH_LIST_OPTIONS);
		Map<String, Map.Entry<String, String>> dataMap = IntStream.range(0, langPathList.size())
			.filter(index -> languageList
					.contains(JsonPath.compile(langPathList.get(index)).read(identityString, READ_OPTIONS)))
			.boxed()
			.collect(Collectors.toMap(langPathList::get,
					i -> new AbstractMap.SimpleImmutableEntry<String, String>(valuePathList.get(i),
							JsonPath.compile(valuePathList.get(i)).read(identityString, READ_OPTIONS))));
		dataMap.entrySet().stream()
			.filter(entry -> {
				String lang = JsonPath.compile(entry.getKey()).read(identityString, READ_OPTIONS);
				return provinceMap.containsKey(lang) && !provinceMap.get(lang).contains(entry.getValue().getValue());
			})
			.forEach(entry -> errorList
					.add(new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
									convertToPath(entry.getValue().getKey())))));
	}
	
	/**
	 * Validate city.
	 *
	 * @param identityString the identity string
	 * @param errorList the error list
	 */
	private void validateCity(String identityString, List<ServiceError> errorList) {
		SetValuedMap<String, String> cityMap = new HashSetValuedHashMap<>();
		Set<String> cityNameList = locationHierarchyDetails.get(IdObjectValidatorLocationMapping.CITY.getLevel());
		Optional.ofNullable(cityNameList).orElse(Collections.emptySet()).stream()
				.forEach(hierarchyName -> Optional.ofNullable(locationDetails.get(hierarchyName))
						.ifPresent(cityMap::putAll));
		JsonPath langPath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_CITY_LANGUAGE_PATH.getValue());
		List<String> langPathList = langPath.read(identityString, PATH_LIST_OPTIONS);
		JsonPath valuePath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_CITY_VALUE_PATH.getValue());
		List<String> valuePathList = valuePath.read(identityString, PATH_LIST_OPTIONS);
		Map<String, Map.Entry<String, String>> dataMap = IntStream.range(0, langPathList.size())
			.filter(index -> languageList
					.contains(JsonPath.compile(langPathList.get(index)).read(identityString, READ_OPTIONS)))
			.boxed()
			.collect(Collectors.toMap(langPathList::get,
					i -> new AbstractMap.SimpleImmutableEntry<String, String>(valuePathList.get(i),
							JsonPath.compile(valuePathList.get(i)).read(identityString, READ_OPTIONS))));
		dataMap.entrySet().stream()
			.filter(entry -> {
				String lang = JsonPath.compile(entry.getKey()).read(identityString, READ_OPTIONS);
				return cityMap.containsKey(lang) && !cityMap.get(lang).contains(entry.getValue().getValue());
			})
			.forEach(entry -> errorList
					.add(new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
									convertToPath(entry.getValue().getKey())))));
	}
	
	/**
	 * Validate local administrative authority.
	 *
	 * @param identityString the identity string
	 * @param errorList the error list
	 */
	private void validateLocalAdministrativeAuthority(String identityString, List<ServiceError> errorList) {
		SetValuedMap<String, String> localAdministrativeAuthorityMap = new HashSetValuedHashMap<>();
		Set<String> localAdminAuthNameList = locationHierarchyDetails
				.get(IdObjectValidatorLocationMapping.LOCAL_ADMINISTRATIVE_AUTHORITY.getLevel());
		Optional.ofNullable(localAdminAuthNameList).orElse(Collections.emptySet()).stream()
				.forEach(hierarchyName -> Optional.ofNullable(locationDetails.get(hierarchyName))
						.ifPresent(localAdministrativeAuthorityMap::putAll));
		JsonPath langPath = JsonPath
				.compile(IdObjectValidatorConstant.IDENTITY_LOCALADMINISTRATIVEAUTHORITY_LANGUAGE_PATH.getValue());
		List<String> langPathList = langPath.read(identityString, PATH_LIST_OPTIONS);
		JsonPath valuePath = JsonPath
				.compile(IdObjectValidatorConstant.IDENTITY_LOCALADMINISTRATIVEAUTHORITY_VALUE_PATH.getValue());
		List<String> valuePathList = valuePath.read(identityString, PATH_LIST_OPTIONS);
		Map<String, Map.Entry<String, String>> dataMap = IntStream.range(0, langPathList.size())
			.filter(index -> languageList
					.contains(JsonPath.compile(langPathList.get(index)).read(identityString, READ_OPTIONS)))
			.boxed()
			.collect(Collectors.toMap(langPathList::get,
					i -> new AbstractMap.SimpleImmutableEntry<String, String>(valuePathList.get(i),
							JsonPath.compile(valuePathList.get(i)).read(identityString, READ_OPTIONS))));
		dataMap.entrySet().stream()
			.filter(entry -> {
				String lang = JsonPath.compile(entry.getKey()).read(identityString, READ_OPTIONS);
					return localAdministrativeAuthorityMap.containsKey(lang)
							&& !localAdministrativeAuthorityMap.get(lang).contains(entry.getValue().getValue());
			})
			.forEach(entry -> errorList
					.add(new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
									convertToPath(entry.getValue().getKey())))));
	}
	
	/**
	 * Validate postal code.
	 *
	 * @param identityString the identity string
	 * @param errorList the error list
	 */
	private void validatePostalCode(String identityString, List<ServiceError> errorList) {
		SetValuedMap<String, String> postalCodeMap = new HashSetValuedHashMap<>();
		Set<String> postalCodeNameList = locationHierarchyDetails
				.get(IdObjectValidatorLocationMapping.POSTAL_CODE.getLevel());
		Optional.ofNullable(postalCodeNameList).orElse(Collections.emptySet()).stream()
				.forEach(hierarchyName -> Optional.ofNullable(locationDetails.get(hierarchyName))
						.ifPresent(postalCodeMap::putAll));
		JsonPath jsonPath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_POSTAL_CODE_PATH.getValue());
		String value = jsonPath.read(identityString, READ_OPTIONS);
		if (Objects.nonNull(value) && !postalCodeMap.values().contains(value)) {
			errorList.add(new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
							convertToPath(jsonPath.getPath()))));
		}
	}
	
	/**
	 * Validate documents.
	 *
	 * @param identityString the identity string
	 * @param errorList the error list
	 */
	private void validateDocuments(String identityString, List<ServiceError> errorList) {
		IdObjectValidatorDocumentMapping.getAllMapping().entrySet().stream()
				.filter(entry -> docTypeMap.containsKey(entry.getKey())).forEach(entry -> {
					JsonPath jsonPath = JsonPath.compile("identity." + entry.getValue() + ".type");
					Object value = jsonPath.read(identityString, READ_OPTIONS);
					if (Objects.nonNull(value) && !docTypeMap.get(entry.getKey()).contains(value)) {
						errorList.add(
								new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
										String.format(
												IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
												convertToPath(jsonPath.getPath()))));
					}
				});
	}
	
	/**
	 * Convert to path.
	 *
	 * @param jsonPath the json path
	 * @return the string
	 */
	private String convertToPath(String jsonPath) {
		String path = String.valueOf(jsonPath.replaceAll("[$']", ""));
		return path.substring(1, path.length() - 1).replace("][", "/");
	}
}
