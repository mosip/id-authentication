
package io.mosip.preregistration.application.test.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BeanPropertyBindingResult;

import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.preregistration.application.DemographicTestApplication;
import io.mosip.preregistration.application.controller.DemographicController;
import io.mosip.preregistration.booking.serviceimpl.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.core.common.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.core.util.RequestValidator;
import io.mosip.preregistration.demographic.dto.DeletePreRegistartionDTO;
import io.mosip.preregistration.demographic.dto.DemographicCreateResponseDTO;
import io.mosip.preregistration.demographic.dto.DemographicMetadataDTO;
import io.mosip.preregistration.demographic.dto.DemographicRequestDTO;
import io.mosip.preregistration.demographic.dto.DemographicUpdateResponseDTO;
import io.mosip.preregistration.demographic.dto.DemographicViewDTO;
import io.mosip.preregistration.demographic.service.DemographicServiceIntf;
import io.mosip.preregistration.document.service.DocumentServiceIntf;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

/**
 * Test class to test the PreRegistration Controller methods
 * 
 * @author Rajath KR
 * @author Sanober Noor
 * @author Tapaswini Bahera
 * @author Jagadishwari S
 * @author Ravi C Balaji
 * @since 1.0.0
 * 
 */
@SpringBootTest(classes = { DemographicTestApplication.class })
// @ContextConfiguration(classes = { DemographicTestApplication.class })
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc

public class DemographicControllerTest {

	/**
	 * Autowired reference for {@link #MockMvc}
	 */
	@Autowired
	private MockMvc mockMvc;

	@Mock
	private RequestValidator requestValidator;

	String preRegistrationId = "";

	/**
	 * Creating Mock Bean for DemographicService
	 */
	@MockBean
	private DemographicServiceIntf preRegistrationService;

	@MockBean
	private DocumentServiceIntf documentServiceIntf;

	@MockBean
	private BookingServiceIntf bookingServiceIntf;

	@MockBean
	private PridGenerator<String> pridGenerator;

	@Mock
	private AuthUserDetails authUserDetails;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Object jsonObject = null;

	@Autowired
	private DemographicController controller;

	String userId = "";
	MainRequestDTO<DemographicRequestDTO> reqDto = new MainRequestDTO<>();

	/**
	 * @throws FileNotFoundException
	 *             when file not found
	 * @throws IOException
	 *             on input error
	 * @throws ParseException
	 *             on json parsing error
	 */
	@Before
	public void setup() throws FileNotFoundException, IOException, ParseException {
		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();
		File file = new File(classLoader.getResource("pre-registration.json").getFile());
		jsonObject = parser.parse(new FileReader(file));
		preRegistrationId = "98746563542672";
		userId = "9988905333";
	}

	/**
	 * @throws Exception
	 *             on error
	 */
	@WithUserDetails("INDIVIDUAL")
	@Test
	public void successSave() throws Exception {
		logger.info("----------Successful save of application-------");
		MainResponseDTO<DemographicCreateResponseDTO> response = new MainResponseDTO<>();
		List<DemographicCreateResponseDTO> saveList = new ArrayList<>();
		DemographicCreateResponseDTO createDto = new DemographicCreateResponseDTO();

		MainRequestDTO<DemographicRequestDTO> request = new MainRequestDTO<>();
		DemographicRequestDTO demo = new DemographicRequestDTO();
		request.setRequest(demo);

		createDto.setPreRegistrationId("98746563542672");
		// saveList.add(createDto);
		DemographicRequestDTO req= new DemographicRequestDTO();
		req.setLangCode("eng");
		response.setResponse(createDto);
		reqDto.setId("mosip.pre-registration.demographic.create");
		reqDto.setVersion("1.0");
		reqDto.setRequesttime(new Date());
		reqDto.setRequest(req);
		Mockito.when(preRegistrationService.addPreRegistration(Mockito.any())).thenReturn(response);
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(reqDto,
				"MainRequestDTO<DemographicRequestDTO>");
		ResponseEntity<MainResponseDTO<DemographicCreateResponseDTO>> responseEntity = controller.register(reqDto,
				errors);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}


	/**
	 * @throws Exception
	 *             on error
	 */

	@Test
	@WithUserDetails("INDIVIDUAL")
	public void successUpdate() throws Exception {
		logger.info("----------Successful save of application-------");

		MainResponseDTO<DemographicUpdateResponseDTO> response = new MainResponseDTO<>();
		List<DemographicUpdateResponseDTO> saveList = new ArrayList<>();
		DemographicUpdateResponseDTO createDto = new DemographicUpdateResponseDTO();
		createDto.setPreRegistrationId("98746563542672");
		preRegistrationId = "98746563542672";
		// saveList.add(createDto);
		response.setResponse(createDto);

		MainRequestDTO<DemographicRequestDTO> request = new MainRequestDTO<>();
		DemographicRequestDTO demo = new DemographicRequestDTO();
		preRegistrationId = "98746563542672";
		request.setRequest(demo);
		Mockito.when(preRegistrationService.authUserDetails()).thenReturn(authUserDetails);
		Mockito.when(authUserDetails.getUserId()).thenReturn(userId);
		Mockito.when(preRegistrationService.updatePreRegistration(request, preRegistrationId, userId))
				.thenReturn(response);

		DemographicRequestDTO req= new DemographicRequestDTO();
		req.setLangCode("eng");
		response.setResponse(createDto);
		reqDto.setId("mosip.pre-registration.demographic.update");
		reqDto.setVersion("1.0");
		reqDto.setRequesttime(new Date());
		reqDto.setRequest(req);
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(reqDto,
				"MainRequestDTO<DemographicRequestDTO>");
		ResponseEntity<MainResponseDTO<DemographicUpdateResponseDTO>> responseEntity = controller.update(preRegistrationId,reqDto,
				errors);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	/**
	 * @throws Exception
	 *             on error
	 */
	@WithUserDetails("INDIVIDUAL")
	@Test
	public void getAllApplicationTest() throws Exception {
		MainResponseDTO<DemographicMetadataDTO> response = new MainResponseDTO<>();
		List<DemographicViewDTO> viewList = new ArrayList<>();
		DemographicMetadataDTO demographicMetadataDTO = new DemographicMetadataDTO();
		DemographicViewDTO viewDto = new DemographicViewDTO();
		viewDto.setPreRegistrationId("1234");
		viewDto.setStatusCode("Pending_Appointment");
		viewList.add(viewDto);
		demographicMetadataDTO.setBasicDetails(viewList);
		response.setResponse(demographicMetadataDTO);
		Mockito.when(preRegistrationService.authUserDetails()).thenReturn(authUserDetails);
		Mockito.when(authUserDetails.getUserId()).thenReturn(userId);
		Mockito.when(preRegistrationService.getAllApplicationDetails(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/applications")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());

	}

	/**
	 * @throws Exception
	 *             on error
	 */
	@WithUserDetails("INDIVIDUAL")
	@Test
	public void getApplicationStatusTest() throws Exception {
		String preId = "14532456789";
		MainResponseDTO<PreRegistartionStatusDTO> response = new MainResponseDTO<>();
		List<PreRegistartionStatusDTO> statusList = new ArrayList<PreRegistartionStatusDTO>();
		PreRegistartionStatusDTO statusDto = new PreRegistartionStatusDTO();
		statusDto.setPreRegistartionId(preId);
		statusDto.setStatusCode("Pending_Appointment");
		// statusList.add(statusDto);
		response.setResponse(statusDto);
		Mockito.when(preRegistrationService.authUserDetails()).thenReturn(authUserDetails);
		Mockito.when(authUserDetails.getUserId()).thenReturn(userId);
		Mockito.when(preRegistrationService.getApplicationStatus(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/applications/status/{preRegistrationId}", preId)
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception
	 *             on error
	 */
	@WithUserDetails("INDIVIDUAL")
	@Test
	public void discardIndividualTest() throws Exception {
		String preId = "3";
		MainResponseDTO<DeletePreRegistartionDTO> response = new MainResponseDTO<>();
		List<DeletePreRegistartionDTO> DeleteList = new ArrayList<DeletePreRegistartionDTO>();
		DeletePreRegistartionDTO deleteDto = new DeletePreRegistartionDTO();

		deleteDto.setPreRegistrationId("3");
		deleteDto.setDeletedBy(userId);
		// DeleteList.add(deleteDto);
		response.setResponse(deleteDto);
		Mockito.when(preRegistrationService.authUserDetails()).thenReturn(authUserDetails);
		Mockito.when(authUserDetails.getUserId()).thenReturn(userId);
		Mockito.when(preRegistrationService.deleteIndividual("3", userId)).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/applications/{preRegistrationId}", preId)
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception
	 *             on error
	 */
	@WithUserDetails("INDIVIDUAL")
	@Test
	public void getApplicationSuccessTest() throws Exception {
		MainResponseDTO<DemographicResponseDTO> response = new MainResponseDTO<>();
		DemographicResponseDTO createDto = new DemographicResponseDTO();

		createDto.setPreRegistrationId("98746563542672");
		response.setResponse(createDto);
		Mockito.when(preRegistrationService.authUserDetails()).thenReturn(authUserDetails);
		Mockito.when(authUserDetails.getUserId()).thenReturn(userId);
		Mockito.when(preRegistrationService.getDemographicData("98746563542672")).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/applications/{preRegistrationId}", createDto.getPreRegistrationId())
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception
	 *             on error
	 */
	@WithUserDetails("INDIVIDUAL")
	@Test
	public void updateApplicationStatusTest() throws Exception {
		MainResponseDTO<String> response = new MainResponseDTO<>();
		response.setErrors(null);
		response.setResponse("Status Updated sucessfully");
		// response.setResTime(new Timestamp(System.currentTimeMillis()));
		Mockito.when(preRegistrationService.authUserDetails()).thenReturn(authUserDetails);
		Mockito.when(authUserDetails.getUserId()).thenReturn(userId);
		Mockito.when(preRegistrationService.updatePreRegistrationStatus("1234", "Booked", userId)).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/applications/status/{preRegistrationId}", "1234")
				.contentType(MediaType.ALL).characterEncoding("UTF-8").accept(MediaType.ALL)
				.param("statusCode", "Booked");

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception
	 *             on error
	 */
	@WithUserDetails("INDIVIDUAL")
	@Test
	public void getUpdatedDateTimeTest() throws Exception {
		MainRequestDTO<PreRegIdsByRegCenterIdDTO> mainRequestDTO = new MainRequestDTO<>();
		List<String> list = new ArrayList<>();
		list.add("98746563542672");
		PreRegIdsByRegCenterIdDTO byRegCenterIdDTO = new PreRegIdsByRegCenterIdDTO();
		byRegCenterIdDTO.setPreRegistrationIds(list);
		mainRequestDTO.setRequest(byRegCenterIdDTO);

		MainResponseDTO<Map<String, String>> response = new MainResponseDTO<>();
		Map<String, String> map = new HashMap<>();
		map.put("98746563542672", LocalDateTime.now().toString());
		response.setResponse(map);
		response.setErrors(null);
		response.setResponsetime(LocalDateTime.now().toString());

		Mockito.when(preRegistrationService.getUpdatedDateTimeForPreIds(byRegCenterIdDTO)).thenReturn(response);
		JSONParser parser = new JSONParser();
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("preids.json").getFile());
		jsonObject = parser.parse(new FileReader(file));
		PreRegIdsByRegCenterIdDTO input= new PreRegIdsByRegCenterIdDTO();
		input.setRegistrationCenterId("10001");
		MainRequestDTO<PreRegIdsByRegCenterIdDTO> reqDto= new MainRequestDTO<>();
		reqDto.setId("mosip.pre-registration.demographic.retrieve.date");
		reqDto.setVersion("1.0");
		reqDto.setRequesttime(new Date());
		reqDto.setRequest(input);
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(reqDto,
				"MainRequestDTO<PreRegIdsByRegCenterIdDTO>");
		ResponseEntity<MainResponseDTO<Map<String, String>>> responseEntity = controller.getUpdatedDateTimeByPreIds(reqDto,
				errors);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

}
