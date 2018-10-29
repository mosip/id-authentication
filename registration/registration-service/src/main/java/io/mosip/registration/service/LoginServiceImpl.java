package io.mosip.registration.service;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.exception.MosipIOException;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.MosipJsonMappingException;
import io.mosip.kernel.core.util.exception.MosipJsonParseException;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.dao.RegistrationAppLoginDAO;
import io.mosip.registration.dao.RegistrationCenterDAO;
import io.mosip.registration.dao.RegistrationUserDetailDAO;
import io.mosip.registration.dao.RegistrationUserPasswordDAO;
import io.mosip.registration.dao.RegistrationUserRoleDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.OtpGeneratorRequestDto;
import io.mosip.registration.dto.OtpGeneratorResponseDto;
import io.mosip.registration.dto.OtpValidatorResponseDto;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

/**
 * Class for implementing login service
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Service
public class LoginServiceImpl implements LoginService {

	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * serviceDelegateUtil which processes the HTTPRequestDTO requests
	 */
	@Autowired
	private ServiceDelegateUtil serviceDelegateUtil;

	/**
	 * Class to retrieve the Login Details from DB
	 */
	@Autowired
	private RegistrationAppLoginDAO registrationAppLoginDAO;

	/**
	 * Class to retrieve the Registration Officer Credentials from DB
	 */
	@Autowired
	private RegistrationUserPasswordDAO registrationUserPasswordDAO;

	/**
	 * Class to retrieve the Registration Officer Details from DB
	 */
	@Autowired
	private RegistrationUserDetailDAO registrationUserDetailDAO;

	/**
	 * Class to retrieve the Registration Center details from DB
	 */
	@Autowired
	private RegistrationCenterDAO registrationCenterDAO;

	/**
	 * Class to retrieve the Registration Officer roles from DB
	 */
	@Autowired
	private RegistrationUserRoleDAO registrationUserRoleDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.service.login.LoginService#getModesOfLogin()
	 */
	@Override
	public Map<String, Object> getModesOfLogin() {
		// Retrieve Login information
		return registrationAppLoginDAO.getModesOfLogin();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.service.login.LoginService#validateUserPassword(
	 * java.lang.String,java.lang.String)
	 */
	@Override
	public boolean validateUserPassword(String userId, String hashPassword) {
		// Validating Registration Officer Credentials
		if (hashPassword.equals(registrationUserPasswordDAO.getPassword(userId, hashPassword))) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.service.login.LoginService#getUserDetail(java.lang.
	 * String)
	 */
	@Override
	public Map<String, String> getUserDetail(String userId) {
		// Retrieving Registration Officer details
		return registrationUserDetailDAO.getUserDetail(userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.service.login.LoginService#getCenterName(java.lang.
	 * String)
	 */
	@Override
	public String getCenterName(String centerId) {
		// Retrieving Registration Center name
		return registrationCenterDAO.getCenterName(centerId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.service.login.LoginService#
	 * getRegistrationCenterDetails(java.lang.String)
	 */
	@Override
	public RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId) {
		// Retrieving Registration Center details
		return registrationCenterDAO.getRegistrationCenterDetails(centerId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.service.login.LoginService#getRoles(java.lang.String)
	 */
	@Override
	public List<String> getRoles(String userId) {
		// Retrieving User roles
		return registrationUserRoleDAO.getRoles(userId);
	}

	public String getBlockedUserCheck(String userId) {
		return registrationUserDetailDAO.getBlockedUserCheck(userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.service.LoginService#getOTP(java.lang.String)
	 */
	@Override
	public ResponseDTO getOTP(final String key) {
		LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Get OTP method called");

		// Create Response to return to UI layer
		ResponseDTO response = new ResponseDTO();
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		OtpGeneratorResponseDto otpGeneratorResponseDto = new OtpGeneratorResponseDto();

		List<ErrorResponseDTO> errorResponses = null;
		SuccessResponseDTO successResponse = null;

		// prepare otpGeneratorRequestDto with specified key(EO Username) obtained from
		// UI
		otpGeneratorRequestDto.setKey(key);

		try {

			// obtain otpGeneratorResponseDto from serviceDelegateUtil
			otpGeneratorResponseDto = (OtpGeneratorResponseDto) serviceDelegateUtil
					.post(RegConstants.OTP_GENERATOR_SERVICE_NAME, otpGeneratorRequestDto);
		} catch (RegBaseCheckedException e) {

			// create Error Response
			response = getErrorResponse(response, RegConstants.OTP_GENERATION_ERROR_MESSAGE);
			LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Error Response created");

		} catch (HttpClientErrorException httpClientErrorException) {
			try {
				// obtain otpGeneratorResponseDto from JsonUtil
				otpGeneratorResponseDto = (OtpGeneratorResponseDto) JsonUtils.jsonStringToJavaObject(
						OtpGeneratorResponseDto.class, httpClientErrorException.getResponseBodyAsString());
				LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME),
						getPropertyValue(APPLICATION_ID), "JSON conversion completed");

			} catch (MosipJsonParseException  | MosipJsonMappingException | MosipIOException exception) {
				// create Error Response
				response = getErrorResponse(response, RegConstants.OTP_GENERATION_ERROR_MESSAGE);
				LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME),
						getPropertyValue(APPLICATION_ID), "Error Response created");
			} 
		}

		if (otpGeneratorResponseDto != null) {
			// create Success Response
			successResponse = new SuccessResponseDTO();
			successResponse.setCode(RegConstants.ALERT_INFORMATION);
			successResponse.setMessage(RegConstants.OTP_GENERATION_SUCCESS_MESSAGE + otpGeneratorResponseDto.getOtp());

			Map<String, Object> otherAttributes = new HashMap<String, Object>();
			otherAttributes.put(RegConstants.OTP_GENERATOR_RESPONSE_DTO, otpGeneratorResponseDto);

			successResponse.setOtherAttributes(otherAttributes);
			response.setSuccessResponseDTO(successResponse);
			LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Success Response created");

		} else {
			// create Error Response
			response = getErrorResponse(response, RegConstants.OTP_GENERATION_ERROR_MESSAGE);
			LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Error Response created");

		}
		LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Get OTP method ended");

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.service.LoginService#validateOTP(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ResponseDTO validateOTP(final String key, final String otp) {

		LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Validation of OTP called");

		// Create Response to Return to UI layer
		ResponseDTO response = new ResponseDTO();
		List<ErrorResponseDTO> errorResponses = null;
		SuccessResponseDTO successResponse;
		OtpValidatorResponseDto otpValidatorResponseDto = null;

		// Validator response service api creation
		final String SERVICE_NAME = RegConstants.OTP_VALIDATOR_SERVICE_NAME;

		// prepare request params to pass through URI
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegConstants.USERNAME_KEY, key);
		requestParamMap.put(RegConstants.OTP_GENERATED, otp);

		try {
			try {
				// Obtain otpValidatorResponseDto from service delegate util
				otpValidatorResponseDto = (OtpValidatorResponseDto) serviceDelegateUtil.get(SERVICE_NAME,
						requestParamMap);
			} catch (RegBaseCheckedException e) {
				// Create Error response
				response = getErrorResponse(response, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);
				LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME),
						getPropertyValue(APPLICATION_ID), "Error Response Created");

			}
		} catch (HttpClientErrorException httpClientErrorException) {
			try {
				// obtain otpValidatorResponseDto through JSON Util
				otpValidatorResponseDto = (OtpValidatorResponseDto) JsonUtils.jsonStringToJavaObject(
						OtpValidatorResponseDto.class, httpClientErrorException.getResponseBodyAsString());
			} catch (MosipJsonParseException e) {

				// Create Error response
				response = getErrorResponse(response, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);
				LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME),
						getPropertyValue(APPLICATION_ID), "Error Response Created");

			} catch (MosipJsonMappingException e) {
				// Create Error response
				response = getErrorResponse(response, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);
				LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME),
						getPropertyValue(APPLICATION_ID), "Error Response Created");

			} catch (MosipIOException e) {
				// Create Error response
				response = getErrorResponse(response, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);
				LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME),
						getPropertyValue(APPLICATION_ID), "Error Response Created");

			}
			// Create Error response
			response = getErrorResponse(response, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);
			LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Error Response Created");

		}
		if (otpValidatorResponseDto != null) {
			if (otpValidatorResponseDto.getStatus() != null) {
				if (otpValidatorResponseDto.getStatus().equalsIgnoreCase("true")) {

					// Create Success Response
					successResponse = new SuccessResponseDTO();
					successResponse.setCode(RegConstants.ALERT_INFORMATION);
					successResponse.setMessage(RegConstants.OTP_VALIDATION_SUCCESS_MESSAGE);
					Map<String, Object> otherAttributes = new HashMap<String, Object>();
					otherAttributes.put(RegConstants.OTP_VALIDATOR_RESPONSE_DTO, otpValidatorResponseDto);
					successResponse.setOtherAttributes(otherAttributes);
					response.setSuccessResponseDTO(successResponse);
					LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME),
							getPropertyValue(APPLICATION_ID), "Success Response Created");

				} else {

					// Create Error response
					response = getErrorResponse(response, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);
					LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME),
							getPropertyValue(APPLICATION_ID), "Error Response Created");

				}
			} else {

				// Create Error response
				response = getErrorResponse(response, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);
				LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME),
						getPropertyValue(APPLICATION_ID), "Error Response Created");

			}
		} else {

			// Create Error response
			response = getErrorResponse(response, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);
			LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Error Response Created");

		}

		LOGGER.debug("REGISTRATION - LOGIN - OTP", getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Validation of OTP ended");

		return response;

	}

	private ResponseDTO getErrorResponse(ResponseDTO response, final String message) {
		// Create list of Error Response
		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<ErrorResponseDTO>();

		// Error response
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();

		errorResponse.setCode(RegConstants.ALERT_ERROR);
		errorResponse.setMessage(message);
		Map<String, Object> otherAttributes = new HashMap<String, Object>();
		otherAttributes.put(RegConstants.OTP_VALIDATOR_RESPONSE_DTO, null);

		errorResponses.add(errorResponse);

		// Assing list of error responses to response
		response.setErrorResponseDTOs(errorResponses);
		return response;

	}
}
