package io.mosip.authentication.service.impl.indauth.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.DemoDTO;
import io.mosip.authentication.core.dto.indauth.PersonalAddressDTO;
import io.mosip.authentication.core.dto.indauth.PersonalFullAddressDTO;
import io.mosip.authentication.core.dto.indauth.PersonalIdentityDTO;
import io.mosip.authentication.core.dto.indauth.PersonalIdentityDataDTO;
import io.mosip.authentication.service.impl.indauth.validator.AuthRequestValidator;
import io.mosip.authentication.service.impl.indauth.validator.DemoValidator;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class DemoValidatorTest {

	@Autowired
	Environment env;

	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	Errors errors;

	@Mock
	AuthTypeDTO authTypeDTO;

	@InjectMocks
	DemoValidator demoValidator;

	@Before
	public void before() {
		MosipRollingFileAppender mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName(env.getProperty("log4j.appender.Appender"));
		mosipRollingFileAppender.setFileName(env.getProperty("log4j.appender.Appender.file"));
		mosipRollingFileAppender.setFileNamePattern(env.getProperty("log4j.appender.Appender.filePattern"));
		mosipRollingFileAppender.setMaxFileSize(env.getProperty("log4j.appender.Appender.maxFileSize"));
		mosipRollingFileAppender.setTotalCap(env.getProperty("log4j.appender.Appender.totalCap"));
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);
		ReflectionTestUtils.setField(demoValidator, "env", env);
		ReflectionTestUtils.invokeMethod(demoValidator, "initializeLogger", mosipRollingFileAppender);
	}

	
	@Test
	public void testSupportTrue() {
		assertTrue(demoValidator.supports(AuthRequestDTO.class));
	}
	
	@Test
	public void testSupportFalse() {
		assertFalse(demoValidator.supports(AuthRequestValidator.class));
	}
	
	@Test
	public void checkValidateMethod() {
		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		AuthTypeDTO auth = new AuthTypeDTO();
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();
		PersonalFullAddressDTO personalFullAddressDTO = new PersonalFullAddressDTO();
		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
		
		ReflectionTestUtils.invokeMethod(authRequestdto, "setAuthType", auth);
		ReflectionTestUtils.invokeMethod(auth, "setAd", true);
		ReflectionTestUtils.invokeMethod(auth, "setFad", false);
		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		String priLanguage = "en";
		String secLanguage = "ar";
		ReflectionTestUtils.invokeMethod(demodto, "setLangPri", priLanguage);
		ReflectionTestUtils.invokeMethod(demodto, "setLangSec", secLanguage);
		ReflectionTestUtils.invokeMethod(demodto, "setPersonalFullAddressDTO", personalFullAddressDTO);
		ReflectionTestUtils.invokeMethod(demodto, "setLangPri", priLanguage);
		ReflectionTestUtils.invokeMethod(demodto, "setLangSec", secLanguage);
		ReflectionTestUtils.invokeMethod(demodto, "setPersonalAddressDTO", personalAddressDTO);
		
		personalAddressDTO.setAddrLine1Pri("mosip");
		personalFullAddressDTO.setAddrPri("mosip");
		ReflectionTestUtils.invokeMethod(demoValidator, "completeAddressValidation", authRequestdto, errors);
		ReflectionTestUtils.invokeMethod(demoValidator, "personalIdentityValidation", authRequestdto, errors);
		
		ReflectionTestUtils.invokeMethod(demoValidator, "validate", authRequestdto, errors);
		
		//demoValidator.validate(authRequestdto, errors);
	}
	
	// ========================= complete address validation =================

	@Test
	public void testCompleteAddressValidation_WhenFadAndAdBothTrue_ShouldBeMutuallyExculive() {
		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		AuthTypeDTO auth = new AuthTypeDTO();

		ReflectionTestUtils.invokeMethod(authRequestdto, "setAuthType", auth);
		ReflectionTestUtils.invokeMethod(auth, "setAd", true);
		ReflectionTestUtils.invokeMethod(auth, "setFad", true);

		ReflectionTestUtils.invokeMethod(errors, "reject",
				IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST.getErrorCode(),
				IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST.getErrorMessage());

		ReflectionTestUtils.invokeMethod(demoValidator, "completeAddressValidation", authRequestdto, errors);
	}

	@Test
	public void testCompleteAddressValidation_WhenFadIsTrue_ValidateFullAddressOnly() {
		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		AuthTypeDTO auth = new AuthTypeDTO();
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();
		PersonalFullAddressDTO personalFullAddressDTO = new PersonalFullAddressDTO();
		String priLanguage = "en";
		String secLanguage = null;

		ReflectionTestUtils.invokeMethod(authRequestdto, "setAuthType", auth);
		ReflectionTestUtils.invokeMethod(auth, "setAd", false);
		ReflectionTestUtils.invokeMethod(auth, "setFad", true);
		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demodto, "setPersonalFullAddressDTO", personalFullAddressDTO);
		ReflectionTestUtils.invokeMethod(demodto, "setLangPri", priLanguage);
		ReflectionTestUtils.invokeMethod(demodto, "setLangSec", secLanguage);

		String allNullForPriLanguage = "mosip";
		ReflectionTestUtils.invokeMethod(personalFullAddressDTO, "setAddrPri", allNullForPriLanguage);
		
		ReflectionTestUtils.invokeMethod(demoValidator,"fullAddressValidation",authRequestdto, errors);

		ReflectionTestUtils.invokeMethod(demoValidator, "completeAddressValidation", authRequestdto, errors);
	}
	
	@Test
	public void testCompleteAddressValidation_WhenAdIsTrue_ValidateFullAddressOnly() {
		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		AuthTypeDTO auth = new AuthTypeDTO();
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();
		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
		String priLanguage = "en";
		String secLanguage = null;

		ReflectionTestUtils.invokeMethod(authRequestdto, "setAuthType", auth);
		ReflectionTestUtils.invokeMethod(auth, "setAd", true);

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demodto, "setPersonalAddressDTO", personalAddressDTO);
		ReflectionTestUtils.invokeMethod(demodto, "setLangPri", priLanguage);
		ReflectionTestUtils.invokeMethod(demodto, "setLangSec", secLanguage);

		String allNullForPriLanguage = "mosip";
		ReflectionTestUtils.invokeMethod(personalAddressDTO, "setAddrLine1Pri", allNullForPriLanguage);
		
		ReflectionTestUtils.invokeMethod(demoValidator,"addressValidation",authRequestdto, errors);

		ReflectionTestUtils.invokeMethod(demoValidator, "completeAddressValidation", authRequestdto, errors);
	}

	// ====================== Full Address Validation Test ===================
	@Ignore
	@Test
	public void testFullAddressValidation_WhenFadIstrue_AndPriAndSecLanguageIsNull() {

		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		AuthTypeDTO auth = new AuthTypeDTO();
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();
		String priLanguage = null;
		String secLanguage = null;

		ReflectionTestUtils.invokeMethod(authRequestdto, "setAuthType", auth);
		ReflectionTestUtils.invokeMethod(auth, "setFad", true);

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demodto, "setLangPri", priLanguage);
		ReflectionTestUtils.invokeMethod(demodto, "setLangSec", secLanguage);

		ReflectionTestUtils.invokeMethod(errors, "reject",
				IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST.getErrorCode(),
				IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST.getErrorMessage());

		ReflectionTestUtils.invokeMethod(demoValidator, "fullAddressValidation", authRequestdto, errors);

	}

	@Test
	public void testFullAddressValidation_WhenFadIstrue_PriIsNotNullAndAllOtherAttributeIsNull() {

		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		AuthTypeDTO auth = new AuthTypeDTO();
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();
		PersonalFullAddressDTO personalFullAddressDTO = new PersonalFullAddressDTO();
		String priLanguage = "en";
		String secLanguage = null;

		ReflectionTestUtils.invokeMethod(authRequestdto, "setAuthType", auth);
		ReflectionTestUtils.invokeMethod(auth, "setFad", true);

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demodto, "setPersonalFullAddressDTO", personalFullAddressDTO);
		ReflectionTestUtils.invokeMethod(demodto, "setLangPri", priLanguage);
		ReflectionTestUtils.invokeMethod(demodto, "setLangSec", secLanguage);

		String allNullForPriLanguage = null;
		ReflectionTestUtils.invokeMethod(personalFullAddressDTO, "setAddrPri", allNullForPriLanguage);
		ReflectionTestUtils.invokeMethod(personalFullAddressDTO, "setMsPri", allNullForPriLanguage);
		ReflectionTestUtils.invokeMethod(personalFullAddressDTO, "setMtPri", allNullForPriLanguage);

		ReflectionTestUtils.invokeMethod(errors, "reject",
				IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST_PRI.getErrorCode(),
				IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST_PRI.getErrorMessage());

		ReflectionTestUtils.invokeMethod(demoValidator, "fullAddressValidation", authRequestdto, errors);

	}

	@Test
	public void testFullAddressValidation_WhenFadIstrue_SecIsNotNullAndAllOtherAttributeIsNull() {

		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		AuthTypeDTO auth = new AuthTypeDTO();
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();
		PersonalFullAddressDTO personalFullAddressDTO = new PersonalFullAddressDTO();
		String priLanguage = null;
		String secLanguage = "en";

		ReflectionTestUtils.invokeMethod(authRequestdto, "setAuthType", auth);
		ReflectionTestUtils.invokeMethod(auth, "setFad", true);

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demodto, "setPersonalFullAddressDTO", personalFullAddressDTO);
		ReflectionTestUtils.invokeMethod(demodto, "setLangPri", priLanguage);
		ReflectionTestUtils.invokeMethod(demodto, "setLangSec", secLanguage);

		String allNullForSecLanguage = null;
		ReflectionTestUtils.invokeMethod(personalFullAddressDTO, "setAddrSec", allNullForSecLanguage);
		ReflectionTestUtils.invokeMethod(personalFullAddressDTO, "setMsSec", allNullForSecLanguage);
		ReflectionTestUtils.invokeMethod(personalFullAddressDTO, "setMtSec", allNullForSecLanguage);

		ReflectionTestUtils.invokeMethod(errors, "reject",
				IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST_SEC.getErrorCode(),
				IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST_SEC.getErrorMessage());

		ReflectionTestUtils.invokeMethod(demoValidator, "fullAddressValidation", authRequestdto, errors);

	}

	// ====================== Address Validation Test ==================
	@Ignore
	@Test
	public void testAddressValidation_WhenAdIsTrue_AndPriAndSecLanguageIsNull() {

		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		AuthTypeDTO auth = new AuthTypeDTO();
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();
		String priLanguage = null;
		String secLanguage = null;

		ReflectionTestUtils.invokeMethod(authRequestdto, "setAuthType", auth);
		ReflectionTestUtils.invokeMethod(auth, "setAd", true);

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demodto, "setLangPri", priLanguage);
		ReflectionTestUtils.invokeMethod(demodto, "setLangSec", secLanguage);

		ReflectionTestUtils.invokeMethod(errors, "reject",
				IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST.getErrorCode(),
				IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST.getErrorMessage());

		ReflectionTestUtils.invokeMethod(demoValidator, "addressValidation", authRequestdto, errors);

	}

	@Test
	public void testAddressValidation_WhenAdIstrue_PriIsNotNullAndAllOtherAttributeIsNull() {

		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		AuthTypeDTO auth = new AuthTypeDTO();
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();
		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
		String priLanguage = "en";
		String secLanguage = null;

		ReflectionTestUtils.invokeMethod(authRequestdto, "setAuthType", auth);
		ReflectionTestUtils.invokeMethod(auth, "setAd", true);

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demodto, "setPersonalAddressDTO", personalAddressDTO);
		ReflectionTestUtils.invokeMethod(demodto, "setLangPri", priLanguage);
		ReflectionTestUtils.invokeMethod(demodto, "setLangSec", secLanguage);

		String allNullForPriLanguage = null;
		ReflectionTestUtils.invokeMethod(personalAddressDTO, "setAddrLine1Pri", allNullForPriLanguage);
		ReflectionTestUtils.invokeMethod(personalAddressDTO, "setAddrLine2Pri", allNullForPriLanguage);
		ReflectionTestUtils.invokeMethod(personalAddressDTO, "setAddrLine3Pri", allNullForPriLanguage);
		ReflectionTestUtils.invokeMethod(personalAddressDTO, "setCountryPri", allNullForPriLanguage);
		ReflectionTestUtils.invokeMethod(personalAddressDTO, "setPinCodePri", allNullForPriLanguage);

		ReflectionTestUtils.invokeMethod(errors, "reject",
				IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST_PRI.getErrorCode(),
				IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST_PRI.getErrorMessage());

		ReflectionTestUtils.invokeMethod(demoValidator, "addressValidation", authRequestdto, errors);

	}

	@Test
	public void testAddressValidation_WhenAdIstrue_SecIsNotNullAndAllOtherAttributeIsNull() {

		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		AuthTypeDTO auth = new AuthTypeDTO();
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();
		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
		String priLanguage = null;
		String secLanguage = "en";

		ReflectionTestUtils.invokeMethod(authRequestdto, "setAuthType", auth);
		ReflectionTestUtils.invokeMethod(auth, "setAd", true);

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demodto, "setPersonalAddressDTO", personalAddressDTO);
		ReflectionTestUtils.invokeMethod(demodto, "setLangPri", priLanguage);
		ReflectionTestUtils.invokeMethod(demodto, "setLangSec", secLanguage);

		String allNullForSecLanguage = null;
		ReflectionTestUtils.invokeMethod(personalAddressDTO, "setAddrLine1Sec", allNullForSecLanguage);
		ReflectionTestUtils.invokeMethod(personalAddressDTO, "setAddrLine2Sec", allNullForSecLanguage);
		ReflectionTestUtils.invokeMethod(personalAddressDTO, "setAddrLine3Sec", allNullForSecLanguage);
		ReflectionTestUtils.invokeMethod(personalAddressDTO, "setCountrySec", allNullForSecLanguage);
		ReflectionTestUtils.invokeMethod(personalAddressDTO, "setPinCodeSec", allNullForSecLanguage);

		ReflectionTestUtils.invokeMethod(errors, "reject",
				IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST_SEC.getErrorCode(),
				IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST_SEC.getErrorMessage());

		ReflectionTestUtils.invokeMethod(demoValidator, "addressValidation", authRequestdto, errors);

	}

	// ====================== Personal Identity Validation =============

	@Test
	public void testPersonalIdentity_WhenPiIsTrue_ValidDOBFormate() {
		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		AuthTypeDTO auth = new AuthTypeDTO();
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();
		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();

		String dob = "2017-10-26";
		ReflectionTestUtils.invokeMethod(authRequestdto, "setAuthType", auth);
		ReflectionTestUtils.invokeMethod(auth, "setPi", true);

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demodto, "setPersonalIdentityDTO", personalIdentityDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setDob", dob);

		ReflectionTestUtils.invokeMethod(demoValidator, "dobValidation", authRequestdto, errors);

		ReflectionTestUtils.invokeMethod(demoValidator, "personalIdentityValidation", authRequestdto, errors);
	}

	// @Test(expected =ParseException.class)
	@Test // TODO for handle exception
	public void testPersonalIdentity_WhenPiIsTrue_AllPiAttributesIsNull() {
		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		AuthTypeDTO auth = new AuthTypeDTO();
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();
		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();

		ReflectionTestUtils.invokeMethod(authRequestdto, "setAuthType", auth);
		ReflectionTestUtils.invokeMethod(auth, "setPi", true);

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demodto, "setPersonalIdentityDTO", personalIdentityDTO);

		ReflectionTestUtils.invokeMethod(demoValidator, "isAllPINull", personalIdentityDTO);
		ReflectionTestUtils.invokeMethod(errors, "reject",
				IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION.getErrorCode(),
				IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION.getErrorMessage());

		ReflectionTestUtils.invokeMethod(demoValidator, "personalIdentityValidation", authRequestdto, errors);
	}

	@Test
	public void testersonalIdentity_WhenPiIsTrue_PriLanguageIsNullForPrimaryName() {
		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		AuthTypeDTO auth = new AuthTypeDTO();
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();
		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
		String primaryLanguage = null;
		String namePri = "mosip";

		ReflectionTestUtils.invokeMethod(authRequestdto, "setAuthType", auth);
		ReflectionTestUtils.invokeMethod(auth, "setPi", true);

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demodto, "setPersonalIdentityDTO", personalIdentityDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setNamePri", namePri);
		ReflectionTestUtils.invokeMethod(demodto, "setLangPri", primaryLanguage);

		ReflectionTestUtils.invokeMethod(errors, "reject",
				IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION_PRI.getErrorCode(),
				IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION_PRI.getErrorMessage());

		ReflectionTestUtils.invokeMethod(demoValidator, "personalIdentityValidation", authRequestdto, errors);
	}

	@Test
	public void testersonalIdentity_WhenPiIsTrue_SecLanguageIsNullForSecondaryName() {
		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		AuthTypeDTO auth = new AuthTypeDTO();
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();
		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
		String secondaryLanguage = null;
		String nameSec = "mosip";

		ReflectionTestUtils.invokeMethod(authRequestdto, "setAuthType", auth);
		ReflectionTestUtils.invokeMethod(auth, "setPi", true);

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demodto, "setPersonalIdentityDTO", personalIdentityDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setNameSec", nameSec);
		ReflectionTestUtils.invokeMethod(demodto, "setLangSec", secondaryLanguage);

		ReflectionTestUtils.invokeMethod(errors, "reject",
				IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION_SEC.getErrorCode(),
				IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION_SEC.getErrorMessage());

		ReflectionTestUtils.invokeMethod(demoValidator, "personalIdentityValidation", authRequestdto, errors);
	}

	// =================== DOB Validation Test =========================

	@Test
	public void testDOB() throws java.text.ParseException {

		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();
		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
		String setDob = "2019-02-26";

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demodto, "setPersonalIdentityDTO", personalIdentityDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setDob", setDob);

		SimpleDateFormat formatter = new SimpleDateFormat(env.getProperty("dob.date.time.pattern"));
		Date dob = formatter.parse(setDob);
		Instant instantDob = dob.toInstant();

		Instant now = Instant.now();

		ReflectionTestUtils.invokeMethod(instantDob, "isAfter", now);
		ReflectionTestUtils.invokeMethod(errors, "reject",
				IdAuthenticationErrorConstants.INVALID_DOB_YEAR.getErrorCode(),
				IdAuthenticationErrorConstants.INVALID_DOB_YEAR.getErrorMessage());

		ReflectionTestUtils.invokeMethod(demoValidator, "dobValidation", authRequestdto, errors);
	}

	// ==================== validate Language code ===========================
	@Test
	public void testPrimaryLanguageWithInvalidCode() {
		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demoValidator, "checkValidPrimaryLanguageCode", "english", errors);

		errors.getFieldErrors();
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testPrimaryLanguageWithValidCode() {
		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demoValidator, "checkValidPrimaryLanguageCode", "en", errors);

		errors.getFieldErrors();
		assertFalse(errors.hasErrors());
	}
	
	
	@Test
	public void testSecondaryLanguageWithInvalidCode() {
		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demoValidator, "checkValidSecondaryLanguageCode", "english", errors);

		errors.getFieldErrors();
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testSecondaryLanguageWithValidCode() {
		AuthRequestDTO authRequestdto = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
		PersonalIdentityDataDTO personalIdentityDataDTO = new PersonalIdentityDataDTO();
		DemoDTO demodto = new DemoDTO();

		ReflectionTestUtils.invokeMethod(authRequestdto, "setPersonalDataDTO", personalIdentityDataDTO);
		ReflectionTestUtils.invokeMethod(personalIdentityDataDTO, "setDemoDTO", demodto);
		ReflectionTestUtils.invokeMethod(demoValidator, "checkValidSecondaryLanguageCode", "en", errors);

		errors.getFieldErrors();
		assertFalse(errors.hasErrors());
	}

}
