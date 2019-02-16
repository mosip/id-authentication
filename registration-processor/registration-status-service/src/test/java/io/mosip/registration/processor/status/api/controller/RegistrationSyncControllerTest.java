package io.mosip.registration.processor.status.api.controller;

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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.registration.processor.status.api.controller.RegistrationStatusController;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusRequestDTO;
import io.mosip.registration.processor.status.dto.RegistrationStatusSubRequestDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
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
public class RegistrationSyncControllerTest {

	/** The registration status controller. */
	@InjectMocks
	RegistrationSyncController registrationSyncController = new RegistrationSyncController();

	/** The registration status service. */
	@MockBean
	RegistrationStatusService<String, InternalRegistrationStatusDto,RegistrationStatusDto> registrationStatusService;

	/** The sync registration service. */
	@MockBean
	SyncRegistrationService<SyncResponseDto, SyncRegistrationDto> syncRegistrationService;

	/** The sync registration dto. */ 	
	@MockBean
	SyncRegistrationDto syncRegistrationDto;

	
	RegistrationStatusRequestDTO registrationStatusRequestDTO;
	/** The mock mvc. */
	@Autowired
	private MockMvc mockMvc;

	/** The list. */
	private List<SyncRegistrationDto> list;
	
	/** The SyncResponseDtoList. */
	private List<SyncResponseDto> syncResponseDtoList;

	/** The array to json. */
	private String arrayToJson;
	
	/** The ridValidator. */
	@MockBean
	private RidValidator<String> ridValidator;

	
	/**
	 * Sets the up.
	 * @throws JsonProcessingException
	 */
	@Before
	public void setUp() throws JsonProcessingException {
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
		
		SyncResponseDto syncResponseDto = new SyncResponseDto();
		
		syncResponseDto.setRegistrationId("1001");
		syncResponseDto.setParentRegistrationId("12334");
		syncResponseDto.setMessage("Registartion Id's are successfully synched in Sync table");
		syncResponseDto.setStatus("Success");
		
		syncResponseDtoList = new ArrayList<>();
		syncResponseDtoList.add(syncResponseDto);
}

	/**
	 * Test creation of A new project succeeds.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void syncRegistrationControllerSuccessTest() throws Exception {

		Mockito.when(syncRegistrationService.sync(ArgumentMatchers.any())).thenReturn(syncResponseDtoList);
		
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

		Mockito.when(syncRegistrationService.sync(ArgumentMatchers.any())).thenReturn(syncResponseDtoList);
		this.mockMvc.perform(post("/v0.1/registration-processor/registration-status/sync").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

}
