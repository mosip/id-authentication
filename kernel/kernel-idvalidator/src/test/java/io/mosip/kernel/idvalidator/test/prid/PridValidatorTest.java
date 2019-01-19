package io.mosip.kernel.idvalidator.test.prid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.IdValidator;


/**
 * Test class for pridValidatorImpl class
 * 
 * @author M1037462
 * 
 * @since 1.0.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class PridValidatorTest {

	@Autowired
	private IdValidator<String> pridValidatorImpl;

	@Test(expected = InvalidIDException.class)
	public void nullTest() {
		String id = null;
		pridValidatorImpl.validateId(id);

	}

	@Test(expected = InvalidIDException.class)
	public void lengthTest() {
		String id = "537184361359";
		pridValidatorImpl.validateId(id);

	}

	@Test(expected = InvalidIDException.class)
	public void firstDigitZeroTest() {
		String id = "05124301326317";

		pridValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void firstDigitOneTest() {
		String id = "15124301326317";
		pridValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void ChecksumTest() {
		String id = "75124301326313";
		pridValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void alphaNumericTest() {

		String id = "751A4301326317";
		pridValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void repeatingBlockTest() {
		String id = "75124751226317";
		pridValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void sequentialNumberTest() {
		String id = "75123751226317";
		pridValidatorImpl.validateId(id);
	}

//	@Test(expected = InvalidIDException.class)
//	public void repeatingNumberTest() {
//		String id = "75122251226317";
//		pridValidatorImpl.validateId(id);
//	}

	@Test
	public void ValidIdTest() {
		String id = "75124301328620";

		assertEquals(true, pridValidatorImpl.validateId(id));
	}

}
