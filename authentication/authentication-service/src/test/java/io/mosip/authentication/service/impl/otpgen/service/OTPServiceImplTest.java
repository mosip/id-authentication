package io.mosip.authentication.service.impl.otpgen.service;

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

import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.otpgen.ChannelDTO;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.IDAMappingFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.indauth.service.IdInfoFetcherImpl;
import io.mosip.authentication.service.impl.notification.service.NotificationServiceImpl;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.authentication.service.integration.dto.OtpGeneratorRequestDto;
import io.mosip.authentication.service.integration.dto.OtpGeneratorResponseDto;
import io.mosip.authentication.service.repository.AutnTxnRepository;

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
	IdAuthService<AutnTxn> idAuthService;

	@Mock
	IdTemplateManager idTemplateService;

	@InjectMocks
	private IdInfoHelper idInfoHelper;

	@InjectMocks
	private IdInfoFetcherImpl idInfoFetcherImpl;

	@Mock
	NotificationServiceImpl notificationService;

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

	@Autowired
	Environment env;

	@Before
	public void before() {
		ReflectionTestUtils.setField(otpServiceImpl, "env", env);
		ReflectionTestUtils.setField(otpServiceImpl, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(otpServiceImpl, "otpManager", otpManager);
		ReflectionTestUtils.setField(notificationService, "env", env);
		ReflectionTestUtils.setField(notificationService, "idTemplateManager", idTemplateService);
		ReflectionTestUtils.setField(idInfoHelper, "environment", env);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);
		ReflectionTestUtils.setField(idInfoHelper, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(idInfoFetcherImpl, "environment", env);
		ReflectionTestUtils.setField(otpServiceImpl, "notificationService", notificationService);
		ReflectionTestUtils.setField(otpServiceImpl, "idAuthService", idAuthService);
	}

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
		RestRequestDTO value = getRestDto();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
		OtpGeneratorResponseDto otpGeneratorResponseDto = new OtpGeneratorResponseDto();
		otpGeneratorResponseDto.setStatus("success");
		otpGeneratorResponseDto.setMessage("otp_generated");
		otpGeneratorResponseDto.setOtp("123456");
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpGeneratorResponseDto);
		otpServiceImpl.generateOtp(otpRequestDto, "1234567890");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestOtpisNull() throws RestServiceException, IdAuthenticationBusinessException {
		OtpRequestDTO otpRequestDto = getOtpRequestDTO();
		RestRequestDTO value = getRestDto();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
		OtpGeneratorResponseDto otpGeneratorResponseDto = new OtpGeneratorResponseDto();
		otpGeneratorResponseDto.setOtp(null);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpGeneratorResponseDto);
		otpServiceImpl.generateOtp(otpRequestDto, "1234567890");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestPhoneorEmailisNull() throws IdAuthenticationBusinessException, RestServiceException {
		OtpRequestDTO otpRequestDto = getOtpRequestDTO();
		Map<String, Object> valueMap = new HashMap<>();
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		valueMap.put("uin", "426789089018");
		valueMap.put("response", idInfo);
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(valueMap);
		Mockito.when(idAuthService.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
		RestRequestDTO value = getRestDto();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
		OtpGeneratorResponseDto otpGeneratorResponseDto = new OtpGeneratorResponseDto();
		otpGeneratorResponseDto.setOtp("123456");
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpGeneratorResponseDto);
		otpServiceImpl.generateOtp(otpRequestDto, "1234567890");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestOtpFloodException() throws IdAuthenticationBusinessException {
		OtpRequestDTO otpRequestDTO = getOtpRequestDTO();
		otpRequestDTO.setRequestTime("2019-03-23T14:52:29.008");
		otpServiceImpl.generateOtp(otpRequestDTO, "1234567890");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestOtpFloodisTrue() throws IdAuthenticationBusinessException {
		OtpRequestDTO otpRequestDTO = getOtpRequestDTO();
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(100);
		otpServiceImpl.generateOtp(otpRequestDTO, "1234567890");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestPhonenumberisNull() throws IdAuthenticationBusinessException, RestServiceException {
		OtpRequestDTO otpRequestDto = getOtpRequestDTO();
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
		RestRequestDTO value = getRestDto();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
		OtpGeneratorResponseDto otpGeneratorResponseDto = new OtpGeneratorResponseDto();
		otpGeneratorResponseDto.setOtp("123456");
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpGeneratorResponseDto);
		otpServiceImpl.generateOtp(otpRequestDto, "1234567890");

	}

	private RestRequestDTO getRestDto() {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		restRequestDTO.setHttpMethod(HttpMethod.POST);
		restRequestDTO.setUri("http://localhost:8083/otpmanager/otps");
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("test");
		restRequestDTO.setRequestBody(otpGeneratorRequestDto);
		restRequestDTO.setResponseType(OtpGeneratorResponseDto.class);
		restRequestDTO.setTimeout(23);
		return restRequestDTO;
	}

	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setId("id");
		otpRequestDto.setRequestTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));
		otpRequestDto.setTransactionID("1234567890");
		ChannelDTO otpChannel = new ChannelDTO();
		otpChannel.setPhone(true);
		otpChannel.setEmail(true);
		otpRequestDto.setOtpChannel(otpChannel);
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
