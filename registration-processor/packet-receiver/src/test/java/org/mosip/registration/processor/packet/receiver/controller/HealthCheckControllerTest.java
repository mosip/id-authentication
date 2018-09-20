package org.mosip.registration.processor.packet.receiver.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.registration.processor.packet.receiver.controller.HealthCheckController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@WebMvcTest(HealthCheckController.class)
public class HealthCheckControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void shouldReturnHealthMessage() throws Exception {
		mockMvc.perform(get("/health")).andExpect(status().isOk());
	}

}
