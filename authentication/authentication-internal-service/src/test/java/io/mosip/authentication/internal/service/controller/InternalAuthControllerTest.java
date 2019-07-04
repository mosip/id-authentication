package io.mosip.authentication.internal.service.controller;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.facade.AuthFacadeImpl;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.IdServiceImpl;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.internal.service.validator.InternalAuthRequestValidator;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class InternalAuthControllerTest {

	@InjectMocks
	InternalAuthController authController;

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

	@Autowired
	Environment env;

	@InjectMocks
	private RestRequestFactory restFactory;

	@InjectMocks
	private AuditRequestFactory auditFactory;

	@InjectMocks
	private InternalAuthRequestValidator internalAuthRequestValidator;

	Errors error = new BindException(AuthRequestDTO.class, "authReqDTO");

	@Before
	public void before() {
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.invokeMethod(authController, "initBinder", binder);
		ReflectionTestUtils.setField(authController, "authFacade", authfacade);
		ReflectionTestUtils.setField(authfacade, "env", env);
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void showAuthenticateTspValidator()
			throws IdAuthenticationAppException, IdAuthenticationDaoException, IdAuthenticationBusinessException {
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		AuthRequestDTO authReqestsDTO = new AuthRequestDTO();
		AuthTypeDTO request = new AuthTypeDTO();
		authReqestsDTO.setRequestedAuth(request);
		authReqestsDTO.setIndividualIdType(IdType.UIN.getType());
		Errors error = new BindException(authReqestsDTO, "authReqDTO");
		error.rejectValue("id", "errorCode", "defaultMessage");
		List<AuthError> errors = new ArrayList<>();
		authResponseDTO.setErrors(errors);
		ResponseDTO response = new ResponseDTO();
		response.setAuthStatus(true);
		authResponseDTO.setResponse(response);
		authResponseDTO.setErrors(new ArrayList<>());
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(authResponseDTO);
		authController.authenticate(authReqestsDTO, error);
	}

	@Test
	public void auhtenticationTspSuccess()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IdAuthenticationAppException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		authReqestDTO.setIndividualIdType(IdType.UIN.getType());
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		authReqestDTO.setRequestedAuth(requestedAuth);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		List<AuthError> errors = new ArrayList<>();
		authResponseDTO.setErrors(errors);
		ResponseDTO response = new ResponseDTO();
		response.setAuthStatus(true);
		authResponseDTO.setResponse(response);
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(authResponseDTO);
		Errors error = new BindException(authReqestDTO, "authReqDTO");

		authController.authenticate(authReqestDTO, error);
	}

	@Test
	public void auhtenticationTspvalid()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IdAuthenticationAppException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authReqestDTO.setIndividualIdType(IdType.UIN.getType());
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		authReqestDTO.setRequestedAuth(requestedAuth);
		List<AuthError> errors = new ArrayList<>();
		authResponseDTO.setErrors(errors);
		ResponseDTO response = new ResponseDTO();
		response.setAuthStatus(true);
		authResponseDTO.setResponse(response);
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(authResponseDTO);
//		Mockito.when(authfacade.authenticateIndividual(authReqestDTO, false, InternalAuthController.DEFAULT_PARTNER_ID))
//				.thenReturn(new AuthResponseDTO());
		authController.authenticate(authReqestDTO, error);
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void TestAuthIdException()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IdAuthenticationAppException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		authReqestDTO.setIndividualIdType(IdType.UIN.getType());
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		authReqestDTO.setRequestedAuth(requestedAuth);
		Mockito.when(authfacade.authenticateIndividual(authReqestDTO, false, InternalAuthController.DEFAULT_PARTNER_ID))
				.thenThrow(new IDDataValidationException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED));
		authController.authenticate(authReqestDTO, error);
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void TestAuthIdException2()
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		authReqestDTO.setIndividualIdType(IdType.UIN.getType());
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		authReqestDTO.setRequestedAuth(requestedAuth);
		Mockito.when(authfacade.authenticateIndividual(authReqestDTO, false, InternalAuthController.DEFAULT_PARTNER_ID))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		authController.authenticate(authReqestDTO, error);
	}

	@Test
	public void TestValidOtpRequest()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authRequestDTO = getRequestDto();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setOtp(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		List<AuthError> errors = new ArrayList<>();
		authResponseDTO.setErrors(errors);
		ResponseDTO response = new ResponseDTO();
		response.setAuthStatus(true);
		authResponseDTO.setResponse(response);
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(authResponseDTO);
		authController.authenticate(authRequestDTO, error);
	}

	@Test
	public void TestValidDemoRequest()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authRequestDTO = getRequestDto();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setDemo(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO response = new ResponseDTO();
		response.setAuthStatus(true);
		authResponseDTO.setResponse(response);
		authResponseDTO.setErrors(new ArrayList<>());
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(authResponseDTO);
		authController.authenticate(authRequestDTO, error);
	}

	@Test
	public void TestValidPinRequest()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authRequestDTO = getRequestDto();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setPin(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		authRequestDTO.setIndividualId("5134256294");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO response = new ResponseDTO();
		response.setAuthStatus(true);
		authResponseDTO.setResponse(response);
		authResponseDTO.setErrors(new ArrayList<>());
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(authResponseDTO);
		authController.authenticate(authRequestDTO, error);
	}

	@Test
	public void TestValidBioFingerPrintRequest()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authRequestDTO = getRequestDto();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setBio(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		RequestDTO request = new RequestDTO();
		List<BioIdentityInfoDTO> bioIdentityList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		dataDTO.setBioType("FMR");
		dataDTO.setBioSubType("LEFT_INDEX");
		dataDTO.setDeviceProviderID("provider001");
		dataDTO.setBioValue(value);
		bioIdentityInfoDTO.setData(dataDTO);
		bioIdentityList.add(bioIdentityInfoDTO);

		BioIdentityInfoDTO IrisDto = new BioIdentityInfoDTO();
		DataDTO irisdata = new DataDTO();
		irisdata.setBioType("IIR");
		irisdata.setBioSubType("LEFT");
		irisdata.setDeviceProviderID("provider001");
		irisdata.setBioValue(value);
		IrisDto.setData(irisdata);
		bioIdentityList.add(IrisDto);

		BioIdentityInfoDTO faceDto = new BioIdentityInfoDTO();
		DataDTO facedata = new DataDTO();
		facedata.setBioType("FID");
		facedata.setBioSubType("FACE");
		facedata.setDeviceProviderID("provider001");
		facedata.setBioValue(value);
		faceDto.setData(facedata);
		bioIdentityList.add(faceDto);

		request.setBiometrics(bioIdentityList);
		authRequestDTO.setRequest(request);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO response = new ResponseDTO();
		response.setAuthStatus(true);
		authResponseDTO.setResponse(response);
		Mockito.when(authfacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.any()))
				.thenReturn(authResponseDTO);
		List<AuthError> errors = new ArrayList<>();
		authResponseDTO.setErrors(errors);
		authController.authenticate(authRequestDTO, error);
	}

	private AuthRequestDTO getRequestDto() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.otp");
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");
		return authRequestDTO;
	}

}
