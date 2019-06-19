package io.mosip.preregistration.generateqrcode.service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.qrcodegenerator.spi.QrCodeGenerator;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.generateqrcode.dto.QRCodeResponseDTO;
import io.mosip.preregistration.generateqrcode.exception.util.QRcodeExceptionCatcher;
import io.mosip.preregistration.generateqrcode.service.util.GenerateQRcodeServiceUtil;

/**
 * @author Sanober Noor
 *@since 1.0.0
 */
@Service
public class GenerateQRcodeService {
	
	/**
	 * The reference to {@link GenerateQRcodeServiceUtil}.
	 */
	@Autowired
	private GenerateQRcodeServiceUtil serviceUtil;
	
	private Logger log = LoggerConfiguration.logConfig(GenerateQRcodeService.class);
	
	@Autowired
	private QrCodeGenerator<QrVersion> qrCodeGenerator;
	
	Map<String, String> requiredRequestMap = new HashMap<>();
	
	@Value("${mosip.pre-registration.qrcode.generate.id}")
	private String Id;
	
	@Value("${mosip.pre-registration.qrcode.service.version}")
	private String version;
	
	@Value("${qrversion}")
	private String qrversion;
	
	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;
	
	@PostConstruct
	public void setupBookingService() {
		requiredRequestMap.put("version", version);
		requiredRequestMap.put("id", Id);

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
				"In generateQRCode service of generateQRCode ");
		QRCodeResponseDTO responsedto = new QRCodeResponseDTO();
		

		MainRequestDTO<String> qrcodedto = new MainRequestDTO<>();
		
		MainResponseDTO<QRCodeResponseDTO> response = new MainResponseDTO<>();
		
		response.setId(Id);
		response.setVersion(version);
		try {
			JSONObject qrCodeReqData = new JSONObject(data);

			qrcodedto.setId(qrCodeReqData.get("id").toString());
			qrcodedto.setVersion(qrCodeReqData.get("version").toString());
			qrcodedto.setRequesttime(new SimpleDateFormat(utcDateTimePattern).parse(qrCodeReqData.get("requesttime").toString()) );
			response.setId(qrcodedto.getId());
			response.setVersion(qrcodedto.getVersion());
			JSONObject requestvalue=(JSONObject) qrCodeReqData.get("request");
			if(requestvalue.length()==0) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_004.getCode(),
						ErrorMessages.INVALID_REQUEST_BODY.getMessage(), null);
			}
			String qrCodeData =   qrCodeReqData.get("request").toString();
			qrcodedto.setRequest(qrCodeData);
			
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestMap(qrcodedto),requiredRequestMap)) {
			qrCode = qrCodeGenerator.generateQrCode(qrCodeData,QrVersion.valueOf(qrversion));
			}
			
			responsedto.setQrcode(qrCode);

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In generateQRCode service of generateQRCode "+ex.getMessage());
			new QRcodeExceptionCatcher().handle(ex,response);
		}
		
		response.setResponse(responsedto);
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		

		return response;
	}

	

}
