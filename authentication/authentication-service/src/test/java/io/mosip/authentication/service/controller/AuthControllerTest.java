package io.mosip.authentication.service.controller;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.util.TestHttpServletRequest;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.IdTypeUtil;
import org.junit.Before;
import org.junit.Ignore;
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
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.facade.AuthFacadeImpl;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.impl.IdServiceImpl;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.TestObjectWithMetadata;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.EkycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.idrepository.core.helper.RestHelper;

/**
 * This code tests the AuthController
 * 
 * @author Arun Bose
 * 
 * @author Prem Kumar
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class AuthControllerTest {

	@Mock
	private RestHelper restHelper;

	@Mock
	EnvUtil env;

	@Autowired
	Environment environment;

	@InjectMocks
	private RestRequestFactory restFactory;

	@InjectMocks
	private AuditRequestFactory auditFactory;

	@Mock
	private AuthFacadeImpl authFacade;

	@InjectMocks
	private AuthController authController;

	@Mock
	AuditHelper auditHelper;

	@Mock
	IdServiceImpl idServiceImpl;

	@Mock
	WebDataBinder binder;

	@Mock
	private AuthRequestValidator authRequestValidator;

	@Mock
	private IdTypeUtil idTypeUtil;

	@Mock
	private AuthTransactionHelper authTransactionHelper;

	@Mock
	private PartnerService partnerService;

	Errors error = new BindException(AuthRequestDTO.class, "authReqDTO");
	Errors errors = new BindException(EkycAuthRequestDTO.class, "kycAuthReqDTO");

	TestHttpServletRequest requestWithMetadata = new TestHttpServletRequest();

	@Before
	public void before() {
		ReflectionTestUtils.setField(env, "env", environment);
		//ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.invokeMethod(authController, "initAuthRequestBinder", binder);
		ReflectionTestUtils.setField(authController, "authFacade", authFacade);
		ReflectionTestUtils.setField(authFacade, "env", env);

		requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_DATA, "identity data");
		requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_INFO, "identity info");
	}

	/*
	 * 
	 * Errors in the AuthRequestValidator is handled here and exception is thrown
	 */
	@Test(expected = IdAuthenticationAppException.class)
	public void showRequestValidator()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authReqDTO = new AuthRequestDTO();
		authReqDTO.setIndividualIdType(IdType.UIN.getType());
		Errors error = new BindException(authReqDTO, "authReqDTO");
		error.rejectValue("id", "errorCode", "defaultMessage");
		TestHttpServletRequest requestWithMetadata = new TestHttpServletRequest();
		requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_DATA, "identity data");
		requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_INFO, "identity info");
		Optional<PartnerDTO> partner = Optional.empty();
		Mockito.when(partnerService.getPartner("partnerId", authReqDTO.getMetadata())).thenReturn(partner);
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(authReqDTO, !true, partner))
				.thenReturn(AuthTransactionBuilder.newInstance());
		Mockito.when(authTransactionHelper.createDataValidationException(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED));

		authController.authenticateIndividual(authReqDTO, error, "123456", "123456","1234567", requestWithMetadata);

	}

	@Test(expected = IdAuthenticationAppException.class)
	public void authenticationFailed()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authReqDTO = new AuthRequestDTO();
		authReqDTO.setIndividualIdType(IdType.UIN.getType());
		Optional<PartnerDTO> partner = Optional.empty();
		AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance();
		Mockito.when(partnerService.getPartner("partnerId", authReqDTO.getMetadata())).thenReturn(partner);
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(authReqDTO, !true, partner))
				.thenReturn(authTransactionBuilder);
		Mockito.when(authTransactionHelper.createUnableToProcessException(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(new IdAuthenticationAppException( IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		Mockito.when(authFacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UIN_DEACTIVATED));

		authController.authenticateIndividual(authReqDTO, error, "123456", "123456","1234567", requestWithMetadata);

	}

	@Test
	public void authenticationSuccess()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authReqDTO = new AuthRequestDTO();
		authReqDTO.setIndividualIdType(IdType.UIN.getType());
		Mockito.when(authFacade.authenticateIndividual(authReqDTO, true, "123456", "12345", true, new TestObjectWithMetadata())).thenReturn(new AuthResponseDTO());
		authController.authenticateIndividual(authReqDTO, error, "123456", "123456","1234567", requestWithMetadata);

	}

	@Test
	public void TestValidOtpRequest()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authRequestDTO = getRequestDto();
		authController.authenticateIndividual(authRequestDTO, error, "123456", "123456","1234567", requestWithMetadata);
	}

	@Test
	public void TestValidDemoRequest()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authRequestDTO = getRequestDto();
		authController.authenticateIndividual(authRequestDTO, error, "123456", "123456","1234567", requestWithMetadata);
	}

	@Test
	public void TestValidPinRequest()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authRequestDTO = getRequestDto();
		authController.authenticateIndividual(authRequestDTO, error, "123456", "123456","1234567", requestWithMetadata);
	}

	@Test
	public void TestValidBioFingerPrintRequest()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authRequestDTO = getRequestDto();
		RequestDTO request = new RequestDTO();
		List<BioIdentityInfoDTO> bioIdentityList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		dataDTO.setBioType("FMR");
		dataDTO.setBioSubType("LEFT_INDEX");
		dataDTO.setBioValue(value);
		bioIdentityInfoDTO.setData(dataDTO);
		bioIdentityList.add(bioIdentityInfoDTO);

		BioIdentityInfoDTO IrisDto = new BioIdentityInfoDTO();
		DataDTO irisdata = new DataDTO();
		irisdata.setBioType("Iris");
		irisdata.setBioSubType("LEFT");
		irisdata.setBioValue(value);
		IrisDto.setData(irisdata);
		bioIdentityList.add(IrisDto);

		BioIdentityInfoDTO faceDto = new BioIdentityInfoDTO();
		DataDTO facedata = new DataDTO();
		facedata.setBioType("Face");
		facedata.setBioValue(value);
		faceDto.setData(facedata);
		bioIdentityList.add(faceDto);

		request.setBiometrics(bioIdentityList);
		authRequestDTO.setRequest(request);
		authController.authenticateIndividual(authRequestDTO, error, "123456", "123456","1234567", requestWithMetadata);
	}

	private AuthRequestDTO getRequestDto() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.otp");
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")).toString());
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");
		return authRequestDTO;
	}

	/*
	 * @Test(expected = IdAuthenticationAppException.class) public void
	 * showProcessKycValidator() throws IdAuthenticationBusinessException,
	 * IdAuthenticationAppException, IdAuthenticationDaoException {
	 * KycAuthRequestDTO kycAuthReqDTO = new KycAuthRequestDTO(); Errors errors =
	 * new BindException(kycAuthReqDTO, "kycAuthReqDTO"); errors.rejectValue("id",
	 * "errorCode", "defaultMessage");
	 * authFacade.authenticateIndividual(kycAuthReqDTO, true, "123456789"); //
	 * authController.processKyc(kycAuthReqDTO, errors, "123456", "123456"); }
	 * 
	 * @Test public void processKycSuccess() throws
	 * IdAuthenticationBusinessException, IdAuthenticationAppException,
	 * IdAuthenticationDaoException {
	 * 
	 * KycAuthRequestDTO kycAuthReqDTO = new KycAuthRequestDTO();
	 * kycAuthReqDTO.setId("id"); kycAuthReqDTO.setVersion("1.1");
	 * kycAuthReqDTO.setRequestTime(ZonedDateTime.now()
	 * .format(DateTimeFormatter.ofPattern(env.getDateTimePattern())).
	 * toString()); kycAuthReqDTO.setId("id");
	 * kycAuthReqDTO.setTransactionID("1234567890"); AuthTypeDTO authTypeDTO = new
	 * AuthTypeDTO(); authTypeDTO.setDemo(false); authTypeDTO.setOtp(true);
	 * IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
	 * idInfoDTO.setLanguage("EN"); idInfoDTO.setValue("John"); IdentityInfoDTO
	 * idInfoDTO1 = new IdentityInfoDTO(); idInfoDTO1.setLanguage("fre");
	 * idInfoDTO1.setValue("Mike"); List<IdentityInfoDTO> idInfoList = new
	 * ArrayList<>(); idInfoList.add(idInfoDTO); idInfoList.add(idInfoDTO1);
	 * 
	 * IdentityDTO idDTO = new IdentityDTO(); idDTO.setName(idInfoList); RequestDTO
	 * request = new RequestDTO(); kycAuthReqDTO.setIndividualId("5134256294");
	 * request.setOtp("456789"); request.setDemographics(idDTO);
	 * kycAuthReqDTO.setRequest(request);
	 * kycAuthReqDTO.setRequestedAuth(authTypeDTO);
	 * kycAuthReqDTO.setRequest(request); kycAuthReqDTO.setSecondaryLangCode("fra");
	 * KycResponseDTO kycResponseDTO = new KycResponseDTO(); KycAuthResponseDTO
	 * kycAuthResponseDTO = new KycAuthResponseDTO();
	 * kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
	 * .format(DateTimeFormatter.ofPattern(env.getDateTimePattern())).
	 * toString()); kycAuthResponseDTO.setTransactionID("34567");
	 * kycAuthResponseDTO.setErrors(null);
	 * kycResponseDTO.setTtl(env.getProperty("ekyc.ttl.hours"));
	 * kycResponseDTO.setKycStatus(Boolean.TRUE);
	 * 
	 * kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
	 * .format(DateTimeFormatter.ofPattern(env.getDateTimePattern())).
	 * toString()); Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
	 * List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>(); list.add(new
	 * IdentityInfoDTO("en", "mosip")); idInfo.put("name", list);
	 * idInfo.put("email", list); idInfo.put("phone", list);
	 * kycResponseDTO.setIdentity(idInfo);
	 * kycAuthResponseDTO.setResponse(kycResponseDTO); AuthResponseDTO
	 * authResponseDTO = new AuthResponseDTO(); ResponseDTO res = new ResponseDTO();
	 * res.setAuthStatus(Boolean.TRUE); res.setStaticToken("234567890");
	 * authResponseDTO.setResponse(res);
	 * authResponseDTO.setResponseTime(ZonedDateTime.now()
	 * .format(DateTimeFormatter.ofPattern(env.getDateTimePattern())).
	 * toString()); authResponseDTO.setErrors(null);
	 * authResponseDTO.setTransactionID("123456789");
	 * authResponseDTO.setVersion("1.0");
	 * Mockito.when(authFacade.authenticateIndividual(Mockito.any(),
	 * Mockito.anyBoolean(), Mockito.anyString())) .thenReturn(authResponseDTO); //
	 * Mockito.when(authFacade.processKycAuth(kycAuthReqDTO, authResponseDTO,
	 * "123456789")) // .thenReturn(kycAuthResponseDTO); //
	 * authController.processKyc(kycAuthReqDTO, errors, "123456789", "12345689");
	 * assertFalse(error.hasErrors()); }
	 * 
	 * @Test(expected = IdAuthenticationAppException.class) public void
	 * processKycFailure() throws IdAuthenticationBusinessException,
	 * IdAuthenticationAppException, IdAuthenticationDaoException {
	 * KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
	 * kycAuthRequestDTO.setId("id"); kycAuthRequestDTO.setVersion("1.1");
	 * kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
	 * .format(DateTimeFormatter.ofPattern(env.getDateTimePattern())).
	 * toString()); kycAuthRequestDTO.setId("id");
	 * kycAuthRequestDTO.setTransactionID("1234567890"); AuthTypeDTO authTypeDTO =
	 * new AuthTypeDTO(); authTypeDTO.setDemo(false); authTypeDTO.setOtp(true);
	 * IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
	 * idInfoDTO.setLanguage("EN"); idInfoDTO.setValue("John"); IdentityInfoDTO
	 * idInfoDTO1 = new IdentityInfoDTO(); idInfoDTO1.setLanguage("fre");
	 * idInfoDTO1.setValue("Mike"); List<IdentityInfoDTO> idInfoList = new
	 * ArrayList<>(); idInfoList.add(idInfoDTO); idInfoList.add(idInfoDTO1);
	 * 
	 * IdentityDTO idDTO = new IdentityDTO(); idDTO.setName(idInfoList); RequestDTO
	 * request = new RequestDTO(); kycAuthRequestDTO.setIndividualId("5134256294");
	 * request.setOtp("456789"); request.setDemographics(idDTO);
	 * kycAuthRequestDTO.setRequest(request);
	 * kycAuthRequestDTO.setRequestedAuth(authTypeDTO);
	 * kycAuthRequestDTO.setRequest(request);
	 * 
	 * KycResponseDTO kycResponseDTO = new KycResponseDTO(); KycAuthResponseDTO
	 * kycAuthResponseDTO = new KycAuthResponseDTO();
	 * kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
	 * .format(DateTimeFormatter.ofPattern(env.getDateTimePattern())).
	 * toString()); kycAuthResponseDTO.setTransactionID("34567");
	 * kycAuthResponseDTO.setErrors(null);
	 * kycResponseDTO.setTtl(env.getProperty("ekyc.ttl.hours"));
	 * kycResponseDTO.setStaticToken("2345678");
	 * kycResponseDTO.setKycStatus(Boolean.TRUE);
	 * 
	 * kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
	 * .format(DateTimeFormatter.ofPattern(env.getDateTimePattern())).
	 * toString()); Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
	 * List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>(); list.add(new
	 * IdentityInfoDTO("en", "mosip")); idInfo.put("name", list);
	 * idInfo.put("email", list); idInfo.put("phone", list);
	 * kycResponseDTO.setIdentity(idInfo);
	 * kycAuthResponseDTO.setResponse(kycResponseDTO); AuthResponseDTO
	 * authResponseDTO = new AuthResponseDTO(); ResponseDTO res = new ResponseDTO();
	 * res.setAuthStatus(Boolean.TRUE); res.setStaticToken("234567890");
	 * authResponseDTO.setResponse(res);
	 * authResponseDTO.setResponseTime(ZonedDateTime.now()
	 * .format(DateTimeFormatter.ofPattern(env.getDateTimePattern())).
	 * toString()); authResponseDTO.setErrors(null);
	 * authResponseDTO.setTransactionID("123456789");
	 * authResponseDTO.setVersion("1.0");
	 * Mockito.when(authFacade.authenticateIndividual(Mockito.any(),
	 * Mockito.anyBoolean(), Mockito.anyString())) .thenReturn(authResponseDTO); //
	 * Mockito.when(authFacade.processKycAuth(kycAuthRequestDTO, authResponseDTO,
	 * "12346789")) // .thenThrow(new
	 * IdAuthenticationBusinessException(IdAuthenticationErrorConstants.
	 * UNABLE_TO_PROCESS)); // authController.processKyc(kycAuthRequestDTO, errors,
	 * "12346789", "1234567"); }
	 */
}
