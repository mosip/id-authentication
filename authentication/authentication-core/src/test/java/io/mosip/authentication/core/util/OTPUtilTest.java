package io.mosip.authentication.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Base64;

import org.junit.Test;

import io.mosip.authentication.core.dto.OTPUtil;

/**
 * The Class OTPUtilTest.
 *
 * @author Manoj SP
 */
public class OTPUtilTest {

	/**
	 * Test generate key.
	 */
	@Test
	public void testGenerateKey() {
		String actualKey = OTPUtil.generateKey("IDA", "uin", "txnId", "auaCode");
		String expectedKey = "IDA".concat("_").concat(Base64.getEncoder().encodeToString("uin".getBytes())).concat("_")
				.concat("txnId").concat("_").concat("auaCode");
		assertEquals(expectedKey, actualKey);
	}
	
	/**
	 * Test generate key fail.
	 */
	@Test
	public void testGenerateKeyFail() {
		String actualKey = OTPUtil.generateKey("IDA", "ref", "txnId", "auaCode");
		String expectedKey = "IDA".concat("_").concat(Base64.getEncoder().encodeToString("uin".getBytes())).concat("_")
				.concat("txnId").concat("_").concat("auaCode");
		assertNotEquals(expectedKey, actualKey);
	}

}
