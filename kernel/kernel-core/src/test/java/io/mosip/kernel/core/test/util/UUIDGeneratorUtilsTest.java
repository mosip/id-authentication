package io.mosip.kernel.core.test.util;

import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import io.mosip.kernel.core.util.UUIDUtils;

public class UUIDGeneratorUtilsTest {
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final String SAMPLE_NAME = "balvikash.sharma@bihar";
	private static final String HYPHEN = "-";
	private static final String ALPHANUMERIC = "[\\w]+";

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGenerateType5UUIDUUIDString() {
		UUID value = UUIDUtils.getUUID(UUIDUtils.NAMESPACE_URL, SAMPLE_NAME);
		checkValidUUIDType5(value);

	}

	@Test(expected = NullPointerException.class)
	public void testGenerateType5UUIDUUIDStringNPEWithNullNamespace() {
		UUIDUtils.getUUID(null, SAMPLE_NAME);
	}

	@Test(expected = NullPointerException.class)
	public void testGenerateType5UUIDUUIDStringNPEWithNullName() {
		UUIDUtils.getUUIDFromBytes(UUIDUtils.NAMESPACE_URL, null);
	}

	@Test
	public void generateType5UUIDByteArray() {
		UUID value = UUIDUtils.getUUIDFromBytes(UUIDUtils.NAMESPACE_URL, SAMPLE_NAME.getBytes(UTF8));
		checkValidUUIDType5(value);
	}

	private void checkValidUUIDType5(UUID value) {
		String uuidStr = value.toString();
		assertTrue(uuidStr.trim().length() == 36);
		String[] tokenArray = uuidStr.split(HYPHEN);
		assertTrue(tokenArray.length == 5);
		for (String string : tokenArray) {
			assertTrue(string.matches(ALPHANUMERIC));
		}
	}

}
