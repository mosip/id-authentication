package io.mosip.preregistration.booking.service;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.preregistration.booking.code.StatusCodes;
import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.BookingDTO;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.DateTimeDto;
import io.mosip.preregistration.booking.dto.ExceptionJSONInfo;
import io.mosip.preregistration.booking.dto.HolidayDto;
import io.mosip.preregistration.booking.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.booking.dto.RegistrationCenterDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterHolidayDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterResponseDto;
import io.mosip.preregistration.booking.dto.ResponseDto;
import io.mosip.preregistration.booking.dto.SlotDto;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingPK;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.AppointmentCannotBeBookedException;
import io.mosip.preregistration.booking.exception.BookingPreIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingRegistrationCenterIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingTimeSlotNotSeletectedException;
import io.mosip.preregistration.booking.exception.IncorrectIDException;
import io.mosip.preregistration.booking.exception.IncorrectVersionException;
import io.mosip.preregistration.booking.exception.InvalidDateTimeFormatException;
import io.mosip.preregistration.booking.exception.TablenotAccessibleException;
import io.mosip.preregistration.booking.exception.TimeSlotAlreadyBooked;
import io.mosip.preregistration.booking.repository.BookingRepository;
import io.mosip.preregistration.booking.repository.RegistrationBookingRepository;

@Component
public class BookingService {

	private RestTemplate restTemplate;

	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	@Autowired
	BookingRepository bookingRepository;

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

	@Value("${id}")
	String idUrl;

	@Value("${preRegResourceUrl}")
	private String preRegResourceUrl;

	Timestamp resTime = new Timestamp(System.currentTimeMillis());


	public ResponseDto<String> addAvailability() {
		ResponseDto<String> response = new ResponseDto<>();

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

			if (regCenter.size() == 0) {
				response.setResTime(new Timestamp(System.currentTimeMillis()));
				response.setStatus(false);
				response.setResponse("No data is preent in registration center master table");
				return response;
			} else {
				for (RegistrationCenterDto regDto : regCenter) {
					String holidayUrl = holidayListUrl + regDto.getLanguageCode() + "/" + 1 + "/"
							+ LocalDate.now().getYear();

					UriComponentsBuilder builder2 = UriComponentsBuilder.fromHttpUrl(holidayUrl);

					HttpEntity<RegistrationCenterHolidayDto> entity2 = new HttpEntity<>(headers);

					String uriBuilder2 = builder2.build().encode().toUriString();
					ResponseEntity<RegistrationCenterHolidayDto> responseEntity2 = restTemplate.exchange(uriBuilder2,
							HttpMethod.GET, entity2, RegistrationCenterHolidayDto.class);
					List<String> holidaylist = new ArrayList<String>();
					if (responseEntity2.getBody().getHolidays().size() > 0) {
						for (HolidayDto holiday : responseEntity2.getBody().getHolidays()) {
							holidaylist.add(holiday.getHolidayDate());
						}
						holidaylist.add("2018-11-30");
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
									/ regDto.getPerKioskProcessTime().getMinute();

							int loop2 = ((regDto.getCenterEndTime().getHour() * 60
									+ regDto.getCenterEndTime().getMinute())
									- (regDto.getLunchEndTime().getHour() * 60 + regDto.getLunchEndTime().getMinute()))
									/ regDto.getPerKioskProcessTime().getMinute();

							int extraTime1 = ((regDto.getLunchStartTime().getHour() * 60
									+ regDto.getLunchStartTime().getMinute())
									- (regDto.getCenterStartTime().getHour() * 60
											+ regDto.getCenterStartTime().getMinute()))
									% regDto.getPerKioskProcessTime().getMinute();

							int extraTime2 = ((regDto.getCenterEndTime().getHour() * 60
									+ regDto.getCenterEndTime().getMinute())
									- (regDto.getLunchEndTime().getHour() * 60 + regDto.getLunchEndTime().getMinute()))
									% regDto.getPerKioskProcessTime().getMinute();

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

		} catch (DataAccessException e) {
			throw new TablenotAccessibleException("Table not accessable ");
		} catch (NullPointerException e) {

		}

		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus(true);
		response.setResponse("Master Data is synched successfully");
		return response;

	}

	public ResponseDto<AvailabilityDto> getAvailability(String regID) {
		ResponseDto<AvailabilityDto> response = new ResponseDto<>();
		List<ExceptionJSONInfo> exceptionJSONInfos = new ArrayList<>();
		try {
			List<String> dateList = bookingRepository.findDate(regID);
			if (dateList.size() > 0) {
				AvailabilityDto availability = new AvailabilityDto();
				List<DateTimeDto> dateTimeList = new ArrayList<>();
				for (String day : dateList) {
					DateTimeDto dateTime = new DateTimeDto();
					List<AvailibityEntity> entity = bookingRepository.findByRegcntrIdAndRegDate(regID, day);
					if (entity.size() > 0) {
						List<SlotDto> slotList = new ArrayList<>();
						for (AvailibityEntity en : entity) {
							SlotDto slots = new SlotDto();
							slots.setAvailability(en.getAvailabilityNo());
							slots.setFromTime(en.getFromTime());
							slots.setToTime(en.getToTime());
							slotList.add(slots);
						}
						if (entity.get(0).getIsActive()) {
							dateTime.setHoliday(false);
						} else {
							dateTime.setHoliday(true);
						}
						dateTime.setTimeSlots(slotList);
						dateTime.setDate(day);
						dateTimeList.add(dateTime);
					} else {
						ExceptionJSONInfo exception = new ExceptionJSONInfo("", "No slots available for that date");
						exceptionJSONInfos.add(exception);
						response.setErr(exceptionJSONInfos);
						response.setResTime(new Timestamp(System.currentTimeMillis()));
						response.setStatus(false);
						return response;
					}

				}
				availability.setCenterDetails(dateTimeList);
				availability.setRegCenterId(regID);

				response.setResTime(new Timestamp(System.currentTimeMillis()));
				response.setStatus(true);
				response.setResponse(availability);

			} else {
				ExceptionJSONInfo exception = new ExceptionJSONInfo("", "No time slots are assigned to that registration center");
				exceptionJSONInfos.add(exception);
				response.setErr(exceptionJSONInfos);
				response.setResTime(new Timestamp(System.currentTimeMillis()));
				response.setStatus(false);

			}
		} catch (DataAccessException e) {
			throw new TablenotAccessibleException("Table not accessable ");
		} catch (NullPointerException e) {

		}
		return response;
	}

	private void saveAvailability(RegistrationCenterDto regDto, LocalDate date, LocalTime currentTime, LocalTime toTime)
			throws TablenotAccessibleException {

		AvailibityEntity avaEntity = new AvailibityEntity();
		avaEntity.setRegDate(date.toString());
		avaEntity.setRegcntrId(regDto.getId());
		avaEntity.setFromTime(currentTime);
		avaEntity.setToTime(toTime);

		avaEntity.setCrBy(regDto.getContactPerson());
		if (currentTime.equals(toTime)) {
			avaEntity.setIsActive(false);
			avaEntity.setAvailabilityNo(0);
		} else {
			avaEntity.setAvailabilityNo(regDto.getNumberOfKiosks());
			avaEntity.setIsActive(true);
		}
		bookingRepository.save(avaEntity);
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseDto<BookingDTO> bookAppointment(BookingDTO bookingDTO) throws java.text.ParseException {
		ResponseDto responseDTO = new ResponseDto<>();
		try {

			System.out.println("inside bookappointment service");
			RegistrationBookingEntity entity = new RegistrationBookingEntity();
			RegistrationBookingPK bookingPK = new RegistrationBookingPK();
			BookingRequestDTO requestDTO = new BookingRequestDTO();
			if (inputCheck(bookingDTO)) {

				requestDTO = bookingDTO.getRequest();

				ResponseEntity<PreRegistartionStatusDTO> respEntity = callGetStatusRestService(
						requestDTO.getPre_registration_id());
				PreRegistartionStatusDTO responseGetDto = respEntity.getBody();

				String preRegStatusCode = responseGetDto.getStatusCode();
				System.out.println("status from pre reg: " + preRegStatusCode);

				AvailibityEntity availableEntity = bookingRepository.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
						LocalTime.parse(requestDTO.getSlotFromTime()), LocalTime.parse(requestDTO.getSlotToTime()),
						new Date(requestDTO.getReg_date().getTime()).toString(),
						requestDTO.getRegistration_center_id());

				if (!registrationBookingRepository.existsBypreIdandbookingDateTime(requestDTO.getPre_registration_id(),
						requestDTO.getReg_date())) {
					System.out.println(preRegStatusCode);
					System.out.println(availableEntity.getAvailabilityNo());

					if (availableEntity != null && availableEntity.getAvailabilityNo() > 0 && preRegStatusCode != null
							&& preRegStatusCode.trim().equalsIgnoreCase("Pending_Appointment")) {

						bookingPK.setPreregistrationId(requestDTO.getPre_registration_id());
						bookingPK.setBookingDateTime(bookingDTO.getReqTime());

						entity.setBookingPK(bookingPK);
						entity.setRegistrationCenterId(requestDTO.getRegistration_center_id());
						entity.setStatus_code("Booked");
						entity.setLang_code("12L");
						entity.setCrBy("987654321");
						entity.setCrDate(resTime);
						entity.setRegDate(new Date(requestDTO.getReg_date().getTime()));
						entity.setSlotFromTime(convertStringToTime(requestDTO.getSlotFromTime()));
						entity.setSlotToTime(convertStringToTime(requestDTO.getSlotToTime()));

						RegistrationBookingEntity resultEntity = registrationBookingRepository.save(entity);

						ResponseEntity<ResponseDto> responseEntity = callUpdateStatusRestService(
								requestDTO.getPre_registration_id(), "Booked");
						if (responseEntity.getStatusCode() == HttpStatus.OK) {
							System.out.println("PreId updated");
							// updateRepository.getOne(resultEntity.getBookingPK().getPreregistrationId());
						}
						if (resultEntity != null) {
							List<String> respList = new ArrayList<>();

							// preRegistrationEntity.setStatusCode("Booked");
							// updateRepository.update(preRegistrationEntity);

							availableEntity.setAvailabilityNo(availableEntity.getAvailabilityNo() - 1);
							bookingRepository.update(availableEntity);

							respList.add(StatusCodes.APPOINTMENT_SUCCESSFULLY_BOOKED.toString());
							responseDTO.setResponse(respList);
						}
					} else {

						throw new AppointmentCannotBeBookedException(
								ErrorMessages.APPOINTMENT_CANNOT_BE_BOOKED.toString());

					}
				} else {
					throw new TimeSlotAlreadyBooked(ErrorMessages.APPOINTMENT_TIME_SLOT_IS_ALREADY_BOOKED.toString());

				}
			}
		} catch (DataAccessLayerException e) {

			throw new TablenotAccessibleException(ErrorMessages.REGISTRATION_TABLE_NOT_ACCESSIBLE.toString());

		}
		return responseDTO;
	}

	public boolean mandatoryFieldsCheck(String field) {
		if (field == null || field.equals(null) || field.toString().trim().length() == 0) {
			return false;
		}
		return true;

	}

	/**
	 * @param bookingDto
	 * @return true or false
	 * @throws java.text.ParseException
	 */
	public boolean inputCheck(BookingDTO bookingDto) throws java.text.ParseException {
		boolean flag = true;
		BookingRequestDTO requestDTO = new BookingRequestDTO();
		if (bookingDto.getId() == null || !bookingDto.getId().trim().equalsIgnoreCase(idUrl.trim())) {
			flag = false;
			throw new IncorrectIDException(ErrorMessages.INVALID_ID.toString());
		} else if (bookingDto.getVer() == null || !bookingDto.getVer().trim().equalsIgnoreCase(versionUrl.trim())) {
			flag = false;
			throw new IncorrectVersionException(ErrorMessages.INVALID_VERSION.toString());
		} else if (!validateDate(bookingDto.getReqTime())) {
			flag = false;
			throw new InvalidDateTimeFormatException(ErrorMessages.INVALID_DATE_TIME_FORMAT.toString());
		} else if (bookingDto.getRequest() == null) {
			flag = false;
		} else {
			requestDTO = bookingDto.getRequest();
			if (!mandatoryFieldsCheck(requestDTO.getPre_registration_id())) {
				throw new BookingPreIdNotFoundException(ErrorMessages.PREREGISTRATION_ID_NOT_ENTERED.toString());
			} else if (!mandatoryFieldsCheck(requestDTO.getRegistration_center_id())) {
				throw new BookingRegistrationCenterIdNotFoundException(
						ErrorMessages.REGISTRATION_CENTER_ID_NOT_ENTERED.toString());
			} else if (!validateDate(requestDTO.getReg_date())) {
				throw new BookingTimeSlotNotSeletectedException(
						ErrorMessages.BOOKING_DATE_TIME_NOT_SELECTED.toString());
			} else if (!mandatoryFieldsCheck(requestDTO.getSlotFromTime())) {
				throw new BookingTimeSlotNotSeletectedException(
						ErrorMessages.USER_HAS_NOT_SELECTED_ANY_TIME_SLOT.toString());
			} else if (!mandatoryFieldsCheck(requestDTO.getSlotToTime())) {
				throw new BookingTimeSlotNotSeletectedException(
						ErrorMessages.USER_HAS_NOT_SELECTED_ANY_TIME_SLOT.toString());
			}
		}
		return flag;

	}

	// public Time convertStringToTime(String times) throws java.text.ParseException
	// {
	// SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	// java.util.Date d1 = (java.util.Date) format.parse(times);
	// Time ppstime = new Time(d1.getTime());
	// return ppstime;
	// }

	public Time convertStringToTime(String times) throws java.text.ParseException {
		// SimpleDateFormat simpleFormat = new SimpleDateFormat("HH:mm:ss[.SSSSSS]");
		SimpleDateFormat simpleParse = new SimpleDateFormat("HH:mm");
		java.util.Date d1 = (java.util.Date) simpleParse.parse((times));
		Time ppstime = new Time(d1.getTime());

		return ppstime;
	}

	public boolean validateDate(Timestamp input) {
		if (input != null) {
			boolean time = false;
			String inputTimeInString = "";
			final String ISO_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS";
			final SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
			final TimeZone utc = TimeZone.getTimeZone("UTC");
			sdf.setTimeZone(utc);
			java.util.Date date = input;
			inputTimeInString = sdf.format(date);

			try {
				// Tokenize string and separate date and time
				StringTokenizer st = new StringTokenizer(inputTimeInString, " ");
				if (st.countTokens() != 2) {
					return false;
				}
				String[] dateAndTime = new String[2];
				int i = 0;
				while (st.hasMoreTokens()) {
					dateAndTime[i] = st.nextToken();
					i++;
				}

				// Separated Date and Time
				String timeToken = dateAndTime[1];
				StringTokenizer timeTokens = new StringTokenizer(timeToken, ":");
				if (timeTokens.countTokens() != 3) {
					return false;
				}

				// Separated Time
				String[] timeAt = new String[4];
				int j = 0;
				while (timeTokens.hasMoreTokens()) {
					timeAt[j] = timeTokens.nextToken();
					j++;
				}

				int HH = Integer.valueOf(timeAt[0].toString());
				int mm = Integer.valueOf(timeAt[1].toString());
				float ss = Float.valueOf(timeAt[2].toString());

				if (HH < 60 && HH >= 0 && mm < 60 && mm >= 0 && ss < 60 && ss >= 0) {
					time = true;
				}

				// Got Date
				String dateToken = dateAndTime[0];

				// Tokenize separated date and separate year-month-day
				StringTokenizer dateTokens = new StringTokenizer(dateToken, "-");
				if (dateTokens.countTokens() != 3) {
					return false;
				}
				String[] tokenAt = new String[3];

				// token string array with year month and day value.
				int k = 0;
				while (dateTokens.hasMoreTokens()) {
					tokenAt[k] = dateTokens.nextToken();
					k++;
				}

				// create new date with got value of date
				int dayInt = Integer.parseInt(tokenAt[2]);
				int monthInt = Integer.parseInt(tokenAt[1]);
				int yearInt = Integer.parseInt(tokenAt[0]);

				Calendar cal = new GregorianCalendar();
				cal.setLenient(false);
				cal.set(yearInt, monthInt - 1, dayInt);
				cal.getTime();

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			// Here we'll check for correct format is provided else it'll return false
			try {
				Pattern p = Pattern.compile("^\\d{4}[-]?\\d{1,2}[-]?\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}[.]?\\d{1,6}$");
				if (p.matcher(inputTimeInString).matches()) {
				} else {
					return false;
				}

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			// Cross checking with simple date format to get correct time stamp only
			SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
			try {
				format.parse(inputTimeInString);
				if (time) {
					return true;
				} else {
					return false;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;

	}

	/**
	 * @param preId
	 * @param status
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ResponseEntity<ResponseDto> callUpdateStatusRestService(String preId, String status) {
		restTemplate = restTemplateBuilder.build();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(preRegResourceUrl).queryParam("preRegId", preId)
				.queryParam("status", status);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<ResponseDto<String>> httpEntity = new HttpEntity<>(headers);
		String uriBuilder = builder.build().encode().toUriString();

		return restTemplate.exchange(uriBuilder, HttpMethod.GET, httpEntity, ResponseDto.class);

	}

	public ResponseEntity<PreRegistartionStatusDTO> callGetStatusRestService(String preId) {
		restTemplate = restTemplateBuilder.build();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(preRegResourceUrl)
				.queryParam("preRegId", preId);
		HttpHeaders headers = new HttpHeaders();
		// headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<ResponseDto<String>> httpEntity = new HttpEntity<>(headers);
		String uriBuilder = builder.build().encode().toUriString();

		return restTemplate.exchange(uriBuilder, HttpMethod.GET, httpEntity, PreRegistartionStatusDTO.class);

		// return restTemplate.getForEntity(preRegResourceUrl + "/applications",
		// ResponseDto.class).getBody();

	}

}
