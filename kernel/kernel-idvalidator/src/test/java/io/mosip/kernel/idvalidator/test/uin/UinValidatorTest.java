
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
import io.mosip.kernel.core.idvalidator.spi.IdValidator;

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
	private IdValidator<String> uinValidatorImpl;

	@Test
	public void uinValidatorImplTest() {

		String id = "426789089018";
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

		String id = "126789089018";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testFistDigitZero() throws InvalidIDException {

		String id = "026789089018";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testUinLenght() throws InvalidIDException {

		String id = "42678908901";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testUinOverLenght() throws InvalidIDException {

		String id = "4267890890188";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testUinCkeckSum() throws InvalidIDException {

		String id = "42678908900";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testRepeated() throws InvalidIDException {

		String id = "426789089011";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testSequential() throws InvalidIDException {

		String id = "426789089123";
		uinValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void testAlphanumeric() throws InvalidIDException {

		String id = "42678908912A";
		uinValidatorImpl.validateId(id);
	}

}
