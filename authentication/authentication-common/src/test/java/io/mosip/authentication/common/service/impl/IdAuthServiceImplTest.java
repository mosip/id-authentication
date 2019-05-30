package io.mosip.authentication.common.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.integration.IdRepoManager;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.spi.id.service.IdService;

/**
 * IdAuthServiceImplTest test class.
 *
 * @author Rakesh Roshan
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class IdAuthServiceImplTest {

	@Mock
	private IdRepoManager idRepoManager;
	@Mock
	private AuditRequestFactory auditFactory;
	@Mock
	private RestRequestFactory restFactory;
	@Mock
	private RestHelper restHelper;

	@InjectMocks
	IdServiceImpl idAuthServiceImpl;

	@Mock
	IdServiceImpl idAuthServiceImplMock;

	@Mock
	IdService<AutnTxn> idAuthService;

	@Mock
	AutnTxnRepository autntxnrepository;
	@Mock
	AutnTxn autnTxn;

	@Autowired
	Environment env;

	@Before
	public void before() {
		ReflectionTestUtils.setField(idAuthServiceImpl, "idRepoManager", idRepoManager);
		ReflectionTestUtils.setField(idAuthServiceImpl, "auditFactory", auditFactory);
		ReflectionTestUtils.setField(idAuthServiceImpl, "restFactory", restFactory);

	}

	@Test
	public void testAuditData() {
		ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "auditData");
	}

	@Test
	public void testGetIdRepoByVidAsRequest_IsNotNull() throws IdAuthenticationBusinessException {
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", "476567");
		idRepo.put("vid", "476567");
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Object invokeMethod = ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "getIdRepoByVidAsRequest",
				Mockito.anyString(), false);
		assertNotNull(invokeMethod);
	}

	@Test
	public void testProcessIdType_IdTypeIsD() throws IdAuthenticationBusinessException {
		String idvIdType = "UIN";
		String idvId = "875948796";
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", "476567");
		ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "processIdType", idvIdType, idvId, false);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessIdType_IdTypeIsV() throws IdAuthenticationBusinessException {
		String idvIdType = "VID";
		String idvId = "875948796";
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", "476567");
		Mockito.when(idRepoManager.getIdenity(Mockito.any(), Mockito.anyBoolean())).thenReturn(idRepo);
		Map<String, Object> idResponseMap = (Map<String, Object>) ReflectionTestUtils.invokeMethod(idAuthServiceImpl,
				"processIdType", idvIdType, idvId, false);
		assertEquals("476567", idResponseMap.get("uin"));
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void processIdtypeVIDFailed() throws IdAuthenticationBusinessException {
		String idvIdType = "VID";
		String idvId = "875948796";
		IdAuthenticationBusinessException idBusinessException = new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.INVALID_VID);
		Mockito.when(idRepoManager.getUINByVID(idvId))
		.thenThrow(idBusinessException);;
		idAuthServiceImpl.processIdType(idvIdType, idvId, false);

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void processIdtypeUINFailed() throws IdAuthenticationBusinessException {
		String idvIdType = "UIN";
		String idvId = "875948796";

		IdAuthenticationBusinessException idBusinessException = new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.INVALID_UIN);

		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean()))
				.thenThrow(idBusinessException);

		Mockito.when(idAuthService.getIdByVid(Mockito.anyString(), Mockito.anyBoolean()))
				.thenThrow(idBusinessException);
		Mockito.when(idAuthServiceImpl.processIdType(idvIdType, idvId, false)).thenThrow(idBusinessException);

	}

	@Test
	public void testSaveAutnTxn() {
		OtpRequestDTO otpRequestDto = getOtpRequestDTO();
		otpRequestDto.getRequestTime();
		otpRequestDto.getTransactionID();

		ReflectionTestUtils.invokeMethod(autntxnrepository, "saveAndFlush", autnTxn);
		ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "saveAutnTxn", autnTxn);
	}

	// =========================================================
	// ************ Helping Method *****************************
	// =========================================================
	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setId("id");
		otpRequestDto.setRequestTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));
		otpRequestDto.setTransactionID("2345678901234");
//		otpRequestDto.get("2345678901234");

		return otpRequestDto;
	}

	@Test
	public void testGetIdInfo()
			throws IdAuthenticationBusinessException, JsonParseException, JsonMappingException, IOException {
		String res = "{\r\n" + "  \"id\": \"mosip.id.read\",\r\n" + "  \"version\": \"1.0\",\r\n"
				+ "  \"timestamp\": \"2019-01-18T05:13:22.710Z\",\r\n" + "  \"status\": \"ACTIVATED\",\r\n"
				+ "  \"response\": {\r\n" + "    \"identity\": {\r\n" + "      \"IDSchemaVersion\": 1,\r\n"
				+ "      \"UIN\": 201786049258,\r\n" + "      \"fullName\": [\r\n" + "        {\r\n"
				+ "          \"language\": \"ara\",\r\n" + "          \"value\": \"ابراهيم بن علي\"\r\n"
				+ "        },\r\n" + "        {\r\n" + "          \"language\": \"fre\",\r\n"
				+ "          \"value\": \"Ibrahim Ibn Ali\"\r\n" + "        }\r\n" + "      ],\r\n"
				+ "      \"dateOfBirth\": \"1955/04/15\",\r\n" + "      \"age\": 45,\r\n" + "      \"gender\": [\r\n"
				+ "        {\r\n" + "          \"language\": \"ara\",\r\n" + "          \"value\": \"الذكر\"\r\n"
				+ "        },\r\n" + "        {\r\n" + "          \"language\": \"fre\",\r\n"
				+ "          \"value\": \"mâle\"\r\n" + "        }\r\n" + "      ],\r\n"
				+ "      \"addressLine1\": [\r\n" + "        {\r\n" + "          \"language\": \"ara\",\r\n"
				+ "          \"value\": \"عنوان العينة سطر 1\"\r\n" + "        },\r\n" + "        {\r\n"
				+ "          \"language\": \"fre\",\r\n" + "          \"value\": \"exemple d'adresse ligne 1\"\r\n"
				+ "        }\r\n" + "      ],\r\n" + "      \"addressLine2\": [\r\n" + "        {\r\n"
				+ "          \"language\": \"ara\",\r\n" + "          \"value\": \"عنوان العينة سطر 2\"\r\n"
				+ "        },\r\n" + "        {\r\n" + "          \"language\": \"fre\",\r\n"
				+ "          \"value\": \"exemple d'adresse ligne 2\"\r\n" + "        }\r\n" + "      ],\r\n"
				+ "      \"addressLine3\": [\r\n" + "        {\r\n" + "          \"language\": \"ara\",\r\n"
				+ "          \"value\": \"عنوان العينة سطر 2\"\r\n" + "        },\r\n" + "        {\r\n"
				+ "          \"language\": \"fre\",\r\n" + "          \"value\": \"exemple d'adresse ligne 2\"\r\n"
				+ "        }\r\n" + "      ],\r\n" + "      \"region\": [\r\n" + "        {\r\n"
				+ "          \"language\": \"ara\",\r\n" + "          \"value\": \"طنجة - تطوان - الحسيمة\"\r\n"
				+ "        },\r\n" + "        {\r\n" + "          \"language\": \"fre\",\r\n"
				+ "          \"value\": \"Tanger-Tétouan-Al Hoceima\"\r\n" + "        }\r\n" + "      ],\r\n"
				+ "      \"province\": [\r\n" + "        {\r\n" + "          \"language\": \"ara\",\r\n"
				+ "          \"value\": \"فاس-مكناس\"\r\n" + "        },\r\n" + "        {\r\n"
				+ "          \"language\": \"fre\",\r\n" + "          \"value\": \"Fès-Meknès\"\r\n" + "        }\r\n"
				+ "      ],\r\n" + "      \"city\": [\r\n" + "        {\r\n" + "          \"language\": \"ara\",\r\n"
				+ "          \"value\": \"الدار البيضاء\"\r\n" + "        },\r\n" + "        {\r\n"
				+ "          \"language\": \"fre\",\r\n" + "          \"value\": \"Casablanca\"\r\n" + "        }\r\n"
				+ "      ],\r\n" + "      \"postalCode\": \"570004\",\r\n" + "      \"phone\": \"9876543210\",\r\n"
				+ "      \"email\": \"abc@xyz.com\",\r\n" + "      \"CNIENumber\": 6789545678909,\r\n"
				+ "      \"localAdministrativeAuthority\": [\r\n" + "        {\r\n"
				+ "          \"language\": \"ara\",\r\n" + "          \"value\": \"سلمى\"\r\n" + "        },\r\n"
				+ "        {\r\n" + "          \"language\": \"fre\",\r\n" + "          \"value\": \"salma\"\r\n"
				+ "        }\r\n" + "      ],\r\n" + "      \"parentOrGuardianRIDOrUIN\": 212124324784912,\r\n"
				+ "      \"parentOrGuardianName\": [\r\n" + "        {\r\n" + "          \"language\": \"ara\",\r\n"
				+ "          \"value\": \"سلمى\"\r\n" + "        },\r\n" + "        {\r\n"
				+ "          \"language\": \"fre\",\r\n" + "          \"value\": \"salma\"\r\n" + "        }\r\n"
				+ "      ],\r\n" + "      \"proofOfAddress\": {\r\n" + "        \"format\": \"pdf\",\r\n"
				+ "        \"type\": \"drivingLicense\",\r\n" + "        \"value\": \"fileReferenceID\"\r\n"
				+ "      },\r\n" + "      \"proofOfIdentity\": {\r\n" + "        \"format\": \"txt\",\r\n"
				+ "        \"type\": \"passport\",\r\n" + "        \"value\": \"fileReferenceID\"\r\n" + "      },\r\n"
				+ "      \"proofOfRelationship\": {\r\n" + "        \"format\": \"pdf\",\r\n"
				+ "        \"type\": \"passport\",\r\n" + "        \"value\": \"fileReferenceID\"\r\n" + "      },\r\n"
				+ "      \"proofOfDateOfBirth\": {\r\n" + "        \"format\": \"pdf\",\r\n"
				+ "        \"type\": \"passport\",\r\n" + "        \"value\": \"fileReferenceID\"\r\n" + "      },\r\n"
				+ "      \"individualBiometrics\": {\r\n" + "        \"format\": \"cbeff\",\r\n"
				+ "        \"version\": 1,\r\n" + "        \"value\": \"fileReferenceID\"\r\n" + "      },\r\n"
				+ "      \"parentOrGuardianBiometrics\": {\r\n" + "        \"format\": \"cbeff\",\r\n"
				+ "        \"version\": 1,\r\n" + "        \"value\": \"fileReferenceID\"\r\n" + "      }\r\n"
				+ "    },\r\n" + "    \"documents\": [\r\n" + "      {\r\n"
				+ "        \"category\": \"individualBiometrics\",\r\n"
				+ "        \"value\": \"ew0KCQkJImxlZnRJbmRleCI6IFt7DQoJCQkJInZhbHVlIjogIlJrMVNBQ0F5TUFBQUFBRmNBQUFCUEFGaUFNVUF4UUVBQUFBb05VQjlBTUYwVjRDQkFLQkJQRUMwQUw2OFpJQzRBS2pOWkVCaUFKdldYVUJQQU5QV05VRFNBSzdSVUlDMkFRSWZaRURKQVBNeFBFQnlBR3dQWFlDcEFSWVBaRUNmQUZqb1pFQ0dBRXY5WkVCRUFGbXRWMEJwQVVHTlhVQy9BVUVFU1VDVUFWSUVQRUMyQVZOeFBJQ2NBTFd1WklDdUFMbTNaRUNOQUpxeFEwQ1VBSTNHUTBDWEFQZ2hWMEJWQUtET1pFQmZBUHFIWFVCREFLZS9aSUI5QUczeFhVRFBBSWJaVUVCY0FHWWhaRUNJQVNnSFhZQkpBR0FuVjBEakFSNGpHMERLQVRxSklVQ0dBREdTWkVEU0FVWUdJVUF4QUQrblYwQ1hBSytvU1VCb0FMcjZRNENTQU91S1hVQ2lBSXZOWkVDOUFKelFaSUJOQUxiVFhVQkJBTDY4VjBDZUFIRFpaRUN3QUhQYVpFQlJBUHdIVUlCSEFIVzJYVURYQVJBVURVQzRBUzRIWkVEWEFTMENRMENZQURMNFpFQ3NBVXp1UEVCa0FDZ1JaQUFBIg0KCQkJfV0sDQoJCQkicmlnaHRJbmRleCI6IFt7DQoJCQkJInZhbHVlIjogIlJrMVNBQ0F5TUFBQUFBRmNBQUFCUEFGaUFNVUF4UUVBQUFBb05VQ3RBTXZsWklDUkFPbFhYWURCQVBCcVpFQ2tBS1BXWFlEWkFOOW9aRUJ6QU1YUlNZQzZBSk5jVjRETUFKZFpYVURwQU85elpJQnlBUUptWkVDREFKQzdaSURmQVE1NlhVQmVBUFpnVjBCaEFKd3lWMERWQVNzQlhVRUVBSi9SWkVCWkFJcTZVRUMrQVZWN1NVQ2pBT2hlWFVDSEFObmNWMERGQUs1Y1hVRFpBTkpmWFVCM0FOQlJTVUNkQUpyS1hVQ0pBSnZFWFVDeEFROTFYVUNqQVJqN1hZQ3VBSVhlVjRDVUFJQzRaSURSQVJyM1hVREZBSDVqVjBCb0FKRzFaRURjQVNXQ1VFRFdBSEZmWkVCbkFTcDRTVURoQVZkMUZFQ01BTHpXVjBES0FMeFlYVUN3QUtiZlYwRFNBT3BzWkVDQUFLODlWNENTQVFkcFpJQ3BBSlBXVjBEb0FPUmlYVUNtQUlhelYwQ3hBUnQ3WFVDREFScDBaRUQ5QU5OVFpJRGhBSXpTWklDU0FIU2paSUJYQVFweFVFQ2dBR2FJWFlDQkFVSi9aQUFBIg0KCQkJfV0NCgkJfQ0KCX0\"\r\n"
				+ "      }\r\n" + "    ]\r\n" + "  }\r\n" + "}";
		ObjectMapper mapper = new ObjectMapper();
		byte resByte[] = res.getBytes();
		String value = new String(resByte, "UTF-8");

		@SuppressWarnings("unchecked")
		Map<String, Object> idResponseDTO = mapper.readValue(value, Map.class);
		ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "getIdInfo", idResponseDTO);
	}

	@Test
	public void testGetIdInfo1()
			throws IdAuthenticationBusinessException, JsonParseException, JsonMappingException, IOException {

		String res = "{\r\n" + "	\"id\": \"mosip.id.read\",\r\n" + "	\"timestamp\": \"2019-01-02T09:10:05.506\",\r\n"
				+ "	\"registrationId\": \"1234234320000920181212010055\",\r\n" + "	\"status\": \"REGISTERED\",\r\n"
				+ "	\"response\": {}\r\n" + "}";

		ObjectMapper mapper = new ObjectMapper();
		byte resByte[] = res.getBytes();
		String value = new String(resByte, "UTF-8");

		@SuppressWarnings("unchecked")
		Map<String, Object> idResponseDTO = mapper.readValue(value, Map.class);
		ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "getIdInfo", idResponseDTO);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testIdRepoServiceException_UINDeActivated() throws Throwable {
		try {
			Map<String, Object> idRepo = new HashMap<>();
			String vid = "476567";
			idRepo.put("uin", vid);
			IdAuthenticationBusinessException idBusinessException = new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.UIN_DEACTIVATED);
			Mockito.when(idRepoManager.getUINByVID(vid))
			.thenReturn(12345l);
			Mockito.when(idRepoManager.getIdenity("12345", false))
			.thenThrow(idBusinessException);
			//Mockito.when(vidRepository.findUinByVid(Mockito.any())).thenReturn(optVID);
			ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "getIdRepoByVidAsRequest",
					vid, false);

		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testIdRepoServiceException_InvalidUIN() throws Throwable {
		try {
			Map<String, Object> idRepo = new HashMap<>();
			String vid = "476567";
			idRepo.put("uin", vid);
			IdAuthenticationBusinessException idBusinessException = new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.INVALID_UIN);

			
			Mockito.when(idRepoManager.getUINByVID(vid))
			.thenReturn(12345l);
			Mockito.when(idRepoManager.getIdenity("12345", false))
			.thenThrow(idBusinessException);
			//Mockito.when(vidRepository.findUinByVid(Mockito.any())).thenReturn(optVID);
			ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "getIdRepoByVidAsRequest",
					vid, false);

		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void testIdRepoServiceException_UINDeactivated() throws Throwable {
		try {
			Map<String, Object> idRepo = new HashMap<>();
			String vid = "476567";
			idRepo.put("uin", vid);
			IdAuthenticationBusinessException idBusinessException = new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.VID_DEACTIVATED_UIN);

			
			Mockito.when(idRepoManager.getUINByVID(vid))
			.thenReturn(12345l);
			Mockito.when(idRepoManager.getIdenity("12345", false))
			.thenThrow(idBusinessException);
			//Mockito.when(vidRepository.findUinByVid(Mockito.any())).thenReturn(optVID);
			ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "getIdRepoByVidAsRequest",
					vid, false);

		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}
}
