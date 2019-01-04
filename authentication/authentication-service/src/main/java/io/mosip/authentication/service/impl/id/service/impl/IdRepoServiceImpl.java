package io.mosip.authentication.service.impl.id.service.impl;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;

/**
 * 
 * @author Dinesh Karuppiah.T
 * @author Rakesh Roshan
 */

@Service
public class IdRepoServiceImpl implements IdRepoService {

	private static Logger logger = IdaLogger.getLogger(OTPManager.class);

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String SESSION_ID = "sessionId";

	/** The Constant AUTH_FACADE. */
	private static final String ID_REPO_SERVICE = "IDA - IdRepoService";

	@Autowired
	private RestHelper restHelper;

	@Autowired
	private RestRequestFactory restRequestFactory;

	public Map<String, Object> getIdRepo(String uin) throws IdAuthenticationBusinessException {

		RestRequestDTO buildRequest = null;
		Map<String, Object> response = null;

		try {
			buildRequest = restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE, null, Map.class);
			Map<String, String> params = new HashMap<>();
			params.put("uin", uin);
			buildRequest.setPathVariables(params);
			response = restHelper.requestSync(buildRequest);
			response.put("uin", uin);
		} catch (RestServiceException e) {
			logger.error(SESSION_ID, ID_REPO_SERVICE, e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		} catch (IDDataValidationException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<IdentityInfoDTO>> getIdInfo(Map<String, Object> idResponseDTO)
			throws IdAuthenticationBusinessException {

		return idResponseDTO.entrySet().stream()
				.filter(entry -> entry.getKey().equals("response") && entry.getValue() instanceof Map)
				.flatMap(entry -> ((Map<String, Object>) entry.getValue()).entrySet().stream()).flatMap(entry -> {
					if (entry.getKey().equals("identity") && entry.getValue() instanceof Map) {
						return getIdentityValues((Map<String, Object>) entry.getValue()).entrySet().stream();
					} else if (entry.getKey().equals("documents") && entry.getValue() instanceof List) {
						return (getDocumentValues((List<Map<String, Object>>) entry.getValue())).entrySet().stream();
					} else {
						return Stream.empty();
					}
				}).collect(Collectors.toMap(Entry<String, Object>::getKey, entry -> {
					Object val = entry.getValue();
					if (val instanceof List) {
						List<Map> arrayList = (List) val;
						return arrayList.stream().filter(elem -> elem instanceof Map)
								.map(elem -> (Map<String, Object>) elem).map(map1 -> {
									IdentityInfoDTO idInfo = new IdentityInfoDTO();
									idInfo.setLanguage(String.valueOf(map1.get("language")));
									idInfo.setValue(String.valueOf(map1.get("value")));
									return idInfo;
								}).collect(Collectors.toList());

					}
					return Collections.emptyList();
				}));

	}

	private Map<String, Object> getIdentityValues(Map<String, Object> map) {
		return map.entrySet().stream().filter(entry -> entry.getValue() instanceof Map)
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> {
					List<Map<String, Object>> idValuesList = ((Map<String, Object>) entry.getValue()).entrySet()
							.stream()
							.filter(entry1 -> "values".equals(entry1.getKey()) && entry1.getValue() instanceof List)
							.flatMap(entry1 -> ((List<Map<String, Object>>) entry1.getValue()).stream())
							.collect(Collectors.toList());

					return idValuesList;
				}));

	}

	private Map<String, Object> getDocumentValues(List<Map<String, Object>> value) {
		return value.stream().filter(map -> "individualBiometrics".equals(map.get("docType")))
				.flatMap(map -> map.entrySet().stream()).filter(entry -> entry.getKey().equalsIgnoreCase("docValue"))
				.map(entry -> (Map<String, Object>) decodeToMap(entry.getValue()))
				.flatMap(map -> map.entrySet().stream())
				.collect(Collectors.toMap(Entry<String, Object>::getKey, entry -> {
					Object val = entry.getValue();
					if (val instanceof List) {
						return (List<Map<String, Object>>) (List) val;

					}
					return Collections.emptyList();
				}));
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> decodeToMap(Object value) {
		if (value instanceof String) {
			try {
				byte[] decodeBase64 = CryptoUtil.decodeBase64((String) value);
				ObjectMapper mapper = new ObjectMapper();
				return mapper.readValue(decodeBase64, Map.class);
			} catch (IOException e) {
				logger.error(SESSION_ID, ID_REPO_SERVICE, e.getMessage(), e.getLocalizedMessage());
				return Collections.emptyMap();
			}
		} else {
			return Collections.emptyMap();
		}

	}

}
