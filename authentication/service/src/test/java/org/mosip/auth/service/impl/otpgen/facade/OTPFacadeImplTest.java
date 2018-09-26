package org.mosip.auth.service.impl.otpgen.facade;

import java.util.Date;

import org.assertj.core.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mosip.auth.core.dto.indauth.IDType;
import org.mosip.auth.core.dto.otpgen.OtpRequestDTO;
import org.mosip.auth.core.dto.otpgen.OtpResponseDTO;
import org.mosip.auth.core.spi.otpgen.service.OTPService;
import org.mosip.auth.service.dao.AutnTxnRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Test class for OTPFacadeImpl. Mockito with PowerMockito.
 *
 * @author Rakesh Roshan
 */

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({OTPFacadeImpl.class})
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-local.properties")
public class OTPFacadeImplTest {

	@Mock
	OtpRequestDTO otpRequestDto;
	@Mock
	OtpResponseDTO otpResponseDTO;
	@Mock
	OTPService otpService;
	@Mock
	Environment env;
	@Mock
	AutnTxnRepository autntxnrepository;
	@Mock
	Date date;
	//@Mock
	// DateUtil dateUtil ;

	@InjectMocks
	OTPFacadeImpl otpFacadeImpl;

	@Before
	public void before() {
		//otpRequestDto = getOtpRequestDTO();
		//DateUtil mock = mock(DateUtil.class);
	}

	@Test
	public void testIsOtpFlooded_False() {
		otpRequestDto = getOtpRequestDTO();
		String uniqueID = otpRequestDto.getUniqueID();
		Date requestTime = otpRequestDto.getRequestTime();
		Date addMinutesInOtpRequestDTime = new Date();
		//TODO Integrate with kernel DateUtil
				//DateUtil.addMinutes(requestTime, -1);

		// MockitoAnnotations.initMocks(this);

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
		otpRequestDto = getOtpRequestDTO();
		Date requestTime = otpRequestDto.getRequestTime();
		//ReflectionTestUtils.setField(otpFacadeImpl, "DateUtil", DateUtil.class);
		//ReflectionTestUtils.invokeMethod(DateUtil, "formateDate", requestTime,-1);
		//ReflectionTestUtils.invokeMethod(otpFacadeImpl, "formateDate", requestTime,-1);
	}

	// =========================================================
	// ************ Helping Method *****************************
	// =========================================================

	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setAsaLicenseKey("1234567890");
		otpRequestDto.setAuaCode("1234567890");
		otpRequestDto.setIdType(IDType.UIN);

		// otpRequestDto.setRequestTime(new Date(Long.valueOf("2018-09-24
		// 12:06:28.501")));
		otpRequestDto.setRequestTime(new Date());
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
