package org.mosip.kernel.ridgenerator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.mosip.kernel.ridgenerator.RidGenerator;
import org.mosip.kernel.ridgenerator.exception.MosipEmptyInputException;
import org.mosip.kernel.ridgenerator.exception.MosipInputLengthException;
import org.mosip.kernel.ridgenerator.exception.MosipNullValueException;

/**
 * Unit test for RID generator
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 */
public class RidGeneratorTest {
	RidGenerator ridGenerator = new RidGenerator();

	@Test
	public void ridGenerationTest() {
		String testAgentId = "12345";
		String testMachineId = "56789";

		assertThat((ridGenerator.ridGeneration(testAgentId, testMachineId).length()), is(27));
	}

	@Test(expected = MosipNullValueException.class)
	public void ridGenerationTestWithNullException() {
		String testAgentId = null;
		String testMachineId = null;
		ridGenerator.ridGeneration(testAgentId, testMachineId);

	}

	@Test(expected = MosipEmptyInputException.class)
	public void ridGenerationTestWithEmptyException() {
		String testAgentId = "";
		String testMachineId = "";
		ridGenerator.ridGeneration(testAgentId, testMachineId);

	}

	@Test(expected = MosipInputLengthException.class)
	public void ridGenerationTestWithLengthException() {
		String testAgentId = "12";
		String testMachineId = "12";
		ridGenerator.ridGeneration(testAgentId, testMachineId);

	}
}
