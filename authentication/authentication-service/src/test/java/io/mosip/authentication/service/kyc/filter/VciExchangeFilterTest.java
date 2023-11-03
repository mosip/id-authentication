package io.mosip.authentication.service.kyc.filter;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.partner.dto.AuthPolicy;
import io.mosip.authentication.core.partner.dto.MispPolicyDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@Import(EnvUtil.class)
public class VciExchangeFilterTest {

    VciExchangeFilter vciExchangeFilter = new VciExchangeFilter();

    @Test
    public void test_defaultMethods() {
        Assert.assertTrue(vciExchangeFilter.isPartnerCertificateNeeded());
        Assert.assertTrue(vciExchangeFilter.isSigningRequired());
        Assert.assertTrue(vciExchangeFilter.isSignatureVerificationRequired());
        Assert.assertTrue(vciExchangeFilter.isTrustValidationRequired());
        Assert.assertTrue(vciExchangeFilter.needStoreAuthTransaction());
        Assert.assertTrue(vciExchangeFilter.needStoreAnonymousProfile());
        Assert.assertTrue(vciExchangeFilter.isMispPolicyValidationRequired());
        Assert.assertTrue(vciExchangeFilter.isCertificateValidationRequired());
        Assert.assertFalse(vciExchangeFilter.isAMRValidationRequired());
    }

    @Test
    public void test_checkAllowedAuthTypeBasedOnPolicy_withValidPolicy_thenPass() throws IdAuthenticationAppException {
        AuthPolicy authPolicy = new AuthPolicy();
        authPolicy.setAuthType("vciexchange");
        vciExchangeFilter.checkAllowedAuthTypeBasedOnPolicy(null, Arrays.asList(authPolicy));
    }

    @Test(expected = IdAuthenticationAppException.class)
    public void test_checkAllowedAuthTypeBasedOnPolicy_withInvalidPolicy_thenFail() throws IdAuthenticationAppException {
        AuthPolicy authPolicy = new AuthPolicy();
        authPolicy.setAuthType("kycexchange");
        vciExchangeFilter.checkAllowedAuthTypeBasedOnPolicy(null, Arrays.asList(authPolicy));
    }

    @Test(expected = IdAuthenticationAppException.class)
    public void test_checkMispPolicyAllowed_withInvalidPolicy_thenFail()
            throws IdAuthenticationAppException {
        MispPolicyDTO mispPolicyDTO = new MispPolicyDTO();
        mispPolicyDTO.setAllowVciRequestDelegation(false);
        vciExchangeFilter.checkMispPolicyAllowed(mispPolicyDTO);
    }

    @Test
    public void test_checkMispPolicyAllowed_withValidPolicy_thenPass()
            throws IdAuthenticationAppException {
        MispPolicyDTO mispPolicyDTO = new MispPolicyDTO();
        mispPolicyDTO.setAllowVciRequestDelegation(true);
        vciExchangeFilter.checkMispPolicyAllowed(mispPolicyDTO);
    }
}
