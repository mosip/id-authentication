package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegCenterMachineUserReqDto;
import io.mosip.registration.dto.RegistrationCenterUserMachineMappingDto;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.UserMachineMappingService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;
/**
 * 
 * @author Brahmananda Reddy
 *
 */
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
		List<RegistrationCenterUserMachineMappingDto> list = new ArrayList<>();
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
				RegCenterMachineUserReqDto<RegistrationCenterUserMachineMappingDto> regCenterMachineUserReqDto = new RegCenterMachineUserReqDto<>();
				regCenterMachineUserReqDto.setId("REGISTRATION");
				regCenterMachineUserReqDto.setTimestamp(LocalDateTime.now());
				for (UserMachineMapping userMachineMapping : userMachineMappingList) {
					RegistrationCenterUserMachineMappingDto registrationCenterUserMachineMappingDto = new RegistrationCenterUserMachineMappingDto();
					registrationCenterUserMachineMappingDto.setCntrId(centerId);
					registrationCenterUserMachineMappingDto.setMachineId(machineId);
					registrationCenterUserMachineMappingDto.setActive(true);
					registrationCenterUserMachineMappingDto.setUsrId(userMachineMapping.getUserDetail().getId());
					list.add(registrationCenterUserMachineMappingDto);
				}
				regCenterMachineUserReqDto.setVer("0.08");
				regCenterMachineUserReqDto.setRequest(list);
				serviceDelegateUtil.post("user_machine_mapping", regCenterMachineUserReqDto);
				successResponseDTO.setCode(RegistrationConstants.POLICY_SYNC_SUCCESS_CODE);
				successResponseDTO.setMessage(RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE);
				successResponseDTO.setInfoType(RegistrationConstants.ALERT_INFORMATION);
				responseDTO.setSuccessResponseDTO(successResponseDTO);

				LOGGER.debug("REGISTRATION-ONBOARDED-USER-DETAILS- SYNC", APPLICATION_NAME, APPLICATION_ID,
						"sync user details is ended");
			} catch (HttpClientErrorException | ResourceAccessException | SocketTimeoutException
					| RegBaseCheckedException | RegBaseUncheckedException exception) {
				exception.printStackTrace();
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
