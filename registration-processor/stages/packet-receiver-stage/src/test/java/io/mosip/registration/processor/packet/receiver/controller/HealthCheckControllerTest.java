package io.mosip.registration.processor.packet.receiver.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.registration.processor.packet.receiver.stage.PacketReceiverStage;

@RunWith(SpringRunner.class)
@WebMvcTest(HealthCheckController.class)
public class HealthCheckControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	PacketReceiverStage packetReceiverStage;

	@Test
	public void shouldReturnHealthMessage() throws Exception {
		mockMvc.perform(get("/health")).andExpect(status().isOk());
	}

}
