package io.mosip.kernel.idvalidator.test.vid;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idvalidator.exception.MosipInvalidIDException;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;

/**
 * Test class for VIDValidator class
 * 
 * @author M1037462 since 1.0.0
 * 
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class VidValidatorTest {

	@Autowired
	VidValidatorImpl vidValidator;

	@Test(expected = MosipInvalidIDException.class)
	public void nullTest() {
		String id = null;
		vidValidator.validateId(id);

	}

	@Test(expected = MosipInvalidIDException.class)
	public void lengthTest() {
		String id = "537184361359820";
		vidValidator.validateId(id);

	}

	@Test(expected = MosipInvalidIDException.class)
	public void firstDigitZeroTest() {
		String id = "0247389354374855";

		vidValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void firstDigitOneTest() {
		String id = "1247389354374855";
		vidValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void ChecksumTest() {
		String id = "5371843613598205";
		vidValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void alphaNumericTest() {

		String id = "53718A3613598206";
		vidValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void repeatingBlockTest() {
		String id = "8241239351234855";
		vidValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void sequentialNumberTest() {
		String id = "8245679354374855";
		vidValidator.validateId(id);
	}

	@Test(expected = MosipInvalidIDException.class)
	public void repeatingNumberTest() {
		String id = "5371143613598206";
		vidValidator.validateId(id);
	}

	@Test
	public void ValidIdTest() {
		String id = "5371843613598206";
		assertEquals(true, vidValidator.validateId(id));
	}

}
