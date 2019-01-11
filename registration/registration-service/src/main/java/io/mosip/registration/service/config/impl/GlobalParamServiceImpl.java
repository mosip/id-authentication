package io.mosip.registration.service.config.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.GlobalParamDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.GlobalParam;
import io.mosip.registration.entity.GlobalParamId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.impl.LoginServiceImpl;

/**
 * Class for implementing GlobalContextParam service
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Service
public class GlobalParamServiceImpl extends BaseService implements GlobalParamService {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(LoginServiceImpl.class);

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	/**
	 * Class to retrieve Global parameters of application
	 */
	@Autowired
	private GlobalParamDAO globalParamDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.GlobalParamService#getGlobalParams
	 */
	public Map<String, Object> getGlobalParams() {

		LOGGER.debug("REGISTRATION - GLOBALPARAMS - GLOBALPARAMSSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Fetching list of global params");

		auditFactory.audit(AuditEvent.LOGIN_MODES_FETCH, Components.LOGIN_MODES, "Fetching list of global params",
				"refId", "refIdType");

		return globalParamDAO.getGlobalParams();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.config.GlobalParamService#synchConfigData(
	 * String)
	 */
	@Override
	public ResponseDTO synchConfigData(String centerId) {
		LOGGER.debug("REGISTRATION - SYNCHCONFIGDATA - GLOBALPARAMSSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"config data synch is started");

		ResponseDTO responseDTO = new ResponseDTO();

		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.REGISTRATION_CENTER_ID, centerId);

		try {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> map = (HashMap<String, Object>) serviceDelegateUtil
					.get(RegistrationConstants.GET_GLOBAL_CONFIG, requestParamMap, true);
			HashMap<String, String> globalParamMap = new HashMap<>();
			parseToMap(map, globalParamMap);
			List<GlobalParam> list = new ArrayList<>();
			for (String key : globalParamMap.keySet()) {
				GlobalParam globalParam = new GlobalParam();
				GlobalParamId globalParamId = new GlobalParamId();
				globalParamId.setCode(UUID.randomUUID().toString());
				globalParamId.setLangCode("ENG");
				globalParam.setGlobalParamId(globalParamId);
				globalParam.setName(key);
				globalParam.setTyp("CONFIGURATION");
				globalParam.setIsActive(true);
				globalParam.setCrBy("brahma");
				globalParam.setCrDtime(Timestamp.valueOf(LocalDateTime.now()));
				globalParam.setVal(globalParamMap.get(key));
				list.add(globalParam);
			}
			globalParamDAO.saveAll(list);
			SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
			successResponseDTO.setCode(RegistrationConstants.POLICY_SYNC_SUCCESS_CODE);
			successResponseDTO.setMessage(RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE);
			successResponseDTO.setInfoType(RegistrationConstants.ALERT_INFORMATION);
			responseDTO.setSuccessResponseDTO(successResponseDTO);
			return responseDTO;

		} catch (HttpClientErrorException | SocketTimeoutException | RegBaseCheckedException | ClassCastException
				| ResourceAccessException exception) {
			responseDTO = buildErrorRespone(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_CODE,
					RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE);
			LOGGER.error("REGISTRATION_SYNCH_CONFIG_DATA", APPLICATION_NAME, APPLICATION_ID,
					"error response is created");
		}

		return responseDTO;
	}

	private ResponseDTO buildErrorRespone(ResponseDTO response, final String errorCode, final String message) {
		/* Create list of Error Response */
		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		/* Error response */
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(errorCode);
		errorResponse.setInfoType(RegistrationConstants.ALERT_ERROR);
		errorResponse.setMessage(message);
		errorResponses.add(errorResponse);

		/* Adding list of error responses to response */
		response.setErrorResponseDTOs(errorResponses);

		return response;
	}

	@SuppressWarnings("unchecked")
	private void parseToMap(HashMap<String, Object> map, HashMap<String, String> globalParamMap) {
		for (Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();

			if (entry.getValue() instanceof HashMap) {
				parseToMap((HashMap<String, Object>) entry.getValue(), globalParamMap);
			} else {
				globalParamMap.put(key, entry.getValue().toString());
			}
		}
	}
}
