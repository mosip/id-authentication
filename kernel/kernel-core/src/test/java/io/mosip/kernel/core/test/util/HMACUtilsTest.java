package io.mosip.kernel.core.test.util;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import io.mosip.kernel.core.util.HMACUtils;

public class HMACUtilsTest {

	@Test
	public void testGenerateHash() {
		String name = "Bal Vikash Sharma";
		assertNotNull(HMACUtils.generateHash(name.getBytes()));
	}

	@Test
	public void testUpdate() {
		String name = "Bal Vikash Sharma";
		HMACUtils.update(name.getBytes());
	}

	@Test
	public void testUpdatedHash() {
		assertNotNull(HMACUtils.generateHash(HMACUtils.updatedHash()));
	}

	@Test
	public void testDigestAsPlainText() {
		assertNotNull(HMACUtils.digestAsPlainText("Bal Vikash Sharma".getBytes()));
	}
	
	@Test
	public void testGenarateRandomIV(){
		assertThat(HMACUtils.genarateSalt(),isA(byte[].class));
	}
	
	@Test
	public void testGenarateRandomIVInputBytes(){
		assertThat(HMACUtils.genarateSalt(16),isA(byte[].class));
	}

}
