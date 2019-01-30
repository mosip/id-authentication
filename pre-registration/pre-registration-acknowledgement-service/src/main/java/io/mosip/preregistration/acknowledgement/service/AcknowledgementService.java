package io.mosip.preregistration.acknowledgement.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.preregistration.acknowledgement.code.RequestCodes;
import io.mosip.preregistration.acknowledgement.dto.AcknowledgementDTO;
import io.mosip.preregistration.acknowledgement.dto.NotificationResponseDTO;
import io.mosip.preregistration.acknowledgement.dto.ResponseDTO;
import io.mosip.preregistration.acknowledgement.dto.SMSRequestDTO;
import io.mosip.preregistration.acknowledgement.dto.TemplateResponseDTO;
import io.mosip.preregistration.acknowledgement.error.ErrorCodes;
import io.mosip.preregistration.acknowledgement.error.ErrorMessages;
import io.mosip.preregistration.acknowledgement.exception.MandatoryFieldException;
import io.mosip.preregistration.acknowledgement.exception.util.AcknowledgementExceptionCatcher;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.util.ValidationUtil;

/**
 * @author Sanober Noor
 * @since 1.0.0
 *
 */
@Service
public class AcknowledgementService {

	/**
	 * Reference for ${resource.url} from property file
	 */
	@Value("${resource.url}")
	private String resourceUrl;

	@Value("${emailResourse.url}")
	private String emailResourseUrl;

	@Value("${smsResourse.url}")
	private String smsResourseUrl;

	@Value("${ver}")
	String versionUrl;

	@Value("${id}")
	String idUrl;

	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private TemplateManager templateManager;
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
	public String acknowledgementNotifier(String jsonStirng, String langCode, MultipartFile file) {
		String emailResponse = null;
		String smsResponse = null;

		try {

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
					smsResponse = smsNotification(acknowledgementDTO, langCode);
				}
				if (acknowledgementDTO.getEmailID() != null && !acknowledgementDTO.getEmailID().isEmpty()) {
					emailResponse = emailNotification(acknowledgementDTO, langCode, file);
				}

				if ((acknowledgementDTO.getEmailID() == null||acknowledgementDTO.getEmailID().isEmpty())
						&& (acknowledgementDTO.getMobNum() == null || acknowledgementDTO.getMobNum().isEmpty())) {
					throw new MandatoryFieldException(ErrorCodes.PRG_ACK_001.getCode(),
							ErrorMessages.MOBILE_NUMBER_OR_EMAIL_ADDRESS_NOT_FILLED.getCode());
				}

			}
		} catch (Exception ex) {
			new AcknowledgementExceptionCatcher().handle(ex);
		}
		return emailResponse;

	}

	/**
	 * @param acknowledgementDTO
	 * @param langCode
	 * @param file
	 * @return
	 */
	public String emailNotification(AcknowledgementDTO acknowledgementDTO, String langCode, MultipartFile file) {
		List<TemplateResponseDTO> response = null;
		ResponseEntity<NotificationResponseDTO> resp = null;
		String merseTemplate = null;
		try {
			File convFile = new File(file.getOriginalFilename());

			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			String url = resourceUrl + "/" + langCode + "/" + "Email-Acknowledgement";

			ResponseEntity<ResponseDTO> respEntity = restTemplate.getForEntity(url, ResponseDTO.class);

			response = respEntity.getBody().getTemplates();

			String fileText = response.get(0).getFileText();
			merseTemplate = templateMerge(fileText, acknowledgementDTO);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<Object, Object> emailMap = new LinkedMultiValueMap<>();
			emailMap.add("attachments", convFile);
			emailMap.add("mailContent", merseTemplate);
			emailMap.add("mailSubject", getEmailSubject(acknowledgementDTO, langCode));
			emailMap.add("mailTo", acknowledgementDTO.getEmailID());
			HttpEntity<MultiValueMap<Object, Object>> httpEntity = new HttpEntity<>(emailMap, headers);

			resp = restTemplate.exchange(emailResourseUrl, HttpMethod.POST, httpEntity, NotificationResponseDTO.class);
			fos.close();
		} catch (Exception ex) {
			new AcknowledgementExceptionCatcher().handle(ex);

		}
		return resp.getBody().getStatus();
	}

	/**
	 * @param acknowledgementDTO
	 * @param langCode
	 * @return
	 */
	public String getEmailSubject(AcknowledgementDTO acknowledgementDTO, String langCode) {

		String url = resourceUrl + "/" + langCode + "/" + "Acknowledgement-email-subject";

		ResponseEntity<ResponseDTO> respEntity = restTemplate.getForEntity(url, ResponseDTO.class);

		List<TemplateResponseDTO> response = respEntity.getBody().getTemplates();

		String fileText = response.get(0).getFileText();
		return templateMerge(fileText, acknowledgementDTO);
	}

	/**
	 * @param acknowledgementDTO
	 * @return
	 */
	public Map<String, Object> mapSetting(AcknowledgementDTO acknowledgementDTO) {
		Map<String, Object> responseMap = new HashMap<>();

		DateTimeFormatter dateFormate = DateTimeFormatter.ofPattern("yyyy/MM/dd");

		LocalDateTime now = LocalDateTime.now();
		LocalTime localTime = LocalTime.now(ZoneId.of("UTC"));

		responseMap.put("name", acknowledgementDTO.getName());
		responseMap.put("PRID", acknowledgementDTO.getPreId());
		responseMap.put("Date", dateFormate.format(now));
		responseMap.put("Time", localTime);
		responseMap.put("Appointmentdate", acknowledgementDTO.getAppointmentDate());
		responseMap.put("Appointmenttime", acknowledgementDTO.getAppointmentTime());
		return responseMap;
	}

	/**
	 * @param acknowledgementDTO
	 * @param langCode
	 * @return
	 */
	public String smsNotification(AcknowledgementDTO acknowledgementDTO, String langCode) {
		List<TemplateResponseDTO> response = null;
		ResponseEntity<NotificationResponseDTO> resp = null;
		try {
		String url = resourceUrl + "/" + langCode + "/" + "SMS-Acknowledgement";

		ResponseEntity<ResponseDTO> respEntity = restTemplate.getForEntity(url, ResponseDTO.class);
		response = respEntity.getBody().getTemplates();
		String fileText = response.get(0).getFileText();
		String merseTemplate = templateMerge(fileText, acknowledgementDTO);
		SMSRequestDTO smsRequestDTO = new SMSRequestDTO();
		smsRequestDTO.setMessage(merseTemplate);
		smsRequestDTO.setNumber(acknowledgementDTO.getMobNum());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<SMSRequestDTO> httpEntity = new HttpEntity<>(smsRequestDTO, headers);
		
			resp = restTemplate.exchange(smsResourseUrl, HttpMethod.POST, httpEntity, NotificationResponseDTO.class);
		
		}
		catch (Exception ex) {
			new AcknowledgementExceptionCatcher().handle(ex);

		}
				return resp.getBody().getStatus();

	}

	/**
	 * This method merging the template
	 * 
	 * @param fileText
	 * @param acknowledgementDTO
	 * @return
	 */
	public String templateMerge(String fileText, AcknowledgementDTO acknowledgementDTO) {

		String merseTemplate = null;
		Map<String, Object> map = mapSetting(acknowledgementDTO);
		try {
			InputStream templateInputStream = new ByteArrayInputStream(fileText.getBytes(Charset.forName("UTF-8")));

			InputStream resultedTemplate = templateManager.merge(templateInputStream, map);

			merseTemplate = IOUtils.toString(resultedTemplate, StandardCharsets.UTF_8.name());
		} catch (Exception ex) {
			new AcknowledgementExceptionCatcher().handle(ex);
		}

		return merseTemplate;
	}

	/**
	 * @param acknowledgementReqDto
	 * @return
	 */
	public Map<String, String> prepareRequestParamMap(MainRequestDTO<AcknowledgementDTO> acknowledgementReqDto) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.id.toString(), acknowledgementReqDto.getId());
		inputValidation.put(RequestCodes.ver.toString(), acknowledgementReqDto.getVer());
		inputValidation.put(RequestCodes.reqTime.toString(),
				new SimpleDateFormat(dateTimeFormat).format(acknowledgementReqDto.getReqTime()));
		inputValidation.put(RequestCodes.request.toString(), acknowledgementReqDto.getRequest().toString());
		return inputValidation;
	}

}
