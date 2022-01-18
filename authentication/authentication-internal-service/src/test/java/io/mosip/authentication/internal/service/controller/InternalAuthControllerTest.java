package io.mosip.authentication.internal.service.controller;

import java.sql.Ref;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.util.IdTypeUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.facade.AuthFacadeImpl;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.IdServiceImpl;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.TestHttpServletRequest;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.internal.service.validator.InternalAuthRequestValidator;
import io.mosip.idrepository.core.helper.RestHelper;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@Import(EnvUtil.class)
public class InternalAuthControllerTest {

	@Mock
	WebDataBinder binder;

	@Mock
	AuditHelper auditHelper;

	@Mock
	IdInfoFetcherImpl idInfoFetcher;

	@Mock
	IdServiceImpl idservice;

	@Mock
	AuthFacadeImpl authfacade;

	@Mock
	private KycService kycService;

	@Mock
	private RestHelper restHelper;

	@Mock
	private IdTypeUtil idTypeUtil;
	
	@Autowired
	EnvUtil env;	
	
	@InjectMocks
	private RestRequestFactory restFactory;

	@InjectMocks
	private AuditRequestFactory auditFactory;

	@Mock
	private InternalAuthRequestValidator internalAuthRequestValidator;
	
	@Mock
	private AuthTransactionHelper authTransactionHelper;
	
	@Mock
	private IdAuthSecurityManager securityManager;
	
	@InjectMocks
	InternalAuthController authController;

	Errors error = new BindException(AuthRequestDTO.class, "authReqDTO");

	@Before
	public void before() {
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.invokeMethod(authController, "initBinder", binder);
		ReflectionTestUtils.setField(authController, "idTypeUtil", idTypeUtil);
		ReflectionTestUtils.setField(authController, "authFacade", authfacade);
		ReflectionTestUtils.setField(authfacade, "env", env);
		
		Mockito.when(securityManager.getUser()).thenReturn("user");

	}

	@Test(expected = IdAuthenticationAppException.class)
	public void showAuthenticateTspValidator()
			throws IdAuthenticationAppException, IdAuthenticationDaoException, IdAuthenticationBusinessException {
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		AuthRequestDTO authReqestsDTO = new AuthRequestDTO();
		authReqestsDTO.setIndividualIdType(IdType.UIN.getType());
		Errors error = new BindException(authReqestsDTO, "authReqDTO");
		error.rejectValue("id", "errorCode", "defaultMessage");
		List<AuthError> errors = new ArrayList<>();
		authResponseDTO.setErrors(errors);
		ResponseDTO response = new ResponseDTO();
		response.setAuthStatus(true);
		authResponseDTO.setResponse(response);
		authResponseDTO.setErrors(new ArrayList<>());
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(authResponseDTO);
		AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance();
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(Mockito.any(), Mockito.anyBoolean(), Mockito.any())).thenReturn(authTransactionBuilder);
		Mockito.when(authTransactionHelper.createDataValidationException(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		authController.authenticate(authReqestsDTO, error, new TestHttpServletRequest());
	}

	@Test
	public void auhtenticationTspSuccess()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IdAuthenticationAppException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();

	 	authReqestDTO.setIndividualIdType(IdType.UIN.getType());
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		List<AuthError> errors = new ArrayList<>();
		authResponseDTO.setErrors(errors);
		ResponseDTO response = new ResponseDTO();
		response.setAuthStatus(true);
		authResponseDTO.setResponse(response);
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(authResponseDTO);
		Errors error = new BindException(authReqestDTO, "authReqDTO");
		TestHttpServletRequest request = new TestHttpServletRequest();
		authController.authenticate(authReqestDTO, error, request);
	}

	@Test
	public void auhtenticationTspvalid()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IdAuthenticationAppException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		List<AuthError> errors = new ArrayList<>();
		authResponseDTO.setErrors(errors);
		ResponseDTO response = new ResponseDTO();
		response.setAuthStatus(true);
		authResponseDTO.setResponse(response);
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(authResponseDTO);
//		Mockito.when(authfacade.authenticateIndividual(authReqestDTO, false, InternalAuthController.DEFAULT_PARTNER_ID))
//				.thenReturn(new AuthResponseDTO());
		TestHttpServletRequest request = new TestHttpServletRequest();
		authReqestDTO.setIndividualIdType(null);
		authReqestDTO.setIndividualId("1122");
		Mockito.when(idTypeUtil.getIdType("1122")).thenReturn(IdType.UIN);
		authController.authenticate(authReqestDTO, error, request);

		authReqestDTO.setIndividualIdType(IdType.UIN.getType());
		authController.authenticate(authReqestDTO, error, request);
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void TestAuthIdException1()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IdAuthenticationAppException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		authReqestDTO.setIndividualIdType(IdType.UIN.getType());
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenThrow(new IDDataValidationException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED));
		AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance();
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(Mockito.any(), Mockito.anyBoolean(), Mockito.any())).thenReturn(authTransactionBuilder);
		Mockito.when(authTransactionHelper.createDataValidationException(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		authController.authenticate(authReqestDTO, error, new TestHttpServletRequest());
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void TestAuthIdException2()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IdAuthenticationAppException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		authReqestDTO.setIndividualIdType(IdType.UIN.getType());
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenThrow(new IDDataValidationException());
		AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance();
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(Mockito.any(), Mockito.anyBoolean(), Mockito.any())).thenReturn(authTransactionBuilder);
		Mockito.when(authTransactionHelper.createDataValidationException(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		authController.authenticate(authReqestDTO, error, new TestHttpServletRequest());
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void TestAuthIdException3()
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		authReqestDTO.setIndividualIdType(IdType.UIN.getType());
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		Mockito.when(authTransactionHelper.createUnableToProcessException(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		authController.authenticate(authReqestDTO, error, new TestHttpServletRequest());
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void TestAuthIdException4()
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		authReqestDTO.setIndividualIdType(IdType.UIN.getType());
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenThrow(new IdAuthenticationBusinessException());
		Mockito.when(authTransactionHelper.createUnableToProcessException(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		authController.authenticate(authReqestDTO, error, new TestHttpServletRequest());
	}

	@Test
	public void TestValidOtpRequest()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authRequestDTO = getRequestDto();
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		List<AuthError> errors = new ArrayList<>();
		authResponseDTO.setErrors(errors);
		ResponseDTO response = new ResponseDTO();
		response.setAuthStatus(true);
		authResponseDTO.setResponse(response);
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(authResponseDTO);
		AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance();
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(Mockito.any(), Mockito.anyBoolean(), Mockito.any())).thenReturn(authTransactionBuilder);
		authController.authenticate(authRequestDTO, error, new TestHttpServletRequest());
	}

	@Test
	public void TestValidDemoRequest()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authRequestDTO = getRequestDto();
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO response = new ResponseDTO();
		response.setAuthStatus(true);
		authResponseDTO.setResponse(response);
		authResponseDTO.setErrors(new ArrayList<>());
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(authResponseDTO);
		authController.authenticate(authRequestDTO, error, new TestHttpServletRequest());
	}

	@Test
	public void TestValidPinRequest()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authRequestDTO = getRequestDto();
		authRequestDTO.setIndividualId("5134256294");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO response = new ResponseDTO();
		response.setAuthStatus(true);
		authResponseDTO.setResponse(response);
		authResponseDTO.setErrors(new ArrayList<>());
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(authResponseDTO);
		authController.authenticate(authRequestDTO, error, new TestHttpServletRequest());
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
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO response = new ResponseDTO();
		response.setAuthStatus(true);
		authResponseDTO.setResponse(response);
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(authResponseDTO);
		List<AuthError> errors = new ArrayList<>();
		authResponseDTO.setErrors(errors);
		authController.authenticate(authRequestDTO, error, new TestHttpServletRequest());
	}

	@Test
	public void authenticateInternalTest() throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		Errors error = new BindException(authReqestDTO, "authReqDTO");
		 TestHttpServletRequest request= new TestHttpServletRequest();
		authReqestDTO.setIndividualIdType(null);
		authReqestDTO.setIndividualId("1122");
		ReflectionTestUtils.setField(authController, "idTypeUtil", idTypeUtil);
		Mockito.when(idTypeUtil.getIdType("1122")).thenReturn(IdType.UIN);
		authController.authenticateInternal(authReqestDTO, error, request);

		authReqestDTO.setIndividualIdType(IdType.UIN.getType());
		Assert.assertEquals(null, authController.authenticateInternal(authReqestDTO, error, request));
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void authenticateInternalExceptionTest1()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IdAuthenticationAppException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		authReqestDTO.setIndividualIdType(IdType.UIN.getType());
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenThrow(new IDDataValidationException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED));
		AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance();
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(Mockito.any(), Mockito.anyBoolean(), Mockito.any())).thenReturn(authTransactionBuilder);
		Mockito.when(authTransactionHelper.createDataValidationException(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		authController.authenticateInternal(authReqestDTO, error, new TestHttpServletRequest());
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void authenticateInternalExceptionTest2()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IdAuthenticationAppException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		authReqestDTO.setIndividualIdType(IdType.UIN.getType());
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenThrow(new IDDataValidationException());
		AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance();
		Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(Mockito.any(), Mockito.anyBoolean(), Mockito.any())).thenReturn(authTransactionBuilder);
		Mockito.when(authTransactionHelper.createDataValidationException(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		authController.authenticateInternal(authReqestDTO, error, new TestHttpServletRequest());
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void authenticateInternalExceptionTest3()
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		authReqestDTO.setIndividualIdType(IdType.UIN.getType());
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		Mockito.when(authTransactionHelper.createUnableToProcessException(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		authController.authenticateInternal(authReqestDTO, error, new TestHttpServletRequest());
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void authenticateInternalExceptionTest4()
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		authReqestDTO.setIndividualIdType(IdType.UIN.getType());
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
				.thenThrow(new IdAuthenticationBusinessException());
		Mockito.when(authTransactionHelper.createUnableToProcessException(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		authController.authenticateInternal(authReqestDTO, error, new TestHttpServletRequest());
	}

	private AuthRequestDTO getRequestDto() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.otp");
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");
		return authRequestDTO;
	}

}
