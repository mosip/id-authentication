package io.mosip.preregistration.notification.service;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.exception.FileNotFoundException;
import io.mosip.kernel.core.qrcodegenerator.spi.QrCodeGenerator;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import io.mosip.preregistration.core.util.NotificationUtil;
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

	@Autowired
	private QrCodeGenerator<QrVersion> qrCodeGenerator;
	
	@Value("${global.config.file}")
	private String globalFileName;
	
	@Value("${pre.reg.config.file}")
	private String preRegFileName;
	
	
	@Value("${ui.config.params}")
	private String uiConfigParams;
	/**
	 * Environment instance
	 */
	@Autowired
	private Environment env;
	
	/**
	 * Autowired reference for {@link #RestTemplateBuilder}
	 */
	@Autowired
	private RestTemplateBuilder restTemplateBuilder;
	
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
	
	/**
	 * This will return UI related configurations
	 * return
	 */
	public MainResponseDTO<Map<String,String>> getConfig() {
		MainResponseDTO<Map<String,String>> res= new MainResponseDTO<>();
		RestTemplate restTemplate = restTemplateBuilder.build();
		List<String> reqParams= new ArrayList<>();
		String[] uiParams=uiConfigParams.split(",");
		for(int i=0;i<uiParams.length;i++) {
			reqParams.add(uiParams[i]);
		}
		Map<String,String> configParams= new HashMap<>();
		String configServerUri = env.getProperty("spring.cloud.config.uri");
		String configLabel = env.getProperty("spring.cloud.config.label");
		String configProfile = env.getProperty("spring.profiles.active");
		String configAppName = env.getProperty("spring.cloud.config.name");
		JSONObject result = null;
		StringBuilder globaluriBuilder = null;
		StringBuilder preReguriBuilder = null;
		try {
		if (globalFileName != null  && preRegFileName !=null) {
			globaluriBuilder = new StringBuilder();
			preReguriBuilder = new StringBuilder();
			globaluriBuilder.append(configServerUri + "/").append(configAppName + "/").append(configProfile + "/")
					.append(configLabel + "/").append(globalFileName);
			preReguriBuilder.append(configServerUri + "/").append(configAppName + "/").append(configProfile + "/")
			.append(configLabel + "/").append(preRegFileName);
		} else {
			   throw new ConfigFileNotFoundException(ErrorCodes.PRG_ACK_007.name(), ErrorMessages.CONFIG_FILE_NOT_FOUND_EXCEPTION.name());
		}
			String globalParam = restTemplate.getForObject(globaluriBuilder.toString(), String.class);
			String preregParam = restTemplate.getForObject(preReguriBuilder.toString(), String.class);
			Properties prop1 = parsePropertiesString(globalParam);
			Properties prop2 = parsePropertiesString(preregParam);
			result = new JSONObject();
			for (Entry<Object, Object> e : prop1.entrySet()) {
				result.put(String.valueOf(e.getKey()), e.getValue());
				if(reqParams.contains(String.valueOf(e.getKey()))){
					System.out.println(String.valueOf(e.getKey())+" ----value--- "+ e.getValue());
					configParams.put(String.valueOf(e.getKey()), e.getValue().toString());
				}
				
			}
			for (Entry<Object, Object> e : prop2.entrySet()) {
				result.put(String.valueOf(e.getKey()), e.getValue());
				if(reqParams.contains(String.valueOf(e.getKey()))){
					System.out.println(String.valueOf(e.getKey())+" ----value--- "+ e.getValue());
					configParams.put(String.valueOf(e.getKey()), e.getValue().toString());
				}
				
			}
		} catch (RestClientException | IOException e) {
			new NotificationExceptionCatcher().handle(e);
		}
        res.setResponse(configParams);
        res.setResTime(serviceUtil.getCurrentResponseTime());
		res.setStatus(Boolean.TRUE);
		return res;
	}
	
	public Properties parsePropertiesString(String s) throws IOException {
		final Properties p = new Properties();
		p.load(new StringReader(s));
		return p;
	}
}
