package org.mosip.auth.core.util;

import static org.junit.Assert.*;

import java.util.Base64;

import org.junit.Test;

public class OTPUtilTest {

	@Test
	public void testGenerateKey() {
		String actualKey = OTPUtil.generateKey("IDA", "refId", "txnId", "auaCode");
		String expectedKey = "IDA".concat("_").concat(Base64.getEncoder().encodeToString("refId".getBytes())).concat("_")
				.concat("txnId").concat("_").concat("auaCode");
		assertEquals(expectedKey, actualKey);
	}
	
	@Test
	public void testGenerateKeyFail() {
		String actualKey = OTPUtil.generateKey("IDA", "ref", "txnId", "auaCode");
		String expectedKey = "IDA".concat("_").concat(Base64.getEncoder().encodeToString("refId".getBytes())).concat("_")
				.concat("txnId").concat("_").concat("auaCode");
		assertNotEquals(expectedKey, actualKey);
	}

}
