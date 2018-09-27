package org.mosip.auth.service.impl.otpgen.facade;

import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mosip.auth.core.dto.indauth.IdType;
import org.mosip.auth.core.dto.otpgen.OtpRequestDTO;
import org.mosip.auth.core.dto.otpgen.OtpResponseDTO;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.spi.idauth.service.IdAuthService;
import org.mosip.auth.core.spi.otpgen.service.OTPService;
import org.mosip.auth.core.util.OTPUtil;
import org.mosip.auth.service.dao.AutnTxnRepository;
import org.mosip.auth.service.entity.AutnTxn;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Test class for OTPFacadeImpl. Mockito with PowerMockito.
 *
 * @author Rakesh Roshan
 */

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({OTPFacadeImpl.class})
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(value = {"classpath:log.properties"})
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
	@Autowired
	IdAuthService idAuthService;
	
	//@Mock
	// DateUtil dateUtil ;

	@InjectMocks
	OTPFacadeImpl otpFacadeImpl;

	@Before
	public void before() {
		otpRequestDto = getOtpRequestDTO();
		otpResponseDTO = getOtpResponseDTO();
		
		//ReflectionTestUtils.setField(otpFacadeImpl, "env", env);
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
	
	//FIXME
	/*@Test
	public void testGenerateOtp() throws IdAuthenticationBusinessException, ClassNotFoundException {
		String refId = "12345";
		String productid = "ida";
		String txnID = otpRequestDto.getTxnID();
		String auaCode = otpRequestDto.getAuaCode();
		String uniqueID = otpRequestDto.getUniqueID();
		Date requestTime = otpRequestDto.getRequestTime();
		Date addMinutesInOtpRequestDTime = DateUtil.addMinutes(requestTime, -1);
		
		//ReflectionTestUtils.invokeMethod(idAuthService, "validateUIN", uniqueID);
		//String refId =ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
		
		
		ReflectionTestUtils.invokeMethod(autntxnrepository, "countRequestDTime", requestTime,
				addMinutesInOtpRequestDTime, uniqueID);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "isOtpFlooded", otpRequestDto);
		
		ReflectionTestUtils.invokeMethod(idAuthService, "validateUIN", uniqueID);
		
		Class<OTPUtil> dateUtil = (Class<OTPUtil>) Class.forName("org.mosip.auth.core.util.OTPUtil");
		
		//ReflectionTestUtils.invokeMethod(dateUtil, "generateKey", productid,refId,txnID,auaCode);
		String otpKey = OTPUtil.generateKey(productid, refId, txnID, auaCode);
		//String otpKey ="12345";
		String otp = "806373";
		ReflectionTestUtils.invokeMethod(otpService, "generateOtp", otpKey);
		Mockito.when(otpService.generateOtp(otpKey)).thenReturn(otp);
		//ReflectionTestUtils.invokeMethod(otpFacadeImpl, "generateOtp", otpRequestDto);
		//Mockito.when(otpFacadeImpl.generateOtp(otpRequestDto)).thenReturn(otpResponseDTO);
	}*/

	@Test
	public void testIsOtpFlooded_False() {
		String uniqueID = otpRequestDto.getUniqueID();
		Date requestTime = otpRequestDto.getReqTime();
		Date addMinutesInOtpRequestDTime = new Date();
		//TODO Integrate with kernel DateUtil
				//DateUtil.addMinutes(requestTime, -1);


		//ReflectionTestUtils.setField(otpFacadeImpl, "env", env);
		ReflectionTestUtils.setField(otpFacadeImpl, "autntxnrepository", autntxnrepository);
		String pattern = env.getProperty("date.format.pattern");
		// ReflectionTestUtils.invokeMethod(dateUtil, "formatDate", date,pattern);
		// ReflectionTestUtils.invokeMethod(otpFacadeImpl, "formateDate", date,pattern);
		ReflectionTestUtils.invokeMethod(autntxnrepository, "countRequestDTime", requestTime,
				addMinutesInOtpRequestDTime, uniqueID);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "isOtpFlooded", otpRequestDto);
	}

	@Test
	public void testAddMinute() {
		//otpRequestDto = getOtpRequestDTO();
		Date requestTime = otpRequestDto.getReqTime();
		//ReflectionTestUtils.setField(otpFacadeImpl, "DateUtil", DateUtil.class);
		//ReflectionTestUtils.invokeMethod(DateUtil, "formateDate", requestTime,-1);
		//ReflectionTestUtils.invokeMethod(otpFacadeImpl, "formateDate", requestTime,-1);
	}
	
	@Test
	public void testSaveAutnTxn() {
		ReflectionTestUtils.invokeMethod(autntxnrepository, "saveAndFlush", autnTxn);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "saveAutnTxn", otpRequestDto);
	}

	@Ignore
	@Test
	public void testGetRefIdForUIN() {
		String uniqueID = otpRequestDto.getUniqueID();
		ReflectionTestUtils.invokeMethod(idAuthService, "validateUIN", uniqueID);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
		
	}
	
	@Ignore
	@Test
	public void testGetRefIdForVID() {
		String uniqueID = otpRequestDto.getUniqueID();
		otpRequestDto.setIdType(IdType.VID);
		ReflectionTestUtils.invokeMethod(idAuthService, "validateVID", uniqueID);
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
		
	}
	
	/*@Test(expected=IdAuthenticationBusinessException.class)
	public void testGetRefIdForUINHandleBusineddException() throws IdAuthenticationBusinessException {
		IdAuthenticationBusinessException e = new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN);
		String uniqueID = otpRequestDto.getUniqueID();
		//Mockito.when(idAuthService.validateUIN(uniqueID)).thenThrow(e);
		ReflectionTestUtils.invokeMethod(idAuthService, "validateUIN", "");
		ReflectionTestUtils.invokeMethod(otpFacadeImpl, "getRefId", otpRequestDto);
		
	}*/
	
	// =========================================================
	// ************ Helping Method *****************************
	// =========================================================

	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setAsaLicenseKey("1234567890");
		otpRequestDto.setAuaCode("1234567890");
		otpRequestDto.setIdType(IdType.UIN);

		// otpRequestDto.setRequestTime(new Date(Long.valueOf("2018-09-24
		// 12:06:28.501")));
		otpRequestDto.setReqTime(new Date());
		otpRequestDto.setTxnID("1234567890");
		otpRequestDto.setUniqueID("1234567890");
		otpRequestDto.setVersion("1.0");

		return otpRequestDto;
	}

	private OtpResponseDTO getOtpResponseDTO() {
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		otpResponseDTO.setStatus("OTP_GENERATED");
		otpResponseDTO.setResponseTime(new Date());

		return otpResponseDTO;
	}
}
