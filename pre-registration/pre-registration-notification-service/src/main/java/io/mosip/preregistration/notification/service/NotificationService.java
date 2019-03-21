package io.mosip.preregistration.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
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


	
}
