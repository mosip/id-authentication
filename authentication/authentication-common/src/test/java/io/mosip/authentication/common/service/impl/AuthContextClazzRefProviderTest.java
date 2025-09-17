package io.mosip.authentication.common.service.impl;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.authtype.acramr.AuthMethodsRefValues;
import io.mosip.authentication.core.spi.authtype.acramr.AuthenticationFactor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
public class AuthContextClazzRefProviderTest {

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AuthContextClazzRefProvider provider;

    private final String mockUri = "http://mock-uri/config.json";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(provider, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(provider, "amracrMappingUri", mockUri);
    }

    @Test
    public void testInitSuccess() throws Exception {

        String mockJson = """
                {
                  "acr_amr": {
                    "acr1": ["pwd"]
                  },
                  "amr": {
                    "pwd": [
                      { "type": "PASSWORD", "count": 1, "subTypes": [] }
                    ]
                  }
                }
                """;

        when(restTemplate.getForObject(mockUri, String.class)).thenReturn(mockJson);

        provider.init();

        AuthMethodsRefValues result = provider.getAuthMethodsRefValues();
        assertNotNull(result);

        Map<String, List<AuthenticationFactor>> map = result.getAuthMethodsRefValues();
        assertTrue(map.containsKey("acr1"));
        AuthenticationFactor factor = map.get("acr1").get(0);

        assertEquals("PASSWORD", factor.getType());
        assertEquals(1, factor.getCount());
        assertTrue(factor.getSubTypes().isEmpty());
    }

    @Test
    public void testInitFailure_InvalidJson() {
        String badJson = "{ invalid-json }";
        when(restTemplate.getForObject(mockUri, String.class)).thenReturn(badJson);

        try {
            provider.init();
            fail("Expected IdAuthenticationBusinessException");
        } catch (IdAuthenticationBusinessException ex) {
            assertEquals(IdAuthenticationErrorConstants.DOWNLOAD_ERROR.getErrorCode(), ex.getErrorCode());
            assertEquals(IdAuthenticationErrorConstants.DOWNLOAD_ERROR.getErrorMessage(), ex.getErrorText());
        }
    }

    @Test
    public void testGetterBeforeInit() {
        assertNull(provider.getAuthMethodsRefValues());
    }
}