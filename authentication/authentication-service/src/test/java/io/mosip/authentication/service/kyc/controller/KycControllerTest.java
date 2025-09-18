/**
 * 
 */
package io.mosip.authentication.service.kyc.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.mosip.authentication.common.service.kafka.impl.AuthenticationErrorEventingPublisher;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.indauth.dto.EKycAuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.EKycResponseDTO;
import io.mosip.authentication.core.indauth.dto.EkycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.KycExchangeRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycExchangeResponseDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.service.kyc.validator.KycExchangeRequestValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.patrner.PartnerServiceImpl;
import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.TestHttpServletRequest;
import io.mosip.authentication.common.service.util.TestObjectWithMetadata;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.service.kyc.facade.KycFacadeImpl;
import io.mosip.authentication.service.kyc.impl.KycServiceImpl;
import io.mosip.authentication.service.kyc.validator.KycAuthRequestValidator;
import io.mosip.authentication.common.service.helper.RestHelper;

/**
 * @author Dinesh Karuppiah.T
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@Import(EnvUtil.class)
public class KycControllerTest {

	@Mock
	AuthTransactionHelper authTransactionHelper;

	@Mock
	private AuthRequestValidator authRequestValidator;

	@Mock
	private KycExchangeRequestValidator kycExchangeValidator;

	@Mock
	PartnerServiceImpl partnerService;

	@Mock
	private RestHelper restHelper;

	@Autowired
	EnvUtil env;

	@InjectMocks
	private RestRequestFactory restFactory;

	@Mock
	AuditHelper auditHelper;

	@InjectMocks
	private AuditRequestFactory auditFactory;

	@Mock
	private IdInfoFetcherImpl idInfoFetcherImpl;

	@Mock
	private KycFacadeImpl kycFacade;

	@Mock
	private IdTypeUtil idTypeUtil;

	@InjectMocks
	private KycAuthController kycAuthController;

	@Mock
	WebDataBinder binder;

	Errors error = new BindException(AuthRequestDTO.class, "authReqDTO");
	Errors errors = new BindException(EkycAuthRequestDTO.class, "kycAuthReqDTO");

	@Mock
	private KycServiceImpl kycService;

	@Mock 
	PartnerServiceManager partnerServiceManager;
	
	@Mock
	private KycAuthRequestValidator kycReqValidator;

	@Autowired
	private ObjectMapper mapper;

	EkycAuthRequestDTO kycAuthReqDTO = null;
	IdentityInfoDTO idInfoDTO = null;
	IdentityInfoDTO idInfoDTO1 = null;
	List<IdentityInfoDTO> idInfoList = null;
	IdentityDTO idDTO = null;
	RequestDTO requestDTO = null;
	List<BioIdentityInfoDTO> bioDataList = null;
	BioIdentityInfoDTO bioIdInfoDto1 = null;
	DataDTO dataDto1 = null;
	DigitalId digitalId1 = null;
	EKycAuthResponseDTO kycAuthResponseDTO = null;
	AuthResponseDTO authResponseDTO = null;
	ResponseDTO res = null;
	EKycResponseDTO kycResponseDTO = null;
	HashMap<String, Object> respMetadata = null;
	HashMap<String, Object> metadata = null;
	Map<String, List<IdentityInfoDTO>> idInfo = null;
	List<IdentityInfoDTO> list = null;
	
	@Before
	public void before() throws Exception {
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.setField(partnerService, "mapper", mapper);
		ReflectionTestUtils.setField(partnerService, "partnerServiceManager", partnerServiceManager);
		ReflectionTestUtils.invokeMethod(kycAuthController, "initEKycBinder", binder);
		ReflectionTestUtils.setField(kycAuthController, "kycFacade", kycFacade);
		ReflectionTestUtils.setField(kycAuthController, "authTransactionHelper", authTransactionHelper);
		ReflectionTestUtils.setField(kycAuthController, "kycReqValidator", kycReqValidator);
		ReflectionTestUtils.setField(kycReqValidator, "idInfoFetcher", idInfoFetcherImpl);
		when(idTypeUtil.getIdType(Mockito.any())).thenReturn(IdType.UIN);

		kycAuthReqDTO = new EkycAuthRequestDTO();
		kycAuthReqDTO.setId("id");
		kycAuthReqDTO.setVersion("1.1");
		kycAuthReqDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthReqDTO.setTransactionID("1234567890");
		kycAuthReqDTO.setIndividualId("5134256294");
		
		idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO1.setValue("Mike");
		idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);

		requestDTO = new RequestDTO();
		bioDataList = new ArrayList<BioIdentityInfoDTO>();
		bioIdInfoDto1 = new BioIdentityInfoDTO();
		dataDto1 = new DataDTO();
		dataDto1.setBioSubType("LEFT");
		dataDto1.setBioType("Iris");
		digitalId1 = new DigitalId();
		digitalId1.setSerialNo("9149795");
		digitalId1.setMake("eyecool");
		dataDto1.setDigitalId(digitalId1);
		dataDto1.setDomainUri("dev.mosip.net");
		dataDto1.setPurpose("Registration");
		dataDto1.setQualityScore(70f);
		dataDto1.setRequestedScore(90f);
		bioIdInfoDto1.setData(dataDto1);
		bioIdInfoDto1.setHash("12341");
		bioIdInfoDto1.setSessionKey("Testsessionkey1");
		bioIdInfoDto1.setSpecVersion("Spec1.1.0");
		bioIdInfoDto1.setThumbprint("testvalue1");
		bioDataList.add(bioIdInfoDto1);
		requestDTO.setBiometrics(bioDataList);
		requestDTO.setOtp("456789");
		requestDTO.setDemographics(idDTO);
		kycAuthReqDTO.setRequest(requestDTO);
	
		metadata = new HashMap<String, Object>();
		metadata.put("IDENTITY_DATA", new HashMap<String, Object>());
		metadata.put("IDENTITY_INFO", new HashMap<String, Object>());
		kycAuthReqDTO.setMetadata(metadata);

		kycResponseDTO = new EKycResponseDTO();
		kycResponseDTO.setKycStatus(Boolean.TRUE);
		idInfo = new HashMap<>();
		list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		kycResponseDTO.setIdentity(mapper.writeValueAsString(idInfo));
		
		kycAuthResponseDTO = new EKycAuthResponseDTO();
		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthResponseDTO.setTransactionID("34567");
		kycAuthResponseDTO.setResponse(kycResponseDTO);
		
		authResponseDTO = new AuthResponseDTO();
		res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setVersion("1.0");
		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authResponseDTO.setErrors(null);
		authResponseDTO.setTransactionID("123456789");
		HashMap<String, Object> respMetadata = new HashMap<String, Object>();
		respMetadata.put("IDENTITY_DATA", new HashMap<String, Object>());
		respMetadata.put("IDENTITY_INFO", new HashMap<String, Object>());

	}

	@Test
	public void processKycSuccess() throws IdAuthenticationBusinessException, IdAuthenticationAppException,
			IdAuthenticationDaoException, Exception {
		kycAuthReqDTO.setIndividualIdType(IdType.UIN.getType());
		
		AuthTransactionBuilder authTxnBuilder = AuthTransactionBuilder.newInstance();
		Optional<PartnerDTO> partner = Optional.empty();

		Mockito.when(partnerService.getPartner("partnerId", kycAuthReqDTO.getMetadata())).thenReturn(partner);
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(kycAuthReqDTO, !false, partner))
				.thenReturn(authTxnBuilder);

		TestHttpServletRequest requestWithMetadata = new TestHttpServletRequest();
		requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_DATA, "identity data");;
		requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_INFO, "identity info");
		Mockito.when(kycFacade.authenticateIndividual(kycAuthReqDTO, true, "1635497344579", "1635497344579", requestWithMetadata))
				.thenReturn(authResponseDTO);
		Mockito.when(kycFacade.processEKycAuth(kycAuthReqDTO, authResponseDTO, "1635497344579", requestWithMetadata.getMetadata()))
				.thenReturn(kycAuthResponseDTO);
		assertEquals(kycAuthResponseDTO,
				kycAuthController.processKyc(kycAuthReqDTO, errors, "1635497344579", "1635497344579", "1635497344579", requestWithMetadata));
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void processKycFailure1() throws IdAuthenticationBusinessException, IdAuthenticationAppException,
			IdAuthenticationDaoException, Exception {

		AuthTransactionBuilder authTxnBuilder = AuthTransactionBuilder.newInstance();
		Errors errors = new BindException(kycAuthReqDTO, "kycAuthReqDTO");
		errors.rejectValue("id", "errorCode", "defaultMessage");
		
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(kycAuthReqDTO, false, Optional.empty()))
				.thenReturn(authTxnBuilder);
		Mockito.when(authTransactionHelper.createDataValidationException(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new IdAuthenticationAppException());
		Mockito.when(kycFacade.authenticateIndividual(kycAuthReqDTO, true, "1635497344579", "1635497344579", new TestObjectWithMetadata())).thenReturn(authResponseDTO);
		Mockito.when(kycFacade.processEKycAuth(kycAuthReqDTO, authResponseDTO, "1635497344579", Collections.emptyMap())).thenReturn(kycAuthResponseDTO);
		kycAuthController.processKyc(kycAuthReqDTO, errors, "1635497344579", "1635497344579", "1635497344579", new TestHttpServletRequest());
	}
	
	@Test
	public void processKycFailure2() throws IdAuthenticationBusinessException, IdAuthenticationAppException,
			IdAuthenticationDaoException, Exception {

		Mockito.when(authTransactionHelper.createUnableToProcessException(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new IdAuthenticationAppException());
		
		TestHttpServletRequest requestWithMetadata = new TestHttpServletRequest();
		requestWithMetadata.setMetadata(new HashMap<>());
		Mockito.when(kycFacade.authenticateIndividual(kycAuthReqDTO, true, "1635497344579", "1635497344579", requestWithMetadata)).thenThrow(new IdAuthenticationBusinessException());
		Mockito.when(kycFacade.processEKycAuth(kycAuthReqDTO, authResponseDTO, "1635497344579", requestWithMetadata.getMetadata())).thenReturn(kycAuthResponseDTO);
		kycAuthController.processKyc(kycAuthReqDTO, errors, "1635497344579", "1635497344579", "1635497344579", new TestHttpServletRequest());
	}
	@Test
	public void testInit_whenEventingEnabledAndNullPublisher_shouldThrow() {
		ReflectionTestUtils.setField(kycAuthController, "isEventingEnabled", true);
		ReflectionTestUtils.setField(kycAuthController, "authenticationErrorEventingPublisher", null);
		assertThrows(BeanCreationException.class, () -> kycAuthController.init());
	}

	@Test
	public void testInit_whenEventingDisabled_shouldNotThrow() {
		ReflectionTestUtils.setField(kycAuthController, "isEventingEnabled", false);
		kycAuthController.init(); // should not throw
	}

	@Test
	public void testProcessKycAuth_success() throws Exception {
		KycAuthRequestDTO req = new KycAuthRequestDTO();
		req.setIndividualId("1234");
		req.setMetadata(new HashMap<>());

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.putMetadata(IdAuthCommonConstants.IDENTITY_DATA, "data");
		request.putMetadata(IdAuthCommonConstants.IDENTITY_INFO, "info");

		Mockito.when(partnerService.getPartner(Mockito.anyString(), Mockito.any()))
				.thenReturn(Optional.empty());
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(Mockito.any(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(AuthTransactionBuilder.newInstance());
		Mockito.when(kycFacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(authResponseDTO);
		Mockito.when(kycFacade.processKycAuth(Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyMap()))
				.thenReturn(new KycAuthResponseDTO());

		KycAuthResponseDTO resp = kycAuthController.processKycAuth(
				req, errors, "misp", "partner", "oidc", request);
		assertNotNull(resp);
	}

	@Test
	public void testProcessKycAuth_withIDDataValidationException() throws Exception {
		KycAuthRequestDTO req = new KycAuthRequestDTO();
		req.setIndividualId("1234");
		req.setMetadata(new HashMap<>());

		TestHttpServletRequest request = new TestHttpServletRequest();

		Mockito.when(partnerService.getPartner(Mockito.anyString(), Mockito.any()))
				.thenReturn(Optional.empty());
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(Mockito.any(),
						Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(AuthTransactionBuilder.newInstance());

		IDDataValidationException ex = new IDDataValidationException("code", "error");

		Mockito.doAnswer(invocation -> {
			throw ex;
		}).when(authRequestValidator).validateIdvId(Mockito.anyString(), Mockito.anyString(), Mockito.any());

		Mockito.when(authTransactionHelper.createDataValidationException(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(new IdAuthenticationAppException());

		assertThrows(IdAuthenticationAppException.class,
				() -> kycAuthController.processKycAuth(req, errors, "misp", "partner", "oidc", request));
	}

	@Test
	public void testProcessKycAuth_withIdAuthBusinessException_eventingOn() throws Exception {
		ReflectionTestUtils.setField(kycAuthController, "isEventingEnabled", true);
		ReflectionTestUtils.setField(kycAuthController, "authenticationErrorEventingPublisher",
				Mockito.mock(AuthenticationErrorEventingPublisher.class));

		KycAuthRequestDTO req = new KycAuthRequestDTO();
		req.setIndividualId("1234");
		req.setMetadata(new HashMap<>());

		TestHttpServletRequest request = new TestHttpServletRequest();

		Mockito.when(partnerService.getPartner(Mockito.anyString(), Mockito.any()))
				.thenReturn(Optional.empty());
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(Mockito.any(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(AuthTransactionBuilder.newInstance());

		IdAuthenticationBusinessException ex = new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
				"test error");
		Mockito.doThrow(ex).when(kycFacade).authenticateIndividual(Mockito.any(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyBoolean());
		Mockito.when(authTransactionHelper.createUnableToProcessException(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(new IdAuthenticationAppException());

		assertThrows(IdAuthenticationAppException.class,
				() -> kycAuthController.processKycAuth(req, errors, "misp", "partner", "oidc", request));
	}

	@Test
	public void testProcessKycAuth_notObjectWithMetadata() {
		HttpServletRequest badRequest = Mockito.mock(HttpServletRequest.class);
		assertThrows(IdAuthenticationBusinessException.class,
				() -> kycAuthController.processKycAuth(new KycAuthRequestDTO(), errors, "m", "p", "o", badRequest));
	}

	@Test
	public void testProcessKycExchange_success() throws Exception {
		KycExchangeRequestDTO req = new KycExchangeRequestDTO();
		req.setIndividualId("123");
		req.setMetadata(new HashMap<>());
		req.setTransactionID("tx");

		TestHttpServletRequest request = new TestHttpServletRequest();

		Mockito.when(partnerService.getPartner(Mockito.anyString(), Mockito.any()))
				.thenReturn(Optional.empty());
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(Mockito.any(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(AuthTransactionBuilder.newInstance());
		Mockito.when(kycFacade.processKycExchange(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyMap(), Mockito.any()))
				.thenReturn(new KycExchangeResponseDTO());

		KycExchangeResponseDTO resp = kycAuthController.processKycExchange(req, errors, "m", "p", "o", request);
		assertNotNull(resp);
	}

	@Test
	public void testProcessKycExchange_withValidationExceptionAndNullTransactionId() throws Exception {
		KycExchangeRequestDTO req = new KycExchangeRequestDTO();
		req.setIndividualId("123");

		TestHttpServletRequest request = new TestHttpServletRequest();

		Mockito.when(partnerService.getPartner(Mockito.anyString(), Mockito.any()))
				.thenReturn(Optional.empty());
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(Mockito.any(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(AuthTransactionBuilder.newInstance());

		IDDataValidationException ex = new IDDataValidationException("code", "msg");

		// Use doAnswer instead of doThrow
		Mockito.doAnswer(invocation -> { throw ex; })
				.when(kycExchangeValidator)
				.validateIdvId(Mockito.anyString(), Mockito.anyString(), Mockito.any());

		Mockito.when(authTransactionHelper.createDataValidationException(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(new IdAuthenticationAppException());

		assertThrows(IdAuthenticationAppException.class,
				() -> kycAuthController.processKycExchange(req, errors, "m", "p", "o", request));
	}

	@Test
	public void testProcessKycExchange_withBusinessException() throws Exception {
		KycExchangeRequestDTO req = new KycExchangeRequestDTO();
		req.setIndividualId("123");
		req.setMetadata(new HashMap<>());

		TestHttpServletRequest request = new TestHttpServletRequest();

		Mockito.when(partnerService.getPartner(Mockito.anyString(), Mockito.any()))
				.thenReturn(Optional.empty());
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(Mockito.any(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(AuthTransactionBuilder.newInstance());
		Mockito.doThrow(new IdAuthenticationBusinessException("code", "msg")).when(kycFacade)
				.processKycExchange(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyMap(), Mockito.any());

		assertThrows(IdAuthenticationAppException.class,
				() -> kycAuthController.processKycExchange(req, errors, "m", "p", "o", request));
	}

	@Test
	public void testProcessKycExchange_notObjectWithMetadata() {
		HttpServletRequest badRequest = Mockito.mock(HttpServletRequest.class);
		assertThrows(IdAuthenticationBusinessException.class,
				() -> kycAuthController.processKycExchange(new KycExchangeRequestDTO(), errors, "m", "p", "o", badRequest));
	}

}
