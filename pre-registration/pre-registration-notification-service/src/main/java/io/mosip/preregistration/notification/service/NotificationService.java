package io.mosip.preregistration.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.qrcodegenerator.spi.QrCodeGenerator;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import io.mosip.preregistration.core.util.NotificationUtil;
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

	@Autowired
	private QrCodeGenerator<QrVersion> qrCodeGenerator;
	/**
	 * Method to send notification.
	 * 
	 * @param jsonStirng
	 *            the json string.
	 * @param langCode
	 *            the language code.
	 * @param file
	 *            the file to send.
	 * @return the response dto.
	 */
	public MainResponseDTO<NotificationDTO> sendNotification(String jsonStirng, String langCode, MultipartFile file) {
		MainResponseDTO<NotificationDTO> response = new MainResponseDTO<>();
		
		try {
			NotificationDTO acknowledgementDTO = (NotificationDTO) JsonUtils
					.jsonStringToJavaObject(NotificationDTO.class, jsonStirng);
			
			if (acknowledgementDTO.getMobNum() != null && !acknowledgementDTO.getMobNum().isEmpty()) {
				notificationUtil.notify("sms", acknowledgementDTO, langCode, file);
			}
			if (acknowledgementDTO.getEmailID() != null && !acknowledgementDTO.getEmailID().isEmpty()) {
				notificationUtil.notify("email", acknowledgementDTO, langCode, file);
			}
			if ((acknowledgementDTO.getEmailID() == null || acknowledgementDTO.getEmailID().isEmpty())
					&& (acknowledgementDTO.getMobNum() == null || acknowledgementDTO.getMobNum().isEmpty())) {
				throw new MandatoryFieldException(ErrorCodes.PRG_ACK_001.getCode(),
						ErrorMessages.MOBILE_NUMBER_OR_EMAIL_ADDRESS_NOT_FILLED.getCode());
			}
			response.setResponse(acknowledgementDTO);
			response.setResTime(serviceUtil.getCurrentResponseTime());
			response.setStatus(Boolean.TRUE);
		} catch (Exception ex) {
			new NotificationExceptionCatcher().handle(ex);
		}
		return response;
	}

	/**This method will generate qrcode
	 * @param data
	 * @return
	 */
	public MainResponseDTO<QRCodeResponseDTO> generateQRCode(String data) {
		byte[] qrCode=null;
		QRCodeResponseDTO responsedto=new QRCodeResponseDTO();
		MainResponseDTO<QRCodeResponseDTO> response=new MainResponseDTO<>();
		try {
		 qrCode=	qrCodeGenerator.generateQrCode(data, QrVersion.V25);
		 
		 responsedto.setQrcode(qrCode);
		 
		} catch (Exception ex) {
			
			new NotificationExceptionCatcher().handle(ex);
		} 
		response.setResponse(responsedto);
		response.setResTime(serviceUtil.getCurrentResponseTime());
		response.setStatus(Boolean.TRUE);
		
		return response;
	}
}
