package io.mosip.authentication.common.service.config;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

public class DemoAuthConfigTest {

    private DemoAuthConfig config;

    @Before
    public void setup() {
        config = new DemoAuthConfig();
    }

    private void setField(String name, String value) throws Exception {
        Field f = DemoAuthConfig.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(config, value);
    }

    /* ---------------- DemoApi ---------------- */

    @Test
    public void testGetDemoApiSDKInstanceClassNotFound() throws Exception {
        setField("demosdkClassName", "invalid.demo.Api.Class");

        try {
            config.getDemoApiSDKInstance();
            fail("Expected ClassNotFoundException");
        } catch (ClassNotFoundException e) {
            assertTrue(e.getMessage().contains("invalid.demo.Api.Class"));
        }
    }

    @Test
    public void testGetDemoApiSDKInstanceNoDefaultConstructor() throws Exception {
        // java.lang.Integer exists but has NO no-arg constructor
        setField("demosdkClassName", Integer.class.getName());

        // ReflectionUtils.findConstructor() returns empty
        assertNull(config.getDemoApiSDKInstance());
    }

    /* ---------------- Normalizer ---------------- */

    @Test
    public void testGetNormalizerSDKInstanceClassNotFound() throws Exception {
        setField("normalizerClassName", "invalid.normalizer.Class");

        try {
            config.getNormalizerSDKInstance();
            fail("Expected ClassNotFoundException");
        } catch (ClassNotFoundException e) {
            assertTrue(e.getMessage().contains("invalid.normalizer.Class"));
        }
    }

    @Test(expected = ClassCastException.class)
    public void testGetNormalizerSDKInstanceInvalidType() throws Exception {
        setField("normalizerClassName", String.class.getName());
        config.getNormalizerSDKInstance();
    }

}
