package io.mosip.kernel.idgenerator.tokenid.test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idgenerator.spi.TokenIdGenerator;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TokenIdGeneratorBootTest {

	@Autowired
	TokenIdGenerator<String> tokenIdGenerator;
	
	
}

