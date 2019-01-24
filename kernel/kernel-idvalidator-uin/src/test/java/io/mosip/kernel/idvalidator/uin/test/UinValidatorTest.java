
/**
 * 
 */
package io.mosip.kernel.idvalidator.uin.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;

/**
 * Test class for uinValidatorImpl class
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UinValidatorTest {

	@Autowired
	private UinValidator<String> uinValidatorImpl;

	@Test
	public void uinValidatorImplTest() {

		String id = "2013023805";
		assertEquals(true, uinValidatorImpl.validateId(id));
	}

	@Test(expected = InvalidIDException.class)
	public void testNull() throws InvalidIDException {

		String id = null;
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testEmpty() throws InvalidIDException {

		String id = "";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testFistDigitOne() throws InvalidIDException {

		String id = "1013023805";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testFistDigitZero() throws InvalidIDException {

		String id = "0013023805";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testUinLenght() throws InvalidIDException {

		String id = "201302380";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testUinOverLenght() throws InvalidIDException {

		String id = "20130238051";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testUinCkeckSum() throws InvalidIDException {

		String id = "2013023800";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testRepeated() throws InvalidIDException {

		String id = "2013013805";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testSequential() throws InvalidIDException {

		String id = "2013223805";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testAlphanumeric() throws InvalidIDException {

		String id = "2A13023805";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testFirstAndLastDigitsValidation() throws InvalidIDException {

		String id = "4345643456";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testFirstAndLastDigitsReverseValidation() throws InvalidIDException {

		String id = "4345665434";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testAscendingCyclicFigure() throws InvalidIDException {

		String id = "4567890123";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testDescendingCyclicFigure() throws InvalidIDException {

		String id = "6543210987";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testRepeatingDigits() throws InvalidIDException {

		String id = "3434343434";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testConjugativeEvenDigits() throws InvalidIDException {

		String id = "2013564809";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testUinXCkeckSum() throws InvalidIDException {

		String id = "2013020131";
		uinValidatorImpl.validateId(id);
	}
}
