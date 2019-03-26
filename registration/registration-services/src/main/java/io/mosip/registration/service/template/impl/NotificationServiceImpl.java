package io.mosip.registration.service.template.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.EMAIL_SERVICE;
import static io.mosip.registration.constants.RegistrationConstants.EMAIL_SUBJECT;
import static io.mosip.registration.constants.RegistrationConstants.NOTIFICATION_SERVICE;
import static io.mosip.registration.constants.RegistrationConstants.SMS_SERVICE;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.NotificationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.template.NotificationService;
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
	private static final Logger LOGGER = AppConfig.getLogger(NotificationServiceImpl.class);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.NotificationService#sendSMS(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public ResponseDTO sendSMS(String message, String number, String regId) {

		LOGGER.info(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID, "sendSMS Method called");

		ResponseDTO responseDTO = new ResponseDTO();
		NotificationDTO smsdto = new NotificationDTO();
		smsdto.setMessage(message);
		smsdto.setNumber(number);

		sendNotification(regId, responseDTO, smsdto, SMS_SERVICE, "success");
		return responseDTO;
	}

	/**
	 * To send notification
	 * 
	 * @param regId
	 * @param responseDTO
	 * @param smsdto
	 */
	private void sendNotification(String regId, ResponseDTO responseDTO, Object object, String service,
			String expectedStatus) {
		StringBuilder sb;
		try {
			@SuppressWarnings("unchecked")
			Map<String, List<Map<String, String>>> response = (Map<String, List<Map<String, String>>>) serviceDelegateUtil
					.post(service, object,RegistrationConstants.JOB_TRIGGER_POINT_USER);
			String res = Optional.ofNullable(String.valueOf(response.get("status"))).orElse("");
			if (res.equals(expectedStatus)) {
				sb = new StringBuilder();
				sb.append(service.toUpperCase()).append(" request submitted successfully");

				LOGGER.info(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID, sb.toString());
				auditFactory.audit(AuditEvent.NOTIFICATION_STATUS, Components.NOTIFICATION_SERVICE,
						SessionContext.userContext().getUserId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
				// creating success response
				SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
				successResponseDTO.setMessage("Success");
				responseDTO.setSuccessResponseDTO(successResponseDTO);
			} else {
				String errorMessage = Optional.ofNullable(response.get("errors")).filter(list -> !list.isEmpty())
						.flatMap(list -> list.stream().filter(map -> map.containsKey("errorMessage")).findAny())
						.map(map -> map.get("errorMessage")).orElse("");				
				
				ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
				errorResponseDTO.setMessage(errorMessage);
				List<ErrorResponseDTO> errorResponse = new ArrayList<>();
				errorResponse.add(errorResponseDTO);
				responseDTO.setErrorResponseDTOs(errorResponse);
				
				LOGGER.info(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID, errorMessage);
				auditFactory.audit(AuditEvent.NOTIFICATION_STATUS, Components.NOTIFICATION_SERVICE,
						SessionContext.userContext().getUserId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
			}
		} catch (HttpClientErrorException | RegBaseCheckedException | HttpServerErrorException | SocketTimeoutException
				| ResourceAccessException exception) {
			sb = new StringBuilder();
			sb.append("Exception in sending ").append(service.toUpperCase()).append(" Notification - ")
					.append(exception.getMessage());

			LOGGER.error(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID, exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			auditFactory.audit(AuditEvent.NOTIFICATION_STATUS, Components.NOTIFICATION_SERVICE,
					SessionContext.userContext().getUserId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
			
			// creating error response
			ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
			errorResponseDTO.setMessage("Unable to send " + service.toUpperCase() + " Notification");
			List<ErrorResponseDTO> errorResponse = new ArrayList<>();
			errorResponse.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(errorResponse);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.NotificationService#sendEmail(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public ResponseDTO sendEmail(String message, String emailId, String regId) {

		LOGGER.info(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID, "sendEmail Method called");
		auditFactory.audit(AuditEvent.NOTIFICATION_STATUS, Components.NOTIFICATION_SERVICE,
				SessionContext.userContext().getUserId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		ResponseDTO responseDTO = new ResponseDTO();
		LinkedMultiValueMap<String, Object> emailDetails = new LinkedMultiValueMap<>();
		emailDetails.add("mailTo", emailId);
		emailDetails.add("mailSubject", EMAIL_SUBJECT);
		emailDetails.add("mailContent", message);

		sendNotification(regId, responseDTO, emailDetails, EMAIL_SERVICE, "success");
		return responseDTO;
	}

}
