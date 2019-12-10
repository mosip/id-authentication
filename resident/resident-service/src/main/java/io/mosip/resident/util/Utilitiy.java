package io.mosip.resident.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.constant.ResidentErrorCode;
import io.mosip.resident.dto.IdRepoResponseDto;
import io.mosip.resident.dto.JsonValue;
import io.mosip.resident.dto.VidGeneratorResponseDto;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.IdRepoAppException;
import io.mosip.resident.exception.ResidentServiceCheckedException;

/**
 * 
 * @author Girish Yarru
 * @version 1.0
 */

@Component
public class Utilitiy {

	@Autowired
	private ResidentServiceRestClient residentServiceRestClient;

	@Autowired
	private TokenGenerator tokenGenerator;

	@Value("${config.server.file.storage.uri}")
	private String configServerFileStorageURL;

	@Value("${registration.processor.identityjson}")
	private String getRegProcessorIdentityJson;

	@Value("${mosip.primary-language}")
	private String primaryLang;

	@Value("${mosip.secondary-language}")
	private String secondaryLang;

	@Value("${mosip.notification.language-type}")
	private String languageType;

	private static final String IDENTITY = "identity";
	private static final String EMAIL = "email";
	private static final String PHONE = "phone";
	private static final String NAME = "name";
	private static final String VALUE = "value";

	@SuppressWarnings("unchecked")
	public JSONObject retrieveIdrepoJson(String id, IdType idType) throws ResidentServiceCheckedException {

		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(id);
		ResponseWrapper<IdRepoResponseDto> response = null;
		try {
			if (IdType.UIN.equals(idType))
				response = (ResponseWrapper<IdRepoResponseDto>) residentServiceRestClient.getApi(
						ApiName.IDREPOGETIDBYUIN, pathsegments, null, null, ResponseWrapper.class,
						tokenGenerator.getToken());
			else if (IdType.RID.equals(idType))
				response = (ResponseWrapper<IdRepoResponseDto>) residentServiceRestClient.getApi(
						ApiName.IDREPOGETIDBYRID, pathsegments, null, null, ResponseWrapper.class,
						tokenGenerator.getToken());
			else if (IdType.VID.equals(idType)) {
				ResponseWrapper<VidGeneratorResponseDto> vidResponse = (ResponseWrapper<VidGeneratorResponseDto>) residentServiceRestClient
						.getApi(ApiName.GETUINBYVID, pathsegments, null, null, ResponseWrapper.class,
								tokenGenerator.getToken());
				if (vidResponse == null)
					throw new IdRepoAppException(ResidentErrorCode.IN_VALID_VID.getErrorCode(),
							ResidentErrorCode.IN_VALID_VID.getErrorCode(),
							"In valid response while requesting from ID Repositary");
				if (!vidResponse.getErrors().isEmpty()) {
					List<ServiceError> error = vidResponse.getErrors();
					throw new IdRepoAppException(ResidentErrorCode.IN_VALID_VID.getErrorCode(),
							ResidentErrorCode.IN_VALID_VID.getErrorCode(), error.get(0).getMessage());
				}

				VidGeneratorResponseDto vidGeneratorResponseDto = JsonUtil.objectMapperReadValue(
						JsonUtil.objectMapperObjectToJson(vidResponse.getResponse()), VidGeneratorResponseDto.class);
				String uin = String.valueOf(vidGeneratorResponseDto.getUIN());
				pathsegments.clear();
				pathsegments.add(uin);
				response = (ResponseWrapper<IdRepoResponseDto>) residentServiceRestClient.getApi(
						ApiName.IDREPOGETIDBYUIN, pathsegments, null, null, ResponseWrapper.class,
						tokenGenerator.getToken());
			}
		} catch (IOException e) {
			throw new ResidentServiceCheckedException(ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorCode(),
					ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorMessage(), e);
		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				throw new ResidentServiceCheckedException(
						ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
						httpClientException.getResponseBodyAsString());

			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				throw new ResidentServiceCheckedException(
						ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
						httpServerException.getResponseBodyAsString());
			} else {
				throw new ResidentServiceCheckedException(
						ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
						ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorMessage() + e.getMessage(), e);
			}
		}
		ResidentErrorCode errorCode;
		if (idType.equals(IdType.UIN))
			errorCode = ResidentErrorCode.IN_VALID_UIN;
		else if (idType.equals(IdType.RID))
			errorCode = ResidentErrorCode.IN_VALID_RID;
		else
			errorCode = ResidentErrorCode.IN_VALID_VID_UIN;
		if (response == null)
			throw new IdRepoAppException(errorCode.getErrorCode(), errorCode.getErrorMessage(),
					"In valid response while requesting ID Repositary");
		if (!response.getErrors().isEmpty()) {
			List<ServiceError> error = response.getErrors();
			throw new IdRepoAppException(errorCode.getErrorCode(), errorCode.getErrorMessage(),
					error.get(0).getMessage());
		}
		String jsonResponse;
		try {
			jsonResponse = JsonUtil.objectMapperObjectToJson(response.getResponse());
			JSONObject json = JsonUtil.objectMapperReadValue(jsonResponse, JSONObject.class);

			return JsonUtil.getJSONObject(json, "identity");
		} catch (IOException e) {
			throw new ResidentServiceCheckedException(ResidentErrorCode.RESIDENT_SYS_EXCEPTION.getErrorCode(),
					ResidentErrorCode.RESIDENT_SYS_EXCEPTION.getErrorMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getMailingAttributes(String id, IdType idType) throws ResidentServiceCheckedException {
		Map<String, Object> attributes = new HashMap<>();
		JSONObject identityJson = retrieveIdrepoJson(id, idType);
		String email = (String) identityJson.get(EMAIL);
		String phone = (String) identityJson.get(PHONE);
		attributes.put(EMAIL, email);
		attributes.put(PHONE, phone);
		String mappingJsonString = getMappingJson();
		JSONObject mappingJsonObject;
		try {
			mappingJsonObject = JsonUtil.objectMapperReadValue(mappingJsonString, JSONObject.class);
			LinkedHashMap<Object, Object> mappingIdentityJson = (LinkedHashMap<Object, Object>) mappingJsonObject
					.get(IDENTITY);
			LinkedHashMap<Object, Object> nameJson = (LinkedHashMap<Object, Object>) mappingIdentityJson.get(NAME);
			String[] names = ((String) nameJson.get(VALUE)).split(",");
			for (String name : names) {
				JsonValue[] nameArray = JsonUtil.getJsonValues(identityJson, name);
				if (nameArray != null) {
					for (JsonValue value : nameArray) {
						if (languageType.equals("BOTH")) {
							if (value.getLanguage().equals(primaryLang))
								attributes.put(name + "_" + primaryLang, value.getValue());
							if (value.getLanguage().equals(secondaryLang))
								attributes.put(name + "_" + secondaryLang, value.getValue());
						} else {
							if (value.getLanguage().equals(primaryLang))
								attributes.put(name + "_" + primaryLang, value.getValue());
						}
					}

				}
			}
		} catch (IOException | ReflectiveOperationException e) {
			throw new ResidentServiceCheckedException(ResidentErrorCode.RESIDENT_SYS_EXCEPTION.getErrorCode(),
					ResidentErrorCode.RESIDENT_SYS_EXCEPTION.getErrorMessage(), e);
		}
		return attributes;
	}

	public String getMappingJson() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(configServerFileStorageURL + getRegProcessorIdentityJson, String.class);
	}

}
