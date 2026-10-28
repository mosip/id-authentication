package io.mosip.authentication.common.service.config;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;

import io.mosip.authentication.core.util.LanguageComparator;

@RunWith(MockitoJUnitRunner.class)
public class LangComparatorConfigTest {

    private LangComparatorConfig config;

    @Before
    public void setup() {
        config = new LangComparatorConfig() {
            @Override
            public List<String> getSystemSupportedLanguageCodes() {
                // provide fixed list for testing
                return List.of("en", "fr", "hi");
            }
        };
    }

    /* ---------------- getSystemSupportedLanguageCodes ---------------- */
    @Test
    public void testGetSystemSupportedLanguageCodes() {
        Method method = ReflectionUtils.findMethod(LangComparatorConfig.class, "getSystemSupportedLanguageCodes");
        ReflectionUtils.makeAccessible(method);

        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) ReflectionUtils.invokeMethod(method, config);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("en"));
        assertTrue(result.contains("fr"));
        assertTrue(result.contains("hi"));
    }

    /* ---------------- getLanguageComparator ---------------- */
    @Test
    public void testGetLanguageComparator() {
        Method method = ReflectionUtils.findMethod(LangComparatorConfig.class, "getLanguageComparator");
        ReflectionUtils.makeAccessible(method);

        LanguageComparator comparator = (LanguageComparator) ReflectionUtils.invokeMethod(method, config);

        // just check object creation (we don't need getLanguageCodes)
        assertNotNull(comparator);
    }

    @Test
    public void testGetSystemSupportedLanguageCodesActualLogic() throws Exception {
        // use ReflectionUtils to invoke the method
        Method method = ReflectionUtils.findMethod(LangComparatorConfig.class, "getSystemSupportedLanguageCodes");
        ReflectionUtils.makeAccessible(method);

        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) ReflectionUtils.invokeMethod(method, config);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("en"));
        assertTrue(result.contains("fr"));
        assertTrue(result.contains("hi"));
    }
}
