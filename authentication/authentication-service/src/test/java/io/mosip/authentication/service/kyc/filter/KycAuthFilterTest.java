package io.mosip.authentication.service.kyc.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.authentication.common.service.util.AuthTypeUtil;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.partner.dto.AuthPolicy;
import io.mosip.authentication.core.partner.dto.MispPolicyDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@Import(EnvUtil.class)
public class KycAuthFilterTest {
    @Autowired
    EnvUtil env;
    KycAuthFilter kyAuthFilter = new KycAuthFilter();
    @Autowired
    ObjectMapper mapper;

    @Before
    public void before() {
        ReflectionTestUtils.setField(kyAuthFilter, "mapper", mapper);
        ReflectionTestUtils.setField(kyAuthFilter, "env", env);
    }
    @Test
    public void checkAllowedAuthTypeBasedOnPolicyTest() {
        AuthPolicy authPolicy = new AuthPolicy();
        authPolicy.setAuthType("demo");
        authPolicy.setMandatory(true);
        try {
            ReflectionTestUtils.invokeMethod(kyAuthFilter, "checkAllowedAuthTypeBasedOnPolicy", new HashMap<>(), Collections.singletonList(authPolicy));
        } catch (UndeclaredThrowableException e) {
            String detailMessage = e.getUndeclaredThrowable().getMessage();
            String[] error = detailMessage.split("-->");
            assertEquals("IDA-MPA-025", error[0].trim());
            assertEquals("Partner is unauthorised for KYC-Auth", error[1].trim());
            assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
        }
    }


    private KycAuthFilter filter;

    @Before
    public void setup() {
        filter = Mockito.spy(new KycAuthFilter());
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
        assertTrue(filter.isMispPolicyValidationRequired());
        assertTrue(filter.isCertificateValidationRequired());
        assertTrue(filter.isAMRValidationRequired());
    }

    @Test
    public void testFetchId() {
        assertEquals("attrkycauth",
                filter.fetchId(null, "attr"));
    }

    @Test
    public void testCheckMispPolicyAllowed_allowed() throws Exception {
        MispPolicyDTO dto = new MispPolicyDTO();
        dto.setAllowKycRequestDelegation(true); // should not throw
        filter.checkMispPolicyAllowed(dto);
    }

    @Test(expected = IdAuthenticationAppException.class)
    public void testCheckMispPolicyAllowed_notAllowed() throws Exception {
        MispPolicyDTO dto = new MispPolicyDTO();
        dto.setAllowKycRequestDelegation(false); // should throw
        filter.checkMispPolicyAllowed(dto);
    }
}
