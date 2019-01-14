
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.FileNotFoundException;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.booking.code.StatusCodes;
import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.booking.dto.DateTimeDto;
import io.mosip.preregistration.booking.dto.HolidayDto;
import io.mosip.preregistration.booking.dto.MainRequestDTO;
import io.mosip.preregistration.booking.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.booking.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.booking.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.booking.dto.RegistrationCenterDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterHolidayDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterResponseDto;
import io.mosip.preregistration.booking.dto.SlotDto;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingPK;
import io.mosip.preregistration.booking.repository.BookingAvailabilityRepository;
import io.mosip.preregistration.booking.repository.RegistrationBookingRepository;
import io.mosip.preregistration.booking.repository.impl.BookingDAO;
import io.mosip.preregistration.booking.service.BookingService;
import io.mosip.preregistration.booking.service.util.BookingServiceUtil;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.MainListRequestDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.ValidationUtil;

/**
 * Booking service Test
 * 
 * @author Sanober Noor
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

//	MainListRequestDTO bookingDTO = new MainListRequestDTO();
	AvailibityEntity availableEntity= new AvailibityEntity();
	RegistrationBookingEntity bookingEntity = new RegistrationBookingEntity();
	List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
	PreRegistartionStatusDTO preRegistartionStatusDTO = new PreRegistartionStatusDTO();
	MainListResponseDTO preRegResponse = new MainListResponseDTO();
	
	List<BookingRequestDTO> bookingList = new ArrayList<>();
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
	List<RegistrationBookingEntity> bookingEntities=new ArrayList<>();
	PreRegIdsByRegCenterIdDTO preRegIdsByRegCenterIdDTO=new PreRegIdsByRegCenterIdDTO();
	MainRequestDTO<PreRegIdsByRegCenterIdDTO> requestDTO=new MainRequestDTO<>();
	MainListRequestDTO<BookingRequestDTO> bookingDto=new MainListRequestDTO<>();

	@Value("${version}")
	String versionUrl;

	@Value("${id}")
	String idUrl;

	@Value("${preRegResourceUrl}")
	private String preRegResourceUrl;

	
	@Before
	public void setup() throws URISyntaxException, FileNotFoundException, ParseException, java.io.FileNotFoundException, IOException, org.json.simple.parser.ParseException {


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
		oldBooking.setRegDate("2018-12-06");
		// rebookingRequestDTO.setOldBookingDetails(oldBooking);

		newBooking.setRegistrationCenterId("1");
		newBooking.setSlotFromTime("09:00");
		newBooking.setSlotToTime("09:13");
		newBooking.setRegDate("2018-12-06");

		rebookingRequestDTO.setNewBookingDetails(newBooking);
		bookingRequestDTO.setPreRegistrationId("23587986034785");
		bookingRequestDTO.setNewBookingDetails(newBooking);
		oldBooking_success.setRegistrationCenterId("1");
		oldBooking_success.setSlotFromTime("09:00");
		oldBooking_success.setSlotToTime("09:13");
		oldBooking_success.setRegDate("2018-12-05");
		rebookingRequestDTO.setOldBookingDetails(oldBooking_success);
		bookingRequestDTO.setOldBookingDetails(oldBooking);

		bookingList.add(bookingRequestDTO);
		

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
		responseDto.setErr(null);
		responseDto.setStatus(true);
		responseDto.setResTime(serviceUtil.getCurrentResponseTime());


		requiredRequestMap.put("id", idUrl);
		requiredRequestMap.put("ver", versionUrl);

		cancelRequestdto.setReqTime(new Date());
		cancelRequestdto.setRequest(cancelbookingDto);
		cancelRequestdto.setId("mosip.pre-registration.booking.book");
		cancelRequestdto.setVer("1.0");
		cancelbookingDto.setPreRegistrationId("23587986034785");
		cancelbookingDto.setRegDate("2018-12-04");
		cancelbookingDto.setRegistrationCenterId("1");
		cancelbookingDto.setSlotFromTime("09:00");
		cancelbookingDto.setSlotToTime("09:13");
		requestMap1.put("id", cancelRequestdto.getId());
		requestMap1.put("ver", cancelRequestdto.getVer());
		requestMap1.put("reqTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(cancelRequestdto.getReqTime()));
		requestMap1.put("request", cancelRequestdto.getRequest().toString());
		
		
		
		availableEntity.setAvailableKiosks(4);
	    availableEntity.setRegcntrId("1");
	    availableEntity.setRegDate(LocalDate.parse("2018-12-04"));
	    availableEntity.setToTime(localTime2);
	    availableEntity.setFromTime(localTime1);
	    availableEntity.setCrBy("987654321");
	    availableEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
	    availableEntity.setDeleted(false);
	    
	    
		bookingEntity.setBookingPK(new RegistrationBookingPK("1234567890", DateUtils.parseDateToLocalDateTime(new Date())));
		bookingEntity.setRegistrationCenterId(oldBooking.getRegistrationCenterId());
		bookingEntity.setStatusCode(StatusCodes.BOOKED.getCode());
		bookingEntity.setLangCode("12L");
		bookingEntity.setCrBy("987654321");
		bookingEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		bookingEntity.setRegDate(LocalDate.parse(oldBooking.getRegDate()));
		bookingEntity.setSlotFromTime(LocalTime.parse(oldBooking.getSlotFromTime()));
		bookingEntity.setSlotToTime(LocalTime.parse(oldBooking.getSlotToTime()));
		
		
		preRegistartionStatusDTO.setStatusCode(StatusCodes.BOOKED.getCode());
		preRegistartionStatusDTO.setPreRegistartionId("23587986034785");
		statusList.add(preRegistartionStatusDTO);
		
		preRegResponse.setResponse(statusList);
		preRegResponse.setErr(null);
		preRegResponse.setStatus(true);
		List<String> preId=new ArrayList<>();
		preId.add("1234567890");
		preRegIdsByRegCenterIdDTO.setRegistrationCenterId("1");
		preRegIdsByRegCenterIdDTO.setPreRegistrationIds(preId);
		requestDTO.setRequest(preRegIdsByRegCenterIdDTO);
		
		requestDTO.setId("mosip.pre-registration.booking.book");
		requestDTO.setVer("1.0");
		requestDTO.setReqTime(new Date());
		bookingDto.setId("mosip.pre-registration.booking.book");
		bookingDto.setReqTime(new Date());
		bookingDto.setVer("1.0");
		bookingDto.setRequest(bookingList);
	}

	@Test
	public void getAvailabilityTest() {
		
		logger.info("Availability dto " + availability);
		List<LocalDate> date = new ArrayList<>();
		List<AvailibityEntity> entityList = new ArrayList<>();
		date.add(LocalDate.now());
		entityList.add(availableEntity);
		logger.info("Availability entity " + availableEntity);
		Mockito.when(bookingDAO.findDate(Mockito.anyString(),Mockito.any(), Mockito.any())).thenReturn(date);
		Mockito.when(bookingDAO.findByRegcntrIdAndRegDateOrderByFromTimeAsc(Mockito.anyString(),
				Mockito.any())).thenReturn(entityList);
		MainResponseDTO<AvailabilityDto> responseDto = service.getAvailability("1");
		logger.info("Response " + responseDto);
		assertEquals("1",responseDto.getResponse().getRegCenterId());

}
    @Test
	public void getPreIdsByRegCenterId() {
		
    	
    	requestValidatorFlag = ValidationUtil.requestValidator(requestMap1, requiredRequestMap);
    	bookingEntities.add(bookingEntity);
    	Mockito.when(bookingDAO.findByRegistrationCenterIdAndStatusCode("1", StatusCodes.BOOKED.getCode())).thenReturn(bookingEntities);
    	MainListResponseDTO<PreRegIdsByRegCenterIdResponseDTO> response=service.getPreIdsByRegCenterId(requestDTO);
assertEquals(serviceUtil.getCurrentResponseTime(), response.getResTime());
    
    }
    
    @Test
    public void successBookAppointment() {
    	requestValidatorFlag = ValidationUtil.requestValidator(requestMap1, requiredRequestMap);
    	RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		ResponseEntity<MainListResponseDTO> respEntity=new ResponseEntity<>(preRegResponse,HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(),
				 Mockito.eq(HttpMethod.GET), Mockito.any(),
				 Mockito.eq(MainListResponseDTO.class))).thenReturn(respEntity);
		Mockito.when(mapper.convertValue(respEntity.getBody().getResponse().get(0), PreRegistartionStatusDTO.class)).thenReturn(preRegistartionStatusDTO);
    
		MainResponseDTO<List<BookingStatusDTO>> response=service.bookAppointment(bookingDto);
		assertEquals(0, response.getResponse().size());
    }
	


	@Test
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
		centerDto.setLanguageCode("LOC01");
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
		assertEquals( "MASTER_DATA_SYNCED_SUCCESSFULLY",response.getResponse());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void cancelAppointmentSuccessTest() {
		
		requestValidatorFlag = ValidationUtil.requestValidator(requestMap1, requiredRequestMap);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		
		ResponseEntity<MainListResponseDTO> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);

		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(MainListResponseDTO.class))).thenReturn(res);

		Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(availableEntity);
		
		Mockito.when(bookingDAO.findPreIdAndStatusCode(Mockito.any(), Mockito.any())).thenReturn(bookingEntity);

		Mockito.when(bookingDAO.saveRegistrationEntityForCancel(Mockito.any())).thenReturn(bookingEntity);
		
		MainResponseDTO mainResponseDTO=new MainResponseDTO<>();
		mainResponseDTO.setErr(null);
		mainResponseDTO.setStatus(true);
		mainResponseDTO.setResponse(bookingEntity);
		ResponseEntity<MainResponseDTO> resp = new ResponseEntity<>(mainResponseDTO,HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
				Mockito.eq(MainResponseDTO.class))).thenReturn(resp);
		availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() + 1);
		Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
		MainResponseDTO<CancelBookingResponseDTO> responseDto = service.cancelAppointment(cancelRequestdto);
		assertEquals( "APPOINTMENT_SUCCESSFULLY_CANCELED",responseDto.getResponse().getMessage());

	}


	@Test
	public void getAppointmentDetailsTest() {
		Mockito.when(bookingDAO.findPreIdAndStatusCode("23587986034785",StatusCodes.BOOKED.getCode())).thenReturn(bookingEntity);
		MainResponseDTO<BookingRegistrationDTO> responseDto = service.getAppointmentDetails("23587986034785");
		assertEquals("1",responseDto.getResponse().getRegistrationCenterId());
	}
@Test
public void bookTest() {
	RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
	Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
	
	MainResponseDTO mainResponseDTO=new MainResponseDTO<>();
	mainResponseDTO.setErr(null);
	mainResponseDTO.setStatus(true);
	mainResponseDTO.setResponse(bookingEntity);
	ResponseEntity<MainResponseDTO> resp = new ResponseEntity<>(mainResponseDTO,HttpStatus.OK);
	Mockito.when(bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(availableEntity);
	Mockito.when(bookingDAO.updateAvailibityEntity(availableEntity)).thenReturn(availableEntity);
	Mockito.when(bookingDAO.saveRegistrationEntityForBooking(Mockito.any())).thenReturn(bookingEntity);
	
	Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
			Mockito.eq(MainResponseDTO.class))).thenReturn(resp);
	BookingStatusDTO response=service.book("23587986034785", newBooking);
	assertEquals("APPOINTMENT_SUCCESSFULLY_BOOKED", response.getBookingMessage());
}
@Test
public void cancelTest() {
	
	
}
}
