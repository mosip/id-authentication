package io.mosip.authentication.core.util;

import org.junit.Assert;
import org.junit.Test;


public class CryptoUtilTest {

	/**
	 * Test combine byte array
	 */
	@Test
	public void testCombineByteArray() {
		byte[] data = "testData".getBytes();
		byte[] key = "testKey".getBytes();
		String keySplitter = "||KEY_SPLITTER||";
		
		byte[] result = CryptoUtil.combineByteArray(data, key, keySplitter);
		
		Assert.assertNotNull(result);
		Assert.assertTrue(result.length > 0);
	}
	
	/**
	 * Test encode base64
	 */
	@Test
	public void testEncodeBase64() {
		byte[] data = "testData".getBytes();
		
		String encoded = CryptoUtil.encodeBase64(data);
		
		Assert.assertNotNull(encoded);
		Assert.assertFalse(encoded.isEmpty());
	}
	
	/**
	 * Test encode base64 URL safe
	 */
	@Test
	public void testEncodeBase64Url() {
		byte[] data = "testData".getBytes();
		
		String encoded = CryptoUtil.encodeBase64Url(data);
		
		Assert.assertNotNull(encoded);
		Assert.assertFalse(encoded.isEmpty());
	}
	
	/**
	 * Test decode base64
	 */
	@Test
	public void testDecodeBase64() {
		byte[] originalData = "testData".getBytes();
		String encoded = CryptoUtil.encodeBase64(originalData);
		
		byte[] decoded = CryptoUtil.decodeBase64(encoded);
		
		Assert.assertNotNull(decoded);
		Assert.assertArrayEquals(originalData, decoded);
	}
	
	/**
	 * Test decode base64 URL
	 */
	@Test
	public void testDecodeBase64Url() {
		byte[] originalData = "testData".getBytes();
		String encoded = CryptoUtil.encodeBase64Url(originalData);
		
		byte[] decoded = CryptoUtil.decodeBase64Url(encoded);
		
		Assert.assertNotNull(decoded);
		Assert.assertArrayEquals(originalData, decoded);
	}
}
