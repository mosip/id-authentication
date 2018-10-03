package io.mosip.kernel.pridgenerator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.spi.idgenerator.MosipPridGenerator;

/**
 * Test class for PridGenerator class
 * 
 * @author M1037462 since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:/test.application.properties")
public class PridGeneratorTest {
	@Value("${mosip.kernel.prid.length}")
	private int pridLength;
	@Autowired
	private MosipPridGenerator<String> pridGenerator;

	@Test
	public void notNullTest() {
		assertNotNull(pridGenerator.generateId());
	}

	@Test
	public void pridDigitTest() {
		assertEquals(pridLength, pridGenerator.generateId().length());
	}
}
