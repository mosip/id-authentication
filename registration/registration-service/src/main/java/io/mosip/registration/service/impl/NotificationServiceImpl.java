package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.EMAIL_SUBJECT;
import static io.mosip.registration.constants.RegistrationConstants.EMAIL_SERVICE;
import static io.mosip.registration.constants.RegistrationConstants.SMS_SERVICE;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dto.EmailDTO;
import io.mosip.registration.dto.SMSDTO;
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
	private static final MosipLogger LOGGER = AppConfig.getLogger(LoginServiceImpl.class);

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
	 * java.lang.String)
	 */
	@Override
	public void sendSMS(String message, String number) {

		LOGGER.debug("REGISTRATION - NOTIFICATION SERVICE ", APPLICATION_NAME, APPLICATION_ID, "sendSMS Method called");

		SMSDTO smsdto = new SMSDTO();
		smsdto.setMessage(message);
		smsdto.setNumber("9994019598");
		try {
			serviceDelegateUtil.post(SMS_SERVICE, smsdto);
		} catch (HttpClientErrorException httpClientErrorException) {		
		} catch (RegBaseCheckedException regBaseCheckedException) {
			
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.NotificationService#sendEmail(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void sendEmail(String message, String emailId) {

		LOGGER.debug("REGISTRATION - NOTIFICATION SERVICE ", APPLICATION_NAME, APPLICATION_ID,
				"sendEmail Method called");

		LinkedMultiValueMap<String, Object> emailData = new LinkedMultiValueMap<>();
		emailData.add("mailTo", "Dinesh.Ashokan@mindtree.com");
		emailData.add("mailSubject", null);
		emailData.add("mailContent", message);
		try {
			EmailDTO response = (EmailDTO) serviceDelegateUtil.post(EMAIL_SERVICE, emailData);

			if (response.getStatus() != null && response.getStatus().equals("Email Request submitted")) {
				
					LOGGER.debug("REGISTRATION - NOTIFICATION SERVICE ", APPLICATION_NAME, APPLICATION_ID,
							response.getStatus());
			
			}

		} catch (HttpClientErrorException httpClientErrorException) {
			LOGGER.debug("REGISTRATION - NOTIFICATION SERVICE ", APPLICATION_NAME, APPLICATION_ID,
					httpClientErrorException.getResponseBodyAsString());			
		} catch (RegBaseCheckedException regBaseCheckedException) {
			
		}
	}

}
