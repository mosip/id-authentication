package io.mosip.authentication.common.service.helper;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.manager.IdAuthFraudAnalysisEventManager;
import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.TestObjectWithMetadata;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BaseRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.spi.id.service.IdService;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@Import(EnvUtil.class)
@TestPropertySource("classpath:application.properties")
public class AuthTransactionHelperTest {

	@Mock
	private IdaUinHashSaltRepo uinHashSaltRepo;
	
	@Autowired
	private EnvUtil env;
	
	@Mock
	private IdAuthSecurityManager securityManager;
	
	@Mock
	private ObjectMapper objectMapper;
	
	@Mock
	private IdService<AutnTxn> idService;
	
	@Mock
	private IdAuthFraudAnalysisEventManager fraudEventManager;
	
	@InjectMocks
	AuthTransactionHelper authTransactionHelper;
	
	BaseRequestDTO baseRequestDTO;
	MockEnvironment mockenv;
	
	@Before
	public void setup() {
		baseRequestDTO = new BaseRequestDTO();
		baseRequestDTO.setId("1.0");
		baseRequestDTO.setIndividualId("123456789");
		baseRequestDTO.setIndividualIdType("uin");
		baseRequestDTO.setTransactionID("12345");
		baseRequestDTO.setVersion("v1");
		mockenv = new MockEnvironment();
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		mockenv.merge(((AbstractEnvironment) env.getEnvironment()));
		ReflectionTestUtils.setField(authTransactionHelper, "env", env);
		baseRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(mockenv.getProperty("datetime.pattern"))).toString());
		
		ReflectionTestUtils.setField(authTransactionHelper, "env", env);
	}
	
	@Test
	public void buildAuthTransactionEntityTest() throws IdAuthenticationBusinessException {
		AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance();
		authTransactionBuilder.withRequest(baseRequestDTO);
		assertNotNull(authTransactionHelper.buildAuthTransactionEntity(authTransactionBuilder));
	}
	
	@Test
	public void setAuthTransactionBuilderMetadataTest() {
		AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance();
		authTransactionBuilder.withRequest(baseRequestDTO);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO requestDTO = new RequestDTO();
		authRequestDTO.setRequest(requestDTO);
		authTransactionHelper.setAuthTransactionBuilderMetadata(authRequestDTO, authTransactionBuilder);
	}
	
	@Test
	public void createUnableToProcessExceptionTest() throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance();
		authTransactionBuilder.withRequest(baseRequestDTO);
		IdAuthenticationBusinessException e = new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		authTransactionHelper.createUnableToProcessException(authTransactionBuilder, e, new TestObjectWithMetadata());
	}
	
	@Test
	public void createDataValidationExceptionTest() throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance();
		authTransactionBuilder.withRequest(baseRequestDTO);
		IDDataValidationException e = new IDDataValidationException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		authTransactionHelper.createDataValidationException(authTransactionBuilder, e, new TestObjectWithMetadata());
	}
	
	@Test
	public void createAndSetAuthTxnBuilderMetadataToRequestTest_AuthReqDto() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO requestDTO = new RequestDTO();
		
		List<BioIdentityInfoDTO> bioDataList = new ArrayList<BioIdentityInfoDTO>();
		BioIdentityInfoDTO bioIdInfoDto2 = new BioIdentityInfoDTO();
		DataDTO dataDto2 = new DataDTO();
		dataDto2.setBioSubType("UNKNOWN");
		dataDto2.setBioType("FACE");
		DigitalId digitalId2 = new DigitalId();
		digitalId2.setSerialNo("9149791");
		digitalId2.setMake("eyecool");
		dataDto2.setDigitalId(digitalId2);
		dataDto2.setDomainUri("dev.mosip.net");
		dataDto2.setPurpose("Registration");
		dataDto2.setQualityScore(75f);
		dataDto2.setRequestedScore(95f);
		bioIdInfoDto2.setData(dataDto2);
		bioIdInfoDto2.setHash("12342");
		bioIdInfoDto2.setSessionKey("Testsessionkey2");
		bioIdInfoDto2.setSpecVersion("Spec1.2.0");
		bioIdInfoDto2.setThumbprint("testvalue2");
		bioDataList.add(bioIdInfoDto2);
		requestDTO.setBiometrics(bioDataList);
		IdentityDTO identityDto = new IdentityDTO();
		requestDTO.setDemographics(identityDto);
		requestDTO.setOtp("12345");
		authRequestDTO.setRequest(requestDTO);
		Set<RequestType> set = new HashSet<>();
		set.add(RequestType.OTP_AUTH);
		set.add(RequestType.FACE_AUTH);
		assertEquals(set,authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(authRequestDTO, false, Optional.empty()).getRequestTypes());
	}
	
	@Test
	public void createAndSetAuthTxnBuilderMetadataToRequestTest_OtpReqDTo() throws IdAuthenticationBusinessException {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		otpRequestDTO.setId("mosip.identity.otp");
		otpRequestDTO.setIndividualId("274390482564");
		otpRequestDTO.setIndividualIdType(IdType.UIN.getType());
		List<String> otpChannel = new ArrayList<>();
		otpChannel.add("email");
		otpChannel.add("mobile");
		otpRequestDTO.setOtpChannel(otpChannel);
		otpRequestDTO.setRequestTime(new SimpleDateFormat(EnvUtil.getDateTimePattern()).format(new Date()));
		otpRequestDTO.setVersion("1.0");
		Set<RequestType> set = new HashSet<>();
		set.add(RequestType.OTP_REQUEST);
		assertEquals(set,authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(otpRequestDTO, false, Optional.empty()).getRequestTypes());
	}
	
	@Test
	public void setAuthTransactionEntityMetadataTest() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO requestDTO = new RequestDTO();
		authRequestDTO.setRequest(requestDTO);
		AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance();
		authTransactionBuilder.withRequest(baseRequestDTO);

		authTransactionHelper.setAuthTransactionEntityMetadata(authRequestDTO, authTransactionBuilder);
	}
}

