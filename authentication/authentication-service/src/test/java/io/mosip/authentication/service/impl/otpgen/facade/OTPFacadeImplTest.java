package io.mosip.authentication.service.impl.otpgen.facade;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.idauth.service.IdAuthService;
import io.mosip.authentication.core.spi.otpgen.service.OTPService;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.repository.AutnTxnRepository;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;

/**
 * Test class for OTPFacadeImpl. Mockito with PowerMockito.
 *
 * @author Rakesh Roshan
 */

@RunWith(SpringRunner.class)
@SpringBootTest
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

	@InjectMocks
	OTPFacadeImpl otpFacadeImpl;

	@Before
	public void before() {
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
	public void test_GenerateOTP() throws IdAuthenticationBusinessException {
		String unqueId = otpRequestDto.getId();
		String txnID = otpRequestDto.getTxnID();
		String productid = "IDA";
		String refId = "8765";
		String otp = "987654";

		Mockito.when(idAuthService.validateUIN(unqueId)).thenReturn(refId);
		String otpKey = OTPUtil.generateKey(productid, refId, txnID, otpRequestDto.getMuaCode());
		Mockito.when(otpService.generateOtp(otpKey)).thenReturn(otp);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "generateOtp", otpRequestDto);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGenerateOTP_WhenOtpIsFlooded_ThrowException() throws IdAuthenticationBusinessException {
		Mockito.when(autntxnrepository.countRequestDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(5);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "isOtpFlooded", otpRequestDto);
		Mockito.when(otpFacadeImpl.generateOtp(otpRequestDto))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED));
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGenerateOTP_WhenOTPIsNull_ThrowException() throws IdAuthenticationBusinessException {
		String unqueId = otpRequestDto.getId();
		String txnID = otpRequestDto.getTxnID();
		String productid = "IDA";
		String refId = "8765";
		String otp = null;

		Mockito.when(idAuthService.validateUIN(unqueId)).thenReturn(refId);
		String otpKey = OTPUtil.generateKey(productid, refId, txnID, otpRequestDto.getMuaCode());
		Mockito.when(otpService.generateOtp(otpKey)).thenReturn(otp);
		Mockito.when(otpFacadeImpl.generateOtp(otpRequestDto))
		.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED));
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
		String actualrefid = ReflectionTestUtils.invokeMethod(idAuthService, "validateUIN", uniqueID);
		String expactedRefId = ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
		assertEquals(actualrefid, expactedRefId);
	}

	@Test
	public void test_WhenInvalidID_ForUIN_RefIdIsNull() throws IdAuthenticationBusinessException {
		otpRequestDto.setId("cvcvcjhg76");
		String uniqueID = otpRequestDto.getId();
		ReflectionTestUtils.invokeMethod(idAuthService, "validateUIN", uniqueID);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
	}

	@Test
	public void testGetRefIdForVID() {
		String uniqueID = otpRequestDto.getId();
		otpRequestDto.setIdType(IdType.VID.getType());
		String actualrefid = ReflectionTestUtils.invokeMethod(idAuthService, "validateVID", uniqueID);
		String expactedRefId = ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);

		assertEquals(actualrefid, expactedRefId);
	}

	@Test
	public void test_WhenInvalidID_ForVID_RefIdIsNull() throws IdAuthenticationBusinessException {
		otpRequestDto.setId("cvcvcjhg76");
		otpRequestDto.setIdType(IdType.VID.getType());
		String uniqueID = otpRequestDto.getId();
		ReflectionTestUtils.invokeMethod(idAuthService, "validateVID", uniqueID);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
	}

	// =========================================================
	// ************ Helping Method *****************************
	// =========================================================

	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setMsaLicenseKey("2345678901234");
		otpRequestDto.setMuaCode("2345678901234");
		otpRequestDto.setIdType(IdType.UIN.getType());

		// otpRequestDto.setReqTime(new Date(Long.valueOf("2018-09-2412:06:28.501")));
		otpRequestDto.setReqTime(new Date());
		otpRequestDto.setTxnID("2345678901234");
		otpRequestDto.setId("2345678901234");
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
