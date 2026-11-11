package io.mosip.authentication.service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InternalAuthFilterFactoryTest {

	@InjectMocks
	private InternalAuthFilterFactory internalAuthFilterFactory;
	
	@BeforeEach
	public void before() {
		// Setup if needed
	}
	
	@Test
	public void testGetMosipAuthFilterClasses() {
		String[] filterClasses = {"filter1", "filter2", "filter3"};
		ReflectionTestUtils.setField(internalAuthFilterFactory, "mosipAuthFilterClasses", filterClasses);
		
		String[] result = internalAuthFilterFactory.getMosipAuthFilterClasses();
		
		assertNotNull(result, "Result should not be null");
		assertArrayEquals(filterClasses, result, "Filter classes should match");
	}
	
	@Test
	public void testGetMosipAuthFilterClassesWithEmptyArray() {
		String[] emptyArray = {};
		ReflectionTestUtils.setField(internalAuthFilterFactory, "mosipAuthFilterClasses", emptyArray);
		
		String[] result = internalAuthFilterFactory.getMosipAuthFilterClasses();
		
		assertNotNull(result, "Result should not be null");
		assertEquals(0, result.length, "Should return empty array");
	}
	
	@Test
	public void testGetMosipAuthFilterClassesWithNull() {
		ReflectionTestUtils.setField(internalAuthFilterFactory, "mosipAuthFilterClasses", (String[]) null);
		
		String[] result = internalAuthFilterFactory.getMosipAuthFilterClasses();
		
		// Should handle null gracefully
		assertTrue(result == null || result.length == 0, "Should handle null case");
	}
}
