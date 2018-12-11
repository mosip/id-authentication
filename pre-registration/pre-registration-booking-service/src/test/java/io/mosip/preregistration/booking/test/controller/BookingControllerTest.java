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
import io.mosip.preregistration.booking.dto.BookingDTO;
import io.mosip.preregistration.booking.dto.BookingRegistrationDTO;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.booking.dto.RequestDto;
import io.mosip.preregistration.booking.dto.ResponseDto;
import io.mosip.preregistration.booking.service.BookingService;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@RunWith(SpringRunner.class)
@WebMvcTest(BookingController.class)
public class BookingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookingService service;

	private AvailabilityDto availabilityDto;

	BookingDTO bookingDTO = new BookingDTO();
	List<BookingRequestDTO> bookingList=new ArrayList<>();
	BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
	BookingRegistrationDTO oldBooking= new BookingRegistrationDTO();
	BookingRegistrationDTO newBooking= new BookingRegistrationDTO();
	Timestamp resTime = new Timestamp(System.currentTimeMillis());
	@SuppressWarnings("rawtypes")
	ResponseDto responseDto = new ResponseDto();
	private Object jsonObject = null;
	
	private Object jsonObject1 = null;
	CancelBookingResponseDTO cancelBookingResponseDTO=new CancelBookingResponseDTO();
	CancelBookingDTO cancelbookingDto=new CancelBookingDTO();
	RequestDto<CancelBookingDTO> dto=new RequestDto<>();

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

		bookingRequestDTO.setPre_registration_id("23587986034785");
		bookingRequestDTO.setNewBookingDetails(new BookingRegistrationDTO());
		bookingRequestDTO.setOldBookingDetails(new BookingRegistrationDTO());
//		bookingRequestDTOA.setSlotFromTime("09:00");
//		bookingRequestDTOA.setSlotToTime("09:13");
//		bookingRequestDTOA.setReg_date("2018-12-06");


		//bookingRequestDTOB.setPre_registration_id("31496715428069");
//		bookingRequestDTOB.setRegistration_center_id("1");
//		bookingRequestDTOB.setSlotFromTime("09:00");
//		bookingRequestDTOB.setSlotToTime("09:13");
//		bookingRequestDTOB.setReg_date("2018-12-06");


		bookingDTO.setRequest(bookingList);

		responseDto.setErr(null);
		
		URI cancelUri = new URI(
				classLoader.getResource("cancelAppointment.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File file1 = new File(cancelUri.getPath());
		jsonObject1 = parser.parse(new FileReader(file1));
		
		cancelbookingDto.setPre_registration_id("12345");
		cancelbookingDto.setRegistration_center_id("2");
		cancelbookingDto.setSlotFromTime("09:00");
		cancelbookingDto.setSlotToTime("09:20");
		String restime="2018-12-04T07:22:57.086+0000";
		cancelbookingDto.setReg_date(restime);
		
		dto.setRequest(cancelbookingDto);
	}

	@Test
	public void getAvailability() throws Exception {
		ResponseDto<AvailabilityDto> response = new ResponseDto<>();
		Mockito.when(service.getAvailability(Mockito.any())).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/booking/availability")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("RegCenterId", "1");
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	public void saveAvailability() throws Exception {
		ResponseDto<String> response = new ResponseDto<>();
		Mockito.when(service.addAvailability()).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/booking/masterSync")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	

	@SuppressWarnings("unchecked")
	@Test
	public void successBookingTest() throws Exception {

		responseDto.setStatus(true);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
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
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
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


}
