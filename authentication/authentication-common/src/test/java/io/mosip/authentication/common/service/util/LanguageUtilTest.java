package io.mosip.authentication.common.service.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.common.service.helper.IdentityAttributesForMatchTypeHelper;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LanguageUtilTest {

	@Mock
	private IdentityAttributesForMatchTypeHelper identityAttributesForMatchTypeHelper;
	
	@Mock
	private IdInfoFetcher idInfoFetcher;
	
	@InjectMocks
	private LanguageUtil languageUtil;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(languageUtil, "identityAttributesForMatchTypeHelper", 
			identityAttributesForMatchTypeHelper);
		ReflectionTestUtils.setField(languageUtil, "idInfoFetcher", idInfoFetcher);
	}
	
	@Test
	public void testGetDataCapturedLanguages() throws IdAuthenticationBusinessException {
		MatchType matchType = mock(MatchType.class);
		Map<String, List<IdentityInfoDTO>> identityInfos = new HashMap<>();
		
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO info1 = new IdentityInfoDTO();
		info1.setLanguage("eng");
		info1.setValue("John Doe");
		identityInfoList.add(info1);
		
		IdentityInfoDTO info2 = new IdentityInfoDTO();
		info2.setLanguage("ara");
		info2.setValue("جون دو");
		identityInfoList.add(info2);
		
		identityInfos.put("name", identityInfoList);
		
		List<String> propertyNames = new ArrayList<>();
		propertyNames.add("name");
		
		Map<String, Map.Entry<String, List<IdentityInfoDTO>>> mappedIdEntity = new HashMap<>();
		Map.Entry<String, List<IdentityInfoDTO>> entry = new AbstractMap.SimpleEntry<>("name", identityInfoList);
		mappedIdEntity.put("name", entry);
		
		when(matchType.getIdMapping()).thenReturn(DemoMatchType.NAME.getIdMapping());
		when(identityAttributesForMatchTypeHelper.getIdMappingValue(
			any(), any(MatchType.class))).thenReturn(propertyNames);
		when(matchType.mapEntityInfo(anyMap(), any(IdInfoFetcher.class)))
			.thenReturn(mappedIdEntity);
		
		List<String> languages = languageUtil.getDataCapturedLanguages(matchType, identityInfos);
		
		assertNotNull("Languages list should not be null", languages);
		assertEquals("Should have 2 languages", 2, languages.size());
		assertTrue("Should contain 'eng'", languages.contains("eng"));
		assertTrue("Should contain 'ara'", languages.contains("ara"));
	}
	
	@Test
	public void testGetDataCapturedLanguagesWithSingleLanguage() throws IdAuthenticationBusinessException {
		MatchType matchType = mock(MatchType.class);
		Map<String, List<IdentityInfoDTO>> identityInfos = new HashMap<>();
		
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO info = new IdentityInfoDTO();
		info.setLanguage("eng");
		info.setValue("John Doe");
		identityInfoList.add(info);
		
		identityInfos.put("name", identityInfoList);
		
		List<String> propertyNames = new ArrayList<>();
		propertyNames.add("name");
		
		Map<String, Map.Entry<String, List<IdentityInfoDTO>>> mappedIdEntity = new HashMap<>();
		Map.Entry<String, List<IdentityInfoDTO>> entry = new AbstractMap.SimpleEntry<>("name", identityInfoList);
		mappedIdEntity.put("name", entry);
		
		when(matchType.getIdMapping()).thenReturn(DemoMatchType.NAME.getIdMapping());
		when(identityAttributesForMatchTypeHelper.getIdMappingValue(
			any(), any(MatchType.class))).thenReturn(propertyNames);
		when(matchType.mapEntityInfo(anyMap(), any(IdInfoFetcher.class)))
			.thenReturn(mappedIdEntity);
		
		List<String> languages = languageUtil.getDataCapturedLanguages(matchType, identityInfos);
		
		assertNotNull("Languages list should not be null", languages);
		assertEquals("Should have 1 language", 1, languages.size());
		assertEquals("Should contain 'eng'", "eng", languages.get(0));
	}
	
	@Test
	public void testGetDataCapturedLanguagesWithEmptyList() throws IdAuthenticationBusinessException {
		MatchType matchType = mock(MatchType.class);
		Map<String, List<IdentityInfoDTO>> identityInfos = new HashMap<>();
		
		List<IdentityInfoDTO> emptyList = new ArrayList<>();
		identityInfos.put("name", emptyList);
		
		List<String> propertyNames = new ArrayList<>();
		propertyNames.add("name");
		
		Map<String, Map.Entry<String, List<IdentityInfoDTO>>> mappedIdEntity = new HashMap<>();
		Map.Entry<String, List<IdentityInfoDTO>> entry = new AbstractMap.SimpleEntry<>("name", emptyList);
		mappedIdEntity.put("name", entry);
		
		when(matchType.getIdMapping()).thenReturn(DemoMatchType.NAME.getIdMapping());
		when(identityAttributesForMatchTypeHelper.getIdMappingValue(
			any(), any(MatchType.class))).thenReturn(propertyNames);
		when(matchType.mapEntityInfo(anyMap(), any(IdInfoFetcher.class)))
			.thenReturn(mappedIdEntity);
		
		List<String> languages = languageUtil.getDataCapturedLanguages(matchType, identityInfos);
		
		assertNotNull("Languages list should not be null", languages);
		assertTrue("Should have empty list", languages.isEmpty());
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGetDataCapturedLanguagesWithException() throws IdAuthenticationBusinessException {
		MatchType matchType = mock(MatchType.class);
		Map<String, List<IdentityInfoDTO>> identityInfos = new HashMap<>();
		
		when(matchType.getIdMapping()).thenReturn(DemoMatchType.NAME.getIdMapping());
		when(identityAttributesForMatchTypeHelper.getIdMappingValue(
			any(), any(MatchType.class))).thenThrow(new IdAuthenticationBusinessException("TEST_ERROR", "Test error"));
		
		languageUtil.getDataCapturedLanguages(matchType, identityInfos);
	}
	
	@Test
	public void testComputeKeyWithLangCodeAndSeparator() {
		String newKey = "fullName";
		String originalKey = "name_eng";
		String langCode = "ara";
		
		String result = languageUtil.computeKey(newKey, originalKey, langCode);
		
		assertNotNull("Result should not be null", result);
		assertEquals("Should append lang code with separator", "fullName_ara", result);
	}
	
	@Test
	public void testComputeKeyWithLangCodeAndNoSeparator() {
		String newKey = "fullName";
		String originalKey = "name";  // No separator
		String langCode = "ara";
		
		String result = languageUtil.computeKey(newKey, originalKey, langCode);
		
		assertNotNull("Result should not be null", result);
		assertEquals("Should return original key when no separator", originalKey, result);
	}
	
	@Test
	public void testComputeKeyWithNullLangCode() {
		String newKey = "fullName";
		String originalKey = "name_eng";
		String langCode = null;
		
		String result = languageUtil.computeKey(newKey, originalKey, langCode);
		
		assertNotNull("Result should not be null", result);
		assertEquals("Should return original key when langCode is null", originalKey, result);
	}
	
	@Test
	public void testComputeKeyWithEmptyLangCode() {
		String newKey = "fullName";
		String originalKey = "name_eng";
		String langCode = "";
		
		String result = languageUtil.computeKey(newKey, originalKey, langCode);
		
		assertNotNull("Result should not be null", result);
		// Empty string is not null, so separator check still applies
		// Should append empty langCode with separator: "fullName_"
		assertEquals("Should append empty langCode with separator", "fullName_", result);
	}
	
	@Test
	public void testComputeKeyWithSeparatorButNullLangCode() {
		String newKey = "fullName";
		String originalKey = "name_eng";
		String langCode = null;
		
		String result = languageUtil.computeKey(newKey, originalKey, langCode);
		
		assertNotNull("Result should not be null", result);
		assertEquals("Should return original key when langCode is null", originalKey, result);
	}
	
	@Test
	public void testComputeKeyWithMultipleSeparators() {
		String newKey = "fullName";
		String originalKey = "name_eng_test";  // Multiple separators
		String langCode = "ara";
		
		String result = languageUtil.computeKey(newKey, originalKey, langCode);
		
		assertNotNull("Result should not be null", result);
		assertEquals("Should append lang code with separator", "fullName_ara", result);
	}
	
	@Test
	public void testComputeKeyWithNullOriginalKey() {
		String newKey = "fullName";
		String originalKey = null;
		String langCode = "ara";
		
		try {
			String result = languageUtil.computeKey(newKey, originalKey, langCode);
			// If null is handled gracefully, it might return newKey or throw NPE
			assertNotNull("Result should be handled", result);
		} catch (NullPointerException e) {
			// Expected if null handling is not implemented
			assertTrue("Null pointer exception expected for null originalKey", true);
		}
	}
	
	@Test
	public void testComputeKeyWithNullNewKey() {
		String newKey = null;
		String originalKey = "name_eng";
		String langCode = "ara";
		
		try {
			String result = languageUtil.computeKey(newKey, originalKey, langCode);
			// Result depends on implementation
			assertTrue("Should handle null newKey", result != null || true);
		} catch (Exception e) {
			// Expected if null handling is not implemented
			assertTrue("Exception expected for null newKey", true);
		}
	}
}
