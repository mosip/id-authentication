package io.mosip.registration.processor.status.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.mosip.registration.processor.status.RegistrationStatusApplication;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.service.SyncRegistrationService;

/**
 * The Class SyncRegistrationControllerTest.
 * @author M1047487
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RegistrationStatusApplication.class)
@ContextConfiguration(classes = { RegistrationStatusApplication.class })
@WebAppConfiguration
@EnableWebMvc
public class SyncRegistrationControllerTest {

	/** The mock mvc. */
	private MockMvc mockMvc;

	/** The sync registration controller. */
	@InjectMocks
	SyncRegistrationController syncRegistrationController = new SyncRegistrationController();

	/** The sync registration service. */
	@MockBean
	SyncRegistrationService<SyncRegistrationDto> syncRegistrationService;
	
	/** The sync registration dto. */
	@MockBean
	SyncRegistrationDto syncRegistrationDto;

	/** The wac. */
	@Autowired
	private WebApplicationContext wac;
	
	/** The list. */
	private List<SyncRegistrationDto> list;
	
	/** The array to json. */
	private String arrayToJson;

	/**
	 * Sets the up.
	 *
	 * @throws JsonProcessingException the json processing exception
	 */
	@Before
	public void setUp() throws JsonProcessingException {
		
		list = new ArrayList<>();
		SyncRegistrationDto syncRegistrationDto = new SyncRegistrationDto();
        syncRegistrationDto = new SyncRegistrationDto();
        syncRegistrationDto.setSyncRegistrationId("1001");
        syncRegistrationDto.setRegistrationId("1002");
        syncRegistrationDto.setLangCode("eng");
        syncRegistrationDto.setIsActive(true);
        syncRegistrationDto.setCreatedBy("MOSIP_SYSTEM");
		list.add(syncRegistrationDto);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		arrayToJson = objectMapper.writeValueAsString(list);
		
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	/**
	 * Test creation of A new project succeeds.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testCreationOfANewProjectSucceeds() throws Exception {

		Mockito.when(syncRegistrationService.sync(ArgumentMatchers.any())).thenReturn(list);
		mockMvc.perform(post("/v0.1/registration-processor/registration-status/sync").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(arrayToJson)).andExpect(status().isOk());
	}

}
