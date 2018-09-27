package org.mosip.kernel.pidgenerator;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.kernel.pridgenerator.generator.PridGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test class for PridGenerator class
 * 
 * @author M1037462 since 1.0.0
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class PridGeneratorTest {

	private static final int NoOfDigits = 14;

	@Autowired
	private PridGenerator pridGenerator;

	@Test
	public void pridDigitTest() {

		assertEquals(NoOfDigits, pridGenerator.generatePrid().length());
	}

	@Test
	public void notNullTest() {

		assertNotNull(pridGenerator.generatePrid());
	}

}
