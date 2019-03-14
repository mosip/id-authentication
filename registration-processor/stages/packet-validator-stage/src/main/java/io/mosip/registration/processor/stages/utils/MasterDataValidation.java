package io.mosip.registration.processor.stages.utils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.IdJSONConstant;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.packet.dto.masterdata.StatusResponseDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.ErrorDTO;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.exception.FieldNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.InstantanceCreationException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

/**
 * The Class MasterDataValidation.
 * 
 * @author Nagalakshmi
 * 
 */
@Service
public class MasterDataValidation {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(MasterDataValidation.class);

	/** The registration status dto. */
	InternalRegistrationStatusDto registrationStatusDto;

	/** The registration processor rest service. */
	RegistrationProcessorRestClientService<Object> registrationProcessorRestService;

	/** The env. */
	Environment env;

	/** The Constant VALID. */
	private static final String VALID = "Valid";

	JSONObject demographicIdentity = null;

	private Utilities utility;

	private RegistrationProcessorIdentity regProcessorIdentityJson;
	private static final String VALUE = "value";
	private static final String LANGUAGE = "language";

	/**
	 * Instantiates a new master data validation.
	 *
	 * @param registrationStatusDto
	 *            the registration status dto
	 * @param env
	 *            the env
	 * @param registrationProcessorRestService
	 *            the registration processor rest service
	 */
	public MasterDataValidation(InternalRegistrationStatusDto registrationStatusDto, Environment env,
			RegistrationProcessorRestClientService<Object> registrationProcessorRestService, Utilities utility,
			RegistrationProcessorIdentity regProcessorIdentityJson) {
		this.registrationStatusDto = registrationStatusDto;
		this.env = env;
		this.registrationProcessorRestService = registrationProcessorRestService;
		this.utility = utility;
		this.regProcessorIdentityJson = regProcessorIdentityJson;
	}

	/**
	 * Validate master data.
	 *
	 * @param regProcessorIdentityJson
	 *            the reg processor identity json
	 * @return the boolean
	 */
	public Boolean validateMasterData(String jsonString) {
		String genderName = null;
		String regionName = null;
		String provinceName = null;
		String cityName = null;
		String postalcode = null;

		try {

			String getIdentityJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
					utility.getGetRegProcessorIdentityJson());
			ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
			regProcessorIdentityJson = mapIdentityJsonStringToObject.readValue(getIdentityJsonString,
					RegistrationProcessorIdentity.class);

			JSONObject demographicJson = JsonUtil.objectMapperReadValue(jsonString, JSONObject.class);
			demographicIdentity = JsonUtil.getJSONObject(demographicJson,
					utility.getGetRegProcessorDemographicIdentity());

			if (demographicIdentity == null)
				throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());

			genderName = getParameter(getJsonValues(regProcessorIdentityJson.getIdentity().getGender().getValue()));
			regionName = getParameter(getJsonValues(regProcessorIdentityJson.getIdentity().getRegion().getValue()));
			provinceName = getParameter(getJsonValues(regProcessorIdentityJson.getIdentity().getProvince().getValue()));
			cityName = getParameter(getJsonValues(regProcessorIdentityJson.getIdentity().getCity().getValue()));
			postalcode = JsonUtil.getJSONValue(demographicIdentity,
					regProcessorIdentityJson.getIdentity().getPostalCode().getValue());

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] elements = env.getProperty("registration.processor.idjson.attributes").split(",");
		List<String> list = new ArrayList<>(Arrays.asList(elements));
		boolean isValid = false;

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"MasterDataValidation::validateMasterData::entry");

		if (getValue(list, IdJSONConstant.GENDER.toString()) && (!validateGenderName(genderName))) {
			registrationStatusDto.setStatusComment(StatusMessage.GENDER_NAME_NOT_AVAILABLE);
			return false;
		}

		if (getValue(list, IdJSONConstant.REGION.toString()) && (!validateLocationName(regionName))) {
			registrationStatusDto.setStatusComment(StatusMessage.REGION_NOT_AVAILABLE);
			return false;
		}

		if (getValue(list, IdJSONConstant.PROVINCE.toString()) && (!validateLocationName(provinceName))) {
			registrationStatusDto.setStatusComment(StatusMessage.PROVINCE_NOT_AVAILABLE);
			return false;
		}

		if (getValue(list, IdJSONConstant.CITY.toString()) && (!validateLocationName(cityName))) {
			registrationStatusDto.setStatusComment(StatusMessage.CITY_NOT_AVAILABLE);
			return false;
		}

		if (getValue(list, IdJSONConstant.POSTALCODE.toString()) && (!validateLocationName(postalcode))) {
			registrationStatusDto.setStatusComment(StatusMessage.POSTALCODE_NOT_AVAILABLE);
			return false;
		}

		isValid = true;

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"MasterDataValidation::validateMasterData::exit");
		return isValid;

	}

	/**
	 * Gets the value.
	 *
	 * @param list
	 *            the list
	 * @param value
	 *            the value
	 * @return the value
	 */
	private Boolean getValue(List<String> list, String value) {
		Iterator<String> it = list.iterator();

		while (it.hasNext()) {
			String dataValue = it.next();
			if (dataValue.equalsIgnoreCase(value))
				return true;
		}
		return false;
	}

	/**
	 * Validate gender name.
	 *
	 * @param genderName
	 *            the gender name
	 * @return true, if successful
	 */
	private boolean validateGenderName(String genderName) {
		boolean isValidGender = false;
		StatusResponseDto statusResponseDto;
		if (genderName != null) {
			try {
				List<String> pathsegments = new ArrayList<>();
				pathsegments.add(genderName);

				statusResponseDto = (StatusResponseDto) registrationProcessorRestService.getApi(ApiName.GENDERTYPE,
						pathsegments, "", "", StatusResponseDto.class);

				if (statusResponseDto.getStatus().equalsIgnoreCase(VALID))
					isValidGender = true;
			} catch (ApisResourceAccessException ex) {
				if (ex.getCause() instanceof HttpClientErrorException) {
					HttpClientErrorException httpClientException = (HttpClientErrorException) ex.getCause();
					String result = httpClientException.getResponseBodyAsString();
					Gson gsonObj = new Gson();
					statusResponseDto = gsonObj.fromJson(result, StatusResponseDto.class);
					ErrorDTO error = statusResponseDto.getErrors().get(0);
					isValidGender = false;

					this.registrationStatusDto.setStatusComment(error.getErrorMessage());

				}
			}
		} else {
			isValidGender = true;
		}
		return isValidGender;
	}

	/**
	 * Validate location name.
	 *
	 * @param locationName
	 *            the location name
	 * @return true, if successful
	 */
	private boolean validateLocationName(String locationName) {
		boolean isValidLocation = false;
		StatusResponseDto statusResponseDto;
		if (locationName != null) {
			try {
				List<String> pathsegments = new ArrayList<>();
				pathsegments.add(locationName);

				statusResponseDto = (StatusResponseDto) registrationProcessorRestService.getApi(ApiName.LOCATION,
						pathsegments, "", "", StatusResponseDto.class);

				if (statusResponseDto.getStatus().equalsIgnoreCase(VALID))
					isValidLocation = true;
			} catch (ApisResourceAccessException ex) {
				if (ex.getCause() instanceof HttpClientErrorException) {
					HttpClientErrorException httpClientException = (HttpClientErrorException) ex.getCause();
					String result = httpClientException.getResponseBodyAsString();
					Gson gsonObj = new Gson();
					statusResponseDto = gsonObj.fromJson(result, StatusResponseDto.class);
					ErrorDTO error = statusResponseDto.getErrors().get(0);
					isValidLocation = false;

					this.registrationStatusDto.setStatusComment(error.getErrorMessage());

				}
			}
		} else {
			isValidLocation = true;
		}
		return isValidLocation;

	}

	private String getParameter(JsonValue[] jsonValues) {

		String langCode = "eng";
		String parameter = null;
		if (jsonValues != null) {
			for (int count = 0; count < jsonValues.length; count++) {
				String lang = jsonValues[count].getLanguage();
				if (langCode.contains(lang)) {
					parameter = jsonValues[count].getValue();
					break;
				}
			}
		}
		return parameter;
	}

	private JsonValue[] getJsonValues(Object identityKey) {
		JSONArray demographicJsonNode = null;
		if (demographicIdentity != null)
			demographicJsonNode = JsonUtil.getJSONArray(demographicIdentity, identityKey);

		return (demographicJsonNode != null) ? mapJsonNodeToJavaObject(JsonValue.class, demographicJsonNode) : null;
	}

	@SuppressWarnings("unchecked")
	private <T> T[] mapJsonNodeToJavaObject(Class<? extends Object> genericType, JSONArray demographicJsonNode) {
		String language;
		String value;
		T[] javaObject = (T[]) Array.newInstance(genericType, demographicJsonNode.size());
		try {
			for (int i = 0; i < demographicJsonNode.size(); i++) {

				T jsonNodeElement = (T) genericType.newInstance();

				JSONObject objects = JsonUtil.getJSONObjectFromArray(demographicJsonNode, i);
				language = (String) objects.get(LANGUAGE);
				value = (String) objects.get(VALUE);

				Field languageField = jsonNodeElement.getClass().getDeclaredField(LANGUAGE);
				languageField.setAccessible(true);
				languageField.set(jsonNodeElement, language);

				Field valueField = jsonNodeElement.getClass().getDeclaredField(VALUE);
				valueField.setAccessible(true);
				valueField.set(jsonNodeElement, value);

				javaObject[i] = jsonNodeElement;
			}
		} catch (InstantiationException | IllegalAccessException e) {

			throw new InstantanceCreationException(PlatformErrorMessages.RPR_SYS_INSTANTIATION_EXCEPTION.getMessage(),
					e);

		} catch (NoSuchFieldException | SecurityException e) {

			throw new FieldNotFoundException(PlatformErrorMessages.RPR_SYS_NO_SUCH_FIELD_EXCEPTION.getMessage(), e);

		}

		return javaObject;

	}

}