
package io.mosip.preregistration.booking.test.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.booking.code.StatusCodes;
import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.BookingDTO;
import io.mosip.preregistration.booking.dto.BookingRegistrationDTO;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.booking.dto.DateTimeDto;
import io.mosip.preregistration.booking.dto.HolidayDto;
import io.mosip.preregistration.booking.dto.PreRegResponseDto;
import io.mosip.preregistration.booking.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.booking.dto.RegistrationCenterDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterHolidayDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterResponseDto;
import io.mosip.preregistration.booking.dto.RequestDto;
import io.mosip.preregistration.booking.dto.ResponseDto;
import io.mosip.preregistration.booking.dto.SlotDto;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingPK;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.AppointmentAlreadyCanceledException;
import io.mosip.preregistration.booking.exception.AppointmentCannotBeCanceledException;
import io.mosip.preregistration.booking.exception.AppointmentReBookingFailedException;
import io.mosip.preregistration.booking.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDataNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDateNotSeletectedException;
import io.mosip.preregistration.booking.exception.BookingPreIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingRegistrationCenterIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingTimeSlotNotSeletectedException;
import io.mosip.preregistration.booking.exception.CancelAppointmentFailedException;
import io.mosip.preregistration.booking.exception.RecordNotFoundException;
import io.mosip.preregistration.booking.exception.InvalidDateTimeFormatException;
import io.mosip.preregistration.booking.repository.BookingAvailabilityRepository;
import io.mosip.preregistration.booking.repository.RegistrationBookingRepository;
import io.mosip.preregistration.booking.service.BookingService;
import io.mosip.preregistration.core.exceptions.InvalidRequestParameterException;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;
import io.mosip.preregistration.core.util.ValidationUtil;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

/**
 * Booking service Test
 * 
 * @author M1046129
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

	private BookingService var=null;
			
	private AvailabilityDto availability = new AvailabilityDto();
	private List<DateTimeDto> dateList = new ArrayList<>();
	private DateTimeDto dateDto = new DateTimeDto();
	private List<SlotDto> slotsList = new ArrayList<>();
	private AvailibityEntity entity = new AvailibityEntity();
	private SlotDto slots = new SlotDto();

	BookingDTO bookingDTO = new BookingDTO();
	
	List<BookingRequestDTO> bookingList = new ArrayList<>();
	BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();

	BookingRequestDTO rebookingRequestDTO = new BookingRequestDTO();

	BookingRegistrationDTO oldBooking= new BookingRegistrationDTO();
	BookingRegistrationDTO oldBooking_success= new BookingRegistrationDTO();
	BookingRegistrationDTO newBooking= new BookingRegistrationDTO();
	ResponseDto<List<BookingStatusDTO>> responseDto = new ResponseDto<>();

	BookingStatusDTO statusDTOA = new BookingStatusDTO();
	BookingStatusDTO statusDTOB = new BookingStatusDTO();
	Map<String, String> requiredRequestMap = new HashMap<>();
	InvalidRequestParameterException parameterException = null;
	Map<String, String> requestMap = new HashMap<>();
	RegistrationBookingEntity bookingEntity = new RegistrationBookingEntity();
	RegistrationBookingPK bookingPK = new RegistrationBookingPK();
	RegistrationCenterResponseDto regCenDto= new RegistrationCenterResponseDto();

	Map<String, String> requestMap1 = new HashMap<>();
	private RequestDto<CancelBookingDTO> cancelRequestdto = new RequestDto<>();
	private CancelBookingDTO cancelbookingDto = new CancelBookingDTO();

	DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	LocalTime localTime1;
	LocalTime localTime2;
	
	@Value("${version}")
	String versionUrl;

	@Value("${id}")
	String idUrl;

	@Value("${preRegResourceUrl}")
	private String preRegResourceUrl;

	/**
	 * @throws URISyntaxException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	@Before
	public void setup() throws URISyntaxException, FileNotFoundException, ParseException {

		var=new BookingService();
		
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

		entity.setAvailableKiosks(4);
		entity.setRegcntrId("1");
		entity.setRegDate(LocalDate.parse("2018-12-04"));
		entity.setToTime(localTime2);
		entity.setFromTime(localTime1);

		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();

		URI dataSyncUri = new URI(
				classLoader.getResource("booking.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File file = new File(dataSyncUri.getPath());
		parser.parse(new FileReader(file));

		// Rebooking
		rebookingRequestDTO.setPre_registration_id("23587986034785");

		oldBooking.setRegistration_center_id("1");
		oldBooking.setSlotFromTime("09:00");
		oldBooking.setSlotToTime("09:13");
		oldBooking.setReg_date("2018-12-06");
		//rebookingRequestDTO.setOldBookingDetails(oldBooking);

		newBooking.setRegistration_center_id("1");
		newBooking.setSlotFromTime("09:00");
		newBooking.setSlotToTime("09:13");
		newBooking.setReg_date("2018-12-06");

		rebookingRequestDTO.setNewBookingDetails(newBooking);
		bookingRequestDTO.setPre_registration_id("23587986034785");
		bookingRequestDTO.setNewBookingDetails(newBooking);
		oldBooking_success.setRegistration_center_id("1");
		oldBooking_success.setSlotFromTime("09:00");
		oldBooking_success.setSlotToTime("09:13");
		oldBooking_success.setReg_date("2018-12-05");
		rebookingRequestDTO.setOldBookingDetails(oldBooking_success);
		bookingRequestDTO.setOldBookingDetails(oldBooking);

		bookingList.add(bookingRequestDTO);
		bookingDTO.setReqTime("2018-12-06T07:22:57.086");
		bookingDTO.setRequest(bookingList);
		bookingDTO.setId("mosip.pre-registration.booking.book");
		bookingDTO.setVer("1.0");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
		bookingPK.setBookingDateTime(LocalDateTime.parse(bookingDTO.getReqTime(), formatter));
		bookingPK.setPreregistrationId("1234567890");

		bookingEntity.setBookingPK(bookingPK);
		bookingEntity.setRegistrationCenterId(oldBooking.getRegistration_center_id());
		bookingEntity.setStatus_code(StatusCodes.Booked.toString().trim());
		bookingEntity.setLang_code("12L");
		bookingEntity.setCrBy("987654321");
		bookingEntity.setCrDate(Timestamp.valueOf(LocalDateTime.parse(bookingDTO.getReqTime())));
		bookingEntity.setRegDate(LocalDate.parse(oldBooking.getReg_date()));
		bookingEntity.setSlotFromTime(LocalTime.parse(oldBooking.getSlotFromTime()));
		bookingEntity.setSlotToTime(LocalTime.parse(oldBooking.getSlotToTime()));

		statusDTOA.setBooking_status(StatusCodes.Booked.toString());
		statusDTOA.setPre_registration_id(bookingRequestDTO.getPre_registration_id());
		statusDTOA.setBooking_message("APPOINTMENT_SUCCESSFULLY_BOOKED");

		statusDTOB.setBooking_status(StatusCodes.Booked.toString());
		statusDTOB.setPre_registration_id(bookingRequestDTO.getPre_registration_id());
		statusDTOB.setBooking_message("APPOINTMENT_SUCCESSFULLY_BOOKED");

		List<BookingStatusDTO> resp = new ArrayList<>();

		resp.add(statusDTOA);
		resp.add(statusDTOB);
		responseDto.setResponse(resp);
		responseDto.setErr(null);
		responseDto.setStatus(true);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));

		requestMap.put("id", bookingDTO.getId());
		requestMap.put("ver", bookingDTO.getVer());
		requestMap.put("reqTime", bookingDTO.getReqTime());
		requestMap.put("request", bookingDTO.getRequest().toString());

		requiredRequestMap.put("id", idUrl);
		requiredRequestMap.put("ver", versionUrl);

		cancelRequestdto.setReqTime("2018-12-06T07:22:57.086");
		cancelRequestdto.setRequest(cancelbookingDto);
		cancelRequestdto.setId("mosip.pre-registration.booking.book");
		cancelRequestdto.setVer("1.0");
		cancelbookingDto.setPre_registration_id("23587986034785");
		cancelbookingDto.setReg_date("2018-12-04");
		cancelbookingDto.setRegistration_center_id("1");
		cancelbookingDto.setSlotFromTime("09:00");
		cancelbookingDto.setSlotToTime("09:13");
		requestMap1.put("id", cancelRequestdto.getId());
		requestMap1.put("ver", cancelRequestdto.getVer());
		requestMap1.put("reqTime", cancelRequestdto.getReqTime());
		requestMap1.put("request", cancelRequestdto.getRequest().toString());
	}

	@Test
	public void getAvailabilityTest() {
		logger.info("Availability dto " + availability);
		List<java.sql.Date> date = new ArrayList<>();
		List<AvailibityEntity> entityList = new ArrayList<>();
		date.add(java.sql.Date.valueOf("2018-12-04"));
		entityList.add(entity);
		logger.info("Availability entity " + entity);
		Mockito.when(bookingAvailabilityRepository.findDate(Mockito.anyString(), Mockito.any())).thenReturn(date);
		Mockito.when(bookingAvailabilityRepository.findByRegcntrIdAndRegDate(Mockito.anyString(), Mockito.any()))
				.thenReturn(entityList);
		ResponseDto<AvailabilityDto> responseDto = service.getAvailability("1");
		logger.info("Response " + responseDto);
		assertEquals(responseDto.getResponse().getRegCenterId(), "1");

	}

//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@Test
//	public void SuccessBook() {
//		
//	//	BookingService spy_var=Mockito.spy(var);
//		
//		BookingDTO bookingDTO1 = new BookingDTO();
//		List<BookingRequestDTO> bookingList1 = new ArrayList<>();
//		bookingList1.add(rebookingRequestDTO);
//		bookingDTO1.setReqTime("2018-12-06T07:22:57.086");
//		bookingDTO1.setRequest(bookingList1);
//		bookingDTO1.setId("mosip.pre-registration.booking.book");
//		bookingDTO1.setVer("1.0");
//		parameterException = ValidationUtil.requestValidator(requestMap, requiredRequestMap);
//		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
//		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
//		
//		List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
//		PreRegistartionStatusDTO preRegistartionStatusDTO = new PreRegistartionStatusDTO();
//		preRegistartionStatusDTO.setStatusCode(StatusCodes.Booked.toString());
//		preRegistartionStatusDTO.setPreRegistartionId("23587986034785");
//		statusList.add(preRegistartionStatusDTO);
//
//		PreRegResponseDto preRegResponse = new PreRegResponseDto();
//		preRegResponse.setResponse(statusList);
//		preRegResponse.setErr(null);
//		preRegResponse.setStatus(true);
//		
//		
////		ResponseDto<CancelBookingResponseDTO> responseDto1= new ResponseDto<>();
////		Mockito.when(spy_var.cancelAppointment(cancelRequestdto)).thenReturn(responseDto1);
////		ResponseDto<CancelBookingResponseDTO> result = var.cancelAppointment(cancelRequestdto);
////		assertEquals(responseDto.getStatus(), result.getStatus());
//		//cancel test
//		ResponseEntity<PreRegResponseDto> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);
//
//		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
//				Mockito.eq(PreRegResponseDto.class))).thenReturn(res);
//
//		Mockito.when(bookingAvailabilityRepository.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
//				LocalTime.parse("09:00"), LocalTime.parse("09:13"), LocalDate.parse("2018-12-05"), "1"))
//				.thenReturn(entity);
//
//		Mockito.when(registrationBookingRepository.existsByPreIdandStatusCode("23587986034785",
//				StatusCodes.Booked.toString())).thenReturn(true);
//		Mockito.when(registrationBookingRepository.findByPreId("23587986034785")).thenReturn(bookingEntity);
//		Mockito.when(registrationBookingRepository.save(Mockito.any())).thenReturn(bookingEntity);
//
//		ResponseEntity<ResponseDto> resp = null;
//		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
//				Mockito.eq(ResponseDto.class))).thenReturn(resp);
//		entity.setAvailableKiosks(entity.getAvailableKiosks() + 1);
//		Mockito.when(bookingAvailabilityRepository.update(entity)).thenReturn(entity);
//		
//		
//		//booking test
//	       List<PreRegistartionStatusDTO> statusList1 = new ArrayList<>();
//	       PreRegistartionStatusDTO preRegistartionStatusDTO1 = new PreRegistartionStatusDTO();
//	preRegistartionStatusDTO1.setStatusCode(StatusCodes.Pending_Appointment.toString());
//	preRegistartionStatusDTO1.setPreRegistartionId("23587986034785");
//	       statusList1.add(preRegistartionStatusDTO1);
//
//	       PreRegResponseDto preRegResponse1 = new PreRegResponseDto();
//	       preRegResponse1.setResponse(statusList1);
//	       preRegResponse1.setErr(null);
//	       preRegResponse1.setStatus(true);
//
//	       ResponseEntity<PreRegResponseDto> res1 = new ResponseEntity<>(preRegResponse1, HttpStatus.OK);
//	       
//
//	     
//	Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
//	       Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
//	                    Mockito.eq(PreRegResponseDto.class))).thenReturn(res1);
//
////	Mockito.when(bookingAvailabilityRepository.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
////	                    LocalTime.parse("09:00"), LocalTime.parse("09:13"), LocalDate.parse("2018-12-06"), "1"))
////	                    .thenReturn(entity);
////
////	Mockito.when(registrationBookingRepository.existsByPreIdandStatusCode("23587986034785",
////	                    StatusCodes.Booked.toString())).thenReturn(false);
////	Mockito.when(registrationBookingRepository.save(Mockito.any())).thenReturn(bookingEntity);
////	       
////	System.out.println("bookingEntity:"+bookingEntity.getStatus_code());
////	       
////	       ResponseEntity<ResponseDto> resp1 = null;
////	       Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
////	                    Mockito.eq(ResponseDto.class))).thenReturn(resp1);
////	       
////	Mockito.when(bookingAvailabilityRepository.update(entity)).thenReturn(entity);
//
//
//		ResponseDto<List<BookingStatusDTO>> responseDto=service.bookAppointment(bookingDTO1);
//	}
	

	@Test(expected=AppointmentReBookingFailedException.class)
	public void AppointmentReBookingFailedExceptionTest() {
		
		entity.setAvailableKiosks(4);
		entity.setRegcntrId("1");
		entity.setRegDate(LocalDate.parse("2018-12-07"));
		entity.setToTime(LocalTime.parse("09:13"));
		entity.setFromTime(LocalTime.parse("09:00"));
		bookingPK.setPreregistrationId("12345678990");
		
		 bookingList.add(bookingRequestDTO);
		 bookingDTO.setReqTime("2018-12-06T07:22:57.086");
		 bookingDTO.setRequest(bookingList);
		 bookingDTO.setId("mosip.pre-registration.booking.book");
		 bookingDTO.setVer("1.0");


		parameterException = ValidationUtil.requestValidator(requestMap, requiredRequestMap);

		ResponseDto<List<BookingStatusDTO>> responseDto=service.bookAppointment(bookingDTO);
		// assertEquals(responseDto.getStatus(), true);
	}


	
	@Test
	public void isMandatoryTest() {
		//Mockito.when(service.isMandatory("")).thenReturn(false);
		service.isMandatory(null);
		
	}	

	
	@Test(expected=RecordNotFoundException.class)
	public void getAvailabilityFailureTest() {
		logger.info("Availability dto "+availability);
		List<java.sql.Date> date= new ArrayList<>();
		List<AvailibityEntity> entityList= new ArrayList<>();
		entityList.add(entity);
		Mockito.when(bookingAvailabilityRepository.findDate(Mockito.anyString(),Mockito.any())).thenReturn(date);
		Mockito.when(bookingAvailabilityRepository.findByRegcntrIdAndRegDate(Mockito.anyString(),Mockito.any())).thenReturn(entityList);
		service.getAvailability("1");
	}
	
	@Test(expected=AvailablityNotFoundException.class)
	public void getAvailabilityTableFailureTest()  throws Exception{
		DataAccessLayerException exception = new DataAccessLayerException(ErrorCodes.PRG_BOOK_RCI_016.toString(),
				ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.toString(),null);
		List<java.sql.Date> date= new ArrayList<>();
		List<AvailibityEntity> entityList= new ArrayList<>();
		entityList.add(entity);
		Mockito.when(bookingAvailabilityRepository.findDate(Mockito.anyString(),Mockito.any())).thenThrow(exception);
		service.getAvailability("1");
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
		LocalTime endTime= localDateTime2.toLocalTime();
		LocalTime perKioskTime=localDateTime3.toLocalTime();
		LocalTime LunchStartTime=LocalDateTime.parse(date4, format).toLocalTime();
		LocalTime LunchEndTime=LocalDateTime.parse(date5, format).toLocalTime();
		RegistrationCenterDto centerDto= new RegistrationCenterDto();
		List<RegistrationCenterDto> centerList= new ArrayList<>();
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
		RegistrationCenterHolidayDto CenholidayDto= new RegistrationCenterHolidayDto();
		HolidayDto holiday= new HolidayDto();
		List<HolidayDto> holidayList= new ArrayList<>();
		holiday.setHolidayDate("2018-12-12");
		holidayList.add(holiday);
		CenholidayDto.setHolidays(holidayList);
		
		ResponseDto<String> response= new ResponseDto<>();

		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		
		ResponseEntity<RegistrationCenterResponseDto> rescenter = new ResponseEntity<>(regCenDto, HttpStatus.OK);
		ResponseEntity<RegistrationCenterHolidayDto> resHoliday = new ResponseEntity<>(CenholidayDto, HttpStatus.OK);
		
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(RegistrationCenterResponseDto.class))).thenReturn(rescenter);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(RegistrationCenterHolidayDto.class))).thenReturn(resHoliday);
		response= service.addAvailability();
		assertEquals(response.getResponse(),"MASTER_DATA_SYNCED_SUCCESSFULLY");
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void cancelAppointmentSuccessTest() {
		parameterException = ValidationUtil.requestValidator(requestMap1, requiredRequestMap);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);

		List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
		PreRegistartionStatusDTO preRegistartionStatusDTO = new PreRegistartionStatusDTO();
		preRegistartionStatusDTO.setStatusCode(StatusCodes.Booked.toString());
		preRegistartionStatusDTO.setPreRegistartionId("23587986034785");
		statusList.add(preRegistartionStatusDTO);

		PreRegResponseDto preRegResponse = new PreRegResponseDto();
		preRegResponse.setResponse(statusList);
		preRegResponse.setErr(null);
		preRegResponse.setStatus(true);

		ResponseEntity<PreRegResponseDto> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);

		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(PreRegResponseDto.class))).thenReturn(res);

		Mockito.when(bookingAvailabilityRepository.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
				LocalTime.parse("09:00"), LocalTime.parse("09:13"), LocalDate.parse("2018-12-04"), "1"))
				.thenReturn(entity);

		Mockito.when(registrationBookingRepository.existsByPreIdandStatusCode("23587986034785",
				StatusCodes.Booked.toString())).thenReturn(true);
		Mockito.when(registrationBookingRepository.findByPreId("23587986034785")).thenReturn(bookingEntity);
		Mockito.when(registrationBookingRepository.save(bookingEntity)).thenReturn(bookingEntity);

		ResponseEntity<ResponseDto> resp = null;
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
				Mockito.eq(ResponseDto.class))).thenReturn(resp);
		entity.setAvailableKiosks(entity.getAvailableKiosks() + 1);
		Mockito.when(bookingAvailabilityRepository.update(entity)).thenReturn(entity);
		ResponseDto<CancelBookingResponseDTO> responseDto = service.cancelAppointment(cancelRequestdto);
		assertEquals(responseDto.getResponse().getMessage(), "APPOINTMENT_SUCCESSFULLY_CANCELED");

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected = CancelAppointmentFailedException.class)
	public void AppointmentCanceledFailedExceptionTest() throws Exception {
		CancelAppointmentFailedException exception = new CancelAppointmentFailedException();
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
		PreRegistartionStatusDTO preRegistartionStatusDTO = new PreRegistartionStatusDTO();
		preRegistartionStatusDTO.setStatusCode(StatusCodes.Booked.toString());
		preRegistartionStatusDTO.setPreRegistartionId("23587986034785");
		statusList.add(preRegistartionStatusDTO);

		PreRegResponseDto preRegResponse = new PreRegResponseDto();
		preRegResponse.setResponse(statusList);
		preRegResponse.setErr(null);
		preRegResponse.setStatus(false);

		ResponseEntity<PreRegResponseDto> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(PreRegResponseDto.class))).thenReturn(res);

		Mockito.when(bookingAvailabilityRepository.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
				LocalTime.parse("09:00"), LocalTime.parse("09:13"), LocalDate.parse("2018-12-04"), "1"))
				.thenReturn(entity);

		Mockito.when(registrationBookingRepository.existsByPreIdandStatusCode("23587986034785",
				StatusCodes.Booked.toString())).thenReturn(true);
		Mockito.when(registrationBookingRepository.findByPreId("23587986034785")).thenReturn(bookingEntity);
		Mockito.when(registrationBookingRepository.save(bookingEntity)).thenReturn(null);
		ResponseDto<CancelBookingResponseDTO> responseDto = service.cancelAppointment(cancelRequestdto);
		assertEquals(responseDto.getErr().getMessage().toString(), exception.getMessage().toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected = AvailablityNotFoundException.class)
	public void AvailablityNotFoundExceptionTest() throws Exception {
		AvailablityNotFoundException exception = new AvailablityNotFoundException();
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
		PreRegistartionStatusDTO preRegistartionStatusDTO = new PreRegistartionStatusDTO();
		preRegistartionStatusDTO.setStatusCode(StatusCodes.Booked.toString());
		preRegistartionStatusDTO.setPreRegistartionId("23587986034785");
		statusList.add(preRegistartionStatusDTO);

		PreRegResponseDto preRegResponse = new PreRegResponseDto();
		preRegResponse.setResponse(statusList);
		preRegResponse.setErr(null);
		preRegResponse.setStatus(false);

		ResponseEntity<PreRegResponseDto> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(PreRegResponseDto.class))).thenReturn(res);

		System.out.println("Response:" + res);
		Mockito.when(registrationBookingRepository.existsByPreIdandStatusCode("23587986034785",
				StatusCodes.Booked.toString())).thenReturn(true);

		Mockito.when(bookingAvailabilityRepository.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
				LocalTime.parse("09:00"), LocalTime.parse("09:13"), LocalDate.parse("2014-12-04"), "1"))
				.thenReturn(null);

		ResponseDto<CancelBookingResponseDTO> responseDto = service.cancelAppointment(cancelRequestdto);
		assertEquals(responseDto.getErr().getMessage().toString(), exception.getMessage().toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected = AppointmentCannotBeCanceledException.class)
	public void AppointmentCannotBeCanceledExceptionTest() throws Exception {
		AppointmentCannotBeCanceledException exception = new AppointmentCannotBeCanceledException();
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
		PreRegistartionStatusDTO preRegistartionStatusDTO = new PreRegistartionStatusDTO();
		preRegistartionStatusDTO.setStatusCode(StatusCodes.Pending_Appointment.toString());
		preRegistartionStatusDTO.setPreRegistartionId("23587986034785");
		statusList.add(preRegistartionStatusDTO);

		PreRegResponseDto preRegResponse = new PreRegResponseDto();
		preRegResponse.setResponse(statusList);
		preRegResponse.setErr(null);
		preRegResponse.setStatus(false);

		ResponseEntity<PreRegResponseDto> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(PreRegResponseDto.class))).thenReturn(res);

		ResponseDto<CancelBookingResponseDTO> responseDto = service.cancelAppointment(cancelRequestdto);
		assertEquals(responseDto.getErr().getMessage().toString(), exception.getMessage().toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected = AppointmentAlreadyCanceledException.class)
	public void AppointmentAlreadyCanceledExceptionTest() throws Exception {
		AppointmentAlreadyCanceledException exception = new AppointmentAlreadyCanceledException();
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
		PreRegistartionStatusDTO preRegistartionStatusDTO = new PreRegistartionStatusDTO();
		preRegistartionStatusDTO.setStatusCode(StatusCodes.Canceled.toString());
		preRegistartionStatusDTO.setPreRegistartionId("23587986034785");
		statusList.add(preRegistartionStatusDTO);

		PreRegResponseDto preRegResponse = new PreRegResponseDto();
		preRegResponse.setResponse(statusList);
		preRegResponse.setErr(null);
		preRegResponse.setStatus(false);

		ResponseEntity<PreRegResponseDto> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(PreRegResponseDto.class))).thenReturn(res);

		ResponseDto<CancelBookingResponseDTO> responseDto = service.cancelAppointment(cancelRequestdto);
		assertEquals(responseDto.getErr().getMessage().toString(), exception.getMessage().toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected = BookingDataNotFoundException.class)
	public void BookingDataNotFoundExceptionExceptionTest() throws Exception {
		BookingDataNotFoundException exception = new BookingDataNotFoundException();
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
		PreRegistartionStatusDTO preRegistartionStatusDTO = new PreRegistartionStatusDTO();
		preRegistartionStatusDTO.setStatusCode(StatusCodes.Booked.toString());
		preRegistartionStatusDTO.setPreRegistartionId("23587986034785");
		statusList.add(preRegistartionStatusDTO);

		PreRegResponseDto preRegResponse = new PreRegResponseDto();
		preRegResponse.setResponse(statusList);
		preRegResponse.setErr(null);
		preRegResponse.setStatus(false);

		ResponseEntity<PreRegResponseDto> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(PreRegResponseDto.class))).thenReturn(res);

		Mockito.when(bookingAvailabilityRepository.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
				LocalTime.parse("09:00"), LocalTime.parse("09:13"), LocalDate.parse("2018-12-04"), "1"))
				.thenReturn(entity);

		Mockito.when(registrationBookingRepository.existsByPreIdandStatusCode("23587986034785",
				StatusCodes.Booked.toString())).thenReturn(false);

		ResponseDto<CancelBookingResponseDTO> responseDto = service.cancelAppointment(cancelRequestdto);
		assertEquals(responseDto.getErr().getMessage().toString(), exception.getMessage().toString());
	}

	@Test
	public void mandatoryFieldCheckedSuccessTest() throws Exception {
		boolean flag = service.mandatoryParameterCheckforCancel(cancelbookingDto);
		assertEquals(flag, true);
	}

	@Test(expected = BookingPreIdNotFoundException.class)
	public void checkedPreIdFailureTest() throws Exception {

		cancelbookingDto.setPre_registration_id(null);
		service.mandatoryParameterCheckforCancel(cancelbookingDto);
	}

	@Test(expected = BookingRegistrationCenterIdNotFoundException.class)
	public void bookingRegistrationCenterIdNotFoundExceptionTest() throws Exception {

		cancelbookingDto.setRegistration_center_id(null);
		service.mandatoryParameterCheckforCancel(cancelbookingDto);
	}

	@Test(expected = BookingDateNotSeletectedException.class)
	public void bookingDateNotSeletectedExceptionTest() throws Exception {

		cancelbookingDto.setReg_date(null);
		service.mandatoryParameterCheckforCancel(cancelbookingDto);
	}

	@Test(expected = BookingTimeSlotNotSeletectedException.class)
	public void bookingTimeSlotNotSeletectedExceptionTest() throws Exception {

		cancelbookingDto.setSlotFromTime(null);
		cancelbookingDto.setSlotToTime(null);
		service.mandatoryParameterCheckforCancel(cancelbookingDto);
	}
	@Test(expected=BookingPreIdNotFoundException.class)
	public void bookingPreIdNotFoundExceptionTest() throws Exception{
		bookingRequestDTO.setPre_registration_id(null);
		service.mandatoryParameterCheck(bookingRequestDTO);
	}
	@Test(expected=BookingRegistrationCenterIdNotFoundException.class)
	public void oldBookingRegistrationCenterIdNotFoundExceptionTest() throws Exception{
		oldBooking.setRegistration_center_id(null);
		service.mandatoryParameterCheck(bookingRequestDTO);
	}
	@Test(expected=BookingTimeSlotNotSeletectedException.class)
	public void oldBookingTimeSlotNotSeletectedExceptionTest() throws Exception{
		oldBooking.setSlotFromTime(null);
		oldBooking.setSlotToTime(null);
		
		service.mandatoryParameterCheck(bookingRequestDTO);
	}
	
	
    @Test
    public void getAppointmentDetailsTest() {
    Mockito.when(registrationBookingRepository.findByPreId("23587986034785")).thenReturn(bookingEntity);
          ResponseDto<BookingRegistrationDTO> responseDto =service.getAppointmentDetails("23587986034785");
    assertEquals(responseDto.getResponse().getRegistration_center_id(),"1");
    }
    
    @Test(expected=BookingDataNotFoundException.class)
    public void getAppointmentDetailsTestFailure() {
    Mockito.when(registrationBookingRepository.findByPreId("23587986034785")).thenReturn(null);
          service.getAppointmentDetails("23587986034785");
    }
    
    @Test(expected=TablenotAccessibleException.class)
    public void getAppointmentDetailsTableFailure() {
          DataAccessLayerException exception = new DataAccessLayerException(ErrorCodes.PRG_BOOK_RCI_010.toString(),
                     ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.toString(),null);
    Mockito.when(registrationBookingRepository.findByPreId("23587986034785")).thenThrow(exception);
          service.getAppointmentDetails("23587986034785");
    }


}
