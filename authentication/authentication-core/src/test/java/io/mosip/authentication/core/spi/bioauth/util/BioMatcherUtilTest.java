package io.mosip.authentication.core.spi.bioauth.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
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
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
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
		ReflectionTestUtils.setField(bioMatcherUtil, "fingerApi", bioApiImpl);
		ReflectionTestUtils.setField(bioMatcherUtil, "faceApi", bioApiImpl);
		ReflectionTestUtils.setField(bioMatcherUtil, "irisApi", bioApiImpl);
	}

	Map<String, String> valueMap = new HashMap<>();
	private final String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";

	@Ignore
	@Test
	public void TestmatchValue() throws IdAuthenticationBusinessException {
		valueMap.put(value, value);
		double matchValue = bioMatcherUtil.matchValue(valueMap, valueMap);
		assertEquals(0, Double.compare(90.0, matchValue));
	}

	@Ignore
	@Test
	public void TestInvalidMatchValue() throws IdAuthenticationBusinessException {
		valueMap.put(value, value);
		Map<String, String> invalidMap = new HashMap<>();
		invalidMap.put("invalid", "invalid");
		double matchValue = bioMatcherUtil.matchValue(valueMap, invalidMap);
		assertNotEquals("90.0", matchValue);
	}

	@Test
	public void TestMatchValuereturnsZerowhenreqInfoisINvalid() throws IdAuthenticationBusinessException {
		double matchValue = bioMatcherUtil.matchValue(valueMap, valueMap);
		assertEquals(0, Double.compare(0, matchValue));
	}

	@Test
	public void TesInvalidtMatchValuereturnsZero() throws IdAuthenticationBusinessException {
		double matchValue = bioMatcherUtil.matchValue(valueMap, valueMap);
		assertEquals(0, Double.compare(0, matchValue));
	}

	@Test
	public void TestMultipleValues() throws IdAuthenticationBusinessException {
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("", value);
		double matchMultiValue = bioMatcherUtil.matchMultiValue(valueMap, valueMap);
		assertEquals(0, Double.compare(90.0, matchMultiValue));
	}

	@Test
	public void TestInvaidMultipleValues() throws IdAuthenticationBusinessException {
		Map<String, String> reqInfo = new HashMap<>();
		reqInfo.put("", value);
		Map<String, String> entityInfo = new HashMap<>();
		entityInfo.put("", "Invalid");
		double matchMultiValue = bioMatcherUtil.matchMultiValue(reqInfo, entityInfo);
		assertNotEquals("90.0", matchMultiValue);
	}

	@Test
	public void TestUnknownValues() throws IdAuthenticationBusinessException {
		Map<String, String> reqInfo = new HashMap<>();
		reqInfo.put(IdAuthCommonConstants.UNKNOWN_BIO, value);
		Map<String, String> entityInfo = new HashMap<>();
		entityInfo.put(IdAuthCommonConstants.UNKNOWN_BIO, value);
		double matchMultiValue = bioMatcherUtil.matchMultiValue(reqInfo, entityInfo);
		assertNotEquals("90.0", matchMultiValue);

	}

}
