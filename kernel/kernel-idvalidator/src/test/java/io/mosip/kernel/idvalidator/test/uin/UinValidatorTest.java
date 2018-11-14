
/**
 * 
 */
package io.mosip.kernel.idvalidator.test.uin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;

/**
 * Test class for UinValidator class
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UinValidatorTest {

	@Autowired
	private UinValidatorImpl uinValidator;

	@Test
	public void uinValidatorTest() {

		String id = "426789089018";
		assertEquals(true, uinValidator.validateId(id));
	}

	@Test(expected = InvalidIDException.class)
	public void testNull() throws InvalidIDException {

		String id = null;
		uinValidator.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testEmpty() throws InvalidIDException {

		String id = "";
		uinValidator.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testFistDigitOne() throws InvalidIDException {

		String id = "126789089018";
		uinValidator.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testFistDigitZero() throws InvalidIDException {

		String id = "026789089018";
		uinValidator.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testUinLenght() throws InvalidIDException {

		String id = "42678908901";
		uinValidator.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testUinOverLenght() throws InvalidIDException {

		String id = "4267890890188";
		uinValidator.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testUinCkeckSum() throws InvalidIDException {

		String id = "42678908900";
		uinValidator.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testRepeated() throws InvalidIDException {

		String id = "426789089011";
		uinValidator.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testSequential() throws InvalidIDException {

		String id = "426789089123";
		uinValidator.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testAlphanumeric() throws InvalidIDException {

		String id = "42678908912A";
		uinValidator.validateId(id);
	}

}
