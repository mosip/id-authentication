package io.mosip.authentication.otp.service.config;

import io.mosip.authentication.common.service.helper.IdentityAttributesForMatchTypeHelper;
import io.mosip.authentication.common.service.helper.MatchIdentityDataHelper;
import io.mosip.authentication.common.service.helper.MatchTypeHelper;
import io.mosip.authentication.common.service.helper.SeparatorHelper;
import io.mosip.authentication.common.service.integration.RequireOtpNotFrozenHelper;
import io.mosip.authentication.common.service.integration.ValidateOtpHelper;
import io.mosip.authentication.common.service.util.EntityInfoUtil;
import io.mosip.authentication.common.service.util.LanguageUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OtpServiceConfigTest {


    private final OtpServiceConfig config = new OtpServiceConfig();

    @Test
    void testLanguageUtilBean() {
        assertNotNull(config.languageUtil());
        assertTrue(config.languageUtil() instanceof LanguageUtil);
    }

    @Test
    void testIdentityAttributesForMatchTypeHelperBean() {
        assertNotNull(config.identityAttributesForMatchTypeHelper());
        assertTrue(config.identityAttributesForMatchTypeHelper() instanceof IdentityAttributesForMatchTypeHelper);
    }

    @Test
    void testValidateOtpHelperBean() {
        assertNotNull(config.validateOtpHelper());
        assertTrue(config.validateOtpHelper() instanceof ValidateOtpHelper);
    }

    @Test
    void testRequireOtpNotFrozenHelperBean() {
        assertNotNull(config.requireOtpNotFrozenHelper());
        assertTrue(config.requireOtpNotFrozenHelper() instanceof RequireOtpNotFrozenHelper);
    }

    @Test
    void testSeparatorHelperBean() {
        assertNotNull(config.separatorHelper());
        assertTrue(config.separatorHelper() instanceof SeparatorHelper);
    }

    @Test
    void testMatchTypeHelperBean() {
        assertNotNull(config.matchTypeHelper());
        assertTrue(config.matchTypeHelper() instanceof MatchTypeHelper);
    }

    @Test
    void testEntityInfoUtilBean() {
        assertNotNull(config.entityInfoUtil());
        assertTrue(config.entityInfoUtil() instanceof EntityInfoUtil);
    }

    @Test
    void testMatchIdentityDataHelperBean() {
        assertNotNull(config.matchIdentityDataHelper());
        assertTrue(config.matchIdentityDataHelper() instanceof MatchIdentityDataHelper);
    }
}
