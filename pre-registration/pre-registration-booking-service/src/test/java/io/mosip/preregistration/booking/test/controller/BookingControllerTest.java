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
		oldBooking.setRegistration_center_id("1");
		oldBooking.setSlotFromTime("09:00");
		oldBooking.setSlotToTime("09:13");
		oldBooking.setReg_date("2018-12-06");

		newBooking.setRegistration_center_id("1");
		newBooking.setSlotFromTime("09:00");
		newBooking.setSlotToTime("09:13");
		newBooking.setReg_date("2018-12-06");

		bookingDTO.setRequest(bookingList);

		responseDto.setErr(null);
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

}
