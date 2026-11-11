package io.mosip.authentication.service.kyc.filter;


import static org.junit.jupiter.api.Assertions.assertThrows;
import io.mosip.authentication.common.service.filter.IdAuthFilter;
import io.mosip.authentication.common.service.filter.ResettableStreamHttpServletRequest;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.partner.dto.AuthPolicy;
import io.mosip.authentication.core.partner.dto.MispPolicyDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@Import(EnvUtil.class)
public class IdentityKeyBindingFilterTest {

    IdentityKeyBindingFilter filterSpy = Mockito.spy(new IdentityKeyBindingFilter());

    @Test
    public void testIsPartnerCertificateNeeded() {
        Assertions.assertTrue(filterSpy.isPartnerCertificateNeeded());
    }

    @Test
    public void testInvokeProtectedIsAllowedAuthType() throws Exception {
        IdentityKeyBindingFilter filter = new IdentityKeyBindingFilter();

        Method method = IdAuthFilter.class.getDeclaredMethod("isAllowedAuthType", String.class, List.class);
        method.setAccessible(true);

        String authType = "keybinding";
        List<AuthPolicy> authPolicies = Collections.emptyList();

        boolean allowed = (boolean) method.invoke(filter, authType, authPolicies);

        Assertions.assertFalse(allowed);
    }


    /**
     * We can't directly invoke super since the method is overridden, so we make a small wrapper.
     * This is possible in test via subclassing.
     */
    private static class TestFilter extends IdentityKeyBindingFilter {
        void super_checkAllowedAuthTypeBasedOnPolicy(Map<String,Object> body, List<AuthPolicy> policies) throws IdAuthenticationAppException {
            super.checkAllowedAuthTypeBasedOnPolicy(body, policies);
        }
    }

    @Test
    public void testCheckAllowedAuthTypeBasedOnPolicy_NotAllowed() throws Exception {
		assertThrows(IdAuthenticationAppException.class, () -> {
        IdentityKeyBindingFilter filter = new IdentityKeyBindingFilter() {
            @Override
            protected boolean isAllowedAuthType(String authType, List<AuthPolicy> authPolicies) {
                return false;
            }
        };
        filter.checkAllowedAuthTypeBasedOnPolicy(Map.of(), Collections.emptyList());
    		});
	}

    @Test
    public void testCheckMandatoryAuthTypeBasedOnPolicy_NoOp() throws Exception {
        filterSpy.checkMandatoryAuthTypeBasedOnPolicy(Map.of(), Collections.emptyList());
    }

    @Test
    public void testBooleanOverrides() {
        Assertions.assertTrue(filterSpy.isSigningRequired());
        Assertions.assertTrue(filterSpy.isSignatureVerificationRequired());
        Assertions.assertTrue(filterSpy.isTrustValidationRequired());
        Assertions.assertTrue(filterSpy.needStoreAuthTransaction());
        Assertions.assertTrue(filterSpy.needStoreAnonymousProfile());
        Assertions.assertTrue(filterSpy.isMispPolicyValidationRequired());
        Assertions.assertTrue(filterSpy.isCertificateValidationRequired());
        Assertions.assertFalse(filterSpy.isAMRValidationRequired());
    }

    @Test
    public void testFetchId() {
        ResettableStreamHttpServletRequest req = Mockito.mock(ResettableStreamHttpServletRequest.class);
        String result = filterSpy.fetchId(req, "attr");
        Assertions.assertEquals("attrkeybinding", result);
    }

    @Test
    public void testCheckMispPolicyAllowed_Allowed() throws Exception {
        MispPolicyDTO dto = new MispPolicyDTO();
        dto.setAllowKeyBindingDelegation(true);
        filterSpy.checkMispPolicyAllowed(dto);
    }

    @Test
    public void testCheckMispPolicyAllowed_NotAllowed() throws Exception {
		assertThrows(IdAuthenticationAppException.class, () -> {
        MispPolicyDTO dto = new MispPolicyDTO();
        dto.setAllowKeyBindingDelegation(false);
        filterSpy.checkMispPolicyAllowed(dto);
    		});
	}
}
