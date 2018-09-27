/*package org.mosip.auth.service.impl.indauth.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mosip.auth.core.dto.indauth.AuthResponseInfo;
import org.mosip.auth.core.dto.indauth.AuthTypeDTO;
import org.mosip.auth.core.dto.indauth.IDType;
import org.mosip.auth.core.dto.indauth.OtpTriggerRequestDTO;
import org.mosip.auth.core.dto.indauth.OtpTriggerResponseDTO;
import org.mosip.auth.core.exception.IdAuthenticationAppException;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.exception.IdValidationFailedException;
import org.mosip.auth.core.spi.otpgen.service.OTPService;
import org.mosip.auth.service.dao.OtpRepository;
import org.mosip.auth.service.entity.OtpEntity;
import org.mosip.auth.service.exception.IDAuthenticationExceptionHandler;
import org.mosip.auth.service.impl.otpgen.controller.OTPController;
import org.mosip.auth.service.impl.otpgen.service.OTPServiceImpl;
import org.mosip.auth.service.impl.otpgen.validator.OTPRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;

import com.fasterxml.jackson.databind.ObjectMapper;

@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest(value = OTPController.class, secure = false)
public class AuthControllerTest {

	@Mock
	WebDataBinder binder;

	@Autowired
	private MockMvc mockMvc;

	@Mock
	OTPService otpservice;

	@Mock
	OTPRequestValidator otpvalidator;

	@Mock
	OTPController otpcontroller;

	@MockBean
	OTPServiceImpl otpserviceimpl;

	@MockBean
	OtpRepository otprepository;

	static Validator validator;
	private String Uniqueid = "0000000001";
	private String tempid = "TMP0000001";
	private String txnID = "TXN0000001";
	OtpTriggerRequestDTO otptriggerreqdto = new OtpTriggerRequestDTO();
	OtpTriggerResponseDTO otptriggerresdto = new OtpTriggerResponseDTO();
	Errors errors;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(OTPController.class).setValidator(otpvalidator)
				.setControllerAdvice(IDAuthenticationExceptionHandler.class).build();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	public void TestInvalidOtpTriggerReequestDto() {
		otptriggerreqdto = new OtpTriggerRequestDTO();
		otptriggerreqdto.setUniqueID("3432143214321412341324");
		

	}

	@Ignore
	@Test(expected = IdValidationFailedException.class)
	public void TestInValidTriggerOtp() throws IdValidationFailedException, IdAuthenticationAppException {
		Mockito.when(otpcontroller.triggerOTP(otptriggerreqdto, errors)).thenThrow(IdValidationFailedException.class);
		otpcontroller.triggerOTP(otptriggerreqdto, errors);
		assertEquals(true, errors.hasErrors());
	}

	@Ignore
	@Test
	public void TestValidTriggerOtp() throws IdValidationFailedException, IdAuthenticationAppException {
		otptriggerresdto = new OtpTriggerResponseDTO();
		otptriggerresdto.setErrorCode(null);
		AuthResponseInfo authresinfo = new AuthResponseInfo();
		authresinfo.setApiVersion("1.0");
		authresinfo.setMaskedEmailId("validmail@test.com");
		authresinfo.setRequestTimeStamp(new Date());
		authresinfo.setUid(IDType.UIN);
		otptriggerresdto.setInfo(authresinfo);
		otptriggerresdto.setStatus("Success");
		otptriggerresdto.setTxnID(txnID);
		otptriggerresdto.setResponseTime(new Date());
		Mockito.when(otpcontroller.triggerOTP(otptriggerreqdto, errors)).thenReturn(otptriggerresdto);
		OtpTriggerResponseDTO finalotptriggerresdto = otpcontroller.triggerOTP(otptriggerreqdto, errors);
		assertEquals("Success", finalotptriggerresdto.getStatus());
	}

	@Ignore
	@Test
	public void TestValidOTPService() throws IdAuthenticationBusinessException {
//		Mockito.when(otpservice.IsOtpTriggered(otptriggerreqdto)).thenReturn(true);
//		boolean triggerflag = otpservice.IsOtpTriggered(otptriggerreqdto);
//		assertEquals(true, triggerflag);
	}

	@Ignore
	@Test
	public void TestInvalidOTPTriggerRequest() {
		OtpTriggerRequestDTO otpauthreqdto = new OtpTriggerRequestDTO();
		otpauthreqdto.setUniqueID("232434242534321432");
		Set<ConstraintViolation<OtpTriggerRequestDTO>> violations = validator.validate(otpauthreqdto);
		assertTrue(!violations.isEmpty());
	}

	@Ignore
	@Test
	public void TestValidOTPTriggerRequest() throws Exception {
		OtpTriggerRequestDTO otpauthreqdto = new OtpTriggerRequestDTO();
		AuthTypeDTO authtypedto = new AuthTypeDTO();
		IDType idtype = IDType.UIN;
		otpauthreqdto.setAsaLicenseKey("TST0000001");
		otpauthreqdto.setAuaCode("AUA001");
//		otpauthreqdto.setIdType(idtype);
		authtypedto.setBioAuth(true);
		authtypedto.setOtpAuth(true);
		authtypedto.setPaAuth(true);
		authtypedto.setPiAuth(true);
		authtypedto.setPinAuth(true);
//		otpauthreqdto.setRequestedAuth(authtypedto);
		otpauthreqdto.setRequestTime(new Date());
		otpauthreqdto.setTxnID("000001");
		otpauthreqdto.setUniqueID("0000000001");
		otpauthreqdto.setVersion("1.0");
		ObjectMapper mapper = new ObjectMapper();
		String jsonvalue = mapper.writeValueAsString(otpauthreqdto);
		Mockito.when(otpservice.IsOtpTriggered(otpauthreqdto)).thenReturn(true);
	}

	@Ignore
	@Test
	public void TestUniqueIDNull() {
		Mockito.when(otprepository.findByUniqueID(Uniqueid)).thenReturn(null);
		OtpEntity tmpentity = otprepository.findByUniqueID(Uniqueid);
		assertEquals(null, tmpentity);
	}

	@Ignore
	@Test
	public void TestValidUniqueID() {
		OtpEntity entity = new OtpEntity();
		entity.setId(1);
		entity.setTempID(tempid);
		entity.setUniqueID(Uniqueid);
		Mockito.when(otprepository.findByUniqueID(Uniqueid)).thenReturn(entity);
		OtpEntity tmpentity = otprepository.findByUniqueID(Uniqueid);
		assertEquals(Uniqueid, tmpentity.getUniqueID());
	}

	@Ignore
	@Test
	public void TestInvalidUniqueID() {
		String invalidUniqueID = "INV0000001";
		OtpEntity entity = new OtpEntity();
		entity.setId(1);
		entity.setTempID(tempid);
		entity.setUniqueID(Uniqueid);
		Mockito.when(otprepository.findByUniqueID(Uniqueid)).thenReturn(entity);
		OtpEntity tmpentity = otprepository.findByUniqueID(Uniqueid);
		assertNotSame(invalidUniqueID, tmpentity.getUniqueID());
	}

	@Ignore
	@Test
	public void TestTempIDNull() {
		Mockito.when(otprepository.findByTempID(tempid)).thenReturn(null);
		OtpEntity tmpentity = otprepository.findByTempID(tempid);
		assertEquals(null, tmpentity);
	}

	@Ignore
	@Test
	public void TestTempIdInvalid() {
		String invalidTempid = "INV0000001";
		OtpEntity entity = new OtpEntity();
		entity.setId(1);
		entity.setTempID(tempid);
		entity.setUniqueID(Uniqueid);
		Mockito.when(otprepository.findByTempID(tempid)).thenReturn(entity);
		OtpEntity tmpentity = otprepository.findByTempID(tempid);
		assertNotSame(invalidTempid, tmpentity.getTempID());
	}

	@Ignore
	@Test
	public void TestValidTempId() {
		OtpEntity entity = new OtpEntity();
		entity.setId(1);
		entity.setTempID(tempid);
		entity.setUniqueID(Uniqueid);
		Mockito.when(otprepository.findByTempID(Uniqueid)).thenReturn(entity);
		OtpEntity tmpentity = otprepository.findByTempID(Uniqueid);
		assertEquals(tempid, tmpentity.getTempID());
	}

}
*/