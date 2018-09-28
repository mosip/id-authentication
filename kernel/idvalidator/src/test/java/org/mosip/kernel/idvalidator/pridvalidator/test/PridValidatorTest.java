package org.mosip.kernel.idvalidator.pridvalidator.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mosip.kernel.idvalidator.exception.MosipInvalidIDException;
import org.mosip.kernel.idvalidator.pridvalidator.PridValidator;

/**
 * Test class for PridValidator class
 * 
 * @author M1037462
 * 
 * @since 1.0.0
 */
public class PridValidatorTest {

	PridValidator pridValidator = new PridValidator();

	@Test(expected = MosipInvalidIDException.class)
	public void nullTest() {
		String id = null;
		pridValidator.validateId(id);

	}

	@Test(expected = MosipInvalidIDException.class)
	public void lengthTest() {
		String id = "537184361359";
		pridValidator.validateId(id);

	}

	@Test(expected = MosipInvalidIDException.class)
	public void firstDigitZeroTest() {
		String id = "05124301326317";

		pridValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void firstDigitOneTest() {
		String id = "15124301326317";
		pridValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void ChecksumTest() {
		String id = "75124301326313";
		pridValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void alphaNumericTest() {

		String id = "751A4301326317";
		pridValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void repeatingBlockTest() {
		String id = "75124751226317";
		pridValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void sequentialNumberTest() {
		String id = "75123751226317";
		pridValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void repeatingNumberTest() {
		String id = "75122251226317";
		pridValidator.validateId(id);
	}

	@Test
	public void ValidIdTest() {
		String id = "75124301328620";

		assertEquals(true, pridValidator.validateId(id));
	}

}
