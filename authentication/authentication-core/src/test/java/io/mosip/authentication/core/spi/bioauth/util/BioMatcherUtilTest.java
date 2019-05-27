package io.mosip.authentication.core.spi.bioauth.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.kernel.bioapi.impl.BioApiImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class BioMatcherUtilTest {

	@InjectMocks
	private BioMatcherUtil bioMatcherUtil;

	@InjectMocks
	private BioApiImpl bioApiImpl;

	@Before
	public void before() {
		ReflectionTestUtils.setField(bioMatcherUtil, "bioApi", bioApiImpl);
	}

	private final String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";

	@Test
	public void TestmatchValue() {
		double matchValue = bioMatcherUtil.matchValue(value, value);
		assertEquals(0, Double.compare(90.0, matchValue));
	}

	@Test
	public void TestInvalidMatchValue() {
		double matchValue = bioMatcherUtil.matchValue(value, "Invalid");
		assertNotEquals("90.0", matchValue);
	}

	@Test
	public void TestMatchValuereturnsZero() {
		double matchValue = bioMatcherUtil.matchValue(10, "Invalid");
		assertEquals(0, Double.compare(0, matchValue));
	}

	@Test
	public void TestMatchValuereturnsZerowhenreqInfoisINvalid() {
		double matchValue = bioMatcherUtil.matchValue("Invalid", 10);
		assertEquals(0, Double.compare(0, matchValue));
	}

	@Test
	public void TesInvalidtMatchValuereturnsZero() {
		double matchValue = bioMatcherUtil.matchValue(10, "test");
		assertEquals(0, Double.compare(0, matchValue));
	}

	@Test
	public void TestMultipleValues() {
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("", value);
		double matchMultiValue = bioMatcherUtil.matchMultiValue(valueMap, valueMap);
		assertEquals(0, Double.compare(90.0, matchMultiValue));
	}

	@Test
	public void TestInvaidMultipleValues() {
		Map<String, String> reqInfo = new HashMap<>();
		reqInfo.put("", value);
		Map<String, String> entityInfo = new HashMap<>();
		entityInfo.put("", "Invalid");
		double matchMultiValue = bioMatcherUtil.matchMultiValue(reqInfo, entityInfo);
		assertNotEquals("90.0", matchMultiValue);
	}

	@Test
	public void TestUnknownValues() {
		Map<String, String> reqInfo = new HashMap<>();
		reqInfo.put(IdAuthCommonConstants.UNKNOWN_BIO, value);
		Map<String, String> entityInfo = new HashMap<>();
		entityInfo.put(IdAuthCommonConstants.UNKNOWN_BIO, value);
		double matchMultiValue = bioMatcherUtil.matchMultiValue(reqInfo, entityInfo);
		assertNotEquals("90.0", matchMultiValue);

	}

}
