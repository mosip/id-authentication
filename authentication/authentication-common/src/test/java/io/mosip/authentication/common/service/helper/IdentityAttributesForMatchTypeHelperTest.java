package io.mosip.authentication.common.service.helper;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdentityAttributesForMatchTypeHelperTest {

    @Mock
    private IDAMappingConfig idMappingConfig;

    private IdentityAttributesForMatchTypeHelper helper;

    @Before
    public void setUp() {
        helper = new IdentityAttributesForMatchTypeHelper();
        ReflectionTestUtils.setField(helper, "idMappingConfig", idMappingConfig);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void getIdMappingValue_throws_whenMappingsNull() throws Exception {
        MatchType matchType = mock(MatchType.class);
        when(matchType.getCategory()).thenReturn(MatchType.Category.DEMO);

        IdMapping idMapping = mock(IdMapping.class);
        @SuppressWarnings("unchecked")
        BiFunction<?, ?, List<String>> func = (cfg, mt) -> null;
        when(idMapping.getMappingFunction()).thenReturn((BiFunction) func);

        helper.getIdMappingValue(idMapping, matchType);
    }

    @Test(expected = NullPointerException.class)
    public void getIdMappingValue_throws_whenMappingContainsNull() throws Exception {
        MatchType matchType = mock(MatchType.class);
        when(matchType.getCategory()).thenReturn(MatchType.Category.DEMO);

        IdMapping idMapping = mock(IdMapping.class);
        when(idMapping.getMappingFunction()).thenReturn((cfg, mt) -> List.of((String) null));

        helper.getIdMappingValue(idMapping, matchType);
    }

    @Test
    public void getIdMappingValue_recursesIntoDynamicIdMapping() throws Exception {
        when(idMappingConfig.getDynamicAttributes()).thenReturn(Map.of("dynKey", List.of("a", "b")));

        MatchType matchType = mock(MatchType.class);
        when(matchType.getCategory()).thenReturn(MatchType.Category.DEMO);

        IdMapping idMapping = mock(IdMapping.class);
        when(idMapping.getMappingFunction()).thenReturn((cfg, mt) -> List.of("dynKey"));

        List<String> result = helper.getIdMappingValue(idMapping, matchType);
        assertEquals(List.of("a", "b"), result);
    }

    @Test
    public void getIdentityAttributesForMatchType_returnsEmpty_whenIdNameMismatch() {
        MatchType matchType = mock(MatchType.class);
        when(matchType.isDynamic()).thenReturn(false);

        IdMapping idMapping = mock(IdMapping.class);
        when(idMapping.getIdname()).thenReturn("name");
        when(matchType.getIdMapping()).thenReturn(idMapping);

        List<String> result = helper.getIdentityAttributesForMatchType(matchType, "different");
        assertTrue(result.isEmpty());
    }

    @Test
    public void getIdentityAttributesForMatchType_returnsEmpty_whenGetIdMappingValueThrows() throws Exception {
        IdentityAttributesForMatchTypeHelper spyHelper = spy(helper);

        MatchType matchType = mock(MatchType.class);
        when(matchType.isDynamic()).thenReturn(false);

        IdMapping idMapping = mock(IdMapping.class);
        when(idMapping.getIdname()).thenReturn("name");
        when(matchType.getIdMapping()).thenReturn(idMapping);

        doThrow(new IdAuthenticationBusinessException("E", "m")).when(spyHelper).getIdMappingValue(idMapping, matchType);

        List<String> result = spyHelper.getIdentityAttributesForMatchType(matchType, "name");
        assertTrue(result.isEmpty());
    }

    @Test
    public void getIdentityAttributesForMatchType_returnsFromGetIdMappingValue_whenNotDynamicAndNameMatches() throws Exception {
        IdentityAttributesForMatchTypeHelper spyHelper = spy(helper);

        MatchType matchType = mock(MatchType.class);
        when(matchType.isDynamic()).thenReturn(false);

        IdMapping idMapping = mock(IdMapping.class);
        when(idMapping.getIdname()).thenReturn("name");
        when(matchType.getIdMapping()).thenReturn(idMapping);

        doReturn(List.of("fullName")).when(spyHelper).getIdMappingValue(idMapping, matchType);

        List<String> result = spyHelper.getIdentityAttributesForMatchType(matchType, "name");
        assertEquals(List.of("fullName"), result);
    }

    @Test
    public void getIdentityAttributesForMatchType_dynamic_returnsConfiguredListWhenPresent() {
        when(idMappingConfig.getDynamicAttributes()).thenReturn(Map.of("residenceStatus", List.of("residenceStatus")));

        MatchType matchType = mock(MatchType.class);
        when(matchType.isDynamic()).thenReturn(true);

        IdMapping idMapping = mock(IdMapping.class);

        List<String> result = helper.getIdentityAttributesForMatchType(matchType, "residenceStatus");
        assertEquals(List.of("residenceStatus"), result);
    }

    @Test
    public void getIdentityAttributesForMatchType_dynamic_returnsIdNameWhenNotConfigured() {
        when(idMappingConfig.getDynamicAttributes()).thenReturn(Map.of());

        MatchType matchType = mock(MatchType.class);
        when(matchType.isDynamic()).thenReturn(true);

        IdMapping idMapping = mock(IdMapping.class);

        List<String> result = helper.getIdentityAttributesForMatchType(matchType, "newAttribute");
        assertEquals(List.of("newAttribute"), result);
    }
}


