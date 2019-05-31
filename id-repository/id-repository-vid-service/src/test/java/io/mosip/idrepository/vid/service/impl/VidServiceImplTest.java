package io.mosip.idrepository.vid.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.TransactionException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.idrepository.core.builder.RestRequestBuilder;
import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.constant.RestServicesConstants;
import io.mosip.idrepository.core.dto.IdResponseDTO;
import io.mosip.idrepository.core.dto.ResponseDTO;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.dto.VidPolicy;
import io.mosip.idrepository.core.dto.VidRequestDTO;
import io.mosip.idrepository.core.dto.VidResponseDTO;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.exception.IdRepoAppUncheckedException;
import io.mosip.idrepository.core.exception.IdRepoDataValidationException;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.AuditHelper;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.idrepository.vid.entity.Vid;
import io.mosip.idrepository.vid.provider.VidPolicyProvider;
import io.mosip.idrepository.vid.repository.VidRepo;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.idgenerator.spi.VidGenerator;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.idgenerator.vid.exception.VidException;

/**
 * 
 * @author Prem Kumar
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles("test")
@ConfigurationProperties("mosip.idrepo.vid")
@Ignore
public class VidServiceImplTest {

	@InjectMocks
	private VidServiceImpl service;

	@Mock
	private VidRepo vidRepo;

	@Mock
	private VidPolicyProvider vidPolicyProvider;

	@Mock
	private RestRequestBuilder restBuilder;

	@Mock
	private AuditHelper auditHelper;

	@Mock
	private RestHelper restHelper;

	@Mock
	private WebClient webClient;

	@Mock
	private VidGenerator<String> vidGenerator;

	/** The security manager. */
	@Mock
	private IdRepoSecurityManager securityManager;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	@Autowired
	Environment environment;

	private Map<String, String> id;

	public void setId(Map<String, String> id) {
		this.id = id;
	}

	@Before
	public void before() {
		ReflectionTestUtils.setField(service, "env", environment);
		ReflectionTestUtils.setField(restHelper, "mapper", mapper);
		ReflectionTestUtils.setField(service, "id", id);
	}

	@Test
	public void testCreateVid() throws IdRepoAppException {
		when(securityManager.hash(Mockito.any())).thenReturn("123");
		when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any(Class.class)))
				.thenReturn(new RestRequestDTO());
		IdResponseDTO identityResponse = new IdResponseDTO();
		ResponseDTO response = new ResponseDTO();
		response.setStatus("ACTIVATED");
		identityResponse.setResponse(response);
		when(restHelper.requestSync(Mockito.any())).thenReturn(identityResponse);
		VidPolicy policy = new VidPolicy();
		policy.setAllowedInstances(2);
		when(vidPolicyProvider.getPolicy(Mockito.any())).thenReturn(policy);
		Vid vid = new Vid();
		vid.setVid("123");
		vid.setStatusCode("");
		when(vidRepo.findByUinHashAndStatusCodeAndVidTypeCode(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Collections.singletonList(vid));
		when(vidRepo.save(Mockito.any())).thenReturn(vid);
		VidRequestDTO request = new VidRequestDTO();
		request.setUin(2953190571L);
		ResponseWrapper<VidResponseDTO> vidResponse = service.createVid(request);
		assertEquals(vidResponse.getResponse().getVid().toString(), vid.getVid());
		assertEquals(vidResponse.getResponse().getVidStatus(), vid.getStatusCode());
	}

	@Test
	public void testCreateVidInstanceFail() throws RestServiceException, IdRepoDataValidationException {
		when(securityManager.hash(Mockito.any())).thenReturn("123");
		when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any(Class.class)))
				.thenReturn(new RestRequestDTO());
		IdResponseDTO identityResponse = new IdResponseDTO();
		ResponseDTO response = new ResponseDTO();
		response.setStatus("ACTIVATED");
		identityResponse.setResponse(response);
		when(restHelper.requestSync(Mockito.any())).thenReturn(identityResponse);
		VidPolicy policy = new VidPolicy();
		policy.setAllowedInstances(2);
		when(vidPolicyProvider.getPolicy(Mockito.any())).thenReturn(policy);
		Vid vid = new Vid();
		vid.setVid("123");
		vid.setStatusCode("");
		when(vidRepo.findByUinHashAndStatusCodeAndVidTypeCode(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Collections.singletonList(vid));
		when(vidRepo.save(Mockito.any())).thenReturn(vid);
		VidRequestDTO request = new VidRequestDTO();
		request.setUin(2953190571L);
		try {
			service.createVid(request);
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testCreateVidPolicyFailed() throws RestServiceException, IdRepoDataValidationException {
		when(securityManager.hash(Mockito.any())).thenReturn("123");
		when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any(Class.class)))
				.thenReturn(new RestRequestDTO());
		IdResponseDTO identityResponse = new IdResponseDTO();
		ResponseDTO response = new ResponseDTO();
		response.setStatus("ACTIVATED");
		identityResponse.setResponse(response);
		when(restHelper.requestSync(Mockito.any())).thenReturn(identityResponse);
		VidPolicy policy = new VidPolicy();
		policy.setAllowedInstances(0);
		when(vidPolicyProvider.getPolicy(Mockito.any())).thenReturn(policy);
		Vid vid = new Vid();
		vid.setVid("123");
		vid.setStatusCode("");
		when(vidRepo.findByUinHashAndStatusCodeAndVidTypeCode(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Collections.singletonList(vid));
		when(vidRepo.save(Mockito.any())).thenReturn(vid);
		VidRequestDTO request = new VidRequestDTO();
		request.setUin(2953190571L);
		when(vidGenerator.generateId()).thenThrow(new VidException("", "", null));
		try {
			service.createVid(request);
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.VID_POLICY_FAILED.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.VID_POLICY_FAILED.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testCreateVidUinNotActive() throws IdRepoAppException {
		when(securityManager.hash(Mockito.any())).thenReturn("123");
		when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any(Class.class)))
				.thenReturn(new RestRequestDTO());
		IdResponseDTO identityResponse = new IdResponseDTO();
		ResponseDTO response = new ResponseDTO();
		response.setStatus("DEACTIVATED");
		identityResponse.setResponse(response);
		when(restHelper.requestSync(Mockito.any())).thenReturn(identityResponse);
		VidPolicy policy = new VidPolicy();
		policy.setAllowedInstances(2);
		when(vidPolicyProvider.getPolicy(Mockito.any())).thenReturn(policy);
		Vid vid = new Vid();
		vid.setVid("123");
		vid.setStatusCode("");
		when(vidRepo.findByUinHashAndStatusCodeAndVidTypeCode(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Collections.singletonList(vid));
		when(vidRepo.save(Mockito.any())).thenReturn(vid);
		VidRequestDTO request = new VidRequestDTO();
		request.setUin(2953190571L);
		try {
			service.createVid(request);
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.INVALID_UIN.getErrorCode(), e.getErrorCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_UIN.getErrorMessage(), "DEACTIVATED"),
					e.getErrorText());
		}
	}

	@Test
	public void testCreateVidUinNotFound() throws IdRepoAppException, JsonProcessingException {
		when(securityManager.hash(Mockito.any())).thenReturn("123");
		when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any(Class.class)))
				.thenReturn(new RestRequestDTO());
		IdResponseDTO identityResponse = new IdResponseDTO();
		ResponseDTO response = new ResponseDTO();
		response.setStatus("ACTIVATED");
		identityResponse.setResponse(response);
		identityResponse.setErrors(
				Collections.singletonList(new ServiceError(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode(),
						IdRepoErrorConstants.NO_RECORD_FOUND.getErrorMessage())));
		RestServiceException exception = new RestServiceException(IdRepoErrorConstants.NO_RECORD_FOUND,
				mapper.writeValueAsString(identityResponse), null);
		when(restHelper.requestSync(Mockito.any())).thenThrow(exception);
		VidPolicy policy = new VidPolicy();
		policy.setAllowedInstances(2);
		when(vidPolicyProvider.getPolicy(Mockito.any())).thenReturn(policy);
		Vid vid = new Vid();
		vid.setVid("123");
		vid.setStatusCode("");
		when(vidRepo.findByUinHashAndStatusCodeAndVidTypeCode(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Collections.singletonList(vid));
		when(vidRepo.save(Mockito.any())).thenReturn(vid);
		VidRequestDTO vidRequest = new VidRequestDTO();
		vidRequest.setUin(2953190571L);
		try {
			service.createVid(vidRequest);
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testCreateVidFailedUinRetrieval() throws IdRepoAppException, JsonProcessingException {
		when(securityManager.hash(Mockito.any())).thenReturn("123");
		when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any(Class.class)))
				.thenReturn(new RestRequestDTO());
		IdResponseDTO identityResponse = new IdResponseDTO();
		ResponseDTO response = new ResponseDTO();
		response.setStatus("ACTIVATED");
		identityResponse.setResponse(response);
		identityResponse.setErrors(
				Collections.singletonList(new ServiceError(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode(),
						IdRepoErrorConstants.NO_RECORD_FOUND.getErrorMessage())));
		RestServiceException exception = new RestServiceException();
		when(restHelper.requestSync(Mockito.any())).thenThrow(exception);
		VidPolicy policy = new VidPolicy();
		policy.setAllowedInstances(2);
		when(vidPolicyProvider.getPolicy(Mockito.any())).thenReturn(policy);
		Vid vid = new Vid();
		vid.setVid("123");
		vid.setStatusCode("");
		when(vidRepo.findByUinHashAndStatusCodeAndVidTypeCode(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Collections.singletonList(vid));
		when(vidRepo.save(Mockito.any())).thenReturn(vid);
		VidRequestDTO vidRequest = new VidRequestDTO();
		vidRequest.setUin(2953190571L);
		try {
			service.createVid(vidRequest);
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.UIN_RETRIEVAL_FAILED.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.UIN_RETRIEVAL_FAILED.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testCreateVidGenerationFailed() throws RestServiceException, IdRepoDataValidationException {
		when(securityManager.hash(Mockito.any())).thenReturn("123");
		when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any(Class.class)))
				.thenReturn(new RestRequestDTO());
		IdResponseDTO identityResponse = new IdResponseDTO();
		ResponseDTO response = new ResponseDTO();
		response.setStatus("ACTIVATED");
		identityResponse.setResponse(response);
		when(restHelper.requestSync(Mockito.any())).thenReturn(identityResponse);
		VidPolicy policy = new VidPolicy();
		policy.setAllowedInstances(2);
		when(vidPolicyProvider.getPolicy(Mockito.any())).thenReturn(policy);
		Vid vid = new Vid();
		vid.setVid("123");
		vid.setStatusCode("");
		when(vidRepo.findByUinHashAndStatusCodeAndVidTypeCode(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Collections.singletonList(vid));
		when(vidRepo.save(Mockito.any())).thenReturn(vid);
		VidRequestDTO request = new VidRequestDTO();
		request.setUin(2953190571L);
		when(vidGenerator.generateId()).thenThrow(new VidException("", "", null));
		try {
			service.createVid(request);
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testCreateVidIdRepoAppUncheckedException() throws RestServiceException, IdRepoDataValidationException {
		when(securityManager.hash(Mockito.any()))
				.thenThrow(new IdRepoAppUncheckedException(IdRepoErrorConstants.VID_GENERATION_FAILED));
		try {
			VidRequestDTO request = new VidRequestDTO();
			request.setUin(2953190571L);
			service.createVid(request);
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorMessage(), e.getErrorText());
		}
	}

	@SuppressWarnings("serial")
	@Test
	public void testCreateVidTransactionFailed() throws RestServiceException, IdRepoDataValidationException {
		when(securityManager.hash(Mockito.any())).thenThrow(new TransactionException("") {
		});
		try {
			VidRequestDTO request = new VidRequestDTO();
			request.setUin(2953190571L);
			service.createVid(request);
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testRetrieveVidIdRepoAppUncheckedException()
			throws RestServiceException, IdRepoDataValidationException {
		when(vidRepo.findByVid(Mockito.any()))
				.thenThrow(new IdRepoAppUncheckedException(IdRepoErrorConstants.VID_GENERATION_FAILED));
		try {
			VidRequestDTO request = new VidRequestDTO();
			request.setUin(2953190571L);
			service.updateVid("123", request);
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorMessage(), e.getErrorText());
		}
	}

	@SuppressWarnings("serial")
	@Test
	public void testUpdateVidTransactionFailed() throws RestServiceException, IdRepoDataValidationException {
		when(vidRepo.findByVid(Mockito.any())).thenThrow(new TransactionException("") {
		});
		try {
			VidRequestDTO request = new VidRequestDTO();
			request.setUin(2953190571L);
			service.updateVid("123", request);
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testUpdateVidIdRepoAppUncheckedException() throws RestServiceException, IdRepoDataValidationException {
		when(vidRepo.findByVid(Mockito.any()))
				.thenThrow(new IdRepoAppUncheckedException(IdRepoErrorConstants.VID_GENERATION_FAILED));
		try {
			service.retrieveUinByVid("123");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorMessage(), e.getErrorText());
		}
	}

	@SuppressWarnings("serial")
	@Test
	public void testRetrieveVidTransactionFailed() throws RestServiceException, IdRepoDataValidationException {
		when(vidRepo.findByVid(Mockito.any())).thenThrow(new TransactionException("") {
		});
		try {
			service.retrieveUinByVid("123");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testCreateVidRestDataValidationFailed() throws IdRepoAppException, JsonProcessingException {
		when(securityManager.hash(Mockito.any())).thenReturn("123");
		when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any(Class.class)))
				.thenThrow(new IdRepoDataValidationException("", ""));
		IdResponseDTO identityResponse = new IdResponseDTO();
		ResponseDTO response = new ResponseDTO();
		response.setStatus("ACTIVATED");
		identityResponse.setResponse(response);
		identityResponse.setErrors(
				Collections.singletonList(new ServiceError(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode(),
						IdRepoErrorConstants.NO_RECORD_FOUND.getErrorMessage())));
		RestServiceException exception = new RestServiceException();
		when(restHelper.requestSync(Mockito.any())).thenThrow(exception);
		VidPolicy policy = new VidPolicy();
		policy.setAllowedInstances(2);
		when(vidPolicyProvider.getPolicy(Mockito.any())).thenReturn(policy);
		Vid vid = new Vid();
		vid.setVid("123");
		vid.setStatusCode("");
		when(vidRepo.findByUinHashAndStatusCodeAndVidTypeCode(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Collections.singletonList(vid));
		when(vidRepo.save(Mockito.any())).thenReturn(vid);
		VidRequestDTO request = new VidRequestDTO();
		request.setUin(2953190571L);
		try {
			service.createVid(request);
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), e.getErrorCode());
			assertEquals("", e.getErrorText());
		}
	}

	@Test
	public void testRetrieveUinByVid() throws IdRepoAppException {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime().plusDays(1);
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "ACTIVE", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		when(securityManager.hash(Mockito.any())).thenReturn("123");
		when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any(Class.class)))
				.thenReturn(new RestRequestDTO());
		IdResponseDTO identityResponse = new IdResponseDTO();
		ResponseDTO response = new ResponseDTO();
		response.setStatus("ACTIVATED");
		identityResponse.setResponse(response);
		when(restHelper.requestSync(Mockito.any())).thenReturn(identityResponse);
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
		service.retrieveUinByVid("12345678");
	}

	@Test
	public void testRetrieveUinByVidExpired() {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime();
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "ACTIVATED", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
		try {
			service.retrieveUinByVid("12345678");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.INVALID_VID.getErrorCode(), e.getErrorCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_VID.getErrorMessage(), "EXPIRED"),
					e.getErrorText());
		}
	}

	@Test
	public void testRetrieveUinByVidBlocked() {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime().plusDays(1);
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "Blocked", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
		try {
			service.retrieveUinByVid("12345678");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.INVALID_VID.getErrorCode(), e.getErrorCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_VID.getErrorMessage(), "Blocked"),
					e.getErrorText());
		}
	}

	@Test
	public void testRetrieveUinByVidInvalidNoRecordsFound() {
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(null);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
		try {
			service.retrieveUinByVid("12345678");
		} catch (IdRepoAppException e) {
			assertEquals("IDR-IDC-007 --> No Record(s) found", e.getMessage());
		}
	}

	@Test
	public void testUpdateVidvalid() throws IdRepoAppException {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime().plusDays(1);
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "ACTIVE", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
		VidPolicy policy = new VidPolicy();
		policy.setAllowedInstances(1);
		policy.setAllowedTransactions(null);
		policy.setAutoRestoreAllowed(true);
		policy.setRestoreOnAction("REVOKE");
		policy.setValidForInMinutes(null);
		Mockito.when(vidPolicyProvider.getPolicy(Mockito.anyString())).thenReturn(policy);
		VidRequestDTO request = new VidRequestDTO();
		request.setVidStatus("ACTIVE");
		service.updateVid("12345678", request);
	}

	@Test
	public void testUpdateVidvalidREVOKE() throws IdRepoAppException {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime().plusDays(1);
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "ACTIVE", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
		VidPolicy policy = new VidPolicy();
		policy.setAllowedInstances(1);
		policy.setAllowedTransactions(null);
		policy.setAutoRestoreAllowed(true);
		policy.setRestoreOnAction("REVOKE");
		policy.setValidForInMinutes(null);
		Mockito.when(vidPolicyProvider.getPolicy(Mockito.anyString())).thenReturn(policy);
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		IdResponseDTO idResponse = new IdResponseDTO();
		ResponseDTO resDTO = new ResponseDTO();
		resDTO.setStatus("ACTIVATED");
		idResponse.setResponse(resDTO);
		Mockito.when(restBuilder.buildRequest(RestServicesConstants.IDREPO_IDENTITY_SERVICE, null, IdResponseDTO.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(restRequestDTO)).thenReturn(idResponse);
		Mockito.when(vidRepo.save(Mockito.any())).thenReturn(vid);
		Mockito.when(securityManager.hash(Mockito.any()))
				.thenReturn("6B764AE0FF065490AEFAF796A039D6B4F251101A5F13DA93146B9DEB11087AFC");
		VidRequestDTO request = new VidRequestDTO();
		request.setVidStatus("REVOKE");
		service.updateVid("12345678", request);
	}

	@Test
	public void testUpdateVidInvalid() {
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(null);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
		VidRequestDTO request = new VidRequestDTO();
		request.setVidStatus("ACTIVE");
		try {
			service.updateVid("12345678", request);
		} catch (IdRepoAppException e) {
			assertEquals("IDR-IDC-007 --> No Record(s) found", e.getMessage());
		}
	}

	@Test
	public void testRegenerate_Valid() throws IdRepoAppException {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime().plusDays(1);
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "ACTIVE", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
		VidPolicy policy = new VidPolicy();
		policy.setAllowedInstances(1);
		policy.setAllowedTransactions(null);
		policy.setAutoRestoreAllowed(true);
		policy.setRestoreOnAction("REVOKE");
		policy.setValidForInMinutes(null);
		Mockito.when(vidPolicyProvider.getPolicy(Mockito.anyString())).thenReturn(policy);
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		IdResponseDTO idResponse = new IdResponseDTO();
		ResponseDTO resDTO = new ResponseDTO();
		resDTO.setStatus("ACTIVATED");
		idResponse.setResponse(resDTO);
		Mockito.when(restBuilder.buildRequest(RestServicesConstants.IDREPO_IDENTITY_SERVICE, null, IdResponseDTO.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(restRequestDTO)).thenReturn(idResponse);
		Mockito.when(vidRepo.save(Mockito.any())).thenReturn(vid);
		Mockito.when(securityManager.hash(Mockito.any()))
				.thenReturn("6B764AE0FF065490AEFAF796A039D6B4F251101A5F13DA93146B9DEB11087AFC");
		service.regenerateVid("12345678");
	}

	@Test
	public void testRegenerateVid_EmptyRecordsInDb() {
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(null);
		try {
			service.regenerateVid("12345678");
		} catch (IdRepoAppException e) {
			assertEquals("IDR-IDC-007 --> No Record(s) found", e.getMessage());
		}
	}

	@Test
	public void testRegenerateVid_InValidStatus() throws IdRepoAppException {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime().plusDays(1);
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "INACTIVE", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
		VidPolicy policy = new VidPolicy();
		policy.setAllowedInstances(1);
		policy.setAllowedTransactions(null);
		policy.setAutoRestoreAllowed(true);
		policy.setRestoreOnAction("REVOKE");
		policy.setValidForInMinutes(null);
		Mockito.when(vidPolicyProvider.getPolicy(Mockito.anyString())).thenReturn(policy);
		try {
			service.regenerateVid("12345678");
		} catch (IdRepoAppException e) {
			assertEquals("IDR-VID-001 --> VID is INACTIVE", e.getMessage());
		}
	}

	@SuppressWarnings("serial")
	@Test
	public void testRegenerateVidTransactionFailed() throws RestServiceException, IdRepoDataValidationException {
		when(vidRepo.findByVid(Mockito.any())).thenThrow(new TransactionException("") {
		});
		try {
			service.regenerateVid("123");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testRegenerate_IdRepoAppUncheckedException() throws Throwable {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime().plusDays(1);
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "ACTIVE", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
		VidPolicy policy = new VidPolicy();
		policy.setAllowedInstances(1);
		policy.setAllowedTransactions(null);
		policy.setAutoRestoreAllowed(true);
		policy.setRestoreOnAction("REVOKE");
		policy.setValidForInMinutes(null);
		Mockito.when(vidPolicyProvider.getPolicy(Mockito.anyString())).thenReturn(policy);
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		IdResponseDTO idResponse = new IdResponseDTO();
		ResponseDTO resDTO = new ResponseDTO();
		resDTO.setStatus("ACTIVATED");
		idResponse.setResponse(resDTO);
		Mockito.when(restBuilder.buildRequest(RestServicesConstants.IDREPO_IDENTITY_SERVICE, null, IdResponseDTO.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(restRequestDTO)).thenReturn(idResponse);
		Mockito.when(vidRepo.save(Mockito.any())).thenReturn(vid);
		when(securityManager.hash(Mockito.any()))
				.thenThrow(new IdRepoAppUncheckedException(IdRepoErrorConstants.VID_GENERATION_FAILED));
		try {
			service.regenerateVid("123");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorMessage(), e.getErrorText());
		}
	}
}
