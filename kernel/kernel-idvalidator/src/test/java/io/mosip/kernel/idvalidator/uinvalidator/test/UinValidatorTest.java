
/**
 * 
 */
package io.mosip.kernel.idvalidator.uinvalidator.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import io.mosip.kernel.idvalidator.exception.MosipInvalidIDException;
import io.mosip.kernel.idvalidator.uinvalidator.UinValidator;

/**
 * Test class for UinValidator class
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public class UinValidatorTest {

	UinValidator uinValidator = new UinValidator();

	@Test
	public void uinValidatorTest() {

		String id = "426789089018";
		assertEquals(true, uinValidator.validateId(id));
	}
	
	@Test(expected = MosipInvalidIDException.class)
	public void testNull() throws MosipInvalidIDException {

		String id = null;
		uinValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void testFistDigitOne() throws MosipInvalidIDException {

		String id = "126789089018";
		uinValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void testFistDigitZero() throws MosipInvalidIDException {

		String id = "026789089018";
		uinValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void testFistDigitLenght() throws MosipInvalidIDException {

		String id = "42678908901";
		uinValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void testFistDigitCkeckSum() throws MosipInvalidIDException {

		String id = "42678908900";
		uinValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void testRepeated() throws MosipInvalidIDException {

		String id = "426789089011";
		uinValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void testSequential() throws MosipInvalidIDException {

		String id = "426789089123";
		uinValidator.validateId(id);
	}
	
	

	@Test(expected = MosipInvalidIDException.class)
	public void testAlphanumeric() throws MosipInvalidIDException {

		String id = "42678908912A";
		uinValidator.validateId(id);
	}

}
