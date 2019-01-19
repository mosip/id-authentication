package io.mosip.kernel.idgenerator.tokenid.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;




import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;



import io.mosip.kernel.core.idgenerator.spi.TokenIdGenerator;


/**
 * Test class for TokenIdenerator class
 * 
 * @author M1037462 since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TokenIdGeneratorTest {
	

	@Autowired
	private TokenIdGenerator<String> tokenIdGenerator;

	@Value("${mosip.kernel.tokenid.length}")
	private Integer  tokenIdLength ;


	@Test
	public void notNullTest() {
		assertNotNull(tokenIdGenerator.generateId());
	}

	@Test
	public void tokenIdLengthTest() {
		Integer tokenLength = tokenIdGenerator.generateId().length();
		assertEquals(tokenIdLength, tokenLength);
	}


}
