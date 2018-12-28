package io.mosip.preregistration.booking.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.booking.code.StatusCodes;
import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.BookingDTO;
import io.mosip.preregistration.booking.dto.BookingRegistrationDTO;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.BookingResponseDto;
import io.mosip.preregistration.booking.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.booking.dto.DateTimeDto;
import io.mosip.preregistration.booking.dto.DocumentGetAllDTO;
import io.mosip.preregistration.booking.dto.HolidayDto;
import io.mosip.preregistration.booking.dto.PreRegResponseDto;
import io.mosip.preregistration.booking.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.booking.dto.RegistrationCenterDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterHolidayDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterResponseDto;
import io.mosip.preregistration.booking.dto.RequestDto;
import io.mosip.preregistration.booking.dto.ResponseDTO;
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
import io.mosip.preregistration.booking.exception.InvalidDateTimeFormatException;
import io.mosip.preregistration.booking.exception.MasterDataNotAvailableException;
import io.mosip.preregistration.booking.exception.RecordNotFoundException;
import io.mosip.preregistration.booking.exception.RestCallException;
import io.mosip.preregistration.booking.exception.util.BookingExceptionCatcher;
import io.mosip.preregistration.booking.repository.BookingAvailabilityRepository;
import io.mosip.preregistration.booking.repository.RegistrationBookingRepository;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;
import io.mosip.preregistration.core.util.UUIDGeneratorUtil;
import io.mosip.preregistration.core.util.ValidationUtil;

/**
 * @author M1046129
 *
 */
@Component
public class BookingService {

	private RestTemplate restTemplate;

	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	@Autowired
	BookingAvailabilityRepository bookingAvailabilityRepository;

	@Autowired
	@Qualifier("registrationBookingRepository")
	RegistrationBookingRepository registrationBookingRepository;

	@Value("${regCenter.url}")
	String regCenterUrl;

	@Value("${holiday.url}")
	String holidayListUrl;

	@Value("${noOfDays}")
	int noOfDays;

	@Value("${version}")
	String versionUrl;

	@Value("${documentUrl}")
	String documentUrl;

	@Value("${id}")
	String idUrl;

	@Value("${preRegResourceUrl}")
	private String preRegResourceUrl;

	Timestamp resTime = new Timestamp(System.currentTimeMillis());
	Map<String, String> requiredRequestMap = new HashMap<>();

	@PostConstruct
	public void setupBookingService() {
		requiredRequestMap.put("id", idUrl);
		requiredRequestMap.put("ver", versionUrl);

	}

	/**
	 * It will sync the registration center details
	 * 
	 * @return ResponseDto<String>
	 */

	public BookingResponseDto<String> addAvailability() {
		BookingResponseDto<String> response = new BookingResponseDto<>();

		try {
			restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder regbuilder = UriComponentsBuilder.fromHttpUrl(regCenterUrl);
			LocalDate endDate = LocalDate.now().plusDays(noOfDays);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<RegistrationCenterResponseDto> entity = new HttpEntity<>(headers);
			String uriBuilder = regbuilder.build().encode().toUriString();
			ResponseEntity<RegistrationCenterResponseDto> responseEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.GET, entity, RegistrationCenterResponseDto.class);

			List<RegistrationCenterDto> regCenter = responseEntity.getBody().getRegistrationCenters();

			if (regCenter.isEmpty()) {
				throw new MasterDataNotAvailableException(ErrorCodes.PRG_BOOK_RCI_020.toString(),
						ErrorMessages.MASTER_DATA_NOT_FOUND.toString());
			} else {
				for (RegistrationCenterDto regDto : regCenter) {
					String holidayUrl = holidayListUrl + regDto.getLanguageCode() + "/" + regDto.getId() + "/"
							+ LocalDate.now().getYear();
					UriComponentsBuilder builder2 = UriComponentsBuilder.fromHttpUrl(holidayUrl);

					HttpEntity<RegistrationCenterHolidayDto> entity2 = new HttpEntity<>(headers);

					String uriBuilder2 = builder2.build().encode().toUriString();
					ResponseEntity<RegistrationCenterHolidayDto> responseEntity2 = restTemplate.exchange(uriBuilder2,
							HttpMethod.GET, entity2, RegistrationCenterHolidayDto.class);
					List<String> holidaylist = new ArrayList<>();
					if (!responseEntity2.getBody().getHolidays().isEmpty()) {
						for (HolidayDto holiday : responseEntity2.getBody().getHolidays()) {
							holidaylist.add(holiday.getHolidayDate());
						}
					}

					for (LocalDate sDate = LocalDate.now(); (sDate.isBefore(endDate)
							|| sDate.isEqual(endDate)); sDate = sDate.plusDays(1)) {
						if (holidaylist.contains(sDate.toString())) {
							DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
							String text = "2016-11-09 00:00:00";
							LocalDateTime localDateTime = LocalDateTime.parse(text, format);
							LocalTime localTime = localDateTime.toLocalTime();
							saveAvailability(regDto, sDate, localTime, localTime);

						} else {

							int loop1 = ((regDto.getLunchStartTime().getHour() * 60
									+ regDto.getLunchStartTime().getMinute())
									- (regDto.getCenterStartTime().getHour() * 60
											+ regDto.getCenterStartTime().getMinute()))
									/ (regDto.getPerKioskProcessTime().getHour() * 60
											+ regDto.getPerKioskProcessTime().getMinute());

							int loop2 = ((regDto.getCenterEndTime().getHour() * 60
									+ regDto.getCenterEndTime().getMinute())
									- (regDto.getLunchEndTime().getHour() * 60 + regDto.getLunchEndTime().getMinute()))
									/ (regDto.getPerKioskProcessTime().getHour() * 60
											+ regDto.getPerKioskProcessTime().getMinute());

							int extraTime1 = ((regDto.getLunchStartTime().getHour() * 60
									+ regDto.getLunchStartTime().getMinute())
									- (regDto.getCenterStartTime().getHour() * 60
											+ regDto.getCenterStartTime().getMinute()))
									% (regDto.getPerKioskProcessTime().getHour() * 60
											+ regDto.getPerKioskProcessTime().getMinute());

							int extraTime2 = ((regDto.getCenterEndTime().getHour() * 60
									+ regDto.getCenterEndTime().getMinute())
									- (regDto.getLunchEndTime().getHour() * 60 + regDto.getLunchEndTime().getMinute()))
									% (regDto.getPerKioskProcessTime().getHour() * 60
											+ regDto.getPerKioskProcessTime().getMinute());

							LocalTime currentTime1 = regDto.getCenterStartTime();
							for (int i = 0; i < loop1; i++) {
								if (i == (loop1 - 1)) {
									LocalTime toTime = currentTime1
											.plusMinutes(regDto.getPerKioskProcessTime().getMinute())
											.plusMinutes(extraTime1);
									saveAvailability(regDto, sDate, currentTime1, toTime);

								} else {
									LocalTime toTime = currentTime1
											.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
									saveAvailability(regDto, sDate, currentTime1, toTime);
								}
								currentTime1 = currentTime1.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
							}

							LocalTime currentTime2 = regDto.getLunchEndTime();
							for (int i = 0; i < loop2; i++) {
								if (i == (loop2 - 1)) {
									LocalTime toTime = currentTime2
											.plusMinutes(regDto.getPerKioskProcessTime().getMinute())
											.plusMinutes(extraTime2);
									saveAvailability(regDto, sDate, currentTime2, toTime);

								} else {
									LocalTime toTime = currentTime2
											.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
									saveAvailability(regDto, sDate, currentTime2, toTime);
								}
								currentTime2 = currentTime2.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
							}
						}
					}
				}
			}
		} catch (HttpClientErrorException e) {
			throw new RestCallException(ErrorCodes.PRG_BOOK_002.toString(), "HTTP_CLIENT_EXCEPTION");

		} catch (DataAccessException e) {
			throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_016.toString(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.toString());
		}
		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus(true);
		response.setResponse("MASTER_DATA_SYNCED_SUCCESSFULLY");
		return response;

	}

	/**
	 * Gives the availability details
	 * 
	 * @param regID
	 * @return ResponseDto<AvailabilityDto>
	 */
	public BookingResponseDto<AvailabilityDto> getAvailability(String regID) {
		BookingResponseDto<AvailabilityDto> response = new BookingResponseDto<>();
		LocalDate endDate = LocalDate.now().plusDays(noOfDays + 2);
		LocalDate fromDate = LocalDate.now().plusDays(2);
		try {
			List<java.sql.Date> dateList = bookingAvailabilityRepository.findDate(regID, fromDate, endDate);
			if (!dateList.isEmpty()) {
				AvailabilityDto availability = new AvailabilityDto();
				List<DateTimeDto> dateTimeList = new ArrayList<>();
				for (int i = 0; i < dateList.size(); i++) {
					DateTimeDto dateTime = new DateTimeDto();
					List<AvailibityEntity> entity = bookingAvailabilityRepository
							.findByRegcntrIdAndRegDateOrderByFromTimeAsc(regID, dateList.get(i).toLocalDate());
					if (!entity.isEmpty()) {
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

				}
				availability.setCenterDetails(dateTimeList);
				availability.setRegCenterId(regID);

				response.setResTime(new Timestamp(System.currentTimeMillis()));
				response.setStatus(true);
				response.setResponse(availability);

			} else {
				throw new RecordNotFoundException(ErrorCodes.PRG_BOOK_RCI_015.toString(),
						ErrorMessages.NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER.toString());

			}
		} catch (DataAccessLayerException e) {
			throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_016.toString(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.toString());
		} catch (Exception ex) {

			new BookingExceptionCatcher().handle(ex);
		}
		return response;
	}

	private void saveAvailability(RegistrationCenterDto regDto, LocalDate date, LocalTime currentTime,
			LocalTime toTime) {
		AvailibityEntity avaEntity = new AvailibityEntity();
		avaEntity.setRegDate(date);
		avaEntity.setRegcntrId(regDto.getId());
		avaEntity.setFromTime(currentTime);
		avaEntity.setToTime(toTime);
		avaEntity.setCrBy("Admin");
		avaEntity.setCrDate(new Timestamp(System.currentTimeMillis()));
		if (!isMandatory(regDto.getContactPerson())) {
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

	/**
	 * @param bookingDTO
	 * @return response with status code
	 * @throws java.text.ParseException
	 */
	@SuppressWarnings("rawtypes")
	@Transactional(rollbackFor = { DataAccessException.class, AppointmentBookingFailedException.class,
			BookingTimeSlotAlreadyBooked.class, AvailablityNotFoundException.class,
			AppointmentCannotBeBookedException.class })
	public BookingResponseDto<List<BookingStatusDTO>> bookAppointment(BookingDTO bookingDTO) {
		Map<String, String> requestMap = new HashMap<>();
		BookingResponseDto<List<BookingStatusDTO>> responseDTO = new BookingResponseDto<>();
		RegistrationBookingPK bookingPK = new RegistrationBookingPK();
		Boolean requestValidatorFlag = false;
		List<BookingStatusDTO> respList = new ArrayList<>();
		try {
			requestMap.put("id", bookingDTO.getId());
			requestMap.put("ver", bookingDTO.getVer());
			requestMap.put("reqTime",
					new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(bookingDTO.getReqTime()));
			requestMap.put("request", bookingDTO.getRequest().toString());
			requestValidatorFlag = ValidationUtil.requestValidator(requestMap, requiredRequestMap);
			if (requestValidatorFlag) {
				for (BookingRequestDTO bookingRequestDTO : bookingDTO.getRequest()) {
					if (mandatoryParameterCheck(bookingRequestDTO)) {
						restTemplate = restTemplateBuilder.build();
						UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(documentUrl).queryParam("preId",
								bookingRequestDTO.getPreRegistrationId());
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
						HttpEntity<ResponseDTO<DocumentGetAllDTO>> httpEntity = new HttpEntity<>(headers);
						String uriBuilder = builder.build().encode().toUriString();
						ResponseEntity<ResponseDTO> docresp = restTemplate.exchange(uriBuilder, HttpMethod.GET,
								httpEntity, ResponseDTO.class);
						if (!docresp.getBody().getResponse().isEmpty()) {
							if (bookingRequestDTO.getOldBookingDetails() == null) {
								/* booking of new Appointment */
								synchronized (bookingRequestDTO) {
									BookingStatusDTO statusDTO = bookingAPI(bookingDTO, bookingRequestDTO, bookingPK);
									respList.add(statusDTO);
								}
							} else {
								/* Re-Booking */
								BookingRegistrationDTO oldBookingRegistrationDTO = bookingRequestDTO
										.getOldBookingDetails();
								BookingRegistrationDTO newBookingRegistrationDTO = bookingRequestDTO
										.getNewBookingDetails();
								if (oldBookingRegistrationDTO.getRegDate()
										.equals(newBookingRegistrationDTO.getRegDate())
										&& oldBookingRegistrationDTO.getRegistrationCenterId()
												.equals(newBookingRegistrationDTO.getRegistrationCenterId())
										&& oldBookingRegistrationDTO.getSlotFromTime()
												.equals(newBookingRegistrationDTO.getSlotFromTime())
										&& oldBookingRegistrationDTO.getSlotToTime()
												.equals(newBookingRegistrationDTO.getSlotToTime())) {
									throw new AppointmentReBookingFailedException(
											ErrorCodes.PRG_BOOK_RCI_021.toString(),
											ErrorMessages.APPOINTMENT_REBOOKING_FAILED.toString());
								} else {
									CancelBookingDTO cancelBookingDTO = new CancelBookingDTO();
									cancelBookingDTO.setPreRegistrationId(bookingRequestDTO.getPreRegistrationId());
									cancelBookingDTO.setRegistrationCenterId(
											oldBookingRegistrationDTO.getRegistrationCenterId());
									cancelBookingDTO.setRegDate(oldBookingRegistrationDTO.getRegDate());
									cancelBookingDTO.setSlotFromTime(oldBookingRegistrationDTO.getSlotFromTime());
									cancelBookingDTO.setSlotToTime(oldBookingRegistrationDTO.getSlotToTime());
									synchronized (cancelBookingDTO) {
										CancelBookingResponseDTO cancelBookingResponseDTO = cancelBookingAPI(
												cancelBookingDTO);
										if (cancelBookingResponseDTO != null && cancelBookingResponseDTO.getMessage()
												.equals("APPOINTMENT_SUCCESSFULLY_CANCELED")) {
											BookingStatusDTO statusDTO = bookingAPI(bookingDTO, bookingRequestDTO,
													bookingPK);
											respList.add(statusDTO);
										} else {
											throw new CancelAppointmentFailedException(
													ErrorCodes.PRG_BOOK_RCI_019.toString(),
													ErrorMessages.APPOINTMENT_CANCEL_FAILED.toString());
										}
									}

								}
							}

						} else {
							BookingStatusDTO noDocumentDTO = new BookingStatusDTO();
							noDocumentDTO.setPreRegistrationId(bookingRequestDTO.getPreRegistrationId());
							noDocumentDTO.setBookingStatus("Failed");
							noDocumentDTO.setBookingMessage("BOOKING_FAILED_DUE_TO_NO_DOCUMENT");
							respList.add(noDocumentDTO);
						}
					}

				}
				responseDTO.setStatus(true);
				responseDTO.setResTime(resTime);
				responseDTO.setErr(null);
				responseDTO.setResponse(respList);
			}
		} catch (DataAccessLayerException e) {
			throw new DemographicStatusUpdationException("Table not accessable");
		} catch (Exception e) {
			new BookingExceptionCatcher().handle(e);
		}
		return responseDTO;
	}

	/**
	 * @param field
	 * @return true or false
	 */
	public boolean isMandatory(String field) {
		if (field == null || field.equals("") || field.trim().length() == 0) {
			return false;
		}
		return true;

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
		if (!isMandatory(requestDTO.getPreRegistrationId())) {

			throw new BookingPreIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_006.toString(),
					ErrorMessages.PREREGISTRATION_ID_NOT_ENTERED.toString());
		} else if (oldBookingDetails != null) {
			if (!isMandatory(oldBookingDetails.getRegistrationCenterId())) {
				throw new BookingRegistrationCenterIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_007.toString(),
						ErrorMessages.REGISTRATION_CENTER_ID_NOT_ENTERED.toString());
			} else if (!isMandatory(oldBookingDetails.getSlotFromTime())
					&& !isMandatory(oldBookingDetails.getSlotToTime())) {
				throw new BookingTimeSlotNotSeletectedException(ErrorCodes.PRG_BOOK_RCI_003.toString(),
						ErrorMessages.USER_HAS_NOT_SELECTED_TIME_SLOT.toString());
			}
		} else if (newBookingDetails != null) {
			if (!isMandatory(newBookingDetails.getRegistrationCenterId())) {
				throw new BookingRegistrationCenterIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_007.toString(),
						ErrorMessages.REGISTRATION_CENTER_ID_NOT_ENTERED.toString());
			} else if (!isMandatory(newBookingDetails.getSlotFromTime())
					|| !isMandatory(newBookingDetails.getSlotToTime())) {
				throw new BookingTimeSlotNotSeletectedException(ErrorCodes.PRG_BOOK_RCI_003.toString(),
						ErrorMessages.USER_HAS_NOT_SELECTED_TIME_SLOT.toString());
			}
		} else {
			flag = false;
		}
		return flag;

	}

	/**
	 * @param preId
	 * @param status
	 * @return response entity
	 */
	@SuppressWarnings("rawtypes")
	public ResponseEntity<BookingResponseDto> callUpdateStatusRestService(String preId, String status) {
		ResponseEntity<BookingResponseDto> resp = null;
		try {
			restTemplate = restTemplateBuilder.build();

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(preRegResourceUrl + "/applications")
					.queryParam("preRegId", preId).queryParam("status", status);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<BookingResponseDto<String>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			resp = restTemplate.exchange(uriBuilder, HttpMethod.PUT, httpEntity, BookingResponseDto.class);
		} catch (RestClientException e) {
			throw new DemographicStatusUpdationException(ErrorCodes.PRG_BOOK_RCI_011.toString(),
					ErrorMessages.DEMOGRAPHIC_STATUS_UPDATION_FAILED.toString(), e.getCause());
		}
		return resp;
	}

	/**
	 * @param preId
	 * @return status code
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String callGetStatusRestService(String preId) {

		restTemplate = restTemplateBuilder.build();
		String statusCode = "";
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(preRegResourceUrl + "/applicationStatus")
					.queryParam("preId", preId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<PreRegResponseDto> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			ResponseEntity<PreRegResponseDto<PreRegistartionStatusDTO>> respEntity = (ResponseEntity) restTemplate
					.exchange(uriBuilder, HttpMethod.GET, httpEntity, PreRegResponseDto.class);

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

	public BookingStatusDTO bookingAPI(BookingDTO bookingDTO, BookingRequestDTO bookingRequestDTO,
			RegistrationBookingPK bookingPK) {
		RegistrationBookingEntity entity = new RegistrationBookingEntity();
		BookingRegistrationDTO registrationDTO = bookingRequestDTO.getNewBookingDetails();
		BookingStatusDTO bookingStatusDTO = new BookingStatusDTO();

		String preRegStatusCode = callGetStatusRestService(bookingRequestDTO.getPreRegistrationId());
		if (preRegStatusCode != null
				&& preRegStatusCode.trim().equalsIgnoreCase(StatusCodes.PENDINGAPPOINTMENT.toString().trim())) {
			AvailibityEntity availableEntity = bookingAvailabilityRepository
					.findByFromTimeAndToTimeAndRegDateAndRegcntrId(LocalTime.parse(registrationDTO.getSlotFromTime()),
							LocalTime.parse(registrationDTO.getSlotToTime()),
							LocalDate.parse(registrationDTO.getRegDate()), registrationDTO.getRegistrationCenterId());

			if (availableEntity != null && availableEntity.getAvailableKiosks() > 0) {

				boolean slotExistsFlag = registrationBookingRepository.existsByPreIdandStatusCode(
						bookingRequestDTO.getPreRegistrationId(), StatusCodes.BOOKED.toString());

				if (!slotExistsFlag) {
					bookingPK.setPreregistrationId(bookingRequestDTO.getPreRegistrationId());

					bookingPK.setBookingDateTime(DateUtils.parseDateToLocalDateTime(bookingDTO.getReqTime()));

					entity.setBookingPK(bookingPK);
					entity.setRegistrationCenterId(registrationDTO.getRegistrationCenterId());
					entity.setStatus_code(StatusCodes.BOOKED.toString().trim());
					entity.setLang_code("12L");
					entity.setCrBy("987654321");
					entity.setCrDate(Timestamp.valueOf(DateUtils.parseDateToLocalDateTime(bookingDTO.getReqTime())));
					entity.setRegDate(LocalDate.parse(registrationDTO.getRegDate()));
					entity.setSlotFromTime(LocalTime.parse(registrationDTO.getSlotFromTime()));
					entity.setSlotToTime(LocalTime.parse(registrationDTO.getSlotToTime()));

					RegistrationBookingEntity registrationBookingEntity = registrationBookingRepository.save(entity);

					if (registrationBookingEntity != null) {
						/* Pre registration status code update */
						callUpdateStatusRestService(bookingRequestDTO.getPreRegistrationId(),
								StatusCodes.BOOKED.toString().trim());

						/* No. of Availability. update */
						availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() - 1);
						bookingAvailabilityRepository.update(availableEntity);

						bookingStatusDTO.setPreRegistrationId(bookingRequestDTO.getPreRegistrationId());
						bookingStatusDTO.setBookingStatus(StatusCodes.BOOKED.toString());
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

	public BookingResponseDto<BookingRegistrationDTO> getAppointmentDetails(String preRegID) {
		BookingRegistrationDTO bookingRegistrationDTO = new BookingRegistrationDTO();
		BookingResponseDto<BookingRegistrationDTO> responseDto = new BookingResponseDto<>();
		RegistrationBookingEntity entity = new RegistrationBookingEntity();
		try {
			entity = registrationBookingRepository.findByPreIdAndStatusCode(preRegID, StatusCodes.BOOKED.toString());
			if (entity != null) {
				bookingRegistrationDTO.setRegDate(entity.getRegDate().toString());
				bookingRegistrationDTO.setRegistrationCenterId(entity.getRegistrationCenterId());
				bookingRegistrationDTO.setSlotFromTime(entity.getSlotFromTime().toString());
				bookingRegistrationDTO.setSlotToTime(entity.getSlotToTime().toString());
				responseDto.setResponse(bookingRegistrationDTO);
				responseDto.setStatus(true);
				responseDto.setErr(null);
				responseDto.setResTime(resTime);
			} else {
				throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_013.toString(),
						ErrorMessages.BOOKING_DATA_NOT_FOUND.toString());
			}
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_BOOK_RCI_010.toString(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.toString(), e.getCause());
		}

		return responseDto;
	}

	@Transactional(rollbackFor = { DataAccessException.class, CancelAppointmentFailedException.class,
			AppointmentAlreadyCanceledException.class, AvailablityNotFoundException.class,
			AppointmentCannotBeCanceledException.class })
	public BookingResponseDto<CancelBookingResponseDTO> cancelAppointment(RequestDto<CancelBookingDTO> requestdto) {

		Boolean requestValidatorFlag = false;
		BookingResponseDto<CancelBookingResponseDTO> dto = new BookingResponseDto<>();
		Map<String, String> requestMap = new HashMap<>();
		try {
			requestMap.put("id", requestdto.getId());
			requestMap.put("ver", requestdto.getVer());
			requestMap.put("reqTime", requestdto.getReqTime());
			requestMap.put("request", requestdto.getRequest().toString());
			requestValidatorFlag = ValidationUtil.requestValidator(requestMap, requiredRequestMap);
			CancelBookingDTO cancelBookingDTO = requestdto.getRequest();
			if (requestValidatorFlag) {
				CancelBookingResponseDTO cancelBookingResponseDTO = cancelBookingAPI(cancelBookingDTO);
				if (cancelBookingResponseDTO != null) {
					dto.setResponse(cancelBookingResponseDTO);
					dto.setErr(null);
					dto.setStatus(true);
					dto.setResTime(new Timestamp(System.currentTimeMillis()));
				}
			}
		} catch (DataAccessLayerException e) {
			throw new CancelAppointmentFailedException(ErrorCodes.PRG_BOOK_RCI_019.toString(),
					ErrorMessages.APPOINTMENT_CANCEL_FAILED.toString());

		} catch (DateTimeException e) {
			throw new InvalidDateTimeFormatException(ErrorCodes.PRG_BOOK_RCI_009.toString(),
					ErrorMessages.INVALID_DATE_TIME_FORMAT.toString());
		} catch (InvalidRequestParameterException e) {
			throw new InvalidRequestParameterException(
					io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_001.toString(),
					io.mosip.preregistration.core.errorcodes.ErrorMessages.INVALID_REQUEST_ID.toString());
		}
		return dto;

	}

	/**
	 * @param cancelBookingDTO
	 * @return response with status code
	 */
	public CancelBookingResponseDTO cancelBookingAPI(CancelBookingDTO cancelBookingDTO) {
		CancelBookingResponseDTO cancelBookingResponseDTO = new CancelBookingResponseDTO();
		if (mandatoryParameterCheckforCancel(cancelBookingDTO)) {
			String getstatus = callGetStatusRestService(cancelBookingDTO.getPreRegistrationId());
			if (getstatus != null && getstatus.trim().equalsIgnoreCase(StatusCodes.BOOKED.toString().trim())) {
				boolean bookingDetailsExistsFlag = registrationBookingRepository.existsByPreIdandStatusCode(
						cancelBookingDTO.getPreRegistrationId(), StatusCodes.BOOKED.toString());
				if (bookingDetailsExistsFlag) {
					AvailibityEntity availableEntity = bookingAvailabilityRepository
							.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
									LocalTime.parse(cancelBookingDTO.getSlotFromTime()),
									LocalTime.parse(cancelBookingDTO.getSlotToTime()),
									LocalDate.parse(cancelBookingDTO.getRegDate()),
									cancelBookingDTO.getRegistrationCenterId());

					if (availableEntity != null) {
						/* update entity in bookingTable */

						RegistrationBookingEntity bookingEntity = registrationBookingRepository
								.findByPreIdAndStatusCode(cancelBookingDTO.getPreRegistrationId(),
										StatusCodes.BOOKED.toString());

						bookingEntity.setStatus_code(StatusCodes.CANCELED.toString().trim());
						bookingEntity.setUpdDate(new Timestamp(System.currentTimeMillis()));
						RegistrationBookingEntity registrationBookingEntity = registrationBookingRepository
								.save(bookingEntity);
						if (registrationBookingEntity != null) {
							/* Update the status to Canceled in demographic Table */
							callUpdateStatusRestService(cancelBookingDTO.getPreRegistrationId(),
									StatusCodes.PENDINGAPPOINTMENT.toString().trim());

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

			} else if (getstatus != null && getstatus.trim().equalsIgnoreCase(StatusCodes.CANCELED.toString().trim())) {

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

		if (!isMandatory(cancelBookingDTO.getPreRegistrationId())) {
			throw new BookingPreIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_006.toString(),
					ErrorMessages.PREREGISTRATION_ID_NOT_ENTERED.toString());
		} else if (!isMandatory(cancelBookingDTO.getRegistrationCenterId())) {
			throw new BookingRegistrationCenterIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_007.toString(),
					ErrorMessages.REGISTRATION_CENTER_ID_NOT_ENTERED.toString());
		} else if (!isMandatory(cancelBookingDTO.getRegDate())) {
			throw new BookingDateNotSeletectedException(ErrorCodes.PRG_BOOK_RCI_008.toString(),
					ErrorMessages.BOOKING_DATE_TIME_NOT_SELECTED.toString());
		} else if (!isMandatory(cancelBookingDTO.getSlotFromTime()) && !isMandatory(cancelBookingDTO.getSlotToTime())) {
			throw new BookingTimeSlotNotSeletectedException(ErrorCodes.PRG_BOOK_RCI_003.toString(),
					ErrorMessages.USER_HAS_NOT_SELECTED_TIME_SLOT.toString());
		}

		return flag;

	}

}
