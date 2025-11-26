package io.mosip.authentication.service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CustomEnvEndpointTest {

    private CustomEnvEndpoint customEnvEndpoint;

    @BeforeEach
    void setUp() {
        Environment environment = Mockito.mock(Environment.class);
        customEnvEndpoint = new CustomEnvEndpoint(environment);
    }

    @Test
    void testStringifyWithList() {
        List<String> data = Arrays.asList("one", "two");
        Object result = customEnvEndpoint.stringifyIfNecessary(data);

        assertTrue(result instanceof List);
        assertEquals(data, result);
    }

    @Test
    void testStringifyWithSet() {
        Set<String> set = new HashSet<>(Arrays.asList("a", "b"));
        Object result = customEnvEndpoint.stringifyIfNecessary(set);

        assertTrue(result instanceof String);
        assertTrue(result.toString().contains("a") || result.toString().contains("b"));
    }

    @Test
    void testStringifyWithComplexObject() {
        Object obj = new Object();  // not primitive or simple wrapper
        Object result = customEnvEndpoint.stringifyIfNecessary(obj);

        assertSame(obj, result);
    }

    @Test
    void testStringifyWithPrimitiveWrapper() {
        Object result = customEnvEndpoint.stringifyIfNecessary(123);
        assertEquals(123, result);

        result = customEnvEndpoint.stringifyIfNecessary(true);
        assertEquals(true, result);

        result = customEnvEndpoint.stringifyIfNecessary("hello");
        assertEquals("hello", result);

        result = customEnvEndpoint.stringifyIfNecessary('c');
        assertEquals('c', result);
    }

    @Test
    void testStringifyWithNull() {
        Object result = customEnvEndpoint.stringifyIfNecessary(null);
        assertNull(result);
    }
}
