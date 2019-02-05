/**
 * 
 */
package io.mosip.kernel.idvalidator.uin.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import lombok.val;

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
	
	@Value("${mosip.kernel.uin.test.valid-uin}")
	private String validUin;

	@Value("${mosip.kernel.uin.test.invalid-first-digit-zero-uin}")
	private String invalidFirstDigitZeroUin;

	@Value("${mosip.kernel.uin.test.invalid-first-digit-one-uin}")
	private String invalidFirstDigitOneUin;

	@Value("${mosip.kernel.uin.test.invalid-length-uin}")
	private String invalidLengthUin;

	@Value("${mosip.kernel.uin.test.invalid-extra-length-uin}")
	private String invalidExtraLengthUin;

	@Value("${mosip.kernel.uin.test.invalid-checksum-uin}")
	private String invalidChecksumUin;

	@Value("${mosip.kernel.uin.test.invalid-repeating-number-uin}")
	private String invalidRepeatingNumberUin;

	@Value("${mosip.kernel.uin.test.invalid-sequential-number-uin}")
	private String invalidSequentialNumberUin;

	@Value("${mosip.kernel.uin.test.invalid-alpha-numeric-number-uin}")
	private String invalidAlphaNumericUin;
	
	@Value("${mosip.kernel.uin.test.invalid-first-last-digits-number-uin}")
	private String firstAndLastDigitsValidation;
	
	@Value("${mosip.kernel.uin.test.invalid-first-last-digits-reverse-number-uin}")
	private String firstAndLastDigitsReverseValidation;
	
	
	@Value("${mosip.kernel.uin.test.invalid-ascending-cyclicFigure-number-uin}")
	private String ascendingCyclicFigure;
	
	
	@Value("${mosip.kernel.uin.test.invalid-Descending-cyclicFigure-number-uin}")
	private String descendingCyclicFigure;
	
	
	@Value("${mosip.kernel.uin.test.invalid-repeating-digits-number-uin}")
	private String repeatingDigits;
	
	@Value("${mosip.kernel.uin.test.invalid-conjugative-even-digits-uin}")
	private String conjugativeEvenDigits;


	@Autowired
	private UinValidator<String> uinValidatorImpl;
	
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
		uinValidatorImpl.validateId(invalidFirstDigitOneUin);
	}
	
	
	@Test(expected = InvalidIDException.class)
	public void testFistDigitZero() throws InvalidIDException {
		uinValidatorImpl.validateId(invalidFirstDigitZeroUin);
	}
	
	@Test(expected = InvalidIDException.class)
	public void testUinLenght() throws InvalidIDException {
		uinValidatorImpl.validateId(invalidLengthUin);
	}
	
	@Test(expected = InvalidIDException.class)
	public void testUinOverLenght() throws InvalidIDException {
		uinValidatorImpl.validateId(invalidExtraLengthUin);
	}
	
	@Test(expected = InvalidIDException.class)
	public void testUinCkeckSum() throws InvalidIDException {
		uinValidatorImpl.validateId(invalidChecksumUin);
	}
	@Test(expected = InvalidIDException.class)
	public void testRepeated() throws InvalidIDException {
		uinValidatorImpl.validateId(invalidRepeatingNumberUin);
	}
	
	@Test(expected = InvalidIDException.class)
	public void testSequential() throws InvalidIDException {
		uinValidatorImpl.validateId(invalidSequentialNumberUin);
	}
	
	@Test(expected = InvalidIDException.class)
	public void testAlphanumeric() throws InvalidIDException {
		uinValidatorImpl.validateId(invalidAlphaNumericUin);
	}
	
	
	@Test(expected = InvalidIDException.class)
	public void testFirstAndLastDigitsValidation() throws InvalidIDException {
		uinValidatorImpl.validateId(firstAndLastDigitsValidation);
	}
	
	@Test(expected = InvalidIDException.class)
	public void testFirstAndLastDigitsReverseValidation() throws InvalidIDException {
		uinValidatorImpl.validateId(firstAndLastDigitsReverseValidation);
	}

	@Test
	public void uinValidatorImplTest() {
		assertEquals(true, uinValidatorImpl.validateId(validUin));
	}
	
	@Test(expected = InvalidIDException.class)
	public void testAscendingCyclicFigure() throws InvalidIDException {
		uinValidatorImpl.validateId(ascendingCyclicFigure);
	}

	@Test(expected = InvalidIDException.class)
	public void testDescendingCyclicFigure() throws InvalidIDException {
		uinValidatorImpl.validateId(descendingCyclicFigure);
	}
	
	
	@Test(expected = InvalidIDException.class)
	public void testRepeatingDigits() throws InvalidIDException {
		uinValidatorImpl.validateId(repeatingDigits);
	}

	@Test(expected = InvalidIDException.class)
	public void testConjugativeEvenDigits() throws InvalidIDException {
		uinValidatorImpl.validateId(conjugativeEvenDigits);
	}
	
}