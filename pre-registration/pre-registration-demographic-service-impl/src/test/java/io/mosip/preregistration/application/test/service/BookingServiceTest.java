package io.mosip.preregistration.application.test.service;

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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.exception.FileNotFoundException;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.application.DemographicTestApplication;
import io.mosip.preregistration.booking.serviceimpl.codes.RequestCodes;
import io.mosip.preregistration.booking.serviceimpl.dto.AvailabilityDto;
import io.mosip.preregistration.booking.serviceimpl.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.serviceimpl.dto.BookingStatus;
import io.mosip.preregistration.booking.serviceimpl.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.serviceimpl.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.serviceimpl.dto.DateTimeDto;
import io.mosip.preregistration.booking.serviceimpl.dto.HolidayDto;
import io.mosip.preregistration.booking.serviceimpl.dto.MultiBookingRequest;
import io.mosip.preregistration.booking.serviceimpl.dto.MultiBookingRequestDTO;
import io.mosip.preregistration.booking.serviceimpl.dto.RegistrationCenterDto;
import io.mosip.preregistration.booking.serviceimpl.dto.RegistrationCenterHolidayDto;
import io.mosip.preregistration.booking.serviceimpl.dto.RegistrationCenterResponseDto;
import io.mosip.preregistration.booking.serviceimpl.dto.SlotDto;
import io.mosip.preregistration.booking.serviceimpl.entity.AvailibityEntity;
import io.mosip.preregistration.booking.serviceimpl.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.serviceimpl.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.serviceimpl.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.serviceimpl.exception.BookingDataNotFoundException;
import io.mosip.preregistration.booking.serviceimpl.exception.RecordNotFoundException;
import io.mosip.preregistration.booking.serviceimpl.exception.TimeSpanException;
import io.mosip.preregistration.booking.serviceimpl.repository.BookingAvailabilityRepository;
import io.mosip.preregistration.booking.serviceimpl.repository.RegistrationBookingRepository;
import io.mosip.preregistration.booking.serviceimpl.repository.impl.BookingDAO;
import io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf;
import io.mosip.preregistration.booking.serviceimpl.service.util.BookingServiceUtil;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.core.common.dto.DeleteBookingDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.common.entity.DemographicEntity;
import io.mosip.preregistration.core.common.entity.RegistrationBookingEntity;
import io.mosip.preregistration.core.common.entity.RegistrationBookingPK;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.core.util.RequestValidator;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.demographic.service.DemographicServiceIntf;
import io.mosip.preregistration.document.service.DocumentServiceIntf;

/**
 * Booking service Test
 * 
 * @author Kishan Rathore
 * @author Tapaswini Behera
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { DemographicTestApplication.class })
public class BookingServiceTest {

	@MockBean
	private BookingAvailabilityRepository bookingAvailabilityRepository;

	@MockBean
	private RegistrationBookingRepository registrationBookingRepository;

	/**
	 * Mocking the RestTemplateBuilder bean
	 */
	@MockBean(name="restTemplate")
	RestTemplate restTemplate;

	@MockBean
	private SecurityContextHolder context;

	@MockBean
	private RequestValidator requestValidator;

	@MockBean
	private AuditLogUtil auditLogUtil;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@MockBean
	private DemographicServiceIntf demographicServiceIntf;

	@Autowired
	private BookingServiceIntf service;

	@MockBean
	private DocumentServiceIntf documentServiceIntf;

	@MockBean
	private BookingServiceUtil serviceUtil;

	private DemographicEntity preRegistrationEntity;

	/**
	 * Mocking the JsonValidatorImpl bean
	 */
	@MockBean(name = "idObjectValidator")
	private IdObjectValidator jsonValidator;

	@MockBean
	ObjectMapper mapper;

	@MockBean
	private BookingDAO bookingDAO;

	AuditRequestDto auditRequestDto = new AuditRequestDto();

	private AvailabilityDto availability = new AvailabilityDto();
	private List<DateTimeDto> dateList = new ArrayList<>();
	private DateTimeDto dateDto = new DateTimeDto();
	private List<SlotDto> slotsList = new ArrayList<>();

	private SlotDto slots = new SlotDto();

	AvailibityEntity availableEntity = new AvailibityEntity();
	RegistrationBookingEntity bookingEntity = new RegistrationBookingEntity();
	List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
	PreRegistartionStatusDTO preRegistartionStatusDTO = new PreRegistartionStatusDTO();
	@SuppressWarnings("rawtypes")
	MainResponseDTO preRegResponse = new MainResponseDTO();

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
	MainRequestDTO<BookingRequestDTO> bookingDto = new MainRequestDTO<>();
	MainRequestDTO<BookingRequestDTO> reBookingDto = new MainRequestDTO<>();

	MultiBookingRequestDTO multiBookingRequestDto1 = new MultiBookingRequestDTO();
	MultiBookingRequestDTO multiBookingRequestDto2 = new MultiBookingRequestDTO();

	List<MultiBookingRequestDTO> multiBookingListDto = new ArrayList<>();

	@Value("${version}")
	String versionUrl;

	@Value("${id}")
	String idUrl;

	@Value("${demographic.resource.url}")
	private String preRegResourceUrl;

	// Rebooking
	String reBookingPreId = "23587986034785";
	// booking
	String bookingPreId = "23587986034785";
	RegistrationCenterDto centerDto = new RegistrationCenterDto();
	List<RegistrationCenterDto> centerList = new ArrayList<>();

	LocalTime startTime;
	LocalTime endTime;
	LocalTime perKioskTime;
	LocalTime LunchStartTime;
	LocalTime LunchEndTime;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() throws URISyntaxException, FileNotFoundException, ParseException, java.io.FileNotFoundException,
			IOException, org.json.simple.parser.ParseException {
		
		LocalDateTime localDateTime1 = LocalDateTime.now();
		LocalDateTime localDateTime2 = localDateTime1.plusMinutes(15);
		localTime1 = localDateTime1.toLocalTime();
		localTime2 = localDateTime2.toLocalTime();
		slots.setAvailability(4);
		slots.setFromTime(localTime1);
		slots.setToTime(localTime2);
		slotsList.add(slots);
		dateDto.setDate(LocalDate.now().toString());
		dateDto.setHoliday(false);
		dateDto.setTimeSlots(slotsList);
		dateList.add(dateDto);
		availability.setCenterDetails(dateList);
		availability.setRegCenterId("1");

		startTime = localDateTime1.toLocalTime();
		endTime = localDateTime2.toLocalTime();
		perKioskTime = LocalTime.MIDNIGHT.plusMinutes(15);
		LunchStartTime = LocalTime.NOON.plusHours(1);
		LunchEndTime = LunchStartTime.plusHours(1);

		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();

		URI dataSyncUri = new URI(
				classLoader.getResource("booking.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File file = new File(dataSyncUri.getPath());
		parser.parse(new FileReader(file));
		
		oldBooking.setRegistrationCenterId("1");
		oldBooking.setSlotFromTime(LocalTime.of(9,0).toString());
		oldBooking.setSlotToTime(LocalTime.of(9,15).toString());
		oldBooking.setRegDate(LocalDate.now().toString());

		newBooking.setRegistrationCenterId("1");
		newBooking.setSlotFromTime(LocalTime.of(9,0).toString());
		newBooking.setSlotToTime(LocalTime.of(9,15).toString());
		newBooking.setRegDate(LocalDate.now().plusDays(5).toString());

		oldBooking_success.setRegistrationCenterId("1");
		oldBooking_success.setSlotFromTime("09:00");
		oldBooking_success.setSlotToTime("09:13");
		oldBooking_success.setRegDate("2019-12-05");

		statusDTOA.setBookingMessage("Appointment booked successfully");

		statusDTOB.setBookingMessage("Appointment booked successfully");

		List<BookingStatusDTO> resp = new ArrayList<>();
		mapper.setTimeZone(TimeZone.getDefault());
		resp.add(statusDTOA);
		resp.add(statusDTOB);
		responseDto.setResponse(resp);
		responseDto.setErrors(null);
		responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		requiredRequestMap.put("id", idUrl);
		requiredRequestMap.put("version", versionUrl);

		cancelRequestdto.setRequesttime(new Date());
		cancelRequestdto.setRequest(cancelbookingDto);
		cancelRequestdto.setId("mosip.pre-registration.booking.book");
		cancelRequestdto.setVersion("1.0");
		cancelbookingDto.setRegDate(LocalDate.now().plusDays(30).toString());
		cancelbookingDto.setRegistrationCenterId("1");
		cancelbookingDto.setSlotFromTime(LocalTime.of(9,0).toString());
		cancelbookingDto.setSlotToTime(LocalTime.of(9,15).toString());
		requestMap1.put("id", cancelRequestdto.getId());
		requestMap1.put("version", cancelRequestdto.getVersion());
		requestMap1.put("reqTime",
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(cancelRequestdto.getRequesttime()));
		requestMap1.put("request", cancelRequestdto.getRequest().toString());

		availableEntity.setAvailableKiosks(4);
		availableEntity.setRegcntrId("1");
		availableEntity.setRegDate(LocalDate.now().plusDays(30));
		availableEntity.setToTime(localTime2);
		availableEntity.setFromTime(localTime1);
		availableEntity.setCrBy("John Doe");
		availableEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntity.setDeleted(false);
		preRegistrationEntity = new DemographicEntity();
		preRegistrationEntity.setCreateDateTime(LocalDateTime.now());
		preRegistrationEntity.setCreatedBy("John Doe");
		preRegistrationEntity.setStatusCode("Pending_Appointment");
		preRegistrationEntity.setUpdateDateTime(LocalDateTime.now());
		preRegistrationEntity.setPreRegistrationId("48690172097499");

		bookingEntity.setBookingPK(new RegistrationBookingPK(DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity.setLangCode("12L");
		bookingEntity.setCrBy("John Doe");
		bookingEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntity.setRegDate(LocalDate.parse(oldBooking.getRegDate()));
		bookingEntity.setSlotFromTime(LocalTime.parse(oldBooking.getSlotFromTime()));
		bookingEntity.setSlotToTime(LocalTime.parse(oldBooking.getSlotToTime()));
		bookingEntity.setDemographicEntity(preRegistrationEntity);

		preRegistartionStatusDTO.setStatusCode(StatusCodes.PENDING_APPOINTMENT.getCode());
		preRegistartionStatusDTO.setPreRegistartionId("23587986034785");
		statusList.add(preRegistartionStatusDTO);

		preRegResponse.setResponse(statusList);
		preRegResponse.setErrors(null);
		List<String> preId = new ArrayList<>();
		preId.add("1234567890");
		preRegIdsByRegCenterIdDTO.setRegistrationCenterId("1");
		preRegIdsByRegCenterIdDTO.setPreRegistrationIds(preId);
		requestDTO.setRequest(preRegIdsByRegCenterIdDTO);

		requestDTO.setId("mosip.preregistration.booking.book");
		requestDTO.setVersion("1.0");
		requestDTO.setRequesttime(new Date());
		bookingDto.setId("mosip.pre-registration.booking.book");
		bookingDto.setRequesttime(new Date());
		bookingDto.setVersion("1.0");

		// Rebook
		reBookingDto.setId("mosip.pre-registration.booking.book");
		reBookingDto.setRequesttime(new Date());
		reBookingDto.setVersion("1.0");

		AuthUserDetails applicationUser = Mockito.mock(AuthUserDetails.class);
		Authentication authentication = Mockito.mock(Authentication.class);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
		auditRequestDto.setActionTimeStamp(LocalDateTime.now(ZoneId.of("UTC")));
		auditRequestDto.setApplicationId(AuditLogVariables.MOSIP_1.toString());
		auditRequestDto.setApplicationName(AuditLogVariables.PREREGISTRATION.toString());
		auditRequestDto.setCreatedBy(AuditLogVariables.SYSTEM.toString());
		auditRequestDto.setHostIp(auditLogUtil.getServerIp());
		auditRequestDto.setHostName(auditLogUtil.getServerName());
		auditRequestDto.setId(AuditLogVariables.NO_ID.toString());
		auditRequestDto.setIdType(AuditLogVariables.PRE_REGISTRATION_ID.toString());
		auditRequestDto.setSessionUserId(AuditLogVariables.SYSTEM.toString());
		auditRequestDto.setSessionUserName(AuditLogVariables.SYSTEM.toString());
		
		//Get valid reg center list
		centerDto.setId("1");
		centerDto.setLangCode("eng");
		centerDto.setCenterStartTime(startTime);
		centerDto.setCenterEndTime(endTime);
		centerDto.setPerKioskProcessTime(perKioskTime);
		centerDto.setLunchStartTime(LunchStartTime);
		centerDto.setLunchEndTime(LunchEndTime);
		centerDto.setNumberOfKiosks((short) 4);
		centerList.add(centerDto);
	}

	@Test
	public void getAvailabilityTest() {

		logger.info("Availability dto " + availability);
		AvailibityEntity availableEntity1 = new AvailibityEntity();

		availableEntity1.setAvailableKiosks(4);
		availableEntity1.setRegcntrId("1");
		availableEntity1.setRegDate(LocalDate.now());
		availableEntity1.setToTime(localTime2);
		availableEntity1.setFromTime(localTime1);
		availableEntity1.setCrBy("John Doe");
		availableEntity1.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntity1.setDeleted(false);
		List<LocalDate> date = new ArrayList<>();
		List<AvailibityEntity> entityList = new ArrayList<>();
		date.add(LocalDate.now());
		entityList.add(availableEntity);
		entityList.add(availableEntity1);
		logger.info("Availability entity " + availableEntity);

		RegistrationCenterDto centerDto = new RegistrationCenterDto();
		List<RegistrationCenterDto> centerList = new ArrayList<>();
		centerDto.setId("10001");
		centerDto.setLangCode("eng");
		centerDto.setCenterStartTime(startTime);
		centerDto.setCenterEndTime(endTime);
		centerDto.setPerKioskProcessTime(perKioskTime);
		centerDto.setLunchStartTime(LunchStartTime);
		centerDto.setLunchEndTime(LunchEndTime);
		centerDto.setNumberOfKiosks((short) 4);
		centerList.add(centerDto);
		regCenDto.setRegistrationCenters(centerList);
		ResponseWrapper<RegistrationCenterResponseDto> resp = new ResponseWrapper<>();
		resp.setResponse(regCenDto);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		Mockito.when(serviceUtil.mandatoryParameterCheck(Mockito.anyString(), Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.isValidRegCenter(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.isKiosksAvailable(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.getDemographicStatus(Mockito.anyString())).thenReturn("Booked");
		Mockito.when(serviceUtil.getRegCenterMasterData()).thenReturn(centerList);
		Mockito.when(bookingDAO.findAvailability(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn(entityList);
		MainResponseDTO<AvailabilityDto> responseDto = service.getAvailability("10001");
		logger.info("Response " + responseDto);
		assertEquals("10001", responseDto.getResponse().getRegCenterId());

	}

    @Test(expected = RecordNotFoundException.class)
	public void getAvailabilityFailureTest() {
    	RecordNotFoundException ex=	new RecordNotFoundException(ErrorCodes.PRG_BOOK_RCI_035.getCode(),
				ErrorMessages.REG_CENTER_ID_NOT_FOUND.getMessage());
		Mockito.when(serviceUtil.isValidRegCenter("1")).thenThrow(ex);
		service.getAvailability("1");

	}

	@SuppressWarnings("unchecked")
	@Test
	public void successBookAppointment() {

		MainRequestDTO<BookingRequestDTO> bookingRequestDTOs = new MainRequestDTO<>();
		List<BookingRequestDTO> successBookDtoList = new ArrayList<>();
		BookingRequestDTO successBookDto = new BookingRequestDTO();
		successBookDto.setRegistrationCenterId("1");
		successBookDto.setSlotFromTime(LocalTime.of(9,0).toString());
		successBookDto.setSlotToTime(LocalTime.of(9,15).toString());
		successBookDto.setRegDate(LocalDate.now().plusDays(30).toString());
		successBookDtoList.add(successBookDto);
		bookingRequestDTOs.setId("mosip.preregistration.booking.book");
		bookingRequestDTOs.setVersion("1.0");
		bookingRequestDTOs.setRequesttime(new Date());
		bookingRequestDTOs.setRequest(successBookDto);

		availableEntity.setAvailableKiosks(3);
		availableEntity.setRegcntrId("1");
		availableEntity.setRegDate(LocalDate.now().plusDays(30));
		availableEntity.setToTime(LocalTime.of(9,0));
		availableEntity.setFromTime(LocalTime.of(9,15));
		availableEntity.setCrBy("John Doe");
		availableEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntity.setDeleted(false);
		List<PreRegistartionStatusDTO> statusListrebook = new ArrayList<>();

		PreRegistartionStatusDTO preRegistartionStatus = new PreRegistartionStatusDTO();
		preRegistartionStatus.setStatusCode(StatusCodes.BOOKED.getCode());
		preRegistartionStatus.setPreRegistartionId(bookingPreId);
		preRegResponse.setResponse(preRegistartionStatus);
		statusListrebook.add(preRegistartionStatus);
		MainResponseDTO<PreRegistartionStatusDTO> preRegResponseRebook = new MainResponseDTO<PreRegistartionStatusDTO>();
		preRegResponseRebook.setErrors(null);
		preRegResponseRebook.setResponse(preRegistartionStatus);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);

		requestValidatorFlag = ValidationUtil.requestValidator(bookingRequestDTOs);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		Mockito.when(serviceUtil.mandatoryParameterCheck(Mockito.anyString(), Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.validateAppointmentDate(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.isKiosksAvailable(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.getDemographicStatus(Mockito.anyString())).thenReturn("Booked");
		Mockito.when(serviceUtil.getRegCenterMasterData()).thenReturn(centerList);
		// Update status
		RegistrationBookingEntity bookingEntity2 = new RegistrationBookingEntity();
		bookingEntity2.setBookingPK(new RegistrationBookingPK(DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity2.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity2.setLangCode("eng");
		bookingEntity2.setCrBy("John Doe");
		bookingEntity2.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntity2.setRegDate(LocalDate.now().plusDays(30));
		bookingEntity2.setSlotFromTime(LocalTime.of(9,0));
		bookingEntity2.setSlotToTime(LocalTime.of(9,15));

		@SuppressWarnings("rawtypes")
		MainResponseDTO mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(bookingEntity2);
		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenReturn(bookingEntity2);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		MainResponseDTO<BookingStatusDTO> response = service.bookAppointment(bookingRequestDTOs, bookingPreId);
		assertEquals("Appointment booked successfully", response.getResponse().getBookingMessage());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void successBookAppointmentPendingAppointment() {

		MainRequestDTO<BookingRequestDTO> bookingRequestDTOs = new MainRequestDTO<>();
		List<BookingRequestDTO> successBookDtoList = new ArrayList<>();
		BookingRequestDTO successBookDto = new BookingRequestDTO();
		successBookDto.setRegistrationCenterId("1");
		successBookDto.setSlotFromTime(LocalTime.of(9,0).toString());
		successBookDto.setSlotToTime(LocalTime.of(9,15).toString());
		successBookDto.setRegDate(LocalDate.now().plusDays(30).toString());
		successBookDtoList.add(successBookDto);
		bookingRequestDTOs.setId("mosip.preregistration.booking.book");
		bookingRequestDTOs.setVersion("1.0");
		bookingRequestDTOs.setRequesttime(new Date());
		bookingRequestDTOs.setRequest(successBookDto);

		availableEntity.setAvailableKiosks(3);
		availableEntity.setRegcntrId("1");
		availableEntity.setRegDate(LocalDate.now().plusDays(30));
		availableEntity.setToTime(LocalTime.of(9,0));
		availableEntity.setFromTime(LocalTime.of(9,15));
		availableEntity.setCrBy("John Doe");
		availableEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntity.setDeleted(false);
		List<PreRegistartionStatusDTO> statusListrebook = new ArrayList<>();

		PreRegistartionStatusDTO preRegistartionStatus = new PreRegistartionStatusDTO();
		preRegistartionStatus.setStatusCode(StatusCodes.PENDING_APPOINTMENT.getCode());
		preRegistartionStatus.setPreRegistartionId(bookingPreId);
		preRegResponse.setResponse(preRegistartionStatus);
		statusListrebook.add(preRegistartionStatus);
		MainResponseDTO<PreRegistartionStatusDTO> preRegResponseRebook = new MainResponseDTO<PreRegistartionStatusDTO>();
		preRegResponseRebook.setErrors(null);
		preRegResponseRebook.setResponse(preRegistartionStatus);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		Mockito.when(serviceUtil.mandatoryParameterCheck(Mockito.anyString(), Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.validateAppointmentDate(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.isKiosksAvailable(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.getDemographicStatus(Mockito.anyString())).thenReturn("Pending_Appointment");
		Mockito.when(serviceUtil.getRegCenterMasterData()).thenReturn(centerList);
		// Update status
		RegistrationBookingEntity bookingEntity2 = new RegistrationBookingEntity();
		bookingEntity2.setBookingPK(new RegistrationBookingPK(DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity2.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity2.setLangCode("eng");
		bookingEntity2.setCrBy("John Doe");
		bookingEntity2.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntity2.setRegDate(LocalDate.now().plusDays(30));
		bookingEntity2.setSlotFromTime(LocalTime.of(9,0));
		bookingEntity2.setSlotToTime(LocalTime.of(9,15));

		MainResponseDTO mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(bookingEntity2);
		ResponseEntity<MainResponseDTO<String>> resp2 = new ResponseEntity<>(mainResponseDTO, HttpStatus.OK);

		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenReturn(bookingEntity2);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);

		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<String>>() {
				}), Mockito.anyMap())).thenReturn(resp2);

		MainResponseDTO<BookingStatusDTO> response=service.bookAppointment(bookingRequestDTOs, bookingPreId);
		assertEquals("Appointment booked successfully", response.getResponse().getBookingMessage());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(expected = RecordNotFoundException.class)
	public void bookAppointmentFailureTest() {
		
		MainRequestDTO<BookingRequestDTO> bookingRequestDTOs = new MainRequestDTO<>();
		List<BookingRequestDTO> successBookDtoList = new ArrayList<>();
		BookingRequestDTO successBookDto = new BookingRequestDTO();
		successBookDto.setRegistrationCenterId("1");
		successBookDto.setSlotFromTime(LocalTime.of(9,0).toString());
		successBookDto.setSlotToTime(LocalTime.of(9,15).toString());
		successBookDto.setRegDate(LocalDate.now().plusDays(30).toString());
		successBookDtoList.add(successBookDto);
		bookingRequestDTOs.setId("mosip.preregistration.booking.book");
		bookingRequestDTOs.setVersion("1.0");
		bookingRequestDTOs.setRequesttime(new Date());
		bookingRequestDTOs.setRequest(successBookDto);

		availableEntity.setAvailableKiosks(3);
		availableEntity.setRegcntrId("1");
		availableEntity.setRegDate(LocalDate.now().plusDays(30));
		availableEntity.setToTime(LocalTime.of(9,0));
		availableEntity.setFromTime(LocalTime.of(9,15));
		availableEntity.setCrBy("John Doe");
		availableEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntity.setDeleted(false);
		List<PreRegistartionStatusDTO> statusListrebook = new ArrayList<>();

		PreRegistartionStatusDTO preRegistartionStatus = new PreRegistartionStatusDTO();
		preRegistartionStatus.setStatusCode(StatusCodes.PENDING_APPOINTMENT.getCode());
		preRegistartionStatus.setPreRegistartionId(bookingPreId);
		preRegResponse.setResponse(preRegistartionStatus);
		statusListrebook.add(preRegistartionStatus);
		MainResponseDTO<PreRegistartionStatusDTO> preRegResponseRebook = new MainResponseDTO<PreRegistartionStatusDTO>();
		preRegResponseRebook.setErrors(null);
		preRegResponseRebook.setResponse(preRegistartionStatus);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);
		Mockito.when(serviceUtil.validateAppointmentDate(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.mandatoryParameterCheck(Mockito.anyString(), Mockito.any())).thenReturn(true);

		// Update status
		RegistrationBookingEntity bookingEntity2 = new RegistrationBookingEntity();
		bookingEntity2.setBookingPK(new RegistrationBookingPK(DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity2.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity2.setLangCode("eng");
		bookingEntity2.setCrBy("John Doe");
		bookingEntity2.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntity2.setRegDate(LocalDate.now().plusDays(30));
		bookingEntity2.setSlotFromTime(LocalTime.of(9,0));
		bookingEntity2.setSlotToTime(LocalTime.of(9,15));

		MainResponseDTO mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(bookingEntity2);

		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenReturn(null);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		service.bookAppointment(bookingRequestDTOs, bookingPreId);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void successExpiredAppointment() {

		MainRequestDTO<BookingRequestDTO> bookingRequestDTOs = new MainRequestDTO<>();
		List<BookingRequestDTO> successBookDtoList = new ArrayList<>();
		BookingRequestDTO successBookDto = new BookingRequestDTO();
		successBookDto.setRegistrationCenterId("1");
		successBookDto.setSlotFromTime(LocalTime.of(9,0).toString());
		successBookDto.setSlotToTime(LocalTime.of(9,15).toString());
		successBookDto.setRegDate(LocalDate.now().plusDays(30).toString());
		successBookDtoList.add(successBookDto);
		bookingRequestDTOs.setId("mosip.preregistration.booking.book");
		bookingRequestDTOs.setVersion("1.0");
		bookingRequestDTOs.setRequesttime(new Date());
		bookingRequestDTOs.setRequest(successBookDto);

		availableEntity.setAvailableKiosks(3);
		availableEntity.setRegcntrId("1");
		availableEntity.setRegDate(LocalDate.now().plusDays(30));
		availableEntity.setToTime(LocalTime.of(9,0));
		availableEntity.setFromTime(LocalTime.of(9,15));
		availableEntity.setCrBy("John Doe");
		availableEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntity.setDeleted(false);
		List<PreRegistartionStatusDTO> statusListrebook = new ArrayList<>();

		PreRegistartionStatusDTO preRegistartionStatus = new PreRegistartionStatusDTO();
		preRegistartionStatus.setStatusCode(StatusCodes.EXPIRED.getCode());
		preRegistartionStatus.setPreRegistartionId(bookingPreId);
		preRegResponse.setResponse(preRegistartionStatus);
		statusListrebook.add(preRegistartionStatus);
		MainResponseDTO<PreRegistartionStatusDTO> preRegResponseRebook = new MainResponseDTO<PreRegistartionStatusDTO>();
		preRegResponseRebook.setErrors(null);
		preRegResponseRebook.setResponse(preRegistartionStatus);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);

		requestValidatorFlag = ValidationUtil.requestValidator(bookingRequestDTOs);

		RegistrationCenterDto centerDto = new RegistrationCenterDto();
		List<RegistrationCenterDto> centerList = new ArrayList<>();
		centerDto.setId("10001");
		centerDto.setLangCode("eng");
		centerDto.setCenterStartTime(startTime);
		centerDto.setCenterEndTime(endTime);
		centerDto.setPerKioskProcessTime(perKioskTime);
		centerDto.setLunchStartTime(LunchStartTime);
		centerDto.setLunchEndTime(LunchEndTime);
		centerDto.setNumberOfKiosks((short) 4);
		centerList.add(centerDto);

		// Update status
		RegistrationBookingEntity bookingEntity2 = new RegistrationBookingEntity();
		bookingEntity2.setBookingPK(new RegistrationBookingPK(DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity2.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity2.setLangCode("12L");
		bookingEntity2.setCrBy("John Doe");
		bookingEntity2.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntity2.setRegDate(LocalDate.now().plusDays(30));
		bookingEntity2.setSlotFromTime(LocalTime.of(9,0));
		bookingEntity2.setSlotToTime(LocalTime.of(9,15));

		MainResponseDTO mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(bookingEntity2);

		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenReturn(bookingEntity2);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		Mockito.when(serviceUtil.mandatoryParameterCheck(Mockito.anyString(), Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.validateAppointmentDate(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.isKiosksAvailable(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.getDemographicStatus(Mockito.anyString())).thenReturn("Booked");
		Mockito.when(serviceUtil.getRegCenterMasterData()).thenReturn(centerList);

		MainResponseDTO<BookingStatusDTO> response = service.bookAppointment(bookingRequestDTOs, bookingPreId);

		assertEquals("Appointment booked successfully", response.getResponse().getBookingMessage());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void successMultiBookAppointment() {

		multiBookingListDto.add(multiBookingRequestDto1);
		multiBookingListDto.add(multiBookingRequestDto2);

		MultiBookingRequest multiBookingRequest = new MultiBookingRequest();
		multiBookingRequest.setBookingRequest(multiBookingListDto);
		responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		BookingStatusDTO bookingStatusDTO1 = new BookingStatusDTO();
		bookingStatusDTO1.setBookingMessage("Appointment booked successfully");

		BookingStatusDTO bookingStatusDTO2 = new BookingStatusDTO();
		bookingStatusDTO2.setBookingMessage("Appointment booked successfully");

		List<BookingStatusDTO> bookingStatusDTOs = new ArrayList<>();
		bookingStatusDTOs.add(bookingStatusDTO1);
		bookingStatusDTOs.add(bookingStatusDTO2);

		MainRequestDTO<MultiBookingRequest> bookingRequestDTOs = new MainRequestDTO<>();
		MultiBookingRequest bookingRequest = new MultiBookingRequest();
		List<MultiBookingRequestDTO> successBookDtoList = new ArrayList<>();
		MultiBookingRequestDTO successBookDto1 = new MultiBookingRequestDTO();
		successBookDto1.setPreRegistrationId("23587986034785");
		successBookDto1.setRegistrationCenterId("1");
		successBookDto1.setSlotFromTime(LocalTime.of(9,0).toString());
		successBookDto1.setSlotToTime(LocalTime.of(9, 15).toString());
		successBookDto1.setRegDate(LocalDate.now().plusDays(30).toString());
		successBookDtoList.add(successBookDto1);
		bookingRequest.setBookingRequest(successBookDtoList);
		bookingRequestDTOs.setRequest(bookingRequest);
		bookingRequestDTOs.setId("mosip.preregistration.booking.book");
		bookingRequestDTOs.setVersion("1.0");
		bookingRequestDTOs.setRequesttime(new Date());

		availableEntity.setAvailableKiosks(3);
		availableEntity.setRegcntrId("1");
		availableEntity.setRegDate(LocalDate.now().plusDays(30));
		availableEntity.setToTime(LocalTime.of(9,0));
		availableEntity.setFromTime(LocalTime.of(9,15));
		availableEntity.setCrBy("John Doe");
		availableEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntity.setDeleted(false);
		List<PreRegistartionStatusDTO> statusListrebook = new ArrayList<>();

		PreRegistartionStatusDTO preRegistartionStatus = new PreRegistartionStatusDTO();
		preRegistartionStatus.setStatusCode(StatusCodes.BOOKED.getCode());
		preRegistartionStatus.setPreRegistartionId(bookingPreId);
		preRegResponse.setResponse(preRegistartionStatus);
		statusListrebook.add(preRegistartionStatus);
		MainResponseDTO<PreRegistartionStatusDTO> preRegResponseRebook = new MainResponseDTO<PreRegistartionStatusDTO>();
		preRegResponseRebook.setErrors(null);
		preRegResponseRebook.setResponse(preRegistartionStatus);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);

		requestValidatorFlag = ValidationUtil.requestValidator(bookingRequestDTOs);

		// Update status
		RegistrationBookingEntity bookingEntity2 = new RegistrationBookingEntity();
		bookingEntity2.setBookingPK(new RegistrationBookingPK(DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity2.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity2.setLangCode("12L");
		bookingEntity2.setCrBy("John Doe");
		bookingEntity2.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntity2.setRegDate(LocalDate.now().plusDays(30));
		bookingEntity2.setSlotFromTime(LocalTime.of(9,0));
		bookingEntity2.setSlotToTime(LocalTime.of(9,15));

		RegistrationCenterDto centerDto = new RegistrationCenterDto();
		List<RegistrationCenterDto> centerList = new ArrayList<>();
		centerDto.setId("10001");
		centerDto.setLangCode("eng");
		centerDto.setCenterStartTime(startTime);
		centerDto.setCenterEndTime(endTime);
		centerDto.setPerKioskProcessTime(perKioskTime);
		centerDto.setLunchStartTime(LunchStartTime);
		centerDto.setLunchEndTime(LunchEndTime);
		centerDto.setNumberOfKiosks((short) 4);
		centerList.add(centerDto);

		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenReturn(bookingEntity2);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		Mockito.when(serviceUtil.mandatoryParameterCheck(Mockito.anyString(), Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.validateAppointmentDate(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.isKiosksAvailable(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.getDemographicStatus(Mockito.anyString())).thenReturn("Booked");
		Mockito.when(serviceUtil.getRegCenterMasterData()).thenReturn(centerList);

		MainResponseDTO mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(bookingEntity2);
		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenReturn(bookingEntity2);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);

		MainResponseDTO<BookingStatus> response = service.bookMultiAppointment(bookingRequestDTOs);
		assertEquals("Appointment booked successfully",
				response.getResponse().getBookingStatusResponse().get(0).getBookingMessage());
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void successExpiredMultiBookAppointment() {
		multiBookingListDto.add(multiBookingRequestDto1);
		multiBookingListDto.add(multiBookingRequestDto2);

		MultiBookingRequest multiBookingRequest = new MultiBookingRequest();
		multiBookingRequest.setBookingRequest(multiBookingListDto);
		responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		BookingStatusDTO bookingStatusDTO1 = new BookingStatusDTO();
		bookingStatusDTO1.setBookingMessage("Appointment booked successfully");

		BookingStatusDTO bookingStatusDTO2 = new BookingStatusDTO();
		bookingStatusDTO2.setBookingMessage("Appointment booked successfully");
		RegistrationCenterDto centerDto = new RegistrationCenterDto();
		List<RegistrationCenterDto> centerList = new ArrayList<>();
		centerDto.setId("10001");
		centerDto.setLangCode("eng");
		centerDto.setCenterStartTime(startTime);
		centerDto.setCenterEndTime(endTime);
		centerDto.setPerKioskProcessTime(perKioskTime);
		centerDto.setLunchStartTime(LunchStartTime);
		centerDto.setLunchEndTime(LunchEndTime);
		centerDto.setNumberOfKiosks((short) 4);
		centerList.add(centerDto);

		List<BookingStatusDTO> bookingStatusDTOs = new ArrayList<>();
		bookingStatusDTOs.add(bookingStatusDTO1);
		bookingStatusDTOs.add(bookingStatusDTO2);

		MainRequestDTO<MultiBookingRequest> bookingRequestDTOs = new MainRequestDTO<>();
		MultiBookingRequest bookingRequest = new MultiBookingRequest();
		List<MultiBookingRequestDTO> successBookDtoList = new ArrayList<>();
		MultiBookingRequestDTO successBookDto1 = new MultiBookingRequestDTO();
		successBookDto1.setPreRegistrationId("23587986034785");
		successBookDto1.setRegistrationCenterId("1");
		successBookDto1.setSlotFromTime(LocalTime.of(9, 0).toString());
		successBookDto1.setSlotToTime(LocalTime.of(9, 15).toString());
		successBookDto1.setRegDate(LocalDate.now().plusDays(30).toString());
		successBookDtoList.add(successBookDto1);
		bookingRequest.setBookingRequest(successBookDtoList);
		bookingRequestDTOs.setRequest(bookingRequest);
		bookingRequestDTOs.setId("mosip.preregistration.booking.book");
		bookingRequestDTOs.setVersion("1.0");
		bookingRequestDTOs.setRequesttime(new Date());

		availableEntity.setAvailableKiosks(3);
		availableEntity.setRegcntrId("1");
		availableEntity.setRegDate(LocalDate.now().plusDays(30));
		availableEntity.setToTime(LocalTime.of(9,0));
		availableEntity.setFromTime(LocalTime.of(9,15));
		availableEntity.setCrBy("John Doe");
		availableEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntity.setDeleted(false);
		List<PreRegistartionStatusDTO> statusListrebook = new ArrayList<>();

		PreRegistartionStatusDTO preRegistartionStatus = new PreRegistartionStatusDTO();
		preRegistartionStatus.setStatusCode(StatusCodes.EXPIRED.getCode());
		preRegistartionStatus.setPreRegistartionId(bookingPreId);
		preRegResponse.setResponse(preRegistartionStatus);
		statusListrebook.add(preRegistartionStatus);
		MainResponseDTO<PreRegistartionStatusDTO> preRegResponseRebook = new MainResponseDTO<PreRegistartionStatusDTO>();
		preRegResponseRebook.setErrors(null);
		preRegResponseRebook.setResponse(preRegistartionStatus);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);

		requestValidatorFlag = ValidationUtil.requestValidator(bookingRequestDTOs);
		Mockito.when(demographicServiceIntf.getApplicationStatus("23587986034785", null))
				.thenReturn(preRegResponseRebook);

		// Update status
		RegistrationBookingEntity bookingEntity2 = new RegistrationBookingEntity();
		bookingEntity2.setBookingPK(new RegistrationBookingPK(DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity2.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity2.setLangCode("12L");
		bookingEntity2.setCrBy("987654321");
		bookingEntity2.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntity2.setRegDate(LocalDate.now().plusDays(30));
		bookingEntity2.setSlotFromTime(LocalTime.of(9,0));
		bookingEntity2.setSlotToTime(LocalTime.of(9,15));

		MainResponseDTO mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(bookingEntity2);
		Mockito.when(serviceUtil.getDemographicStatus("23587986034785")).thenReturn("Pending_Appointment");
		BookingRequestDTO bookingreq = new BookingRequestDTO();
		bookingreq.setRegDate(LocalDate.now().plusDays(30).toString());
		Mockito.when(serviceUtil.mandatoryParameterCheck(Mockito.anyString(), Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.validateAppointmentDate(Mockito.any())).thenReturn(true);
		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenReturn(bookingEntity2);
		Mockito.when(serviceUtil.isKiosksAvailable(Mockito.any())).thenReturn(true);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		Mockito.when(serviceUtil.getRegCenterMasterData()).thenReturn(centerList);
		MainResponseDTO<BookingStatus> response = service.bookMultiAppointment(bookingRequestDTOs);
		System.out.println(response.getResponse().getBookingStatusResponse().get(0).getBookingMessage());
		assertEquals("Appointment booked successfully",
				response.getResponse().getBookingStatusResponse().get(0).getBookingMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void failureMultiBookAppointment() {
		multiBookingListDto.add(multiBookingRequestDto1);
		multiBookingListDto.add(multiBookingRequestDto2);

		MultiBookingRequest multiBookingRequest = new MultiBookingRequest();
		multiBookingRequest.setBookingRequest(multiBookingListDto);
		responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		BookingStatusDTO bookingStatusDTO1 = new BookingStatusDTO();
		bookingStatusDTO1.setBookingMessage("Appointment booked successfully");

		BookingStatusDTO bookingStatusDTO2 = new BookingStatusDTO();
		bookingStatusDTO2.setBookingMessage("Appointment booked successfully");

		List<BookingStatusDTO> bookingStatusDTOs = new ArrayList<>();
		bookingStatusDTOs.add(bookingStatusDTO1);
		bookingStatusDTOs.add(bookingStatusDTO2);

		MainRequestDTO<MultiBookingRequest> bookingRequestDTOs = new MainRequestDTO<>();
		MultiBookingRequest bookingRequest = new MultiBookingRequest();
		List<MultiBookingRequestDTO> successBookDtoList = new ArrayList<>();
		MultiBookingRequestDTO successBookDto1 = new MultiBookingRequestDTO();
		successBookDto1.setPreRegistrationId("23587986034785");
		successBookDto1.setRegistrationCenterId("1");
		successBookDto1.setSlotFromTime(LocalTime.of(9, 0).toString());
		successBookDto1.setSlotToTime(LocalTime.of(9, 15).toString());
		successBookDto1.setRegDate(LocalDate.now().plusDays(30).toString());
		successBookDtoList.add(successBookDto1);
		bookingRequest.setBookingRequest(successBookDtoList);
		bookingRequestDTOs.setRequest(bookingRequest);
		bookingRequestDTOs.setId("mosip.preregistration.booking.book");
		bookingRequestDTOs.setVersion("1.0");
		bookingRequestDTOs.setRequesttime(new Date());

		availableEntity.setAvailableKiosks(3);
		availableEntity.setRegcntrId("1");
		availableEntity.setRegDate(LocalDate.now().plusDays(30));
		availableEntity.setToTime(LocalTime.of(9, 0));
		availableEntity.setFromTime(LocalTime.of(9, 15));
		availableEntity.setCrBy("JOhn Doe");
		availableEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntity.setDeleted(false);
		List<PreRegistartionStatusDTO> statusListrebook = new ArrayList<>();

		PreRegistartionStatusDTO preRegistartionStatus = new PreRegistartionStatusDTO();
		preRegistartionStatus.setStatusCode(StatusCodes.BOOKED.getCode());
		preRegistartionStatus.setPreRegistartionId(bookingPreId);
		preRegResponse.setResponse(preRegistartionStatus);
		statusListrebook.add(preRegistartionStatus);
		MainResponseDTO<PreRegistartionStatusDTO> preRegResponseRebook = new MainResponseDTO<PreRegistartionStatusDTO>();
		preRegResponseRebook.setErrors(null);
		preRegResponseRebook.setResponse(preRegistartionStatus);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);

		requestValidatorFlag = ValidationUtil.requestValidator(bookingRequestDTOs);
		ResponseEntity<MainResponseDTO<PreRegistartionStatusDTO>> respEntity = new ResponseEntity<>(preRegResponse,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),

				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<PreRegistartionStatusDTO>>() {
				}), Mockito.anyMap())).thenReturn(respEntity);

		// Update status
		RegistrationBookingEntity bookingEntity2 = new RegistrationBookingEntity();
		bookingEntity2.setBookingPK(new RegistrationBookingPK(DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity2.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity2.setLangCode("12L");
		bookingEntity2.setCrBy("987654321");
		bookingEntity2.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntity2.setRegDate(LocalDate.now().plusDays(30));
		bookingEntity2.setSlotFromTime(LocalTime.of(9,0));
		bookingEntity2.setSlotToTime(LocalTime.of(9, 15));

		MainResponseDTO mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(bookingEntity2);
		ResponseEntity<MainResponseDTO<String>> resp2 = new ResponseEntity<>(mainResponseDTO, HttpStatus.OK);

		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenReturn(null);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);

		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<String>>() {
				}), Mockito.anyMap())).thenReturn(resp2);

		MainResponseDTO<BookingStatus> response = service.bookMultiAppointment(bookingRequestDTOs);
		// assertEquals("Appointment booked successfully",
		// response.getResponse().getBookingStatusResponse().get(0).getBookingMessage());
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void cancelAppointmentSuccessTest() throws java.text.ParseException {

		PreRegistartionStatusDTO bookedStatusDTO = new PreRegistartionStatusDTO();
		bookedStatusDTO.setStatusCode(StatusCodes.BOOKED.getCode());
		bookedStatusDTO.setPreRegistartionId("23587986034785");
		preRegResponse.setResponse(bookedStatusDTO);

		requestValidatorFlag = ValidationUtil.requestValidator(cancelRequestdto);

		MainResponseDTO<PreRegistartionStatusDTO> getApplicationStatus = new MainResponseDTO<>();
		PreRegistartionStatusDTO preRegistartionStatus = new PreRegistartionStatusDTO();
		preRegistartionStatus.setStatusCode(StatusCodes.BOOKED.getCode());
		preRegistartionStatus.setPreRegistartionId("12345678909876");
		getApplicationStatus.setResponse(preRegistartionStatus);
		Mockito.when(demographicServiceIntf.getApplicationStatus(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(getApplicationStatus);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);

		Mockito.when(bookingDAO.findByPreRegistrationId(Mockito.any())).thenReturn(bookingEntity);

		Mockito.when(bookingDAO.deleteByPreRegistrationId(Mockito.anyString())).thenReturn(1);
		MainResponseDTO mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(bookingEntity);
		BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
		bookingRequestDTO.setRegistrationCenterId("1");
		bookingRequestDTO.setSlotFromTime(LocalTime.of(9, 0).toString());
		bookingRequestDTO.setSlotToTime(LocalTime.of(9, 15).toString());
		bookingRequestDTO.setRegDate(LocalDate.now().plusDays(30).toString());
		Mockito.when(serviceUtil.mandatoryParameterCheckforCancel("23587986034785")).thenReturn(true);
		Mockito.when(serviceUtil.getDemographicStatusForCancel("23587986034785")).thenReturn(true);
		Mockito.when(demographicServiceIntf.updatePreRegistrationStatus("23587986034785", "Pending_Appointment", null))
				.thenReturn(mainResponseDTO);
		availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() + 1);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		MainResponseDTO<CancelBookingResponseDTO> responseDto = service.cancelAppointment("23587986034785");
		assertEquals("Appointment for the selected application has been successfully cancelled",
				responseDto.getResponse().getMessage());

	}

	@Test
	public void getAppointmentDetailsTest() {
		MainRequestDTO<BookingRequestDTO> reBookingMainDto = new MainRequestDTO<>();
		BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
		bookingRequestDTO.setRegistrationCenterId("1");
		bookingRequestDTO.setSlotFromTime(LocalTime.of(9, 0).toString());
		bookingRequestDTO.setSlotToTime(LocalTime.of(9, 15).toString());
		bookingRequestDTO.setRegDate(LocalDate.now().plusDays(30).toString());
		
		List<BookingRequestDTO> rebookingReqList = new ArrayList<>();
		rebookingReqList.add(bookingRequestDTO);

		reBookingMainDto.setId("mosip.pre-registration.booking.book");
		reBookingMainDto.setVersion("1.0");
		reBookingMainDto.setRequesttime(new Date());
		reBookingMainDto.setRequest(bookingRequestDTO);

		MainResponseDTO<List<BookingStatusDTO>> responseDTO = new MainResponseDTO<>();
		BookingStatusDTO bookingStatusDTO = new BookingStatusDTO();
		bookingStatusDTO.setBookingMessage("Appointment booked successfully");

		List<BookingStatusDTO> respList = new ArrayList<>();
		respList.add(bookingStatusDTO);
		responseDTO.setResponse(respList);
		responseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
		List<PreRegistartionStatusDTO> statusListrebook = new ArrayList<>();
		PreRegistartionStatusDTO preRegistartionStatus = new PreRegistartionStatusDTO();
		preRegistartionStatus.setStatusCode(StatusCodes.BOOKED.getCode());
		preRegistartionStatus.setPreRegistartionId("12345678909876");
		statusListrebook.add(preRegistartionStatus);
		MainResponseDTO<PreRegistartionStatusDTO> preRegResponseRebook = new MainResponseDTO<PreRegistartionStatusDTO>();
		preRegResponseRebook.setErrors(null);
		preRegResponseRebook.setResponse(preRegistartionStatus);
		RegistrationBookingEntity bookingEntityRebook = new RegistrationBookingEntity();
		bookingEntityRebook.setBookingPK(new RegistrationBookingPK(DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntityRebook.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntityRebook.setLangCode("eng");
		bookingEntityRebook.setCrBy("John Doe");
		bookingEntityRebook.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntityRebook.setRegDate(LocalDate.now().plusDays(30));
		bookingEntityRebook.setSlotFromTime(LocalTime.of(9, 0));
		bookingEntityRebook.setSlotToTime(LocalTime.of(9, 15));
		Mockito.when(bookingDAO.findByPreRegistrationId("12345678909876")).thenReturn(bookingEntityRebook);
		ResponseEntity<MainResponseDTO<PreRegistartionStatusDTO>> respEntity = new ResponseEntity<>(
				preRegResponseRebook, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),

				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<PreRegistartionStatusDTO>>() {
				}), Mockito.anyMap())).thenReturn(respEntity);

		MainResponseDTO<BookingRegistrationDTO> responseDto = service.getAppointmentDetails("12345678909876");

		assertEquals("1", responseDto.getResponse().getRegistrationCenterId());
	}

	@SuppressWarnings("unchecked")
	@Test(expected = BookingDataNotFoundException.class)
	public void getAppointmentDetailsTestFail() {

		PreRegistartionStatusDTO bookedStatusDTO = new PreRegistartionStatusDTO();
		bookedStatusDTO.setStatusCode(StatusCodes.BOOKED.getCode());
		bookedStatusDTO.setPreRegistartionId("23587986034785");
		preRegResponse.setResponse(bookedStatusDTO);

		BookingDataNotFoundException exception = new BookingDataNotFoundException(
				ErrorCodes.PRG_BOOK_RCI_013.toString(), ErrorMessages.BOOKING_DATA_NOT_FOUND.toString());
		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenThrow(exception);
		ResponseEntity<MainResponseDTO<PreRegistartionStatusDTO>> respEntity = new ResponseEntity<>(preRegResponse,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),

				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<PreRegistartionStatusDTO>>() {
				}), Mockito.anyMap())).thenReturn(respEntity);

		service.getAppointmentDetails("23587986034785");

	}

	@SuppressWarnings("unchecked")
	@Test(expected = TableNotAccessibleException.class)
	public void getAppointmentDetailsFailureTest() {
		PreRegistartionStatusDTO bookedStatusDTO = new PreRegistartionStatusDTO();
		bookedStatusDTO.setStatusCode(StatusCodes.BOOKED.getCode());
		bookedStatusDTO.setPreRegistartionId("23587986034785");
		preRegResponse.setResponse(bookedStatusDTO);

		ResponseEntity<MainResponseDTO<PreRegistartionStatusDTO>> respEntity = new ResponseEntity<>(preRegResponse,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),

				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<PreRegistartionStatusDTO>>() {
				}), Mockito.anyMap())).thenReturn(respEntity);

		Mockito.when(bookingDAO.findByPreRegistrationId(Mockito.anyString()))
				.thenThrow(new DataAccessLayerException("", "", new Throwable()));
		service.getAppointmentDetails("23587986034785");
	}

	@Test
	public void cancelAppointmentBatch() {

		MainRequestDTO<BookingRequestDTO> bookingRequestDTOs = new MainRequestDTO<>();
		List<BookingRequestDTO> successBookDtoList = new ArrayList<>();
		BookingRequestDTO successBookDto = new BookingRequestDTO();
		successBookDto.setRegistrationCenterId("1");
		successBookDto.setSlotFromTime(LocalTime.of(9, 0).toString());
		successBookDto.setSlotToTime(LocalTime.of(9, 15).toString());
		successBookDto.setRegDate(LocalDate.now().plusDays(30).toString());
		successBookDtoList.add(successBookDto);
		bookingRequestDTOs.setId("mosip.pre-registration.booking.book");
		bookingRequestDTOs.setVersion("1.0");
		bookingRequestDTOs.setRequesttime(new Date());
		bookingRequestDTOs.setRequest(successBookDto);
		RegistrationCenterDto centerDto = new RegistrationCenterDto();
		List<RegistrationCenterDto> centerList = new ArrayList<>();
		centerDto.setId("10001");
		centerDto.setLangCode("eng");
		centerDto.setCenterStartTime(startTime);
		centerDto.setCenterEndTime(endTime);
		centerDto.setPerKioskProcessTime(perKioskTime);
		centerDto.setLunchStartTime(LunchStartTime);
		centerDto.setLunchEndTime(LunchEndTime);
		centerDto.setNumberOfKiosks((short) 4);
		centerList.add(centerDto);
		RegistrationBookingEntity bookingEntity = new RegistrationBookingEntity();
		bookingEntity.setBookingPK(new RegistrationBookingPK(DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity.setLangCode("eng");
		bookingEntity.setCrBy("John Doe");
		bookingEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenReturn(bookingEntity);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		Mockito.when(serviceUtil.mandatoryParameterCheck(Mockito.anyString(), Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.validateAppointmentDate(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.isKiosksAvailable(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.mandatoryParameterCheckforCancel(Mockito.anyString())).thenReturn(true);
		Mockito.when(serviceUtil.getDemographicStatusForCancel(Mockito.anyString())).thenReturn(true);
		Mockito.when(serviceUtil.getDemographicStatus(Mockito.anyString())).thenReturn("Expired");
		Mockito.when(serviceUtil.getRegCenterMasterData()).thenReturn(centerList);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		Mockito.when(bookingDAO.saveRegistrationEntityForBooking(Mockito.any())).thenReturn(bookingEntity);

		MainResponseDTO<CancelBookingResponseDTO> response = service.cancelAppointmentBatch("23587986034785");
	}

	@SuppressWarnings("unchecked")
	@Test(expected = TimeSpanException.class)
	public void cancelTimeSpanFailureTest() throws java.text.ParseException {

		TimeSpanException ex = new TimeSpanException(ErrorCodes.PRG_BOOK_RCI_026.getCode(),
				ErrorMessages.CANCEL_BOOKING_CANNOT_BE_DONE.getMessage() + " " + 24 + "hours");

		List<RegistrationBookingEntity> registrationEntityList = new ArrayList<>();
		RegistrationBookingEntity bookingEntity = new RegistrationBookingEntity();
		bookingEntity.setBookingPK(new RegistrationBookingPK(DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity.setLangCode("eng");
		bookingEntity.setCrBy("John Doe");
		bookingEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
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
		cancelbookingDto2.setRegDate(LocalDate.now().toString());
		cancelbookingDto2.setRegistrationCenterId("1");
		cancelbookingDto2.setSlotFromTime(LocalTime.now().toString());
		cancelbookingDto2.setSlotToTime(LocalTime.of(9, 13).toString());

		PreRegistartionStatusDTO bookedStatusDTO = new PreRegistartionStatusDTO();
		bookedStatusDTO.setStatusCode(StatusCodes.BOOKED.getCode());
		bookedStatusDTO.setPreRegistartionId("23587986034785");
		preRegResponse.setResponse(bookedStatusDTO);

		requestValidatorFlag = ValidationUtil.requestValidator(cancelRequestdto);
		RegistrationCenterDto centerDto = new RegistrationCenterDto();
		List<RegistrationCenterDto> centerList = new ArrayList<>();
		centerDto.setId("10001");
		centerDto.setLangCode("eng");
		centerDto.setCenterStartTime(startTime);
		centerDto.setCenterEndTime(endTime);
		centerDto.setPerKioskProcessTime(perKioskTime);
		centerDto.setLunchStartTime(LunchStartTime);
		centerDto.setLunchEndTime(LunchEndTime);
		centerDto.setNumberOfKiosks((short) 4);
		centerList.add(centerDto);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		Mockito.when(serviceUtil.mandatoryParameterCheck(Mockito.anyString(), Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.mandatoryParameterCheckforCancel(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.getDemographicStatusForCancel(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.validateAppointmentDate(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.isKiosksAvailable(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.getDemographicStatus(Mockito.anyString())).thenReturn("Booked");
		Mockito.when(serviceUtil.getRegCenterMasterData()).thenReturn(centerList);
		Mockito.when(serviceUtil.timeSpanCheckForCancle(Mockito.any())).thenThrow(ex);

		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenReturn(bookingEntity);

		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);
		service.cancelBooking("23587986034785", false);
	}

	@Test
	public void deleteBooking() {

		List<DeleteBookingDTO> deleteList = new ArrayList<>();
		DeleteBookingDTO deleteDto = new DeleteBookingDTO();
		List<RegistrationBookingEntity> registrationEntityList = new ArrayList<>();
		RegistrationBookingEntity bookingEntity = new RegistrationBookingEntity();
		bookingEntity.setBookingPK(new RegistrationBookingPK(DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity.setLangCode("eng");
		bookingEntity.setCrBy("John Doe");
		bookingEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntity.setRegDate(LocalDate.parse(oldBooking.getRegDate()));
		bookingEntity.setSlotFromTime(LocalTime.parse(oldBooking.getSlotFromTime()));
		bookingEntity.setSlotToTime(LocalTime.parse(oldBooking.getSlotToTime()));
		preRegistrationEntity.setPreRegistrationId("23587986034785");
		bookingEntity.setDemographicEntity(preRegistrationEntity);
		registrationEntityList.add(bookingEntity);

		Mockito.when(bookingDAO.findByPreRegistrationId(Mockito.anyString())).thenReturn(bookingEntity);
		deleteDto.setDeletedBy("John Doe");
		deleteDto.setDeletedDateTime(new Date(System.currentTimeMillis()));
		deleteDto.setPreRegistrationId("23587986034785");
		deleteList.add(deleteDto);
		Mockito.when(bookingDAO.deleteByPreRegistrationId(Mockito.anyString())).thenReturn(1);

		MainResponseDTO<DeleteBookingDTO> response = new MainResponseDTO<>();
		response.setErrors(null);
		response.setResponse(deleteDto);
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.anyString())).thenReturn(availableEntity);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);

		assertEquals(response.getResponse().getPreRegistrationId(),
				service.deleteBooking("23587986034785").getResponse().getPreRegistrationId());

	}

	@Test
	public void deleteBookingFailTest() {

		List<DeleteBookingDTO> deleteList = new ArrayList<>();
		DeleteBookingDTO deleteDto = new DeleteBookingDTO();
		List<RegistrationBookingEntity> registrationEntityList = new ArrayList<>();
		RegistrationBookingEntity bookingEntity = new RegistrationBookingEntity();
		bookingEntity.setBookingPK(new RegistrationBookingPK(DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity.setLangCode("eng");
		bookingEntity.setCrBy("John Doe");
		bookingEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntity.setRegDate(LocalDate.parse(oldBooking.getRegDate()));
		bookingEntity.setSlotFromTime(LocalTime.parse(oldBooking.getSlotFromTime()));
		bookingEntity.setSlotToTime(LocalTime.parse(oldBooking.getSlotToTime()));
		registrationEntityList.add(bookingEntity);

		Mockito.when(bookingDAO.findByPreregistrationId(Mockito.anyString())).thenReturn(null);
		deleteDto.setDeletedBy("John Doe");
		deleteDto.setDeletedDateTime(new Date(System.currentTimeMillis()));
		deleteDto.setPreRegistrationId("12345678909876");
		deleteList.add(deleteDto);
		Mockito.when(bookingDAO.deleteByPreRegistrationId(Mockito.anyString())).thenReturn(0);

		MainResponseDTO<DeleteBookingDTO> response = new MainResponseDTO<>();
		response.setErrors(null);
		response.setResponse(deleteDto);
		response.setResponsetime(serviceUtil.getCurrentResponseTime());

		assertEquals("1.0", service.deleteBooking("12345678909876").getVersion());

	}

	@Test
	public void getBookedPreIdsByDateTest() {
		MainResponseDTO<String> response = new MainResponseDTO<>();
		List<String> preIds = new ArrayList<>();
		List<String> details = new ArrayList<>();
		details.add("98746563542672");

		preIds.add("98746563542672");
		response.setResponse("98746563542672");
		response.setVersion("1.0");

		LocalDate fromDate = LocalDate.now();

		LocalDate toDate = LocalDate.now().plusDays(30);

		Mockito.when(bookingDAO.findByBookingDateBetweenAndRegCenterId(fromDate, toDate, "10001")).thenReturn(details);

		MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO> actualRes = service
				.getBookedPreRegistrationByDate(fromDate.toString(), toDate.toString(), "10001");
		assertEquals(actualRes.getVersion(), response.getVersion());

	}

	@Test(expected = BookingDataNotFoundException.class)
	public void getApplicationByDateFailureTest() {
		List<String> preids = new ArrayList<>();
		preids.add("");
		LocalDate fromDate = LocalDate.now();
		LocalDate toDate = LocalDate.now().plusDays(30);
		Mockito.when(
				serviceUtil.validateFromDateAndToDate(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(true);
		Mockito.when(bookingDAO.findByBookingDateBetweenAndRegCenterId(fromDate, toDate, "10001"))
				.thenThrow(new BookingDataNotFoundException("", "", new Throwable()));
		service.getBookedPreRegistrationByDate(fromDate.toString(), toDate.toString(), "10001");

	}

	@Test(expected = AvailablityNotFoundException.class)
	public void checkSlotAvailabilityTest() {
		RegistrationCenterDto centerDto = new RegistrationCenterDto();
		List<AvailibityEntity> availablityList = new ArrayList<>();
		AvailibityEntity entity = new AvailibityEntity();
		entity.setAvailableKiosks(3);
		entity.setRegcntrId("10001");
		availablityList.add(entity);
		List<RegistrationCenterDto> centerList = new ArrayList<>();
		centerDto.setId("10001");
		centerDto.setLangCode("eng");
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
		holiday.setHolidayDate(LocalDate.now().plusDays(30).toString());
		holidayList.add(holiday);
		CenholidayDto.setHolidays(holidayList);
		List<String> regCenterList = new ArrayList<>();
		regCenterList.add("10001");
		List<LocalDate> insertedDate = new ArrayList<>();
		insertedDate.add(LocalDate.now().plusDays(20));

		ResponseWrapper<RegistrationCenterResponseDto> resp = new ResponseWrapper<>();
		resp.setResponse(regCenDto);

		BookingRequestDTO successBookDto = new BookingRequestDTO();
		successBookDto.setRegistrationCenterId("10001");
		successBookDto.setSlotFromTime(LocalTime.of(9, 0).toString());
		successBookDto.setSlotToTime(LocalTime.of(9, 15).toString());
		successBookDto.setRegDate(LocalDate.now().plusDays(30).toString());

		AvailibityEntity availableEntityNull = new AvailibityEntity();
		availableEntityNull.setAvailableKiosks(0);
		availableEntityNull.setRegcntrId("1");
		availableEntityNull.setRegDate(LocalDate.now().plusDays(20));
		availableEntityNull.setCrBy("John Doe");
		availableEntityNull.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntityNull.setDeleted(false);

		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		Mockito.when(serviceUtil.mandatoryParameterCheck(Mockito.anyString(), Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.validateAppointmentDate(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.isKiosksAvailable(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.getDemographicStatus(Mockito.anyString())).thenReturn("Booked");
		Mockito.when(serviceUtil.getRegCenterMasterData()).thenReturn(centerList);
		Mockito.when(bookingDAO.findRegCenter(Mockito.any())).thenReturn(regCenterList);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.anyString())).thenReturn(availableEntityNull);
		service.checkSlotAvailability(successBookDto);
	}

	@Test
	public void deleteOldBookingTest() {
		int count = 1;
		Mockito.when(bookingDAO.deleteByPreRegistrationId("12345678909876")).thenReturn(count);
		boolean flag = service.deleteOldBooking("12345678909876");
		assertEquals(true, flag);
	}

	@Test(expected = RecordFailedToDeleteException.class)
	public void deleteOldBookingFailTest() {
		RecordFailedToDeleteException exception = new RecordFailedToDeleteException(
				ErrorCodes.PRG_BOOK_RCI_028.getCode(),
				ErrorMessages.FAILED_TO_DELETE_THE_PRE_REGISTRATION_RECORD.getMessage());
		Mockito.when(service.deleteOldBooking("12345678909876")).thenThrow(exception);
		service.deleteOldBooking("12345678909876");

	}

	@Test(expected = AvailablityNotFoundException.class)
	public void increaseAvailabilityTest() {

		BookingRequestDTO successBookDto = new BookingRequestDTO();
		successBookDto.setRegistrationCenterId("1");
		successBookDto.setSlotFromTime(LocalTime.of(9, 0).toString());
		successBookDto.setSlotToTime(LocalTime.of(9, 15).toString());
		successBookDto.setRegDate(LocalDate.now().plusDays(30).toString());
		AvailablityNotFoundException exception = new AvailablityNotFoundException(
				ErrorCodes.PRG_BOOK_RCI_002.toString(),
				ErrorMessages.AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME.toString());
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.anyString())).thenThrow(exception);
		service.increaseAvailability(successBookDto);
	}
	
	
	@SuppressWarnings("unchecked")
	@Test(expected=InvalidRequestParameterException.class)
	public void successBookAppointmentException() {

		InvalidRequestParameterException ex=new InvalidRequestParameterException(ErrorCodes.PRG_BOOK_RCI_031.getCode(),
				ErrorMessages.INVALID_BOOKING_DATE_TIME.getMessage() + " found for preregistration id - "
						+ requestMap.get(RequestCodes.PRE_REGISTRAION_ID.getCode()),
				null);
		MainRequestDTO<BookingRequestDTO> bookingRequestDTOs = new MainRequestDTO<>();
		List<BookingRequestDTO> successBookDtoList = new ArrayList<>();
		BookingRequestDTO successBookDto = new BookingRequestDTO();
		successBookDto.setRegistrationCenterId("1");
		successBookDto.setSlotFromTime(LocalTime.of(9, 0).toString());
		successBookDto.setSlotToTime(LocalTime.of(9, 0).toString().toString());
		successBookDto.setRegDate(LocalDate.now().plusDays(30).toString());
		successBookDtoList.add(successBookDto);
		bookingRequestDTOs.setId("mosip.preregistration.booking.book");
		bookingRequestDTOs.setVersion("1.0");
		bookingRequestDTOs.setRequesttime(new Date());
		bookingRequestDTOs.setRequest(successBookDto);
		List<PreRegistartionStatusDTO> statusListrebook = new ArrayList<>();

		PreRegistartionStatusDTO preRegistartionStatus = new PreRegistartionStatusDTO();
		preRegistartionStatus.setStatusCode(StatusCodes.BOOKED.getCode());
		preRegistartionStatus.setPreRegistartionId(bookingPreId);
		preRegResponse.setResponse(preRegistartionStatus);
		statusListrebook.add(preRegistartionStatus);
		MainResponseDTO<PreRegistartionStatusDTO> preRegResponseRebook = new MainResponseDTO<PreRegistartionStatusDTO>();
		preRegResponseRebook.setErrors(null);
		preRegResponseRebook.setResponse(preRegistartionStatus);

		requestValidatorFlag = ValidationUtil.requestValidator(bookingRequestDTOs);

		Mockito.when(serviceUtil.mandatoryParameterCheck(Mockito.anyString(), Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.validateAppointmentDate(Mockito.any())).thenThrow(ex);
		

		service.bookAppointment(bookingRequestDTOs, bookingPreId);

	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void successBookAppointmentForExpired() {

		MainRequestDTO<BookingRequestDTO> bookingRequestDTOs = new MainRequestDTO<>();
		List<BookingRequestDTO> successBookDtoList = new ArrayList<>();
		BookingRequestDTO successBookDto = new BookingRequestDTO();
		successBookDto.setRegistrationCenterId("1");
		successBookDto.setSlotFromTime(LocalTime.of(9, 0).toString());
		successBookDto.setSlotToTime(LocalTime.of(9, 15).toString());
		successBookDto.setRegDate(LocalDate.now().plusDays(30).toString());
		successBookDtoList.add(successBookDto);
		bookingRequestDTOs.setId("mosip.preregistration.booking.book");
		bookingRequestDTOs.setVersion("1.0");
		bookingRequestDTOs.setRequesttime(new Date());
		bookingRequestDTOs.setRequest(successBookDto);

		availableEntity.setAvailableKiosks(3);
		availableEntity.setRegcntrId("1");
		availableEntity.setRegDate(LocalDate.now().plusDays(30));
		availableEntity.setToTime(LocalTime.of(9, 0));
		availableEntity.setFromTime(LocalTime.of(9, 0));
		availableEntity.setCrBy("John Doe");
		availableEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntity.setDeleted(false);
		List<PreRegistartionStatusDTO> statusListrebook = new ArrayList<>();

		PreRegistartionStatusDTO preRegistartionStatus = new PreRegistartionStatusDTO();
		preRegistartionStatus.setStatusCode(StatusCodes.BOOKED.getCode());
		preRegistartionStatus.setPreRegistartionId(bookingPreId);
		preRegResponse.setResponse(preRegistartionStatus);
		statusListrebook.add(preRegistartionStatus);
		MainResponseDTO<PreRegistartionStatusDTO> preRegResponseRebook = new MainResponseDTO<PreRegistartionStatusDTO>();
		preRegResponseRebook.setErrors(null);
		preRegResponseRebook.setResponse(preRegistartionStatus);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);

		requestValidatorFlag = ValidationUtil.requestValidator(bookingRequestDTOs);
		RegistrationCenterDto centerDto = new RegistrationCenterDto();
		List<RegistrationCenterDto> centerList = new ArrayList<>();
		centerDto.setId("10001");
		centerDto.setLangCode("eng");
		centerDto.setCenterStartTime(startTime);
		centerDto.setCenterEndTime(endTime);
		centerDto.setPerKioskProcessTime(perKioskTime);
		centerDto.setLunchStartTime(LunchStartTime);
		centerDto.setLunchEndTime(LunchEndTime);
		centerDto.setNumberOfKiosks((short) 4);
		centerList.add(centerDto);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		Mockito.when(serviceUtil.mandatoryParameterCheck(Mockito.anyString(), Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.validateAppointmentDate(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.isKiosksAvailable(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.getDemographicStatus(Mockito.anyString())).thenReturn("Expired");
		Mockito.when(serviceUtil.getRegCenterMasterData()).thenReturn(centerList);
		// Update status
		RegistrationBookingEntity bookingEntity2 = new RegistrationBookingEntity();
		bookingEntity2.setBookingPK(new RegistrationBookingPK(DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity2.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity2.setLangCode("12L");
		bookingEntity2.setCrBy("987654321");
		bookingEntity2.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntity2.setRegDate(LocalDate.now().plusDays(30));
		bookingEntity2.setSlotFromTime(LocalTime.of(9, 0));
		bookingEntity2.setSlotToTime(LocalTime.of(9, 0));

		MainResponseDTO mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(bookingEntity2);

		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenReturn(bookingEntity2);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		MainResponseDTO<BookingStatusDTO> response = service.bookAppointment(bookingRequestDTOs, bookingPreId);
		assertEquals("Appointment booked successfully", response.getResponse().getBookingMessage()); 
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void successMultiBookAppointmentExpired() {

		multiBookingListDto.add(multiBookingRequestDto1);
		multiBookingListDto.add(multiBookingRequestDto2);

		MultiBookingRequest multiBookingRequest = new MultiBookingRequest();
		multiBookingRequest.setBookingRequest(multiBookingListDto);
		responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		BookingStatusDTO bookingStatusDTO1 = new BookingStatusDTO();
		bookingStatusDTO1.setBookingMessage("Appointment booked successfully");

		BookingStatusDTO bookingStatusDTO2 = new BookingStatusDTO();
		bookingStatusDTO2.setBookingMessage("Appointment booked successfully");

		List<BookingStatusDTO> bookingStatusDTOs = new ArrayList<>();
		bookingStatusDTOs.add(bookingStatusDTO1);
		bookingStatusDTOs.add(bookingStatusDTO2);

		MainRequestDTO<MultiBookingRequest> bookingRequestDTOs = new MainRequestDTO<>();
		MultiBookingRequest bookingRequest = new MultiBookingRequest();
		List<MultiBookingRequestDTO> successBookDtoList = new ArrayList<>();
		MultiBookingRequestDTO successBookDto1 = new MultiBookingRequestDTO();
		successBookDto1.setPreRegistrationId("23587986034785");
		successBookDto1.setRegistrationCenterId("1");
		successBookDto1.setSlotFromTime(LocalTime.of(9, 0).toString());
		successBookDto1.setSlotToTime(LocalTime.of(9, 15).toString());
		successBookDto1.setRegDate(LocalDate.now().plusDays(30).toString());
		successBookDtoList.add(successBookDto1);
		bookingRequest.setBookingRequest(successBookDtoList);
		bookingRequestDTOs.setRequest(bookingRequest);
		bookingRequestDTOs.setId("mosip.preregistration.booking.book");
		bookingRequestDTOs.setVersion("1.0");
		bookingRequestDTOs.setRequesttime(new Date());

		availableEntity.setAvailableKiosks(3);
		availableEntity.setRegcntrId("1");
		availableEntity.setRegDate(LocalDate.now().plusDays(30));
		availableEntity.setToTime(LocalTime.of(9, 0));
		availableEntity.setFromTime(LocalTime.of(9, 15));
		availableEntity.setCrBy("John Doe");
		availableEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntity.setDeleted(false);
		List<PreRegistartionStatusDTO> statusListrebook = new ArrayList<>();

		PreRegistartionStatusDTO preRegistartionStatus = new PreRegistartionStatusDTO();
		preRegistartionStatus.setStatusCode(StatusCodes.BOOKED.getCode());
		preRegistartionStatus.setPreRegistartionId(bookingPreId);
		preRegResponse.setResponse(preRegistartionStatus);
		statusListrebook.add(preRegistartionStatus);
		MainResponseDTO<PreRegistartionStatusDTO> preRegResponseRebook = new MainResponseDTO<PreRegistartionStatusDTO>();
		preRegResponseRebook.setErrors(null);
		preRegResponseRebook.setResponse(preRegistartionStatus);
		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(availableEntity);

		requestValidatorFlag = ValidationUtil.requestValidator(bookingRequestDTOs);

		// Update status
		RegistrationBookingEntity bookingEntity2 = new RegistrationBookingEntity();
		bookingEntity2.setBookingPK(new RegistrationBookingPK(DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity2.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity2.setLangCode("eng");
		bookingEntity2.setCrBy("John Doe");
		bookingEntity2.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntity2.setRegDate(LocalDate.now().plusDays(30));
		bookingEntity2.setSlotFromTime(LocalTime.of(9, 0));
		bookingEntity2.setSlotToTime(LocalTime.of(9, 15));

		RegistrationCenterDto centerDto = new RegistrationCenterDto();
		List<RegistrationCenterDto> centerList = new ArrayList<>();
		centerDto.setId("10001");
		centerDto.setLangCode("eng");
		centerDto.setCenterStartTime(startTime);
		centerDto.setCenterEndTime(endTime);
		centerDto.setPerKioskProcessTime(perKioskTime);
		centerDto.setLunchStartTime(LunchStartTime);
		centerDto.setLunchEndTime(LunchEndTime);
		centerDto.setNumberOfKiosks((short) 4);
		centerList.add(centerDto);

		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenReturn(bookingEntity2);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		Mockito.when(serviceUtil.mandatoryParameterCheck(Mockito.anyString(), Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.validateAppointmentDate(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.isKiosksAvailable(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.getDemographicStatus(Mockito.anyString())).thenReturn("Expired");
		Mockito.when(serviceUtil.getRegCenterMasterData()).thenReturn(centerList);

		MainResponseDTO mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(bookingEntity2);
		Mockito.when(bookingDAO.findByPreRegistrationId("23587986034785")).thenReturn(bookingEntity2);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);

		MainResponseDTO<BookingStatus> response = service.bookMultiAppointment(bookingRequestDTOs);
		assertEquals("Appointment booked successfully",
				response.getResponse().getBookingStatusResponse().get(0).getBookingMessage());
	}
	
	
	@Test(expected=InvalidRequestParameterException.class)
	public void successMultiBookAppointmentException() {
		InvalidRequestParameterException ex=new InvalidRequestParameterException(ErrorCodes.PRG_BOOK_RCI_031.getCode(),
				ErrorMessages.INVALID_BOOKING_DATE_TIME.getMessage() + " found for preregistration id - "
						+ requestMap.get(RequestCodes.PRE_REGISTRAION_ID.getCode()),
				null);
		multiBookingListDto.add(multiBookingRequestDto1);
		multiBookingListDto.add(multiBookingRequestDto2);

		MultiBookingRequest multiBookingRequest = new MultiBookingRequest();
		multiBookingRequest.setBookingRequest(multiBookingListDto);
		responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		BookingStatusDTO bookingStatusDTO1 = new BookingStatusDTO();
		bookingStatusDTO1.setBookingMessage("Appointment booked successfully");

		BookingStatusDTO bookingStatusDTO2 = new BookingStatusDTO();
		bookingStatusDTO2.setBookingMessage("Appointment booked successfully");

		List<BookingStatusDTO> bookingStatusDTOs = new ArrayList<>();
		bookingStatusDTOs.add(bookingStatusDTO1);
		bookingStatusDTOs.add(bookingStatusDTO2);

		MainRequestDTO<MultiBookingRequest> bookingRequestDTOs = new MainRequestDTO<>();
		MultiBookingRequest bookingRequest = new MultiBookingRequest();
		List<MultiBookingRequestDTO> successBookDtoList = new ArrayList<>();
		MultiBookingRequestDTO successBookDto1 = new MultiBookingRequestDTO();
		successBookDto1.setPreRegistrationId("23587986034785");
		successBookDto1.setRegistrationCenterId("1");
		successBookDto1.setSlotFromTime(LocalTime.of(9, 0).toString());
		successBookDto1.setSlotToTime(LocalTime.of(9, 15).toString());
		successBookDto1.setRegDate(org.joda.time.LocalDate.now().plusDays(30).toString());
		successBookDtoList.add(successBookDto1);
		bookingRequest.setBookingRequest(successBookDtoList);
		bookingRequestDTOs.setRequest(bookingRequest);
		bookingRequestDTOs.setId("mosip.preregistration.booking.book");
		bookingRequestDTOs.setVersion("1.0");
		bookingRequestDTOs.setRequesttime(new Date());

		availableEntity.setAvailableKiosks(3);
		availableEntity.setRegcntrId("1");
		availableEntity.setRegDate(LocalDate.now().plusDays(30));
		availableEntity.setToTime(LocalTime.of(9,0));
		availableEntity.setFromTime(LocalTime.of(9, 15));
		availableEntity.setCrBy("John Doe");
		availableEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntity.setDeleted(false);
		requestValidatorFlag = ValidationUtil.requestValidator(bookingRequestDTOs);

		Mockito.when(serviceUtil.validateAppointmentDate(Mockito.any())).thenThrow(ex);


		service.bookMultiAppointment(bookingRequestDTOs);
		
	}
	
}