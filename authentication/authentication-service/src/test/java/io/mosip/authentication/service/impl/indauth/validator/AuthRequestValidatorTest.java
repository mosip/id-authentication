package io.mosip.authentication.service.impl.indauth.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.dto.indauth.AdditionalFactorsDTO;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.otpgen.validator.OTPRequestValidator;
import io.mosip.authentication.service.integration.MasterDataManager;
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
public class AuthRequestValidatorTest {

	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	Errors error;

	@Autowired
	Environment env;

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

	@InjectMocks
	IdInfoHelper idinfoHelper;
	
	@Mock
	private MasterDataManager masterDataManager;

	@Before
	public void before() {
		ReflectionTestUtils.setField(authRequestValidator, "env", env);
		ReflectionTestUtils.setField(authRequestValidator, "emailValidatorImpl", emailValidatorImpl);
		ReflectionTestUtils.setField(authRequestValidator, "phoneValidatorImpl", phoneValidatorImpl);
		ReflectionTestUtils.setField(authRequestValidator, "idInfoHelper", idinfoHelper);
		ReflectionTestUtils.setField(idinfoHelper, "environment", env);

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
		
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		 authRequestDTO.setVersion("1.1");
		authRequestDTO.setPartnerID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
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
		idDTO.setUin("234567890123");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setTransactionID("1234567890");
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		System.err.println(errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidUin() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		 authRequestDTO.setVersion("1.1");
		authRequestDTO.setPartnerID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
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
		idDTO.setUin("234567890123");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidVid() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		 authRequestDTO.setVersion("1.1");
		authRequestDTO.setPartnerID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
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
		idDTO.setUin("234567890123");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidVid() {
		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		 authRequestDTO.setVersion("1.1");
		authRequestDTO.setPartnerID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
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
		idDTO.setVid("234567890123");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
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
		authRequestDTO.setRequestTime("2001-07-04T12:08:56.235-0700");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidTimestamp2() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setRequestTime(Instant.now().plus(2, ChronoUnit.DAYS).atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidTimestamp3() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ReflectionTestUtils
				.invokeMethod(authRequestValidator, "validateRequestTimedOut",
						Instant.now().minus(2, ChronoUnit.DAYS).atOffset(ZoneOffset.of("+0530"))
								.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString(),
						errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidVer() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setRequestTime(Instant.now().toString());
		 authRequestDTO.setVersion("1.12");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	
	@Test
	public void testInvalidTxnId() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setRequestTime(Instant.now().toString());
		 authRequestDTO.setVersion("1.1");
		authRequestDTO.setTransactionID("");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testNullId() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		 authRequestDTO.setVersion("1.1");
		authRequestDTO.setPartnerID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
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
		idDTO.setUin(null);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	
	@Test
	public void testInValidRequest() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		 authRequestDTO.setVersion("1.1");
		authRequestDTO.setPartnerID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
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
		idDTO.setVid("234567890123");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidRequest() throws IdAuthenticationBusinessException {
		Mockito.when(masterDataManager.fetchGenderType()).thenReturn(fetchGenderType());
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setPartnerID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
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
				.format(DateTimeFormatter.ofPattern(env.getProperty("dob.req.date.pattern"))).toString());
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
		idDTO.setDob(idInfoList1);
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);
		idDTO.setUin("5371843613598206");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInValidRequest2() throws IdAuthenticationBusinessException {
		Mockito.when(masterDataManager.fetchGenderType()).thenReturn(fetchGenderType());
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setPartnerID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
//		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
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
				.format(DateTimeFormatter.ofPattern(env.getProperty("dob.req.date.pattern"))).toString());
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
		idDTO.setDob(idInfoList1);
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);
		idDTO.setUin("5371843613598206");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	private Map<String, List<String>> fetchGenderType() {
		Map<String, List<String>> map = new HashMap<>();
		List<String> list = new ArrayList<>();
		list.add("M");
		map.put(env.getProperty("mosip.primary.lang-code"), list);
		return map;
	}

	@Test
	public void testInValidRequest3() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setPartnerID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("dob.req.date.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
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
		idDTO.setDob(idInfoList1);
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);
		idDTO.setUin("5371843613598206");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest4() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setPartnerID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
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
		idDTO.setDob(null);
		idDTO.setDobType(null);
		idDTO.setGender(null);
		idDTO.setAge(null);
		idDTO.setUin("5371843613598206");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest5() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setPartnerID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("dob.req.date.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
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
		idDTO.setDob(idInfoList1);
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);
		idDTO.setUin("5371843613598206");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest6() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setPartnerID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(false);
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
		idDTO.setDob(idInfoList1);
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);
		idDTO.setUin("5371843613598206");
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest7() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setPartnerID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
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
		idDTO.setDob(idInfoList1);
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);
		idDTO.setUin("5371843613598206");
		authTypeDTO.setOtp(true);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInValidRequest8() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setPartnerID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
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
		idDTO.setDob(idInfoList1);
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);
		idDTO.setUin("5371843613598206");
		RequestDTO reqDTO = new RequestDTO();
		String pin="123456";
		authTypeDTO.setOtp(true);
		AdditionalFactorsDTO additionalFactors=new AdditionalFactorsDTO();
		additionalFactors.setTotp(pin);
		reqDTO.setAdditionalFactors(additionalFactors);
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testValidRequest2() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setPartnerID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
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
		idDTO.setDob(idInfoList1);
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);
		idDTO.setUin("5371843613598206");
		authTypeDTO.setOtp(true);
		String pin="123456";
		authTypeDTO.setOtp(true);
		RequestDTO reqDTO = new RequestDTO();
		AdditionalFactorsDTO additionalFactors=new AdditionalFactorsDTO();
		additionalFactors.setTotp(pin);
		reqDTO.setAdditionalFactors(additionalFactors);
		reqDTO.setIdentity(idDTO);
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidRequest10() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setPartnerID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
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
		idDTO.setDob(idInfoList1);
		idDTO.setDobType(idInfoList2);
		idDTO.setGender(idInfoList4);
		idDTO.setAge(idInfoList3);

		authTypeDTO.setOtp(true);
		RequestDTO reqDTO = new RequestDTO();
		String otp="";
		authTypeDTO.setOtp(true);
		AdditionalFactorsDTO additionalFactors=new AdditionalFactorsDTO();
		additionalFactors.setTotp(otp);
		reqDTO.setAdditionalFactors(additionalFactors);
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidateEmail() {
		AuthRequestDTO authRequestDTO = getAuthRequestDto();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");

		RequestDTO request = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();

		List<IdentityInfoDTO> emailId = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("fre");
		identityInfoDTO.setValue("sample@sample.com");
		emailId.add(identityInfoDTO);
		identity.setEmailId(emailId);

		identity.setEmailId(emailId);
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);

		Mockito.when(emailValidatorImpl.validateEmail(Mockito.anyString())).thenReturn(true);

		ReflectionTestUtils.invokeMethod(authRequestValidator, "validateEmail", authRequestDTO, errors);
	}

	@Test
	public void testValidatePhone() {
		AuthRequestDTO authRequestDTO = getAuthRequestDto();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");

		RequestDTO request = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();

		List<IdentityInfoDTO> phone = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("fre");
		identityInfoDTO.setValue("76598749689");
		phone.add(identityInfoDTO);
		identity.setEmailId(phone);

		request.setIdentity(identity);
		authRequestDTO.setRequest(request);

		Mockito.when(phoneValidatorImpl.validatePhone(Mockito.anyString())).thenReturn(true);

		ReflectionTestUtils.invokeMethod(authRequestValidator, "validatePhone", authRequestDTO, errors);
	}

	// ----------- Supporting method ---------------

	private AuthRequestDTO getAuthRequestDto() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setPartnerID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());

		return authRequestDTO;
	}

	@Test
	public void testInValidAuthType() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		// authRequestDTO.setVer("1.1");
		authRequestDTO.setPartnerID("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
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
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}
	
	@Test
	public void testNullAuthType() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method declaredMethod = AuthRequestValidator.class.getDeclaredMethod("checkAuthRequest", AuthRequestDTO.class, Errors.class);
		declaredMethod.setAccessible(true);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		declaredMethod.invoke(authRequestValidator, authRequestDTO, errors);
	}
	
	@Test
	public void testNullAuthType2() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method declaredMethod = AuthRequestValidator.class.getDeclaredMethod("checkAuthRequest", AuthRequestDTO.class, Errors.class);
		declaredMethod.setAccessible(true);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setOtp(false);
		authType.setDemo(false);
		authType.setPin(false);
		authRequestDTO.setRequestedAuth(authType);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		declaredMethod.invoke(authRequestValidator, authRequestDTO, errors);
	}
	
	@Test
	public void testInvalidTimeStamp() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method declaredMethod = AuthRequestValidator.class.getDeclaredMethod("validateRequestTimedOut", String.class, Errors.class);
		declaredMethod.setAccessible(true);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		declaredMethod.invoke(authRequestValidator, "2019-01-28", errors);
	}
}
