package io.mosip.preregistration.notification.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import io.mosip.preregistration.notification.service.NotificationService;
import io.mosip.preregistration.notification.service.util.NotificationServiceUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

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
	private NotificationService service;

	@MockBean
	private NotificationServiceUtil serviceUtil;
	

	private NotificationDTO notificationDTO;

	MainResponseDTO<NotificationDTO> responseDTO = new MainResponseDTO<>();

	@Before
	public void setUp() {
		notificationDTO = new NotificationDTO();
		notificationDTO.setName("sanober Noor");
		notificationDTO.setPreId("1234567890");
		notificationDTO.setMobNum("1234567890");
		notificationDTO.setEmailID("sanober,noor2@mindtree.com");
		notificationDTO.setAppointmentDate("2019-01-22");
		notificationDTO.setAppointmentTime("22:57");

		responseDTO.setResponse(notificationDTO);
		responseDTO.setResTime(serviceUtil.getCurrentResponseTime());
		responseDTO.setStatus(Boolean.TRUE);
	}

	@Test
	public void sendNotificationTest() throws Exception {
		String stringjson = mapper.writeValueAsString(notificationDTO);
String langCode="eng";
		Mockito.when(service.sendNotification(stringjson, "eng", null)).thenReturn(responseDTO);
		
		mockMvc.perform(MockMvcRequestBuilders.multipart("/v0.1/pre-registration/notification")
				.file(new MockMultipartFile("NotificationDTO",stringjson,
						"application/json",stringjson.getBytes(Charset.forName("UTF-8") ))).
				file(new MockMultipartFile("langCode",langCode,
						"application/json",langCode.getBytes(Charset.forName("UTF-8") 
								 )))).andExpect(status().isOk());

	}

	@Test
	public void qrCodeGenerationTest() throws Exception {
		String stringjson = mapper.writeValueAsString(notificationDTO);
		Mockito.when(service.sendNotification(stringjson, "eng", null)).thenReturn(responseDTO);
		
		mockMvc.perform(post("/v0.1/pre-registration/generateQRCode").contentType(MediaType.APPLICATION_JSON)
			.content(stringjson)).andExpect(status().isOk());

	}
}
