package io.mosip.authentication.common.service.filter;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ExternalAuthFilterTest {

    private ExternalAuthFilter filter;

    @Before
    public void setUp() {
        filter = new ExternalAuthFilter();
    }

    @Test
    public void testFetchId() {
        ResettableStreamHttpServletRequest request = mock(ResettableStreamHttpServletRequest.class);
        String attribute = "attribute";
        String id = filter.fetchId(request, attribute);
        assertEquals("attributeauth", id);
    }

    @Test
    public void testNeedStoreAuthTransaction() {
        assertTrue(filter.needStoreAuthTransaction());
    }

    @Test
    public void testNeedStoreAnonymousProfile() {
        assertTrue(filter.needStoreAnonymousProfile());
    }

    @Test
    public void testIsMispPolicyValidationRequired() {
        assertFalse(filter.isMispPolicyValidationRequired());
    }

    @Test
    public void testIsCertificateValidationRequired() {
        assertTrue(filter.isCertificateValidationRequired());
    }

    @Test
    public void testIsAMRValidationRequired() {
        assertFalse(filter.isAMRValidationRequired());
    }
}
