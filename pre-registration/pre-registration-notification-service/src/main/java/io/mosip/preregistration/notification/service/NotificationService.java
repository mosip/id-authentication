package io.mosip.preregistration.notification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.qrcodegenerator.spi.QrCodeGenerator;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.NotificationUtil;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.notification.dto.QRCodeResponseDTO;
import io.mosip.preregistration.notification.error.ErrorCodes;
import io.mosip.preregistration.notification.error.ErrorMessages;
import io.mosip.preregistration.notification.exception.ConfigFileNotFoundException;
import io.mosip.preregistration.notification.exception.MandatoryFieldException;
import io.mosip.preregistration.notification.exception.util.NotificationExceptionCatcher;
import io.mosip.preregistration.notification.service.util.NotificationServiceUtil;

/**
 * The service class for notification.
 * 
 * @author Sanober Noor
 * @since 1.0.0
 *
 */
@Service
public class NotificationService {

	/**
	 * The reference to {@link NotificationUtil}.
	 */
	@Autowired
	private NotificationUtil notificationUtil;

	/**
	 * The reference to {@link NotificationServiceUtil}.
	 */
	@Autowired
	private NotificationServiceUtil serviceUtil;
	
	private Logger log = LoggerConfiguration.logConfig(NotificationService.class);

	@Autowired
	private QrCodeGenerator<QrVersion> qrCodeGenerator;

	@Value("${global.config.file}")
	private String globalFileName;

	@Value("${pre.reg.config.file}")
	private String preRegFileName;

	@Value("${ui.config.params}")
	private String uiConfigParams;

	/**
	 * Method to send notification.
	 * 
	 * @param jsonString
	 *            the json string.
	 * @param langCode
	 *            the language code.
	 * @param file
	 *            the file to send.
	 * @return the response dto.
	 */
	public MainResponseDTO<NotificationDTO> sendNotification(String jsonString, String langCode, MultipartFile file) {
		MainResponseDTO<NotificationDTO> response = new MainResponseDTO<>();
		log.info("sessionId", "idType", "id",
				"In notification service of sendNotification ");
		
		try {
			MainRequestDTO<NotificationDTO> notificationReqDTO = serviceUtil.createNotificationDetails(jsonString);
			NotificationDTO notififcationDto=notificationReqDTO.getRequest();
			if (ValidationUtil.requestValidator(notificationReqDTO)) {
			
			
			if (notififcationDto.getMobNum() != null && !notififcationDto.getMobNum().isEmpty()) {
				notificationUtil.notify("sms", notififcationDto, langCode, file);
			}
			if (notififcationDto.getEmailID() != null && !notififcationDto.getEmailID().isEmpty()) {
				notificationUtil.notify("email", notififcationDto, langCode, file);
			}
			if ((notififcationDto.getEmailID() == null || notififcationDto.getEmailID().isEmpty())
					&& (notififcationDto.getMobNum() == null || notififcationDto.getMobNum().isEmpty())) {
				throw new MandatoryFieldException(ErrorCodes.PRG_ACK_001.getCode(),
						ErrorMessages.MOBILE_NUMBER_OR_EMAIL_ADDRESS_NOT_FILLED.getCode());
			}
			}
			response.setId(notificationReqDTO.getId());
			response.setVersion(notificationReqDTO.getVersion());
			response.setResponse(notififcationDto);
			response.setResponsetime(serviceUtil.getCurrentResponseTime());
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In notification service of sendNotification "+ex.getMessage());
			new NotificationExceptionCatcher().handle(ex);
		}
		return response;
	}

	/**
	 * This method will generate qrcode
	 * 
	 * @param data
	 * @return
	 */
	public MainResponseDTO<QRCodeResponseDTO> generateQRCode(String data) {
		byte[] qrCode = null;
		log.info("sessionId", "idType", "id",
				"In notification service of generateQRCode ");
		QRCodeResponseDTO responsedto = new QRCodeResponseDTO();
		MainResponseDTO<QRCodeResponseDTO> response = new MainResponseDTO<>();
		try {
			qrCode = qrCodeGenerator.generateQrCode(data, QrVersion.V25);

			responsedto.setQrcode(qrCode);

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In notification service of generateQRCode "+ex.getMessage());
			new NotificationExceptionCatcher().handle(ex);
		}
		response.setResponse(responsedto);
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		

		return response;
	}

	/**
	 * This will return UI related configurations return
	 */
	public MainResponseDTO<Map<String, String>> getConfig() {
		log.info("sessionId", "idType", "id",
				"In notification service of getConfig ");
		MainResponseDTO<Map<String, String>> res = new MainResponseDTO<>();
		List<String> reqParams = new ArrayList<>();
		Map<String, String> configParams = new HashMap<>();
		try {
			String[] uiParams = uiConfigParams.split(",");
			for (int i = 0; i < uiParams.length; i++) {
				reqParams.add(uiParams[i]);
			}
			if (globalFileName != null && preRegFileName != null) {
				String globalParam = serviceUtil.configRestCall(globalFileName);
				String preregParam = serviceUtil.configRestCall(preRegFileName);
				Properties prop1 = serviceUtil.parsePropertiesString(globalParam);
				Properties prop2 = serviceUtil.parsePropertiesString(preregParam);
				serviceUtil.getConfigParams(prop1,configParams,reqParams);
				serviceUtil.getConfigParams(prop2,configParams,reqParams);
		
			} else {
				throw new ConfigFileNotFoundException(ErrorCodes.PRG_ACK_007.name(),
						ErrorMessages.CONFIG_FILE_NOT_FOUND_EXCEPTION.name());
			}
			
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In notification service of getConfig "+ex.getMessage());
			new NotificationExceptionCatcher().handle(ex);
		}
		res.setResponse(configParams);
		res.setResponsetime(serviceUtil.getCurrentResponseTime());
		return res;
	}

	
}
