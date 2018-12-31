package io.mosip.preregistration.application.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
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

import io.mosip.preregistration.application.controller.DemographicController;
import io.mosip.preregistration.application.dto.CreateDemographicDTO;
import io.mosip.preregistration.application.dto.DeletePreRegistartionDTO;
import io.mosip.preregistration.application.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.application.dto.PreRegistrationViewDTO;
import io.mosip.preregistration.application.dto.MainListResponseDTO;
import io.mosip.preregistration.application.dto.UpdateResponseDTO;
import io.mosip.preregistration.application.service.DemographicService;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;
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
@RunWith(SpringRunner.class)
@WebMvcTest(DemographicController.class)
public class DemographicControllerTest {

	/**
	 * Autowired reference for {@link #MockMvc}
	 */
	@Autowired
	private MockMvc mockMvc;

	/**
	 * Creating Mock Bean for DemographicService
	 */
	@MockBean
	private DemographicService preRegistrationService;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Object jsonObject = null;

	/**
	 * @throws FileNotFoundException when file not found
	 * @throws IOException on input error
	 * @throws ParseException on json parsing error
	 */
	@Before
	public void setup() throws FileNotFoundException, IOException, ParseException {
		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();
		File file = new File(classLoader.getResource("pre-registration.json").getFile());
		jsonObject = parser.parse(new FileReader(file));

	}

	/**
	 * @throws Exception on error
	 */
	@Test
	public void successSave() throws Exception {
		logger.info("----------Successful save of application-------");
		MainListResponseDTO<CreateDemographicDTO> response = new MainListResponseDTO<>();
		List<CreateDemographicDTO> saveList = new ArrayList<CreateDemographicDTO>();
		CreateDemographicDTO createDto = new CreateDemographicDTO();

		createDto.setPreRegistrationId("22893647484937");
		saveList.add(createDto);
		response.setResponse(saveList);

		Mockito.when(preRegistrationService.addPreRegistration(Mockito.any())).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/applications")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("pre-id", "").content(jsonObject.toString());
		logger.info("Resonse " + response);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception on error
	 */
	@Test
	public void failureSave() throws Exception {
		logger.info("----------Unsuccessful save of application-------");
		Mockito.doThrow(new TablenotAccessibleException("ex")).when(preRegistrationService)
				.addPreRegistration(Mockito.any());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/applications")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("pre-id", "").content(jsonObject.toString());
		mockMvc.perform(requestBuilder).andExpect(status().isInternalServerError());
	}

	/**
	 * @throws Exception on error
	 */
	@Test
	public void successUpdate() throws Exception {
		logger.info("----------Successful save of application-------");

		MainListResponseDTO<CreateDemographicDTO> response = new MainListResponseDTO<>();
		List<CreateDemographicDTO> saveList = new ArrayList<CreateDemographicDTO>();
		CreateDemographicDTO createDto = new CreateDemographicDTO();
		createDto.setPreRegistrationId("22893647484937");
		saveList.add(createDto);
		response.setResponse(saveList);
		Mockito.when(preRegistrationService.addPreRegistration(Mockito.any())).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/applications")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("pre-id", "22893647484937")
				.content(jsonObject.toString());
		logger.info("Resonse " + response);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception on error
	 */
	@Test
	public void getAllApplicationTest() throws Exception {

		String userId = "9988905333";
		MainListResponseDTO<PreRegistrationViewDTO> response = new MainListResponseDTO<>();
		List<PreRegistrationViewDTO> viewList = new ArrayList<>();
		PreRegistrationViewDTO viewDto = new PreRegistrationViewDTO();
		viewDto.setPreId("1234");
		viewDto.setStatusCode("Pending_Appointment");
		viewList.add(viewDto);
		response.setResponse(viewList);

		Mockito.when(preRegistrationService.getAllApplicationDetails(Mockito.anyString())).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/applications/")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("userId", userId);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());

	}

	/**
	 * @throws Exception on error
	 */
	@Test
	public void getApplicationStatusTest() throws Exception {
		String preId = "14532456789";
		MainListResponseDTO<PreRegistartionStatusDTO> response = new MainListResponseDTO<>();
		List<PreRegistartionStatusDTO> statusList = new ArrayList<PreRegistartionStatusDTO>();
		PreRegistartionStatusDTO statusDto = new PreRegistartionStatusDTO();
		statusDto.setPreRegistartionId(preId);
		statusDto.setStatusCode("Pending_Appointment");
		statusList.add(statusDto);
		response.setResponse(statusList);

		Mockito.when(preRegistrationService.getApplicationStatus(Mockito.anyString())).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/applicationStatus/")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("preId", preId);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception on error
	 */
	@Test
	public void discardIndividualTest() throws Exception {
		String preId = "3";
		MainListResponseDTO<DeletePreRegistartionDTO> response = new MainListResponseDTO<>();
		List<DeletePreRegistartionDTO> DeleteList = new ArrayList<DeletePreRegistartionDTO>();
		DeletePreRegistartionDTO deleteDto = new DeletePreRegistartionDTO();

		deleteDto.setPrId("3");
		deleteDto.setDeletedBy("9527832358");
		DeleteList.add(deleteDto);
		response.setResponse(DeleteList);
		Mockito.when(preRegistrationService.deleteIndividual(ArgumentMatchers.any())).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/v0.1/pre-registration/applications")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("preId", preId);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception on error
	 */
	@Test
	public void getApplicationSuccessTest() throws Exception {
		MainListResponseDTO<CreateDemographicDTO> response = new MainListResponseDTO<>();
		List<CreateDemographicDTO> saveList = new ArrayList<CreateDemographicDTO>();
		CreateDemographicDTO createDto = new CreateDemographicDTO();

		createDto.setPreRegistrationId("22893647484937");
		saveList.add(createDto);
		response.setResponse(saveList);

		Mockito.when(preRegistrationService.getDemographicData("1234")).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/applicationData")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("preRegId", createDto.getPreRegistrationId());

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception on error
	 */
	@Test
	public void updateApplicationStatusTest() throws Exception {
		UpdateResponseDTO<String> response = new UpdateResponseDTO<>();
		response.setErr(null);
		response.setResponse("Status Updated sucessfully");
		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus("true");

		Mockito.when(preRegistrationService.updatePreRegistrationStatus("1234", "Booked")).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/v0.1/pre-registration/applications")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("preRegId", "1234").param("status", "Booked");

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception on error
	 */
	@Test
	public void getAllApplicationByDateTest() throws Exception {

		String fromDate = "2018-12-06 09:49:29";
		String toDate = "2018-12-06 12:59:29";
		MainListResponseDTO<String> response = new MainListResponseDTO<>();
		List<String> preIds = new ArrayList<>();
		preIds.add("1234");
		response.setResponse(preIds);

		Mockito.when(preRegistrationService.getPreRegistrationByDate(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/applicationDataByDateTime/")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("fromDate", fromDate)
				.accept(MediaType.APPLICATION_JSON_VALUE).param("toDate", toDate);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());

	}
}
