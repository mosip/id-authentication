package io.mosip.kernel.idgenerator.prid.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.core.idvalidator.spi.PridValidator;

/**
 * Test class for PridGenerator class
 * 
 * @author M1037462 since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PridGeneratorTest {

	@Value("${mosip.kernel.prid.length}")
	private int pridLength;

	@Autowired
	private PridGenerator<String> pridGenerator;

	@Autowired
	private PridValidator<String> pridValidator;

	@Test
	public void notNullTest() {
		assertNotNull(pridGenerator.generateId());
	}

	@Test
	public void pridLengthTest() {
		assertEquals(pridLength, pridGenerator.generateId().length());
	}

	//@Test
	public void pridValidationTest() {
		assertTrue(pridValidator.validateId(pridGenerator.generateId()));
	}
}
