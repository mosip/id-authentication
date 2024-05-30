package io.mosip.authentication.common.service.impl;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.util.CryptoUtil;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.RestHelper;

/**
 * IdAuthServiceImplTest test class.
 *
 * @author Rakesh Roshan
 */
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class IdAuthServiceImplTest {

	@Mock
	private AuditRequestFactory auditFactory;
	@Mock
	private RestRequestFactory restFactory;
	@Mock
	private RestHelper restHelper;

	@Mock
	private IdAuthSecurityManager securityManager;

	@InjectMocks
	IdServiceImpl idServiceImpl;

	@Mock
	IdServiceImpl idServiceImplMock;

	@Mock
	IdService<AutnTxn> idAuthService;

	@Mock
	AutnTxnRepository autntxnrepository;
	@Mock
	AutnTxn autnTxn;

	@Autowired
	EnvUtil env;
	
	@Mock
	private IdentityCacheRepository identityRepo;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(idServiceImpl, "mapper", mapper);

	}

	@Test
	@Ignore
	public void testGetIdRepoByVidAsRequest_IsNotNull() throws IdAuthenticationBusinessException {
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", "476567");
		idRepo.put("vid", "476567");
		Object invokeMethod = ReflectionTestUtils.invokeMethod(idServiceImpl, "getIdRepoByVidAsRequest",
				Mockito.anyString(), false);
		assertNotNull(invokeMethod);
	}

	@Test
	public void testProcessIdType_IdTypeIsD() throws Throwable {
		String idvIdType = "UIN";
		String idvId = "875948796";
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", "476567");
		try {
			Mockito.when(securityManager.hash(idvId)).thenReturn(idvId);
			Mockito.when(identityRepo.existsById(idvId)).thenReturn(true);
			IdentityEntity entity = new IdentityEntity();
			byte[] demoData = ("{\"UIN\":" + idvId + "}").getBytes();
			byte[] bioData = CryptoUtil.encodeBase64("fingreprintdata".getBytes()).getBytes();
			entity.setDemographicData(demoData);
			Mockito.when(identityRepo.getOne(idvId)).thenReturn(entity);
			List<Object[]> data = new ArrayList<>();
			Object[] iddata = new Object[] {idvId, demoData, null, null};
			data.add(iddata);
			Mockito.when(identityRepo.findDemoDataById(idvId)).thenReturn(data);
			Map<String, String> demoDataMap = mapper.readValue(demoData, Map.class);
			Map<String, String> bioDataMap = mapper.readValue(bioData, Map.class);
			Mockito.when(securityManager.zkDecrypt(idvId, demoDataMap)).thenReturn(demoDataMap);
			Mockito.when(securityManager.zkDecrypt(idvId, bioDataMap)).thenReturn(bioDataMap);
			ReflectionTestUtils.invokeMethod(idServiceImpl, "processIdType", idvIdType, idvId, false);
		} catch (Exception e) {
			e.printStackTrace();
			throw e.getCause();
		} 
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessIdType_IdTypeIsV() throws Throwable {
		String idvIdType = "VID";
		String idvId = "875948796";
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", "476567");
		try {
			Mockito.when(securityManager.hash(idvId)).thenReturn(idvId);
			Mockito.when(identityRepo.existsById(idvId)).thenReturn(true);
			IdentityEntity entity = new IdentityEntity();
			byte[] demoData = ("{\"UIN\":" + idvId + "}").getBytes();
			byte[] bioData = CryptoUtil.encodeBase64("fingreprintdata".getBytes()).getBytes();
			entity.setDemographicData(demoData);
			Mockito.when(identityRepo.getOne(idvId)).thenReturn(entity);
			List<Object[]> data = new ArrayList<>();
			Object[] iddata = new Object[] {idvId, demoData, null, null};
			data.add(iddata);
			Mockito.when(identityRepo.findDemoDataById(idvId)).thenReturn(data);
			Map<String, String> demoDataMap = mapper.readValue(demoData, Map.class);
			Map<String, String> bioDataMap = mapper.readValue(bioData, Map.class);
			Mockito.when(securityManager.zkDecrypt(idvId, demoDataMap)).thenReturn(demoDataMap);
			Mockito.when(securityManager.zkDecrypt(idvId, bioDataMap)).thenReturn(bioDataMap);
			ReflectionTestUtils.invokeMethod(idServiceImpl, "processIdType", idvIdType, idvId, false);
		} catch (Exception e) {
			e.printStackTrace();
			throw e.getCause();
		}
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void processIdtypeVIDFailed() throws IdAuthenticationBusinessException {
		String idvIdType = "VID";
		String idvId = "875948796";
		new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.INVALID_VID);
		Mockito.when(identityRepo.existsById(idvId)).thenReturn(false);
		
		idServiceImpl.processIdType(idvIdType, idvId, false, true, Collections.emptySet());

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void processIdtypeUINFailed() throws IdAuthenticationBusinessException {
		String idvIdType = "UIN";
		String idvId = "875948796";

		IdAuthenticationBusinessException idBusinessException = new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.INVALID_UIN);

		Mockito.when(identityRepo.existsById(idvId)).thenReturn(false);

		Mockito.when(idAuthService.getIdByVid(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet()))
				.thenThrow(idBusinessException);
		Mockito.when(idServiceImpl.processIdType(idvIdType, idvId, false, true, Collections.emptySet())).thenThrow(idBusinessException);

	}

	@Test
	public void testSaveAutnTxn() {
		OtpRequestDTO otpRequestDto = getOtpRequestDTO();
		otpRequestDto.getRequestTime();
		otpRequestDto.getTransactionID();

		ReflectionTestUtils.invokeMethod(autntxnrepository, "saveAndFlush", autnTxn);
		ReflectionTestUtils.invokeMethod(idServiceImpl, "saveAutnTxn", autnTxn);
	}

	// =========================================================
	// ************ Helping Method *****************************
	// =========================================================
	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setId("id");
		otpRequestDto.setRequestTime(new SimpleDateFormat(EnvUtil.getDateTimePattern()).format(new Date()));
		otpRequestDto.setTransactionID("2345678901234");
		// otpRequestDto.get("2345678901234");

		return otpRequestDto;
	}

	@Test
	public void testGetIdInfo()
			throws IdAuthenticationBusinessException, JsonParseException, JsonMappingException, IOException {
		String res = "{\r\n" + "  \"id\": \"mosip.id.read\",\r\n" + "  \"version\": \"1.0\",\r\n"
				+ "  \"timestamp\": \"2019-01-18T05:13:22.710Z\",\r\n" + "  \"status\": \"ACTIVATED\",\r\n"
				+ "  \"response\": {\r\n" + "    \"identity\": {\r\n" + "      \"IDSchemaVersion\": 1,\r\n"
				+ "      \"UIN\": 201786049258,\r\n" + "      \"fullName\": [\r\n" + "        {\r\n"
				+ "          \"language\": \"ara\",\r\n" + "          \"value\": \"Ø§Ø¨Ø±Ø§Ù‡ÙŠÙ… Ø¨Ù† Ø¹Ù„ÙŠ\"\r\n"
				+ "        },\r\n" + "        {\r\n" + "          \"language\": \"fre\",\r\n"
				+ "          \"value\": \"Ibrahim Ibn Ali\"\r\n" + "        }\r\n" + "      ],\r\n"
				+ "      \"dateOfBirth\": \"1955/04/15\",\r\n" + "      \"age\": 45,\r\n" + "      \"gender\": [\r\n"
				+ "        {\r\n" + "          \"language\": \"ara\",\r\n" + "          \"value\": \"Ø§Ù„Ø°ÙƒØ±\"\r\n"
				+ "        },\r\n" + "        {\r\n" + "          \"language\": \"fre\",\r\n"
				+ "          \"value\": \"mÃ¢le\"\r\n" + "        }\r\n" + "      ],\r\n"
				+ "      \"addressLine1\": [\r\n" + "        {\r\n" + "          \"language\": \"ara\",\r\n"
				+ "          \"value\": \"Ø¹Ù†ÙˆØ§Ù† Ø§Ù„Ø¹ÙŠÙ†Ø© Ø³Ø·Ø± 1\"\r\n" + "        },\r\n" + "        {\r\n"
				+ "          \"language\": \"fre\",\r\n" + "          \"value\": \"exemple d'adresse ligne 1\"\r\n"
				+ "        }\r\n" + "      ],\r\n" + "      \"addressLine2\": [\r\n" + "        {\r\n"
				+ "          \"language\": \"ara\",\r\n"
				+ "          \"value\": \"Ø¹Ù†ÙˆØ§Ù† Ø§Ù„Ø¹ÙŠÙ†Ø© Ø³Ø·Ø± 2\"\r\n" + "        },\r\n" + "        {\r\n"
				+ "          \"language\": \"fre\",\r\n" + "          \"value\": \"exemple d'adresse ligne 2\"\r\n"
				+ "        }\r\n" + "      ],\r\n" + "      \"addressLine3\": [\r\n" + "        {\r\n"
				+ "          \"language\": \"ara\",\r\n"
				+ "          \"value\": \"Ø¹Ù†ÙˆØ§Ù† Ø§Ù„Ø¹ÙŠÙ†Ø© Ø³Ø·Ø± 2\"\r\n" + "        },\r\n" + "        {\r\n"
				+ "          \"language\": \"fre\",\r\n" + "          \"value\": \"exemple d'adresse ligne 2\"\r\n"
				+ "        }\r\n" + "      ],\r\n" + "      \"region\": [\r\n" + "        {\r\n"
				+ "          \"language\": \"ara\",\r\n"
				+ "          \"value\": \"Ø·Ù†Ø¬Ø© - ØªØ·ÙˆØ§Ù† - Ø§Ù„Ø­Ø³ÙŠÙ…Ø©\"\r\n" + "        },\r\n"
				+ "        {\r\n" + "          \"language\": \"fre\",\r\n"
				+ "          \"value\": \"Tanger-TÃ©touan-Al Hoceima\"\r\n" + "        }\r\n" + "      ],\r\n"
				+ "      \"province\": [\r\n" + "        {\r\n" + "          \"language\": \"ara\",\r\n"
				+ "          \"value\": \"Ù�Ø§Ø³-Ù…ÙƒÙ†Ø§Ø³\"\r\n" + "        },\r\n" + "        {\r\n"
				+ "          \"language\": \"fre\",\r\n" + "          \"value\": \"FÃ¨s-MeknÃ¨s\"\r\n" + "        }\r\n"
				+ "      ],\r\n" + "      \"city\": [\r\n" + "        {\r\n" + "          \"language\": \"ara\",\r\n"
				+ "          \"value\": \"Ø§Ù„Ø¯Ø§Ø± Ø§Ù„Ø¨ÙŠØ¶Ø§Ø¡\"\r\n" + "        },\r\n" + "        {\r\n"
				+ "          \"language\": \"fre\",\r\n" + "          \"value\": \"Casablanca\"\r\n" + "        }\r\n"
				+ "      ],\r\n" + "      \"postalCode\": \"570004\",\r\n" + "      \"phone\": \"9876543210\",\r\n"
				+ "      \"email\": \"abc@xyz.com\",\r\n" + "      \"CNIENumber\": 6789545678909,\r\n"
				+ "      \"localAdministrativeAuthority\": [\r\n" + "        {\r\n"
				+ "          \"language\": \"ara\",\r\n" + "          \"value\": \"Ø³Ù„Ù…Ù‰\"\r\n" + "        },\r\n"
				+ "        {\r\n" + "          \"language\": \"fre\",\r\n" + "          \"value\": \"salma\"\r\n"
				+ "        }\r\n" + "      ],\r\n" + "      \"parentOrGuardianRIDOrUIN\": 212124324784912,\r\n"
				+ "      \"parentOrGuardianName\": [\r\n" + "        {\r\n" + "          \"language\": \"ara\",\r\n"
				+ "          \"value\": \"Ø³Ù„Ù…Ù‰\"\r\n" + "        },\r\n" + "        {\r\n"
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
		ReflectionTestUtils.invokeMethod(idServiceImpl, "getIdInfo", idResponseDTO);
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
		ReflectionTestUtils.invokeMethod(idServiceImpl, "getIdInfo", idResponseDTO);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	@Ignore
	public void testIdRepoServiceException_UINDeActivated() throws Throwable {
		try {
			Map<String, Object> idRepo = new HashMap<>();
			String vid = "476567";
			idRepo.put("uin", vid);			
			ReflectionTestUtils.invokeMethod(idServiceImpl, "getIdRepoByVidAsRequest", vid, false);
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	@Ignore
	public void testIdRepoServiceException_InvalidUIN() throws Throwable {
		try {
			Map<String, Object> idRepo = new HashMap<>();
			String vid = "476567";
			idRepo.put("uin", vid);
			ReflectionTestUtils.invokeMethod(idServiceImpl, "getIdRepoByVidAsRequest", vid, false);
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	@Ignore
	public void testIdRepoServiceException_UINDeactivated() throws Throwable {
		try {
			Map<String, Object> idRepo = new HashMap<>();
			String vid = "476567";
			idRepo.put("uin", vid);
			ReflectionTestUtils.invokeMethod(idServiceImpl, "getIdRepoByVidAsRequest", vid, false);
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}
	
	@Test
	public void testUpdateVIDStatus() throws RestServiceException, IdAuthenticationBusinessException {
		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("234433356");
		Mockito.when(identityRepo.existsById(Mockito.anyString())).thenReturn(false);
		ReflectionTestUtils.invokeMethod(idServiceImpl, "updateVIDstatus", "234433356");
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testUpdateVIDStatusFailed() throws RestServiceException, IdAuthenticationBusinessException {
		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("234433356");
		Mockito.when(identityRepo.existsById(Mockito.anyString())).thenReturn(true);
		Mockito.when(identityRepo.getOne(Mockito.anyString())).thenThrow(new DataAccessException("error") {});
		ReflectionTestUtils.invokeMethod(idServiceImpl, "updateVIDstatus", "234433356");
	}

}
