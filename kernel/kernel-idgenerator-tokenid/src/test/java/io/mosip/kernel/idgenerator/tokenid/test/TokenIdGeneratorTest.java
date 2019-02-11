package io.mosip.kernel.idgenerator.tokenid.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idgenerator.exception.TokenIdGeneratorException;
import io.mosip.kernel.core.idgenerator.spi.TokenIdGenerator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TokenIdGeneratorBootApplication.class)
public class TokenIdGeneratorTest {

	@Autowired
	private TokenIdGenerator<String, String> tokenIdGenerator;

	@Value("${mosip.kernel.tokenid.length}")
	private Integer tokenIdLength;

	@Test
	public void notNullTest() {
		assertNotNull(tokenIdGenerator.generateId("9870", "029476298109"));
	}

	@Test
	public void tokenIdLengthTest() {
		int tokenLength = tokenIdGenerator.generateId("6475", "984763876283").length();
		assertThat(tokenLength, is(tokenIdLength));
	}

	@Test(expected = TokenIdGeneratorException.class)
	public void tokenIdApiExceptionTestWhenTSPIsEmpty() {
		tokenIdGenerator.generateId("", "874238947132894");
	}

	@Test(expected = TokenIdGeneratorException.class)
	public void tokenIdApiExceptionTestWhenUINIsEmpty() {
		tokenIdGenerator.generateId("6732874784", "");
	}

	@Test(expected = TokenIdGeneratorException.class)
	public void tokenIdApiExceptonTestWhenTSPIsEmptyAndUINIsNull() {
		tokenIdGenerator.generateId("", null);
	}

	@Test(expected = TokenIdGeneratorException.class)
	public void tokenIdApiExcetionTestWhenTSPIsNullAndUINIsEmpty() {
		tokenIdGenerator.generateId(null, "");
	}
}
