package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.EMAIL_SERVICE;
import static io.mosip.registration.constants.RegistrationConstants.EMAIL_SUBJECT;
import static io.mosip.registration.constants.RegistrationConstants.SMS_SERVICE;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import static io.mosip.registration.constants.RegistrationConstants.NOTIFICATION_SERVICE;
import io.mosip.registration.dto.EmailDTO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SMSDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.NotificationService;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

/**
 * SMS and Email notification service
 * 
 * @author Dinesh Ashokan
 *
 */
@Service
public class NotificationServiceImpl implements NotificationService {

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
	 * serviceDelegateUtil which processes the HTTPRequestDTO requests
	 */
	@Autowired
	private ServiceDelegateUtil serviceDelegateUtil;

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.NotificationService#sendSMS(java.lang.String, java.lang.String)
	 */
	@Override
	public ResponseDTO sendSMS(String message, String number) {

		LOGGER.debug(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID, "sendSMS Method called");
		
		ResponseDTO responseDTO=new ResponseDTO();
		
		SMSDTO smsdto = new SMSDTO();
		smsdto.setMessage(message);
		smsdto.setNumber(number);

		try {

			SMSDTO response = (SMSDTO) serviceDelegateUtil.post(SMS_SERVICE, smsdto);

			if (response.getStatus() != null && response.getStatus().equals("success")) {

				LOGGER.debug(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID,
						"SMS Request Submitted");
				
				SuccessResponseDTO successResponseDTO=new SuccessResponseDTO();
				successResponseDTO.setMessage("Success");
				responseDTO.setSuccessResponseDTO(successResponseDTO);
			}
		} catch (HttpClientErrorException | RegBaseCheckedException
				| HttpServerErrorException | SocketTimeoutException | ResourceAccessException  exception) {
			
			LOGGER.debug(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID,
					"Exception in sending SMS Notification");
			exception.printStackTrace();
			ErrorResponseDTO errorResponseDTO=new ErrorResponseDTO();
			errorResponseDTO.setMessage("Unable to send SMS Notification");
			List<ErrorResponseDTO> errorResponse = new ArrayList<>();
			errorResponse.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(errorResponse);

		}
		return responseDTO;
	}

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.NotificationService#sendEmail(java.lang.String, java.lang.String)
	 */
	@Override
	public ResponseDTO sendEmail(String message, String emailId) {

		LOGGER.debug(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID,
				"sendEmail Method called");
		
		ResponseDTO responseDTO = new ResponseDTO();
		
		LinkedMultiValueMap<String, Object> emailDetails = new LinkedMultiValueMap<>();
		emailDetails.add("mailTo", emailId);
		emailDetails.add("mailSubject", EMAIL_SUBJECT);
		emailDetails.add("mailContent", message);

		try {
			EmailDTO response = (EmailDTO) serviceDelegateUtil.post(EMAIL_SERVICE, emailDetails);

			if (response.getStatus() != null && response.getStatus().equals("Email Request submitted")) {

				LOGGER.debug(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID,
						response.getStatus());
				
				SuccessResponseDTO successResponseDTO=new SuccessResponseDTO();
				successResponseDTO.setMessage("Success");
				responseDTO.setSuccessResponseDTO(successResponseDTO);
				
			}

		} catch (HttpClientErrorException | RegBaseCheckedException
				| HttpServerErrorException | SocketTimeoutException | ResourceAccessException exception) {

			LOGGER.debug(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID,
					"Exception in sending Email Notification");		
			
			ErrorResponseDTO errorResponseDTO=new ErrorResponseDTO();
			errorResponseDTO.setMessage("Unable to send Email Notification");
			List<ErrorResponseDTO> errorResponse = new ArrayList<>();
			errorResponse.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(errorResponse);
		}
		return responseDTO;
	}

}
