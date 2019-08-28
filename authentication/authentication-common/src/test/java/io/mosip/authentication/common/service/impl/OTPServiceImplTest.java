package io.mosip.authentication.common.service.impl;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.IDAMappingFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.OTPServiceImpl;
import io.mosip.authentication.common.service.integration.IdTemplateManager;
import io.mosip.authentication.common.service.integration.OTPManager;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorRequestDto;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorResponseDto;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.UinEncryptSaltRepo;
import io.mosip.authentication.common.service.repository.UinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthTransactionManager;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.OtpErrorConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;

/**
 * Test class for OTPServiceImpl.
 *
 * @author Dinesh Karuppiah.T
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IDAMappingConfig.class,
		IDAMappingFactory.class })
@WebMvcTest
public class OTPServiceImplTest {

	@Mock
	AutnTxnRepository autntxnrepository;

	@Mock
	IdService<AutnTxn> idAuthService;

	@Mock
	IdTemplateManager idTemplateService;

	@InjectMocks
	private IdInfoFetcherImpl idInfoFetcherImpl;

	@InjectMocks
	private OTPServiceImpl otpServiceImpl;

	@Autowired
	private IDAMappingConfig idMappingConfig;

	@InjectMocks
	private OTPManager otpManager;

	@Mock
	private RestRequestFactory restRequestFactory;

	@Mock
	private AuditRequestFactory auditRequestFactory;

	@Mock
	private RestHelper restHelper;

	@InjectMocks
	private IdInfoHelper idInfoHelper;
	
	@Mock
	private AuditHelper auditHelper;

	@Autowired
	Environment env;
	
	@Mock
	private UinEncryptSaltRepo uinEncryptSaltRepo;
	
	@Mock
	private UinHashSaltRepo uinHashSaltRepo;
	
	@Mock
	private IdAuthTransactionManager idAuthTransactionManager;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(otpServiceImpl, "env", env);
		ReflectionTestUtils.setField(otpServiceImpl, "uinEncryptSaltRepo", uinEncryptSaltRepo);
		ReflectionTestUtils.setField(otpServiceImpl, "uinHashSaltRepo", uinHashSaltRepo);
		ReflectionTestUtils.setField(otpServiceImpl, "transactionManager", idAuthTransactionManager);
		ReflectionTestUtils.setField(otpServiceImpl, "otpManager", otpManager);
		ReflectionTestUtils.setField(otpServiceImpl, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(otpServiceImpl, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(otpManager, "environment", env);
		ReflectionTestUtils.setField(idInfoHelper, "environment", env);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);
		ReflectionTestUtils.setField(idInfoHelper, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(idInfoFetcherImpl, "environment", env);
		ReflectionTestUtils.setField(otpServiceImpl, "idAuthService", idAuthService);
		ReflectionTestUtils.setField(otpServiceImpl, "auditHelper", auditHelper);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void TestgenerateOtp() throws IdAuthenticationBusinessException, RestServiceException {
		OtpRequestDTO otpRequestDto = getOtpRequestDTO();
		Map<String, Object> valueMap = new HashMap<>();
		Map<String, List<IdentityInfoDTO>> idInfo = getIdInfo();
		valueMap.put("uin", "426789089018");
		valueMap.put("response", idInfo);
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(valueMap);
		Mockito.when(idAuthService.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
		Mockito.when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyLong())).thenReturn("2344");
		Mockito.when(idAuthTransactionManager.getUser()).thenReturn("ida_app_user");
		RestRequestDTO value = getRestDto();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
		ResponseWrapper<Map> response = new ResponseWrapper<>();
		Map<String, Object> map = new HashMap<>();
		map.put("otp", "123456");
		map.put("status", "success");
		map.put("messaage", "otp_generated");
		response.setResponse(map);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
		otpServiceImpl.generateOtp(otpRequestDto, "1234567890");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestPhoneorEmailisNull() throws IdAuthenticationBusinessException, RestServiceException {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setId("id");
		otpRequestDto.setRequestTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));
		otpRequestDto.setTransactionID("1234567890");
		ArrayList<String> channelList = new ArrayList<String>();
		otpRequestDto.setOtpChannel(channelList);
		otpRequestDto.setIndividualId("2345678901234");
		otpRequestDto.setIndividualIdType(IdType.UIN.getType());
		otpRequestDto.setRequestTime("2019-02-18T18:17:48.923+05:30");
		Map<String, Object> valueMap = new HashMap<>();
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		List<IdentityInfoDTO> mailList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("abc@bc.com");
		mailList.add(identityInfoDTO);
		List<IdentityInfoDTO> phoneList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setValue("9876543210");
		phoneList.add(identityInfoDTO1);
		idInfo.put("email", mailList);
		idInfo.put("mobile", phoneList);
		valueMap.put("uin", "426789089018");
		valueMap.put("phone", "426789089018");
		valueMap.put("response", idInfo);
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(valueMap);
		Mockito.when(idAuthService.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
		RestRequestDTO value = getRestDto();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
		ResponseWrapper<OtpGeneratorResponseDto> response = new ResponseWrapper<>();
		List<ServiceError> errors = new ArrayList<>();
		ServiceError serviceError = new ServiceError();
		serviceError.setErrorCode(OtpErrorConstants.EMAILPHONENOTREGISTERED.getErrorCode());
		serviceError.setMessage(OtpErrorConstants.EMAILPHONENOTREGISTERED.getErrorMessage());
		errors.add(serviceError);
		response.setErrors(errors);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED, response.toString(), response));
		otpServiceImpl.generateOtp(otpRequestDto, "1234567890");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestOtpFloodException() throws IdAuthenticationBusinessException {
		OtpRequestDTO otpRequestDTO = getOtpRequestDTO();
		otpRequestDTO.setRequestTime("2019-03-23T14:52:29.008");
		otpServiceImpl.generateOtp(otpRequestDTO, "1234567890");
	}

	@Test
	public void TestOtpFloodisTrue() throws IdAuthenticationBusinessException {
		OtpRequestDTO otpRequestDTO = getOtpRequestDTO();
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(100);
	   try {	
		otpServiceImpl.generateOtp(otpRequestDTO, "1234567890");
	   }
	   catch(IdAuthenticationBusinessException ex) {
		   assertEquals(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED.getErrorCode(), ex.getErrorCode());
		   assertEquals(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED.getErrorMessage(), ex.getErrorText());
	   }
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void TestPhonenumberisNull() throws IdAuthenticationBusinessException, RestServiceException {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setId("id");
		otpRequestDto.setRequestTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));
		otpRequestDto.setTransactionID("1234567890");
		ArrayList<String> channelList = new ArrayList<String>();
		otpRequestDto.setOtpChannel(channelList);
		otpRequestDto.setIndividualId("2345678901234");
		otpRequestDto.setIndividualIdType(IdType.UIN.getType());
		otpRequestDto.setRequestTime("2019-02-18T18:17:48.923+05:30");
		Map<String, Object> valueMap = new HashMap<>();
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		List<IdentityInfoDTO> mailList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("abc@test.com");
		mailList.add(identityInfoDTO);
		idInfo.put("email", mailList);
		valueMap.put("uin", "426789089018");
		valueMap.put("response", idInfo);
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(valueMap);
		Mockito.when(idAuthService.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
		Mockito.when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyLong())).thenReturn("2344");
		Mockito.when(idAuthTransactionManager.getUser()).thenReturn("ida_app_user");
		RestRequestDTO value = getRestDto();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
		ResponseWrapper<Map> response = new ResponseWrapper<>();
		Map<String, Object> map = new HashMap<>();
		map.put("otp", "123456");
		response.setResponse(map);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
	   try {	
		otpServiceImpl.generateOtp(otpRequestDto, "1234567890");
	   }
	   catch(IdAuthenticationBusinessException ex) {
		   assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(), ex.getErrorCode());
		   assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage(), ex.getErrorText());
	   }

	}

	private RestRequestDTO getRestDto() {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		restRequestDTO.setHttpMethod(HttpMethod.POST);
		restRequestDTO.setUri("http://localhost:8083/otpmanager/otps");
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		restRequestDTO.setRequestBody(otpGeneratorRequestDto);
		restRequestDTO.setTimeout(23);
		return restRequestDTO;
	}

	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setId("id");
		otpRequestDto.setRequestTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));
		otpRequestDto.setTransactionID("1234567890");
		ArrayList<String> channelList = new ArrayList<String>();
		channelList.add("MOBILE");
		channelList.add("EMAIL");
		otpRequestDto.setOtpChannel(channelList);
		otpRequestDto.setIndividualId("2345678901234");
		otpRequestDto.setIndividualIdType(IdType.UIN.getType());
		otpRequestDto.setRequestTime("2019-02-18T18:17:48.923+05:30");
		return otpRequestDto;
	}

	private Map<String, List<IdentityInfoDTO>> getIdInfo() {
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		List<IdentityInfoDTO> phoneList = new ArrayList<>();
		List<IdentityInfoDTO> mailList = new ArrayList<>();
		IdentityInfoDTO phonedto = new IdentityInfoDTO();
		phonedto.setValue("9999999999");
		phoneList.add(phonedto);
		IdentityInfoDTO maildto = new IdentityInfoDTO();
		maildto.setValue("abc@test.com");
		mailList.add(maildto);
		idInfo.put("phone", phoneList);
		idInfo.put("email", mailList);
		return idInfo;
	}

}
