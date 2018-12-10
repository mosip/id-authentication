
/**
 * 
 */
package io.mosip.kernel.datavalidator.test.phone;

/**
 * 
 */
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.datavalidator.exception.InvalidPhoneNumberException;
import io.mosip.kernel.datavalidator.phone.impl.PhoneValidatorImpl;
import io.mosip.kernel.core.datavalidator.spi.PhoneValidator;

/**
 * Test class for testing PhoneValidator class
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PhoneValidatorImplTest {

	@Autowired
	PhoneValidator phonevalidatorImpl;
	
	@Value("${mosip.kernel.phone.test.null}")
	String phoneNull;

	@Value("${mosip.kernel.phone.test.true}")
	String phoneTrue;

	@Value("${mosip.kernel.phone.test.empty}")
	String phoneEmpty;

	@Value("${mosip.kernel.phone.test.max-length}")
	String phoneMaxLength;

	@Value("${mosip.kernel.phone.test.min-length}")
	String phoneMinLength;

	@Value("${mosip.kernel.phone.test.alphanumaric}")
	String phoneAlpanumaric;

	@Value("${mosip.kernel.phone.test.specialchar}")
	String phoneSpecialChar;

	@Test
	public void phoneValidatorTestTrue() {
		assertEquals(true, phonevalidatorImpl.validatePhone(phoneTrue));
	}

	@Test(expected = InvalidPhoneNumberException.class)
	public void testNull() throws InvalidPhoneNumberException {

		phonevalidatorImpl.validatePhone(phoneNull);
	}

	@Test(expected = InvalidPhoneNumberException.class)
	public void testEmpty() throws InvalidPhoneNumberException {

		phonevalidatorImpl.validatePhone(phoneEmpty);
	}

	@Test(expected = InvalidPhoneNumberException.class)
	public void testMaxLenght() throws InvalidPhoneNumberException {

		phonevalidatorImpl.validatePhone(phoneMaxLength);
	}

	@Test(expected = InvalidPhoneNumberException.class)
	public void testMinLenght() throws InvalidPhoneNumberException {

		phonevalidatorImpl.validatePhone(phoneMinLength);
	}

	@Test(expected = InvalidPhoneNumberException.class)
	public void testAlphanumeric() throws InvalidPhoneNumberException {

		phonevalidatorImpl.validatePhone(phoneAlpanumaric);
	}

	@Test(expected = InvalidPhoneNumberException.class)
	public void testSpecialChar() throws InvalidPhoneNumberException {

		phonevalidatorImpl.validatePhone(phoneSpecialChar);
	}

}
