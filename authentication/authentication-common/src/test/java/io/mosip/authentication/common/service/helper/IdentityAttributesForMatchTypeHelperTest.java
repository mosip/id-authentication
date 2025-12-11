package io.mosip.authentication.common.service.helper;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IdentityAttributesForMatchTypeHelperTest {

    @InjectMocks
    private IdentityAttributesForMatchTypeHelper helper;

    private IdentityAttributesForMatchTypeHelper spyHelper;

    @Mock
    private IDAMappingConfig idMappingConfig;

    @Mock
    private MatchType matchType;

    @Mock
    private IdMapping idMapping;

    @Mock
    private MatchType.Category category;

    private List<String> validMappings;
    private String validIdName;

    @Before
    public void setUp() {
        // Spy the helper
        spyHelper = spy(helper);

        // Test data
        validMappings = Arrays.asList("mapping1", "mapping2");
        validIdName = "validIdName";

        // Common category setup
        when(matchType.getCategory()).thenReturn(category);
        when(category.getType()).thenReturn("demo");
    }

    // ==================== getIdMappingValue ====================

    @Test
    public void testGetIdMappingValue_ValidMappings_ReturnsList() throws Exception {
        when(idMapping.getMappingFunction()).thenReturn((config, mt) -> validMappings);

        // Use actual enum values; no static mocking
        List<String> result = spyHelper.getIdMappingValue(idMapping, matchType);

        assertEquals(2, result.size());
        assertTrue(result.contains("mapping1"));
        assertTrue(result.contains("mapping2"));
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void testGetIdMappingValue_NullMapping_ThrowsException() throws Exception {
        when(idMapping.getMappingFunction()).thenReturn((config, mt) -> Arrays.asList((String) null));

        spyHelper.getIdMappingValue(idMapping, matchType);
    }

    // ==================== getIdentityAttributesForMatchType ====================

    @Test
    public void testGetIdentityAttributesForMatchType_Static_MatchingIdName() throws Exception {
        when(matchType.isDynamic()).thenReturn(false);
        when(matchType.getIdMapping()).thenReturn(idMapping);
        when(idMapping.getIdname()).thenReturn(validIdName);

        doReturn(validMappings).when(spyHelper).getIdMappingValue(idMapping, matchType);

        List<String> result = spyHelper.getIdentityAttributesForMatchType(matchType, validIdName);

        assertEquals(validMappings.size(), result.size());
        assertTrue(result.contains("mapping1"));
    }

    @Test
    public void testGetIdentityAttributesForMatchType_Static_ExceptionHandled() throws Exception {
        when(matchType.isDynamic()).thenReturn(false);
        when(matchType.getIdMapping()).thenReturn(idMapping);
        when(idMapping.getIdname()).thenReturn(validIdName);

        doThrow(new IdAuthenticationBusinessException("ERR", "msg"))
                .when(spyHelper).getIdMappingValue(idMapping, matchType);

        List<String> result = spyHelper.getIdentityAttributesForMatchType(matchType, validIdName);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetIdentityAttributesForMatchType_Static_IdNameMismatch() {
        when(matchType.isDynamic()).thenReturn(false);
        when(matchType.getIdMapping()).thenReturn(idMapping);
        when(idMapping.getIdname()).thenReturn("otherId");

        List<String> result = spyHelper.getIdentityAttributesForMatchType(matchType, validIdName);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetIdentityAttributesForMatchType_Dynamic_ExistsInConfig() {
        when(matchType.isDynamic()).thenReturn(true);
        Map<String, List<String>> dynamicMap = new HashMap<>();
        dynamicMap.put(validIdName, Arrays.asList("dynamic1", "dynamic2"));
        when(idMappingConfig.getDynamicAttributes()).thenReturn(dynamicMap);

        List<String> result = spyHelper.getIdentityAttributesForMatchType(matchType, validIdName);

        assertEquals(2, result.size());
        assertTrue(result.contains("dynamic1"));
        assertTrue(result.contains("dynamic2"));
    }

    @Test
    public void testGetIdentityAttributesForMatchType_Dynamic_NotInConfig() {
        when(matchType.isDynamic()).thenReturn(true);
        when(idMappingConfig.getDynamicAttributes()).thenReturn(new HashMap<>());

        List<String> result = spyHelper.getIdentityAttributesForMatchType(matchType, validIdName);

        assertEquals(1, result.size());
        assertEquals(validIdName, result.get(0));
    }
}
