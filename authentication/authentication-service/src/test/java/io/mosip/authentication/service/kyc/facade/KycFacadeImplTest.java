/**
 * 
 */
package io.mosip.authentication.service.kyc.facade;

import static org.junit.Assert.assertNotNull;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.facade.AuthFacadeImpl;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.helper.RestHelperImpl;
import io.mosip.authentication.common.service.impl.AuthtypeStatusImpl;
import io.mosip.authentication.common.service.impl.BioAuthServiceImpl;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.patrner.PartnerServiceImpl;
import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.KycResponseDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.service.kyc.facade.KycFacadeImpl;
import io.mosip.idrepository.core.dto.AuthtypeStatus;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * @author Dinesh Karuppiah.T
 *
 */
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, TemplateManagerBuilderImpl.class, PartnerServiceImpl.class,PartnerServiceImpl.class, PartnerServiceManager.class, RestRequestFactory.class,RestHelper.class,RestHelperImpl.class})
public class KycFacadeImplTest {

	@InjectMocks
	private KycFacadeImpl kycFacade;

	@InjectMocks
	private AuthFacadeImpl authFacadeImpl;

	@Mock
	private IdInfoFetcher idInfoFetcher;

	@Mock
	IdService<AutnTxn> idinfoservice;

	@Autowired
	Environment env;

	@Mock
	private IdService<?> idInfoService;

	@Mock
	private BioAuthServiceImpl bioAuthService;

	@Mock
	private AuditHelper auditHelper;

	@Mock
	private TokenIdManager tokenIdManager;

	@Mock
	private NotificationService notificationService;
	
	@Mock
	private KycService kycService;
	
	@Mock
	private IdService<AutnTxn> idService;

	@Mock
	private IdaUinHashSaltRepo uinHashSaltRepo;
	
	@Mock
	private IdAuthSecurityManager idAuthSecurityManager;
	
	@Mock
	private AuthtypeStatusImpl authTypeStatus;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	PartnerService partnerService;
	
	@Before
	public void beforeClass() {
		ReflectionTestUtils.setField(kycFacade, "authFacade", authFacadeImpl);
		ReflectionTestUtils.setField(kycFacade, "authFacade", authFacadeImpl);
		ReflectionTestUtils.setField(kycFacade, "idService", idService);
		ReflectionTestUtils.setField(authFacadeImpl, "uinHashSaltRepo", uinHashSaltRepo);
		ReflectionTestUtils.setField(authFacadeImpl, "securityManager", idAuthSecurityManager);
		ReflectionTestUtils.setField(authFacadeImpl, "authTypeStatusService", authTypeStatus);
		ReflectionTestUtils.setField(kycFacade, "env", env);
		ReflectionTestUtils.setField(authFacadeImpl, "env", env);
		ReflectionTestUtils.setField(authFacadeImpl, "bioAuthService", bioAuthService);
		ReflectionTestUtils.setField(authFacadeImpl, "partnerService", partnerService);
		ReflectionTestUtils.setField(authFacadeImpl, "idService", idService);
		ReflectionTestUtils.setField(kycFacade, "partnerService", partnerService);
		

	}

	@Test
	public void TestKycFacade() throws IdAuthenticationBusinessException, IdAuthenticationDaoException, JsonProcessingException {

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
		fingerValue.setData(dataDTOFinger);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris = new DataDTO();
		dataDTOIris.setBioValue("iris img");
		dataDTOIris.setBioSubType("left");
		dataDTOIris.setBioType(BioAuthType.IRIS_IMG.getType());
		irisValue.setData(dataDTOIris);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFace = new DataDTO();
		dataDTOFace.setBioValue("face img");
		dataDTOFace.setBioSubType("Thumb");
		dataDTOFace.setBioType(BioAuthType.FACE_IMG.getType());
		faceValue.setData(dataDTOFace);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idRepo.put("response", response);
		HashMap<Object, Object> identity = new HashMap<>();
		identity.put("UIN", Long.valueOf(uin));
		response.put("identity", identity );
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
//		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idinfoservice.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean()))
				.thenReturn(idRepo);
		Mockito.when(idinfoservice.getIdByUin(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		//Mockito.when(IdInfoFetcher.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idService.getToken(idRepo)).thenReturn(uin);
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn("247334310780728918141754192454591343");
		//Mockito.when(IdInfoFetcher.getIdInfo(Mockito.any())).thenReturn(idInfo);
		String partnerId = "123456";
		Mockito.when(bioAuthService.authenticate(authRequestDTO, uin, idInfo, partnerId, true)).thenReturn(authStatusInfo);
		authFacadeImpl.authenticateIndividual(authRequestDTO, true, partnerId, "12345", true);
		kycFacade.authenticateIndividual(authRequestDTO, true, partnerId, "12345");
	}
	
	
	@Test
	public void processKycAuthValid() throws IdAuthenticationBusinessException, JsonProcessingException {
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
		kycAuthRequestDTO.setIndividualIdType(IdType.UIN.getType());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setTransactionID("1234567890");
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
		kycAuthRequestDTO.setRequest(request);
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(repoDetails());
		KycResponseDTO kycResponseDTO = new KycResponseDTO();
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthResponseDTO.setTransactionID("34567");
		kycAuthResponseDTO.setErrors(null);
		kycResponseDTO.setAuthToken("2345678");
		kycResponseDTO.setKycStatus(Boolean.TRUE);

		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		kycResponseDTO.setIdentity(mapper.writeValueAsString(idInfo));
		kycAuthResponseDTO.setResponse(kycResponseDTO);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res=new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authResponseDTO.setErrors(null);
		authResponseDTO.setTransactionID("123456789");
		authResponseDTO.setVersion("1.0");
		Mockito.when(kycService.retrieveKycInfo(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(kycResponseDTO);
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(authTypeStatus.fetchAuthtypeStatus(Mockito.anyString())).thenReturn(new ArrayList<AuthtypeStatus>());
		assertNotNull(kycFacade.processKycAuth(kycAuthRequestDTO, authResponseDTO, "123456"));

	}

	private Map<String, Object> repoDetails() {
		Map<String, Object> map = new HashMap<>();
		map.put("uin", "863537");
		return map;
	}
}
