package io.mosip.kernel.idvalidator.vid.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;

/**
 * Test class for vidValidatorImpl class
 * 
 * @author M1037462 since 1.0.0
 * 
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class VidValidatorTest {

	@Value("${mosip.kernel.vid.test.valid-vid}")
	private String validVid;

	@Value("${mosip.kernel.vid.test.invalid-length-vid}")
	private String invalidLengthVid;

	@Value("${mosip.kernel.vid.test.invalid-first-digit-zero-vid}")
	private String invalidFirstDigitZeroVid;

	@Value("${mosip.kernel.vid.test.invalid-first-digit-one-vid}")
	private String invalidFirstDigitOneVid;

	@Value("${mosip.kernel.vid.test.invalid-checksum-vid}")
	private String invalidChecksumVid;

	@Value("${mosip.kernel.vid.test.invalid-alphanumeric-vid}")
	private String invalidAlphaNumericVid;

	@Value("${mosip.kernel.vid.test.invalid-repeating-block-vid}")
	private String invalidReapeatingBlockVid;

	@Value("${mosip.kernel.vid.test.invalid-sequencial-number-vid}")
	private String invalidSequencialNumberVid;

	@Value("${mosip.kernel.vid.test.invalid-repeating-number-vid}")
	private String invalidRepeatingNumberVid;

	@Autowired
	private VidValidator<String> vidValidatorImpl;

	@Test(expected = InvalidIDException.class)
	public void nullTest() {
		String id = null;
		vidValidatorImpl.validateId(id);

	}

	@Test(expected = InvalidIDException.class)
	public void lengthTest() {

		vidValidatorImpl.validateId(invalidLengthVid);

	}

	@Test(expected = InvalidIDException.class)
	public void firstDigitZeroTest() {

		vidValidatorImpl.validateId(invalidFirstDigitZeroVid);
	}

	@Test(expected = InvalidIDException.class)
	public void firstDigitOneTest() {

		vidValidatorImpl.validateId(invalidFirstDigitOneVid);
	}

	@Test(expected = InvalidIDException.class)
	public void ChecksumTest() {

		vidValidatorImpl.validateId(invalidChecksumVid);
	}

	@Test(expected = InvalidIDException.class)
	public void alphaNumericTest() {

		vidValidatorImpl.validateId(invalidAlphaNumericVid);
	}

	@Test(expected = InvalidIDException.class)
	public void repeatingBlockTest() {

		vidValidatorImpl.validateId(invalidReapeatingBlockVid);
	}

	@Test(expected = InvalidIDException.class)
	public void sequentialNumberTest() {

		vidValidatorImpl.validateId(invalidSequencialNumberVid);
	}

	@Test(expected = InvalidIDException.class)
	public void repeatingNumberTest() {

		vidValidatorImpl.validateId(invalidRepeatingNumberVid);
	}

	@Test
	public void ValidIdTest() {

		assertEquals(true, vidValidatorImpl.validateId(validVid));
	}

}
