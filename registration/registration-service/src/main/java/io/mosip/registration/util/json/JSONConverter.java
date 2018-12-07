package io.mosip.registration.util.json;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.BaseDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.LocationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;

/**
 * Class to convert the {@link DemographicDTO} into MOSIP ID JSON format
 * 
 */
public class JSONConverter {

	/**
	 * Private Constructor
	 */
	private JSONConverter() {

	}

	/**
	 * Converts the {@link DemographicDTO} object into {@link Map} object equivalent
	 * to the ID JSON format
	 * 
	 * @param demographicDTO
	 *            the {@link DemographicDTO} object to be converted
	 * @return the {@link Map} object equivalent to the ID JSON format
	 * @throws RegBaseCheckedException
	 */
	public static Map<String, Map<String, List<Map<String, String>>>> jsonConvertor(DemographicDTO demographicDTO)
			throws RegBaseCheckedException {
		Map<String, Map<String, List<Map<String, String>>>> demographicInfoJSON = new HashMap<>();

		try {
			demographicInfoJSON.put("identity",
					buildJSON(demographicDTO.getDemoInUserLang(), demographicDTO.getDemoInLocalLang()));
		} catch (RegBaseCheckedException baseCheckedException) {
			throw baseCheckedException;
		} catch (RuntimeException exception) {
			throw new RegBaseUncheckedException(RegistrationConstants.ID_JSON_CONVERSION_EXCEPTION,
					exception.getLocalizedMessage());
		}
		return demographicInfoJSON;
	}

	private static Map<String, List<Map<String, String>>> buildJSON(DemographicInfoDTO inUserLang,
			DemographicInfoDTO inLocalLang) throws RegBaseCheckedException {
		Map<String, List<Map<String, String>>> jsonMap = new LinkedHashMap<>();

		try {
			Field[] fields = DemographicInfoDTO.class.getDeclaredFields();

			for (Field field : fields) {
				field.setAccessible(true);

				if (field.getType().isAssignableFrom(AddressDTO.class)) {
					Field[] addressFields = AddressDTO.class.getDeclaredFields();

					for (Field addressField : addressFields) {
						addressField.setAccessible(true);

						if (addressField.getType().isAssignableFrom(LocationDTO.class)) {
							Field[] locationFields = LocationDTO.class.getDeclaredFields();

							for (Field locationField : locationFields) {
								locationField.setAccessible(true);
								Field inLocalLanguage = inLocalLang.getAddressDTO().getLocationDTO().getClass()
										.getDeclaredField(locationField.getName());
								inLocalLanguage.setAccessible(true);
								jsonMap.put(locationField.getName(),
										buildLanguageObject(locationField, inLocalLanguage,
												inUserLang.getAddressDTO().getLocationDTO(),
												inLocalLang.getAddressDTO().getLocationDTO()));
							}
						} else {
							Field inLocalLanguage = inLocalLang.getAddressDTO().getClass()
									.getDeclaredField(addressField.getName());
							inLocalLanguage.setAccessible(true);
							jsonMap.put(addressField.getName(), buildLanguageObject(addressField, inLocalLanguage,
									inUserLang.getAddressDTO(), inLocalLang.getAddressDTO()));
						}
					}
				} else {
					Field inLocalLanguage = inLocalLang.getClass().getDeclaredField(field.getName());
					inLocalLanguage.setAccessible(true);
					jsonMap.put(field.getName(), buildLanguageObject(field, inLocalLanguage, inUserLang, inLocalLang));
				}
			}

		} catch (NoSuchFieldException | SecurityException exception) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_ID_JSON_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_ID_JSON_ERROR.getErrorMessage());
		} catch (RegBaseCheckedException regBaseCheckedException) {
			throw regBaseCheckedException;
		} catch (RuntimeException exception) {
			throw new RegBaseUncheckedException(RegistrationConstants.ID_JSON_PARSER_EXCEPTION,
					exception.getLocalizedMessage());
		}

		return jsonMap;
	}

	private static List<Map<String, String>> buildLanguageObject(Field inUserLanguage, Field inLocalLanguage,
			BaseDTO inUserLang, BaseDTO inLocalLang) throws RegBaseCheckedException {
		List<Map<String, String>> languagesSpecificObjects = new LinkedList<>();

		try {
			Map<String, String> languagesSpecificObject = new LinkedHashMap<>();
			languagesSpecificObject.put("language", AppConfig.getApplicationProperty("application_language"));
			languagesSpecificObject.put("label", inUserLanguage.getName());
			languagesSpecificObject.put("value", inUserLanguage.get(inUserLang) == null ? null : String.valueOf(inUserLanguage.get(inUserLang)));

			languagesSpecificObjects.add(languagesSpecificObject);

			languagesSpecificObject = new LinkedHashMap<>();
			languagesSpecificObject.put("language", AppConfig.getApplicationProperty("local_language"));
			languagesSpecificObject.put("label", inLocalLanguage.getName());
			languagesSpecificObject.put("value", inLocalLanguage.get(inLocalLang) == null ? null : String.valueOf(inLocalLanguage.get(inLocalLang)));

			languagesSpecificObjects.add(languagesSpecificObject);
		} catch (IllegalArgumentException | IllegalAccessException exception) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_ID_JSON_FIELD_ACCESS_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_ID_JSON_FIELD_ACCESS_ERROR.getErrorMessage());
		} catch (RuntimeException exception) {
			throw new RegBaseUncheckedException(RegistrationConstants.ID_JSON_FIELD_ACCESS_EXCEPTION,
					exception.getLocalizedMessage());
		}

		return languagesSpecificObjects;
	}
}
