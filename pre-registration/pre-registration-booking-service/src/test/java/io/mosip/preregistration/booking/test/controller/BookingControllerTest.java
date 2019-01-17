package io.mosip.preregistration.booking.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.mosip.preregistration.booking.controller.BookingController;
import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.booking.dto.MainRequestDTO;
import io.mosip.preregistration.booking.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.booking.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.booking.service.BookingService;
import io.mosip.preregistration.booking.service.util.BookingServiceUtil;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.MainListRequestDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
/**
 * Booking Controller Test
 * 
 * @author Sanober Noor
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest(BookingController.class)
public class BookingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookingService service;

	@MockBean
	private BookingServiceUtil serviceUtil;

	private AvailabilityDto availabilityDto;

	MainListRequestDTO bookingDTO = new MainListRequestDTO();
	List<BookingRequestDTO> bookingList = new ArrayList<>();
	BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
	BookingRegistrationDTO oldBooking = new BookingRegistrationDTO();
	BookingRegistrationDTO newBooking = new BookingRegistrationDTO();
	Timestamp resTime = new Timestamp(System.currentTimeMillis());
	@SuppressWarnings("rawtypes")
	MainResponseDTO responseDto = new MainResponseDTO();
	private Object jsonObject = null;

	private Object jsonObject1 = null;
	CancelBookingResponseDTO cancelBookingResponseDTO=new CancelBookingResponseDTO();
	CancelBookingDTO cancelbookingDto=new CancelBookingDTO();
	MainRequestDTO<CancelBookingDTO> dto=new MainRequestDTO<>();
	MainRequestDTO<PreRegIdsByRegCenterIdDTO> requestDTO=new MainRequestDTO<>();
	PreRegIdsByRegCenterIdResponseDTO preRegIdsResponseDTO=new PreRegIdsByRegCenterIdResponseDTO();
	List<PreRegIdsByRegCenterIdResponseDTO> respList = new ArrayList<>();
	PreRegIdsByRegCenterIdDTO preRegIdsByRegCenterIdDTO=new PreRegIdsByRegCenterIdDTO();


	@SuppressWarnings({ "deprecation" })
	@Before
	public void setup() throws FileNotFoundException, ParseException, URISyntaxException {
		availabilityDto = new AvailabilityDto();
		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();

		URI dataSyncUri = new URI(
				classLoader.getResource("booking.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File file = new File(dataSyncUri.getPath());
		jsonObject = parser.parse(new FileReader(file));

		bookingRequestDTO.setPreRegistrationId("23587986034785");
		bookingRequestDTO.setNewBookingDetails(new BookingRegistrationDTO());
		bookingRequestDTO.setOldBookingDetails(new BookingRegistrationDTO());
		// bookingRequestDTOB.setPre_registration_id("31496715428069");
		// bookingRequestDTOB.setRegistration_center_id("1");
		// bookingRequestDTOB.setSlotFromTime("09:00");
		// bookingRequestDTOB.setSlotToTime("09:13");
		// bookingRequestDTOB.setReg_date("2018-12-06");

		bookingDTO.setRequest(bookingList);
		
		
		responseDto.setErr(null);

		URI cancelUri = new URI(
				classLoader.getResource("cancelAppointment.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File file1 = new File(cancelUri.getPath());
		jsonObject1 = parser.parse(new FileReader(file1));

		cancelbookingDto.setPreRegistrationId("12345");
		cancelbookingDto.setRegistrationCenterId("2");
		cancelbookingDto.setSlotFromTime("09:00");
		cancelbookingDto.setSlotToTime("09:20");
		String restime = "2018-12-04T07:22:57.086+0000";
		cancelbookingDto.setRegDate(restime);

		dto.setRequest(cancelbookingDto);
		requestDTO.setRequest(preRegIdsByRegCenterIdDTO);
		List<String> respList = new ArrayList<>();
		respList.add("Reterived all pre-registration ids successfully");
		preRegIdsResponseDTO.setRegistrationCenterId("1");
		preRegIdsResponseDTO.setPreRegistrationIds(respList);
	}

	@Test
	public void getAvailability() throws Exception {
		MainResponseDTO<AvailabilityDto> response = new MainResponseDTO<>();
		Mockito.when(service.getAvailability(Mockito.any())).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/booking/availability")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("registration_center_id", "1");
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	public void saveAvailability() throws Exception {
		MainResponseDTO<String> response = new MainResponseDTO<>();
		Mockito.when(service.addAvailability()).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/v0.1/pre-registration/booking/masterSynchronization")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void successBookingTest() throws Exception {

		responseDto.setStatus(true);
		responseDto.setResTime(serviceUtil.getCurrentResponseTime());
		List<String> respList = new ArrayList<>();
		respList.add("APPOINTMENT_SUCCESSFULLY_BOOKED");
		responseDto.setResponse(respList);

		Mockito.when(service.bookAppointment(bookingDTO)).thenReturn(responseDto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/booking/book")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObject.toString());

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void failureBookingTest() throws Exception {

		responseDto.setStatus(false);
		bookingDTO.setRequest(null);
		Mockito.when(service.bookAppointment(bookingDTO)).thenReturn(responseDto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/booking/book")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObject.toString());

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void cancelAppointmentSuccessTest() throws Exception {

		responseDto.setErr(null);
		responseDto.setStatus(true);
		responseDto.setResTime(serviceUtil.getCurrentResponseTime());
		cancelBookingResponseDTO.setMessage("APPOINTMENT_SUCCESSFULLY_CANCELED");
		cancelBookingResponseDTO.setTransactionId("375765");
		responseDto.setResponse(cancelBookingResponseDTO);

		Mockito.when(service.cancelAppointment(dto)).thenReturn(responseDto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/v0.1/pre-registration/booking/book")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObject1.toString());

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void cancelAppointmentFailureTest() throws Exception {

		responseDto.setStatus(false);
		dto.setRequest(null);
		Mockito.when(service.cancelAppointment(dto)).thenReturn(responseDto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/v0.1/pre-registration/booking/book")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObject1.toString());

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	public void getAppointmentDetails() throws Exception {
		MainResponseDTO<BookingRegistrationDTO> response=new MainResponseDTO<>();
		Mockito.when(service.getAppointmentDetails("12345")).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/booking//appointmentDetails")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("pre_registration_id", "12345");
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	public void getPreIdsByRegCenterId() throws Exception {
		MainListResponseDTO<PreRegIdsByRegCenterIdResponseDTO> response=new MainListResponseDTO<>();
response.setErr(null);
response.setStatus(false);
response.setResTime(serviceUtil.getCurrentResponseTime());

		response.setResponse(respList);

		Mockito.when(service.getPreIdsByRegCenterId(requestDTO)).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/booking/bookedPreIdsByRegId")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObject1.toString());

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

}
