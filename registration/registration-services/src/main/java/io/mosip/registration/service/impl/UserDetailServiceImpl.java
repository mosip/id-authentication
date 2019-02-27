package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_USER_DETAIL;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.UserDetailResponseDto;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.UserDetailService;
import io.mosip.registration.service.UserOnboardService;

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
	public ResponseDTO save() {

		ResponseDTO responseDTO = new ResponseDTO();

		LOGGER.info(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID, "Entering into user detail save method...");

		String regCenterId = RegistrationConstants.EMPTY;
		Map<String, String> mapOfcenterId = userOnboardService.getMachineCenterId();

		LOGGER.info(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID,
				"Fetching registration center details......");

		if (null != mapOfcenterId && mapOfcenterId.size() > 0) {
			regCenterId = mapOfcenterId.get(RegistrationConstants.USER_CENTER_ID);
		}

		LOGGER.info(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID, "Registration center id...." + regCenterId);

		try {

			UserDetailResponseDto userDetail = getUsrDetails(regCenterId);
			userDetailDAO.save(userDetail);
			responseDTO = setSuccessResponse(responseDTO, RegistrationConstants.SUCCESS, null);

		} catch (RegBaseCheckedException exRegBaseCheckedException) {
			LOGGER.error(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID,
					exRegBaseCheckedException.getMessage() + ExceptionUtils.getStackTrace(exRegBaseCheckedException));
			responseDTO = getErrorResponse(responseDTO, RegistrationConstants.ERROR);
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
	private UserDetailResponseDto getUsrDetails(String regCentrId) throws RegBaseCheckedException {

		LOGGER.info(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID,
				"Entering into user detail rest calling method");

		UserDetailResponseDto response = null;

		// Setting uri Variables

		Map<String, String> requestParamMap = new LinkedHashMap<>();
		requestParamMap.put(RegistrationConstants.REG_ID, regCentrId);

		try {
			response = (UserDetailResponseDto) serviceDelegateUtil.get(RegistrationConstants.USER_DETAILS_SERVICE_NAME,
					requestParamMap, true);
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

		return response;
	}

}
