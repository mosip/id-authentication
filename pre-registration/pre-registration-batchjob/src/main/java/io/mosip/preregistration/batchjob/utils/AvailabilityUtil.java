package io.mosip.preregistration.batchjob.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.batchjob.code.ErrorCodes;
import io.mosip.preregistration.batchjob.code.ErrorMessages;
import io.mosip.preregistration.batchjob.entity.AvailibityEntity;
import io.mosip.preregistration.batchjob.exception.NoRecordFoundException;
import io.mosip.preregistration.batchjob.exception.NotificationException;
import io.mosip.preregistration.batchjob.exception.RestCallException;
import io.mosip.preregistration.batchjob.exception.util.BatchServiceExceptionCatcher;
import io.mosip.preregistration.batchjob.model.HolidayDto;
import io.mosip.preregistration.batchjob.model.RegistrationCenterDto;
import io.mosip.preregistration.batchjob.model.RegistrationCenterHolidayDto;
import io.mosip.preregistration.batchjob.model.RegistrationCenterResponseDto;
import io.mosip.preregistration.batchjob.repository.utils.BatchJpaRepositoryImpl;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.EventId;
import io.mosip.preregistration.core.code.EventName;
import io.mosip.preregistration.core.code.EventType;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import io.mosip.preregistration.core.common.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.core.common.dto.RequestWrapper;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.common.entity.RegistrationBookingEntity;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.AuditLogUtil;
/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
public class AvailabilityUtil {

	/**
	 * Reference for ${preregistration.availability.sync} from property file
	 */
	@Value("${preregistration.availability.sync}")
	int syncDays;

	@Value("${version}")
	String versionUrl;

	@Value("${mosip.preregistration.booking.availability.sync.id}")
	String idUrlSync;

	@Value("${mosip.primary-language}")
	String primaryLang;

	@Value("${notification.url}")
	private String notificationResourseurl;

	@Value("${mosip.batch.token.authmanager.userName}")
	private String auditUsername;

	@Value("${mosip.batch.token.authmanager.appId}")
	private String auditUserId;

	/**
	 * Reference for ${holiday.url} from property file
	 */
	@Value("${holiday.url}")
	String holidayListUrl;

	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;

	/**
	 * Reference for ${regCenter.url} from property file
	 */
	@Value("${regCenter.url}")
	String regCenterUrl;

	@Value("${batch.appointment.cancel}")
	String cancelResourceUrl;

	/**
	 * Autowired reference for {@link #batchServiceDAO}
	 */
	@Autowired
	private BatchJpaRepositoryImpl batchServiceDAO;

	@Autowired
	private AuditLogUtil auditLogUtil;

	/**
	 * Autowired reference for {@link #restTemplateBuilder}
	 */
	@Autowired
	RestTemplate restTemplate;

	private Logger log = LoggerConfiguration.logConfig(AvailabilityUtil.class);

	public MainResponseDTO<String> addAvailability(HttpHeaders headers) {
		log.info("sessionId", "idType", "id", "In addAvailability method of Booking Service");
		MainResponseDTO<String> response = new MainResponseDTO<>();
		response.setId(idUrlSync);
		response.setVersion(versionUrl);
		boolean isSaveSuccess = false;
		try {
			LocalDate endDate = LocalDate.now().plusDays(syncDays - 1);
			List<RegistrationCenterDto> regCenter = getRegCenterMasterData(headers);
			List<RegistrationCenterDto> regCenterDtos = regCenter.stream()
					.filter(regCenterDto -> regCenterDto.getLangCode().equals(primaryLang))
					.collect(Collectors.toList());
			List<String> regCenterDumped = batchServiceDAO.findRegCenter(LocalDate.now());
			for (RegistrationCenterDto regDto : regCenterDtos) {
				List<LocalDate> insertedDate = batchServiceDAO.findDistinctDate(LocalDate.now(), regDto.getId());
				List<String> holidaylist = getHolidayListMasterData(regDto,headers);
				regCenterDumped.remove(regDto.getId());
				for (LocalDate sDate = LocalDate.now(); (sDate.isBefore(endDate)
						|| sDate.isEqual(endDate)); sDate = sDate.plusDays(1)) {

					if (insertedDate.isEmpty()) {
						timeSlotCalculator(regDto, holidaylist, sDate, batchServiceDAO);
					} else {
						List<AvailibityEntity> regSlots = batchServiceDAO.findSlots(sDate, regDto.getId());
						if (regSlots.size() == 1) {
							batchServiceDAO.deleteSlots(regDto.getId(), sDate);
							timeSlotCalculator(regDto, holidaylist, sDate, batchServiceDAO);
						} else if (holidaylist.contains(sDate.toString())) {
							List<RegistrationBookingEntity> regBookingEntityList = batchServiceDAO
									.findAllPreIds(regDto.getId(), sDate);
							if (!regBookingEntityList.isEmpty()) {
								for (int i = 0; i < regBookingEntityList.size(); i++) {
									if (batchServiceDAO.getDemographicStatus(
											regBookingEntityList.get(i).getDemographicEntity().getPreRegistrationId())
											.equals(StatusCodes.BOOKED.getCode())) {
										cancelBooking(regBookingEntityList.get(i).getDemographicEntity()
												.getPreRegistrationId(),headers);
										sendNotification(regBookingEntityList.get(i),headers);
									}
								}
							}
							batchServiceDAO.deleteSlots(regDto.getId(), sDate);
							timeSlotCalculator(regDto, holidaylist, sDate, batchServiceDAO);
						} else if (!insertedDate.contains(sDate)) {
							timeSlotCalculator(regDto, holidaylist, sDate, batchServiceDAO);
						}
					}
				}

			}
			if (!regCenterDumped.isEmpty()) {
				for (int i = 0; i < regCenterDumped.size(); i++) {
					List<RegistrationBookingEntity> entityList = batchServiceDAO
							.findAllPreIdsByregID(regCenterDumped.get(i), LocalDate.now());
					if (!entityList.isEmpty()) {
						for (int j = 0; j < entityList.size(); j++) {
							if (batchServiceDAO
									.getDemographicStatus(
											entityList.get(j).getDemographicEntity().getPreRegistrationId())
									.equals(StatusCodes.BOOKED.getCode())) {
								cancelBooking(entityList.get(j).getDemographicEntity().getPreRegistrationId(),headers);
								sendNotification(entityList.get(j),headers);
							}
						}
					}

					batchServiceDAO.deleteAllSlotsByRegId(regCenterDumped.get(i), LocalDate.now());
				}
			}
			isSaveSuccess = true;
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In addAvailability method of Booking Service- " + ex.getMessage());
			new BatchServiceExceptionCatcher().handle(ex);
		} finally {
			response.setResponsetime(getCurrentResponseTime());
			if (isSaveSuccess) {
				setAuditValues(EventId.PRE_407.toString(), EventName.PERSIST.toString(), EventType.SYSTEM.toString(),
						"Availability for booking successfully saved in the database",
						AuditLogVariables.MULTIPLE_ID.toString(), auditUserId, auditUsername, null);
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"addAvailability failed", AuditLogVariables.NO_ID.toString(), auditUserId, auditUsername, null);
			}
		}
		response.setResponsetime(getCurrentResponseTime());
		response.setResponse("MASTER_DATA_SYNCED_SUCCESSFULLY");
		return response;
	}

	private boolean cancelBooking(String preRegistrationId,HttpHeaders headers) {

		log.info("sessionId", "idType", "id", "In cancelBooking method of Availability Util");
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("preRegistrationId", preRegistrationId);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(cancelResourceUrl + "/batch/appointment/");
			//headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<PreRegistartionStatusDTO>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			uriBuilder += "{preRegistrationId}";
			log.info("sessionId", "idType", "id", "In cancelBooking method of Availability Util URL- " + uriBuilder);

			ResponseEntity<MainResponseDTO<CancelBookingResponseDTO>> respEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.PUT, httpEntity,
					new ParameterizedTypeReference<MainResponseDTO<CancelBookingResponseDTO>>() {
					}, params);

			if (respEntity.getBody().getErrors() == null) {
				ObjectMapper mapper = new ObjectMapper();
				PreRegistartionStatusDTO preRegResponsestatusDto = mapper
						.convertValue(respEntity.getBody().getResponse(), PreRegistartionStatusDTO.class);

				String statusCode = preRegResponsestatusDto.getStatusCode().trim();

				if (!statusCode.equals(StatusCodes.BOOKED.getCode())) {
					if (statusCode.equals(StatusCodes.PENDING_APPOINTMENT.getCode())) {
						throw new NoRecordFoundException(ErrorCodes.PRG_PAM_BAT_016.getCode(),
								ErrorMessages.BOOKING_DATA_NOT_FOUND.getMessage());
					}

					else {
						throw new NoRecordFoundException(ErrorCodes.PRG_PAM_BAT_017.getCode(),
								ErrorMessages.APPOINTMENT_CANNOT_BE_CANCELED.getMessage());
					}

				}
			} else {
				for (ExceptionJSONInfoDTO dto : respEntity.getBody().getErrors()) {
					throw new NoRecordFoundException(dto.getErrorCode(), dto.getMessage());
				}

			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In cancelBooking method of Availability Util for HttpClientErrorException- " + ex.getMessage());
			throw new NoRecordFoundException(ErrorCodes.PRG_PAM_BAT_018.getCode(),
					ErrorMessages.CANCEL_BOOKING_BATCH_CALL_FAILED.getMessage());
		}
		return true;
	}

	/**
	 * This method will call kernel service for registration center date.
	 * 
	 * @return List of RegistrationCenterDto
	 */
	public List<RegistrationCenterDto> getRegCenterMasterData(HttpHeaders headers) {
		log.info("sessionId", "idType", "id", "In callRegCenterDateRestService method of Booking Service Util");
		List<RegistrationCenterDto> regCenter = null;
		try {
			UriComponentsBuilder regbuilder = UriComponentsBuilder.fromHttpUrl(regCenterUrl);
			/*HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);*/
			HttpEntity<RequestWrapper<RegistrationCenterResponseDto>> entity = new HttpEntity<>(headers);
			String uriBuilder = regbuilder.build().encode().toUriString();
			log.info("sessionId", "idType", "id",
					"In callRegCenterDateRestService method of Booking Service URL- " + uriBuilder);
			ResponseEntity<ResponseWrapper<RegistrationCenterResponseDto>> responseEntity = restTemplate.exchange(
					uriBuilder, HttpMethod.GET, entity,
					new ParameterizedTypeReference<ResponseWrapper<RegistrationCenterResponseDto>>() {
					});
			if (responseEntity.getBody().getErrors() != null && !responseEntity.getBody().getErrors().isEmpty()) {
				throw new NoRecordFoundException(responseEntity.getBody().getErrors().get(0).getErrorCode(),
						responseEntity.getBody().getErrors().get(0).getMessage());
			}
			regCenter = responseEntity.getBody().getResponse().getRegistrationCenters();
			if (regCenter == null || regCenter.isEmpty()) {
				throw new NoRecordFoundException(ErrorCodes.PRG_PAM_BAT_011.getCode(),
						ErrorMessages.MASTER_DATA_NOT_FOUND.getMessage());
			}

		} catch (HttpClientErrorException ex) {
			log.error("sessionId", "idType", "id",
					"In callRegCenterDateRestService method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());
			throw new RestCallException(ErrorCodes.PRG_PAM_BAT_011.getCode(),
					ErrorMessages.MASTER_DATA_NOT_FOUND.getMessage());
		}
		return regCenter;
	}

	/**
	 * This method will call kernel service holiday list
	 * 
	 * @param regDto
	 * @return List of string
	 */
	public List<String> getHolidayListMasterData(RegistrationCenterDto regDto,HttpHeaders headers) {
		log.info("sessionId", "idType", "id", "In callGetHolidayListRestService method of Booking Service Util");
		List<String> holidaylist = null;
		try {

			String holidayUrl = holidayListUrl + regDto.getLangCode() + "/" + regDto.getId() + "/"
					+ LocalDate.now().getYear();
			UriComponentsBuilder builder2 = UriComponentsBuilder.fromHttpUrl(holidayUrl);
			HttpEntity<RequestWrapper<RegistrationCenterHolidayDto>> httpHolidayEntity = new HttpEntity<>(headers);
			String uriBuilder = builder2.build().encode().toUriString();
			log.info("sessionId", "idType", "id",
					"In callGetHolidayListRestService method of Booking Service URL- " + uriBuilder);
			ResponseEntity<ResponseWrapper<RegistrationCenterHolidayDto>> responseEntity2 = restTemplate.exchange(
					uriBuilder, HttpMethod.GET, httpHolidayEntity,
					new ParameterizedTypeReference<ResponseWrapper<RegistrationCenterHolidayDto>>() {
					});
			if (responseEntity2.getBody().getErrors() != null && !responseEntity2.getBody().getErrors().isEmpty()) {
				throw new NoRecordFoundException(responseEntity2.getBody().getErrors().get(0).getErrorCode(),
						responseEntity2.getBody().getErrors().get(0).getMessage());
			}
			holidaylist = new ArrayList<>();
			if (!responseEntity2.getBody().getResponse().getHolidays().isEmpty()) {
				for (HolidayDto holiday : responseEntity2.getBody().getResponse().getHolidays()) {
					holidaylist.add(holiday.getHolidayDate());
				}
			}

		} catch (HttpClientErrorException ex) {
			log.error("sessionId", "idType", "id",
					"In callGetHolidayListRestService method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());
			throw new RestCallException(ErrorCodes.PRG_PAM_BAT_011.getCode(),
					ErrorMessages.MASTER_DATA_NOT_FOUND.getMessage());

		}
		return holidaylist;
	}

	/**
	 * This method will do booking time slots.
	 * 
	 * @param regDto
	 * @param holidaylist
	 * @param sDate
	 * @param batchServiceDAO
	 */
	public void timeSlotCalculator(RegistrationCenterDto regDto, List<String> holidaylist, LocalDate sDate,
			BatchJpaRepositoryImpl batchServiceDAO) {
		log.info("sessionId", "idType", "id", "In timeSlotCalculator method of Booking Service Util");
		if (holidaylist.contains(sDate.toString())) {
			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String text = "2016-11-09 00:00:00";
			LocalDateTime localDateTime = LocalDateTime.parse(text, format);
			LocalTime localTime = localDateTime.toLocalTime();
			saveAvailability(regDto, sDate, localTime, localTime, batchServiceDAO);

		} else {

			int window1 = ((regDto.getLunchStartTime().getHour() * 60 + regDto.getLunchStartTime().getMinute())
					- (regDto.getCenterStartTime().getHour() * 60 + regDto.getCenterStartTime().getMinute()))
					/ (regDto.getPerKioskProcessTime().getHour() * 60 + regDto.getPerKioskProcessTime().getMinute());

			int window2 = ((regDto.getCenterEndTime().getHour() * 60 + regDto.getCenterEndTime().getMinute())
					- (regDto.getLunchEndTime().getHour() * 60 + regDto.getLunchEndTime().getMinute()))
					/ (regDto.getPerKioskProcessTime().getHour() * 60 + regDto.getPerKioskProcessTime().getMinute());

			int extraTime1 = ((regDto.getLunchStartTime().getHour() * 60 + regDto.getLunchStartTime().getMinute())
					- (regDto.getCenterStartTime().getHour() * 60 + regDto.getCenterStartTime().getMinute()))
					% (regDto.getPerKioskProcessTime().getHour() * 60 + regDto.getPerKioskProcessTime().getMinute());

			int extraTime2 = ((regDto.getCenterEndTime().getHour() * 60 + regDto.getCenterEndTime().getMinute())
					- (regDto.getLunchEndTime().getHour() * 60 + regDto.getLunchEndTime().getMinute()))
					% (regDto.getPerKioskProcessTime().getHour() * 60 + regDto.getPerKioskProcessTime().getMinute());

			LocalTime currentTime1 = regDto.getCenterStartTime();
			for (int i = 0; i < window1; i++) {
				if (i == (window1 - 1)) {
					LocalTime toTime = currentTime1.plusMinutes(regDto.getPerKioskProcessTime().getMinute())
							.plusMinutes(extraTime1);
					saveAvailability(regDto, sDate, currentTime1, toTime, batchServiceDAO);

				} else {
					LocalTime toTime = currentTime1.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
					saveAvailability(regDto, sDate, currentTime1, toTime, batchServiceDAO);
				}
				currentTime1 = currentTime1.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
			}

			LocalTime currentTime2 = regDto.getLunchEndTime();
			for (int i = 0; i < window2; i++) {
				if (i == (window2 - 1)) {
					LocalTime toTime = currentTime2.plusMinutes(regDto.getPerKioskProcessTime().getMinute())
							.plusMinutes(extraTime2);
					saveAvailability(regDto, sDate, currentTime2, toTime, batchServiceDAO);

				} else {
					LocalTime toTime = currentTime2.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
					saveAvailability(regDto, sDate, currentTime2, toTime, batchServiceDAO);
				}
				currentTime2 = currentTime2.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
			}
		}
	}

	private void saveAvailability(RegistrationCenterDto regDto, LocalDate date, LocalTime currentTime, LocalTime toTime,
			BatchJpaRepositoryImpl batchServiceDAO) {
		log.info("sessionId", "idType", "id", "In saveAvailability method of Booking Service Util");
		AvailibityEntity avaEntity = new AvailibityEntity();
		avaEntity.setRegDate(date);
		avaEntity.setRegcntrId(regDto.getId());
		avaEntity.setFromTime(currentTime);
		avaEntity.setToTime(toTime);
		avaEntity.setCrBy("Admin");
		avaEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		if (isNull(regDto.getContactPerson())) {
			avaEntity.setCrBy("Admin");
		} else {
			avaEntity.setCrBy(regDto.getContactPerson());
		}
		if (currentTime.equals(toTime)) {
			avaEntity.setAvailableKiosks(0);
		} else {
			avaEntity.setAvailableKiosks(regDto.getNumberOfKiosks());
		}
		batchServiceDAO.saveAvailability(avaEntity);
	}

	/**
	 * This method is used as Null checker for different input keys.
	 *
	 * @param key
	 *            pass the key
	 * @return true if key not null and return false if key is null.
	 */

	public boolean isNull(Object key) {
		log.info("sessionId", "idType", "id", "In isNull method of Booking Service Util");
		if (key instanceof String) {
			if (key.equals("") || ((String) key).trim().length() == 0)
				return true;
		} else {
			if (key == null)
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param registrationBookingEntity
	 * @throws JsonProcessingException
	 */
	public void sendNotification(RegistrationBookingEntity registrationBookingEntity,HttpHeaders headers) throws JsonProcessingException {
		log.info("sessionId", "idType", "id", "In sendNotification method of Booking Service");
		NotificationDTO notification = new NotificationDTO();
		notification.setAppointmentDate(registrationBookingEntity.getRegDate().toString());
		notification.setPreRegistrationId(registrationBookingEntity.getDemographicEntity().getPreRegistrationId());
		String time = LocalTime
				.parse(registrationBookingEntity.getSlotFromTime().toString(), DateTimeFormatter.ofPattern("HH:mm"))
				.format(DateTimeFormatter.ofPattern("hh:mm a"));
		notification.setAppointmentTime(time);
		notification.setAdditionalRecipient(false);
		notification.setIsBatch(true);
		emailNotification(notification, primaryLang,headers);
	}

	/**
	 * 
	 * @param notificationDTO
	 * @param langCode
	 * @return NotificationResponseDTO
	 * @throws JsonProcessingException
	 */
	public void emailNotification(NotificationDTO notificationDTO, String langCode,HttpHeaders headers) throws JsonProcessingException {
		String emailResourseUrl = notificationResourseurl + "/notify";
		ResponseEntity<String> resp = null;
		//HttpHeaders headers = new HttpHeaders();
		MainRequestDTO<NotificationDTO> request = new MainRequestDTO<>();
		ObjectMapper mapper = new ObjectMapper();
		mapper.setTimeZone(TimeZone.getDefault());
		try {
			request.setRequest(notificationDTO);
			request.setId("mosip.pre-registration.notification.notify");
			request.setVersion("1.0");
			request.setRequesttime(new Date());
			//headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<Object, Object> emailMap = new LinkedMultiValueMap<>();
			emailMap.add("NotificationRequestDTO", mapper.writeValueAsString(request));
			emailMap.add("langCode", langCode);
			HttpEntity<MultiValueMap<Object, Object>> httpEntity = new HttpEntity<>(emailMap, headers);
			log.info("sessionId", "idType", "id",
					"In emailNotification method of NotificationUtil service emailResourseUrl: " + emailResourseUrl);
			resp = restTemplate.exchange(emailResourseUrl, HttpMethod.POST, httpEntity, String.class);
			List<ServiceError> validationErrorList = ExceptionUtils.getServiceErrorList(resp.getBody());
			if (validationErrorList != null && !validationErrorList.isEmpty()) {
				throw new NotificationException(validationErrorList, null);
			}
		} catch (HttpClientErrorException ex) {
			log.error("sessionId", "idType", "id",
					"In emailNotification method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());
			throw new RestCallException(ErrorCodes.PRG_PAM_BAT_012.getCode(),
					ErrorMessages.NOTIFICATION_CALL_FAILED.getMessage());

		}
	}

	public String getCurrentResponseTime() {
		log.info("sessionId", "idType", "id", "In getCurrentResponseTime method of Booking Service Util");
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), utcDateTimePattern);
	}

	/**
	 * This method is used to audit all the booking events
	 * 
	 * @param eventId
	 * @param eventName
	 * @param eventType
	 * @param description
	 * @param idType
	 */
	public void setAuditValues(String eventId, String eventName, String eventType, String description, String idType,
			String userId, String userName, String ref_id) {
		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto.setEventId(eventId);
		auditRequestDto.setEventName(eventName);
		auditRequestDto.setEventType(eventType);
		auditRequestDto.setDescription(description);
		auditRequestDto.setId(idType);
		auditRequestDto.setSessionUserId(userId);
		auditRequestDto.setSessionUserName(userName);
		auditRequestDto.setModuleId(AuditLogVariables.BOOK.toString());
		auditRequestDto.setModuleName(AuditLogVariables.BOOKING_SERVICE.toString());
		auditRequestDto.setId(ref_id);
		auditLogUtil.saveAuditDetails(auditRequestDto);
	}

}
