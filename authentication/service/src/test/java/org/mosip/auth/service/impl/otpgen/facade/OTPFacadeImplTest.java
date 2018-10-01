package org.mosip.auth.service.impl.otpgen.facade;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mosip.auth.core.dto.indauth.IdType;
import org.mosip.auth.core.dto.otpgen.OtpRequestDTO;
import org.mosip.auth.core.dto.otpgen.OtpResponseDTO;
import org.mosip.auth.core.spi.idauth.service.IdAuthService;
import org.mosip.auth.core.spi.otpgen.service.OTPService;
import org.mosip.auth.service.dao.AutnTxnRepository;
import org.mosip.auth.service.entity.AutnTxn;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

/**
 * Test class for OTPFacadeImpl. Mockito with PowerMockito.
 *
 * @author Rakesh Roshan
 */


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(value = { "classpath:log.properties" })
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class OTPFacadeImplTest {

	@Mock
	OtpRequestDTO otpRequestDto;
	@Mock
	OtpResponseDTO otpResponseDTO;
	@Mock
	OTPService otpService;
	@Autowired
	Environment env;
	@Mock
	AutnTxnRepository autntxnrepository;
	@Mock
	Date date;
	@Mock
	AutnTxn autnTxn;
	@Mock
	IdAuthService idAuthService;

	// @Mock
	// DateUtil dateUtil ;

	@InjectMocks
	OTPFacadeImpl otpFacadeImpl;

	@Before
	public void before() {
		//MockitoAnnotations.initMocks(this);

		otpRequestDto = getOtpRequestDTO();
		otpResponseDTO = getOtpResponseDTO();

		MosipRollingFileAppender mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName(env.getProperty("log4j.appender.Appender"));
		mosipRollingFileAppender.setFileName(env.getProperty("log4j.appender.Appender.file"));
		mosipRollingFileAppender.setFileNamePattern(env.getProperty("log4j.appender.Appender.filePattern"));
		mosipRollingFileAppender.setMaxFileSize(env.getProperty("log4j.appender.Appender.maxFileSize"));
		mosipRollingFileAppender.setTotalCap(env.getProperty("log4j.appender.Appender.totalCap"));
	
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);
		ReflectionTestUtils.setField(otpFacadeImpl, "env", env);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "initializeLogger", mosipRollingFileAppender);
	}

	@Test
	public void testIsOtpFlooded_False() {
		String uniqueID = otpRequestDto.getId();
		Date requestTime = otpRequestDto.getReqTime();
		Date addMinutesInOtpRequestDTime = new Date();

		ReflectionTestUtils.setField(otpFacadeImpl, "autntxnrepository", autntxnrepository);
		String pattern = env.getProperty("date.format.pattern");
		ReflectionTestUtils.invokeMethod(autntxnrepository, "countRequestDTime", requestTime,
				addMinutesInOtpRequestDTime, uniqueID);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "isOtpFlooded", otpRequestDto);
	}

	@Test
	public void testAddMinute() {
		Date requestTime = otpRequestDto.getReqTime();
	}

	@Test
	public void testSaveAutnTxn() {
		ReflectionTestUtils.invokeMethod(autntxnrepository, "saveAndFlush", autnTxn);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "saveAutnTxn", otpRequestDto);
	}

	
	@Test
	public void testGetRefIdForUIN() {
		String uniqueID = otpRequestDto.getId();
		String actualrefid=ReflectionTestUtils.invokeMethod(idAuthService, "validateUIN", uniqueID);
		String expactedRefId=ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
		assertEquals(actualrefid, expactedRefId);
	}

	
	@Test
	public void testGetRefIdForVID() {
		String uniqueID = otpRequestDto.getId();
		otpRequestDto.setIdType(IdType.VID);
		String actualrefid=ReflectionTestUtils.invokeMethod(idAuthService, "validateVID", uniqueID);
		String expactedRefId=ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
		
		assertEquals(actualrefid, expactedRefId);
	}

	/*
	 * @Test(expected=IdAuthenticationBusinessException.class) public void
	 * testGetRefIdForUINHandleBusineddException() throws
	 * IdAuthenticationBusinessException { IdAuthenticationBusinessException e = new
	 * IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN)
	 * ; String uniqueID = otpRequestDto.getUniqueID();
	 * //Mockito.when(idAuthService.validateUIN(uniqueID)).thenThrow(e);
	 * ReflectionTestUtils.invokeMethod(idAuthService, "validateUIN", "");
	 * ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
	 * 
	 * }
	 */

	// =========================================================
	// ************ Helping Method *****************************
	// =========================================================

	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setMsaLicenseKey("1234567890");
		otpRequestDto.setMuaCode("1234567890");
		otpRequestDto.setIdType(IdType.UIN);

		// otpRequestDto.setReqTime(new Date(Long.valueOf("2018-09-2412:06:28.501")));
		otpRequestDto.setReqTime(new Date());
		otpRequestDto.setTxnID("1234567890");
		otpRequestDto.setId("1234567890");
		otpRequestDto.setVer("1.0");

		return otpRequestDto;
	}

	private OtpResponseDTO getOtpResponseDTO() {
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		otpResponseDTO.setStatus("OTP_GENERATED");
		otpResponseDTO.setResponseTime(new Date());

		return otpResponseDTO;
	}
}
