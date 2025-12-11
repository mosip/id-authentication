package io.mosip.authentication.common.service.util;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.helper.IdentityAttributesForMatchTypeHelper;
import io.mosip.authentication.common.service.helper.SeparatorHelper;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EntityInfoUtilTest {

    @InjectMocks
    private EntityInfoUtil entityInfoUtil;

    @Mock
    private SeparatorHelper separatorHelper;

    @Mock
    private LanguageUtil languageUtil;

    @Mock
    private IdInfoFetcher idInfoFetcher;

    @Mock
    private IDAMappingConfig idMappingConfig;

    @Mock
    private IdentityAttributesForMatchTypeHelper identityAttributesForMatchTypeHelper;

    @Mock
    private MatchType matchType;

    private AutoCloseable mocks;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @After  
    public void tearDown() throws Exception {  
       mocks.close();  
     } 

    @Test
    public void testGetIdEntityInfoMap_withLanguage() throws Exception {
        Map<String, List<IdentityInfoDTO>> idEntity = new HashMap<>();
        IdentityInfoDTO info = new IdentityInfoDTO();
        info.setValue("testValue");
        info.setLanguage("en");
        idEntity.put("name", Arrays.asList(info));

        when(identityAttributesForMatchTypeHelper.getIdentityAttributesForMatchType(matchType, null))
                .thenReturn(Arrays.asList("name"));

        Map<String, Map.Entry<String, List<IdentityInfoDTO>>> mapped = new HashMap<>();
        mapped.put("name", new AbstractMap.SimpleEntry<>("nameKey", Arrays.asList(info)));
        when(matchType.mapEntityInfo(anyMap(), any(IdInfoFetcher.class))).thenReturn(mapped);

        when(matchType.getEntityInfoMapper()).thenReturn((map, props) -> map);

        Map<String, String> result = entityInfoUtil.getIdEntityInfoMap(matchType, idEntity, "en");

        assertNotNull(result);
        assertTrue(result.containsKey("nameKey|en") || result.containsKey("nameKey"));
        assertEquals("testValue", result.values().iterator().next());
    }
    @Test
    public void testGetEntityInfoAsString() throws Exception {
        Map<String, List<IdentityInfoDTO>> idEntity = new HashMap<>();
        IdentityInfoDTO info = new IdentityInfoDTO();
        info.setValue("value1");
        info.setLanguage("en");
        idEntity.put("name", Arrays.asList(info));

        // Mock identityAttributesForMatchTypeHelper
        when(identityAttributesForMatchTypeHelper.getIdentityAttributesForMatchType(matchType, null))
                .thenReturn(Arrays.asList("name"));

        // Mock mapEntityInfo
        Map<String, Map.Entry<String, List<IdentityInfoDTO>>> mapped = new HashMap<>();
        mapped.put("name", new AbstractMap.SimpleEntry<>("key1", Arrays.asList(info)));
        when(matchType.mapEntityInfo(anyMap(), any(IdInfoFetcher.class))).thenReturn(mapped);

        // Mock entityInfoMapper
        when(matchType.getEntityInfoMapper()).thenReturn((map, props) -> map);

        // Mock IdMapping and getIdMapping
        IdMapping idMapping = mock(IdMapping.class);
        when(idMapping.getIdname()).thenReturn("idName");
        when(matchType.getIdMapping()).thenReturn(idMapping);

        // Mock separatorHelper
        when(separatorHelper.getSeparator("idName")).thenReturn("|");

        // Call the method
        Map<String, String> map = entityInfoUtil.getEntityInfoAsStringWithKey(matchType, "en", idEntity, null);

        // Assertions
        assertNotNull(map);
        assertTrue(map.values().iterator().next().contains("value1"));
    }

    @Test
    public void testConcatValues() throws Exception {
        Method method = EntityInfoUtil.class.getDeclaredMethod("concatValues", String.class, String[].class);
        method.setAccessible(true);
        String result = (String) method.invoke(entityInfoUtil, "|", new String[]{"a", "b", "c"});
        assertEquals("a|b|c", result);
    }

    @Test
    public void testMergeNonNullValues() throws Exception {
        Method method = EntityInfoUtil.class.getDeclaredMethod("mergeNonNullValues", Map.class, Map.class);
        method.setAccessible(true);

        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("a", "1");
        map1.put("b", null);

        Map<String, String> map2 = new LinkedHashMap<>();
        map2.put("b", "2");
        map2.put("c", "3");

        @SuppressWarnings("unchecked")
        Map<String, String> merged = (Map<String, String>) method.invoke(entityInfoUtil, map1, map2);

        assertEquals(3, merged.size());
        assertEquals("1", merged.get("a"));
        assertEquals("2", merged.get("b"));
        assertEquals("3", merged.get("c"));
    }

    @Test
    public void testGetIdentityValueFromMap() throws Exception {
        // Prepare test data
        Map<String, Map.Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
        IdentityInfoDTO info = new IdentityInfoDTO();
        info.setValue("val");
        info.setLanguage("en");
        map.put("prop", new AbstractMap.SimpleEntry<>("key", Arrays.asList(info)));

        // Access private method via reflection
        Method method = EntityInfoUtil.class.getDeclaredMethod(
                "getIdentityValueFromMap", String.class, String.class, Map.class, MatchType.class);
        method.setAccessible(true);

        // Mock behavior
        when(idInfoFetcher.checkLanguageType("en", "en")).thenReturn(true);

        // Invoke method
        Stream<String> resultStream = (Stream<String>) method.invoke(entityInfoUtil, "prop", "en", map, matchType);
        List<String> result = Arrays.asList(resultStream.toArray(String[]::new));

        // Assertions
        assertEquals(1, result.size());
        assertEquals("val", result.get(0));
    }

    @Test
    public void testGetEntityInfoAsString_noLang() throws Exception {
        // Prepare test data
        Map<String, List<IdentityInfoDTO>> idEntity = new HashMap<>();
        IdentityInfoDTO info = new IdentityInfoDTO();
        info.setValue("value1");
        info.setLanguage("en");
        idEntity.put("name", Arrays.asList(info));

        // Mock identityAttributesForMatchTypeHelper
        when(identityAttributesForMatchTypeHelper.getIdentityAttributesForMatchType(matchType, null))
                .thenReturn(Arrays.asList("name"));

        // Mock mapEntityInfo
        Map<String, Map.Entry<String, List<IdentityInfoDTO>>> mapped = new HashMap<>();
        mapped.put("name", new AbstractMap.SimpleEntry<>("key1", Arrays.asList(info)));
        when(matchType.mapEntityInfo(anyMap(), any(IdInfoFetcher.class))).thenReturn(mapped);

        // Mock entityInfoMapper
        when(matchType.getEntityInfoMapper()).thenReturn((map, props) -> map);

        // Mock IdMapping and getIdMapping
        IdMapping idMapping = mock(IdMapping.class);
        when(idMapping.getIdname()).thenReturn("idName");
        when(matchType.getIdMapping()).thenReturn(idMapping);

        // Mock separatorHelper
        when(separatorHelper.getSeparator("idName")).thenReturn("|");

        // Call getEntityInfoAsString without language
        String result = entityInfoUtil.getEntityInfoAsString(matchType, idEntity);

        assertNotNull(result);
        assertTrue(result.contains("value1"));
    }

    @Test
    public void testGetEntityInfoAsString_withLang() throws Exception {
        // Prepare test data
        Map<String, List<IdentityInfoDTO>> idEntity = new HashMap<>();
        IdentityInfoDTO info = new IdentityInfoDTO();
        info.setValue("value2");
        info.setLanguage("fr");
        idEntity.put("name", Arrays.asList(info));

        // Mock identityAttributesForMatchTypeHelper
        when(identityAttributesForMatchTypeHelper.getIdentityAttributesForMatchType(matchType, null))
                .thenReturn(Arrays.asList("name"));

        // Mock mapEntityInfo
        Map<String, Map.Entry<String, List<IdentityInfoDTO>>> mapped = new HashMap<>();
        mapped.put("name", new AbstractMap.SimpleEntry<>("key1", Arrays.asList(info)));
        when(matchType.mapEntityInfo(anyMap(), any(IdInfoFetcher.class))).thenReturn(mapped);

        // Mock entityInfoMapper
        when(matchType.getEntityInfoMapper()).thenReturn((map, props) -> map);

        // Mock IdMapping and getIdMapping
        IdMapping idMapping = mock(IdMapping.class);
        when(idMapping.getIdname()).thenReturn("idName");
        when(matchType.getIdMapping()).thenReturn(idMapping);

        // Mock separatorHelper
        when(separatorHelper.getSeparator("idName")).thenReturn("|");

        // Call getEntityInfoAsString with language
        String result = entityInfoUtil.getEntityInfoAsString(matchType, "fr", idEntity);

        assertNotNull(result);
        assertTrue(result.contains("value2"));
    }
}
