package io.mosip.authentication.service.kyc.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.partner.dto.AuthPolicy;
import io.mosip.idrepository.core.util.EnvUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@Import(EnvUtil.class)
public class KycAuthenticationFilterTest {

    @Mock
    EnvUtil env;

    @Autowired
    ObjectMapper mapper;

    private KycAuthenticationFilter filter;

    @Before
    public void before() {
        ReflectionTestUtils.setField(filter, "env", new EnvUtil());
    }


    @Before
    public void setup() {
        filter = Mockito.spy(new KycAuthenticationFilter());
        ReflectionTestUtils.setField(filter, "mapper", mapper);
        ReflectionTestUtils.setField(filter, "env", env);
    }

    @Test
    public void testBooleans() {
        assertTrue(filter.isPartnerCertificateNeeded());
        assertTrue(filter.isSigningRequired());
        assertTrue(filter.isSignatureVerificationRequired());
        assertTrue(filter.isTrustValidationRequired());
        assertTrue(filter.needStoreAuthTransaction());
        assertTrue(filter.needStoreAnonymousProfile());
        assertFalse(filter.isMispPolicyValidationRequired());
        assertTrue(filter.isCertificateValidationRequired());
        assertFalse(filter.isAMRValidationRequired());
    }

    @Test
    public void testFetchId() {
        assertEquals("attrkyc", filter.fetchId(null, "attr"));
    }

    @Test
    public void testCheckAllowedAuthTypeBasedOnPolicy_notAllowed() {
        AuthPolicy policy = new AuthPolicy();
        policy.setAuthType("demo");
        policy.setMandatory(true);

        try {
            ReflectionTestUtils.invokeMethod(filter,
                    "checkAllowedAuthTypeBasedOnPolicy",
                    new HashMap<>(),
                    Collections.singletonList(policy));
            fail("Exception expected");
        } catch (UndeclaredThrowableException e) {
            assertTrue(e.getCause() instanceof IdAuthenticationAppException);
            assertEquals("IDA-MPA-001", ((IdAuthenticationAppException) e.getCause()).getErrorCode()); // UNAUTHORISED_PARTNER
        }
    }

    @Test
    public void testCheckAllowedAuthTypeBasedOnPolicy_allowed() throws Exception {
        // Subclass to override protected method without compile error
        KycAuthenticationFilter allowedFilter = new KycAuthenticationFilter() {
            @Override
            protected boolean isAllowedAuthType(String authType, java.util.List<AuthPolicy> policies) {
                return true;
            }
        };
        ReflectionTestUtils.invokeMethod(allowedFilter,
                "checkAllowedAuthTypeBasedOnPolicy",
                new HashMap<>(),
                Collections.singletonList(new AuthPolicy())); // Should not throw
    }
}
