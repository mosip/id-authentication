package io.mosip.preregistration.booking.test.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.exception.FileNotFoundException;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.CancelBookingResponseDTO;
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
import io.mosip.preregistration.booking.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDataNotFoundException;
import io.mosip.preregistration.booking.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.booking.exception.TimeSpanException;
import io.mosip.preregistration.booking.repository.BookingAvailabilityRepository;
import io.mosip.preregistration.booking.repository.RegistrationBookingRepository;
import io.mosip.preregistration.booking.repository.impl.BookingDAO;
import io.mosip.preregistration.booking.service.BookingService;
import io.mosip.preregistration.booking.service.util.BookingServiceUtil;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.DeleteBookingDTO;
import io.mosip.preregistration.core.common.dto.MainListRequestDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.core.exception.AppointmentReBookException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.core.util.ValidationUtil;

/**
 * Booking service Test
 * 
 * @author Kishan Rathore
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BookingServiceTest {

	@MockBean
	private BookingAvailabilityRepository bookingAvailabilityRepository;

	@MockBean
	private RegistrationBookingRepository registrationBookingRepository;

	@MockBean
	RestTemplateBuilder restTemplateBuilder;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BookingService service;

	@Autowired
	private BookingServiceUtil serviceUtil;

	@MockBean
	ObjectMapper mapper;

	@MockBean
	private BookingDAO bookingDAO;

	private AvailabilityDto availability = new AvailabilityDto();
	private List<DateTimeDto> dateList = new ArrayList<>();
	private DateTimeDto dateDto = new DateTimeDto();
	private List<SlotDto> slotsList = new ArrayList<>();

	private SlotDto slots = new SlotDto();

	// MainListRequestDTO bookingDTO = new MainListRequestDTO();
	AvailibityEntity availableEntity = new AvailibityEntity();
	RegistrationBookingEntity bookingEntity = new RegistrationBookingEntity();
	List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
	PreRegistartionStatusDTO preRegistartionStatusDTO = new PreRegistartionStatusDTO();
	MainListResponseDTO preRegResponse = new MainListResponseDTO();

	List<BookingRequestDTO> bookingList = new ArrayList<>();
	List<BookingRequestDTO> rebookingList = new ArrayList<>();
	BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();

	BookingRequestDTO rebookingRequestDTO = new BookingRequestDTO();

	BookingRegistrationDTO oldBooking = new BookingRegistrationDTO();
	BookingRegistrationDTO oldBooking_success = new BookingRegistrationDTO();
	BookingRegistrationDTO newBooking = new BookingRegistrationDTO();
	MainResponseDTO<List<BookingStatusDTO>> responseDto = new MainResponseDTO<>();
	BookingStatusDTO statusDTOA = new BookingStatusDTO();
	BookingStatusDTO statusDTOB = new BookingStatusDTO();
	Map<String, String> requiredRequestMap = new HashMap<>();
	InvalidRequestParameterException parameterException = null;
	boolean requestValidatorFlag = false;
	Map<String, String> requestMap = new HashMap<>();

	RegistrationCenterResponseDto regCenDto = new RegistrationCenterResponseDto();

	Map<String, String> requestMap1 = new HashMap<>();
	private MainRequestDTO<CancelBookingDTO> cancelRequestdto = new MainRequestDTO<>();
	private CancelBookingDTO cancelbookingDto = new CancelBookingDTO();

	DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	LocalTime localTime1;
	LocalTime localTime2;
	PreRegIdsByRegCenterIdDTO preRegIdsByRegCenterIdDTO = new PreRegIdsByRegCenterIdDTO();
	List<RegistrationBookingEntity> bookingEntities = new ArrayList<>();
	MainRequestDTO<PreRegIdsByRegCenterIdDTO> requestDTO = new MainRequestDTO<>();
	MainListRequestDTO<BookingRequestDTO> bookingDto = new MainListRequestDTO<>();
	MainListRequestDTO<BookingRequestDTO> reBookingDto = new MainListRequestDTO<>();

	@Value("${ver}")
	String versionUrl;

	@Value("${id}")
	String idUrl;

	@Value("${demographic.resource.url}")
	private String preRegResourceUrl;

	@Before
	public void setup() throws URISyntaxException, FileNotFoundException, ParseException, java.io.FileNotFoundException,
			IOException, org.json.simple.parser.ParseException {

		String date1 = "2016-11-09 09:00:00";
		String date2 = "2016-11-09 09:20:00";
		LocalDateTime localDateTime1 = LocalDateTime.parse(date1, format);
		LocalDateTime localDateTime2 = LocalDateTime.parse(date2, format);
		localTime1 = localDateTime1.toLocalTime();
		localTime2 = localDateTime2.toLocalTime();
		slots.setAvailability(4);
		slots.setFromTime(localTime1);
		slots.setToTime(localTime2);
		slotsList.add(slots);
		dateDto.setDate("2018-12-04");
		dateDto.setHoliday(true);
		dateDto.setTimeSlots(slotsList);
		dateList.add(dateDto);
		availability.setCenterDetails(dateList);
		availability.setRegCenterId("1");

		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();

		URI dataSyncUri = new URI(
				classLoader.getResource("booking.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File file = new File(dataSyncUri.getPath());
		parser.parse(new FileReader(file));

		// Rebooking
		rebookingRequestDTO.setPreRegistrationId("23587986034785");

		oldBooking.setRegistrationCenterId("1");
		oldBooking.setSlotFromTime("09:00");
		oldBooking.setSlotToTime("09:13");
		oldBooking.setRegDate("2019-12-06");
		rebookingRequestDTO.setOldBookingDetails(oldBooking);

		newBooking.setRegistrationCenterId("1");
		newBooking.setSlotFromTime("09:00");
		newBooking.setSlotToTime("09:13");
		newBooking.setRegDate("2019-12-12");

		rebookingRequestDTO.setNewBookingDetails(newBooking);
		bookingRequestDTO.setPreRegistrationId("23587986034785");
		bookingRequestDTO.setNewBookingDetails(newBooking);
		oldBooking_success.setRegistrationCenterId("1");
		oldBooking_success.setSlotFromTime("09:00");
		oldBooking_success.setSlotToTime("09:13");
		oldBooking_success.setRegDate("2019-12-05");
		rebookingRequestDTO.setOldBookingDetails(oldBooking_success);
		bookingRequestDTO.setOldBookingDetails(null);

		bookingList.add(bookingRequestDTO);
		rebookingList.add(rebookingRequestDTO);

		statusDTOA.setBookingStatus(StatusCodes.BOOKED.getCode());
		statusDTOA.setPreRegistrationId(bookingRequestDTO.getPreRegistrationId());
		statusDTOA.setBookingMessage("APPOINTMENT_SUCCESSFULLY_BOOKED");

		statusDTOB.setBookingStatus(StatusCodes.BOOKED.getCode());
		statusDTOB.setPreRegistrationId(bookingRequestDTO.getPreRegistrationId());
		statusDTOB.setBookingMessage("APPOINTMENT_SUCCESSFULLY_BOOKED");

		List<BookingStatusDTO> resp = new ArrayList<>();

		resp.add(statusDTOA);
		resp.add(statusDTOB);
		responseDto.setResponse(resp);
		responseDto.setErrors(null);
		responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		requiredRequestMap.put("id", idUrl);
		requiredRequestMap.put("ver", versionUrl);

		cancelRequestdto.setRequesttime(new Date());
		cancelRequestdto.setRequest(cancelbookingDto);
		cancelRequestdto.setId("mosip.pre-registration.booking.book");
		cancelRequestdto.setVersion("1.0");
		cancelbookingDto.setPreRegistrationId("23587986034785");
		cancelbookingDto.setRegDate("2019-12-04");
		cancelbookingDto.setRegistrationCenterId("1");
		cancelbookingDto.setSlotFromTime("09:00");
		cancelbookingDto.setSlotToTime("09:13");
		requestMap1.put("id", cancelRequestdto.getId());
		requestMap1.put("ver", cancelRequestdto.getVersion());
		requestMap1.put("reqTime",
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(cancelRequestdto.getRequesttime()));
		requestMap1.put("request", cancelRequestdto.getRequest().toString());

		availableEntity.setAvailableKiosks(4);
		availableEntity.setRegcntrId("1");
		availableEntity.setRegDate(LocalDate.parse("2019-12-04"));
		availableEntity.setToTime(localTime2);
		availableEntity.setFromTime(localTime1);
		availableEntity.setCrBy("987654321");
		availableEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntity.setDeleted(false);

		bookingEntity
				.setBookingPK(new RegistrationBookingPK("1234567890", DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity.setLangCode("12L");
		bookingEntity.setCrBy("987654321");
		bookingEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntity.setRegDate(LocalDate.parse(oldBooking.getRegDate()));
		bookingEntity.setSlotFromTime(LocalTime.parse(oldBooking.getSlotFromTime()));
		bookingEntity.setSlotToTime(LocalTime.parse(oldBooking.getSlotToTime()));

		preRegistartionStatusDTO.setStatusCode(StatusCodes.PENDING_APPOINTMENT.getCode());
		preRegistartionStatusDTO.setPreRegistartionId("23587986034785");
		statusList.add(preRegistartionStatusDTO);

		preRegResponse.setResponse(statusList);
		preRegResponse.setErr(null);
		List<String> preId = new ArrayList<>();
		preId.add("1234567890");
		preRegIdsByRegCenterIdDTO.setRegistrationCenterId("1");
		preRegIdsByRegCenterIdDTO.setPreRegistrationIds(preId);
		requestDTO.setRequest(preRegIdsByRegCenterIdDTO);

		requestDTO.setId("mosip.pre-registration.booking.book");
		requestDTO.setVersion("1.0");
		requestDTO.setRequesttime(new Date());
		bookingDto.setId("mosip.pre-registration.booking.book");
		bookingDto.setRequesttime(new Date());
		bookingDto.setVersion("1.0");
		bookingDto.setRequest(bookingList);

		// Rebook
		reBookingDto.setId("mosip.pre-registration.booking.book");
		reBookingDto.setRequesttime(new Date());
		reBookingDto.setVersion("1.0");
		reBookingDto.setRequest(rebookingList);
	}

//	@Test
	public void getAvailabilityTest() {

		logger.info("Availability dto " + availability);
		List<LocalDate> date = new ArrayList<>();
		List<AvailibityEntity> entityList = new ArrayList<>();
		date.add(LocalDate.now());
		entityList.add(availableEntity);
		logger.info("Availability entity " + availableEntity);
		Mockito.when(bookingDAO.findDate(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(date);
		Mockito.when(bookingDAO.findByRegcntrIdAndRegDateOrderByFromTimeAsc(Mockito.anyString(), Mockito.any()))
				.thenReturn(entityList);
		MainResponseDTO<AvailabilityDto> responseDto = service.getAvailability("1");
		logger.info("Response " + responseDto);
		assertEquals("1", responseDto.getResponse().getRegCenterId());

	}

//	@Test(expected = TableNotAccessibleException.class)
	public void getAvailabilityFailureTest() {

		Mockito.when(bookingDAO.findDate(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "", new Throwable()));
		service.getAvailability("1");

	}

//	@Test
	public void successBookAppointment() {

		requestValidatorFlag = ValidationUtil.requestValidator(bookingDto);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		ResponseEntity<MainListResponseDTO<PreRegistartionStatusDTO>> respEntity = new ResponseEntity<>(preRegResponse, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainListResponseDTO<PreRegistartionStatusDTO>>() {}))).thenReturn(respEntity);

		MainResponseDTO<List<BookingStatusDTO>> response = service.bookAppointment(bookingDto);
		assertEquals(1, response.getResponse().size());
	}

//	@Test
	public void successExpiredAppointment() {

		MainListRequestDTO<BookingRequestDTO> reBookingMainDto = new MainListRequestDTO<>();
		BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
		BookingRegistrationDTO oldBookingRegistrationDTO = new BookingRegistrationDTO();
		BookingRegistrationDTO newBookingRegistrationDTO = new BookingRegistrationDTO();
		oldBookingRegistrationDTO.setRegistrationCenterId("1");
		oldBookingRegistrationDTO.setSlotFromTime("09:00");
		oldBookingRegistrationDTO.setSlotToTime("09:15");
		oldBookingRegistrationDTO.setRegDate("2019-12-06");

		newBookingRegistrationDTO.setRegistrationCenterId("10005");
		newBookingRegistrationDTO.setSlotFromTime("09:00");
		newBookingRegistrationDTO.setSlotToTime("09:15");
		newBookingRegistrationDTO.setRegDate("2019-12-12");

		bookingRequestDTO.setPreRegistrationId("12345678909876");
		bookingRequestDTO.setNewBookingDetails(newBookingRegistrationDTO);
		bookingRequestDTO.setOldBookingDetails(oldBookingRegistrationDTO);
		List<BookingRequestDTO> rebookingReqList = new ArrayList<>();
		rebookingReqList.add(bookingRequestDTO);

		reBookingMainDto.setId("mosip.pre-registration.booking.book");
		reBookingMainDto.setVersion("1.0");
		reBookingMainDto.setRequesttime(new Date());
		reBookingMainDto.setRequest(rebookingReqList);

		MainResponseDTO<List<BookingStatusDTO>> responseDTO = new MainResponseDTO<>();
		BookingStatusDTO bookingStatusDTO = new BookingStatusDTO();
		bookingStatusDTO.setPreRegistrationId("12345678909876");
		bookingStatusDTO.setBookingStatus(StatusCodes.EXPIRED.getCode());
		bookingStatusDTO.setBookingMessage("APPOINTMENT_SUCCESSFULLY_BOOKED");

		List<BookingStatusDTO> respList = new ArrayList<>();
		respList.add(bookingStatusDTO);
		responseDTO.setResponse(respList);
		responseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
		List<PreRegistartionStatusDTO> statusListrebook = new ArrayList<>();
		PreRegistartionStatusDTO preRegistartionStatus = new PreRegistartionStatusDTO();
		preRegistartionStatus.setStatusCode(StatusCodes.EXPIRED.getCode());
		preRegistartionStatus.setPreRegistartionId("12345678909876");
		statusListrebook.add(preRegistartionStatus);
		MainListResponseDTO<PreRegistartionStatusDTO> preRegResponseRebook = new MainListResponseDTO<PreRegistartionStatusDTO>();
		preRegResponseRebook.setErr(null);
		preRegResponseRebook.setResponse(statusListrebook);

		RegistrationBookingEntity bookingEntityRebook = new RegistrationBookingEntity();
		bookingEntityRebook.setBookingPK(
				new RegistrationBookingPK("12345678909876", DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntityRebook.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntityRebook.setLangCode("12L");
		bookingEntityRebook.setCrBy("987654321");
		bookingEntityRebook.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntityRebook.setRegDate(LocalDate.parse(oldBookingRegistrationDTO.getRegDate()));
		bookingEntityRebook.setSlotFromTime(LocalTime.parse(oldBookingRegistrationDTO.getSlotFromTime()));
		bookingEntityRebook.setSlotToTime(LocalTime.parse(oldBooking.getSlotToTime()));

		requestValidatorFlag = ValidationUtil.requestValidator(bookingDto);

		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		Mockito.when(bookingDAO.findByPreRegistrationId("12345678909876")).thenReturn(bookingEntity);
		ResponseEntity<MainListResponseDTO<PreRegistartionStatusDTO>> respEntity = new ResponseEntity<>(preRegResponseRebook, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainListResponseDTO<PreRegistartionStatusDTO>>() {}))).thenReturn(respEntity);
		Mockito.when(mapper.convertValue(respEntity.getBody().getResponse().get(0), PreRegistartionStatusDTO.class))
				.thenReturn(preRegistartionStatus);

		MainResponseDTO<List<BookingStatusDTO>> response = service.bookAppointment(reBookingMainDto);
		assertEquals(1, response.getResponse().size());
	}

//	@Test
	public void successRebookAppointment() {
		MainListRequestDTO<BookingRequestDTO> reBookingMainDto = new MainListRequestDTO<>();
		BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
		BookingRegistrationDTO oldBookingRegistrationDTO = new BookingRegistrationDTO();
		BookingRegistrationDTO newBookingRegistrationDTO = new BookingRegistrationDTO();
		oldBookingRegistrationDTO.setRegistrationCenterId("1");
		oldBookingRegistrationDTO.setSlotFromTime("09:00");
		oldBookingRegistrationDTO.setSlotToTime("09:15");
		oldBookingRegistrationDTO.setRegDate("2019-12-06");

		newBookingRegistrationDTO.setRegistrationCenterId("10005");
		newBookingRegistrationDTO.setSlotFromTime("09:00");
		newBookingRegistrationDTO.setSlotToTime("09:15");
		newBookingRegistrationDTO.setRegDate("2019-12-12");

		bookingRequestDTO.setPreRegistrationId("12345678909876");
		bookingRequestDTO.setNewBookingDetails(newBookingRegistrationDTO);
		bookingRequestDTO.setOldBookingDetails(oldBookingRegistrationDTO);
		List<BookingRequestDTO> rebookingReqList = new ArrayList<>();
		rebookingReqList.add(bookingRequestDTO);

		reBookingMainDto.setId("mosip.pre-registration.booking.book");
		reBookingMainDto.setVersion("1.0");
		reBookingMainDto.setRequesttime(new Date());
		reBookingMainDto.setRequest(rebookingReqList);

		MainResponseDTO<List<BookingStatusDTO>> responseDTO = new MainResponseDTO<>();
		BookingStatusDTO bookingStatusDTO = new BookingStatusDTO();
		bookingStatusDTO.setPreRegistrationId("12345678909876");
		bookingStatusDTO.setBookingStatus(StatusCodes.BOOKED.getCode());
		bookingStatusDTO.setBookingMessage("APPOINTMENT_SUCCESSFULLY_BOOKED");

		List<BookingStatusDTO> respList = new ArrayList<>();
		respList.add(bookingStatusDTO);
		responseDTO.setResponse(respList);
		responseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
		List<PreRegistartionStatusDTO> statusListrebook = new ArrayList<>();
		PreRegistartionStatusDTO preRegistartionStatus = new PreRegistartionStatusDTO();
		preRegistartionStatus.setStatusCode(StatusCodes.BOOKED.getCode());
		preRegistartionStatus.setPreRegistartionId("12345678909876");
		statusListrebook.add(preRegistartionStatus);
		MainListResponseDTO<PreRegistartionStatusDTO> preRegResponseRebook = new MainListResponseDTO<PreRegistartionStatusDTO>();
		preRegResponseRebook.setErr(null);
		preRegResponseRebook.setResponse(statusListrebook);

		RegistrationBookingEntity bookingEntityRebook = new RegistrationBookingEntity();
		bookingEntityRebook.setBookingPK(
				new RegistrationBookingPK("12345678909876", DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntityRebook.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntityRebook.setLangCode("12L");
		bookingEntityRebook.setCrBy("987654321");
		bookingEntityRebook.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntityRebook.setRegDate(LocalDate.parse(oldBookingRegistrationDTO.getRegDate()));
		bookingEntityRebook.setSlotFromTime(LocalTime.parse(oldBookingRegistrationDTO.getSlotFromTime()));
		bookingEntityRebook.setSlotToTime(LocalTime.parse(oldBooking.getSlotToTime()));

		availableEntity.setAvailableKiosks(3);
		availableEntity.setRegcntrId("1");
		availableEntity.setRegDate(LocalDate.parse("2019-12-04"));
		availableEntity.setToTime(localTime2);
		availableEntity.setFromTime(localTime1);
		availableEntity.setCrBy("987654321");
		availableEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntity.setDeleted(false);

		requestValidatorFlag = ValidationUtil.requestValidator(bookingDto);

		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);
		availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() + 1);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		Mockito.when(bookingDAO.findByPreRegistrationId("12345678909876")).thenReturn(bookingEntity);
		ResponseEntity<MainListResponseDTO<PreRegistartionStatusDTO>> respEntity = new ResponseEntity<>(preRegResponseRebook, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainListResponseDTO<PreRegistartionStatusDTO>>() {}))).thenReturn(respEntity);
		Mockito.when(mapper.convertValue(respEntity.getBody().getResponse().get(0), PreRegistartionStatusDTO.class))
				.thenReturn(preRegistartionStatus);

		MainResponseDTO<List<BookingStatusDTO>> response = service.bookAppointment(reBookingMainDto);
		assertEquals(1, response.getResponse().size());
	}

//	@Test
	public void addAvailabilityServiceTest() {

		String date1 = "2016-11-09 09:00:00";
		String date2 = "2016-11-09 17:00:00";
		String date3 = "2016-11-09 00:20:00";
		String date4 = "2016-11-09 13:00:00";
		String date5 = "2016-11-09 14:20:00";
		LocalDateTime localDateTime1 = LocalDateTime.parse(date1, format);
		LocalDateTime localDateTime2 = LocalDateTime.parse(date2, format);
		LocalDateTime localDateTime3 = LocalDateTime.parse(date3, format);
		LocalTime startTime = localDateTime1.toLocalTime();
		LocalTime endTime = localDateTime2.toLocalTime();
		LocalTime perKioskTime = localDateTime3.toLocalTime();
		LocalTime LunchStartTime = LocalDateTime.parse(date4, format).toLocalTime();
		LocalTime LunchEndTime = LocalDateTime.parse(date5, format).toLocalTime();
		RegistrationCenterDto centerDto = new RegistrationCenterDto();
		List<RegistrationCenterDto> centerList = new ArrayList<>();
		centerDto.setId("1");
		centerDto.setLangCode("LOC01");
		centerDto.setCenterStartTime(startTime);
		centerDto.setCenterEndTime(endTime);
		centerDto.setPerKioskProcessTime(perKioskTime);
		centerDto.setLunchStartTime(LunchStartTime);
		centerDto.setLunchEndTime(LunchEndTime);
		centerDto.setNumberOfKiosks((short) 4);
		centerList.add(centerDto);
		regCenDto.setRegistrationCenters(centerList);
		RegistrationCenterHolidayDto CenholidayDto = new RegistrationCenterHolidayDto();
		HolidayDto holiday = new HolidayDto();
		List<HolidayDto> holidayList = new ArrayList<>();
		holiday.setHolidayDate("2018-12-12");
		holidayList.add(holiday);
		CenholidayDto.setHolidays(holidayList);

		MainResponseDTO<String> response = new MainResponseDTO<>();

		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);

		ResponseEntity<RegistrationCenterResponseDto> rescenter = new ResponseEntity<>(regCenDto, HttpStatus.OK);
		ResponseEntity<RegistrationCenterHolidayDto> resHoliday = new ResponseEntity<>(CenholidayDto, HttpStatus.OK);

		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(RegistrationCenterResponseDto.class))).thenReturn(rescenter);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(RegistrationCenterHolidayDto.class))).thenReturn(resHoliday);
		response = service.addAvailability();
		assertEquals("MASTER_DATA_SYNCED_SUCCESSFULLY", response.getResponse());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@Test
	public void cancelAppointmentSuccessTest() throws java.text.ParseException {

		String date5 = "2016-11-09 14:20:00";
		Date localDateTime1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date5);
		preRegistartionStatusDTO.setStatusCode(StatusCodes.BOOKED.getCode());
		preRegistartionStatusDTO.setPreRegistartionId("23587986034785");
		statusList.add(preRegistartionStatusDTO);

		requestValidatorFlag = ValidationUtil.requestValidator(cancelRequestdto);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);

		ResponseEntity<MainListResponseDTO<PreRegistartionStatusDTO>> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);

		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainListResponseDTO<PreRegistartionStatusDTO>>() {}))).thenReturn(res);

		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);

		Mockito.when(bookingDAO.findByPreRegistrationId(Mockito.any())).thenReturn(bookingEntity);

		// Mockito.when(bookingDAO.saveRegistrationEntityForCancel(Mockito.any())).thenReturn(bookingEntity);
		Mockito.doNothing().when(bookingDAO).deleteRegistrationEntity(Mockito.any());
		MainResponseDTO mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(bookingEntity);
		ResponseEntity<MainResponseDTO<String>> resp = new ResponseEntity<>(mainResponseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<String>>() {}))).thenReturn(resp);
		availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() + 1);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		MainResponseDTO<CancelBookingResponseDTO> responseDto = service.cancelAppointment(cancelRequestdto);
		assertEquals("APPOINTMENT_SUCCESSFULLY_CANCELED", responseDto.getResponse().getMessage());

	} 

	@Test
	public void getAppointmentDetailsTest() {
		MainListRequestDTO<BookingRequestDTO> reBookingMainDto = new MainListRequestDTO<>();
		BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
		BookingRegistrationDTO oldBookingRegistrationDTO = new BookingRegistrationDTO();
		BookingRegistrationDTO newBookingRegistrationDTO = new BookingRegistrationDTO();
		oldBookingRegistrationDTO.setRegistrationCenterId("1");
		oldBookingRegistrationDTO.setSlotFromTime("09:00");
		oldBookingRegistrationDTO.setSlotToTime("09:15");
		oldBookingRegistrationDTO.setRegDate("2019-12-06");

		newBookingRegistrationDTO.setRegistrationCenterId("10005");
		newBookingRegistrationDTO.setSlotFromTime("09:00");
		newBookingRegistrationDTO.setSlotToTime("09:15");
		newBookingRegistrationDTO.setRegDate("2019-12-12");

		bookingRequestDTO.setPreRegistrationId("12345678909876");
		bookingRequestDTO.setNewBookingDetails(newBookingRegistrationDTO);
		bookingRequestDTO.setOldBookingDetails(oldBookingRegistrationDTO);
		List<BookingRequestDTO> rebookingReqList = new ArrayList<>();
		rebookingReqList.add(bookingRequestDTO);

		reBookingMainDto.setId("mosip.pre-registration.booking.book");
		reBookingMainDto.setVersion("1.0");
		reBookingMainDto.setRequesttime(new Date());
		reBookingMainDto.setRequest(rebookingReqList);

		MainResponseDTO<List<BookingStatusDTO>> responseDTO = new MainResponseDTO<>();
		BookingStatusDTO bookingStatusDTO = new BookingStatusDTO();
		bookingStatusDTO.setPreRegistrationId("12345678909876");
		bookingStatusDTO.setBookingStatus(StatusCodes.BOOKED.getCode());
		bookingStatusDTO.setBookingMessage("APPOINTMENT_SUCCESSFULLY_BOOKED");

		List<BookingStatusDTO> respList = new ArrayList<>();
		respList.add(bookingStatusDTO);
		responseDTO.setResponse(respList);
		responseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
		List<PreRegistartionStatusDTO> statusListrebook = new ArrayList<>();
		PreRegistartionStatusDTO preRegistartionStatus = new PreRegistartionStatusDTO();
		preRegistartionStatus.setStatusCode(StatusCodes.BOOKED.getCode());
		preRegistartionStatus.setPreRegistartionId("12345678909876");
		statusListrebook.add(preRegistartionStatus);
		MainListResponseDTO<PreRegistartionStatusDTO> preRegResponseRebook = new MainListResponseDTO<PreRegistartionStatusDTO>();
		preRegResponseRebook.setErr(null);
		preRegResponseRebook.setResponse(statusListrebook);
		RegistrationBookingEntity bookingEntityRebook = new RegistrationBookingEntity();
		bookingEntityRebook.setBookingPK(
				new RegistrationBookingPK("12345678909876", DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntityRebook.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntityRebook.setLangCode("12L");
		bookingEntityRebook.setCrBy("987654321");
		bookingEntityRebook.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntityRebook.setRegDate(LocalDate.parse(oldBookingRegistrationDTO.getRegDate()));
		bookingEntityRebook.setSlotFromTime(LocalTime.parse(oldBookingRegistrationDTO.getSlotFromTime()));
		bookingEntityRebook.setSlotToTime(LocalTime.parse(oldBookingRegistrationDTO.getSlotToTime()));
		Mockito.when(bookingDAO.findByPreRegistrationId("12345678909876")).thenReturn(bookingEntityRebook);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class); 
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		ResponseEntity<MainListResponseDTO<PreRegistartionStatusDTO>> respEntity = new ResponseEntity<>(preRegResponseRebook, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainListResponseDTO<PreRegistartionStatusDTO>>() {}))).thenReturn(respEntity);
		MainResponseDTO<BookingRegistrationDTO> responseDto = service.getAppointmentDetails("12345678909876");
		assertEquals("1", responseDto.getResponse().getRegistrationCenterId());
	}

	@Test(expected = BookingDataNotFoundException.class)
	public void getAppointmentDetailsTestFail() {
		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenReturn(bookingEntity);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		ResponseEntity<MainListResponseDTO<PreRegistartionStatusDTO>> respEntity = new ResponseEntity<>(preRegResponse, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainListResponseDTO<PreRegistartionStatusDTO>>() {}))).thenReturn(respEntity);
		MainResponseDTO<BookingRegistrationDTO> responseDto = service.getAppointmentDetails("23587986034785");
	}

	@Test(expected = TableNotAccessibleException.class)
	public void getAppointmentDetailsFailureTest() {
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		ResponseEntity<MainListResponseDTO<PreRegistartionStatusDTO>> respEntity = new ResponseEntity<>(preRegResponse, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainListResponseDTO<PreRegistartionStatusDTO>>() {}))).thenReturn(respEntity);
		Mockito.when(bookingDAO.findByPreRegistrationId(Mockito.anyString()))
				.thenThrow(new DataAccessLayerException("", "", new Throwable()));
		service.getAppointmentDetails("23587986034785");
	}

	@Test
	public void bookTest() {
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);

		MainResponseDTO mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(bookingEntity);
		ResponseEntity<MainResponseDTO> resp = new ResponseEntity<>(mainResponseDTO, HttpStatus.OK);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		Mockito.when(bookingDAO.saveRegistrationEntityForBooking(Mockito.any())).thenReturn(bookingEntity);

		ResponseEntity<MainResponseDTO<String>> resp2 = new ResponseEntity<>(mainResponseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<String>>() {}))).thenReturn(resp2);
		BookingStatusDTO response = service.book("23587986034785", newBooking);
		assertEquals("APPOINTMENT_SUCCESSFULLY_BOOKED", response.getBookingMessage());
	}

//	@Test(expected = TableNotAccessibleException.class)
	public void cancelBookingFailureTest() throws java.text.ParseException {

		String date5 = "2016-11-09 14:20:00";
		Date localDateTime1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date5);
		preRegistartionStatusDTO.setStatusCode(StatusCodes.BOOKED.getCode());
		preRegistartionStatusDTO.setPreRegistartionId("23587986034785");
		statusList.add(preRegistartionStatusDTO);

		requestValidatorFlag = ValidationUtil.requestValidator(cancelRequestdto);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);

		ResponseEntity<MainListResponseDTO<PreRegistartionStatusDTO>> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);

		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainListResponseDTO<PreRegistartionStatusDTO>>() {}))).thenReturn(res);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenThrow(new DataAccessLayerException("", "", new Throwable()));
		service.cancelBooking(cancelbookingDto);
	}

//	@Test(expected = TimeSpanException.class)
	public void cancelTimeSpanFailureTest() throws java.text.ParseException {
		AppointmentReBookException exception = new AppointmentReBookException(ErrorCodes.PRG_BOOK_RCI_026.getCode(),
				ErrorMessages.BOOKING_STATUS_CANNOT_BE_ALTERED.getMessage());

		List<RegistrationBookingEntity> registrationEntityList = new ArrayList<>();
		RegistrationBookingEntity bookingEntity = new RegistrationBookingEntity();
		bookingEntity.setBookingPK(
				new RegistrationBookingPK("23587986034785", DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity.setLangCode("12L");
		bookingEntity.setCrBy("987654321");
		bookingEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		System.out.println(LocalDate.now());
		bookingEntity.setRegDate(LocalDate.now());
		bookingEntity.setSlotFromTime(LocalTime.parse(oldBooking.getSlotFromTime()));
		bookingEntity.setSlotToTime(LocalTime.parse(oldBooking.getSlotToTime()));
		registrationEntityList.add(bookingEntity);
		MainRequestDTO<CancelBookingDTO> cancelRequestdto2 = new MainRequestDTO<>();
		cancelRequestdto2.setRequesttime(new Date());
		cancelRequestdto2.setRequest(cancelbookingDto);
		cancelRequestdto2.setId("mosip.pre-registration.booking.book");
		cancelRequestdto2.setVersion("1.0");
		CancelBookingDTO cancelbookingDto2 = new CancelBookingDTO();
		cancelbookingDto2.setPreRegistrationId("23587986034785");
		cancelbookingDto2.setRegDate(LocalDate.now().toString());
		cancelbookingDto2.setRegistrationCenterId("1");
		cancelbookingDto2.setSlotFromTime(LocalTime.now().toString());
		cancelbookingDto2.setSlotToTime("09:13");
		preRegistartionStatusDTO.setStatusCode(StatusCodes.BOOKED.getCode());
		preRegistartionStatusDTO.setPreRegistartionId("23587986034785");
		statusList.add(preRegistartionStatusDTO);

		requestValidatorFlag = ValidationUtil.requestValidator(cancelRequestdto);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);

		ResponseEntity<MainListResponseDTO<PreRegistartionStatusDTO>> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);

		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenReturn(bookingEntity);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainListResponseDTO<PreRegistartionStatusDTO>>() {}))).thenReturn(res);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);
		service.cancelBooking(cancelbookingDto);
	}

	@Test
	public void deleteBooking() {

		List<DeleteBookingDTO> deleteList = new ArrayList<>();
		DeleteBookingDTO deleteDto = new DeleteBookingDTO();
		List<RegistrationBookingEntity> registrationEntityList = new ArrayList<>();
		RegistrationBookingEntity bookingEntity = new RegistrationBookingEntity();
		bookingEntity.setBookingPK(
				new RegistrationBookingPK("12345678909876", DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity.setLangCode("12L");
		bookingEntity.setCrBy("987654321");
		bookingEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntity.setRegDate(LocalDate.parse(oldBooking.getRegDate()));
		bookingEntity.setSlotFromTime(LocalTime.parse(oldBooking.getSlotFromTime()));
		bookingEntity.setSlotToTime(LocalTime.parse(oldBooking.getSlotToTime()));
		registrationEntityList.add(bookingEntity);

		Mockito.when(bookingDAO.findByPreregistrationId(Mockito.anyString())).thenReturn(registrationEntityList);
		deleteDto.setDeletedBy("987654321");
		deleteDto.setDeletedDateTime(new Date(System.currentTimeMillis()));
		deleteDto.setPreRegistrationId("12345678909876");
		deleteList.add(deleteDto);
		Mockito.when(bookingDAO.deleteByPreRegistrationId(Mockito.anyString())).thenReturn(1);

		MainListResponseDTO<DeleteBookingDTO> response = new MainListResponseDTO<>();
		response.setErr(null);
		response.setResponse(deleteList);
		response.setResponsetime(serviceUtil.getCurrentResponseTime());

		assertEquals(response.getResponse().get(0).getPreRegistrationId(),
				service.deleteBooking("12345678909876").getResponse().get(0).getPreRegistrationId());

	}

	//@Test
	public void getBookedPreIdsByDateTest() {
		LocalDate fromDate = LocalDate.now();
		LocalDate toDate = LocalDate.now();
		MainListResponseDTO<String> response = new MainListResponseDTO<>();
		List<String> preIds = new ArrayList<>();
		List<String> details = new ArrayList<>();
		details.add("98746563542672");

		preIds.add("98746563542672");
		response.setResponse(preIds);
		//response.setStatus(true);

		LocalDateTime fromLocaldate = fromDate.atStartOfDay();

		LocalDateTime toLocaldate = toDate.atTime(23, 59, 59);
		Mockito.when(bookingDAO.findByBookingDateBetweenAndRegCenterId(fromLocaldate, toLocaldate, "10001"))
				.thenReturn(details);

		MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO> actualRes = service.getBookedPreRegistrationByDate(fromDate,
				toDate, "10001");
		//assertEquals(actualRes.getVersion(), response.isStatus());

	}

	@Test(expected = BookingDataNotFoundException.class)
	public void getApplicationByDateFailureTest() {

		//requestValidatorFlag = ValidationUtil.requestValidator(requestMap1, requiredRequestMap);
		// bookingEntities.add(bookingEntity);
		List<String> preids = new ArrayList<>();
		preids.add("");
		LocalDate fromDate = LocalDate.now();
		LocalDate toDate = LocalDate.now();
		LocalDateTime fromLocaldate = fromDate.atStartOfDay();

		LocalDateTime toLocaldate = toDate.atTime(23, 59, 59);
		Mockito.when(bookingDAO.findByBookingDateBetweenAndRegCenterId(fromLocaldate, toLocaldate, "10001"))
				.thenThrow(new BookingDataNotFoundException("", "", new Throwable()));
		service.getBookedPreRegistrationByDate(fromDate, toDate, "10001");

	}

	@Test(expected = AvailablityNotFoundException.class)
	public void checkSlotAvailabilityTest() {
		AvailablityNotFoundException exception = new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_002.getCode(),
				ErrorMessages.AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME.getMessage());
		AvailibityEntity availableEntityNull = new AvailibityEntity();
		availableEntityNull.setAvailableKiosks(0);
		availableEntityNull.setRegcntrId("1");
		availableEntityNull.setRegDate(LocalDate.parse("2018-12-04"));
		availableEntityNull.setCrBy("987654321");
		availableEntityNull.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntityNull.setDeleted(false);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.anyString())).thenReturn(availableEntityNull);
		service.checkSlotAvailability(newBooking);
	}

	@Test(expected = RecordFailedToDeleteException.class)
	public void deleteOldBookingTest() {
		RecordFailedToDeleteException exception = new RecordFailedToDeleteException(
				ErrorCodes.PRG_BOOK_RCI_028.getCode(),
				ErrorMessages.FAILED_TO_DELETE_THE_PRE_REGISTRATION_RECORD.getMessage());
		Mockito.when(service.deleteOldBooking("12345678909876")).thenThrow(exception);
		service.deleteOldBooking("12345678909876");
	}

	@Test(expected = AvailablityNotFoundException.class)
	public void increaseAvailabilityTest() {
		AvailablityNotFoundException exception = new AvailablityNotFoundException(
				ErrorCodes.PRG_BOOK_RCI_002.toString(),
				ErrorMessages.AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME.toString());
		AvailibityEntity available = new AvailibityEntity();
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.anyString())).thenThrow(exception);
		service.increaseAvailability(newBooking);
	}
}
