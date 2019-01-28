package io.mosip.kernel.idvalidator.test.tspid;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.IdValidator;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TspIdValidatorTest {

	@Value("${mosip.kernel.tspid.test.valid-tspid}")
	private String validTspId;

	@Value("${mosip.kernel.tspid.test.invalid-tspid}")
	private String invalidTspId;
	
	@Autowired
	private IdValidator<String> tspIdValidatorImpl;

	@Test
	public void validIdTest() {
		assertThat(tspIdValidatorImpl.validateId(validTspId), is(true));

	}

	@Test(expected = InvalidIDException.class)
	public void invalidTspIdTest() {

		tspIdValidatorImpl.validateId(invalidTspId);
	}

	@Test(expected = InvalidIDException.class)
	public void nullTspIdTest() {
		tspIdValidatorImpl.validateId(null);
	}

	@Test(expected = InvalidIDException.class)
	public void emptyTspIdTest() {
		tspIdValidatorImpl.validateId("");
	}
}
