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

import io.mosip.preregistration.booking.code.StatusCodes;
import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.BookingDTO;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.dto.DateTimeDto;
import io.mosip.preregistration.booking.dto.ResponseDto;
import io.mosip.preregistration.booking.dto.SlotDto;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingPK;
import io.mosip.preregistration.booking.repository.BookingAvailabilityRepository;
import io.mosip.preregistration.booking.repository.RegistrationBookingRepository;
import io.mosip.preregistration.booking.service.BookingService;
import io.mosip.preregistration.core.exceptions.InvalidRequestParameterException;
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
	RegistrationBookingRepository registrationBookingRepository;

	@MockBean
	RestTemplateBuilder restTemplateBuilder;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BookingService service;

	private AvailabilityDto availability = new AvailabilityDto();
	private List<DateTimeDto> dateList = new ArrayList<>();
	private DateTimeDto dateDto = new DateTimeDto();
	private List<SlotDto> slotsList = new ArrayList<>();
	private AvailibityEntity entity = new AvailibityEntity();
	private SlotDto slots = new SlotDto();

	BookingDTO bookingDTO = new BookingDTO();
	List<BookingRequestDTO> bookingList = new ArrayList<>();
	BookingRequestDTO bookingRequestDTOA = new BookingRequestDTO();
	BookingRequestDTO bookingRequestDTOB = new BookingRequestDTO();
	public ResponseDto<List<BookingStatusDTO>> responseDto = new ResponseDto<>();
	BookingStatusDTO statusDTOA = new BookingStatusDTO();
	BookingStatusDTO statusDTOB = new BookingStatusDTO();
	Map<String, String> requiredRequestMap = new HashMap<>();
	InvalidRequestParameterException parameterException = null;
	Map<String, String> requestMap = new HashMap<>();
	RegistrationBookingEntity bookingEntity = new RegistrationBookingEntity();
	RegistrationBookingPK bookingPK = new RegistrationBookingPK();

	@Value("${version}")
	String versionUrl;

	@Value("${id}")
	String idUrl;

	@Value("${preRegResourceUrl}")
	private String preRegResourceUrl;

	@Before
	public void setup() throws URISyntaxException, FileNotFoundException, ParseException {

		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String date1 = "2016-11-09 09:00:00";
		String date2 = "2016-11-09 09:20:00";
		LocalDateTime localDateTime1 = LocalDateTime.parse(date1, format);
		LocalDateTime localDateTime2 = LocalDateTime.parse(date2, format);
		LocalTime localTime1 = localDateTime1.toLocalTime();
		LocalTime localTime2 = localDateTime2.toLocalTime();
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

		entity.setAvailabilityNo(4);
		entity.setRegcntrId("1");
		entity.setRegDate("2018-12-04");
		entity.setToTime(localTime2);
		entity.setIsActive(true);
		entity.setFromTime(localTime1);

		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();

		URI dataSyncUri = new URI(
				classLoader.getResource("booking.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File file = new File(dataSyncUri.getPath());
		parser.parse(new FileReader(file));

		bookingRequestDTOA.setPre_registration_id("23587986034785");
		bookingRequestDTOA.setRegistration_center_id("1");
		bookingRequestDTOA.setSlotFromTime("09:00");
		bookingRequestDTOA.setSlotToTime("09:13");
		bookingRequestDTOA.setReg_date("2018-12-06");

		bookingRequestDTOB.setPre_registration_id("31496715428069");
		bookingRequestDTOB.setRegistration_center_id("1");
		bookingRequestDTOB.setSlotFromTime("09:00");
		bookingRequestDTOB.setSlotToTime("09:13");
		bookingRequestDTOB.setReg_date("2018-12-06");

		bookingList.add(bookingRequestDTOA);
		bookingList.add(bookingRequestDTOB);

		bookingDTO.setReqTime("2018-12-06T07:22:57.086");
		bookingDTO.setRequest(bookingList);
		bookingDTO.setId("mosip.pre-registration.booking.book");
		bookingDTO.setVer("1.0");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
		bookingPK.setBookingDateTime(LocalDateTime.parse(bookingDTO.getReqTime(), formatter));

		bookingEntity.setBookingPK(bookingPK);
		bookingEntity.setRegistrationCenterId(bookingRequestDTOA.getRegistration_center_id());
		bookingEntity.setStatus_code(StatusCodes.Booked.toString().trim());
		bookingEntity.setLang_code("12L");
		bookingEntity.setCrBy("987654321");
		bookingEntity.setCrDate(LocalDateTime.parse(bookingDTO.getReqTime()));
		bookingEntity.setRegDate(LocalDate.parse(bookingRequestDTOA.getReg_date()));
		bookingEntity.setSlotFromTime(LocalTime.parse(bookingRequestDTOA.getSlotFromTime()));
		bookingEntity.setSlotToTime(LocalTime.parse(bookingRequestDTOA.getSlotToTime()));

		statusDTOA.setBooking_status(StatusCodes.Booked.toString());
		statusDTOA.setPre_registration_id(bookingRequestDTOA.getPre_registration_id());
		statusDTOA.setBooking_message("APPOINTMENT_SUCCESSFULLY_BOOKED");

		statusDTOB.setBooking_status(StatusCodes.Booked.toString());
		statusDTOB.setPre_registration_id(bookingRequestDTOB.getPre_registration_id());
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

	}

	@Test
	public void getAvailabilityTest() {
		logger.info("Availability dto " + availability);
		List<String> date = new ArrayList<>();
		List<AvailibityEntity> entityList = new ArrayList<>();
		date.add("2018-12-04");
		entityList.add(entity);
		logger.info("Availability entity " + entity);
		Mockito.when(bookingAvailabilityRepository.findDate(Mockito.anyString())).thenReturn(date);
		Mockito.when(bookingAvailabilityRepository.findByRegcntrIdAndRegDate(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(entityList);
		ResponseDto<AvailabilityDto> responseDto = service.getAvailability("1");
		logger.info("Response " + responseDto);
		assertEquals(responseDto.getResponse().getRegCenterId(), "1");

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void successBook() {
		List<AvailibityEntity> entityList = new ArrayList<>();
		entity.setAvailabilityNo(4);
		entity.setRegcntrId("1");
		entity.setRegDate("2018-12-07");
		entity.setToTime(LocalTime.parse("09:13"));
		entity.setIsActive(true);
		entity.setFromTime(LocalTime.parse("09:00"));

		parameterException = ValidationUtil.requestValidator(requestMap, requiredRequestMap);

		Mockito.when(bookingAvailabilityRepository.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
				LocalTime.parse("09:00"), LocalTime.parse("09:13"), "2018-12-07", "1")).thenReturn(entity);

		Mockito.when(registrationBookingRepository.existsByPreIdandStatusCode("23587986034785",
				StatusCodes.Booked.toString())).thenReturn(true);

		Mockito.when(registrationBookingRepository.save(bookingEntity)).thenReturn(bookingEntity);

		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		ResponseEntity<ResponseDto> resp = new ResponseEntity<>(HttpStatus.OK);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
				Mockito.eq(ResponseDto.class))).thenReturn(resp);

		entity.setAvailabilityNo(entity.getAvailabilityNo() - 1);
		Mockito.when(bookingAvailabilityRepository.update(entity)).thenReturn(entity);

		// ResponseDto<List<BookingStatusDTO>> result =
		// service.bookAppointment(bookingDTO);
		// assertEquals(responseDto, result);
	}
}
