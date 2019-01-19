package io.mosip.kernel.idvalidator.test.vid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.IdValidator;

/**
 * Test class for vidValidatorImpl class
 * 
 * @author M1037462 since 1.0.0
 * 
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class VidValidatorTest {
	
	@Autowired
	private IdValidator<String> vidValidatorImpl;

	

	@Test(expected = InvalidIDException.class)
	public void nullTest() {
		String id = null;
		vidValidatorImpl.validateId(id);

	}

	@Test(expected = InvalidIDException.class)
	public void lengthTest() {
		String id = "537184361359820";
		vidValidatorImpl.validateId(id);

	}

	@Test(expected = InvalidIDException.class)
	public void firstDigitZeroTest() {
		String id = "0247389354374855";

		vidValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void firstDigitOneTest() {
		String id = "1247389354374855";
		vidValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void ChecksumTest() {
		String id = "5371843613598205";
		vidValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void alphaNumericTest() {

		String id = "53718A3613598206";
		vidValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void repeatingBlockTest() {
		String id = "8241239351234855";
		vidValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void sequentialNumberTest() {
		String id = "8245679354374855";
		vidValidatorImpl.validateId(id);
	}

	@Test(expected = InvalidIDException.class)
	public void repeatingNumberTest() {
		String id = "5371143613598206";
		vidValidatorImpl.validateId(id);
	}

	@Test
	public void ValidIdTest() {
		String id = "5371843613598206";
		assertEquals(true, vidValidatorImpl.validateId(id));
	}

}
