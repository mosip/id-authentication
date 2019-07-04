/**
 * 
 */
package io.mosip.authentication.kyc.service.controller;

import static org.junit.Assert.assertFalse;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.KycResponseDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.kyc.service.facade.KycFacadeImpl;
import io.mosip.authentication.kyc.service.impl.KycServiceImpl;
import io.mosip.authentication.kyc.service.validator.KycAuthRequestValidator;

/**
 * @author Dinesh Karuppiah.T
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class KycControllerTest {

	@Mock
	private RestHelper restHelper;

	@Autowired
	Environment env;

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

	@InjectMocks
	private KycAuthController kycAuthController;

	@Mock
	WebDataBinder binder;

	@InjectMocks
	private KycAuthRequestValidator KycAuthRequestValidator;

	Errors error = new BindException(AuthRequestDTO.class, "authReqDTO");
	Errors errors = new BindException(KycAuthRequestDTO.class, "kycAuthReqDTO");

	/** The Kyc Service */
	@Mock
	private KycServiceImpl kycService;

	@Before
	public void before() {
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.invokeMethod(kycAuthController, "initKycBinder", binder);
		ReflectionTestUtils.setField(kycAuthController, "kycFacade", kycFacade);
		ReflectionTestUtils.setField(KycAuthRequestValidator, "env", env);
		ReflectionTestUtils.setField(KycAuthRequestValidator, "idInfoFetcher", idInfoFetcherImpl);

	}

	@Test(expected = IdAuthenticationAppException.class)
	public void showProcessKycValidator()
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		KycAuthRequestDTO kycAuthReqDTO = new KycAuthRequestDTO();
		kycAuthReqDTO.setIndividualId("5134256294");
		kycAuthReqDTO.setIndividualIdType(IdType.UIN.getType());
		kycAuthReqDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BindException(kycAuthReqDTO, "kycAuthReqDTO");
		errors.rejectValue("id", "errorCode", "defaultMessage");
		kycFacade.authenticateIndividual(kycAuthReqDTO, true, "123456789");
		kycAuthController.processKyc(kycAuthReqDTO, errors, "123456", "123456");
	}

	@Test
	public void processKycSuccess()
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {

		KycAuthRequestDTO kycAuthReqDTO = new KycAuthRequestDTO();
		kycAuthReqDTO.setIndividualIdType(IdType.UIN.getType());
		kycAuthReqDTO.setId("id");
		kycAuthReqDTO.setVersion("1.1");
		kycAuthReqDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthReqDTO.setId("id");
		kycAuthReqDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(false);
		authTypeDTO.setBio(false);
		authTypeDTO.setDemo(false);
		authTypeDTO.setOtp(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO request = new RequestDTO();
		kycAuthReqDTO.setIndividualId("5134256294");
		request.setOtp("456789");
		request.setDemographics(idDTO);
		kycAuthReqDTO.setRequest(request);
		kycAuthReqDTO.setRequestedAuth(authTypeDTO);
		kycAuthReqDTO.setRequest(request);
		kycAuthReqDTO.setSecondaryLangCode("fra");
		KycResponseDTO kycResponseDTO = new KycResponseDTO();
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthResponseDTO.setTransactionID("34567");
		kycAuthResponseDTO.setErrors(null);
		kycResponseDTO.setKycStatus(Boolean.TRUE);

		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		kycResponseDTO.setIdentity(idInfo);
		kycAuthResponseDTO.setResponse(kycResponseDTO);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setStaticToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authResponseDTO.setErrors(null);
		authResponseDTO.setTransactionID("123456789");
		authResponseDTO.setVersion("1.0");
		Mockito.when(kycFacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenReturn(authResponseDTO);
		Mockito.when(kycFacade.processKycAuth(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(kycAuthResponseDTO);
//		Mockito.when(kycService.processKycAuth(kycAuthReqDTO, authResponseDTO, "123456789"))
//				.thenReturn(kycAuthResponseDTO);
		kycAuthController.processKyc(kycAuthReqDTO, errors, "123456789", "12345689");
		assertFalse(error.hasErrors());
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void processKycFailure()
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
		kycAuthRequestDTO.setIndividualIdType(IdType.UIN.getType());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(false);
		authTypeDTO.setOtp(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO request = new RequestDTO();
		kycAuthRequestDTO.setIndividualId("5134256294");
		request.setOtp("456789");
		request.setDemographics(idDTO);
		kycAuthRequestDTO.setRequest(request);
		kycAuthRequestDTO.setRequestedAuth(authTypeDTO);
		kycAuthRequestDTO.setRequest(request);

		KycResponseDTO kycResponseDTO = new KycResponseDTO();
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthResponseDTO.setTransactionID("34567");
		kycAuthResponseDTO.setErrors(null);
		kycResponseDTO.setStaticToken("2345678");
		kycResponseDTO.setKycStatus(Boolean.TRUE);

		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		kycResponseDTO.setIdentity(idInfo);
		kycAuthResponseDTO.setResponse(kycResponseDTO);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setStaticToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authResponseDTO.setErrors(null);
		authResponseDTO.setTransactionID("123456789");
		authResponseDTO.setVersion("1.0");
		Mockito.when(kycFacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenReturn(authResponseDTO);
		Mockito.when(kycFacade.processKycAuth(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
//		Mockito.when(kycFacade.processKycAuth(kycAuthRequestDTO, authResponseDTO, "12346789"))
//				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		kycAuthController.processKyc(kycAuthRequestDTO, errors, "12346789", "1234567");
	}

}
