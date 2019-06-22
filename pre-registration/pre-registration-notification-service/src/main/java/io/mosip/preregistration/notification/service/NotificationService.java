package io.mosip.preregistration.notification.service;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.EventId;
import io.mosip.preregistration.core.code.EventName;
import io.mosip.preregistration.core.code.EventType;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.core.util.NotificationUtil;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.notification.code.RequestCodes;
import io.mosip.preregistration.notification.dto.ResponseDTO;
import io.mosip.preregistration.notification.error.ErrorCodes;
import io.mosip.preregistration.notification.error.ErrorMessages;
import io.mosip.preregistration.notification.exception.BookingDetailsNotFoundException;
import io.mosip.preregistration.notification.exception.DemographicDetailsNotFoundException;
import io.mosip.preregistration.notification.exception.MandatoryFieldException;
import io.mosip.preregistration.notification.exception.NotificationSeriveException;
import io.mosip.preregistration.notification.exception.RestCallException;
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

	/**
	 * Reference for ${appointmentResourse.url} from property file
	 */
	@Value("${appointmentResourse.url}")
	private String appointmentResourseUrl;

	private Logger log = LoggerConfiguration.logConfig(NotificationService.class);

	Map<String, String> requiredRequestMap = new HashMap<>();
	/**
	 * Autowired reference for {@link #restTemplateBuilder}
	 */
	@Autowired
	RestTemplate restTemplate;

	@Value("${mosip.pre-registration.notification.id}")
	private String Id;

	@Value("${version}")
	private String version;

	/**
	 * 
	 */
	@Value("${demographic.resource.url}")
	private String demographicResourceUrl;
	/**
	 * 
	 */
	@Value("${preregistartion.response}")
	private String demographicResponse;

	@Value("${preregistartion.demographicDetails}")
	private String demographicDetails;

	@Value("${preregistartion.identity}")
	private String identity;

	@Value("${preregistartion.identity.email}")
	private String email;

	@Value("${preregistartion.identity.fullName}")
	private String fullName;

	@Value("${preregistartion.identity.phone}")
	private String phone;

	MainResponseDTO<ResponseDTO> response;

	/**
	 * Autowired reference for {@link #AuditLogUtil}
	 */
	@Autowired
	private AuditLogUtil auditLogUtil;
	
	@Autowired
	private ValidationUtil validationUtil;

	@PostConstruct
	public void setupBookingService() {
		requiredRequestMap.put("version", version);
	}

	public AuthUserDetails authUserDetails() {
		return (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

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
	public MainResponseDTO<ResponseDTO> sendNotification(String jsonString, String langCode, MultipartFile file) {

		response = new MainResponseDTO<>();

		ResponseDTO notificationResponse = new ResponseDTO();
		log.info("sessionId", "idType", "id", "In notification service of sendNotification ");
		requiredRequestMap.put("id", Id);
		response.setId(Id);
		response.setVersion(version);
		String resp = null;
		boolean isSuccess = false;
		try {
			MainRequestDTO<NotificationDTO> notificationReqDTO = serviceUtil.createNotificationDetails(jsonString);
			response.setId(notificationReqDTO.getId());
			response.setVersion(notificationReqDTO.getVersion());
			NotificationDTO notificationDto = notificationReqDTO.getRequest();
			if (ValidationUtil.requestValidator(serviceUtil.createRequestMap(notificationReqDTO), requiredRequestMap)&&validationUtil.langvalidation(langCode)) {
				notificationDtoValidation(notificationDto);
				if (notificationDto.isAdditionalRecipient()) {
					if (notificationDto.getMobNum() != null && !notificationDto.getMobNum().isEmpty()) {
						if( ValidationUtil.phoneValidator(notificationDto.getMobNum())) {
							notificationUtil.notify(RequestCodes.SMS.getCode(), notificationDto, langCode, file);
						}else {
							throw new MandatoryFieldException(ErrorCodes.PRG_PAM_ACK_007.getCode(),ErrorMessages.PHONE_VALIDATION_EXCEPTION.getMessage(), response);
						}
					}
					if (notificationDto.getEmailID() != null && !notificationDto.getEmailID().isEmpty()) {
						if(ValidationUtil.emailValidator(notificationDto.getEmailID())) {
							notificationUtil.notify(RequestCodes.EMAIL.getCode(), notificationDto, langCode, file);
						}else {
							throw new MandatoryFieldException(ErrorCodes.PRG_PAM_ACK_006.getCode(),ErrorMessages.EMAIL_VALIDATION_EXCEPTION.getMessage(), response);
						
						}
					}
					if ((notificationDto.getEmailID() == null || notificationDto.getEmailID().isEmpty()||!ValidationUtil.emailValidator(notificationDto.getEmailID()))
							&& (notificationDto.getMobNum() == null || notificationDto.getMobNum().isEmpty()||!ValidationUtil.phoneValidator(notificationDto.getMobNum()))) {
						throw new MandatoryFieldException(ErrorCodes.PRG_PAM_ACK_001.getCode(),
								ErrorMessages.MOBILE_NUMBER_OR_EMAIL_ADDRESS_NOT_FILLED.getMessage(), response);
					}
					notificationResponse.setMessage(RequestCodes.MESSAGE.getCode());
				} else {
					resp = getDemographicDetailsWithPreId(notificationDto, langCode, file);
					notificationResponse.setMessage(resp);
				}
			}

			response.setResponse(notificationResponse);
			isSuccess = true;
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In notification service of sendNotification " + ex.getMessage());
			new NotificationExceptionCatcher().handle(ex, response);
		} finally {
			response.setResponsetime(serviceUtil.getCurrentResponseTime());
			if (isSuccess) {
				setAuditValues(EventId.PRE_411.toString(), EventName.NOTIFICATION.toString(),
						EventType.SYSTEM.toString(),
						"Pre-Registration data is sucessfully trigger notification to the user",
						AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Failed to trigger notification to the user", AuditLogVariables.NO_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername());
			}
		}
		return response;
	}

	/**
	 * This method is calling demographic getApplication service to get the user
	 * emailId and mobile number
	 * 
	 * @param notificationDto
	 * @param langCode
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private String getDemographicDetailsWithPreId(NotificationDTO notificationDto, String langCode, MultipartFile file)
			throws IOException {
		String url = demographicResourceUrl + "/" + "applications" + "/" + notificationDto.getPreRegistrationId();
		ObjectMapper mapper = new ObjectMapper();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		HttpEntity<MainResponseDTO<DemographicResponseDTO>> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

		List<ServiceError> validationErrorList = ExceptionUtils.getServiceErrorList(responseEntity.getBody());
		if (!validationErrorList.isEmpty()) {
			throw new NotificationSeriveException(validationErrorList, response);
		}

		JsonNode responseNode = mapper.readTree(responseEntity.getBody());

		responseNode = responseNode.get(demographicResponse);

		responseNode = responseNode.get(demographicDetails);
		responseNode = responseNode.get(identity);

		notificationDto.setName(responseNode.get(fullName).get(0).get("value").asText());

		if (responseNode.get(email) != null) {
			String emailId = responseNode.get(email).asText();
			notificationDto.setEmailID(emailId);
			notificationUtil.notify(RequestCodes.EMAIL.getCode(), notificationDto, langCode, file);
		}
		if (responseNode.get(phone) != null) {
			String phoneNumber = responseNode.get(phone).asText();
			notificationDto.setMobNum(phoneNumber);
			notificationUtil.notify(RequestCodes.SMS.getCode(), notificationDto, langCode, file);

		}
		if (responseNode.get(email) == null && responseNode.get(phone) == null) {
			log.info("sessionId", "idType", "id",
					"In notification service of sendNotification failed to send Email and sms request ");
		}
		return RequestCodes.MESSAGE.getCode();
	}

	/**
	 * This method is used to audit all the trigger notification events
	 * 
	 * @param eventId
	 * @param eventName
	 * @param eventType
	 * @param description
	 * @param idType
	 */
	public void setAuditValues(String eventId, String eventName, String eventType, String description, String idType,
			String userId, String userName) {
		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto.setEventId(eventId);
		auditRequestDto.setEventName(eventName);
		auditRequestDto.setEventType(eventType);
		auditRequestDto.setDescription(description);
		auditRequestDto.setSessionUserId(userId);
		auditRequestDto.setSessionUserName(userName);
		auditRequestDto.setId(idType);
		auditRequestDto.setModuleId(AuditLogVariables.NOTIFY.toString());
		auditRequestDto.setModuleName(AuditLogVariables.NOTIFICATION_SERVICE.toString());
		auditLogUtil.saveAuditDetails(auditRequestDto);
	}

	public void notificationDtoValidation(NotificationDTO dto) throws IOException, ParseException  {
		getDemographicDetails(dto);
		if(!dto.getIsBatch()) {
			BookingRegistrationDTO bookingDTO = getAppointmentDetailsRestService(dto.getPreRegistrationId());
			String time = LocalTime
					.parse(bookingDTO.getSlotFromTime().toString(), DateTimeFormatter.ofPattern("HH:mm"))
					.format(DateTimeFormatter.ofPattern("hh:mm a")); 
			if(dto.getAppointmentDate()!=null && !dto.getAppointmentDate().trim().equals("")) {
			if (bookingDTO.getRegDate().equals(dto.getAppointmentDate())) {
				if(dto.getAppointmentTime()!=null&&!dto.getAppointmentTime().trim().equals("")) {
				
				if (!time.equals(dto.getAppointmentTime())) {
					throw new MandatoryFieldException(ErrorCodes.PRG_PAM_ACK_010.getCode(),
							ErrorMessages.APPOINTMENT_TIME_NOT_CORRECT.getMessage(), response);
				}
			}
				else {
					throw new MandatoryFieldException(ErrorCodes.PRG_PAM_ACK_002.getCode(),
							ErrorMessages.INCORRECT_MANDATORY_FIELDS.getMessage(),response);
				}
			}
				
				else {
				throw new MandatoryFieldException(ErrorCodes.PRG_PAM_ACK_009.getCode(),
						ErrorMessages.APPOINTMENT_DATE_NOT_CORRECT.getMessage(), response);
			}
				
		}
		
		else {
			throw new MandatoryFieldException(ErrorCodes.PRG_PAM_ACK_002.getCode(),
					ErrorMessages.INCORRECT_MANDATORY_FIELDS.getMessage(),response);
		}
		}
	}

	/**
	 * This method is calling demographic getApplication service to validate the demographic details
	 * 
	 * @param notificationDto
	 * @throws IOException
	 * @throws ParseException
	 */

	public void getDemographicDetails(NotificationDTO notificationDto) throws IOException, ParseException {
		try {
			String url = demographicResourceUrl + "/" + "applications" + "/" + notificationDto.getPreRegistrationId();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			ObjectMapper mapper = new ObjectMapper();
			HttpEntity<MainResponseDTO<DemographicResponseDTO>> httpEntity = new HttpEntity<>(headers);
			ResponseEntity<MainResponseDTO<DemographicResponseDTO>> responseEntity = restTemplate.exchange(url,
					HttpMethod.GET, httpEntity,
					new ParameterizedTypeReference<MainResponseDTO<DemographicResponseDTO>>() {
					});
			if (responseEntity.getBody().getErrors() != null) {
				throw new DemographicDetailsNotFoundException(responseEntity.getBody().getErrors(), response);
			}
			JsonNode responseNode = mapper
					.readTree(responseEntity.getBody().getResponse().getDemographicDetails().toJSONString());
			responseNode = responseNode.get(identity);
			if(!notificationDto.isAdditionalRecipient()) {
				if(notificationDto.getMobNum()!=null|| notificationDto.getEmailID()!=null) {
					log.error("sessionId", "idType", "id",
					"Not considering the requested mobilenumber/email since additional recipient is false ");
				}
			}
			if (!notificationDto.getIsBatch()&&!notificationDto.getName().equals(responseNode.get(fullName).get(0).get("value").asText())) {
				throw new MandatoryFieldException(ErrorCodes.PRG_PAM_ACK_008.getCode(),
						ErrorMessages.FULL_NAME_VALIDATION_EXCEPTION.getMessage(), response);
			}
		} catch (RestClientException  ex) {

			log.error("sessionId", "idType", "id",
					"In getDemographicDetails method of notification service - " + ex.getMessage());
			throw new RestCallException(ErrorCodes.PRG_PAM_ACK_011.getCode(),
					ErrorMessages.DEMOGRAPHIC_CALL_FAILED.getMessage());

		}
	}

	/**
	 * This  Method is used to retrieve booking data
	 * 
	 * @param preId
	 * @return BookingRegistrationDTO
	 * 
	 */
	public BookingRegistrationDTO getAppointmentDetailsRestService(String preId) {
		log.info("sessionId", "idType", "id", "In getAppointmentDetailsRestService method of notification service ");

		BookingRegistrationDTO bookingRegistrationDTO = null;
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("preRegistrationId", preId);
			String url = appointmentResourseUrl + "/appointment/";
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<MainResponseDTO<BookingRegistrationDTO>> httpEntity = new HttpEntity<>(headers);
			url += "{preRegistrationId}";
			log.info("sessionId", "idType", "id", "In getAppointmentDetailsRestService method URL- " + url);

			ResponseEntity<MainResponseDTO<BookingRegistrationDTO>> respEntity = restTemplate.exchange(url,
					HttpMethod.GET, httpEntity,
					new ParameterizedTypeReference<MainResponseDTO<BookingRegistrationDTO>>() {
					}, params);
			if (respEntity.getBody().getErrors() != null) {
				throw new BookingDetailsNotFoundException(respEntity.getBody().getErrors(), response);
			}
			bookingRegistrationDTO = respEntity.getBody().getResponse();
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In getAppointmentDetailsRestService method of notification service - " + ex.getMessage());
			throw new RestCallException(ErrorCodes.PRG_PAM_ACK_012.getCode(),
					ErrorMessages.BOOKING_CALL_FAILED.getMessage());
		}
		return bookingRegistrationDTO;
	}
}
