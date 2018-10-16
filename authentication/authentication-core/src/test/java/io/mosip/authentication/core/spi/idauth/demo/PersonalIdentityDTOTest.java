package io.mosip.authentication.core.spi.idauth.demo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.core.dto.indauth.PersonalIdentityDTO;

/**
 * personal identity info test
 *
 * @author Rakesh Roshan
 */

@Ignore
@RunWith(SpringRunner.class)
public class PersonalIdentityDTOTest {

	private PersonalIdentityDTO personalIdentityDTO;

	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Before
	public void initData() {
		personalIdentityDTO = new PersonalIdentityDTO();
	}

	@Test
	public void testValidEmials() {

		personalIdentityDTO.setMtPri(1);
		personalIdentityDTO.setMtSec(1);
		personalIdentityDTO.setAge(1);

		Set<ConstraintViolation<PersonalIdentityDTO>> violations;

		String[] validEmailProviders = ValidEmailProvider();
		for (String email : validEmailProviders) {
			ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setEmail", email);
			violations = validator.validate(personalIdentityDTO);
			assertTrue(violations.isEmpty());
		}

	}

	@Test
	public void testInvalidEmials() {

		personalIdentityDTO.setMtPri(1);
		personalIdentityDTO.setMtSec(1);
		personalIdentityDTO.setAge(1);

		Set<ConstraintViolation<PersonalIdentityDTO>> violations;

		String[] validEmailProviders = InvalidEmailProvider();
		for (String email : validEmailProviders) {
			ReflectionTestUtils.invokeMethod(personalIdentityDTO, "setEmail", email);
			violations = validator.validate(personalIdentityDTO);
			assertFalse(violations.isEmpty());
		}

	}

	@Test
	public void testInvalidAgeExceedMax() {
		personalIdentityDTO.setMtPri(1);
		personalIdentityDTO.setMtSec(1);
		personalIdentityDTO.setAge(151);
		Set<ConstraintViolation<PersonalIdentityDTO>> violations = validator.validate(personalIdentityDTO);
		assertFalse(violations.isEmpty());
	}

	@Test
	public void testInvalidAgeLowerThanMin() {
		personalIdentityDTO.setMtPri(1);
		personalIdentityDTO.setMtSec(1);
		personalIdentityDTO.setAge(0);
		Set<ConstraintViolation<PersonalIdentityDTO>> violations = validator.validate(personalIdentityDTO);
		assertFalse(violations.isEmpty());
	}

	@Test
	public void testInvalidMatchingThresholdPri() {
		personalIdentityDTO.setMtPri(10);
		personalIdentityDTO.setMtSec(1);
		personalIdentityDTO.setAge(18);
		Set<ConstraintViolation<PersonalIdentityDTO>> violations = validator.validate(personalIdentityDTO);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testInvalidMatchingThresholdSec() {
		personalIdentityDTO.setMtPri(10);
		personalIdentityDTO.setMtSec(100);
		personalIdentityDTO.setAge(15);
		Set<ConstraintViolation<PersonalIdentityDTO>> violations = validator.validate(personalIdentityDTO);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testValidMatchingStrategyLowerPriThanMin() {
		personalIdentityDTO.setMtPri(10);
		personalIdentityDTO.setMtSec(10);
		personalIdentityDTO.setAge(18);
		personalIdentityDTO.setMsPri("E");
		Set<ConstraintViolation<PersonalIdentityDTO>> violations = validator.validate(personalIdentityDTO);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testInvalidMatchingStrategyLowerPriThanMin() {
		personalIdentityDTO.setMtPri(10);
		personalIdentityDTO.setMtSec(10);
		personalIdentityDTO.setAge(18);
		personalIdentityDTO.setMsPri("H");
		Set<ConstraintViolation<PersonalIdentityDTO>> violations = validator.validate(personalIdentityDTO);
		assertFalse(violations.isEmpty());
	}

	@Test
	public void testDob_InvalidFormat() {
		personalIdentityDTO.setMtPri(10);
		personalIdentityDTO.setMtSec(10);
		personalIdentityDTO.setAge(18);
		personalIdentityDTO.setMsPri("E");
		personalIdentityDTO.setDob("9-24-2017");  // Valid format "yyyy-MM-dd"
		Set<ConstraintViolation<PersonalIdentityDTO>> violations = validator.validate(personalIdentityDTO);
		assertFalse(violations.isEmpty());
	}
	
	@Test
	public void testDob_ValidFormat() {
		personalIdentityDTO.setMtPri(10);
		personalIdentityDTO.setMtSec(10);
		personalIdentityDTO.setAge(18);
		personalIdentityDTO.setMsPri("E");
		personalIdentityDTO.setDob("2017-11-25");  // Valid format "yyyy-MM-dd"
		Set<ConstraintViolation<PersonalIdentityDTO>> violations = validator.validate(personalIdentityDTO);
		assertTrue(violations.isEmpty());
	}
	
	
	@Test
	public void testDob_ValidFormat_ButNoOfDayExceed() {
		personalIdentityDTO.setMtPri(10);
		personalIdentityDTO.setMtSec(10);
		personalIdentityDTO.setAge(18);
		personalIdentityDTO.setMsPri("E");
		personalIdentityDTO.setDob("2017-11-35");  // Valid format "yyyy-MM-dd"
		Set<ConstraintViolation<PersonalIdentityDTO>> violations = validator.validate(personalIdentityDTO);
		assertFalse(violations.isEmpty());
	}
	
	@Test
	public void testDob_ValidFormat_ButNoOfMonthExceed() {
		personalIdentityDTO.setMtPri(10);
		personalIdentityDTO.setMtSec(10);
		personalIdentityDTO.setAge(18);
		personalIdentityDTO.setMsPri("E");
		personalIdentityDTO.setDob("2017-13-25");  // Valid format "yyyy-MM-dd"
		Set<ConstraintViolation<PersonalIdentityDTO>> violations = validator.validate(personalIdentityDTO);
		assertFalse(violations.isEmpty());
	}
	
	public String[] ValidEmailProvider() {
		return new String[] { "mosip@yahoo.com", "mosip-100@yahoo.com", "mosip.100@yahoo.com", "mosip111@abc.com",
				"mosip-100@abc.net", "mosip.100@abc.com.au", "mosip@1.com", "mosip@gmail.com.com",
				"mosip+100@gmail.com", "mosip-100@yahoo-test.com" };
	}

	public String[] InvalidEmailProvider() {
		return new String[] { "mosip", "mosip@.com.my", "mosip123@gmail.a", "mosip123@.com", "mosip123@.com.com",
				".mosip@mosip.com", "mosip()*@gmail.com", "mosip@%*.com", "mosip..2002@gmail.com", "mosip.@gmail.com",
				"mosip@mosip@gmail.com", "mosip@gmail.com.1a" };
	}
}
