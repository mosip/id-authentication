package io.mosip.registration.validator;

import static io.mosip.registration.constants.LoggerConstants.REG_ID_OBJECT_MASTER_DATA_VALIDATOR;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

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
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorDocumentMapping;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorLocationMapping;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.idobjectvalidator.constant.IdObjectValidatorConstant;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.entity.DocumentCategory;
import io.mosip.registration.entity.DocumentType;
import io.mosip.registration.entity.Gender;
import io.mosip.registration.entity.Language;
import io.mosip.registration.entity.Location;
import io.mosip.registration.entity.ValidDocument;
import net.minidev.json.JSONArray;

/**
 *  This class validates the user selected (which is provided by the UI layer) dropdown data against one available at the database layer, which is 
 * 	synced from MOSIP server. 
 *
 * @author SaravanaKumar G
 */
@Service
public class RegIdObjectMasterDataValidator {


	/** Instance of {@link Logger}. */

	private Logger LOGGER = AppConfig.getLogger(RegIdObjectMasterDataValidator.class);

	@Autowired
	private MasterSyncDao masterSyncDao;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The Constant READ_OPTIONS. */
	private static final Configuration READ_OPTIONS = Configuration.defaultConfiguration()
			.addOptions(Option.SUPPRESS_EXCEPTIONS);

	/** The Constant PATH_LIST_OPTIONS. */
	private static final Configuration PATH_LIST_OPTIONS = Configuration.defaultConfiguration()
			.addOptions(Option.SUPPRESS_EXCEPTIONS, Option.AS_PATH_LIST, Option.ALWAYS_RETURN_LIST);

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
	 * 
	 * @throws IdObjectIOException
	 */
	@PostConstruct
	public void loadData() {
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Loading data has been started post construct");
		
		mapper.registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());
		loadLanguages();
		loadGenderTypes();
		loadLocationDetails();
		loadDocCategories();
		loadDocTypes();

		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Loading data has ended");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator#validateIdObject
	 * (java.lang.Object)
	 */
	public boolean validateIdObject(Object identityObject, IdObjectValidatorSupportedOperations operation)
			throws IdObjectIOException, IdObjectValidationFailedException {
		
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating ID object has been started");
		
		try {
			String identityString = mapper.writeValueAsString(identityObject);
			List<ServiceError> errorList = new ArrayList<>();
			validateLanguage(identityString, errorList);
			validateGender(identityString, errorList);
			validateRegion(identityString, errorList);
			validateProvince(identityString, errorList);
			validateCity(identityString, errorList);
			//validatePostalCode(identityString, errorList);
			validateLocalAdministrativeAuthority(identityString, errorList);
			validateDocuments(identityString, errorList);
			LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating ID object has been ended");
			if (errorList.isEmpty()) {
				return true;
			} else {
				throw new IdObjectValidationFailedException(IdObjectValidatorErrorConstant.ID_OBJECT_VALIDATION_FAILED,
						errorList);
			}
		} catch (JsonProcessingException e) {
			throw new IdObjectIOException(IdObjectValidatorErrorConstant.ID_OBJECT_PARSING_FAILED, e);
		}
	}

	/**
	 * Load languages.
	 */
	private void loadLanguages() {
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Loading languages has been started");

		List<Language> langList = masterSyncDao.getActiveLanguages();
		languageList = new ArrayList<>();
		if (!langList.isEmpty()) {
			langList.forEach(lang -> {
				languageList.add(lang.getCode());
			});
		}
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Loading languages has been ended");
	}

	/**
	 * Load gender types.
	 */
	private void loadGenderTypes() {
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Loading gender types has been started");

		List<Gender> genderList = masterSyncDao.getGenders();
		if (!genderList.isEmpty()) {
			genderMap = new HashSetValuedHashMap<>(genderList.size());
			genderList.forEach(gender -> {
				genderMap.put(gender.getLangCode(), gender.getGenderName());
			});
		}
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Loading gender types has been ended");

	}

	/**
	 * Load doc categories.
	 */
	private void loadDocCategories() {
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Loading document categories has been started");

		List<DocumentCategory> docList = masterSyncDao.getDocumentCategory();
		if (!docList.isEmpty()) {
			docCatMap = new HashSetValuedHashMap<>(docList.size());
			docList.forEach(doc -> {
				docCatMap.put(doc.getLangCode(), doc.getCode());
			});
		}
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Loading document categories has been ended");
	}

	/**
	 * Load doc types.
	 */
	private void loadDocTypes() {
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Loading document types has been started");
		
		docTypeMap = new HashSetValuedHashMap<>();
		if (Objects.nonNull(docCatMap) && !docCatMap.isEmpty()) {
			docCatMap.keySet().stream().forEach(langCode -> docCatMap.get(langCode).stream().forEach(docCat -> {
				List<ValidDocument> masterValidDocuments = masterSyncDao.getValidDocumets(docCat);

				List<String> validDocuments = new ArrayList<>();
				masterValidDocuments.forEach(docs -> {
					validDocuments.add(docs.getDocTypeCode());
				});

				List<DocumentType> masterDocuments = masterSyncDao.getDocumentTypes(validDocuments, langCode);
				if (!masterDocuments.isEmpty()) {
					masterDocuments.forEach(docType -> {
						docTypeMap.put(docCat, docType.getName());
						docTypeMap.put(docCat, docType.getCode());
					});
				}
			}));
		}
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Loading document types has been ended");

	}

	/**
	 * Load location details.
	 */
	private void loadLocationDetails() {
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Loading location details has been started");

		locationHierarchyDetails = new HashSetValuedHashMap<>();
		locationDetails = new LinkedHashMap<>();
		List<Location> locationList = masterSyncDao.getLocationDetails();
			if (!locationList.isEmpty()) {
				locationList.forEach(location -> {
						locationHierarchyDetails.put(String.valueOf(location.getHierarchyLevel()),
								location.getHierarchyName());
						locationDetails.put(location.getHierarchyName(), null);
				});
				
				Set<String> locationHierarchyNames = locationDetails.keySet().parallelStream().collect(Collectors.toSet());
				locationHierarchyNames.stream().forEach(hierarchyName -> {
					SetValuedMap<String, String> locations = new HashSetValuedHashMap<>();
					locationList.forEach(location -> {
						if(location.getHierarchyName().equalsIgnoreCase(hierarchyName)) {
							locations.put(location.getLangCode(), location.getName());
							locations.put(location.getLangCode(), location.getCode());
						}
					});
					locationDetails.put(hierarchyName, locations);
				});
			}
			
			LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Loading location details has been ended");

	}

	/**
	 * Validate language.
	 *
	 * @param identityString
	 *            the identity string
	 * @param errorList
	 *            the error list
	 */
	private void validateLanguage(String identityString, List<ServiceError> errorList) {
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating language has been started");
		JsonPath jsonPath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_LANGUAGE_PATH.getValue());
		JSONArray pathList = jsonPath.read(identityString, PATH_LIST_OPTIONS);
		Map<String, String> dataMap = IntStream.range(0, pathList.size()).boxed().parallel()
				.collect(Collectors.toMap(i -> String.valueOf(pathList.get(i)),
						i -> JsonPath.compile(String.valueOf(pathList.get(i))).read(identityString, READ_OPTIONS)));
		dataMap.entrySet().parallelStream().filter(entry -> !languageList.contains(entry.getValue()))
				.forEach(entry -> errorList
						.add(new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
								String.format(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
										convertToPath(entry.getKey())))));
		
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating language has been ended");
	}

	/**
	 * Validate gender.
	 *
	 * @param identityString
	 *            the identity string
	 * @param errorList
	 *            the error list
	 */
	private void validateGender(String identityString, List<ServiceError> errorList) {
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating Gender has been started");

		JsonPath genderLangPath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_GENDER_LANGUAGE_PATH.getValue());
		List<String> genderLangPathList = genderLangPath.read(identityString, PATH_LIST_OPTIONS);
		JsonPath genderValuePath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_GENDER_VALUE_PATH.getValue());
		List<String> genderValuePathList = genderValuePath.read(identityString, PATH_LIST_OPTIONS);
		Map<String, Map.Entry<String, String>> dataMap = IntStream.range(0, genderLangPathList.size()).parallel()
				.filter(index -> languageList
						.contains(JsonPath.compile(genderLangPathList.get(index)).read(identityString, READ_OPTIONS)))
				.boxed()
				.collect(Collectors.toMap(genderLangPathList::get,
						i -> new AbstractMap.SimpleImmutableEntry<String, String>(genderValuePathList.get(i),
								JsonPath.compile(genderValuePathList.get(i)).read(identityString, READ_OPTIONS))));
		dataMap.entrySet().parallelStream().filter(entry -> {
			String lang = JsonPath.compile(entry.getKey()).read(identityString, READ_OPTIONS);
			System.out.println(
					"Entry Val**" + lang + "->>>>" + genderMap.get(lang).contains(entry.getValue().getValue()));
			return genderMap.containsKey(lang) && !genderMap.get(lang).contains(entry.getValue().getValue());
		}).forEach(entry -> errorList
				.add(new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
								convertToPath(entry.getValue().getKey())))));
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating Gender has been ended");

	}

	/**
	 * Validate region.
	 *
	 * @param identityString
	 *            the identity string
	 * @param errorList
	 *            the error list
	 */
	private void validateRegion(String identityString, List<ServiceError> errorList) {
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating region has been started");

		SetValuedMap<String, String> regionMap = new HashSetValuedHashMap<>();
		Set<String> regionNameList = locationHierarchyDetails.get(IdObjectValidatorLocationMapping.REGION.getLevel());
		Optional.ofNullable(regionNameList).orElse(Collections.emptySet()).parallelStream()
				.forEach(hierarchyName -> Optional.ofNullable(locationDetails.get(hierarchyName))
						.ifPresent(locationDetail -> regionMap.putAll(locationDetail)));
		JsonPath langPath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_REGION_LANGUAGE_PATH.getValue());
		List<String> langPathList = langPath.read(identityString, PATH_LIST_OPTIONS);
		JsonPath valuePath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_REGION_VALUE_PATH.getValue());
		List<String> valuePathList = valuePath.read(identityString, PATH_LIST_OPTIONS);
		Map<String, Map.Entry<String, String>> dataMap = IntStream.range(0, langPathList.size()).parallel()
				.filter(index -> languageList
						.contains(JsonPath.compile(langPathList.get(index)).read(identityString, READ_OPTIONS)))
				.boxed()
				.collect(Collectors.toMap(langPathList::get,
						i -> new AbstractMap.SimpleImmutableEntry<String, String>(valuePathList.get(i),
								JsonPath.compile(valuePathList.get(i)).read(identityString, READ_OPTIONS))));
		dataMap.entrySet().parallelStream().filter(entry -> {
			String lang = JsonPath.compile(entry.getKey()).read(identityString, READ_OPTIONS);
			return regionMap.containsKey(lang) && !regionMap.get(lang).contains(entry.getValue().getValue());
		}).forEach(entry -> errorList
				.add(new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
								convertToPath(entry.getValue().getKey())))));

		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating region has been ended");
	}

	/**
	 * Validate province.
	 *
	 * @param identityString
	 *            the identity string
	 * @param errorList
	 *            the error list
	 */
	private void validateProvince(String identityString, List<ServiceError> errorList) {
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating province has been started");

		SetValuedMap<String, String> provinceMap = new HashSetValuedHashMap<>();
		Set<String> provinceNameList = locationHierarchyDetails
				.get(IdObjectValidatorLocationMapping.PROVINCE.getLevel());
		Optional.ofNullable(provinceNameList).orElse(Collections.emptySet()).parallelStream()
				.forEach(hierarchyName -> Optional.ofNullable(locationDetails.get(hierarchyName))
						.ifPresent(locationDetail -> provinceMap.putAll(locationDetail)));
		JsonPath langPath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_PROVINCE_LANGUAGE_PATH.getValue());
		List<String> langPathList = langPath.read(identityString, PATH_LIST_OPTIONS);
		JsonPath valuePath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_PROVINCE_VALUE_PATH.getValue());
		List<String> valuePathList = valuePath.read(identityString, PATH_LIST_OPTIONS);
		Map<String, Map.Entry<String, String>> dataMap = IntStream.range(0, langPathList.size()).parallel()
				.filter(index -> languageList
						.contains(JsonPath.compile(langPathList.get(index)).read(identityString, READ_OPTIONS)))
				.boxed()
				.collect(Collectors.toMap(langPathList::get,
						i -> new AbstractMap.SimpleImmutableEntry<String, String>(valuePathList.get(i),
								JsonPath.compile(valuePathList.get(i)).read(identityString, READ_OPTIONS))));
		dataMap.entrySet().parallelStream().filter(entry -> {
			String lang = JsonPath.compile(entry.getKey()).read(identityString, READ_OPTIONS);
			return provinceMap.containsKey(lang) && !provinceMap.get(lang).contains(entry.getValue().getValue());
		}).forEach(entry -> errorList
				.add(new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
								convertToPath(entry.getValue().getKey())))));
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating province has been ended");
	}

	/**
	 * Validate city.
	 *
	 * @param identityString
	 *            the identity string
	 * @param errorList
	 *            the error list
	 */
	private void validateCity(String identityString, List<ServiceError> errorList) {
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating city has been started");

		SetValuedMap<String, String> cityMap = new HashSetValuedHashMap<>();
		Set<String> cityNameList = locationHierarchyDetails.get(IdObjectValidatorLocationMapping.CITY.getLevel());
		Optional.ofNullable(cityNameList).orElse(Collections.emptySet()).parallelStream()
				.forEach(hierarchyName -> Optional.ofNullable(locationDetails.get(hierarchyName))
						.ifPresent(locationDetail -> cityMap.putAll(locationDetail)));
		JsonPath langPath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_CITY_LANGUAGE_PATH.getValue());
		List<String> langPathList = langPath.read(identityString, PATH_LIST_OPTIONS);
		JsonPath valuePath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_CITY_VALUE_PATH.getValue());
		List<String> valuePathList = valuePath.read(identityString, PATH_LIST_OPTIONS);
		Map<String, Map.Entry<String, String>> dataMap = IntStream.range(0, langPathList.size()).parallel()
				.filter(index -> languageList
						.contains(JsonPath.compile(langPathList.get(index)).read(identityString, READ_OPTIONS)))
				.boxed()
				.collect(Collectors.toMap(langPathList::get,
						i -> new AbstractMap.SimpleImmutableEntry<String, String>(valuePathList.get(i),
								JsonPath.compile(valuePathList.get(i)).read(identityString, READ_OPTIONS))));
		dataMap.entrySet().parallelStream().filter(entry -> {
			String lang = JsonPath.compile(entry.getKey()).read(identityString, READ_OPTIONS);
			return cityMap.containsKey(lang) && !cityMap.get(lang).contains(entry.getValue().getValue());
		}).forEach(entry -> errorList
				.add(new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
								convertToPath(entry.getValue().getKey())))));
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating city has been ended");
	}

	/**
	 * Validate local administrative authority.
	 *
	 * @param identityString
	 *            the identity string
	 * @param errorList
	 *            the error list
	 */
	private void validateLocalAdministrativeAuthority(String identityString, List<ServiceError> errorList) {
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating local administrative authority has been started");

		SetValuedMap<String, String> localAdministrativeAuthorityMap = new HashSetValuedHashMap<>();
		Set<String> localAdminAuthNameList = locationHierarchyDetails
				.get(IdObjectValidatorLocationMapping.LOCAL_ADMINISTRATIVE_AUTHORITY.getLevel());
		Optional.ofNullable(localAdminAuthNameList).orElse(Collections.emptySet()).parallelStream()
				.forEach(hierarchyName -> Optional.ofNullable(locationDetails.get(hierarchyName))
						.ifPresent(locationDetail -> localAdministrativeAuthorityMap.putAll(locationDetail)));
		JsonPath langPath = JsonPath
				.compile(IdObjectValidatorConstant.IDENTITY_LOCALADMINISTRATIVEAUTHORITY_LANGUAGE_PATH.getValue());
		List<String> langPathList = langPath.read(identityString, PATH_LIST_OPTIONS);
		JsonPath valuePath = JsonPath
				.compile(IdObjectValidatorConstant.IDENTITY_LOCALADMINISTRATIVEAUTHORITY_VALUE_PATH.getValue());
		List<String> valuePathList = valuePath.read(identityString, PATH_LIST_OPTIONS);
		Map<String, Map.Entry<String, String>> dataMap = IntStream.range(0, langPathList.size()).parallel()
				.filter(index -> languageList
						.contains(JsonPath.compile(langPathList.get(index)).read(identityString, READ_OPTIONS)))
				.boxed()
				.collect(Collectors.toMap(langPathList::get,
						i -> new AbstractMap.SimpleImmutableEntry<String, String>(valuePathList.get(i),
								JsonPath.compile(valuePathList.get(i)).read(identityString, READ_OPTIONS))));
		dataMap.entrySet().parallelStream().filter(entry -> {
			String lang = JsonPath.compile(entry.getKey()).read(identityString, READ_OPTIONS);
			return localAdministrativeAuthorityMap.containsKey(lang)
					&& !localAdministrativeAuthorityMap.get(lang).contains(entry.getValue().getValue());
		}).forEach(entry -> errorList
				.add(new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
								convertToPath(entry.getValue().getKey())))));
		
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating local administrative authority has been ended");
	}

	/**
	 * Validate postal code.
	 *
	 * @param identityString
	 *            the identity string
	 * @param errorList
	 *            the error list
	 */
	private void validatePostalCode(String identityString, List<ServiceError> errorList) {
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating postal code has been started");

		SetValuedMap<String, String> postalCodeMap = new HashSetValuedHashMap<>();
		Set<String> postalCodeNameList = locationHierarchyDetails
				.get(IdObjectValidatorLocationMapping.POSTAL_CODE.getLevel());
		Optional.ofNullable(postalCodeNameList).orElse(Collections.emptySet()).parallelStream()
				.forEach(hierarchyName -> Optional.ofNullable(locationDetails.get(hierarchyName))
						.ifPresent(locationDetail -> postalCodeMap.putAll(locationDetail)));
		JsonPath jsonPath = JsonPath.compile(IdObjectValidatorConstant.IDENTITY_POSTAL_CODE_PATH.getValue());
		String value = jsonPath.read(identityString, READ_OPTIONS);
		System.out.println("Val**"+value);
		System.out.println("Values : "+postalCodeMap.values());
		if (Objects.nonNull(value) && !postalCodeMap.values().parallelStream()
				.allMatch(postalCodeList -> postalCodeList.contains(value.trim()))) {	
			errorList.add(new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
							convertToPath(jsonPath.getPath()))));
		}
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating postal code has been ended");
	}

	/**
	 * Validate documents.
	 *
	 * @param identityString
	 *            the identity string
	 * @param errorList
	 *            the error list
	 */
	private void validateDocuments(String identityString, List<ServiceError> errorList) {
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating documents has been started");

		IdObjectValidatorDocumentMapping.getAllMapping().entrySet().parallelStream()
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
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "Validating documents has been ended");
	}

	/**
	 * Convert to path.
	 *
	 * @param jsonPath
	 *            the json path
	 * @return the string
	 */
	private String convertToPath(String jsonPath) {
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "converting to path has been started");

		String path = String.valueOf(jsonPath.replaceAll("[$']", ""));
		
		LOGGER.info(REG_ID_OBJECT_MASTER_DATA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID, "converting to path has been ended");
		return path.substring(1, path.length() - 1).replace("][", "/");
	}
}
