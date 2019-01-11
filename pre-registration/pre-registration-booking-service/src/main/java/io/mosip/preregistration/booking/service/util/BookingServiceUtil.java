/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.service.util;

/**
 * This class provides the utility methods for Booking application.
 * 
 * @author Kishan Rathore
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.preregistration.booking.code.StatusCodes;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.DateTimeDto;
import io.mosip.preregistration.booking.dto.DocumentGetAllDTO;
import io.mosip.preregistration.booking.dto.HolidayDto;
import io.mosip.preregistration.booking.dto.MainRequestDTO;
import io.mosip.preregistration.booking.dto.PreRegistartionStatusDTO;
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
import io.mosip.preregistration.booking.exception.BookingDateNotSeletectedException;
import io.mosip.preregistration.booking.exception.BookingPreIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingRegistrationCenterIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingTimeSlotNotSeletectedException;
import io.mosip.preregistration.booking.exception.DemographicGetStatusException;
import io.mosip.preregistration.booking.exception.DemographicStatusUpdationException;
import io.mosip.preregistration.booking.exception.DocumentNotFoundException;
import io.mosip.preregistration.booking.exception.MasterDataNotAvailableException;
import io.mosip.preregistration.booking.exception.RestCallException;
import io.mosip.preregistration.booking.repository.impl.BookingDAO;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.MainListRequestDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;

/**
 * This class provides the utility methods for Booking application.
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 *
 */
@Component
public class BookingServiceUtil {

	/**
	 * Autowired reference for {@link #restTemplateBuilder}
	 */
	@Autowired
	RestTemplateBuilder restTemplateBuilder;

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
	 * Reference for ${preRegResourceUrl} from property file
	 */
	@Value("${preRegResourceUrl}")
	private String preRegResourceUrl;

	/**
	 * Reference for ${documentUrl} from property file
	 */
	@Value("${documentUrl}")
	String documentUrl;

	private Logger log = LoggerConfiguration.logConfig(BookingServiceUtil.class);

	/**
	 * This method will call kernel service for registration center date.
	 * 
	 * @return List of RegistrationCenterDto
	 */
	public List<RegistrationCenterDto> callRegCenterDateRestService() {
		log.info("sessionId", "idType", "id", "In callRegCenterDateRestService method of Booking Service Util");
		List<RegistrationCenterDto> regCenter = null;
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder regbuilder = UriComponentsBuilder.fromHttpUrl(regCenterUrl);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<RegistrationCenterResponseDto> entity = new HttpEntity<>(headers);
			String uriBuilder = regbuilder.build().encode().toUriString();
			ResponseEntity<RegistrationCenterResponseDto> responseEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.GET, entity, RegistrationCenterResponseDto.class);
			regCenter = responseEntity.getBody().getRegistrationCenters();
			if (regCenter == null || regCenter.isEmpty()) {
				throw new MasterDataNotAvailableException(ErrorCodes.PRG_BOOK_RCI_020.toString(),
						ErrorMessages.MASTER_DATA_NOT_FOUND.toString());
			}
		} catch (HttpClientErrorException ex) {
			log.error("sessionId", "idType", "id",
					"In callRegCenterDateRestService method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());
			System.out.println(ex.getResponseBodyAsString());
			try {
				ErrorResponse<ServiceError> errorResponse = (ErrorResponse<ServiceError>) JsonUtils
						.jsonStringToJavaObject(ErrorResponse.class, ex.getResponseBodyAsString());
				throw new RestCallException(errorResponse.getErrors().get(0).getErrorCode(),
						errorResponse.getErrors().get(0).getErrorMessage());
			} catch (JsonParseException | JsonMappingException | IOException e1) {
				e1.printStackTrace();
				log.error("sessionId", "idType", "id",
						"In callRegCenterDateRestService method of Booking Service Util for JsonParseException- "
								+ ex.getMessage());

			}

		}
		return regCenter;
	}

	/**
	 * This method will call kernel service holiday list
	 * 
	 * @param regDto
	 * @return List of string
	 */
	public List<String> callGetHolidayListRestService(RegistrationCenterDto regDto) {
		log.info("sessionId", "idType", "id", "In callGetHolidayListRestService method of Booking Service Util");
		List<String> holidaylist = null;
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			String holidayUrl = holidayListUrl + regDto.getLanguageCode() + "/" + regDto.getId() + "/"
					+ LocalDate.now().getYear();
			UriComponentsBuilder builder2 = UriComponentsBuilder.fromHttpUrl(holidayUrl);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<RegistrationCenterHolidayDto> httpHolidayEntity = new HttpEntity<>(headers);
			String uriBuilder = builder2.build().encode().toUriString();
			ResponseEntity<RegistrationCenterHolidayDto> responseEntity2 = restTemplate.exchange(uriBuilder,
					HttpMethod.GET, httpHolidayEntity, RegistrationCenterHolidayDto.class);
			holidaylist = new ArrayList<>();
			if (!responseEntity2.getBody().getHolidays().isEmpty()) {
				for (HolidayDto holiday : responseEntity2.getBody().getHolidays()) {
					holidaylist.add(holiday.getHolidayDate());
				}
			}
		} catch (HttpClientErrorException ex) {
			log.error("sessionId", "idType", "id",
					"In callGetHolidayListRestService method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());

			try {
				ErrorResponse<ServiceError> errorResponse = (ErrorResponse<ServiceError>) JsonUtils
						.jsonStringToJavaObject(ErrorResponse.class, ex.getResponseBodyAsString());
				throw new RestCallException(errorResponse.getErrors().get(0).getErrorCode(),
						errorResponse.getErrors().get(0).getErrorMessage());
			} catch (JsonParseException | JsonMappingException | IOException e1) {
				log.error("sessionId", "idType", "id",
						"In callGetHolidayListRestService method of Booking Service Util for JsonParseException- "
								+ ex.getMessage());
				e1.printStackTrace();
			}

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
			RestTemplate restTemplate = restTemplateBuilder.build();

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(preRegResourceUrl + "/applications")
					.queryParam("pre_registration_id", preId).queryParam("status_code", status);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<String>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			@SuppressWarnings("rawtypes")
			ResponseEntity<MainResponseDTO> bookingResponse = restTemplate.exchange(uriBuilder, HttpMethod.PUT,
					httpEntity, MainResponseDTO.class);
			if (!bookingResponse.getBody().isStatus()) {
				throw new DemographicStatusUpdationException(bookingResponse.getBody().getErr().getErrorCode(),
						bookingResponse.getBody().getErr().getMessage());
			}

			return bookingResponse.getBody().isStatus();

		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callUpdateStatusRestService method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());

			throw new DemographicStatusUpdationException(ErrorCodes.PRG_BOOK_RCI_011.toString(),
					ErrorMessages.DEMOGRAPHIC_SERVICE_CALL_FAILED.toString(), ex.getCause());
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
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(preRegResourceUrl + "/applicationStatus")
					.queryParam("pre_registration_id", preId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainListResponseDTO<PreRegistartionStatusDTO>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			System.out.println("uriBuilder::" + uriBuilder);
			@SuppressWarnings({ "rawtypes" })
			ResponseEntity<MainListResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET,
					httpEntity, MainListResponseDTO.class);

			if (respEntity.getBody().isStatus()) {
				ObjectMapper mapper = new ObjectMapper();
				PreRegistartionStatusDTO preRegResponsestatusDto = mapper
						.convertValue(respEntity.getBody().getResponse().get(0), PreRegistartionStatusDTO.class);

				statusCode = preRegResponsestatusDto.getStatusCode().trim();
			} else {
				throw new DemographicGetStatusException(respEntity.getBody().getErr().getErrorCode(),
						respEntity.getBody().getErr().getMessage());
			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callGetStatusRestService method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());

			throw new DemographicGetStatusException(ErrorCodes.PRG_BOOK_RCI_012.toString(),
					ErrorMessages.DEMOGRAPHIC_SERVICE_CALL_FAILED.toString());
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
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(preRegResourceUrl + "/applicationStatus")
					.queryParam("pre_registration_id", preId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainListResponseDTO<?>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();

			@SuppressWarnings({ "rawtypes" })
			ResponseEntity<MainListResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET,
					httpEntity, MainListResponseDTO.class);

			if (respEntity.getBody().isStatus()) {
				ObjectMapper mapper = new ObjectMapper();
				PreRegistartionStatusDTO preRegResponsestatusDto = mapper
						.convertValue(respEntity.getBody().getResponse().get(0), PreRegistartionStatusDTO.class);

				String statusCode = preRegResponsestatusDto.getStatusCode().trim();

				if (!statusCode.equals(StatusCodes.BOOKED.getCode())) {
					throw new AppointmentCannotBeCanceledException(ErrorCodes.PRG_BOOK_RCI_018.toString(),
							ErrorMessages.APPOINTMENT_CANNOT_BE_CANCELED.toString());
				}
			} else {
				throw new DemographicGetStatusException(respEntity.getBody().getErr().getErrorCode(),
						respEntity.getBody().getErr().getMessage());
			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callGetStatusForCancelRestService method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());
			throw new DemographicGetStatusException(ErrorCodes.PRG_BOOK_RCI_012.toString(),
					ErrorMessages.DEMOGRAPHIC_SERVICE_CALL_FAILED.toString());
		}
		return true;
	}

	/**
	 * This method will call document service.
	 * 
	 * @param bookingRequestDTO
	 * @return boolean
	 */
	public boolean callGetDocumentsByPreIdRestService(BookingRequestDTO bookingRequestDTO) {
		log.info("sessionId", "idType", "id", "In callGetDocumentsByPreIdRestService method of Booking Service Util");
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(documentUrl)
					.queryParam("pre_registration_id", bookingRequestDTO.getPreRegistrationId());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainListResponseDTO<DocumentGetAllDTO>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			@SuppressWarnings("rawtypes")
			ResponseEntity<MainListResponseDTO> docresp = restTemplate.exchange(uriBuilder, HttpMethod.GET, httpEntity,
					MainListResponseDTO.class);
			if (!docresp.getBody().getResponse().isEmpty()) {
				return true;
			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callGetDocumentsByPreIdRestService method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());

			throw new DocumentNotFoundException(ErrorCodes.PRG_BOOK_RCI_023.toString(),
					ErrorMessages.DOCUMENTS_NOT_FOUND_EXCEPTION.toString(), ex.getCause());
		}
		return false;
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
	public boolean mandatoryParameterCheck(String preRegistrationId, BookingRegistrationDTO oldBookingDetails,
			BookingRegistrationDTO newBookingDetails) {
		log.info("sessionId", "idType", "id", "In mandatoryParameterCheck method of Booking Service Util");
		boolean flag = true;
		if (isNull(preRegistrationId)) {
			throw new BookingPreIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_006.toString(),
					ErrorMessages.PREREGISTRATION_ID_NOT_ENTERED.toString());
		} else if (oldBookingDetails != null) {
			if (isNull(oldBookingDetails.getRegistrationCenterId())) {
				throw new BookingRegistrationCenterIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_007.toString(),
						ErrorMessages.REGISTRATION_CENTER_ID_NOT_ENTERED.toString());
			} else if (isNull(oldBookingDetails.getSlotFromTime()) && isNull(oldBookingDetails.getSlotToTime())) {
				throw new BookingTimeSlotNotSeletectedException(ErrorCodes.PRG_BOOK_RCI_003.toString(),
						ErrorMessages.USER_HAS_NOT_SELECTED_TIME_SLOT.toString());
			}
		} else if (newBookingDetails != null) {
			if (isNull(newBookingDetails.getRegistrationCenterId())) {
				throw new BookingRegistrationCenterIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_007.toString(),
						ErrorMessages.REGISTRATION_CENTER_ID_NOT_ENTERED.toString());
			} else if (isNull(newBookingDetails.getSlotFromTime()) || isNull(newBookingDetails.getSlotToTime())) {
				throw new BookingTimeSlotNotSeletectedException(ErrorCodes.PRG_BOOK_RCI_003.toString(),
						ErrorMessages.USER_HAS_NOT_SELECTED_TIME_SLOT.toString());
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
		dateTime.setTimeSlots(slotList);
		dateTime.setDate(dateList.get(i).toString());
		dateTimeList.add(dateTime);
	}

	/**
	 * This method will do mandatory parameter check for cancel.
	 * 
	 * @param cancelBookingDTO
	 * @return true or false
	 */
	public boolean mandatoryParameterCheckforCancel(CancelBookingDTO cancelBookingDTO) {
		log.info("sessionId", "idType", "id", "In mandatoryParameterCheckforCancel method of Booking Service Util");
		boolean flag = true;

		if (isNull(cancelBookingDTO.getPreRegistrationId())) {
			throw new BookingPreIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_006.toString(),
					ErrorMessages.PREREGISTRATION_ID_NOT_ENTERED.toString());
		} else if (isNull(cancelBookingDTO.getRegistrationCenterId())) {
			throw new BookingRegistrationCenterIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_007.toString(),
					ErrorMessages.REGISTRATION_CENTER_ID_NOT_ENTERED.toString());
		} else if (isNull(cancelBookingDTO.getRegDate())) {
			throw new BookingDateNotSeletectedException(ErrorCodes.PRG_BOOK_RCI_008.toString(),
					ErrorMessages.BOOKING_DATE_TIME_NOT_SELECTED.toString());
		} else if (isNull(cancelBookingDTO.getSlotFromTime()) || isNull(cancelBookingDTO.getSlotToTime())) {
			throw new BookingTimeSlotNotSeletectedException(ErrorCodes.PRG_BOOK_RCI_003.toString(),
					ErrorMessages.USER_HAS_NOT_SELECTED_TIME_SLOT.toString());
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
			throw new AppointmentReBookingFailedException(ErrorCodes.PRG_BOOK_RCI_021.toString(),
					ErrorMessages.APPOINTMENT_REBOOKING_FAILED.toString());
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
	public Map<String, String> prepareRequestMap(MainListRequestDTO<?> requestDto) {
		log.info("sessionId", "idType", "id", "In prepareRequestMap method of Booking Service Util");
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("id", requestDto.getId());
		requestMap.put("ver", requestDto.getVer());
		requestMap.put("reqTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(requestDto.getReqTime()));
		requestMap.put("request", requestDto.getRequest().toString());
		return requestMap;
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
		requestMap.put("ver", requestDto.getVer());
		requestMap.put("reqTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(requestDto.getReqTime()));
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
		cancelBookingDTO.setPreRegistrationId(preRegistrationId);
		cancelBookingDTO.setRegistrationCenterId(oldBookingRegistrationDTO.getRegistrationCenterId());
		cancelBookingDTO.setRegDate(oldBookingRegistrationDTO.getRegDate());
		cancelBookingDTO.setSlotFromTime(oldBookingRegistrationDTO.getSlotFromTime());
		cancelBookingDTO.setSlotToTime(oldBookingRegistrationDTO.getSlotToTime());
		return cancelBookingDTO;
	}

	public String getCurrentResponseTime() {
		log.info("sessionId", "idType", "id", "In getCurrentResponseTime method of Booking Service Util");
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
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
			throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_002.toString(),
					ErrorMessages.AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME.toString());
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
			BookingRegistrationDTO bookingRegistrationDTO) {
		log.info("sessionId", "idType", "id", "In bookingEntitySetter method of Booking Service Util");
		RegistrationBookingEntity entity = new RegistrationBookingEntity();
		entity.setBookingPK(
				new RegistrationBookingPK(preRegistrationId, DateUtils.parseDateToLocalDateTime(new Date())));
		entity.setRegistrationCenterId(bookingRegistrationDTO.getRegistrationCenterId());
		entity.setStatusCode(StatusCodes.BOOKED.getCode());
		entity.setLangCode("12L");
		entity.setCrBy("987654321");
		entity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		entity.setRegDate(LocalDate.parse(bookingRegistrationDTO.getRegDate()));
		entity.setSlotFromTime(LocalTime.parse(bookingRegistrationDTO.getSlotFromTime()));
		entity.setSlotToTime(LocalTime.parse(bookingRegistrationDTO.getSlotToTime()));
		return entity;
	}
}
