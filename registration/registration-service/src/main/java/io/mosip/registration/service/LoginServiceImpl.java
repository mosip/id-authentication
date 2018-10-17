package io.mosip.registration.service;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.MosipIOException;
import io.mosip.kernel.core.util.exception.MosipJsonMappingException;
import io.mosip.kernel.core.util.exception.MosipJsonParseException;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModuleEnum;
import io.mosip.registration.constants.AuditEventEnum;
import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.dao.RegistrationAppLoginDAO;
import io.mosip.registration.dao.RegistrationCenterDAO;
import io.mosip.registration.dao.RegistrationScreenAuthorizationDAO;
import io.mosip.registration.dao.RegistrationUserDetailDAO;
import io.mosip.registration.dao.RegistrationUserPasswordDAO;
import io.mosip.registration.dto.AuthorizationDTO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.OtpGeneratorRequestDto;
import io.mosip.registration.dto.OtpGeneratorResponseDto;
import io.mosip.registration.dto.OtpValidatorResponseDto;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.RegistrationUserDetail;
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

	/**
	 * Instance of LOGGER
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

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
	 * Class to retrieve the Registration screen authorization from DB
	 */
	@Autowired
	private RegistrationScreenAuthorizationDAO registrationScreenAuthorizationDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.service.login.LoginService#getModesOfLogin()
	 */
	@Override
	public Map<String, Object> getModesOfLogin() {
		// Retrieve Login information

		LOGGER.debug("REGISTRATION - LOGINMODES - LOGINSERVICE", APPLICATION_NAME,
				APPLICATION_ID, "Fetching list of login modes");

		auditFactory.audit(AuditEventEnum.LOGIN_MODES_FETCH, AppModuleEnum.LOGIN_MODES, "Fetching list of login modes",
				"refId", "refIdType");

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

		LOGGER.debug("REGISTRATION - VALIDATECREDENTIALS - LOGINSERVICE", APPLICATION_NAME,
				APPLICATION_ID, "Validating User credentials");

		auditFactory.audit(AuditEventEnum.VALIDATE_USER_CRED, AppModuleEnum.VALIDATE_USER,
				"Validating User credentials", "refId", "refIdType");

		return registrationUserPasswordDAO.getPassword(userId, hashPassword);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.service.login.LoginService#getUserDetail(java.lang.
	 * String)
	 */
	@Override
	public RegistrationUserDetail getUserDetail(String userId) {
		// Retrieving Registration Officer details

		LOGGER.debug("REGISTRATION - USERDETAIL - LOGINSERVICE", APPLICATION_NAME,
				APPLICATION_ID, "Fetching User details");

		auditFactory.audit(AuditEventEnum.FETCH_USR_DET, AppModuleEnum.USER_DETAIL, "Fetching User details", "refId",
				"refIdType");

		return registrationUserDetailDAO.getUserDetail(userId);
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

		LOGGER.debug("REGISTRATION - CENTERDETAILS - LOGINSERVICE", APPLICATION_NAME,
				APPLICATION_ID, "Fetching Center details");

		auditFactory.audit(AuditEventEnum.FETCH_CNTR_DET, AppModuleEnum.CENTER_DETAIL, "Fetching Center details",
				"refId", "refIdType");

		return registrationCenterDAO.getRegistrationCenterDetails(centerId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.service.login.LoginService#
	 * getScreenAuthorizationDetails(java.lang.String)
	 */
	@Override
	public AuthorizationDTO getScreenAuthorizationDetails(String roleCode) {
		// Fetching screen authorization details

		LOGGER.debug("REGISTRATION - SCREENAUTHORIZATION - LOGINSERVICE", APPLICATION_NAME,
				APPLICATION_ID, "Fetching list of Screens to be Authorized");

		auditFactory.audit(AuditEventEnum.FETCH_SCR_AUTH, AppModuleEnum.SCREEN_AUTH,
				"Fetching list of Screens to be Authorized", "refId", "refIdType");

		return registrationScreenAuthorizationDAO.getScreenAuthorizationDetails(roleCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.service.LoginService#getOTP(java.lang.String)
	 */
	@Override
	public ResponseDTO getOTP(final String key) {
		LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME, APPLICATION_ID,
				"Get OTP method called");

		// Create Response to return to UI layer
		ResponseDTO response = new ResponseDTO();
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		OtpGeneratorResponseDto otpGeneratorResponseDto = new OtpGeneratorResponseDto();

		SuccessResponseDTO successResponse = null;

		// prepare otpGeneratorRequestDto with specified key(EO Username) obtained from
		// UI
		otpGeneratorRequestDto.setKey(key);

		try {
			try {

				// obtain otpGeneratorResponseDto from serviceDelegateUtil
				otpGeneratorResponseDto = (OtpGeneratorResponseDto) serviceDelegateUtil
						.post(RegConstants.OTP_GENERATOR_SERVICE_NAME, otpGeneratorRequestDto);
			} catch (RegBaseCheckedException e) {

				// create Error Response
				response = getErrorResponse(response, RegConstants.OTP_GENERATION_ERROR_MESSAGE);
				LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
						APPLICATION_ID, "Error Response created");

			}
		} catch (HttpClientErrorException httpClientErrorException) {
			try {
				// obtain otpGeneratorResponseDto from JsonUtil
				otpGeneratorResponseDto = (OtpGeneratorResponseDto) JsonUtils.jsonStringToJavaObject(
						OtpGeneratorResponseDto.class, httpClientErrorException.getResponseBodyAsString());
				LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
						APPLICATION_ID, "JSON conversion completed");

			} catch (MosipJsonParseException e) {
				// create Error Response
				response = getErrorResponse(response, RegConstants.OTP_GENERATION_ERROR_MESSAGE);
				LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
						APPLICATION_ID, "Error Response created");

			} catch (MosipJsonMappingException e) {
				// create Error Response
				response = getErrorResponse(response, RegConstants.OTP_GENERATION_ERROR_MESSAGE);
				LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
						APPLICATION_ID, "Error Response created");

			} catch (MosipIOException e) {
				// create Error Response
				response = getErrorResponse(response, RegConstants.OTP_GENERATION_ERROR_MESSAGE);
				LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
						APPLICATION_ID, "Error Response created");

			}
			// create Error Response
			response = getErrorResponse(response, RegConstants.OTP_GENERATION_ERROR_MESSAGE);
			LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
					APPLICATION_ID, "Error Response created");

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
			LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
					APPLICATION_ID, "Success Response created");

		} else {
			// create Error Response
			response = getErrorResponse(response, RegConstants.OTP_GENERATION_ERROR_MESSAGE);
			LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
					APPLICATION_ID, "Error Response created");

		}
		LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME, APPLICATION_ID,
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

		LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME, APPLICATION_ID,
				"Validation of OTP called");

		// Create Response to Return to UI layer
		ResponseDTO response = new ResponseDTO();
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
				LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
						APPLICATION_ID, "Error Response Created");

			}
		} catch (HttpClientErrorException httpClientErrorException) {
			try {
				// obtain otpValidatorResponseDto through JSON Util
				otpValidatorResponseDto = (OtpValidatorResponseDto) JsonUtils.jsonStringToJavaObject(
						OtpValidatorResponseDto.class, httpClientErrorException.getResponseBodyAsString());
			} catch (MosipJsonParseException e) {

				// Create Error response
				response = getErrorResponse(response, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);
				LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
						APPLICATION_ID, "Error Response Created");

			} catch (MosipJsonMappingException e) {
				// Create Error response
				response = getErrorResponse(response, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);
				LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
						APPLICATION_ID, "Error Response Created");

			} catch (MosipIOException e) {
				// Create Error response
				response = getErrorResponse(response, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);
				LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
						APPLICATION_ID, "Error Response Created");

			}
			// Create Error response
			response = getErrorResponse(response, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);
			LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
					APPLICATION_ID, "Error Response Created");

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
					LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
							APPLICATION_ID, "Success Response Created");

				} else {

					// Create Error response
					response = getErrorResponse(response, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);
					LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
							APPLICATION_ID, "Error Response Created");

				}
			} else {

				// Create Error response
				response = getErrorResponse(response, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);
				LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
						APPLICATION_ID, "Error Response Created");

			}
		} else {

			// Create Error response
			response = getErrorResponse(response, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);
			LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME,
					APPLICATION_ID, "Error Response Created");

		}

		LOGGER.debug("REGISTRATION - LOGIN - OTP", APPLICATION_NAME, APPLICATION_ID,
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
