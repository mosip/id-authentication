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

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.facade.AuthFacadeImpl;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
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
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.kyc.service.controller.KycAuthController;
import io.mosip.authentication.kyc.service.impl.KycServiceImpl;
import io.mosip.authentication.kyc.service.validator.KycAuthRequestValidator;

/**
 * @author M1047697
 *
 */
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, })
public class KycControllerTest {

	@Mock
	private RestHelper restHelper;

	@Autowired
	Environment env;

	@InjectMocks
	private RestRequestFactory restFactory;

	@InjectMocks
	private AuditRequestFactory auditFactory;

	@Mock
	private AuthFacadeImpl authFacade;

	@Mock
	private IdService<AutnTxn> idAuthService;

	@InjectMocks
	private KycAuthController kycauthController;

	@InjectMocks
	private IdInfoFetcherImpl idInfoFetcherImpl;

	@Mock
	WebDataBinder binder;

	@InjectMocks
	KycServiceImpl KycService;

	@InjectMocks
	private KycAuthRequestValidator KycAuthRequestValidator;

	@InjectMocks
	private AuthRequestValidator authRequestValidator;

	@InjectMocks
	private AuditHelper auditHelper;

	Errors errors = new BindException(KycAuthRequestDTO.class, "kycAuthReqDTO");

	@Before
	public void before() {
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.invokeMethod(kycauthController, "initKycBinder", binder);
		ReflectionTestUtils.setField(kycauthController, "authFacade", authFacade);
		ReflectionTestUtils.setField(KycService, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(KycService, "env", env);
		ReflectionTestUtils.setField(KycService, "auditHelper", auditHelper);
		ReflectionTestUtils.setField(authFacade, "env", env);
		ReflectionTestUtils.setField(auditHelper, "auditFactory", auditFactory);
		ReflectionTestUtils.setField(auditHelper, "restFactory", restFactory);
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void showProcessKycValidator()
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		KycAuthRequestDTO kycAuthReqDTO = new KycAuthRequestDTO();
		Errors errors = new BindException(kycAuthReqDTO, "kycAuthReqDTO");
		errors.rejectValue("id", "errorCode", "defaultMessage");
		Mockito.when(authFacade.authenticateIndividual(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new IdAuthenticationBusinessException());
		Mockito.when(idAuthService.getIdInfo(Mockito.any())).thenThrow(new IdAuthenticationBusinessException());
		kycauthController.processKyc(kycAuthReqDTO, errors, "123456", "123456");
	}

	@Test
	public void processKycSuccess()
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {

		KycAuthRequestDTO kycAuthReqDTO = new KycAuthRequestDTO();
		kycAuthReqDTO.setId("id");
		kycAuthReqDTO.setVersion("1.1");
		kycAuthReqDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthReqDTO.setId("id");
		kycAuthReqDTO.setTransactionID("1234567890");
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
		kycAuthReqDTO.setIndividualId("5134256294");
		kycAuthReqDTO.setIndividualIdType(IdType.UIN.getType());
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
		kycResponseDTO.setTtl(env.getProperty("ekyc.ttl.hours"));
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
		Mockito.when(authFacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenReturn(authResponseDTO);
		Mockito.when(KycService.processKycAuth(kycAuthReqDTO, authResponseDTO, "123456789"))
				.thenReturn(kycAuthResponseDTO);
		kycauthController.processKyc(kycAuthReqDTO, errors, "123456789", "12345689");
		assertFalse(errors.hasErrors());
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void processKycFailure()
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
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
		kycResponseDTO.setTtl(env.getProperty("ekyc.ttl.hours"));
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
		Mockito.when(authFacade.authenticateIndividual(Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenReturn(authResponseDTO);
		Mockito.when(KycService.processKycAuth(kycAuthRequestDTO, authResponseDTO, "12346789"))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		kycauthController.processKyc(kycAuthRequestDTO, errors, "12346789", "1234567");
	}

}
