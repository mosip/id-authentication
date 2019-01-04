package io.mosip.preregistration.booking.service.util;

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

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.booking.code.StatusCodes;
import io.mosip.preregistration.booking.dto.BookingRegistrationDTO;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.booking.dto.DateTimeDto;
import io.mosip.preregistration.booking.dto.DocumentGetAllDTO;
import io.mosip.preregistration.booking.dto.HolidayDto;
import io.mosip.preregistration.booking.dto.MainListRequestDTO;
import io.mosip.preregistration.booking.dto.MainListResponseDTO;
import io.mosip.preregistration.booking.dto.MainRequestDTO;
import io.mosip.preregistration.booking.dto.MainResponseDTO;
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
import io.mosip.preregistration.booking.exception.AppointmentAlreadyCanceledException;
import io.mosip.preregistration.booking.exception.AppointmentBookingFailedException;
import io.mosip.preregistration.booking.exception.AppointmentCannotBeBookedException;
import io.mosip.preregistration.booking.exception.AppointmentCannotBeCanceledException;
import io.mosip.preregistration.booking.exception.AppointmentReBookingFailedException;
import io.mosip.preregistration.booking.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDataNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDateNotSeletectedException;
import io.mosip.preregistration.booking.exception.BookingPreIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingRegistrationCenterIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingTimeSlotAlreadyBooked;
import io.mosip.preregistration.booking.exception.BookingTimeSlotNotSeletectedException;
import io.mosip.preregistration.booking.exception.CancelAppointmentFailedException;
import io.mosip.preregistration.booking.exception.DemographicGetStatusException;
import io.mosip.preregistration.booking.exception.DemographicStatusUpdationException;
import io.mosip.preregistration.booking.exception.DocumentNotFoundException;
import io.mosip.preregistration.booking.exception.MasterDataNotAvailableException;
import io.mosip.preregistration.booking.exception.RestCallException;
import io.mosip.preregistration.booking.repository.BookingAvailabilityRepository;
import io.mosip.preregistration.booking.repository.RegistrationBookingRepository;
import io.mosip.preregistration.core.util.UUIDGeneratorUtil;

@Component
public class BookingServiceUtil {
	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	@Value("${regCenter.url}")
	String regCenterUrl;

	@Value("${holiday.url}")
	String holidayListUrl;

	@Value("${preRegResourceUrl}")
	private String preRegResourceUrl;

	@Value("${documentUrl}")
	String documentUrl;

	public List<RegistrationCenterDto> callRegCenterDateRestService() {
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
		} catch (HttpClientErrorException e) {
			throw new RestCallException(ErrorCodes.PRG_BOOK_002.toString(), "HTTP_CLIENT_EXCEPTION");
		}
		return regCenter;
	}

	public List<String> callGetHolidayListRestService(RegistrationCenterDto regDto) {
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
			if (holidaylist.isEmpty()) {
				throw new MasterDataNotAvailableException(ErrorCodes.PRG_BOOK_RCI_020.toString(),
						ErrorMessages.HOLIDAY_MASTER_DATA_NOT_FOUND.toString());
			}
		} catch (HttpClientErrorException e) {
			throw new RestCallException(ErrorCodes.PRG_BOOK_002.toString(), "HTTP_CLIENT_EXCEPTION");
		}
		return holidaylist;
	}

	/**
	 * @param preId
	 * @param status
	 * @return response entity
	 */
	public boolean callUpdateStatusRestService(String preId, String status) {
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(preRegResourceUrl + "/applications")
					.queryParam("preRegId", preId).queryParam("status", status);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<String>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			@SuppressWarnings("rawtypes")
			ResponseEntity<MainResponseDTO> bookingResponse = restTemplate.exchange(uriBuilder, HttpMethod.PUT,
					httpEntity, MainResponseDTO.class);
			return bookingResponse.getBody().getStatus();
		} catch (RestClientException e) {
			throw new DemographicStatusUpdationException(ErrorCodes.PRG_BOOK_RCI_011.toString(),
					ErrorMessages.DEMOGRAPHIC_STATUS_UPDATION_FAILED.toString(), e.getCause());
		}
	}

	/**
	 * @param preId
	 * @return status code
	 */
	public String callGetStatusRestService(String preId) {
		String statusCode = "";
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(preRegResourceUrl + "/applicationStatus")
					.queryParam("preId", preId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainListResponseDTO<?>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();

			@SuppressWarnings({ "rawtypes" })
			ResponseEntity<MainListResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET,
					httpEntity, MainListResponseDTO.class);

			ObjectMapper mapper = new ObjectMapper();
			PreRegistartionStatusDTO preRegResponsestatusDto = mapper
					.convertValue(respEntity.getBody().getResponse().get(0), PreRegistartionStatusDTO.class);

			statusCode = preRegResponsestatusDto.getStatusCode();

		} catch (RestClientException e) {
			throw new DemographicGetStatusException(ErrorCodes.PRG_BOOK_RCI_012.toString(),
					ErrorMessages.DEMOGRAPHIC_GET_STATUS_FAILED.toString(), e.getCause());
		}
		return statusCode;
	}

	public boolean callGetDocumentsByPreIdRestService(BookingRequestDTO bookingRequestDTO) {
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(documentUrl).queryParam("preId",
					bookingRequestDTO.getPreRegistrationId());
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
		} catch (RestClientException e) {
			throw new DocumentNotFoundException(ErrorCodes.PRG_BOOK_RCI_023.toString(),
					ErrorMessages.DOCUMENTS_NOT_FOUND_EXCEPTION.toString(), e.getCause());
		}
		return false;
	}

	public void timeSlotCalculator(RegistrationCenterDto regDto, List<String> holidaylist, LocalDate sDate,
			BookingAvailabilityRepository bookingAvailabilityRepository) {
		if (holidaylist.contains(sDate.toString())) {
			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String text = "2016-11-09 00:00:00";
			LocalDateTime localDateTime = LocalDateTime.parse(text, format);
			LocalTime localTime = localDateTime.toLocalTime();
			saveAvailability(regDto, sDate, localTime, localTime, bookingAvailabilityRepository);

		} else {

			int loop1 = ((regDto.getLunchStartTime().getHour() * 60 + regDto.getLunchStartTime().getMinute())
					- (regDto.getCenterStartTime().getHour() * 60 + regDto.getCenterStartTime().getMinute()))
					/ (regDto.getPerKioskProcessTime().getHour() * 60 + regDto.getPerKioskProcessTime().getMinute());

			int loop2 = ((regDto.getCenterEndTime().getHour() * 60 + regDto.getCenterEndTime().getMinute())
					- (regDto.getLunchEndTime().getHour() * 60 + regDto.getLunchEndTime().getMinute()))
					/ (regDto.getPerKioskProcessTime().getHour() * 60 + regDto.getPerKioskProcessTime().getMinute());

			int extraTime1 = ((regDto.getLunchStartTime().getHour() * 60 + regDto.getLunchStartTime().getMinute())
					- (regDto.getCenterStartTime().getHour() * 60 + regDto.getCenterStartTime().getMinute()))
					% (regDto.getPerKioskProcessTime().getHour() * 60 + regDto.getPerKioskProcessTime().getMinute());

			int extraTime2 = ((regDto.getCenterEndTime().getHour() * 60 + regDto.getCenterEndTime().getMinute())
					- (regDto.getLunchEndTime().getHour() * 60 + regDto.getLunchEndTime().getMinute()))
					% (regDto.getPerKioskProcessTime().getHour() * 60 + regDto.getPerKioskProcessTime().getMinute());

			LocalTime currentTime1 = regDto.getCenterStartTime();
			for (int i = 0; i < loop1; i++) {
				if (i == (loop1 - 1)) {
					LocalTime toTime = currentTime1.plusMinutes(regDto.getPerKioskProcessTime().getMinute())
							.plusMinutes(extraTime1);
					saveAvailability(regDto, sDate, currentTime1, toTime, bookingAvailabilityRepository);

				} else {
					LocalTime toTime = currentTime1.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
					saveAvailability(regDto, sDate, currentTime1, toTime, bookingAvailabilityRepository);
				}
				currentTime1 = currentTime1.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
			}

			LocalTime currentTime2 = regDto.getLunchEndTime();
			for (int i = 0; i < loop2; i++) {
				if (i == (loop2 - 1)) {
					LocalTime toTime = currentTime2.plusMinutes(regDto.getPerKioskProcessTime().getMinute())
							.plusMinutes(extraTime2);
					saveAvailability(regDto, sDate, currentTime2, toTime, bookingAvailabilityRepository);

				} else {
					LocalTime toTime = currentTime2.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
					saveAvailability(regDto, sDate, currentTime2, toTime, bookingAvailabilityRepository);
				}
				currentTime2 = currentTime2.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
			}
		}
	}

	/**
	 * @param bookingDto
	 * @return true or false
	 * @throws java.text.ParseException
	 */
	public boolean mandatoryParameterCheck(BookingRequestDTO requestDTO) {
		boolean flag = true;
		BookingRegistrationDTO oldBookingDetails = requestDTO.getOldBookingDetails();
		BookingRegistrationDTO newBookingDetails = requestDTO.getNewBookingDetails();
		if (isNull(requestDTO.getPreRegistrationId())) {
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

	public boolean isNull(String key) {
		if (key instanceof String) {
			if (key.equals("") || key.trim().length() == 0)
				return true;
		} else {
			if (key == null)
				return true;
		}
		return false;
	}

	private void saveAvailability(RegistrationCenterDto regDto, LocalDate date, LocalTime currentTime, LocalTime toTime,
			BookingAvailabilityRepository bookingAvailabilityRepository) {
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
		bookingAvailabilityRepository.save(avaEntity);
	}

	public void slotSetter(List<java.sql.Date> dateList, List<DateTimeDto> dateTimeList, int i, DateTimeDto dateTime,
			List<AvailibityEntity> entity) {
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

	public BookingStatusDTO bookingAPI(Date reqDateTime, BookingRequestDTO bookingRequestDTO,
			RegistrationBookingRepository registrationBookingRepository,
			BookingAvailabilityRepository bookingAvailabilityRepository) {
		RegistrationBookingEntity entity = new RegistrationBookingEntity();
		BookingRegistrationDTO registrationDTO = bookingRequestDTO.getNewBookingDetails();
		BookingStatusDTO bookingStatusDTO = new BookingStatusDTO();

		String preRegStatusCode = callGetStatusRestService(bookingRequestDTO.getPreRegistrationId());
		if (preRegStatusCode != null
				&& preRegStatusCode.trim().equalsIgnoreCase(StatusCodes.PENDINGAPPOINTMENT.getCode())) {
			AvailibityEntity availableEntity = bookingAvailabilityRepository
					.findByFromTimeAndToTimeAndRegDateAndRegcntrId(LocalTime.parse(registrationDTO.getSlotFromTime()),
							LocalTime.parse(registrationDTO.getSlotToTime()),
							LocalDate.parse(registrationDTO.getRegDate()), registrationDTO.getRegistrationCenterId());

			if (availableEntity != null && availableEntity.getAvailableKiosks() > 0) {

				boolean slotExistsFlag = registrationBookingRepository.existsByPreIdandStatusCode(
						bookingRequestDTO.getPreRegistrationId(), StatusCodes.BOOKED.getCode());

				if (!slotExistsFlag) {
					entity.setBookingPK(new RegistrationBookingPK(bookingRequestDTO.getPreRegistrationId(), DateUtils.parseDateToLocalDateTime(reqDateTime)));
					entity.setRegistrationCenterId(registrationDTO.getRegistrationCenterId());
					entity.setStatusCode(StatusCodes.BOOKED.getCode());
					entity.setLangCode("12L");
					entity.setCrBy("987654321");
					entity.setCrDate(DateUtils.parseDateToLocalDateTime(reqDateTime));
					entity.setRegDate(LocalDate.parse(registrationDTO.getRegDate()));
					entity.setSlotFromTime(LocalTime.parse(registrationDTO.getSlotFromTime()));
					entity.setSlotToTime(LocalTime.parse(registrationDTO.getSlotToTime()));

					RegistrationBookingEntity registrationBookingEntity = registrationBookingRepository.save(entity);

					if (registrationBookingEntity != null) {
						
						/* No. of Availability. update */
						availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() - 1);
						bookingAvailabilityRepository.update(availableEntity);

						/* Pre registration status code update */
						callUpdateStatusRestService(bookingRequestDTO.getPreRegistrationId(),
								StatusCodes.BOOKED.getCode());

						bookingStatusDTO.setPreRegistrationId(bookingRequestDTO.getPreRegistrationId());
						bookingStatusDTO.setBookingStatus(StatusCodes.BOOKED.getCode());
						bookingStatusDTO.setBookingMessage("APPOINTMENT_SUCCESSFULLY_BOOKED");

					} else {
						throw new AppointmentBookingFailedException(ErrorCodes.PRG_BOOK_RCI_005.toString(),
								ErrorMessages.APPOINTMENT_BOOKING_FAILED.toString());
					}
				} else {
					throw new BookingTimeSlotAlreadyBooked(ErrorCodes.PRG_BOOK_RCI_004.toString(),
							ErrorMessages.APPOINTMENT_TIME_SLOT_IS_ALREADY_BOOKED.toString());

				}
			} else {
				throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_002.toString(),
						ErrorMessages.AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME.toString());
			}
		} else {
			throw new AppointmentCannotBeBookedException(ErrorCodes.PRG_BOOK_RCI_001.toString(),
					ErrorMessages.APPOINTMENT_CANNOT_BE_BOOKED.toString());
		}

		return bookingStatusDTO;

	}

	/**
	 * @param cancelBookingDTO
	 * @return response with status code
	 */
	public CancelBookingResponseDTO cancelBookingAPI(CancelBookingDTO cancelBookingDTO,
			RegistrationBookingRepository registrationBookingRepository,
			BookingAvailabilityRepository bookingAvailabilityRepository) {
		CancelBookingResponseDTO cancelBookingResponseDTO = new CancelBookingResponseDTO();
		if (mandatoryParameterCheckforCancel(cancelBookingDTO)) {
			String getstatus = callGetStatusRestService(cancelBookingDTO.getPreRegistrationId());
			if (getstatus != null && getstatus.trim().equalsIgnoreCase(StatusCodes.BOOKED.getCode())) {
				boolean bookingDetailsExistsFlag = registrationBookingRepository.existsByPreIdandStatusCode(
						cancelBookingDTO.getPreRegistrationId(), StatusCodes.BOOKED.getCode());
				if (bookingDetailsExistsFlag) {
					AvailibityEntity availableEntity = bookingAvailabilityRepository
							.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
									LocalTime.parse(cancelBookingDTO.getSlotFromTime()),
									LocalTime.parse(cancelBookingDTO.getSlotToTime()),
									LocalDate.parse(cancelBookingDTO.getRegDate()),
									cancelBookingDTO.getRegistrationCenterId());

					if (availableEntity != null) {
						/* update entity in bookingTable */

						RegistrationBookingEntity bookingEntity = registrationBookingRepository.findPreIdAndStatusCode(
								cancelBookingDTO.getPreRegistrationId(), StatusCodes.BOOKED.getCode());

						bookingEntity.setStatusCode(StatusCodes.CANCELED.getCode());
						bookingEntity.setUpdDate(DateUtils.parseDateToLocalDateTime(new Date()));
						RegistrationBookingEntity registrationBookingEntity = registrationBookingRepository
								.save(bookingEntity);
						if (registrationBookingEntity != null) {
							/* Update the status to Canceled in demographic Table */
							callUpdateStatusRestService(cancelBookingDTO.getPreRegistrationId(),
									StatusCodes.PENDINGAPPOINTMENT.getCode());

							/* No. of Availability. update */
							availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() + 1);
							bookingAvailabilityRepository.update(availableEntity);

							cancelBookingResponseDTO.setTransactionId(UUIDGeneratorUtil.generateId());
							cancelBookingResponseDTO.setMessage("APPOINTMENT_SUCCESSFULLY_CANCELED");

						} else {
							throw new CancelAppointmentFailedException(ErrorCodes.PRG_BOOK_RCI_019.toString(),
									ErrorMessages.APPOINTMENT_CANCEL_FAILED.toString());

						}
					} else {
						throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_002.toString(),
								ErrorMessages.AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME.toString());
					}
				} else {
					throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_013.toString(),
							ErrorMessages.BOOKING_DATA_NOT_FOUND.toString());
				}

			} else if (getstatus != null && getstatus.trim().equalsIgnoreCase(StatusCodes.CANCELED.getCode())) {

				throw new AppointmentAlreadyCanceledException(ErrorCodes.PRG_BOOK_RCI_017.toString(),
						ErrorMessages.APPOINTMENT_TIME_SLOT_IS_ALREADY_CANCELED.toString());

			} else {
				throw new AppointmentCannotBeCanceledException(ErrorCodes.PRG_BOOK_RCI_018.toString(),
						ErrorMessages.APPOINTMENT_CANNOT_BE_CANCELED.toString());
			}
		}
		return cancelBookingResponseDTO;
	}

	/**
	 * @param cancelBookingDTO
	 * @return true or false
	 */
	public boolean mandatoryParameterCheckforCancel(CancelBookingDTO cancelBookingDTO) {
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

	public boolean checkForDuplicate(BookingRegistrationDTO oldBookingRegistrationDTO,
			BookingRegistrationDTO newBookingRegistrationDTO) {
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

	public Map<String, String> prepareRequestMap(MainListRequestDTO<?> requestDto) {
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("id", requestDto.getId());
		requestMap.put("ver", requestDto.getVer());
		requestMap.put("reqTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(requestDto.getReqTime()));
		requestMap.put("request", requestDto.getRequest().toString());
		return requestMap;
	}

	public Map<String, String> prepareRequestMap(MainRequestDTO<?> requestDto) {
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("id", requestDto.getId());
		requestMap.put("ver", requestDto.getVer());
		requestMap.put("reqTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(requestDto.getReqTime()));
		requestMap.put("request", requestDto.getRequest().toString());
		return requestMap;
	}

	public CancelBookingDTO cancelBookingDtoSetter(String preRegistrationId,
			BookingRegistrationDTO oldBookingRegistrationDTO) {
		CancelBookingDTO cancelBookingDTO = new CancelBookingDTO();
		cancelBookingDTO.setPreRegistrationId(preRegistrationId);
		cancelBookingDTO.setRegistrationCenterId(oldBookingRegistrationDTO.getRegistrationCenterId());
		cancelBookingDTO.setRegDate(oldBookingRegistrationDTO.getRegDate());
		cancelBookingDTO.setSlotFromTime(oldBookingRegistrationDTO.getSlotFromTime());
		cancelBookingDTO.setSlotToTime(oldBookingRegistrationDTO.getSlotToTime());
		return cancelBookingDTO;
	}

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	}
}
