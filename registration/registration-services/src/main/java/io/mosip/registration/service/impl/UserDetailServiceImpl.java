package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_MASTER_SYNC;
import static io.mosip.registration.constants.LoggerConstants.LOG_REG_USER_DETAIL;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.UserDetailResponseDto;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.UserDetailService;
import io.mosip.registration.service.UserOnboardService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

/**
 * Implementation for {@link UserDetailService}
 * 
 * @author chukka sreekar
 *
 */
@Service
public class UserDetailServiceImpl extends BaseService implements UserDetailService {

	@Autowired
	private UserDetailDAO userDetailDAO;

	@Autowired
	private UserOnboardService userOnboardService;

	/** Object for Logger. */
	private static final Logger LOGGER = AppConfig.getLogger(UserDetailServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.UserDetailService#save()
	 */
	@SuppressWarnings("unchecked")
	public synchronized ResponseDTO save(String triggerPoint) {

		ResponseDTO responseDTO = new ResponseDTO();
		ObjectMapper objectMapper = new ObjectMapper();

		LOGGER.info(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID, "Entering into user detail save method...");

		String regCenterId = RegistrationConstants.EMPTY;
		Map<String, String> mapOfcenterId = userOnboardService.getMachineCenterId();

		LOGGER.info(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID,
				"Fetching registration center details......");

		if (null != mapOfcenterId && mapOfcenterId.size() > 0) {
			regCenterId = mapOfcenterId.get(RegistrationConstants.USER_CENTER_ID);
		}

		LOGGER.info(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID, "Registration center id...." + regCenterId);

		if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
			try {

				Object userDetail = getUsrDetails(regCenterId, triggerPoint);

				LinkedHashMap<String, Object> userDetailSyncResponse = (LinkedHashMap<String, Object>) userDetail;

				if (null != userDetailSyncResponse.get(RegistrationConstants.PACKET_STATUS_READER_RESPONSE)) {

					String jsonString = new ObjectMapper().writeValueAsString(
							userDetailSyncResponse.get(RegistrationConstants.PACKET_STATUS_READER_RESPONSE));

					UserDetailResponseDto userDtlsSyncDto = objectMapper.readValue(jsonString,
							UserDetailResponseDto.class);

					if (!userDtlsSyncDto.getUserDetails().isEmpty()) {

						userDetailDAO.save(userDtlsSyncDto);
						responseDTO = setSuccessResponse(responseDTO, RegistrationConstants.SUCCESS, null);
						LOGGER.info(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID,
								"User Detail Sync SuccessFull......");
					} else {

						LOGGER.info(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID,
								"User Detail Sync Fail......");
						responseDTO = getErrorResponse(responseDTO, RegistrationConstants.ERROR, null);

					}

				} else {

					LOGGER.info(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID, "User Detail Sync Fail......");
					responseDTO = getErrorResponse(responseDTO, RegistrationConstants.ERROR, null);
				}

			} catch (RegBaseCheckedException | IOException exRegBaseCheckedException) {
				LOGGER.error(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID,
						exRegBaseCheckedException.getMessage()
								+ ExceptionUtils.getStackTrace(exRegBaseCheckedException));
				responseDTO = getErrorResponse(responseDTO, RegistrationConstants.ERROR, null);
			}
		} else {
			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					" Unable to sync user detail data as there is no internet connection");
			responseDTO = getErrorResponse(responseDTO, RegistrationConstants.ERROR, null);
		}

		LOGGER.info(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID, "Leaving into user detail save method");

		return responseDTO;

	}

	/**
	 * Gets the usr details.
	 *
	 * @param regCentrId the reg centr id
	 * @return the usr details
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	@SuppressWarnings("unchecked")
	private Object getUsrDetails(String regCentrId, String triggerPoint) throws RegBaseCheckedException {

		LOGGER.info(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID,
				"Entering into user detail rest calling method");

		ResponseDTO responseDTO = new ResponseDTO();
		List<ErrorResponseDTO> erResponseDTOs = new ArrayList<>();
		Object userDetail = null;

		// Setting uri Variables

		Map<String, String> requestParamMap = new LinkedHashMap<>();
		requestParamMap.put(RegistrationConstants.REG_ID, regCentrId);

		try {

			userDetail = serviceDelegateUtil.get(RegistrationConstants.USER_DETAILS_SERVICE_NAME, requestParamMap, true,
					triggerPoint);

			LinkedHashMap<String, Object> userDetailResponse = (LinkedHashMap<String, Object>) userDetail;

			if (null != userDetailResponse.get(RegistrationConstants.PACKET_STATUS_READER_RESPONSE)) {

				SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
				successResponseDTO.setCode(RegistrationConstants.SUCCESS);
				responseDTO.setSuccessResponseDTO(successResponseDTO);

			} else {

				ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
				errorResponseDTO.setCode(RegistrationConstants.ERRORS);
				errorResponseDTO.setMessage(
						((List<LinkedHashMap<String, String>>) userDetailResponse.get(RegistrationConstants.ERRORS))
								.get(0).get(RegistrationConstants.ERROR_MSG));
				erResponseDTOs.add(errorResponseDTO);
				responseDTO.setErrorResponseDTOs(erResponseDTOs);
			}

		} catch (HttpClientErrorException httpClientErrorException) {
			LOGGER.error(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID,
					httpClientErrorException.getRawStatusCode() + "Http error while pulling json from server"
							+ ExceptionUtils.getStackTrace(httpClientErrorException));
			throw new RegBaseCheckedException(Integer.toString(httpClientErrorException.getRawStatusCode()),
					httpClientErrorException.getStatusText());
		} catch (SocketTimeoutException socketTimeoutException) {
			LOGGER.error(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID,
					socketTimeoutException.getMessage() + "Http error while pulling json from server"
							+ ExceptionUtils.getStackTrace(socketTimeoutException));
			throw new RegBaseCheckedException(socketTimeoutException.getMessage(),
					socketTimeoutException.getLocalizedMessage());
		}

		LOGGER.info(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID,
				"Leaving into user detail rest calling method");

		return userDetail;
	}

}
