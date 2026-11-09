package io.mosip.authentication.common.service.filter;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.partner.dto.AuthPolicy;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultInternalFilterTest {

    private DefaultInternalFilter filter;

    @Before
    public void setUp() {
        filter = new DefaultInternalFilter();
    }

    @Test
    public void testDecipherAndValidateRequest() throws Exception {
        ResettableStreamHttpServletRequest req = mock(ResettableStreamHttpServletRequest.class);
        Map<String, Object> body = new HashMap<>();
        filter.decipherAndValidateRequest(req, body); // no exception, coverage
    }

    @Test
    public void testCheckAllowedAuthTypeBasedOnPolicy() throws Exception {
        Map<String, Object> body = new HashMap<>();
        List<AuthPolicy> policies = new ArrayList<>();
        filter.checkAllowedAuthTypeBasedOnPolicy(body, policies); // no-op
    }

    @Test
    public void testCheckMandatoryAuthTypeBasedOnPolicy() throws Exception {
        Map<String, Object> body = new HashMap<>();
        List<AuthPolicy> policies = new ArrayList<>();
        filter.checkMandatoryAuthTypeBasedOnPolicy(body, policies); // no-op
    }

    @Test
    public void testRemoveNullOrEmptyFieldsInResponse() {
        Map<String, Object> nested = new HashMap<>();
        nested.put("innerKey", null);

        Map<String, Object> map = new HashMap<>();
        map.put("key1", null);
        map.put("key2", new ArrayList<>());
        map.put("key3", "value3");
        map.put("key4", nested);

        Map<String, Object> result = filter.removeNullOrEmptyFieldsInResponse(map);

        assertEquals(2, result.size());
        assertEquals("value3", result.get("key3"));
        assertTrue(result.containsKey("key4"));
        assertTrue(((Map<?, ?>) result.get("key4")).isEmpty());
    }

    @Test
    public void testFetchIdOtp() {
        ResettableStreamHttpServletRequest req = mock(ResettableStreamHttpServletRequest.class);
        when(req.getRequestURL()).thenReturn(new StringBuffer("/internal/otp/somepath"));

        String id = filter.fetchId(req, "attribute");
        assertEquals("attribute" + IdAuthConfigKeyConstants.OTP_INTERNAL_ID_SUFFIX, id);
    }

    @Test
    public void testFetchIdOtherPath() {
        ResettableStreamHttpServletRequest req = mock(ResettableStreamHttpServletRequest.class);
        when(req.getRequestURL()).thenReturn(new StringBuffer("/internal/unknown/somepath"));

        String id = filter.fetchId(req, "attr");
        assertNull(id);
    }

    @Test
    public void testNeedStoreAuthTransaction() {
        assertFalse(filter.needStoreAuthTransaction());
    }

    @Test
    public void testNeedStoreAnonymousProfile() {
        assertFalse(filter.needStoreAnonymousProfile());
    }
}
