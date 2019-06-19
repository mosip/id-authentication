/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.service.util;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.booking.codes.RequestCodes;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.DateTimeDto;
import io.mosip.preregistration.booking.dto.HolidayDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterHolidayDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterResponseDto;
import io.mosip.preregistration.booking.dto.SlotDto;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingPK;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.AppointmentCannotBeCanceledException;
import io.mosip.preregistration.booking.exception.AppointmentReBookingFailedException;
import io.mosip.preregistration.booking.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDataNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDateNotSeletectedException;
import io.mosip.preregistration.booking.exception.BookingPreIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingRegistrationCenterIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingTimeSlotNotSeletectedException;
import io.mosip.preregistration.booking.exception.DemographicGetStatusException;
import io.mosip.preregistration.booking.exception.DemographicStatusUpdationException;
import io.mosip.preregistration.booking.exception.MasterDataNotAvailableException;
import io.mosip.preregistration.booking.exception.NotificationException;
import io.mosip.preregistration.booking.exception.RestCallException;
import io.mosip.preregistration.booking.exception.TimeSpanException;
import io.mosip.preregistration.booking.repository.impl.BookingDAO;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import io.mosip.preregistration.core.common.dto.NotificationResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.core.common.dto.RequestWrapper;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.UUIDGeneratorUtil;

/**
 * This class provides the utility methods for Booking application.
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
@Component
public class BookingServiceUtil {

	/**
	 * Autowired reference for {@link #restTemplateBuilder}
	 */
	@Autowired
	RestTemplate restTemplate;

	/**
	 * Reference for ${regCenter.url} from property file
	 */
	@Value("${regCenter.url}")
	String regCenterUrl;

	/**
	 * Reference for ${holiday.url} from property file
	 */
	@Value("${holiday.url}")
	String holidayListUrl;

	/**
	 * Reference for ${demographic.resource.url} from property file
	 */
	@Value("${demographic.resource.url}")
	private String preRegResourceUrl;

	@Value("${preregistration.timespan.cancel}")
	private long timeSpanCheckForCancel;

	@Value("${preregistration.timespan.rebook}")
	private long timeSpanCheckForRebook;

	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;

	@Value("${notification.url}")
	private String notificationResourseurl;
	
	@Value("${preregistration.country.specific.zoneId}")
	private String specificZoneId;

	private Logger log = LoggerConfiguration.logConfig(BookingServiceUtil.class);

	public AuthUserDetails authUserDetails() {
		return (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	/**
	 * This method will call kernel service for registration center date.
	 * 
	 * @return List of RegistrationCenterDto
	 */
	public List<RegistrationCenterDto> getRegCenterMasterData() {
		log.info("sessionId", "idType", "id", "In callRegCenterDateRestService method of Booking Service Util");
		List<RegistrationCenterDto> regCenter = null;
		try {
			// RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder regbuilder = UriComponentsBuilder.fromHttpUrl(regCenterUrl);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<RequestWrapper<RegistrationCenterResponseDto>> entity = new HttpEntity<>(headers);
			String uriBuilder = regbuilder.build().encode().toUriString();
			log.info("sessionId", "idType", "id",
					"In callRegCenterDateRestService method of Booking Service URL- " + uriBuilder);
			ResponseEntity<ResponseWrapper<RegistrationCenterResponseDto>> responseEntity = restTemplate.exchange(
					uriBuilder, HttpMethod.GET, entity,
					new ParameterizedTypeReference<ResponseWrapper<RegistrationCenterResponseDto>>() {
					});
			if (responseEntity.getBody().getErrors() != null && !responseEntity.getBody().getErrors().isEmpty()) {
				throw new MasterDataNotAvailableException(responseEntity.getBody().getErrors().get(0).getErrorCode(),
						responseEntity.getBody().getErrors().get(0).getMessage());
			}
			regCenter = responseEntity.getBody().getResponse().getRegistrationCenters();
			if (regCenter == null || regCenter.isEmpty()) {
				throw new MasterDataNotAvailableException(ErrorCodes.PRG_BOOK_RCI_020.getCode(),
						ErrorMessages.MASTER_DATA_NOT_FOUND.getMessage());
			}

		} catch (HttpClientErrorException ex) {
			log.error("sessionId", "idType", "id",
					"In callRegCenterDateRestService method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());
			throw new RestCallException(ErrorCodes.PRG_BOOK_RCI_020.getCode(),
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
	public List<String> getHolidayListMasterData(RegistrationCenterDto regDto) {
		log.info("sessionId", "idType", "id", "In callGetHolidayListRestService method of Booking Service Util");
		List<String> holidaylist = null;
		try {

			String holidayUrl = holidayListUrl + regDto.getLangCode() + "/" + regDto.getId() + "/"
					+ LocalDate.now().getYear();
			UriComponentsBuilder builder2 = UriComponentsBuilder.fromHttpUrl(holidayUrl);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<RequestWrapper<RegistrationCenterHolidayDto>> httpHolidayEntity = new HttpEntity<>(headers);
			String uriBuilder = builder2.build().encode().toUriString();
			log.info("sessionId", "idType", "id",
					"In callGetHolidayListRestService method of Booking Service URL- " + uriBuilder);
			ResponseEntity<ResponseWrapper<RegistrationCenterHolidayDto>> responseEntity2 = restTemplate.exchange(
					uriBuilder, HttpMethod.GET, httpHolidayEntity,
					new ParameterizedTypeReference<ResponseWrapper<RegistrationCenterHolidayDto>>() {
					});
			if (responseEntity2.getBody().getErrors() != null && !responseEntity2.getBody().getErrors().isEmpty()) {
				throw new MasterDataNotAvailableException(responseEntity2.getBody().getErrors().get(0).getErrorCode(),
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
			throw new RestCallException(ErrorCodes.PRG_BOOK_RCI_020.getCode(),
					ErrorMessages.MASTER_DATA_NOT_FOUND.getMessage());

		}
		return holidaylist;
	}

	/**
	 * This method will call demographic service for update status.
	 * 
	 * @param preId
	 * @param status
	 * @return response entity
	 */
	public boolean callUpdateStatusRestService(String preId, String status) {
		log.info("sessionId", "idType", "id", "In callUpdateStatusRestService method of Booking Service Util");
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("preRegistrationId", preId);
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(preRegResourceUrl + "/applications/status/{preRegistrationId}");

			URI uri = builder.buildAndExpand(params).toUri();
			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(uri).queryParam("statusCode", status);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<String>> httpEntity = new HttpEntity<>(headers);
			String uriBuilderString = uriBuilder.build().encode().toUriString();
			log.info("sessionId", "idType", "id", "Call Update Status in demographic URL : " + uriBuilderString);
			ResponseEntity<MainResponseDTO<String>> bookingResponse = restTemplate.exchange(uriBuilderString,
					HttpMethod.PUT, httpEntity, new ParameterizedTypeReference<MainResponseDTO<String>>() {
					}, params);
			if (bookingResponse.getBody().getErrors() != null) {
				throw new DemographicStatusUpdationException(
						bookingResponse.getBody().getErrors().get(0).getErrorCode(),
						bookingResponse.getBody().getErrors().get(0).getMessage());
			}

			return true;

		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callUpdateStatusRestService method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());

			throw new DemographicStatusUpdationException(ErrorCodes.PRG_BOOK_RCI_011.getCode(),
					ErrorMessages.DEMOGRAPHIC_SERVICE_CALL_FAILED.getMessage(), ex.getCause());
		}
	}

	/**
	 * This method will call demographic service for status.
	 * 
	 * @param preId
	 * @return status code
	 */
	public String callGetStatusRestService(String preId) {
		log.info("sessionId", "idType", "id", "In callGetStatusRestService method of Booking Service Util");
		String statusCode = "";
		try {

			// RestTemplate restTemplate = restTemplateBuilder.build();
			Map<String, Object> params = new HashMap<>();
			params.put("preRegistrationId", preId);
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(preRegResourceUrl + "/applications/status/");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<PreRegistartionStatusDTO>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			uriBuilder += "{preRegistrationId}";
			log.info("sessionId", "idType", "id", "Call Get Status from demographic URL : " + uriBuilder);
			ResponseEntity<MainResponseDTO<PreRegistartionStatusDTO>> respEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.GET, httpEntity,
					new ParameterizedTypeReference<MainResponseDTO<PreRegistartionStatusDTO>>() {
					}, params);

			if (respEntity.getBody().getErrors() == null) {
				ObjectMapper mapper = new ObjectMapper();
				PreRegistartionStatusDTO preRegResponsestatusDto = mapper
						.convertValue(respEntity.getBody().getResponse(), PreRegistartionStatusDTO.class);

				statusCode = preRegResponsestatusDto.getStatusCode().trim();
			} else {
				throw new DemographicGetStatusException(respEntity.getBody().getErrors().get(0).getErrorCode(),
						respEntity.getBody().getErrors().get(0).getMessage());
			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callGetStatusRestService method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());

			throw new DemographicGetStatusException(ErrorCodes.PRG_BOOK_RCI_012.getCode(),
					ErrorMessages.DEMOGRAPHIC_SERVICE_CALL_FAILED.getMessage());
		}
		return statusCode;
	}

	/**
	 * This method will call demographic service for cancel status.
	 * 
	 * @param preId
	 * @return status code
	 */
	public boolean callGetStatusForCancelRestService(String preId) {
		log.info("sessionId", "idType", "id", "In callGetStatusForCancelRestService method of Booking Service Util");
		try {
			// RestTemplate restTemplate = restTemplateBuilder.build();
			Map<String, Object> params = new HashMap<>();
			params.put("preRegistrationId", preId);
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(preRegResourceUrl + "/applications/status/");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<PreRegistartionStatusDTO>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			uriBuilder += "{preRegistrationId}";
			log.info("sessionId", "idType", "id",
					"In callGetStatusForCancelRestService method of Booking Service URL- " + uriBuilder);

			ResponseEntity<MainResponseDTO<PreRegistartionStatusDTO>> respEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.GET, httpEntity,
					new ParameterizedTypeReference<MainResponseDTO<PreRegistartionStatusDTO>>() {
					}, params);

			if (respEntity.getBody().getErrors() == null) {
				ObjectMapper mapper = new ObjectMapper();
				PreRegistartionStatusDTO preRegResponsestatusDto = mapper
						.convertValue(respEntity.getBody().getResponse(), PreRegistartionStatusDTO.class);

				String statusCode = preRegResponsestatusDto.getStatusCode().trim();

				if (!statusCode.equals(StatusCodes.BOOKED.getCode())) {
					if (statusCode.equals(StatusCodes.PENDING_APPOINTMENT.getCode())) {
						throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_013.getCode(),
								ErrorMessages.BOOKING_DATA_NOT_FOUND.getMessage());
					}

					else {
						throw new AppointmentCannotBeCanceledException(ErrorCodes.PRG_BOOK_RCI_018.getCode(),
								ErrorMessages.APPOINTMENT_CANNOT_BE_CANCELED.getMessage());
					}

				}
			} else {
				for (ExceptionJSONInfoDTO dto : respEntity.getBody().getErrors()) {
					throw new DemographicGetStatusException(dto.getErrorCode(), dto.getMessage());
				}

			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callGetStatusForCancelRestService method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());
			throw new DemographicGetStatusException(ErrorCodes.PRG_BOOK_RCI_012.getCode(),
					ErrorMessages.DEMOGRAPHIC_SERVICE_CALL_FAILED.getMessage());
		}
		return true;
	}

	public boolean timeSpanCheckForCancle(LocalDateTime bookedDateTime) {
		
		ZonedDateTime currentTime = ZonedDateTime.now();
		LocalDateTime requestTimeCountrySpecific=currentTime.toInstant().atZone(ZoneId.of(specificZoneId)).toLocalDateTime();
		log.info("sessionId", "idType", "id",
				"In timeSpanCheckForCancle method of Booking Service for request Date Time- " + requestTimeCountrySpecific);
		long hours = ChronoUnit.HOURS.between(requestTimeCountrySpecific, bookedDateTime);
		if (hours >= timeSpanCheckForCancel)
			return true;
		else
			throw new TimeSpanException(ErrorCodes.PRG_BOOK_RCI_026.getCode(),
					ErrorMessages.BOOKING_STATUS_CANNOT_BE_ALTERED.getMessage());
	}

	public boolean timeSpanCheckForRebook(LocalDateTime bookedDateTime,Date requestTime) {
		
		LocalDateTime requestTimeCountrySpecific=requestTime.toInstant().atZone(ZoneId.of(specificZoneId)).toLocalDateTime();
		
		log.info("sessionId", "idType", "id",
				"In timeSpanCheckForRebook method of Booking Service for request Date Time- " + requestTimeCountrySpecific);
		long hours = ChronoUnit.HOURS.between(requestTimeCountrySpecific, bookedDateTime);
		if (hours >= timeSpanCheckForRebook)
			return true;
		else
			throw new TimeSpanException(ErrorCodes.PRG_BOOK_RCI_026.getCode(),
					ErrorMessages.BOOKING_STATUS_CANNOT_BE_ALTERED.getMessage());

	}
	

	/**
	 * This method will do booking time slots.
	 * 
	 * @param regDto
	 * @param holidaylist
	 * @param sDate
	 * @param bookingDAO
	 */
	public void timeSlotCalculator(RegistrationCenterDto regDto, List<String> holidaylist, LocalDate sDate,
			BookingDAO bookingDAO) {
		log.info("sessionId", "idType", "id", "In timeSlotCalculator method of Booking Service Util");
		if (holidaylist.contains(sDate.toString())) {
			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String text = "2016-11-09 00:00:00";
			LocalDateTime localDateTime = LocalDateTime.parse(text, format);
			LocalTime localTime = localDateTime.toLocalTime();
			saveAvailability(regDto, sDate, localTime, localTime, bookingDAO);

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
					saveAvailability(regDto, sDate, currentTime1, toTime, bookingDAO);

				} else {
					LocalTime toTime = currentTime1.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
					saveAvailability(regDto, sDate, currentTime1, toTime, bookingDAO);
				}
				currentTime1 = currentTime1.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
			}

			LocalTime currentTime2 = regDto.getLunchEndTime();
			for (int i = 0; i < window2; i++) {
				if (i == (window2 - 1)) {
					LocalTime toTime = currentTime2.plusMinutes(regDto.getPerKioskProcessTime().getMinute())
							.plusMinutes(extraTime2);
					saveAvailability(regDto, sDate, currentTime2, toTime, bookingDAO);

				} else {
					LocalTime toTime = currentTime2.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
					saveAvailability(regDto, sDate, currentTime2, toTime, bookingDAO);
				}
				currentTime2 = currentTime2.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
			}
		}
	}

	/**
	 * This method will check mandatory parameter check.
	 * 
	 * @param bookingDto
	 * @return true or false
	 * @throws java.text.ParseException
	 */
	public boolean mandatoryParameterCheck(String preRegistrationId, BookingRequestDTO bookingRequestDTO) {
		log.info("sessionId", "idType", "id", "In mandatoryParameterCheck method of Booking Service Util");
		boolean flag = true;
		if (isNull(preRegistrationId)) {
			throw new BookingPreIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_006.getCode(),
					ErrorMessages.PREREGISTRATION_ID_NOT_ENTERED.getMessage());
		} else if (bookingRequestDTO != null) {
			if (isNull(bookingRequestDTO.getRegistrationCenterId())) {
				throw new BookingRegistrationCenterIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_007.getCode(),
						ErrorMessages.REGISTRATION_CENTER_ID_NOT_ENTERED.getMessage());
			} else if (isNull(bookingRequestDTO.getRegDate())) {
				throw new BookingDateNotSeletectedException(ErrorCodes.PRG_BOOK_RCI_008.getCode(),
						ErrorMessages.BOOKING_DATE_TIME_NOT_SELECTED.getMessage());
			} else if (isNull(bookingRequestDTO.getSlotFromTime()) || isNull(bookingRequestDTO.getSlotToTime())) {
				throw new BookingTimeSlotNotSeletectedException(ErrorCodes.PRG_BOOK_RCI_003.getCode(),
						ErrorMessages.USER_HAS_NOT_SELECTED_TIME_SLOT.getMessage());
			}
		} else {
			flag = false;
		}
		return flag;

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

	private void saveAvailability(RegistrationCenterDto regDto, LocalDate date, LocalTime currentTime, LocalTime toTime,
			BookingDAO bookingDAO) {
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
		bookingDAO.saveAvailability(avaEntity);
	}

	/**
	 * This method will do booking slots.
	 * 
	 * @param dateList
	 * @param dateTimeList
	 * @param i
	 * @param dateTime
	 * @param entity
	 */
	public void slotSetter(List<LocalDate> dateList, List<DateTimeDto> dateTimeList, int i, DateTimeDto dateTime,
			List<AvailibityEntity> entity) {
		log.info("sessionId", "idType", "id", "In slotSetter method of Booking Service Util");
		List<SlotDto> slotList = new ArrayList<>();
		for (AvailibityEntity en : entity) {
			if (en.getAvailableKiosks() > 0) {
				SlotDto slots = new SlotDto();
				slots.setAvailability(en.getAvailableKiosks());
				slots.setFromTime(en.getFromTime());
				slots.setToTime(en.getToTime());
				slotList.add(slots);
			}
		}
		if (entity.size() == 1) {
			dateTime.setHoliday(true);
		} else {
			dateTime.setHoliday(false);
		}
		if (!slotList.isEmpty()) {
			dateTime.setTimeSlots(slotList);
			dateTime.setDate(dateList.get(i).toString());
			dateTimeList.add(dateTime);
		}

	}

	/**
	 * This method will do mandatory parameter check for cancel.
	 * 
	 * @param cancelBookingDTO
	 * @return true or false
	 */
	public boolean mandatoryParameterCheckforCancel(String preRegistrationId) {
		log.info("sessionId", "idType", "id", "In mandatoryParameterCheckforCancel method of Booking Service Util");
		boolean flag = true;

		if (isNull(preRegistrationId)) {
			throw new BookingPreIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_006.getCode(),
					ErrorMessages.PREREGISTRATION_ID_NOT_ENTERED.getMessage());
		}

		return flag;

	}

	/**
	 * This method will check for duplicates.
	 * 
	 * @param oldBookingRegistrationDTO
	 * @param newBookingRegistrationDTO
	 * @return boolean
	 */
	public boolean isNotDuplicate(BookingRegistrationDTO oldBookingRegistrationDTO,
			BookingRegistrationDTO newBookingRegistrationDTO) {
		log.info("sessionId", "idType", "id", "In isNotDuplicate method of Booking Service Util");
		if (oldBookingRegistrationDTO.getRegDate().equals(newBookingRegistrationDTO.getRegDate())
				&& oldBookingRegistrationDTO.getRegistrationCenterId()
						.equals(newBookingRegistrationDTO.getRegistrationCenterId())
				&& oldBookingRegistrationDTO.getSlotFromTime().equals(newBookingRegistrationDTO.getSlotFromTime())
				&& oldBookingRegistrationDTO.getSlotToTime().equals(newBookingRegistrationDTO.getSlotToTime())) {
			throw new AppointmentReBookingFailedException(ErrorCodes.PRG_BOOK_RCI_021.getCode(),
					ErrorMessages.APPOINTMENT_REBOOKING_FAILED.getMessage());
		}
		return true;
	}

	/**
	 * This method is used to add the initial request values into a map for request
	 * map.
	 * 
	 * @param MainRequestDTO
	 *            pass requestDTO
	 * @return a map for request request map
	 */
	public Map<String, String> prepareRequestMap(MainRequestDTO<?> requestDto) {
		log.info("sessionId", "idType", "id", "In prepareRequestMap method of Booking Service Util");
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("id", requestDto.getId());
		requestMap.put("ver", requestDto.getVersion());
		requestMap.put("reqTime", new SimpleDateFormat(utcDateTimePattern).format(requestDto.getRequesttime()));
		requestMap.put("request", requestDto.getRequest().toString());
		return requestMap;
	}

	/**
	 * Helper method for setting CancelBookingDTO.
	 * 
	 * @param preRegistrationId
	 * @param oldBookingRegistrationDTO
	 * @return
	 */
	public CancelBookingDTO cancelBookingDtoSetter(String preRegistrationId,
			BookingRegistrationDTO oldBookingRegistrationDTO) {
		log.info("sessionId", "idType", "id", "In cancelBookingDtoSetter method of Booking Service Util");
		CancelBookingDTO cancelBookingDTO = new CancelBookingDTO();
		cancelBookingDTO.setRegistrationCenterId(oldBookingRegistrationDTO.getRegistrationCenterId());
		cancelBookingDTO.setRegDate(oldBookingRegistrationDTO.getRegDate());
		cancelBookingDTO.setSlotFromTime(oldBookingRegistrationDTO.getSlotFromTime());
		cancelBookingDTO.setSlotToTime(oldBookingRegistrationDTO.getSlotToTime());
		return cancelBookingDTO;
	}

	public String getCurrentResponseTime() {
		log.info("sessionId", "idType", "id", "In getCurrentResponseTime method of Booking Service Util");
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), utcDateTimePattern);
	}

	/**
	 * This method will check for availability.
	 * 
	 * @param availableEntity
	 * @return boolean
	 */
	public boolean isKiosksAvailable(AvailibityEntity availableEntity) {
		log.info("sessionId", "idType", "id", "In isKiosksAvailable method of Booking Service Util");

		if (availableEntity.getAvailableKiosks() > 0) {
			return true;
		} else {
			throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_002.getCode(),
					ErrorMessages.AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME.getMessage());
		}
	}

	/**
	 * Helper method for setting RegistrationBookingEntity.
	 * 
	 * @param preRegistrationId
	 * @param oldBookingRegistrationDTO
	 * @return
	 */
	public RegistrationBookingEntity bookingEntitySetter(String preRegistrationId,
			BookingRequestDTO bookingRequestDTO) {
		log.info("sessionId", "idType", "id", "In bookingEntitySetter method of Booking Service Util");
		RegistrationBookingEntity entity = new RegistrationBookingEntity();
		entity.setBookingPK(
				new RegistrationBookingPK(preRegistrationId, DateUtils.parseDateToLocalDateTime(new Date())));
		entity.setRegistrationCenterId(bookingRequestDTO.getRegistrationCenterId());
		entity.setId(UUIDGeneratorUtil.generateId());
		entity.setLangCode("12L");
		entity.setCrBy(authUserDetails().getUserId());
		entity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		entity.setRegDate(LocalDate.parse(bookingRequestDTO.getRegDate()));
		entity.setSlotFromTime(LocalTime.parse(bookingRequestDTO.getSlotFromTime()));
		entity.setSlotToTime(LocalTime.parse(bookingRequestDTO.getSlotToTime()));
		return entity;
	}

	/**
	 * 
	 * @param notificationDTO
	 * @param langCode
	 * @return NotificationResponseDTO
	 * @throws JsonProcessingException 
	 */
	public void emailNotification(NotificationDTO notificationDTO, String langCode) throws JsonProcessingException {
		String emailResourseUrl = notificationResourseurl + "/notify";
		ResponseEntity<String> resp = null;
		MainResponseDTO<NotificationResponseDTO> response = new MainResponseDTO<>();
		HttpHeaders headers = new HttpHeaders();
		MainRequestDTO<NotificationDTO> request = new MainRequestDTO<>();
		ObjectMapper mapper= new ObjectMapper();
		mapper.setTimeZone(TimeZone.getDefault());
		try {
			request.setRequest(notificationDTO);
			request.setId("mosip.pre-registration.notification.notify");
			request.setVersion("1.0");
			request.setRequesttime(new Date());
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<Object, Object> emailMap = new LinkedMultiValueMap<>();
			emailMap.add("NotificationRequestDTO", mapper.writeValueAsString(request));
			emailMap.add("langCode", langCode);
			HttpEntity<MultiValueMap<Object, Object>> httpEntity = new HttpEntity<>(emailMap, headers);
			log.info("sessionId", "idType", "id",
					"In emailNotification method of NotificationUtil service emailResourseUrl: " + emailResourseUrl);
			resp = restTemplate.exchange(emailResourseUrl, HttpMethod.POST, httpEntity, String.class);
			List<ServiceError> validationErrorList = ExceptionUtils.getServiceErrorList(resp.getBody());
			if (validationErrorList!=null && !validationErrorList.isEmpty()) {
				throw new NotificationException(validationErrorList, null);
			}
		} catch (HttpClientErrorException ex) {
			log.error("sessionId", "idType", "id",
					"In emailNotification method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());
			throw new RestCallException(ErrorCodes.PRG_BOOK_RCI_033.getCode(),
					ErrorMessages.NOTIFICATION_CALL_FAILED.getMessage());

		}
	}

	/**
	 * This static method is used to check whether the appointment date is valid or
	 * not
	 * 
	 * @param regDate
	 * @return true if the appointment date time is not older date or false if the
	 *         appointment date is older date
	 */
	public boolean validateAppointmentDate(Map<String, String> requestMap) {
		try {
			if (requestMap.get(RequestCodes.REG_DATE.getCode()) != null
					&& !requestMap.get(RequestCodes.REG_DATE.getCode()).isEmpty()) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				sdf.setLenient(false);
				sdf.parse(requestMap.get(RequestCodes.REG_DATE.getCode()));
				LocalDate localDate = LocalDate.parse(requestMap.get(RequestCodes.REG_DATE.getCode()));
				if (localDate.isBefore(LocalDate.now())) {
					throw new InvalidRequestParameterException(
							ErrorCodes.PRG_BOOK_RCI_031.getCode(), ErrorMessages.INVALID_BOOKING_DATE_TIME.getMessage()
									+ " found for - " + requestMap.get(RequestCodes.PRE_REGISTRAION_ID.getCode()),
							null);
				} else if (localDate.isEqual(LocalDate.now())
						&& (requestMap.get(RequestCodes.FROM_SLOT_TIME.getCode()) != null
								&& !requestMap.get(RequestCodes.FROM_SLOT_TIME.getCode()).isEmpty())) {
					LocalTime localTime = LocalTime.parse(requestMap.get(RequestCodes.FROM_SLOT_TIME.getCode()));
					if (localTime.isBefore(LocalTime.now())) {
						throw new InvalidRequestParameterException(ErrorCodes.PRG_BOOK_RCI_031.getCode(),
								ErrorMessages.INVALID_BOOKING_DATE_TIME.getMessage() + " found for - "
										+ requestMap.get(RequestCodes.PRE_REGISTRAION_ID.getCode()),
								null);
					}
				}
			}
		} catch (Exception ex) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_BOOK_RCI_031.getCode(),
					ErrorMessages.INVALID_BOOKING_DATE_TIME.getMessage() + " found for preregistration id - "
							+ requestMap.get(RequestCodes.PRE_REGISTRAION_ID.getCode()),
					null);
		}
		return true;
	}

}
