//package io.mosip.preregistration.notification.service;
//
//import static org.junit.Assert.assertEquals;
//
//import java.sql.Timestamp;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import io.mosip.kernel.core.exception.IOException;
//import io.mosip.kernel.core.qrcodegenerator.spi.QrCodeGenerator;
//import io.mosip.kernel.core.util.exception.JsonMappingException;
//import io.mosip.kernel.core.util.exception.JsonParseException;
//import io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion;
//import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
//import io.mosip.preregistration.core.common.dto.MainRequestDTO;
//import io.mosip.preregistration.core.common.dto.MainResponseDTO;
//import io.mosip.preregistration.core.common.dto.NotificationDTO;
//import io.mosip.preregistration.core.common.dto.NotificationResponseDTO;
//import io.mosip.preregistration.core.common.dto.TemplateResponseDTO;
//import io.mosip.preregistration.core.common.dto.TemplateResponseListDTO;
//import io.mosip.preregistration.core.util.NotificationUtil;
//import io.mosip.preregistration.notification.NotificationApplicationTest;
//import io.mosip.preregistration.notification.dto.QRCodeResponseDTO;
//import io.mosip.preregistration.notification.exception.MandatoryFieldException;
//import io.mosip.preregistration.notification.service.util.NotificationServiceUtil;
//
///**
// * @author Sanober Noor
// * @since 1.0.0
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes= {NotificationApplicationTest.class})
//public class NotificationServiceTest {
//
//	@Autowired
//	private NotificationService service;
//
//	@Autowired
//	private NotificationServiceUtil serviceUtil;
//
//	@Autowired
//	private ObjectMapper mapper;
//
//	@MockBean(name = "restTemplate")
//	private RestTemplate restTemplate;
//
//	@MockBean
//	private QrCodeGenerator<QrVersion> qrCodeGenerator;
//
//	@Value("${mosip.utc-datetime-pattern}")
//	private String utcDateTimePattern;
//	
//	@MockBean
//   private NotificationUtil NotificationUtil;
//	private NotificationDTO notificationDTO;
//	boolean requestValidatorFlag = false;
//	TemplateResponseDTO templateResponseDTO = new TemplateResponseDTO();
//	MainResponseDTO<NotificationDTO> responseDTO = new MainResponseDTO<>();
//	MainListResponseDTO<NotificationResponseDTO> responselist=new MainListResponseDTO<>();
//	MainResponseDTO<QRCodeResponseDTO> qrCodeResponseDTO = new MainResponseDTO<>();
//	NotificationResponseDTO notificationResponseDTO = new NotificationResponseDTO();
//	MainRequestDTO<NotificationDTO> mainReqDto = new MainRequestDTO<>();
//	List<TemplateResponseDTO> tepmlateList = new ArrayList<>();
//
//	@Before
//	public void beforeSet() throws ParseException {
//
//		//mapper.registerModule(new JavaTimeModule());
//		notificationDTO = new NotificationDTO();
//		notificationDTO.setName("sanober Noor");
//		notificationDTO.setPreRegistrationId("1234567890");
//		notificationDTO.setMobNum("1234567890");
//		notificationDTO.setEmailID("sanober.noor2@mindtree.com");
//		notificationDTO.setAppointmentDate("2019-01-22");
//		notificationDTO.setAppointmentTime("22:57");
//		mainReqDto.setId("mosip.pre-registration.notification.notify");
//		mainReqDto.setVersion("1.0");
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//		mapper.setDateFormat(df);
////2019-04-8T07:22:57.186Z
//		mainReqDto.setRequesttime(df.parse("2019-04-5T07:22:57.186Z"));
//		mainReqDto.setRequesttime(new Timestamp(System.currentTimeMillis()));
//		mainReqDto.setRequest(notificationDTO);
//		responseDTO = new MainResponseDTO<>();
//		responseDTO.setResponse(notificationDTO);
//		responseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
//		templateResponseDTO.setFileText("Email message");
//		tepmlateList.add(templateResponseDTO);
//
//		notificationResponseDTO.setMessage("Notification send successfully");
//		notificationResponseDTO.setStatus("True");
//	}
//
//	/**
//	 * This test method is for succes case of sendNotificationSuccess
//	 * 
//	 * @throws JsonParseException
//	 * @throws JsonMappingException
//	 * @throws IOException
//	 * @throws java.io.IOException
//	 */
////	@Test
//	public void sendNotificationSuccessTest()
//			throws JsonParseException, JsonMappingException, IOException, java.io.IOException {
//		String stringjson = mapper.writeValueAsString(mainReqDto);
//		String langCode = "eng";
//		MultipartFile file = new MockMultipartFile("test.txt", "test.txt", null, new byte[1100]);
//		TemplateResponseListDTO templateResponseListDTO = new TemplateResponseListDTO();
//		templateResponseListDTO.setTemplates(tepmlateList);
//		Mockito.when(NotificationUtil.notify("sms", notificationDTO, langCode, file)).thenReturn(responselist);
//		ResponseEntity<TemplateResponseListDTO> res = new ResponseEntity<TemplateResponseListDTO>(
//				templateResponseListDTO, HttpStatus.OK);
//		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(TemplateResponseListDTO.class)))
//				.thenReturn(res);
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//		ResponseEntity<NotificationResponseDTO> resp = new ResponseEntity<NotificationResponseDTO>(
//				notificationResponseDTO, HttpStatus.OK);
//		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
//				Mockito.eq(NotificationResponseDTO.class))).thenReturn(resp);
//		System.out.println(stringjson);
//		MainResponseDTO<NotificationDTO> response = service.sendNotification(stringjson, langCode, file);
//		assertEquals(responseDTO.getResponse(), response.getResponse());
//	}
//
//	/**
//	 * This method is for failure case of sendNotification
//	 * 
//	 * @throws JsonProcessingException
//	 */
////	@Test(expected = MandatoryFieldException.class)
//	public void sendNotificationFailureTest() throws JsonProcessingException {
//		notificationDTO = new NotificationDTO();
//		notificationDTO.setName("sanober Noor");
//		notificationDTO.setPreRegistrationId("1234567890");
//		notificationDTO.setMobNum("");
//		notificationDTO.setEmailID("");
//		notificationDTO.setAppointmentDate("2019-01-22");
//		notificationDTO.setAppointmentTime("22:57");
//		mainReqDto.setRequest(notificationDTO);
//		responseDTO = new MainResponseDTO<>();
//		responseDTO.setResponse(notificationDTO);
//		responseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
//		String stringjson = mapper.writeValueAsString(mainReqDto);
//		MultipartFile file = new MockMultipartFile("test.txt", "test.txt", null, new byte[1100]);
//		MainResponseDTO<NotificationDTO> response = service.sendNotification(stringjson, "eng", file);
//		assertEquals("MOBILE_NUMBER_OR_EMAIL_ADDRESS_NOT_FILLED", response.getResponse());
//
//	}
//
//	/**
//	 * This method is for failure case of sendNotification
//	 * 
//	 * @throws JsonProcessingException
//	 */
////	@Test(expected = MandatoryFieldException.class)
//	public void sendNotificationExceptionTest() throws JsonProcessingException {
//		notificationDTO = new NotificationDTO();
//		notificationDTO.setName("sanober Noor");
//		notificationDTO.setPreRegistrationId("1234567890");
//		notificationDTO.setMobNum(null);
//		notificationDTO.setEmailID(null);
//		notificationDTO.setAppointmentDate("2019-01-22");
//		notificationDTO.setAppointmentTime("22:57");
//		mainReqDto.setRequest(notificationDTO);
//		responseDTO = new MainResponseDTO<>();
//		responseDTO.setResponse(notificationDTO);
//		responseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
//		String stringjson = mapper.writeValueAsString(mainReqDto);
//		MultipartFile file = new MockMultipartFile("test.txt", "test.txt", null, new byte[1100]);
//		MainResponseDTO<NotificationDTO> response = service.sendNotification(stringjson, "eng", file);
//		assertEquals("MOBILE_NUMBER_OR_EMAIL_ADDRESS_NOT_FILLED", response.getResponse());
//
//	}
//
////	/**
////	 * This method return the success test case for generateQRCode method
////	 * 
////	 * @throws QrcodeGenerationException
////	 * @throws java.io.IOException
////	 */
////	@Test
////	public void generateQRCodeSuccessTest() throws QrcodeGenerationException, java.io.IOException {
////		String stringjson = mapper.writeValueAsString(notificationDTO);
////		byte[] qrCode = null;
////		QRCodeResponseDTO responsedto = new QRCodeResponseDTO();
////		responsedto.setQrcode(qrCode);
////		qrCodeResponseDTO.setResponse(responsedto);
////		qrCodeResponseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
////		Mockito.when(qrCodeGenerator.generateQrCode(stringjson, QrVersion.V25)).thenReturn(qrCode);
////		MainResponseDTO<QRCodeResponseDTO> response = service.generateQRCode(stringjson);
////
////		assertEquals(qrCodeResponseDTO.getResponse(), response.getResponse());
////	}
////
////	@Test
////	public void generateQRCodeFailureTest() throws java.io.IOException, QrcodeGenerationException {
////		String stringjson = mapper.writeValueAsString(notificationDTO);
////
////		Mockito.when(qrCodeGenerator.generateQrCode(null, QrVersion.V25)).thenThrow(QrcodeGenerationException.class);
////		service.generateQRCode(stringjson);
////
////		assertEquals(null, qrCodeResponseDTO.getResponse());
////
////	}
//
//	/**
//	 * This test method is for succes case of getConfig
//	 * 
//	 * @throws JsonParseException
//	 * @throws JsonMappingException
//	 * @throws IOException
//	 * @throws java.io.IOException
//	 */
//	/*
//	 * @Test public void getConfigSuccessTest() throws Exception {
//	 * ResponseEntity<String> res = new
//	 * ResponseEntity<String>("mosip.secondary-language=fra", HttpStatus.OK);
//	 * Map<String, String> configParams = new HashMap<>();
//	 * MainResponseDTO<Map<String, String>> response = new MainResponseDTO<>();
//	 * Mockito.when(restTemplate.getForEntity(Mockito.anyString(),
//	 * Mockito.eq(String.class))).thenReturn(res); response = service.getConfig();
//	 * assertEquals(response.getResponse().get("mosip.secondary-language"), "fra");
//	 * }
//	 */
//
//}
