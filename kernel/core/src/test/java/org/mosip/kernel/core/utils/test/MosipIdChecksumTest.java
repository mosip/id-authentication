/**
 * 
 */
package org.mosip.kernel.core.utils.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.mosip.kernel.core.utils.MosipIdChecksum;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class MosipIdChecksumTest {

	@Test
	public void checksumTest() {
		String id = "1234567890";
		String checkSum = MosipIdChecksum.generateChecksumDigit(id);
		String finalId = id + checkSum;
		boolean res = MosipIdChecksum.validateChecksum(finalId);
		assertThat(res, is(true));
	}
	
	@Test
	public void checksumFailTest() {
		String id = "1234567891";
		boolean res = MosipIdChecksum.validateChecksum(id);
		assertThat(res, is(false));
	}

}
