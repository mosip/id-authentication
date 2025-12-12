package io.mosip.authentication.internal.service.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class InternalAuthFilterFactoryTest {

	@InjectMocks
	private InternalAuthFilterFactory internalAuthFilterFactory;
	
	@Before
	public void before() {
		// Setup if needed
	}
	
	@Test
	public void testGetMosipAuthFilterClasses() {
		String[] filterClasses = {"filter1", "filter2", "filter3"};
		ReflectionTestUtils.setField(internalAuthFilterFactory, "mosipAuthFilterClasses", filterClasses);
		
		String[] result = internalAuthFilterFactory.getMosipAuthFilterClasses();
		
		assertNotNull("Result should not be null", result);
		assertArrayEquals("Filter classes should match", filterClasses, result);
	}
	
	@Test
	public void testGetMosipAuthFilterClassesWithEmptyArray() {
		String[] emptyArray = {};
		ReflectionTestUtils.setField(internalAuthFilterFactory, "mosipAuthFilterClasses", emptyArray);
		
		String[] result = internalAuthFilterFactory.getMosipAuthFilterClasses();
		
		assertNotNull("Result should not be null", result);
		assertEquals("Should return empty array", 0, result.length);
	}
	
	@Test
	public void testGetMosipAuthFilterClassesWithNull() {
		ReflectionTestUtils.setField(internalAuthFilterFactory, "mosipAuthFilterClasses", (String[]) null);
		
		String[] result = internalAuthFilterFactory.getMosipAuthFilterClasses();
		
		// Should handle null gracefully
		assertTrue("Should handle null case", result == null || result.length == 0);
	}
}
