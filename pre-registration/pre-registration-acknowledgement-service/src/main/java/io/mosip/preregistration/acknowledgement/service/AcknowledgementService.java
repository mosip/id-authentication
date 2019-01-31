package io.mosip.preregistration.acknowledgement.service;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.preregistration.acknowledgement.code.RequestCodes;
import io.mosip.preregistration.acknowledgement.error.ErrorCodes;
import io.mosip.preregistration.acknowledgement.error.ErrorMessages;
import io.mosip.preregistration.acknowledgement.exception.MandatoryFieldException;
import io.mosip.preregistration.acknowledgement.exception.util.AcknowledgementExceptionCatcher;
import io.mosip.preregistration.core.common.dto.AcknowledgementDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.util.NotificationUtil;
import io.mosip.preregistration.core.util.ValidationUtil;

/**
 * @author Sanober Noor
 * @since 1.0.0
 *
 */
@Service
public class AcknowledgementService {

	

	@Value("${ver}")
	String versionUrl;

	@Value("${id}")
	String idUrl;

	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	
	
    
    @Autowired
	private NotificationUtil notificationUtil;
	AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO();

	Map<String, String> requiredRequestMap = new HashMap<>();

	@PostConstruct
	public void setupAcknowledgementService() {
		requiredRequestMap.put("id", idUrl);
		requiredRequestMap.put("ver", versionUrl);

	}

	/**
	 * @param jsonStirng
	 * @param langCode
	 * @param file
	 * @return
	 */
	public MainListResponseDTO<AcknowledgementDTO> acknowledgementNotifier(String jsonStirng, String langCode,
			MultipartFile file) {
		MainListResponseDTO<AcknowledgementDTO> response = new MainListResponseDTO<>();
		List<AcknowledgementDTO> list = new ArrayList<>();
		try {

			File convFile = new File(file.getOriginalFilename());
	        
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			
			MainRequestDTO<AcknowledgementDTO> mainDTO = new MainRequestDTO<>();
			JSONObject acknowledgementData = new JSONObject(jsonStirng);

			JSONObject acknowledgementDTOData = (JSONObject) acknowledgementData.get("request");
			acknowledgementDTO = (AcknowledgementDTO) JsonUtils.jsonStringToJavaObject(AcknowledgementDTO.class,
					acknowledgementDTOData.toString());
			mainDTO.setId(acknowledgementData.get("id").toString());
			mainDTO.setVer(acknowledgementData.get("ver").toString());
			mainDTO.setReqTime(
					new SimpleDateFormat(dateTimeFormat).parse(acknowledgementData.get("reqTime").toString()));
			mainDTO.setRequest(acknowledgementDTO);
			if (ValidationUtil.requestValidator(prepareRequestParamMap(mainDTO), requiredRequestMap)) {
				if (acknowledgementDTO.getMobNum() != null && !acknowledgementDTO.getMobNum().isEmpty()) {
				notificationUtil.notify("sms",acknowledgementDTO, langCode,convFile);
				
				
				}
				if (acknowledgementDTO.getEmailID() != null && !acknowledgementDTO.getEmailID().isEmpty()) {
					notificationUtil.notify("email",acknowledgementDTO, langCode,convFile);
				}
				fos.close();

				if ((acknowledgementDTO.getEmailID() == null || acknowledgementDTO.getEmailID().isEmpty())
						&& (acknowledgementDTO.getMobNum() == null || acknowledgementDTO.getMobNum().isEmpty())) {
					throw new MandatoryFieldException(ErrorCodes.PRG_ACK_001.getCode(),
							ErrorMessages.MOBILE_NUMBER_OR_EMAIL_ADDRESS_NOT_FILLED.getCode());
				}

			}
			list.add(acknowledgementDTO);
			response.setResponse(list);
			response.setResTime(getCurrentResponseTime());
			response.setStatus(Boolean.TRUE);
			
		} catch (Exception ex) {
			new AcknowledgementExceptionCatcher().handle(ex);
		}
		return response;

	}
	/**
	 * @param acknowledgementReqDto
	 * @return
	 */
	public Map<String, String> prepareRequestParamMap(MainRequestDTO<AcknowledgementDTO> acknowledgementReqDto) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.ID.toString(), acknowledgementReqDto.getId());
		inputValidation.put(RequestCodes.VER.toString(), acknowledgementReqDto.getVer());
		inputValidation.put(RequestCodes.REQTIME.toString(),
				new SimpleDateFormat(dateTimeFormat).format(acknowledgementReqDto.getReqTime()));
		inputValidation.put(RequestCodes.REQUEST.toString(), acknowledgementReqDto.getRequest().toString());
		return inputValidation;
	}
	
	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
	}

	
}
