package org.mosip.kernel.pidgenerator;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kishan Rathore
 *
 */
public class PidGeneratorTest {
	
	private static final int NoOfDigits=14;
	
	@Test
	public void pidDigitTest() {
		
		PidGenerator pidGenerator=new PidGenerator();
		Assert.assertEquals(NoOfDigits, pidGenerator.generateId().length());
		
		
	}
	
	@Test
	public void nonRepeatationTest() {
		
		PidGenerator pidGenerator=new PidGenerator();
		String PID_A=pidGenerator.generateId();
		String PID_B=pidGenerator.generateId();
		Assert.assertNotSame(PID_A, PID_B);
		
	}

}
