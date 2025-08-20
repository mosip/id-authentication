package io.mosip.authentication.service.kyc.config;

import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.util.EnvUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockStatic;

public class KycAuthConfigTest {

    private KycAuthConfig config;
    private MockedStatic<EnvUtil> envUtilMock;

    @Before
    public void setUp() {
        config = new KycAuthConfig();
        // Initialize static mock (requires mockito-inline:3.4.3)
        envUtilMock = mockStatic(EnvUtil.class);
    }

    @After
    public void tearDown() {
        if (envUtilMock != null) {
            envUtilMock.close();
        }
    }

    // -------- FINGER AUTH TESTS --------

    @Test
    public void testIsFingerAuthEnabled_true_withFgrImg() {
        List<String> allowed = Arrays.asList(BioAuthType.FGR_IMG.getConfigNameValue());
        envUtilMock.when(EnvUtil::getEkycAllowedAuthType).thenReturn(allowed);

        assertTrue(config.isFingerAuthEnabled());
    }

    @Test
    public void testIsFingerAuthEnabled_true_withFgrMinAndFmrEnabled() {
        List<String> allowed = Arrays.asList(BioAuthType.FGR_MIN.getConfigNameValue());
        envUtilMock.when(EnvUtil::getEkycAllowedAuthType).thenReturn(allowed);
        envUtilMock.when(EnvUtil::getIsFmrEnabled).thenReturn(true);

        assertTrue(config.isFingerAuthEnabled());
    }

    @Test
    public void testIsFingerAuthEnabled_false() {
        envUtilMock.when(EnvUtil::getEkycAllowedAuthType)
                .thenReturn(Collections.singletonList("OTHER"));
        envUtilMock.when(EnvUtil::getIsFmrEnabled).thenReturn(false);

        assertFalse(config.isFingerAuthEnabled());
    }

    // -------- FACE AUTH TESTS --------

    @Test
    public void testIsFaceAuthEnabled_true() {
        envUtilMock.when(EnvUtil::getEkycAllowedAuthType)
                .thenReturn(Collections.singletonList(BioAuthType.FACE_IMG.getConfigNameValue()));

        assertTrue(config.isFaceAuthEnabled());
    }

    @Test
    public void testIsFaceAuthEnabled_false() {
        envUtilMock.when(EnvUtil::getEkycAllowedAuthType)
                .thenReturn(Collections.singletonList("OTHER"));

        assertFalse(config.isFaceAuthEnabled());
    }

    // -------- IRIS AUTH TESTS --------

    @Test
    public void testIsIrisAuthEnabled_true() {
        envUtilMock.when(EnvUtil::getEkycAllowedAuthType)
                .thenReturn(Collections.singletonList(BioAuthType.IRIS_IMG.getConfigNameValue()));

        assertTrue(config.isIrisAuthEnabled());
    }

    @Test
    public void testIsIrisAuthEnabled_false() {
        envUtilMock.when(EnvUtil::getEkycAllowedAuthType)
                .thenReturn(Collections.singletonList("OTHER"));

        assertFalse(config.isIrisAuthEnabled());
    }
}
