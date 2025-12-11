package io.mosip.authentication.core.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MasterDataFetcher;
import io.mosip.kernel.demographics.spi.IDemoNormalizer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DemoNormalizerTest {

	@Mock
	private IDemoNormalizer iDemoNormalizer;
	
	@Mock
	private MasterDataFetcher masterDataFetcher;
	
	@InjectMocks
	private DemoNormalizer demoNormalizer;

	/**
	 * Test normalize name
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void testNormalizeName() throws IdAuthenticationBusinessException {
		String nameInfo = "John Doe";
		String language = "eng";
		String normalizedName = "JOHN DOE";
		
		when(masterDataFetcher.get()).thenReturn(null);
		when(iDemoNormalizer.normalizeName(anyString(), anyString(), any())).thenReturn(normalizedName);
		
		ReflectionTestUtils.setField(demoNormalizer, "iDemoNormalizer", iDemoNormalizer);
		
		String result = demoNormalizer.normalizeName(nameInfo, language, masterDataFetcher);
		
		assertNotNull(result);
		assertEquals(normalizedName, result);
	}

	@Test
	public void testNormalizeAddress() {
		String address = "123 Main St, City, State";
		String language = "eng";
		String normalizedAddress = "123 MAIN ST CITY STATE";
		
		when(iDemoNormalizer.normalizeAddress(anyString(), anyString())).thenReturn(normalizedAddress);
		ReflectionTestUtils.setField(demoNormalizer, "iDemoNormalizer", iDemoNormalizer);
		
		String result = demoNormalizer.normalizeAddress(address, language);
		
		assertNotNull(result);
		assertEquals(normalizedAddress, result);
	}
	
	/**
	 * Test normalize address with empty string
	 */
	@Test
	public void testNormalizeAddressWithEmptyString() {
		String address = "";
		String language = "eng";
		
		when(iDemoNormalizer.normalizeAddress(anyString(), anyString())).thenReturn("");
		ReflectionTestUtils.setField(demoNormalizer, "iDemoNormalizer", iDemoNormalizer);
		
		String result = demoNormalizer.normalizeAddress(address, language);
		
		assertNotNull(result);
		assertEquals("", result);
	}
	
	/**
	 * Test normalize address with different languages
	 */
	@Test
	public void testNormalizeAddressWithDifferentLanguages() {
		String address = "Test Address";
		String[] languages = {"eng", "ara", "fra"};
		
		when(iDemoNormalizer.normalizeAddress(anyString(), anyString())).thenReturn("TEST ADDRESS");
		ReflectionTestUtils.setField(demoNormalizer, "iDemoNormalizer", iDemoNormalizer);
		
		for (String lang : languages) {
			String result = demoNormalizer.normalizeAddress(address, lang);
			assertNotNull(result);
		}
	}
}
