package io.mosip.kernel.core.test.util;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import io.mosip.kernel.core.util.CryptoUtil;

public class CryptoUtilTest {

	@Test
	public void testCombineByteArray() {
		assertThat(CryptoUtil.combineByteArray("data".getBytes(), "key".getBytes(), "#KEY_SPLITTER#"), isA(byte[].class));
	}

	@Test
	public void testGetSplitterIndex() {
		assertThat(CryptoUtil.getSplitterIndex("data#KEY_SPLITTER#data".getBytes(), 0, "#KEY_SPLITTER#"), isA(int.class));
	}

	@Test
	public void testEncodeBase64() {
		assertThat(CryptoUtil.encodeBase64("data".getBytes()), isA(String.class));
	}

	@Test
	public void testDecodeBase64() {
		assertThat(CryptoUtil.decodeBase64("data"), isA(byte[].class));
	}

}
