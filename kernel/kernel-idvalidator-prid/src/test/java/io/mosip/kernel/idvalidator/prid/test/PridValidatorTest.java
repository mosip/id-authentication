package io.mosip.kernel.idvalidator.prid.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.PridValidator;

/**
 * Test class for pridValidatorImpl class
 * 
 * @author M1037462
 * @author Abhishek Kumar
 * 
 * @since 1.0.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class PridValidatorTest {

	@Value("${mosip.kernel.prid.test.valid-prid}")
	private String validPrid;

	@Value("${mosip.kernel.prid.test.invalid-length-prid}")
	private String invalidLengthPrid;

	@Value("${mosip.kernel.prid.test.invalid-first-digit-zero-prid}")
	private String invalidFirstDigitZeroPrid;

	@Value("${mosip.kernel.prid.test.invalid-first-digit-one-prid}")
	private String invalidFirstDigitOnePrid;

	@Value("${mosip.kernel.prid.test.invalid-checksum-prid}")
	private String invalidChecksumPrid;

	@Value("${mosip.kernel.prid.test.invalid-alphanumeric-prid}")
	private String invalidAlphaNumericPrid;

	@Value("${mosip.kernel.prid.test.invalid-repeating-block-prid}")
	private String invalidReapeatingBlockPrid;

	@Value("${mosip.kernel.prid.test.invalid-sequential-number-prid}")
	private String invalidSequentialNumberPrid;

	@Autowired
	private PridValidator<String> pridValidatorImpl;

	@Test(expected = InvalidIDException.class)
	public void nullTest() {
		String id = null;
		pridValidatorImpl.validateId(id);

	}

	@Test(expected = InvalidIDException.class)
	public void lengthTest() {

		pridValidatorImpl.validateId(invalidLengthPrid);

	}

	@Test(expected = InvalidIDException.class)
	public void firstDigitZeroTest() {

		pridValidatorImpl.validateId(invalidFirstDigitZeroPrid);
	}

	@Test(expected = InvalidIDException.class)
	public void firstDigitOneTest() {

		pridValidatorImpl.validateId(invalidFirstDigitOnePrid);
	}

	@Test(expected = InvalidIDException.class)
	public void ChecksumTest() {

		pridValidatorImpl.validateId(invalidChecksumPrid);
	}

	@Test(expected = InvalidIDException.class)
	public void alphaNumericTest() {

		pridValidatorImpl.validateId(invalidAlphaNumericPrid);
	}

	@Test(expected = InvalidIDException.class)
	public void repeatingBlockTest() {

		pridValidatorImpl.validateId(invalidReapeatingBlockPrid);
	}

	@Test(expected = InvalidIDException.class)
	public void sequentialNumberTest() {

		pridValidatorImpl.validateId(invalidSequentialNumberPrid);
	}

	// @Test(expected = InvalidIDException.class)
	// public void repeatingNumberTest() {
	// String id = "75122251226317";
	// pridValidatorImpl.validateId(id);
	// }

	@Test
	public void ValidIdTest() {

		assertEquals(true, pridValidatorImpl.validateId(validPrid));
	}

	@Test
	public void ValidIdWithParamsTest() {

		assertEquals(true, pridValidatorImpl.validateId(validPrid, 14, 3, 2, 2));
	}

	@Test(expected = InvalidIDException.class)
	public void ValidIdWithParamsTestInvalid() {

		assertEquals(true, pridValidatorImpl.validateId(validPrid, -1, 3, 2, 2));
	}

	@Test(expected = InvalidIDException.class)
	public void ValidIdWithAllParamsTestInvalid() {

		assertEquals(true, pridValidatorImpl.validateId(validPrid, -1, -1, -1, -1));
	}

	@Test(expected = InvalidIDException.class)
	public void ChecksumTestF() {

		pridValidatorImpl.validateId(invalidChecksumPrid);
	}

}
