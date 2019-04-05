package io.mosip.registrationprocessor.eis;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.mosip.registrationprocessor.eis.controller.ExternalController;
import io.mosip.registrationprocessor.eis.entity.MessageRequestDTO;

@RunWith(SpringRunner.class)
@WebMvcTest(ExternalController.class)
public class ExternalIntegrationControllerTest {

	@Autowired
	private MockMvc mockMvc;
	MessageRequestDTO messageRequestDTO=null;
	String arrayToJson;
	@Before
	public void setup() throws JsonProcessingException {
		messageRequestDTO=new MessageRequestDTO();
		messageRequestDTO.setId("io.mosip.registrationprocessor");
		messageRequestDTO.setRequesttime(LocalDateTime.now().toString());
		messageRequestDTO.setVersion("1.0");
		
		messageRequestDTO.setRequest(Arrays.asList("robb","3142"));
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		arrayToJson = objectMapper.writeValueAsString(messageRequestDTO);
	}
	@Test
	public void dummyControllerSuccessTest() throws Exception {

		MvcResult result=mockMvc.perform(post("/registration-processor/external-integration-service/v1.0")
				.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON).content(arrayToJson))
				.andExpect(status().isOk()).andReturn();
		String success=result.getResponse().getContentAsString();
		assertEquals("true", success);
	}
	@Test
	public void dummyControllerControllerFailureTest() throws Exception {

		mockMvc.perform(post("/registration-processor/external-integration-service/v1.0")
				.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON).content(""))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	@Test
	public void dummyControllernegativeresponseTest() throws Exception {
		messageRequestDTO.setRequest(null);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		arrayToJson = objectMapper.writeValueAsString(messageRequestDTO);
		MvcResult result=mockMvc.perform(post("/registration-processor/external-integration-service/v1.0")
				.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON).content(arrayToJson))
				.andExpect(status().isOk()).andReturn();
		String failure=result.getResponse().getContentAsString();
		assertEquals("false", failure);
	}
}
