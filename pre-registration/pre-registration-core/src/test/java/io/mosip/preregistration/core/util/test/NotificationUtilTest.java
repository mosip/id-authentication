package io.mosip.preregistration.core.util.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import io.mosip.preregistration.core.common.dto.NotificationResponseDTO;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.common.dto.TemplateResponseDTO;
import io.mosip.preregistration.core.common.dto.TemplateResponseListDTO;
import io.mosip.preregistration.core.util.NotificationUtil;
import io.mosip.preregistration.core.util.TemplateUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NotificationUtilTest {

	@Value("${emailResourse.url}")
	private String emailResourseUrl;

	@Value("${smsResourse.url}")
	private String smsResourseUrl;
	
	@Autowired
	private TemplateUtil templateUtil;
	
	@Autowired
	private ObjectMapper mapper;
	
	@MockBean
	RestTemplate restTemplate;
	
	@Autowired
	NotificationUtil notificationUtil;
	
	private NotificationDTO notificationDTO;
	boolean requestValidatorFlag = false;
	TemplateResponseDTO templateResponseDTO = new TemplateResponseDTO();
	MainResponseDTO<NotificationDTO> responseDTO = new MainResponseDTO<>();
	NotificationResponseDTO notificationResponseDTO = new NotificationResponseDTO();
	List<TemplateResponseDTO> tepmlateList = new ArrayList<>();
	TemplateResponseListDTO templateResponseListDTO = new TemplateResponseListDTO();
	ResponseEntity<NotificationResponseDTO> resp = null;
	
	@Before
	public void setUp() throws Exception {
		
		
		notificationDTO = new NotificationDTO();
		notificationDTO.setName("sanober Noor");
		notificationDTO.setPreRegistrationId("1234567890");
		notificationDTO.setMobNum("1234567890");
		notificationDTO.setEmailID("sanober,noor2@mindtree.com");
		notificationDTO.setAppointmentDate("2019-01-22");
		notificationDTO.setAppointmentTime("22:57");
		notificationDTO.setIsBatch(false);
		responseDTO = new MainResponseDTO<>();
		responseDTO.setResponse(notificationDTO);
		//responseDTO.setStatus(Boolean.TRUE);
		templateResponseDTO.setFileText("Email message");
		tepmlateList.add(templateResponseDTO);

		notificationResponseDTO.setMessage("Notification send successfully");
		notificationResponseDTO.setStatus("True");
		
		templateResponseListDTO.setTemplates(tepmlateList);
	}
	
	@Test
	public void notifyEmailsuccessTest() throws IOException {
		
		String langCode = "eng";
		MultipartFile file = new MockMultipartFile("test.txt", "test.txt", null, new byte[1100]);
		ResponseWrapper<TemplateResponseListDTO> templateResponseListDTO = new ResponseWrapper<>();
		TemplateResponseListDTO templates= new TemplateResponseListDTO();
		templates.setTemplates(tepmlateList);
		templateResponseListDTO.setResponse(templates);
		ResponseEntity<ResponseWrapper<TemplateResponseListDTO>> res = new ResponseEntity<>(
				templateResponseListDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<ResponseWrapper<TemplateResponseListDTO>>() {})))
				.thenReturn(res);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		ResponseWrapper<NotificationResponseDTO> notificationres= new ResponseWrapper<>();
		notificationres.setResponse(notificationResponseDTO);
		ResponseEntity<ResponseWrapper<NotificationResponseDTO>> resp = new ResponseEntity<>(
				notificationres, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<ResponseWrapper<NotificationResponseDTO>>() {
				}))).thenReturn(resp);
		MainResponseDTO<NotificationResponseDTO> response=notificationUtil.notify("email", notificationDTO, langCode, file);
		assertEquals(notificationResponseDTO.getMessage(), response.getResponse().getMessage());
	}
	
	@Test
	public void notifySMSsuccessTest() throws IOException {
		
		String langCode = "eng";
		MultipartFile file = new MockMultipartFile("test.txt", "test.txt", null, new byte[1100]);
		ResponseWrapper<TemplateResponseListDTO> templateResponseListDTO = new ResponseWrapper<>();
		TemplateResponseListDTO templates= new TemplateResponseListDTO();
		templates.setTemplates(tepmlateList);
		templateResponseListDTO.setResponse(templates);
		ResponseEntity<ResponseWrapper<TemplateResponseListDTO>> res = new ResponseEntity<>(
				templateResponseListDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<ResponseWrapper<TemplateResponseListDTO>>() {})))
				.thenReturn(res);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		ResponseWrapper<NotificationResponseDTO> notificationres= new ResponseWrapper<>();
		notificationres.setResponse(notificationResponseDTO);
		ResponseEntity<ResponseWrapper<NotificationResponseDTO>> resp = new ResponseEntity<>(
				notificationres, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<ResponseWrapper<NotificationResponseDTO>>() {
				}))).thenReturn(resp);
		
		MainResponseDTO<NotificationResponseDTO> response=notificationUtil.notify("sms", notificationDTO, langCode, file);
		assertEquals(notificationResponseDTO.getMessage(), response.getResponse().getMessage());
	}

}
