package io.mosip.registration.processor.status.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;

/**
 * The Class RegistrationStatusControllerTest.
 * 
 * @author M1047487
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationStatusControllerTest {

	/** The registration status controller. */
	@InjectMocks
	RegistrationStatusController registrationStatusController = new RegistrationStatusController();

	/** The registration status service. */
	@MockBean
	RegistrationStatusService<String, InternalRegistrationStatusDto,RegistrationStatusDto> registrationStatusService;

	/** The sync registration service. */
	@MockBean
	SyncRegistrationService<SyncRegistrationDto> syncRegistrationService;

	/** The sync registration dto. */
	@MockBean
	SyncRegistrationDto syncRegistrationDto;

	/** The mock mvc. */
	@Autowired
	private MockMvc mockMvc;

	/** The registration ids. */
	private String registrationIds;

	/** The registration dto list. */
	private List<InternalRegistrationStatusDto> registrationDtoList;

	/** The webApplicationContext. */
	@Autowired
	private WebApplicationContext webApplicationContext;

	/** The list. */
	private List<SyncRegistrationDto> list;

	/** The array to json. */
	private String arrayToJson;

	/**
	 * Sets the up.
	 * @throws JsonProcessingException
	 */
	@Before
	public void setUp() throws JsonProcessingException {

		registrationIds = "1001,1002";
		registrationDtoList = new ArrayList<>();
		InternalRegistrationStatusDto registrationStatusDto1 = new InternalRegistrationStatusDto();
		registrationStatusDto1.setRegistrationId("1001");
		registrationStatusDto1.setRegistrationType("NEW");
		registrationStatusDto1.setLangCode("EN");
		registrationStatusDto1.setIsActive(true);
		registrationStatusDto1.setCreatedBy("MOSIP_SYSTEM");

		InternalRegistrationStatusDto registrationStatusDto2 = new InternalRegistrationStatusDto();
		registrationStatusDto2.setRegistrationId("1002");
		registrationStatusDto2.setRegistrationType("NEW");
		registrationStatusDto2.setLangCode("EN");
		registrationStatusDto2.setIsActive(true);
		registrationStatusDto2.setCreatedBy("MOSIP_SYSTEM");

		registrationDtoList.add(registrationStatusDto1);
		registrationDtoList.add(registrationStatusDto2);

		list = new ArrayList<>();
		SyncRegistrationDto syncRegistrationDto = new SyncRegistrationDto();
        syncRegistrationDto = new SyncRegistrationDto();
        syncRegistrationDto.setRegistrationId("1002");
        syncRegistrationDto.setLangCode("eng");
        syncRegistrationDto.setIsActive(true);
		list.add(syncRegistrationDto);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		arrayToJson = objectMapper.writeValueAsString(list);

	

		Mockito.doReturn(registrationDtoList).when(registrationStatusService).getByIds(ArgumentMatchers.any());

	}

	/**
	 * Search success test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void searchSuccessTest() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/v0.1/registration-processor/registration-status/registrationstatus")
				.param("registrationIds", registrationIds).accept(MediaType.ALL_VALUE).contentType(MediaType.ALL_VALUE))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	/**
	 * Search failure test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void searchFailureTest() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/v0.1/registration-processor/registration-status/registrationstatus").accept(MediaType.ALL_VALUE).contentType(MediaType.ALL_VALUE))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	/**
	 * Test creation of A new project succeeds.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void syncRegistrationControllerSuccessTest() throws Exception {

		Mockito.when(syncRegistrationService.sync(ArgumentMatchers.any())).thenReturn(list);
		this.mockMvc.perform(post("/v0.1/registration-processor/registration-status/sync").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(arrayToJson)).andExpect(status().isOk());
	}

	/**
	 * Sync registration controller failure check.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void syncRegistrationControllerFailureTest() throws Exception {

		Mockito.when(syncRegistrationService.sync(ArgumentMatchers.any())).thenReturn(list);
		this.mockMvc.perform(post("/v0.1/registration-processor/registration-status/sync").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

}
