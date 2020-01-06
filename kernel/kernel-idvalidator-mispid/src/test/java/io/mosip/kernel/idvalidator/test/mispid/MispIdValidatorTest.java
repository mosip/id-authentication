package io.mosip.kernel.idvalidator.test.mispid;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.IdValidator;

/**
 * @author Sidhant Agarwal
 * @author Ritesh Sinha
 * 
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MispIdValidatorTest {

	@Value("${mosip.kernel.mispid.test.valid-mispid}")
	private String validMispId;

	@Value("${mosip.kernel.mispid.test.invalid-mispid}")
	private String invalidMispId;

	@Autowired
	private IdValidator<String> mispIdValidatorImpl;

	@Test
	public void validIdTest() {
		assertThat(mispIdValidatorImpl.validateId(validMispId), is(true));

	}

	@Test(expected = InvalidIDException.class)
	public void invalidMispIdTest() {

		mispIdValidatorImpl.validateId(invalidMispId);
	}

	@Test(expected = InvalidIDException.class)
	public void nullMispIdTest() {
		mispIdValidatorImpl.validateId(null);
	}

	@Test(expected = InvalidIDException.class)
	public void emptyMispIdTest() {
		mispIdValidatorImpl.validateId("");
	}
}
