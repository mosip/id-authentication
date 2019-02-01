package io.mosip.registration.service.impl;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.google.gson.Gson;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.UserMachineMappingService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@Service
public class UserMachineMappingServiceImpl implements UserMachineMappingService {
	@Autowired
	private ServiceDelegateUtil serviceDelegateUtil;
	@Autowired
	private BaseService baseService;
	@Autowired
	private MachineMappingDAO machineMappingDAO;

	private static final Logger LOGGER = AppConfig.getLogger(UserMachineMappingServiceImpl.class);

	public ResponseDTO syncUserDetails() {
		LOGGER.debug("REGISTRATION-ONBOARDED-USER-DETAILS- SYNC", APPLICATION_NAME, APPLICATION_ID,
				"sync user details is started");

		String systemMacId = null;
		String machineId = null;
		String centerId = null;
		List<UserMachineMapping> userMachineMappingList = null;
		ResponseDTO responseDTO = new ResponseDTO();
		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		if (!RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
			responseDTO = buildErrorRespone(responseDTO, RegistrationConstants.POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_CODE,
					RegistrationConstants.POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_MESSAGE);
		} else {

		try {

			systemMacId = baseService.getMacAddress();
			machineId = baseService.getStationId(systemMacId);
			centerId = baseService.getCenterId(machineId);
			
			userMachineMappingList = machineMappingDAO.getUserMappingDetails(machineId);
			Map<String, Object> jsonMap = new HashMap<>();
			jsonMap.put("id", "REGISTRATION");

			List<Map<String, Object>> userDetailsList = new ArrayList();
			for (UserMachineMapping userMachineMapping : userMachineMappingList) {
				Map<String, Object> map = new HashMap<>();
				map.put("cntrId", centerId);
				map.put("isActive", userMachineMapping.getUserDetail().getIsActive());
				map.put("machineId", machineId);
				map.put("userId", userMachineMapping.getUserDetail().getId());
				userDetailsList.add(map);
			}
			jsonMap.put("request", userDetailsList);
			jsonMap.put("timeStamp", Timestamp.valueOf(LocalDateTime.now()));
			jsonMap.put("ver", "0.08");
			Gson gson = new Gson();
			String json = gson.toJson(jsonMap);
			
			serviceDelegateUtil.post("user_machine_mapping", json);
			successResponseDTO.setCode(RegistrationConstants.POLICY_SYNC_SUCCESS_CODE);
			successResponseDTO.setMessage(RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE);
			successResponseDTO.setInfoType(RegistrationConstants.ALERT_INFORMATION);
			responseDTO.setSuccessResponseDTO(successResponseDTO);

			LOGGER.debug("REGISTRATION-ONBOARDED-USER-DETAILS- SYNC", APPLICATION_NAME, APPLICATION_ID,
					"sync user details is ended");
		} catch (HttpClientErrorException | ResourceAccessException | SocketTimeoutException
				| RegBaseCheckedException|RegBaseUncheckedException exception) {
			LOGGER.error("REGISTRATION-ONBOARDED-USER-DETAILS- SYNC", APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage());
			responseDTO = buildErrorRespone(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_CODE,
					RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE);
		
		}
		}

		return responseDTO;
	}

	private ResponseDTO buildErrorRespone(ResponseDTO response, final String errorCode, final String message) {
		/* Create list of Error Response */
		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		/* Error response */
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(errorCode);
		errorResponse.setInfoType(RegistrationConstants.ERROR);
		errorResponse.setMessage(message);
		errorResponses.add(errorResponse);

		/* Adding list of error responses to response */
		response.setErrorResponseDTOs(errorResponses);

		return response;
	}

}
