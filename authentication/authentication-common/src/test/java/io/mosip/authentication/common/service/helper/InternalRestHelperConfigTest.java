package io.mosip.authentication.common.service.helper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;

public class InternalRestHelperConfigTest {

    private InternalRestHelperConfig config;

    @Before
    public void setUp() {
        config = new InternalRestHelperConfig();
    }

    @Test
    public void testRestHelperDefaultBean() {
        RestHelper helper = config.restHelper();
        assertNotNull(helper);
        assertTrue(helper instanceof RestHelper);
    }

    @Test
    public void testRestHelperWithAuth() {
        WebClient mockWebClient = mock(WebClient.class);

        RestHelper helper = config.restHelperWithAuth(mockWebClient);

        assertNotNull(helper);
        assertTrue(helper instanceof RestHelper);
    }
}

