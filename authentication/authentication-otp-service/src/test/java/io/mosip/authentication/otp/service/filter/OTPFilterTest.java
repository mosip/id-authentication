package io.mosip.authentication.otp.service.filter;


import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import io.mosip.authentication.common.service.filter.ResettableStreamHttpServletRequest;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.partner.dto.AuthPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The Class OTPFilterTest.
 */

public class OTPFilterTest {

    // Subclass to expose protected methods
    static class TestableOTPFilter extends OTPFilter {
        @Override
        public void checkAllowedAuthTypeBasedOnPolicy(Map<String, Object> requestBody, List<AuthPolicy> authPolicies)
                throws IdAuthenticationAppException {
            super.checkAllowedAuthTypeBasedOnPolicy(requestBody, authPolicies);
        }

        @Override
        public Map<String, Object> removeNullOrEmptyFieldsInResponse(Map<String, Object> responseMap) {
            return super.removeNullOrEmptyFieldsInResponse(responseMap);
        }

        @Override
        public void checkMandatoryAuthTypeBasedOnPolicy(Map<String, Object> requestBody, List<AuthPolicy> mandatoryAuthPolicies)
                throws IdAuthenticationAppException {
            super.checkMandatoryAuthTypeBasedOnPolicy(requestBody, mandatoryAuthPolicies);
        }

        @Override
        public String fetchId(ResettableStreamHttpServletRequest requestWrapper, String attribute) {
            return super.fetchId(requestWrapper, attribute);
        }

        @Override
        public boolean needStoreAuthTransaction() {
            return super.needStoreAuthTransaction();
        }

        @Override
        public boolean needStoreAnonymousProfile() {
            return super.needStoreAnonymousProfile();
        }
    }

    private final TestableOTPFilter filter = new TestableOTPFilter();

    @org.junit.jupiter.api.Test
    void testRemoveNullOrEmptyFieldsInResponse() {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("a", "value");
        input.put("b", null);
        input.put("c", Collections.emptyList());
        input.put("d", Arrays.asList("x"));
        Map<String, Object> nested = new HashMap<>();
        nested.put("e", null);
        nested.put("f", "val");
        input.put("g", nested);

        Map<String, Object> result = filter.removeNullOrEmptyFieldsInResponse(input);

        Assertions.assertTrue(result.containsKey("a"));
        assertFalse(result.containsKey("b"));
        assertFalse(result.containsKey("c"));
        Assertions.assertTrue(result.containsKey("d"));
        Assertions.assertTrue(result.containsKey("g"));
        assertEquals("val", ((Map<?, ?>) result.get("g")).get("f"));
        assertFalse(((Map<?, ?>) result.get("g")).containsKey("e"));
    }

    @org.junit.jupiter.api.Test
    void testCheckMandatoryAuthTypeBasedOnPolicy() {
        // Should do nothing and not throw
        assertDoesNotThrow(() -> filter.checkMandatoryAuthTypeBasedOnPolicy(new HashMap<>(), new ArrayList<>()));
    }

    @org.junit.jupiter.api.Test
    void testIsSigningRequired() {
        assertTrue(filter.isSigningRequired());
    }

    @org.junit.jupiter.api.Test
    void testIsSignatureVerificationRequired() {
        assertTrue(filter.isSignatureVerificationRequired());
    }

    @org.junit.jupiter.api.Test
    void testIsTrustValidationRequired() {
        assertTrue(filter.isTrustValidationRequired());
    }

    @org.junit.jupiter.api.Test
    void testFetchId() {
        ResettableStreamHttpServletRequest req = null;
        String result = filter.fetchId(req, "attr");
        assertEquals("attrotp", result);
    }

    @org.junit.jupiter.api.Test
    void testNeedStoreAuthTransaction() {
        Assertions.assertTrue(filter.needStoreAuthTransaction());
    }

    @org.junit.jupiter.api.Test
    void testNeedStoreAnonymousProfile() {
        assertFalse(filter.needStoreAnonymousProfile());
    }

    @org.junit.jupiter.api.Test
    void testIsMispPolicyValidationRequired() {
        assertFalse(filter.isMispPolicyValidationRequired());
    }

    @org.junit.jupiter.api.Test
    void testIsCertificateValidationRequired() {
        assertTrue(filter.isCertificateValidationRequired());
    }

    @Test
    void testIsAMRValidationRequired() {
        assertFalse(filter.isAMRValidationRequired());
    }
}
