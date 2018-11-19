package io.mosip.preregistration.application.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.jsonvalidator.dto.JsonValidatorResponseDto;
import io.mosip.preregistration.application.controller.PreRegistrationController;
import io.mosip.preregistration.application.dto.CreateDto;
import io.mosip.preregistration.application.dto.ExceptionInfoDto;
import io.mosip.preregistration.application.dto.ResponseDto;
import io.mosip.preregistration.application.dto.ViewRegistrationResponseDto;
import io.mosip.preregistration.application.service.PreRegistrationService;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@RunWith(SpringRunner.class)
@WebMvcTest(PreRegistrationController.class)
public class PreRegistrationControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private PreRegistrationService preRegistrationService;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Object jsonObject=null;
	
	@Before
	public void setup() throws FileNotFoundException, IOException, ParseException {
        JsonValidatorResponseDto dto= new JsonValidatorResponseDto();
		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();
		File file = new File(classLoader.getResource("pre-registration.json").getFile());
		jsonObject = parser.parse(new FileReader(file));
		
	}
	
	@Test
	public void successSave() throws Exception {
		logger.info("----------Successful save of application-------");		
        ResponseDto<CreateDto> response = new ResponseDto();
		List<CreateDto> saveList= new ArrayList<CreateDto>();
		CreateDto createDto= new CreateDto();

		createDto.setPrId("22893647484937");
        saveList.add(createDto);
        response.setResponse(saveList);

        Mockito.when(preRegistrationService.addRegistration(Mockito.any())).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/applications")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE).param("pre-id", "")
				.content(jsonObject.toString() );
        logger.info("Resonse "+response);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	public void failureSave() throws Exception {
		logger.info("----------Unsuccessful save of application-------");		
        ObjectMapper mapperObj = new ObjectMapper();
       
		
		Mockito.doThrow(new TablenotAccessibleException("ex")).when(preRegistrationService).addRegistration(Mockito.any());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/applications")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE).param("pre-id", "")
				.content(jsonObject.toString());
		mockMvc.perform(requestBuilder).andExpect(status().isInternalServerError());
	}
	
	@Test
	public void successUpdate() throws Exception {
		logger.info("----------Successful save of application-------");		
      
        ResponseDto<CreateDto> response = new ResponseDto();
		List<CreateDto> saveList= new ArrayList<CreateDto>();
		CreateDto createDto= new CreateDto();
		createDto.setPrId("22893647484937");
        saveList.add(createDto);
        response.setResponse(saveList);
        Mockito.when(preRegistrationService.addRegistration(Mockito.any())).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/applications")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE).param("pre-id", "22893647484937")
				.content(jsonObject.toString());
        logger.info("Resonse "+response);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	
	@Test
	public void getAllApplicationTest() throws Exception  {

		String userId = "9988905333";
		ViewRegistrationResponseDto responseDto = new ViewRegistrationResponseDto();
		List<ViewRegistrationResponseDto> response = new ArrayList<>();
		ExceptionInfoDto exceptionInfoDto=new ExceptionInfoDto();
		List<ExceptionInfoDto> responseList = new ArrayList<>();
		//responseDto.setGroup_id("1234");
		responseDto.setFirstname("rupika");
		//responseDto.setNoOfRecords(1);
		responseDto.setStatus_code("draft");
		//responseDto.setUpd_dtimesz("2018-10-08 00:00:00");
		response.add(responseDto);
		exceptionInfoDto.setResponse(response);
		exceptionInfoDto.setStatus(true);
		responseList.add(exceptionInfoDto);
		

		Mockito.when(preRegistrationService.getApplicationDetails(Mockito.anyString())).thenReturn(responseList);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/applications/")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.param("userId", userId);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());

	}

	@Test
	public void getApplicationStatusTest() throws Exception {
		String groupId = "1234";
		Map<String, String> response = new HashMap<String, String>();
		response.put("1234", "12245");

		Mockito.when(preRegistrationService.getApplicationStatus(Mockito.anyString())).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/applicationStatus/")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.param("groupId", groupId);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	

	@Test
	public void discardIndividualTest() throws Exception {
		 String preId= "3";
		 ResponseDto<CreateDto> response = new ResponseDto();
			List<CreateDto> saveList= new ArrayList<CreateDto>();
			CreateDto createDto= new CreateDto();

			createDto.setPrId("3");
	        saveList.add(createDto);
	        response.setResponse(saveList);
		Mockito.when(preRegistrationService.deleteIndividual(ArgumentMatchers.any())).thenReturn(response);
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/v0.1/pre-registration/applications")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.param("preId", preId);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	
//	@Test
//	public void discardGroupTest() throws Exception {
//		 String groupId= "33";
//		 ResponseDto response= new ResponseDto();
//		 response.setPrId("3");
//	     response.setGroupId("33");
//	     List<ResponseDto> resList= new ArrayList<>();
//	     resList.add(response);
//		Mockito.when(registrationService.deleteGroup(ArgumentMatchers.any())).thenReturn(resList);
//		
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/v0.1/pre-registration/discardGroup")
//				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
//				.param("groupId", groupId);
//		mockMvc.perform(requestBuilder).andExpect(status().isOk());
//	}

}
