package io.mosip.authentication.common.service.impl;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.manager.IdAuthFraudAnalysisEventManager;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.IDAMappingFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.patrner.PartnerServiceImpl;
import io.mosip.authentication.common.service.integration.IdTemplateManager;
import io.mosip.authentication.common.service.integration.OTPManager;
import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorRequestDto;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorResponseDto;
import io.mosip.authentication.common.service.repository.AuthLockRepository;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.repository.PartnerMappingRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.TestObjectWithMetadata;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.OtpErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;

/**
 * Test class for OTPServiceImpl.
 *
 * @author Dinesh Karuppiah.T
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IDAMappingConfig.class, IDAMappingFactory.class, RestRequestFactory.class})
@WebMvcTest
@Import(EnvUtil.class)
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

    @Mock
    private PartnerServiceImpl partnerServiceImpl;

    @Mock
    private PartnerServiceManager partnerServiceManager;

    @Mock
    private PartnerMappingRepository partnerMappingRepo;

    @Mock
    private OTPManager otpManager;

    @Mock
    private RestRequestFactory restRequestFactory;

    @Mock
    private AuditRequestFactory auditRequestFactory;

    @Mock
    private RestHelper restHelper;

    @Spy
    @InjectMocks
    private IdInfoHelper idInfoHelper;

    @Mock
    private IdInfoHelper idInfoHelperMock;

    @Autowired
    private EnvUtil env;

    @Mock
    private EnvUtil envMock;

    @Mock
    private IdaUinHashSaltRepo uinHashSaltRepo;

    @Mock
    private IdAuthSecurityManager idAuthSecurityManager;

    @Mock
    private PartnerService partnerService;

    @Mock
    private IdAuthFraudAnalysisEventManager fraudEventManager;
    
    
    /** The auth lock repository. */
    @Mock
	private AuthLockRepository authLockRepository;


    @Before
    public void before() {
        ReflectionTestUtils.setField(otpServiceImpl, "env", env);
        ReflectionTestUtils.setField(otpServiceImpl, "uinHashSaltRepo", uinHashSaltRepo);
        ReflectionTestUtils.setField(otpServiceImpl, "securityManager", idAuthSecurityManager);
        ReflectionTestUtils.setField(otpServiceImpl, "otpManager", otpManager);
        ReflectionTestUtils.setField(otpServiceImpl, "idInfoHelper", idInfoHelper);
        ReflectionTestUtils.setField(otpServiceImpl, "idInfoFetcher", idInfoFetcherImpl);
        ReflectionTestUtils.setField(idInfoHelper, "env", env);
        ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);
        ReflectionTestUtils.setField(idInfoHelper, "idInfoFetcher", idInfoFetcherImpl);
        ReflectionTestUtils.setField(idInfoFetcherImpl, "environment", env);
        ReflectionTestUtils.setField(otpServiceImpl, "idAuthService", idAuthService);
        ReflectionTestUtils.setField(otpServiceImpl, "partnerService", partnerService);
        ReflectionTestUtils.setField(partnerServiceImpl, "partnerServiceManager", partnerServiceManager);
        ReflectionTestUtils.setField(partnerServiceManager, "partnerMappingRepo", partnerMappingRepo);
    }

    /**
     * This class tests the generateOtp method
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     * @throws RestServiceException
     */
    @SuppressWarnings("rawtypes")
    @Test
    public void TestgenerateOtp() throws IdAuthenticationBusinessException, RestServiceException {
        OtpRequestDTO otpRequestDto = getOtpRequestDTO();
        Map<String, Object> idResDTO = new HashMap<>();
        Map<String, List<IdentityInfoDTO>> idInfo = getIdInfo();
        idResDTO.put("uin", "426789089018");
        List<IdentityInfoDTO> mailList = new ArrayList<>();
        IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
        identityInfoDTO.setValue("abc@test.com");
        identityInfoDTO.setLanguage("eng");
        mailList.add(identityInfoDTO);
        idInfo.put("email", mailList);
        idResDTO.put("response", idInfo);
        Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(idResDTO);
        Mockito.when(idAuthService.getToken(Mockito.any())).thenReturn("426789089018");
        Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
        Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
        RestRequestDTO value = getRestDto();
        Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
        ResponseWrapper<Map> response = new ResponseWrapper<>();
        Map<String, Object> map = new HashMap<>();
        map.put("otp", "123456");
        map.put("status", "success");
        map.put("messaage", "otp_generated");
        response.setResponse(map);
        Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
        Mockito.when(otpManager.sendOtp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
        Map<String, List<IdentityInfoDTO>> idInfo1 = getIdInfo();
        List<IdentityInfoDTO> mailList1 = new ArrayList<>();
        List<IdentityInfoDTO> emptyList = new ArrayList<>();
        IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
        identityInfoDTO1.setValue("426789089018");
        identityInfoDTO1.setLanguage(null);
        mailList1.add(identityInfoDTO1);
        idInfo1.put("uin", mailList1);
        idInfo1.put("phone", emptyList);
        idInfo1.put("email", emptyList);
        System.out.println("idInfo1="+idInfo1);
        Mockito.doReturn("abc@test.com").when(idInfoHelper).getEntityInfoAsString(DemoMatchType.EMAIL, idInfo1);
        otpServiceImpl.generateOtp(otpRequestDto, "1234567890", new TestObjectWithMetadata());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void TestPhonenumberisNull_Phone_Channel_Alone() throws IdAuthenticationBusinessException, RestServiceException {
        OtpRequestDTO otpRequestDto = new OtpRequestDTO();
        otpRequestDto.setId("id");
        otpRequestDto.setRequestTime(new SimpleDateFormat(EnvUtil.getDateTimePattern()).format(new Date()));
        otpRequestDto.setTransactionID("1234567890");
        List<String> channelList = List.of("PHONE");
        otpRequestDto.setOtpChannel(channelList);
        otpRequestDto.setIndividualId("2345678901234");
        otpRequestDto.setIndividualIdType(IdType.UIN.getType());
        otpRequestDto.setRequestTime("2019-02-18T18:17:48.923+05:30");
        Map<String, Object> valueMap = new HashMap<>();
        Map<String, Object> idInfo = new HashMap<>();
        idInfo.put("email", "abc@test.com");
        valueMap.put("response", idInfo);
        Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(valueMap);
        Mockito.when(idAuthService.getToken(Mockito.any())).thenReturn("2345678901234");
        Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
        Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
        RestRequestDTO value = getRestDto();
        Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
        ResponseWrapper<Map> response = new ResponseWrapper<>();
        Map<String, Object> map = new HashMap<>();
        map.put("otp", "123456");
        response.setResponse(map);
        Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
        Mockito.when(otpManager.sendOtp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
        		Mockito.any())).thenReturn(true);
        try {
            otpServiceImpl.generateOtp(otpRequestDto, "1234567890", new TestObjectWithMetadata());
            Assert.fail();
        }
        catch(IdAuthenticationBusinessException ex) {
            assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(), ex.getErrorCode());
            assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage() + ". Phone Number is not found in identity data.", ex.getErrorText());
        }
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void TestPhonenumberisNull_bothChannels() throws IdAuthenticationBusinessException, RestServiceException {
        OtpRequestDTO otpRequestDto = new OtpRequestDTO();
        otpRequestDto.setId("id");
        otpRequestDto.setRequestTime(new SimpleDateFormat(EnvUtil.getDateTimePattern()).format(new Date()));
        otpRequestDto.setTransactionID("1234567890");
        List<String> channelList = List.of("PHONE", "EMAIL");
        otpRequestDto.setOtpChannel(channelList);
        otpRequestDto.setIndividualId("2345678901234");
        otpRequestDto.setIndividualIdType(IdType.UIN.getType());
        otpRequestDto.setRequestTime("2019-02-18T18:17:48.923+05:30");
        Map<String, Object> valueMap = new HashMap<>();
        Map<String, Object> idInfo = new HashMap<>();
        idInfo.put("email", "abc@test.com");
        valueMap.put("response", idInfo);
        Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(valueMap);
        Mockito.when(idAuthService.getToken(Mockito.any())).thenReturn("2345678901234");
        Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
        Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
        RestRequestDTO value = getRestDto();
        Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
        ResponseWrapper<Map> response = new ResponseWrapper<>();
        Map<String, Object> map = new HashMap<>();
        map.put("otp", "123456");
        response.setResponse(map);
        Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
        Mockito.when(otpManager.sendOtp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
        		Mockito.any())).thenReturn(true);
        otpServiceImpl.generateOtp(otpRequestDto, "1234567890", new TestObjectWithMetadata());
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void TestEmailIdisNull_Email_Channel_Alone() throws IdAuthenticationBusinessException, RestServiceException {
        OtpRequestDTO otpRequestDto = new OtpRequestDTO();
        otpRequestDto.setId("id");
        otpRequestDto.setRequestTime(new SimpleDateFormat(EnvUtil.getDateTimePattern()).format(new Date()));
        otpRequestDto.setTransactionID("1234567890");
        List<String> channelList = List.of("EMAIL");
        otpRequestDto.setOtpChannel(channelList);
        otpRequestDto.setIndividualId("2345678901234");
        otpRequestDto.setIndividualIdType(IdType.UIN.getType());
        otpRequestDto.setRequestTime("2019-02-18T18:17:48.923+05:30");
        Map<String, Object> valueMap = new HashMap<>();
        Map<String, Object> idInfo = new HashMap<>();
        idInfo.put("phone", "9292292934");
        valueMap.put("response", idInfo);
        Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(valueMap);
        Mockito.when(idAuthService.getToken(Mockito.any())).thenReturn("2345678901234");
        Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
        Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
        RestRequestDTO value = getRestDto();
        Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
        ResponseWrapper<Map> response = new ResponseWrapper<>();
        Map<String, Object> map = new HashMap<>();
        map.put("otp", "123456");
        response.setResponse(map);
        Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
        Mockito.when(otpManager.sendOtp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
        		Mockito.any())).thenReturn(true);
        try {
            otpServiceImpl.generateOtp(otpRequestDto, "1234567890", new TestObjectWithMetadata());
            Assert.fail();
        }
        catch(IdAuthenticationBusinessException ex) {
            assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(), ex.getErrorCode());
            assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage() + ". Email ID is not found in identity data.", ex.getErrorText());
        }
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void TestEmailIdisNull_bothChannels() throws IdAuthenticationBusinessException, RestServiceException {
        OtpRequestDTO otpRequestDto = new OtpRequestDTO();
        otpRequestDto.setId("id");
        otpRequestDto.setRequestTime(new SimpleDateFormat(EnvUtil.getDateTimePattern()).format(new Date()));
        otpRequestDto.setTransactionID("1234567890");
        List<String> channelList = List.of("PHONE", "EMAIL");
        otpRequestDto.setOtpChannel(channelList);
        otpRequestDto.setIndividualId("2345678901234");
        otpRequestDto.setIndividualIdType(IdType.UIN.getType());
        otpRequestDto.setRequestTime("2019-02-18T18:17:48.923+05:30");
        Map<String, Object> valueMap = new HashMap<>();
        Map<String, Object> idInfo = new HashMap<>();
        idInfo.put("phone", "9384848384");
        valueMap.put("response", idInfo);
        Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(valueMap);
        Mockito.when(idAuthService.getToken(Mockito.any())).thenReturn("2345678901234");
        Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
        Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
        RestRequestDTO value = getRestDto();
        Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
        ResponseWrapper<Map> response = new ResponseWrapper<>();
        Map<String, Object> map = new HashMap<>();
        map.put("otp", "123456");
        response.setResponse(map);
        Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
        Mockito.when(otpManager.sendOtp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
        		Mockito.any())).thenReturn(true);
        otpServiceImpl.generateOtp(otpRequestDto, "1234567890", new TestObjectWithMetadata());
    }
    
    @Test
    public void TestPhoneorEmailisNull_both_channels_provided() throws IdAuthenticationBusinessException, RestServiceException {
        OtpRequestDTO otpRequestDto = new OtpRequestDTO();
        otpRequestDto.setId("id");
        otpRequestDto.setRequestTime(new SimpleDateFormat(EnvUtil.getDateTimePattern()).format(new Date()));
        otpRequestDto.setTransactionID("1234567890");
        List<String> channelList = List.of("PHONE", "EMAIL");
        otpRequestDto.setOtpChannel(channelList);
        String individualId = "2345678901234";
        otpRequestDto.setIndividualId(individualId);
        otpRequestDto.setIndividualIdType(IdType.UIN.getType());
        otpRequestDto.setRequestTime("2019-02-18T18:17:48.923+05:30");
        Map<String, Object> valueMap = new HashMap<>();
        Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
        valueMap.put("response", idInfo);
        Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(valueMap);
        Mockito.when(idAuthService.getToken(Mockito.any())).thenReturn(individualId);
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

        Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(valueMap);
        Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
        Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");

        Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
                IdRepoErrorConstants.CLIENT_ERROR, response.toString(), response));
        try {
			otpServiceImpl.generateOtp(otpRequestDto, "1234567890", new TestObjectWithMetadata());
            Assert.fail();
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(), ex.getErrorCode());
            assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage() + ". Both Phone Number and Email ID are not found in identity data.", ex.getErrorText());
		}
    }
    
    
    @SuppressWarnings("rawtypes")
    @Test
    public void TestPhonenumberisNull_Phone_Channel_Alone_lowercase() throws IdAuthenticationBusinessException, RestServiceException {
        OtpRequestDTO otpRequestDto = new OtpRequestDTO();
        otpRequestDto.setId("id");
        otpRequestDto.setRequestTime(new SimpleDateFormat(EnvUtil.getDateTimePattern()).format(new Date()));
        otpRequestDto.setTransactionID("1234567890");
        List<String> channelList = List.of("phone");
        otpRequestDto.setOtpChannel(channelList);
        otpRequestDto.setIndividualId("2345678901234");
        otpRequestDto.setIndividualIdType(IdType.UIN.getType());
        otpRequestDto.setRequestTime("2019-02-18T18:17:48.923+05:30");
        Map<String, Object> valueMap = new HashMap<>();
        Map<String, Object> idInfo = new HashMap<>();
        idInfo.put("email", "abc@test.com");
        valueMap.put("response", idInfo);
        Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(valueMap);
        Mockito.when(idAuthService.getToken(Mockito.any())).thenReturn("2345678901234");
        Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
        Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
        RestRequestDTO value = getRestDto();
        Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
        ResponseWrapper<Map> response = new ResponseWrapper<>();
        Map<String, Object> map = new HashMap<>();
        map.put("otp", "123456");
        response.setResponse(map);
        Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
        Mockito.when(otpManager.sendOtp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
        		Mockito.any())).thenReturn(true);
        try {
            otpServiceImpl.generateOtp(otpRequestDto, "1234567890", new TestObjectWithMetadata());
            Assert.fail();
        }
        catch(IdAuthenticationBusinessException ex) {
            assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(), ex.getErrorCode());
            assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage() + ". Phone Number is not found in identity data.", ex.getErrorText());
        }
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void TestPhonenumberisNull_bothChannels_lowercase() throws IdAuthenticationBusinessException, RestServiceException {
        OtpRequestDTO otpRequestDto = new OtpRequestDTO();
        otpRequestDto.setId("id");
        otpRequestDto.setRequestTime(new SimpleDateFormat(EnvUtil.getDateTimePattern()).format(new Date()));
        otpRequestDto.setTransactionID("1234567890");
        List<String> channelList = List.of("phone", "email");
        otpRequestDto.setOtpChannel(channelList);
        otpRequestDto.setIndividualId("2345678901234");
        otpRequestDto.setIndividualIdType(IdType.UIN.getType());
        otpRequestDto.setRequestTime("2019-02-18T18:17:48.923+05:30");
        Map<String, Object> valueMap = new HashMap<>();
        Map<String, Object> idInfo = new HashMap<>();
        idInfo.put("email", "abc@test.com");
        valueMap.put("response", idInfo);
        Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(valueMap);
        Mockito.when(idAuthService.getToken(Mockito.any())).thenReturn("2345678901234");
        Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
        Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
        RestRequestDTO value = getRestDto();
        Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
        ResponseWrapper<Map> response = new ResponseWrapper<>();
        Map<String, Object> map = new HashMap<>();
        map.put("otp", "123456");
        response.setResponse(map);
        Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
        Mockito.when(otpManager.sendOtp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
        		Mockito.any())).thenReturn(true);
        otpServiceImpl.generateOtp(otpRequestDto, "1234567890", new TestObjectWithMetadata());
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void TestEmailIdisNull_Email_Channel_Alone_lowercase() throws IdAuthenticationBusinessException, RestServiceException {
        OtpRequestDTO otpRequestDto = new OtpRequestDTO();
        otpRequestDto.setId("id");
        otpRequestDto.setRequestTime(new SimpleDateFormat(EnvUtil.getDateTimePattern()).format(new Date()));
        otpRequestDto.setTransactionID("1234567890");
        List<String> channelList = List.of("email");
        otpRequestDto.setOtpChannel(channelList);
        otpRequestDto.setIndividualId("2345678901234");
        otpRequestDto.setIndividualIdType(IdType.UIN.getType());
        otpRequestDto.setRequestTime("2019-02-18T18:17:48.923+05:30");
        Map<String, Object> valueMap = new HashMap<>();
        Map<String, Object> idInfo = new HashMap<>();
        idInfo.put("phone", "9292292934");
        valueMap.put("response", idInfo);
        Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(valueMap);
        Mockito.when(idAuthService.getToken(Mockito.any())).thenReturn("2345678901234");
        Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
        Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
        RestRequestDTO value = getRestDto();
        Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
        ResponseWrapper<Map> response = new ResponseWrapper<>();
        Map<String, Object> map = new HashMap<>();
        map.put("otp", "123456");
        response.setResponse(map);
        Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
        Mockito.when(otpManager.sendOtp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
        		Mockito.any())).thenReturn(true);
        try {
            otpServiceImpl.generateOtp(otpRequestDto, "1234567890", new TestObjectWithMetadata());
            Assert.fail();
        }
        catch(IdAuthenticationBusinessException ex) {
            assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(), ex.getErrorCode());
            assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage() + ". Email ID is not found in identity data.", ex.getErrorText());
        }
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void TestEmailIdisNull_bothChannels_lowercase() throws IdAuthenticationBusinessException, RestServiceException {
        OtpRequestDTO otpRequestDto = new OtpRequestDTO();
        otpRequestDto.setId("id");
        otpRequestDto.setRequestTime(new SimpleDateFormat(EnvUtil.getDateTimePattern()).format(new Date()));
        otpRequestDto.setTransactionID("1234567890");
        List<String> channelList = List.of("phone", "email");
        otpRequestDto.setOtpChannel(channelList);
        otpRequestDto.setIndividualId("2345678901234");
        otpRequestDto.setIndividualIdType(IdType.UIN.getType());
        otpRequestDto.setRequestTime("2019-02-18T18:17:48.923+05:30");
        Map<String, Object> valueMap = new HashMap<>();
        Map<String, Object> idInfo = new HashMap<>();
        idInfo.put("phone", "9384848384");
        valueMap.put("response", idInfo);
        Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(valueMap);
        Mockito.when(idAuthService.getToken(Mockito.any())).thenReturn("2345678901234");
        Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
        Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
        Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
        RestRequestDTO value = getRestDto();
        Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
        ResponseWrapper<Map> response = new ResponseWrapper<>();
        Map<String, Object> map = new HashMap<>();
        map.put("otp", "123456");
        response.setResponse(map);
        Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
        Mockito.when(otpManager.sendOtp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
        		Mockito.any())).thenReturn(true);
        otpServiceImpl.generateOtp(otpRequestDto, "1234567890", new TestObjectWithMetadata());
    }
    
    @Test
    public void TestPhoneorEmailisNull_both_channels_provided_lowercase() throws IdAuthenticationBusinessException, RestServiceException {
        OtpRequestDTO otpRequestDto = new OtpRequestDTO();
        otpRequestDto.setId("id");
        otpRequestDto.setRequestTime(new SimpleDateFormat(EnvUtil.getDateTimePattern()).format(new Date()));
        otpRequestDto.setTransactionID("1234567890");
        List<String> channelList = List.of("phone", "email");
        otpRequestDto.setOtpChannel(channelList);
        String individualId = "2345678901234";
        otpRequestDto.setIndividualId(individualId);
        otpRequestDto.setIndividualIdType(IdType.UIN.getType());
        otpRequestDto.setRequestTime("2019-02-18T18:17:48.923+05:30");
        Map<String, Object> valueMap = new HashMap<>();
        Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
        valueMap.put("response", idInfo);
        Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(valueMap);
        Mockito.when(idAuthService.getToken(Mockito.any())).thenReturn(individualId);
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

        Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(valueMap);
        Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
        Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");

        Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
                IdRepoErrorConstants.CLIENT_ERROR, response.toString(), response));
        try {
			otpServiceImpl.generateOtp(otpRequestDto, "1234567890", new TestObjectWithMetadata());
            Assert.fail();
		} catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(), ex.getErrorCode());
            assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage() + ". Both Phone Number and Email ID are not found in identity data.", ex.getErrorText());
		}
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void TestOtpFloodException() throws IdAuthenticationBusinessException {
        OtpRequestDTO otpRequestDTO = getOtpRequestDTO();
        otpRequestDTO.setRequestTime("2019-03-23T14:52:29.008");
        otpServiceImpl.generateOtp(otpRequestDTO, "1234567890", new TestObjectWithMetadata());
    }

    @Test
    public void TestOtpFloodisTrue() throws IdAuthenticationBusinessException {
        OtpRequestDTO otpRequestDTO = getOtpRequestDTO();

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
        Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(valueMap);
        Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
        Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
        System.out.println("oid= "+otpRequestDTO.getIndividualId());
        Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(100);
        try {
            otpServiceImpl.generateOtp(otpRequestDTO, "1234567890", new TestObjectWithMetadata());
        }
        catch(IdAuthenticationBusinessException ex) {
            assertEquals(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED.getErrorCode(), ex.getErrorCode());
            assertEquals(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED.getErrorMessage(), ex.getErrorText());
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
//        otpRequestDto.setRequestTime(new SimpleDateFormat(env.getDateTimePattern()).format(new Date()));
        otpRequestDto.setTransactionID("1234567890");
        ArrayList<String> channelList = new ArrayList<String>();
        channelList.add("PHONE");
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
