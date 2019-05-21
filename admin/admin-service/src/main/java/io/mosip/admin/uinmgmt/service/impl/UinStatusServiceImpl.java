package io.mosip.admin.uinmgmt.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.admin.uinmgmt.constant.UinStatusErrorCode;
import io.mosip.admin.uinmgmt.dto.UinDetailResponseDto;
import io.mosip.admin.uinmgmt.dto.UinResponseWrapperDto;
import io.mosip.admin.uinmgmt.exception.UinPropertiesNotFoundException;
import io.mosip.admin.uinmgmt.exception.UinStatusException;
import io.mosip.admin.uinmgmt.service.UinStatusService;
import io.mosip.kernel.core.exception.ServiceError;

/**
 * Service Impl class for Uin Status Service
 * 
 * @author Megha Tanga
 *
 */
@Service
@RefreshScope
public class UinStatusServiceImpl implements UinStatusService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	public static final String IDENTITY = "identity";

	/**
	 * Field to hold the URL
	 */
	@Value("${uin.status.search}")
	String uinStatusUrl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.admin.uinmgmt.service.UinStatusService#getUinStatus(java.lang.
	 * String)
	 */
	@Override
	public UinResponseWrapperDto getUinStatus(String uin) {
		UinResponseWrapperDto respDto = null;
		try {
			respDto = restTemplate.getForObject(uinStatusUrl, UinResponseWrapperDto.class, uin);
		} catch (RestClientException e) {
			throw new UinStatusException(UinStatusErrorCode.UIN_STATUS_EXCEPTION.getErrorCode(),
					UinStatusErrorCode.UIN_STATUS_EXCEPTION.getErrorMessage(), e);
		}
		if (respDto != null && respDto.getErrors() != null && !respDto.getErrors().isEmpty()) {
			ServiceError error = respDto.getErrors().get(0);
			throw new UinStatusException(error.getErrorCode(), error.getMessage());
		}

		return respDto;

	}

	@Override
	public UinDetailResponseDto getUinDetails(String uin, String langCode) {
		UinDetailResponseDto uinDetailResponseDto = new UinDetailResponseDto();
		// String langCode = "ara";
		// String respDto =
		// restTemplate.getForObject("https://qa.mosip.io/v1/idrepo/identity/{uin}",
		// String.class,"3586192405");
		Map<String, String> uinProperties = new HashMap<>();
		JsonNode jsonNode = null;
		try {
			String respDto = restTemplate.getForObject(uinStatusUrl, String.class, uin);
			jsonNode = mapper.readTree(respDto);
		} catch (Exception e) {

		}

		JsonNode identityNode = jsonNode.findPath(IDENTITY);
		Iterator<Entry<String, JsonNode>> fields = identityNode.fields();
		while (fields.hasNext()) {
			Entry<String, JsonNode> identityfields = fields.next();
			JsonNode identityFieldNode = identityfields.getValue();
			if (!identityFieldNode.isObject() && identityFieldNode.isValueNode()) {
				uinProperties.put(identityfields.getKey(), identityFieldNode.asText());
			} else {
				Iterator<JsonNode> elements = identityFieldNode.elements();
				while (elements.hasNext()) {
					JsonNode node = elements.next();
					String key = null;
					String value = null;
					if (node.isObject()) {
						Iterator<Entry<String, JsonNode>> nodefields = node.fields();
						while (nodefields.hasNext()) {
							Entry<String, JsonNode> nLang = nodefields.next();
							String nkey = nLang.getKey();
							JsonNode nvalue = nLang.getValue();

							if (nkey.equals("language") && nvalue.isTextual() && langCode.equals(nvalue.asText())) {
								key = identityfields.getKey();
							}
							if (key != null && "value".equals(nkey)) {
								value = nvalue.toString();
								uinProperties.put(key, value);
							}

						}

					}

				}
			}
		}
		System.out.println(uinProperties);
		if (uinProperties == null || uinProperties.isEmpty()) {
			throw new UinPropertiesNotFoundException(UinStatusErrorCode.UIN_PROPERTY_NOT_FOUND.getErrorCode(),
					UinStatusErrorCode.UIN_PROPERTY_NOT_FOUND.getErrorCode());
		}
		uinDetailResponseDto.setUinProperties(uinProperties);
		return uinDetailResponseDto;
	}
}
