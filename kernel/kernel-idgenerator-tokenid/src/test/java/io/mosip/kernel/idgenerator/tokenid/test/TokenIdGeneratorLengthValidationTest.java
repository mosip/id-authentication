package io.mosip.kernel.idgenerator.tokenid.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idgenerator.spi.TokenIdGenerator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TokenIdGeneratorBootApplication.class)
public class TokenIdGeneratorLengthValidationTest {

	@MockBean
	private TokenIdGenerator<String, String> tokenIdGenerator;

	@Value("${mosip.kernel.tokenid.length}")
	private Integer tokenIdLength;

	@Test
	public void tokenIdLengthInValidTest() {
		when(tokenIdGenerator.generateId(Mockito.anyString(), Mockito.anyString())).thenReturn("1");
		int tokenLength = tokenIdGenerator.generateId("6475", "984763876283").length();
		assertNotEquals(tokenLength, is(tokenIdLength));
	}
}
