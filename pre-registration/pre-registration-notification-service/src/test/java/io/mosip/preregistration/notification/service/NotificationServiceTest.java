package io.mosip.preregistration.notification.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.qrcodegenerator.exception.QrcodeGenerationException;
import io.mosip.kernel.core.qrcodegenerator.spi.QrCodeGenerator;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import io.mosip.preregistration.core.common.dto.NotificationResponseDTO;
import io.mosip.preregistration.core.common.dto.TemplateResponseDTO;
import io.mosip.preregistration.core.common.dto.TemplateResponseListDTO;
import io.mosip.preregistration.notification.controller.NotificationController;
import io.mosip.preregistration.notification.dto.QRCodeResponseDTO;
import io.mosip.preregistration.notification.exception.MandatoryFieldException;
import io.mosip.preregistration.notification.service.util.NotificationServiceUtil;

/**
 * @author Sanober Noor
 *@since 1.0.0
 */
@RunWith(SpringRunner.class)
@WebMvcTest(NotificationController.class)
public class NotificationServiceTest {

	@Autowired
	private NotificationService service;



	@Autowired
	private NotificationServiceUtil serviceUtil;

	

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private RestTemplate restTemplate;

	@MockBean
	private QrCodeGenerator<QrVersion> qrCodeGenerator;

	private NotificationDTO notificationDTO;
	boolean requestValidatorFlag = false;
	TemplateResponseDTO templateResponseDTO = new TemplateResponseDTO();
	MainResponseDTO<NotificationDTO> responseDTO = new MainResponseDTO<>();
	MainResponseDTO<QRCodeResponseDTO> qrCodeResponseDTO=new MainResponseDTO<>();
	NotificationResponseDTO notificationResponseDTO = new NotificationResponseDTO();
	List<TemplateResponseDTO> tepmlateList = new ArrayList<>();

	@Before
	public void beforeSet() {
		notificationDTO = new NotificationDTO();
		notificationDTO.setName("sanober Noor");
		notificationDTO.setPreId("1234567890");
		notificationDTO.setMobNum("1234567890");
		notificationDTO.setEmailID("sanober,noor2@mindtree.com");
		notificationDTO.setAppointmentDate("2019-01-22");
		notificationDTO.setAppointmentTime("22:57");
		responseDTO = new MainResponseDTO<>();
		responseDTO.setResponse(notificationDTO);
		responseDTO.setResTime(serviceUtil.getCurrentResponseTime());
		responseDTO.setStatus(Boolean.TRUE);
		templateResponseDTO.setFileText("Email message");
		tepmlateList.add(templateResponseDTO);

		notificationResponseDTO.setMessage("Notification send successfully");
		notificationResponseDTO.setStatus("True");
	}

	/**
	 * This test method is for succes case of sendNotificationSuccess
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws java.io.IOException
	 */
	@Test
	public void sendNotificationSuccessTest()
			throws JsonParseException, JsonMappingException, IOException, java.io.IOException {
		String stringjson = mapper.writeValueAsString(notificationDTO);
		String langCode = "eng";
		MultipartFile file = new MockMultipartFile("test.txt", "test.txt", null, new byte[1100]);
		TemplateResponseListDTO templateResponseListDTO = new TemplateResponseListDTO();
		templateResponseListDTO.setTemplates(tepmlateList);
		ResponseEntity<TemplateResponseListDTO> res = new ResponseEntity<TemplateResponseListDTO>(
				templateResponseListDTO, HttpStatus.OK);
		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(TemplateResponseListDTO.class)))
				.thenReturn(res);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		ResponseEntity<NotificationResponseDTO> resp = new ResponseEntity<NotificationResponseDTO>(
				notificationResponseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
				Mockito.eq(NotificationResponseDTO.class))).thenReturn(resp);
		MainResponseDTO<NotificationDTO> response = service.sendNotification(stringjson, langCode, file);
		assertEquals(responseDTO.getResponse(), response.getResponse());
	}

	/**
	 * This method is for failure case of sendNotification
	 * @throws JsonProcessingException
	 */
	@Test(expected=MandatoryFieldException.class)
	public void sendNotificationFailureTest() throws JsonProcessingException {
		notificationDTO=new NotificationDTO();
		notificationDTO.setName("sanober Noor");
		notificationDTO.setPreId("1234567890");
		notificationDTO.setMobNum("");
		notificationDTO.setEmailID("");
		notificationDTO.setAppointmentDate("2019-01-22");
		notificationDTO.setAppointmentTime("22:57");
		responseDTO = new MainResponseDTO<>();
		responseDTO.setResponse(notificationDTO);
		responseDTO.setResTime(serviceUtil.getCurrentResponseTime());
		responseDTO.setStatus(Boolean.TRUE);
		String stringjson = mapper.writeValueAsString(notificationDTO);
		MultipartFile file = new MockMultipartFile("test.txt", "test.txt", null, new byte[1100]);
		MainResponseDTO<NotificationDTO> response = service.sendNotification(stringjson, "eng", file);
		assertEquals("MOBILE_NUMBER_OR_EMAIL_ADDRESS_NOT_FILLED", response.getResponse());
		
	}
	
	/**
	 * This method return the success test case for generateQRCode method
	 * @throws QrcodeGenerationException
	 * @throws java.io.IOException
	 */
	@Test
	public void generateQRCodeSuccessTest() throws QrcodeGenerationException, java.io.IOException {
		String stringjson = mapper.writeValueAsString(notificationDTO);
		byte[] qrCode = null;
		QRCodeResponseDTO responsedto = new QRCodeResponseDTO();
		responsedto.setQrcode(qrCode);
		qrCodeResponseDTO.setResponse(responsedto);
		qrCodeResponseDTO.setResTime(serviceUtil.getCurrentResponseTime());
		qrCodeResponseDTO.setStatus(Boolean.TRUE);
		Mockito.when(qrCodeGenerator.generateQrCode(stringjson, QrVersion.V25)).thenReturn(qrCode);
		MainResponseDTO<QRCodeResponseDTO> response = service.generateQRCode(stringjson);
		
		assertEquals(qrCodeResponseDTO.getResponse(), response.getResponse());		
	}
	
//	@Test
//	public void generateQRCodeFailureTest() throws  java.io.IOException, QrcodeGenerationException {
//		String stringjson = mapper.writeValueAsString(notificationDTO);
//		
//		Mockito.when(qrCodeGenerator.generateQrCode(null, QrVersion.V25)).thenThrow(QrcodeGenerationException.class);
//      Mockito.when(service.generateQRCode(stringjson)).thenThrow(QrcodeGenerationException.class) ;
//		
//		//assertEquals(qrCodeResponseDTO.getResponse(), response.getResponse());	
//	}
	
	
}
