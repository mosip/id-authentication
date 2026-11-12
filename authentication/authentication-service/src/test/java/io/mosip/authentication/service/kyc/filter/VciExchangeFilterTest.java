package io.mosip.authentication.service.kyc.filter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.partner.dto.AuthPolicy;
import io.mosip.authentication.core.partner.dto.MispPolicyDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@Import(EnvUtil.class)
public class VciExchangeFilterTest {

    VciExchangeFilter vciExchangeFilter = new VciExchangeFilter();

    @Test
    public void test_defaultMethods() {
        Assertions.assertTrue(vciExchangeFilter.isPartnerCertificateNeeded());
        Assertions.assertTrue(vciExchangeFilter.isSigningRequired());
        Assertions.assertTrue(vciExchangeFilter.isSignatureVerificationRequired());
        Assertions.assertTrue(vciExchangeFilter.isTrustValidationRequired());
        Assertions.assertTrue(vciExchangeFilter.needStoreAuthTransaction());
        Assertions.assertTrue(vciExchangeFilter.needStoreAnonymousProfile());
        Assertions.assertTrue(vciExchangeFilter.isMispPolicyValidationRequired());
        Assertions.assertTrue(vciExchangeFilter.isCertificateValidationRequired());
        Assertions.assertFalse(vciExchangeFilter.isAMRValidationRequired());
    }

    @Test
    public void test_checkAllowedAuthTypeBasedOnPolicy_withValidPolicy_thenPass() throws IdAuthenticationAppException {
        AuthPolicy authPolicy = new AuthPolicy();
        authPolicy.setAuthType("vciexchange");
        vciExchangeFilter.checkAllowedAuthTypeBasedOnPolicy(null, Arrays.asList(authPolicy));
    }

    @Test
    public void test_checkAllowedAuthTypeBasedOnPolicy_withInvalidPolicy_thenFail() throws IdAuthenticationAppException {
		assertThrows(IdAuthenticationAppException.class, () -> {
        AuthPolicy authPolicy = new AuthPolicy();
        authPolicy.setAuthType("kycexchange");
        vciExchangeFilter.checkAllowedAuthTypeBasedOnPolicy(null, Arrays.asList(authPolicy));
    		});
	}

    @Test
    public void test_checkMispPolicyAllowed_withInvalidPolicy_thenFail()
            throws IdAuthenticationAppException {
		assertThrows(IdAuthenticationAppException.class, () -> {
        MispPolicyDTO mispPolicyDTO = new MispPolicyDTO();
        mispPolicyDTO.setAllowVciRequestDelegation(false);
        vciExchangeFilter.checkMispPolicyAllowed(mispPolicyDTO);
    		});
	}

    @Test
    public void test_checkMispPolicyAllowed_withValidPolicy_thenPass()
            throws IdAuthenticationAppException {
        MispPolicyDTO mispPolicyDTO = new MispPolicyDTO();
        mispPolicyDTO.setAllowVciRequestDelegation(true);
        vciExchangeFilter.checkMispPolicyAllowed(mispPolicyDTO);
    }
}
