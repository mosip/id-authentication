package io.mosip.resident.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.NotificationTemplateCode;
import io.mosip.resident.dto.NotificationRequestDto;
import io.mosip.resident.dto.NotificationResponseDTO;
import io.mosip.resident.dto.SMSRequestDTO;
import io.mosip.resident.dto.TemplateDto;
import io.mosip.resident.dto.TemplateResponseDto;

@Component
public class NotificationService {
	@Autowired
	private TemplateManager templateManager;

	@Value("${mosip.primary-language}")
	private String primaryLang;

	@Value("${mosip.secondary-language}")
	private String secondaryLang;

	@Value("${mosip.notification.language-type}")
	private String languageType;

	@Value("${resident.notification.emails}")
	private String notificationEmails;

	@Autowired
	private Environment env;

	@Autowired
	private ResidentServiceRestClient restClient;

	@Autowired
	private TokenGenerator tokenGenerator;

	public static final String LINE_SEPARATOR = "" + '\n' + '\n' + '\n';

	@Autowired
	private Utilitiy utility;

	public void sendNotification(NotificationRequestDto dto) {
		String subject = "";

		try {
			Map<String, Object> notificationAttributes = utility.getMailingAttributes(dto.getId(), dto.getIdType());
			if (dto.getAdditionalAttributes() != null && dto.getAdditionalAttributes().size() > 0) {
				notificationAttributes.putAll(dto.getAdditionalAttributes());
			}
			//added only few cases
			switch (dto.getTemplateTypeCode().name()) {
			case "RS_DOW_UIN_Status":
				subject = "Download e-card request sucessful";
				break;
			case "RS_UIN_RPR_Status_EMAIL":
				subject = "Request for re-print UIN successfull";
				break;
			case "RS_AUTH_HIST_Status":
				subject = "Request for Auth History is successfull";
				break;
			case "":

			}

			sendSMSNotification(notificationAttributes, dto.getTemplateTypeCode());
			sendEmailNotification(notificationAttributes, dto.getTemplateTypeCode(), null, subject);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String getTemplate(String langCode, String templatetypecode) throws IOException {
		List<String> pathSegments = new ArrayList<String>();
		pathSegments.add(langCode);
		pathSegments.add(templatetypecode);

		TemplateResponseDto resp = (TemplateResponseDto) restClient.getApi(ApiName.TEMPLATES, pathSegments, null, null,
				TemplateResponseDto.class, tokenGenerator.getToken());
		List<TemplateDto> response = resp.getResponse().getTemplates();

		return response.get(0).getFileText().replaceAll("^\"|\"$", "");

	}

	public String templateMerge(String fileText, Map<String, Object> mailingAttributes) throws IOException {
		String mergeTemplate = null;
		InputStream templateInputStream = new ByteArrayInputStream(fileText.getBytes(Charset.forName("UTF-8")));

		InputStream resultedTemplate = templateManager.merge(templateInputStream, mailingAttributes);

		mergeTemplate = IOUtils.toString(resultedTemplate, StandardCharsets.UTF_8.name());

		return mergeTemplate;
	}

	private boolean sendSMSNotification(Map<String, Object> mailingAttributes,
			NotificationTemplateCode notificationTemplate) throws Exception {
		String primaryLanguageMergeTemplate = templateMerge(getTemplate(primaryLang, notificationTemplate + "_SMS"),
				mailingAttributes);

		if (languageType.equalsIgnoreCase("both")) {
			String secondaryLanguageMergeTemplate = templateMerge(
					getTemplate(secondaryLang, notificationTemplate + "_SMS"), mailingAttributes);
			primaryLanguageMergeTemplate = primaryLanguageMergeTemplate + LINE_SEPARATOR
					+ secondaryLanguageMergeTemplate;
		}

		SMSRequestDTO smsRequestDTO = new SMSRequestDTO();
		smsRequestDTO.setMessage(primaryLanguageMergeTemplate);
		smsRequestDTO.setNumber((String) mailingAttributes.get("phone"));
		RequestWrapper<SMSRequestDTO> req = new RequestWrapper<>();
		req.setRequest(smsRequestDTO);
		NotificationResponseDTO resp = restClient.postApi(env.getProperty(ApiName.SMSNOTIFIER.name()),
				MediaType.APPLICATION_JSON, req, NotificationResponseDTO.class, tokenGenerator.getToken());

		NotificationResponseDTO notifierResponse = new NotificationResponseDTO();
		notifierResponse.setMessage(resp.getResponse().getMessage());
		notifierResponse.setStatus(resp.getResponse().getStatus());
		if (resp.getResponse().getStatus().equalsIgnoreCase("success"))
			return true;
		return false;
	}

	private boolean sendEmailNotification(Map<String, Object> mailingAttributes,
			NotificationTemplateCode notificationTemplate, MultipartFile[] attachment, String subject)
			throws Exception {
		String primaryLanguageMergeTemplate = templateMerge(getTemplate(primaryLang, notificationTemplate + "_EMAIL"),
				mailingAttributes);
		if (languageType.equalsIgnoreCase("both")) {
			String secondaryLanguageMergeTemplate = templateMerge(
					getTemplate(secondaryLang, notificationTemplate + "_EMAIL"), mailingAttributes);
			primaryLanguageMergeTemplate = primaryLanguageMergeTemplate + LINE_SEPARATOR
					+ secondaryLanguageMergeTemplate;
		}
		LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		String[] mailTo = { mailingAttributes.get("email").toString() };
		String[] mailCc = notificationEmails.split("\\|");
		String apiHost = env.getProperty(ApiName.EMAILNOTIFIER.name());
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiHost);
		for (String item : mailTo) {
			builder.queryParam("mailTo", item);
		}

		if (mailCc != null) {
			for (String item : mailCc) {
				builder.queryParam("mailCc", item);
			}
		}

		builder.queryParam("mailSubject", subject);
		builder.queryParam("mailContent", primaryLanguageMergeTemplate);
		params.add("attachments", attachment);
		NotificationResponseDTO response = restClient.postApi(builder.build().toUriString(),
				MediaType.MULTIPART_FORM_DATA, params, NotificationResponseDTO.class, tokenGenerator.getToken());

		if (response.getResponse().getStatus().equals("success")) {
			return true;
		}
		return false;

	}

}
