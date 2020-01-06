
/**
 * 
 */
package io.mosip.kernel.pinvalidator.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.pinvalidator.exception.InvalidPinException;
import io.mosip.kernel.core.pinvalidator.spi.PinValidator;

/**
 * Test class for PinValidatorImpl class
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PinValidatorTest {

	@Autowired
	private PinValidator<String> pinValidatorImpl;

	@Value("${mosip.kernel.pin.test.valid-pin}")
	private String validPin;

	@Value("${mosip.kernel.pin.test.invalid-length-pin}")
	private String invalidPinLength;

	@Value("${mosip.kernel.pin.test.invalid-alphanumric-pin}")
	private String invalidAlphaNumricPin;

	@Test
	public void pinValidatorImplTest() {

		assertEquals(true, pinValidatorImpl.validatePin(validPin));
	}

	@Test(expected = InvalidPinException.class)
	public void testNull() throws InvalidPinException {
		String pin = null;
		pinValidatorImpl.validatePin(pin);
	}

	@Test(expected = InvalidPinException.class)
	public void testEmpty() throws InvalidPinException {
		String pin = "";
		pinValidatorImpl.validatePin(pin);
	}

	@Test(expected = InvalidPinException.class)
	public void testPinLenth() throws InvalidPinException {
		pinValidatorImpl.validatePin(invalidPinLength);
	}

	@Test(expected = InvalidPinException.class)
	public void testPinAlphaNumric() throws InvalidPinException {
		pinValidatorImpl.validatePin(invalidAlphaNumricPin);
	}

}
