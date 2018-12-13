package io.mosip.authentication.service.impl.indauth.validator;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

@Ignore
//@RunWith(SpringRunner.class)
//@WebMvcTest
//@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class DemoValidatorTest {

//	@Autowired
//	Environment env;
//
//	@Mock
//	private SpringValidatorAdapter validator;
//
//	@Mock
//	Errors errors;
//
//	@Mock
//	AuthTypeDTO authTypeDTO;
//
//	@InjectMocks
//	DemoValidator demoValidator;
//
//	private AuthRequestDTO authRequestdto = new AuthRequestDTO();
//
//	@Before
//	public void before() {
//		ReflectionTestUtils.setField(demoValidator, "env", env);
//	}
//
//	@Test
//	public void testSupportTrue() {
//		assertTrue(demoValidator.supports(AuthRequestDTO.class));
//	}
//
//	@Test
//	public void testSupportFalse() {
//		assertFalse(demoValidator.supports(AuthRequestValidator.class));
//	}
//
//	// ========================= complete address validation =================
//
//	@Test
//	public void validtae_WhenFadAndAdIsTrueAndAllPIAttributeIsNull_ResultHasErrors() {
//		AuthRequestDTO authRequestdto = new AuthRequestDTO();
//		PersonalIdentityDataDTO personalDataDTO = new PersonalIdentityDataDTO();
//		AuthTypeDTO auth = new AuthTypeDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(authRequestdto, "setAuthType", auth);
//		ReflectionTestUtils.invokeMethod(authRequestdto, "setPii", personalDataDTO);
//		ReflectionTestUtils.invokeMethod(personalDataDTO, "setDemo", demodto);
//		ReflectionTestUtils.invokeMethod(auth, "setFad", true);
//		ReflectionTestUtils.invokeMethod(auth, "setAd", true);
//		ReflectionTestUtils.invokeMethod(demoValidator, "completeAddressValidation", auth, demodto, errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForAllNullAndDob", auth, demodto, errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForCommon", auth, demodto, errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForMatchStrategyAndValue", auth, demodto, errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "validate", authRequestdto, errors);
//
//		// Then
//		assertTrue(errors.hasErrors());
//	}
//
//	// ====================== Full Address Validation Test ===================
//	@Test
//	public void testFullAddressValidation_WhenFadIstrueAndAllAttributeIsNull_ResultHasErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalFullAddressDTO personalFullAddressDTO = new PersonalFullAddressDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setFad", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setFad", personalFullAddressDTO);
//		ReflectionTestUtils.invokeMethod(demoValidator, "fullAddressValidation", auth, demodto, errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "completeAddressValidation", auth, demodto, errors);
//
//		// Then
//		assertTrue(errors.hasErrors());
//	}
//
//	@Test
//	public void testFullAddressValidation_WhenFadIstrueAndAtLeastOneAttributeAvailable_ResultNoErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalFullAddressDTO personalFullAddressDTO = new PersonalFullAddressDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setFad", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setFad", personalFullAddressDTO);
//		ReflectionTestUtils.invokeMethod(personalFullAddressDTO, "setAddrPri", "mosip");
//		ReflectionTestUtils.invokeMethod(personalFullAddressDTO, "setAddrSec", "mosip");
//		ReflectionTestUtils.invokeMethod(demoValidator, "fullAddressValidation", auth, demodto, errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "completeAddressValidation", auth, demodto, errors);
//
//		// Then
//		assertFalse(errors.hasErrors());
//
//	}
//
//	// ====================== Address Validation Test ==================
//	@Test
//	public void testAddressValidation_WhenAdIsTrueAndAllAttributeIsNull_ResultHasErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setAd", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setAd", personalAddressDTO);
//		ReflectionTestUtils.invokeMethod(demoValidator, "addressValidation", demodto, errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "completeAddressValidation", auth, demodto, errors);
//
//		// Then
//		assertTrue(errors.hasErrors());
//
//	}
//
//	@Test
//	public void testAddressValidation_WhenAdIstrueAndAtLeastOneAttributeAvailable_ResultNoErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setAd", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setAd", personalAddressDTO);
//		ReflectionTestUtils.invokeMethod(personalAddressDTO, "setAddrLine1Pri", "mosip");
//		ReflectionTestUtils.invokeMethod(personalAddressDTO, "setAddrLine1Sec", "mosip");
//		ReflectionTestUtils.invokeMethod(demoValidator, "addressValidation", demodto, errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "completeAddressValidation", auth, demodto, errors);
//
//		// Then
//		assertFalse(errors.hasErrors());
//
//	}
//
//	// ====================== Personal Identity Validation =============
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndAllAttributeIsNull_ResultHasErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForAllNullAndDob", auth, demodto, errors);
//
//		// Then
//		assertTrue(errors.hasErrors());
//
//	}
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndOneAttributeIsAvailableAndWrongDateFormat_ResultHasError() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setNamePri", "mosip");
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setDob", "12/12/2012");
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForAllNullAndDob", auth, demodto, errors);
//
//		// Then
//		assertTrue(errors.hasErrors());
//	}
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndOneAttributeIsAvailableAndWithValidDateFormat_ResultHasNoError() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(personalIdentityDTO, "personalIdentityDTO");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setNamePri", "mosip");
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setDob", "2012-12-12");
//		ReflectionTestUtils.invokeMethod(demoValidator, "dobValidation", personalIdentityDTO.getDob(),
//				env.getProperty("date.pattern"), errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForAllNullAndDob", auth, demodto, errors);
//
//		// Then
//		assertFalse(errors.hasErrors());
//	}
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndOneAttributeIsAvailableAndWithFutureDate_ResultHasError() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(AuthRequestDTO.class, "authRequestDTO");
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setNamePri", "mosip");
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setDob", "3012-12-12");
//		ReflectionTestUtils.invokeMethod(demoValidator, "dobValidation", personalIdentityDTO.getDob(),
//				env.getProperty("date.pattern"), errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForAllNullAndDob", auth, demodto, errors);
//
//		// Then
//		assertTrue(errors.hasErrors());
//	}
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndOneAttributeIsAvailableAndAgeIsMoreThan150_ResultHasErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setAge", 155);
//		ReflectionTestUtils.invokeMethod(demoValidator, "checkAge", personalIdentityDTO.getAge(), errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForCommon", auth, demodto, errors);
//
//		// Then
//		assertTrue(errors.hasErrors());
//	}
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndOneAttributeIsAvailableAndAgeIsInBetween1To150_ResultHasNoErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setAge", 150);
//		ReflectionTestUtils.invokeMethod(demoValidator, "checkAge", personalIdentityDTO.getAge(), errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForCommon", auth, demodto, errors);
//
//		// Then
//		assertFalse(errors.hasErrors());
//	}
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndOneAttributeIsAvailableAndInvalidGenderInput_ResultHasErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setGender", "Q");
//		ReflectionTestUtils.invokeMethod(demoValidator, "checkGender", personalIdentityDTO.getGender(), errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForCommon", auth, demodto, errors);
//
//		// Then
//		assertTrue(errors.hasErrors());
//	}
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndOneAttributeIsAvailableAndValidGenderInput_ResultHasNoErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setGender", "T");
//		ReflectionTestUtils.invokeMethod(demoValidator, "checkGender", personalIdentityDTO.getGender(), errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForCommon", auth, demodto, errors);
//
//		// Then
//		assertFalse(errors.hasErrors());
//	}
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndOneAttributeIsAvailableAndPhoneNumberIsEmpty_ResultHasErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setNamePri", "mosip");
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setPhone", "");
//		ReflectionTestUtils.invokeMethod(demoValidator, "checkPhoneNumber", personalIdentityDTO.getPhone(), errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForCommon", auth, demodto, errors);
//
//		// Then
//		assertTrue(errors.hasErrors());
//	}
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndOneAttributeIsAvailableAndPhoneNumberIsInNumeric_ResultHasNoErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setNamePri", "mosip");
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setPhone", "1");
//		ReflectionTestUtils.invokeMethod(demoValidator, "checkPhoneNumber", personalIdentityDTO.getPhone(), errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForCommon", auth, demodto, errors);
//
//		// Then
//		assertFalse(errors.hasErrors());
//	}
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndOneAttributeIsAvailableAndValidEmails_ResultHasNoErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setNamePri", "mosip");
//
//		String[] validEmailProviders = ValidEmailProvider();
//		for (String email : validEmailProviders) {
//
//			ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setEmail", email);
//			ReflectionTestUtils.invokeMethod(demoValidator, "checkEmail", personalIdentityDTO.getEmail(), errors);
//			ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForCommon", auth, demodto, errors);
//			// Then
//			assertFalse(errors.hasErrors());
//		}
//
//	}
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndOneAttributeIsAvailableAndInvalidEmails_ResultHasErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setNamePri", "mosip");
//
//		String[] validEmailProviders = InvalidEmailProvider();
//		for (String email : validEmailProviders) {
//
//			ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setEmail", email);
//			ReflectionTestUtils.invokeMethod(demoValidator, "checkEmail", personalIdentityDTO.getEmail(), errors);
//			ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForCommon", auth, demodto, errors);
//			// Then
//			assertTrue(errors.hasErrors());
//		}
//	}
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndOneAttributeIsAvailableAndInvalidMatchStrategy_ResultHasErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setNamePri", "mosip");
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setMsPri", "A");
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setMsSec", "B");
//		ReflectionTestUtils.invokeMethod(demoValidator, "checkMatchStrategy", personalIdentityDTO.getMsPri(), "msPri",
//				errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "checkMatchStrategy", personalIdentityDTO.getMsSec(), "msSec",
//				errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForMatchStrategyAndValue", auth, demodto, errors);
//
//		// Then
//		assertTrue(errors.hasErrors());
//	}
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndOneAttributeIsAvailableAndValidMatchStrategy_ResultHasNoErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setNamePri", "mosip");
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setMsPri", "P");
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setMsSec", "PH");
//		ReflectionTestUtils.invokeMethod(demoValidator, "checkMatchStrategy", personalIdentityDTO.getMsPri(), "msPri",
//				errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "checkMatchStrategy", personalIdentityDTO.getMsSec(), "msSec",
//				errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForMatchStrategyAndValue", auth, demodto, errors);
//
//		// Then
//		assertFalse(errors.hasErrors());
//	}
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndOneAttributeIsAvailableAndInvalidMatchThresold_ResultHasErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setNamePri", "mosip");
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setMtPri", 101);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setMtSec", 0);
//		ReflectionTestUtils.invokeMethod(demoValidator, "checkMatchThresold", personalIdentityDTO.getMtPri(), "mtPri",
//				errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "checkMatchThresold", personalIdentityDTO.getMtSec(), "mtSec",
//				errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForMatchStrategyAndValue", auth, demodto, errors);
//
//		// Then
//		assertTrue(errors.hasErrors());
//	}
//
//	@Test
//	public void testPersonalIdentity_WhenPiIsTrueAndOneAttributeIsAvailableAndValidMatchThresold_ResultHasNoErrors() {
//
//		// Given
//		AuthTypeDTO auth = new AuthTypeDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		DemoDTO demodto = new DemoDTO();
//		Errors errors = new BeanPropertyBindingResult(authRequestdto, "authRequestdto");
//
//		// When
//		ReflectionTestUtils.invokeMethod(auth, "setPi", true);
//		ReflectionTestUtils.invokeMethod(demodto, "setPi", personalIdentityDTO);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setNamePri", "mosip");
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setMtPri", 100);
//		ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setMtSec", 1);
//		ReflectionTestUtils.invokeMethod(demoValidator, "checkMatchThresold", personalIdentityDTO.getMtPri(), "mtPri",
//				errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "checkMatchThresold", personalIdentityDTO.getMtSec(), "mtSec",
//				errors);
//		ReflectionTestUtils.invokeMethod(demoValidator, "piValidationForMatchStrategyAndValue", auth, demodto, errors);
//
//		// Then
//		assertFalse(errors.hasErrors());
//	}
//
//	// ============== Helper Method ========================
//	public String[] ValidEmailProvider() {
//		return new String[] { "_mosip@yahoo.com", "mosip-100@yahoo.com", "mosip.100@yahoo.com", "mosip111@abc.com",
//				"mosip-100@abc.net", "mosip.100@abc.com.au", "mosip@1.com", "mosip@gmail.com.com",
//				"mosip+100@gmail.com", "mosip-100@yahoo-test.com" };
//	}
//
//	public String[] InvalidEmailProvider() {
//		return new String[] { "mosip", "mosip@.com.my", "mosip123@gmail.a", "mosip123@.com", "mosip123@.com.com",
//				".mosip@mosip.com", "mosip()*@gmail.com", "mosip@%*.com", "mosip..2002@gmail.com", "mosip.@gmail.com",
//				"mosip@mosip@gmail.com", "mosip@gmail.com.1a" };
//	}

}
