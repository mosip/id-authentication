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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.dto.IdRepoResponseDto;
import io.mosip.resident.dto.JsonValue;
import io.mosip.resident.dto.VidGeneratorResponseDto;
import io.mosip.resident.exception.IdRepoAppException;

/**
 * 
 * @author Girish Yarru
 *
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

	@SuppressWarnings("unchecked")
	public JSONObject retrieveIdrepoJson(String id, IdType idType) throws IOException, Exception {

		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(id);
		IdRepoResponseDto idRepoResponseDto = null;
		if (IdType.UIN.equals(idType))
			idRepoResponseDto = (IdRepoResponseDto) residentServiceRestClient.getApi(ApiName.IDREPOGETIDBYUIN,
					pathsegments, null, null, IdRepoResponseDto.class, tokenGenerator.getToken());
		else if (IdType.RID.equals(idType))
			idRepoResponseDto = (IdRepoResponseDto) residentServiceRestClient.getApi(ApiName.IDREPOGETIDBYRID,
					pathsegments, null, null, IdRepoResponseDto.class, tokenGenerator.getToken());
		else if (IdType.VID.equals(idType)) {
			ResponseWrapper<VidGeneratorResponseDto> response = (ResponseWrapper<VidGeneratorResponseDto>) residentServiceRestClient
					.getApi(ApiName.GETUINBYVID, pathsegments, null, null, ResponseWrapper.class,
							tokenGenerator.getToken());
			if (response == null)
				return null;
			if (!response.getErrors().isEmpty()) {
				List<ServiceError> error = response.getErrors();
				throw new IdRepoAppException(error.get(0).getMessage());
			}

			String uin = response.getResponse().getUIN();
			pathsegments.clear();
			pathsegments.add(uin);
			idRepoResponseDto = (IdRepoResponseDto) residentServiceRestClient.getApi(ApiName.IDREPOGETIDBYUIN,
					pathsegments, null, null, IdRepoResponseDto.class, tokenGenerator.getToken());
		}
		if (idRepoResponseDto == null)
			return null;
		if (!idRepoResponseDto.getErrors().isEmpty()) {
			List<ServiceError> error = idRepoResponseDto.getErrors();
			throw new IdRepoAppException(error.get(0).getMessage());
		}
		idRepoResponseDto.getResponse().getIdentity();
		ObjectMapper objMapper = new ObjectMapper();
		return objMapper.convertValue(idRepoResponseDto.getResponse().getIdentity(), JSONObject.class);

	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getMailingAttributes(String id, IdType idType) throws IOException, Exception {
		Map<String, Object> attributes = new HashMap<>();
		JSONObject idJson = retrieveIdrepoJson(id, idType);
		String email = (String) idJson.get(EMAIL);
		String phone = (String) idJson.get(PHONE);
		attributes.put(EMAIL, email);
		attributes.put(PHONE, phone);
		String mappingJsonString = getMappingJson();
		JSONObject mappingJsonObject = new ObjectMapper().readValue(mappingJsonString, JSONObject.class);
		LinkedHashMap<Object, Object> mappingIdentityJson = (LinkedHashMap<Object, Object>) mappingJsonObject
				.get(IDENTITY);
		LinkedHashMap<Object, Object> nameJson = (LinkedHashMap<Object, Object>) mappingIdentityJson.get("name");
		String[] names = ((String) nameJson.get("value")).split(",");
		for (String name : names) {
			JsonValue[] nameArray = JsonUtil.getJsonValues(idJson, name);
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

		return attributes;
	}

	public String getMappingJson() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(configServerFileStorageURL + getRegProcessorIdentityJson, String.class);
	}

}
