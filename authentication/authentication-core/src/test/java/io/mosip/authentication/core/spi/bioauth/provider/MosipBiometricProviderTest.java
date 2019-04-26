/**
 * 
 */
package io.mosip.authentication.core.spi.bioauth.provider;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author Dinesh Karuppiah.T
 *
 */
public class MosipBiometricProviderTest {

	MosipBiometricProvider mosipBiometricProvider = new MosipBiometricProvider() {

		@Override
		public double matchScoreCalculator(byte[] inputIsoTemplate, byte[] storedIsoTemplate) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double matchScoreCalculator(String inputMinutiae, String storedMinutiae) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double matchImage(Object reqInfo, Object entityInfo) {
			// TODO Auto-generated method stub
			return 0;
		}
	};

	@Test
	public void TestMatchScoreCalculatorString() {
		assertNotNull(mosipBiometricProvider.matchScoreCalculator("test", "invalid"));
	}

	@Test
	public void TestMatchScoreCalculatorByte() {
		assertNotNull(mosipBiometricProvider.matchScoreCalculator("test".getBytes(), "invalid".getBytes()));
	}

	@Test
	public void TestcreateMinutiae() {
		assertNull(mosipBiometricProvider.createMinutiae("test".getBytes()));
	}

	@Test
	public void TestmatchImage() {
		assertNotNull(mosipBiometricProvider.matchImage("test", "invalid"));
	}

	@Test
	public void TestmatchMinutiae() {
		assertNotNull(mosipBiometricProvider.matchMinutiae("test", "invalid"));
	}

	@Test
	public void TestmatchMultiMinutae() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("test", "test");
		assertNotNull(mosipBiometricProvider.matchMultiMinutae(infoMap, infoMap));
	}

	@Test
	public void TestmatchMultiImage() {
		assertNotNull(mosipBiometricProvider.matchMultiImage("test", "invalid"));
	}

}
