package io.mosip.registration.service.operator.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_MASTER_SYNC;
import static io.mosip.registration.constants.LoggerConstants.LOG_REG_USER_SALT_SYNC;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.repositories.UserDetailRepository;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.operator.UserSaltDetailsService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

@Service
public class UserSaltDetailsServiceImpl extends BaseService implements UserSaltDetailsService {

	@Autowired
	private UserDetailRepository userDetailRepository;

	/** Object for Logger. */
	private static final Logger LOGGER = AppConfig.getLogger(UserSaltDetailsServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.UserSaltDetailsService#getUserSaltDetails(java.
	 * lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResponseDTO getUserSaltDetails(String trigger) throws RegBaseCheckedException {

		ResponseDTO responseDTO = new ResponseDTO();

		if (triggerPointNullCheck(trigger)) {

			try {

				LinkedHashMap<String, Object> userSaltMap = (LinkedHashMap<String, Object>) saltService(trigger);

				if (null != userSaltMap && !userSaltMap.isEmpty()
						&& null != userSaltMap.get(RegistrationConstants.PACKET_STATUS_READER_RESPONSE)) {

					LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) userSaltMap
							.get(RegistrationConstants.PACKET_STATUS_READER_RESPONSE);

					LOGGER.info(LOG_REG_USER_SALT_SYNC, APPLICATION_NAME, APPLICATION_ID,
							"Getting User Salt Details......");

					List<LinkedHashMap<String, Object>> saltMap = (List<LinkedHashMap<String, Object>>) responseMap
							.get("mosipUserSaltList");

					LOGGER.info(LOG_REG_USER_SALT_SYNC, APPLICATION_NAME, APPLICATION_ID,
							"salt map size : ===> " + Integer.toString(saltMap.size()));

					List<UserDetail> userDtlsSyncDto = userDetailRepository.findByIsActiveTrue();

					if (!userDtlsSyncDto.isEmpty()) {
						userDtlsSyncDto.forEach(usrDtl -> {

							Optional<LinkedHashMap<String, Object>> filteredMap = saltMap.stream().filter(
									map1 -> map1.get("userId") != null && map1.get("userId").equals(usrDtl.getId()))
									.findAny();
							if (filteredMap.isPresent()) {
								LOGGER.info(LOG_REG_USER_SALT_SYNC, APPLICATION_NAME, APPLICATION_ID,
										"Added Salt to user details successfully.....");
								usrDtl.setSalt((String) filteredMap.get().get("salt"));
							}

						});
						userDetailRepository.saveAll(userDtlsSyncDto);
						LOGGER.info(LOG_REG_USER_SALT_SYNC, APPLICATION_NAME, APPLICATION_ID,
								"user Salt Details saved succesful.....");
						setSuccessResponse(responseDTO, RegistrationConstants.SUCCESS, null);
					} else {
						LOGGER.info(LOG_REG_USER_SALT_SYNC, APPLICATION_NAME, APPLICATION_ID,
								"user details is either empty or table have no data");
						setErrorResponse(responseDTO, RegistrationConstants.ERROR, null);
					}
				} else {
					LOGGER.info(LOG_REG_USER_SALT_SYNC, APPLICATION_NAME, APPLICATION_ID,
							RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);
					setErrorResponse(responseDTO, RegistrationConstants.ERROR, null);
				}
			} catch (RegBaseCheckedException regBaseCheckedException) {
				LOGGER.error(LOG_REG_USER_SALT_SYNC, APPLICATION_NAME, APPLICATION_ID,
						ExceptionUtils.getStackTrace(regBaseCheckedException));

				setErrorResponse(responseDTO, RegistrationConstants.ERROR, null);
			}
		} else {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, RegistrationConstants.TRIGGER_POINT_MSG);
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_TRIGGER_POINT_MISSING.getErrorCode(),
					RegistrationExceptionConstants.REG_TRIGGER_POINT_MISSING.getErrorMessage());
		}
		return responseDTO;

	}

	/**
	 * Salt service.
	 *
	 * @param trigger the trigger
	 * @return the map
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.UserSaltDetailsService#getUserSaltDetails(java.
	 * lang.String)
	 */
	@SuppressWarnings("unchecked")

	private Map<String, Object> saltService(String trigger) throws RegBaseCheckedException {

		ResponseDTO responseDTO = new ResponseDTO();
		LinkedHashMap<String, Object> userSaltDetailsSyncResponse = null;
		Map<String, String> requestParamMap = new LinkedHashMap<>();
		requestParamMap.put("appid", "registrationclient");

		try {

			if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
				userSaltDetailsSyncResponse = (LinkedHashMap<String, Object>) serviceDelegateUtil
						.get(RegistrationConstants.USER_DETAIL_SALT_SERVICE_NAME, requestParamMap, true, trigger);
				if (null != userSaltDetailsSyncResponse.get(RegistrationConstants.PACKET_STATUS_READER_RESPONSE)) {
					LOGGER.info(LOG_REG_USER_SALT_SYNC, APPLICATION_NAME, APPLICATION_ID,
							"User Salt Detail Service Sync Successful..");
					setSuccessResponse(responseDTO, RegistrationConstants.SUCCESS, null);
				} else {
					LOGGER.info(LOG_REG_USER_SALT_SYNC, APPLICATION_NAME, APPLICATION_ID,
							userSaltDetailsSyncResponse.size() > 0
									? ((List<LinkedHashMap<String, String>>) userSaltDetailsSyncResponse
											.get(RegistrationConstants.ERRORS)).get(0)
													.get(RegistrationConstants.ERROR_MSG)
									: "User salt Detail Sync Restful service error");
					setErrorResponse(responseDTO, userSaltDetailsSyncResponse.size() > 0
							? ((List<LinkedHashMap<String, String>>) userSaltDetailsSyncResponse
									.get(RegistrationConstants.ERRORS)).get(0).get(RegistrationConstants.ERROR_MSG)
							: "User salt Detail Sync Restful service error", null);
				}
			} else {
				LOGGER.error(LOG_REG_USER_SALT_SYNC, APPLICATION_NAME, APPLICATION_ID,
						"Unable to sync user salt data as there is no internet connection");
				setErrorResponse(responseDTO, RegistrationConstants.ERROR, null);
			}
		} catch (HttpClientErrorException | SocketTimeoutException | RegBaseCheckedException socketTimeoutException) {
			LOGGER.error(LOG_REG_USER_SALT_SYNC, APPLICATION_NAME, APPLICATION_ID,
					socketTimeoutException.getMessage() + "Http error while pulling json from server"
							+ ExceptionUtils.getStackTrace(socketTimeoutException));
			throw new RegBaseCheckedException(socketTimeoutException.getMessage(),
					socketTimeoutException.getLocalizedMessage());
		}

		return userSaltDetailsSyncResponse;

	}

	/**
	 * trigger point null check.
	 *
	 * @param triggerPoint the language code
	 * @return true, if successful
	 */
	private boolean triggerPointNullCheck(String triggerPoint) {
		if (StringUtils.isEmpty(triggerPoint)) {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"triggerPoint is missing it is a mandatory field.");
			return false;
		} else {
			return true;
		}

	}

}
