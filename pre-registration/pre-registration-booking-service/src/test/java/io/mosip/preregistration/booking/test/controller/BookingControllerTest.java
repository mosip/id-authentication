package io.mosip.preregistration.booking.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import io.mosip.preregistration.booking.dto.ResponseDto;
import io.mosip.preregistration.booking.service.BookingService;


@RunWith(SpringRunner.class)
@WebMvcTest(BookingController.class)
public class BookingControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookingService service;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	

	
	
	@Test
	public void getAvailability() throws Exception {
		ResponseDto<AvailabilityDto> response= new ResponseDto<>();
	   Mockito.when(service.getAvailability(Mockito.any())).thenReturn(response);
	   RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/book/availability")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("RegCenterId", "1");
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	public void saveAvailability() throws Exception {
		ResponseDto<String> response= new ResponseDto<>();
	   Mockito.when(service.addAvailability()).thenReturn(response);
	   RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/book/masterSync")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

}
