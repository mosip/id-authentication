package io.mosip.authentication.service.impl.indauth.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.impl.indauth.validator.AuthRequestValidator;
import io.mosip.authentication.service.impl.otpgen.validator.OTPRequestValidator;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.datavalidator.email.impl.EmailValidatorImpl;
import io.mosip.kernel.datavalidator.phone.impl.PhoneValidatorImpl;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;

/**
 * This class validates the AuthRequestValidator
 * 
 * @author Arun Bose
 * @author Rakesh Roshan
 */

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class AuthRequestValidatorTest{

	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	Errors error;

	@Autowired
	Environment env;

	@InjectMocks
	DateHelper dateHelper;

	@Mock
	UinValidatorImpl uinValidator;

	@Mock
	VidValidatorImpl vidValidator;
	
	@Mock
	EmailValidatorImpl emailValidatorImpl;
	
	@Mock
	PhoneValidatorImpl phoneValidatorImpl;


	@InjectMocks
	RollingFileAppender idaRollingFileAppender;

	@InjectMocks
	private AuthRequestValidator authRequestValidator;

	@Before
	public void before() {
		ReflectionTestUtils.setField(authRequestValidator, "env", env);
		ReflectionTestUtils.setField(dateHelper, "env", env);
		ReflectionTestUtils.setField(authRequestValidator, "dateHelper", dateHelper);
		/*ReflectionTestUtils.setField(authRequestValidator, "emailValidatorImpl", emailValidatorImpl);
		ReflectionTestUtils.setField(authRequestValidator, "phoneValidatorImpl", phoneValidatorImpl);*/
	}

	@Test
	public void testSupportTrue() {
		assertTrue(authRequestValidator.supports(AuthRequestDTO.class));
	}

	@Test
	public void testSupportFalse() {
		assertFalse(authRequestValidator.supports(OTPRequestValidator.class));
	}

	@Test
	public void testValidUin() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
		authRequestDTO.setIdvId("234567890123");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidUin() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
		authRequestDTO.setIdvId("234567890123");
		authRequestDTO.setReqTime(Instant.now().toString());
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidVid() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		authRequestDTO.setIdvId("5371843613598206");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidVid() {
		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		authRequestDTO.setIdvId("5371843613598206");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidRequestDTO() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidTimestamp() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType("V");
		authRequestDTO.setReqTime("2001-07-04T12:08:56.235-0700");
		authRequestDTO.setIdvId("5371843613598211");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidVer() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType("D");
		authRequestDTO.setReqTime(Instant.now().toString());
		authRequestDTO.setIdvId("5371843613598211");
		authRequestDTO.setVer("1.12");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidMuaCode() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType("D");
		authRequestDTO.setReqTime(Instant.now().toString());
		authRequestDTO.setIdvId("5371843613598211");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidTxnId() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType("D");
		authRequestDTO.setReqTime(Instant.now().toString());
		authRequestDTO.setIdvId("5371843613598211");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setTxnID("");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testNullId() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType("D");
		authRequestDTO.setReqTime(Instant.now().toString());
		authRequestDTO.setIdvId(null);
		authRequestDTO.setVer("1.1");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testNullIdType() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType(null);
		authRequestDTO.setReqTime(Instant.now().toString());
		authRequestDTO.setIdvId("5371843613598211");
		authRequestDTO.setVer("1.1");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		authRequestDTO.setIdvId("5371843613598206");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDateOfBirth(idInfoList);
		idDTO.setDateOfBirthType(idInfoList);
		idDTO.setGender(idInfoList);
		idDTO.setAge(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidRequest() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		authRequestDTO.setIdvId("5371843613598206");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue("25");
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(null);
		idInfoDTO5.setValue("M");
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDateOfBirth(idInfoList1);
		idDTO.setDateOfBirthType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInValidRequest2() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		authRequestDTO.setIdvId("5371843613598206");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO6.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue("25");
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(null);
		idInfoDTO5.setValue("M");
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDateOfBirth(idInfoList1);
		idDTO.setDateOfBirthType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest3() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		authRequestDTO.setIdvId("5371843613598206");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(null);
		idInfoDTO6.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue("25");
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(null);
		idInfoDTO5.setValue("M");
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDateOfBirth(idInfoList1);
		idDTO.setDateOfBirthType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest4() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		authRequestDTO.setIdvId("5371843613598206");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO6.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue("25");
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(null);
		idInfoDTO5.setValue("M");
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDateOfBirth(null);
		idDTO.setDateOfBirthType(null);
		idDTO.setGender(null);
		idDTO.setAge(null);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest5() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		authRequestDTO.setIdvId("5371843613598206");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(null);
		idInfoDTO6.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue(null);
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO5.setValue(null);
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDateOfBirth(idInfoList1);
		idDTO.setDateOfBirthType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest6() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		authRequestDTO.setIdvId("5371843613598206");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(false);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(null);
		idInfoDTO6.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue(null);
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO5.setValue(null);
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDateOfBirth(idInfoList1);
		idDTO.setDateOfBirthType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest7() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		authRequestDTO.setIdvId("5371843613598206");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(null);
		idInfoDTO6.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue(null);
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO5.setValue(null);
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDateOfBirth(idInfoList1);
		idDTO.setDateOfBirthType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);

		authTypeDTO.setOtp(true);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setPinInfo(null);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest8() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		authRequestDTO.setIdvId("5371843613598206");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(null);
		idInfoDTO6.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue(null);
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO5.setValue(null);
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDateOfBirth(idInfoList1);
		idDTO.setDateOfBirthType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);

		authTypeDTO.setOtp(true);
		PinInfo pin = new PinInfo();
		pin.setType("OTP");
		pin.setValue("123456");
		List<PinInfo> pinInfo = new ArrayList<>();
		pinInfo.add(pin);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setPinInfo(pinInfo);
		authRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testValidRequest2() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		authRequestDTO.setIdvId("5371843613598206");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(null);
		idInfoDTO6.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue(null);
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO5.setValue(null);
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDateOfBirth(idInfoList1);
		idDTO.setDateOfBirthType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);

		authTypeDTO.setOtp(true);
		PinInfo pin = new PinInfo();
		List<PinInfo> pinInfo = new ArrayList<>();
		pinInfo.add(pin);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setPinInfo(pinInfo);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidRequest10() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		authRequestDTO.setIdvId("5371843613598206");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		// name
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue(null);
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		IdentityInfoDTO idInfoDTO6 = new IdentityInfoDTO();
		idInfoDTO6.setLanguage(null);
		idInfoDTO6.setValue(null);
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		idInfoList.add(idInfoDTO6);
		// dob
		IdentityInfoDTO idInfoDTO2 = new IdentityInfoDTO();
		idInfoDTO2.setLanguage(null);
		idInfoDTO2.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<>();
		idInfoList1.add(idInfoDTO2);
		// dobtype
		IdentityInfoDTO idInfoDTO4 = new IdentityInfoDTO();
		idInfoDTO4.setLanguage(null);
		idInfoDTO4.setValue("V");
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<>();
		idInfoList2.add(idInfoDTO4);
		// age
		IdentityInfoDTO idInfoDTO3 = new IdentityInfoDTO();
		idInfoDTO3.setLanguage(null);
		idInfoDTO3.setValue(null);
		List<IdentityInfoDTO> idInfoList3 = new ArrayList<>();
		idInfoList3.add(idInfoDTO3);
		// gender
		IdentityInfoDTO idInfoDTO5 = new IdentityInfoDTO();
		idInfoDTO5.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO5.setValue(null);
		List<IdentityInfoDTO> idInfoList4 = new ArrayList<>();
		idInfoList4.add(idInfoDTO5);

		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDateOfBirth(idInfoList1);
		idDTO.setDateOfBirthType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);

		authTypeDTO.setOtp(true);
		PinInfo pin = new PinInfo();
		pin.setType("OTP");
		pin.setType("");
		List<PinInfo> pinInfo = new ArrayList<>();
		pinInfo.add(pin);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setPinInfo(pinInfo);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}
	
	
	@Test
	public void testValidateEmail() {
		AuthRequestDTO authRequestDTO = getAuthRequestDto();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		
		RequestDTO request = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		
		List<IdentityInfoDTO>  emailId = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("FR");
		identityInfoDTO.setValue("sample@sample.com");
		emailId.add(identityInfoDTO);
		identity.setEmailId(emailId);
		
		identity.setEmailId(emailId);
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);
	
		Mockito.when(emailValidatorImpl.validateEmail(Mockito.anyString())).thenReturn(true);
		
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateEmail", authRequestDTO,errors);
	}
	
	@Test
	public void testValidatePhone() {
		AuthRequestDTO authRequestDTO = getAuthRequestDto();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		
		RequestDTO request = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		
		List<IdentityInfoDTO>  phone = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("FR");
		identityInfoDTO.setValue("76598749689");
		phone.add(identityInfoDTO);
		identity.setEmailId(phone);
		
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);
	
		Mockito.when(phoneValidatorImpl.validatePhone(Mockito.anyString())).thenReturn(true);
		
		ReflectionTestUtils.invokeMethod(authRequestValidator, "validatePhone", authRequestDTO,errors);
	}
	
	private AuthRequestDTO getAuthRequestDto() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		authRequestDTO.setIdvId("5371843613598206");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		
		
		return authRequestDTO;
	}
}
