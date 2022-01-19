package io.mosip.authentication.common.service.websub;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class CacheUpdatingWebsubInitializerTest {

	private CacheUpdatingWebsubInitializer createTestSubject() {
		CacheUpdatingWebsubInitializer cacheUpdatingWebsubInitializer = new CacheUpdatingWebsubInitializer() {

			@Override
			protected int doInitSubscriptions() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			protected int doRegisterTopics() {
				// TODO Auto-generated method stub
				return 0;
			}};
		ReflectionTestUtils.setField(cacheUpdatingWebsubInitializer, "cacheType", "simple");
		return cacheUpdatingWebsubInitializer;
	}

	@Test
	public void testIsCacheEnabled() throws Exception {
		CacheUpdatingWebsubInitializer testSubject;
		boolean result;

		// default test
		testSubject = createTestSubject();
		result = ReflectionTestUtils.invokeMethod(testSubject, "isCacheEnabled");
		assertTrue(result);
	}
	
	@Test
	public void testIsCacheEnabled_TypeNone() throws Exception {
		CacheUpdatingWebsubInitializer testSubject;
		boolean result;

		// default test
		testSubject = createTestSubject();
		ReflectionTestUtils.setField(testSubject, "cacheType", "none");
		result = ReflectionTestUtils.invokeMethod(testSubject, "isCacheEnabled");
		assertFalse(result);
	}
	
}	