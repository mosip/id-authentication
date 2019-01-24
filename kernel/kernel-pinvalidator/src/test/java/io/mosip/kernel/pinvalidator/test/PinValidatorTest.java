
/**
 * 
 */
package io.mosip.kernel.pinvalidator.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Test
	public void pinValidatorImplTest() {

		String pin = "426789";
		assertEquals(true, pinValidatorImpl.validatePin(pin));
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

		String pin = "126789089018";
		pinValidatorImpl.validatePin(pin);
	}

	@Test(expected = InvalidPinException.class)
	public void testPinAlphaNumric() throws InvalidPinException {

		String pin = "02ABC6";
		pinValidatorImpl.validatePin(pin);
	}

}
