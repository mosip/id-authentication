package io.mosip.preregistration.generateqrcode.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import io.mosip.preregistration.generateqrcode.GenerateQRcodeApplicationTests;
import io.mosip.preregistration.generateqrcode.dto.QRCodeResponseDTO;
import io.mosip.preregistration.generateqrcode.service.GenerateQRcodeService;
import io.mosip.preregistration.generateqrcode.service.util.GenerateQRcodeServiceUtil;


@SpringBootTest(classes = { GenerateQRcodeApplicationTests.class })
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class GenerateQRcodeControllerTest {


	/**
	 * Autowired reference for {@link #MockMvc}
	 */
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	/**
	 * /** Creating Mock Bean for NotificationService
	 */
	@MockBean
	private GenerateQRcodeService service;

	@MockBean
	private GenerateQRcodeServiceUtil serviceUtil;
	

	private NotificationDTO notificationDTO;

	MainResponseDTO<NotificationDTO> responseDTO = new MainResponseDTO<>();
	MainRequestDTO<JSONObject> requestdata=new MainRequestDTO<>();

	@Before
	public void setUp() {
		notificationDTO = new NotificationDTO();
		notificationDTO.setName("sanober Noor");
		notificationDTO.setPreRegistrationId("1234567890");
		notificationDTO.setMobNum("1234567890");
		notificationDTO.setEmailID("sanober,noor2@mindtree.com");
		notificationDTO.setAppointmentDate("2019-01-22");
		notificationDTO.setAppointmentTime("22:57");

		responseDTO.setResponse(notificationDTO);
		responseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
		
	}
	
	/**
	 * This test method is for success qrCodeGeneration 
	 * @throws Exception
	 */
	@WithUserDetails("INDIVIDUAL")
	@Test
	public void qrCodeGenerationTest() throws Exception {
		
		
		MainResponseDTO<QRCodeResponseDTO> response = new MainResponseDTO<>();

		String stringjson = mapper.writeValueAsString(notificationDTO);
		Mockito.when(service.generateQRCode(requestdata)).thenReturn(response);
		mockMvc.perform(post("/generate").contentType(MediaType.APPLICATION_JSON)
				.content(stringjson)).andExpect(status().isOk());
		

	}
}
