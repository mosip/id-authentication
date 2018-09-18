package org.mosip.kernel.eidgenerator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.mosip.kernel.eidgenerator.exception.MosipEmptyInputException;
import org.mosip.kernel.eidgenerator.exception.MosipInputLengthException;
import org.mosip.kernel.eidgenerator.exception.MosipNullValueException;

/**
 * Unit test for EID generator
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 */
public class EidGeneratorTest {
	EidGenerator eidGenerator = new EidGenerator();

	@Test
	public void eidGenerationTest() {
		String testAgentId = "12345";
		String testMachineId = "56789";

		assertThat((eidGenerator.eidGeneration(testAgentId, testMachineId).length()), is(27));
	}

	@Test(expected = MosipNullValueException.class)
	public void eidGenerationTestWithNullException() {
		String testAgentId = null;
		String testMachineId = null;
		eidGenerator.eidGeneration(testAgentId, testMachineId);

	}

	@Test(expected = MosipEmptyInputException.class)
	public void eidGenerationTestWithEmptyException() {
		String testAgentId = "";
		String testMachineId = "";
		eidGenerator.eidGeneration(testAgentId, testMachineId);

	}

	@Test(expected = MosipInputLengthException.class)
	public void eidGenerationTestWithLengthException() {
		String testAgentId = "12";
		String testMachineId = "12";
		eidGenerator.eidGeneration(testAgentId, testMachineId);

	}
}
