package io.mosip.registration.controller.test;

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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.controller.RegistrationController;
import io.mosip.registration.core.generator.MosipGroupIdGenerator;
import io.mosip.registration.dto.AddressDto;
import io.mosip.registration.dto.ApplicationDto;
import io.mosip.registration.dto.ContactDto;
import io.mosip.registration.dto.NameDto;
import io.mosip.registration.dto.RegistrationDto;
import io.mosip.registration.dto.ResponseDto;
import io.mosip.registration.dto.ViewRegistrationResponseDto;
import io.mosip.registration.service.RegistrationService;

@RunWith(SpringRunner.class)
@WebMvcTest(RegistrationController.class)
public class RegistrationControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private RegistrationService registrationService;
	
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
		regDto.setCreatedBy("Rajath");
        regDto.setIsPrimary(true);
        regDto.setGroupId("");
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
        Mockito.when(registrationService.addRegistration(Mockito.anyString(),Mockito.anyString())).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/registration/save")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.content(jsonStr);
        logger.info("Resonse "+response);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
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
        Mockito.when(registrationService.addRegistration(Mockito.anyString(),Mockito.anyString())).thenReturn(response);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/registration/save")
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

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/registration/Applications/")
				.param("userId", userId);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());

	}

	@Test
	public void getApplicationStatusTest() throws Exception {
		String groupId = "1234";
		Map<String, String> response = new HashMap<String, String>();
		response.put("1234", "12245");

		Mockito.when(registrationService.getApplicationStatus(Mockito.anyString())).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/registration/ApplicationStatus/")
				.param("groupId", groupId);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	public void discardIndividualTest() throws Exception {
		 String groupId= "33";
		 String[] preregIds= {"3"};
		Mockito.doNothing().when(registrationService).deleteIndividual(ArgumentMatchers.any(),ArgumentMatchers.any());
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/v0.1/pre-registration/registration/discard")
				.param("groupId", groupId).param("preregIds",preregIds).accept(MediaType.ALL_VALUE);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	public void discardGroupTest() throws Exception {
		 String groupId= "33";
		Mockito.doNothing().when(registrationService).deleteGroup(ArgumentMatchers.any());
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/v0.1/pre-registration/registration/discardGroup")
				.param("groupId", groupId).accept(MediaType.ALL_VALUE);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

}
