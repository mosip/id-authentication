package io.mosip.preregistration.generateqrcode.service;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.qrcodegenerator.exception.QrcodeGenerationException;
import io.mosip.kernel.core.qrcodegenerator.spi.QrCodeGenerator;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import io.mosip.preregistration.core.common.dto.NotificationResponseDTO;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.generateqrcode.GenerateQRcodeApplicationTests;
import io.mosip.preregistration.generateqrcode.dto.QRCodeResponseDTO;
import io.mosip.preregistration.generateqrcode.exception.IllegalParamException;
import io.mosip.preregistration.generateqrcode.service.GenerateQRcodeService;
import io.mosip.preregistration.generateqrcode.service.util.GenerateQRcodeServiceUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { GenerateQRcodeApplicationTests.class })

public class GenerateQRcodeServiceTest {

	@Autowired
	private GenerateQRcodeService service;

	@Autowired
	private GenerateQRcodeServiceUtil serviceUtil;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private QrCodeGenerator<QrVersion> qrCodeGenerator;

	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;

	private NotificationDTO notificationDTO;
	boolean requestValidatorFlag = false;
	MainResponseDTO<NotificationDTO> responseDTO = new MainResponseDTO<>();
	MainResponseDTO<QRCodeResponseDTO> qrCodeResponseDTO = new MainResponseDTO<>();
	NotificationResponseDTO notificationResponseDTO = new NotificationResponseDTO();
	MainRequestDTO<JSONObject> qrcodedto = new MainRequestDTO<>();

	@Before
	public void beforeSet() throws ParseException, JsonProcessingException, org.json.simple.parser.ParseException {
		qrcodedto.setId("mosip.pre-registration.qrcode.generate");
		qrcodedto.setVersion("1.0");
		notificationDTO = new NotificationDTO();
		notificationDTO.setName("sanober Noor");
		notificationDTO.setPreRegistrationId("1234567890");
		notificationDTO.setMobNum("1234567890");
		notificationDTO.setEmailID("sanober.noor2@mindtree.com");
		notificationDTO.setAppointmentDate("2019-01-22");
		notificationDTO.setAppointmentTime("22:57");
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		mapper.setDateFormat(df);
		mapper.setTimeZone(TimeZone.getDefault());
		String jsonString = mapper.writeValueAsString(notificationDTO);
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject) parser.parse(jsonString);
		
		qrcodedto.setRequest(jsonObj);
		qrcodedto.setRequesttime(new Timestamp(System.currentTimeMillis()));
		responseDTO = new MainResponseDTO<>();
		responseDTO.setResponse(notificationDTO);
		responseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());

		notificationResponseDTO.setMessage("Notification send successfully");
		notificationResponseDTO.setStatus("True");
	}

	@Test
	public void generateQRCodeSuccessTest() throws QrcodeGenerationException, java.io.IOException {
		String stringjson = mapper.writeValueAsString(qrcodedto);
		byte[] qrCode = null;

		QRCodeResponseDTO responsedto = new QRCodeResponseDTO();
		responsedto.setQrcode(qrCode);
		qrCodeResponseDTO.setResponse(responsedto);
		qrCodeResponseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
		Mockito.when(qrCodeGenerator.generateQrCode(stringjson, QrVersion.V25)).thenReturn(qrCode);
		MainResponseDTO<QRCodeResponseDTO> response = service.generateQRCode(qrcodedto);

		assertEquals(qrCodeResponseDTO.getResponse(), response.getResponse());
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void generateQRCodeExceptionTest() throws java.io.IOException, QrcodeGenerationException {
		String stringjson = mapper.writeValueAsString(qrcodedto);
		notificationDTO = new NotificationDTO();
		qrcodedto.setRequest(null);
		byte[] qrCode = null;

		QRCodeResponseDTO responsedto = new QRCodeResponseDTO();
		responsedto.setQrcode(qrCode);
		Mockito.when(qrCodeGenerator.generateQrCode(stringjson, QrVersion.V25)).thenReturn(qrCode);
		service.generateQRCode(qrcodedto);

		 assertEquals(ErrorMessages.INVALID_REQUEST_BODY.getMessage(), qrCodeResponseDTO.getResponse());

	}
	@Test(expected = IllegalParamException.class)
	public void generateQRCodeFailureTest() throws java.io.IOException, QrcodeGenerationException {

		Mockito.when(qrCodeGenerator.generateQrCode(null, QrVersion.V25)).thenThrow(QrcodeGenerationException.class);
		service.generateQRCode(null);

		// assertEquals(null, qrCodeResponseDTO.getResponse());

	}
}
