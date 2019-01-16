package io.mosip.registration.processor.stages.uingenerator.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.dto.config.GlobalConfig;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.TemplateResponseDto;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.message.sender.exception.ConfigurationNotFoundException;
import io.mosip.registration.processor.message.sender.exception.EmailIdNotFoundException;
import io.mosip.registration.processor.message.sender.exception.PhoneNumberNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateGenerationFailedException;
import io.mosip.registration.processor.message.sender.exception.TemplateNotFoundException;
import io.mosip.registration.processor.message.sender.utility.MessageSenderUtil;

/**
 * The Class TriggerNotificationForUIN.
 * @author M1049387
 */
@RefreshScope
@Component
public class TriggerNotificationForUIN {


	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(TriggerNotificationForUIN.class);


	/** The notification emails. */
	@Value("${registration.processor.notification.emails}")
	private String notificationEmails;

	/** The notification email subject. */
	@Value("${registration.processor.notification.subject}")
	private String notificationEmailSubject;

	/** The Constant TEMPLATES. */
	private static final String TEMPLATES = "templates";

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The service. */
	@Autowired
	private MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> service;

	/** The utility. */
	@Autowired
	private MessageSenderUtil utility;

	/** The Constant SMS_TEMPLATE_CODE. */
	private static final String SMS_TEMPLATE_UIN_CREATE_CODE = "SMS_TEMP_FOR_UIN_GEN";

	/** The Constant EMAIL_TEMPLATE_CODE. */
	private static final String EMAIL_TEMPLATE_UIN_CREATE_CODE = "EMAIL_TEMP_FOR_UIN_GEN";

	/** The Constant EMAIL_TEMPLATE_CODE. */
	private static final String SMS_TEMPLATE_UIN_UPDATE_CODE = "SMS_TEMP_FOR_UIN_GEN";

	/** The Constant EMAIL_TEMPLATE_CODE. */
	private static final String EMAIL_TEMPLATE_UIN_UPDATE_CODE = "EMAIL_TEMP_FOR_UIN_GEN";

	/** The Constant SMS_TYPE. */
	private static final String SMS_TYPE = "SMS";

	/** The Constant EMAIL_TYPE. */
	private static final String EMAIL_TYPE = "EMAIL";

	/** The is template available. */
	boolean isTemplateAvailable=false;
	
	/**
	 * Trigger notification.
	 *
	 * @param uin the uin
	 * @param isSuccess the is success
	 */
	public void triggerNotification(String uin, boolean isSuccess ){
		
		String smsTemplateCode = "";
		String emailTemplateCode = "";

		Map<String, Object> attributes = new HashMap<>();
		String[] ccEMailList = null;
		try {
			String getIdentityJsonString = MessageSenderUtil.getJson(utility.getConfigServerFileStorageURL(),
					utility.getGetGlobalConfigJson());
			ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
			GlobalConfig jsonObject;
			jsonObject = mapIdentityJsonStringToObject.readValue(getIdentityJsonString, GlobalConfig.class);
			String notificationTypes= jsonObject.getNotificationtype();
			if (notificationTypes.isEmpty()) {
				throw new ConfigurationNotFoundException(
						PlatformErrorMessages.RPR_TEM_CONFIGURATION_NOT_FOUND.getCode());
			}
			String[] allNotificationTypes = notificationTypes.split("\\|");

			if (notificationEmails != null && notificationEmails.length() > 0) {
				ccEMailList = notificationEmails.split("\\|");
			}


			if(isSuccess) {
				smsTemplateCode=SMS_TEMPLATE_UIN_CREATE_CODE;
				emailTemplateCode=EMAIL_TEMPLATE_UIN_CREATE_CODE;
			}else {
				smsTemplateCode=SMS_TEMPLATE_UIN_UPDATE_CODE;
				emailTemplateCode=EMAIL_TEMPLATE_UIN_UPDATE_CODE;
			}

			for (String notificationType : allNotificationTypes) {

				if (notificationType.equalsIgnoreCase(SMS_TYPE) && isTemplateAvailable(smsTemplateCode)) {

					service.sendSmsNotification(smsTemplateCode, uin, IdType.UIN, attributes);

				} else if (notificationType.equalsIgnoreCase(EMAIL_TYPE) && isTemplateAvailable(emailTemplateCode)) {

					service.sendEmailNotification(emailTemplateCode, uin, IdType.UIN, attributes, ccEMailList,notificationEmailSubject, null);

				}else {
					throw new TemplateNotFoundException("sms and email template not found");
				}
			}

		} catch (EmailIdNotFoundException | PhoneNumberNotFoundException | TemplateGenerationFailedException  e) {
			
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.UIN.toString(),uin,"Notification trigger failed"+e.getMessage()+ExceptionUtils.getStackTrace(e));
			throw new TemplateGenerationFailedException(PlatformErrorMessages.RPR_TEM_PROCESSING_FAILURE.getCode());
		} catch (JsonParseException |JsonMappingException jp) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.UIN.toString(),uin,jp.getMessage()+ExceptionUtils.getStackTrace(jp));

		}catch(TemplateNotFoundException tnfe) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.UIN.toString(),uin,tnfe.getMessage()+ExceptionUtils.getStackTrace(tnfe));

		}catch (Exception ex) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.UIN.toString(),uin,ex.getMessage()+ExceptionUtils.getStackTrace(ex));

		}

	}


	/**
	 * Checks if is template available.
	 *
	 * @param templateCode the template code
	 * @return true, if is template available
	 * @throws ApisResourceAccessException the apis resource access exception
	 */
	private boolean isTemplateAvailable(String templateCode) throws ApisResourceAccessException {

		List<String> pathSegments = new ArrayList<>();
		pathSegments.add(TEMPLATES);
		TemplateResponseDto template = (TemplateResponseDto) restClientService.getApi(ApiName.MASTER, pathSegments,	"", "", TemplateResponseDto.class);
		template.getTemplates().forEach(dto -> {		
			if(dto.getTemplateTypeCode().equalsIgnoreCase(templateCode)) {
				isTemplateAvailable= true;
			}});

		return isTemplateAvailable;
	}

}
