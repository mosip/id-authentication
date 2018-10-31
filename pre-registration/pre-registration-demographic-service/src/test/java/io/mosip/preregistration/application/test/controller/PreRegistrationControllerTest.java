package io.mosip.preregistration.application.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import io.mosip.preregistration.application.controller.PreRegistrationController;
import io.mosip.preregistration.application.dto.AddressDto;
import io.mosip.preregistration.application.dto.ApplicationDto;
import io.mosip.preregistration.application.dto.ContactDto;
import io.mosip.preregistration.application.dto.NameDto;
import io.mosip.preregistration.application.dto.RegistrationDto;
import io.mosip.preregistration.application.dto.ResponseDto;
import io.mosip.preregistration.application.dto.ViewRegistrationResponseDto;
import io.mosip.preregistration.application.service.RegistrationService;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;
import io.mosip.preregistration.core.generator.MosipGroupIdGenerator;

@RunWith(SpringRunner.class)
@WebMvcTest(PreRegistrationController.class)
public class PreRegistrationControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private RegistrationService<String,RegistrationDto> registrationService;
	
	@MockBean
	private MosipGroupIdGenerator<String> groupIdGenerator;

	private RegistrationDto regDto= new RegistrationDto();
	private NameDto nameDto= new NameDto();
	private ContactDto contactDto= new ContactDto();
	private AddressDto addrDto= new AddressDto();
	
	private ApplicationDto appDto= new ApplicationDto();
	
	private List<RegistrationDto> applicationForms= new ArrayList<>();
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Before
	public void setup() {

		nameDto.setFirstname("Rajath");
		nameDto.setFullname("Rajath Kumar");
		contactDto.setEmail("rajath.kr1249@gmail.com");
		contactDto.setMobile("9480548558");
		addrDto.setAddrLine1("global");
		addrDto.setAddrLine2("Village");
		addrDto.setLocationCode("1234");
		regDto.setAddress(addrDto);
		regDto.setContact(contactDto);
		regDto.setName(nameDto);
		regDto.setAge(10);
        regDto.setIsPrimary(true);
        regDto.setGroupId("123");
        regDto.setPreRegistrationId("");
        
        logger.info("Registration DTO "+regDto);
        applicationForms.add(regDto);
        appDto.setApplications(applicationForms);
	}
	
	@Test
	public void successSave() throws Exception {
		logger.info("----------Successful save of application-------");		
        ResponseDto response= new ResponseDto();
        ObjectMapper mapperObj = new ObjectMapper();
        String jsonStr="";
        
        try {
             jsonStr = mapperObj.writeValueAsString(appDto);

        } catch (IOException e) {

            e.printStackTrace();
        }
        response.setPrId("22893647484937");
        response.setGroupId("986453847462");
        List<ResponseDto> resList= new ArrayList<>();
        resList.add(response);
        Mockito.when(groupIdGenerator.generateGroupId()).thenReturn("986453847462");
        Mockito.when(registrationService.addRegistration(Mockito.any(),Mockito.anyString())).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/registration/applications")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.content(jsonStr);
        logger.info("Resonse "+response);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	public void failureSave() throws Exception {
		logger.info("----------Unsuccessful save of application-------");		
        ObjectMapper mapperObj = new ObjectMapper();
        
        String jsonStr="";
        
        try {
             jsonStr = mapperObj.writeValueAsString(appDto);

        } catch (IOException e) {

            e.printStackTrace();
        }
		
		Mockito.doThrow(new TablenotAccessibleException("ex")).when(registrationService).addRegistration(Mockito.any(RegistrationDto.class),Mockito.anyObject());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/registration/applications")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.content(jsonStr);
		mockMvc.perform(requestBuilder).andExpect(status().isInternalServerError());
	}
	
	@Test
	public void successUpdate() throws Exception {
		logger.info("----------Successful save of application-------");		
        ResponseDto response= new ResponseDto();
        regDto.setPreRegistrationId("22893647484937");
        regDto.setGroupId("986453847462");
        ObjectMapper mapperObj = new ObjectMapper();
        String jsonStr="";
        
        try {
             jsonStr = mapperObj.writeValueAsString(appDto);

        } catch (IOException e) {

            e.printStackTrace();
        }
        response.setPrId("22893647484937");
        response.setGroupId("986453847462");
        List<ResponseDto> resList= new ArrayList<>();
        resList.add(response);
        Mockito.when(groupIdGenerator.generateGroupId()).thenReturn("986453847462");
        Mockito.when(registrationService.addRegistration(Mockito.any(),Mockito.anyString())).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/registration/applications")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.content(jsonStr);
        logger.info("Resonse "+response);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	
	@Test
	public void getAllApplicationTest() throws Exception  {

		String userId = "9988905333";
		ViewRegistrationResponseDto responseDto = new ViewRegistrationResponseDto();
		List<ViewRegistrationResponseDto> response = new ArrayList<ViewRegistrationResponseDto>();
		responseDto.setGroup_id("1234");
		responseDto.setFirstname("rupika");
		responseDto.setNoOfRecords(1);
		responseDto.setStatus_code("draft");
		responseDto.setUpd_dtimesz("2018-10-08 00:00:00");
		response.add(responseDto);

		Mockito.when(registrationService.getApplicationDetails(Mockito.anyString())).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/registration/applications/")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.param("userId", userId);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());

	}

	@Test
	public void getApplicationStatusTest() throws Exception {
		String groupId = "1234";
		Map<String, String> response = new HashMap<String, String>();
		response.put("1234", "12245");

		Mockito.when(registrationService.getApplicationStatus(Mockito.anyString())).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/registration/applicationStatus/")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.param("groupId", groupId);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	public void discardIndividualTest() throws Exception {
		 String groupId= "33";
		 String[] preregIds= {"3"};
		 ResponseDto response= new ResponseDto();
		 response.setPrId("3");
	     response.setGroupId("33");
	     List<ResponseDto> resList= new ArrayList<>();
	     resList.add(response);
		Mockito.when(registrationService.deleteIndividual(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(resList);
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/v0.1/pre-registration/registration/applications")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.param("groupId", groupId).param("preregIds",preregIds);
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
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/v0.1/pre-registration/registration/discardGroup")
//				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
//				.param("groupId", groupId);
//		mockMvc.perform(requestBuilder).andExpect(status().isOk());
//	}

}
