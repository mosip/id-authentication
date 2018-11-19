package io.mosip.kernel.idgenerator.prid.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idgenerator.spi.PridGenerator;


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
	private PridGenerator<String> mosipPridGenerator;

	@Test
	public void notNullTest() {
		assertNotNull(mosipPridGenerator.generateId());
	}

	@Test
	public void pridLengthTest() {
		assertEquals(pridLength, mosipPridGenerator.generateId().length());
	}
}
