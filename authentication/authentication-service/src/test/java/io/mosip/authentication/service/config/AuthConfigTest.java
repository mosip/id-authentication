package io.mosip.authentication.service.config;

import io.mosip.authentication.common.service.util.EnvUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AuthConfig
 * 
 * @author Test
 */
@ExtendWith(MockitoExtension.class)
public class AuthConfigTest {

    @InjectMocks
    private AuthConfig authConfig;

    private String originalAllowedAuthType;
    private Boolean originalIsFmrEnabled;

    @BeforeEach
    public void setUp() {
        // Save original values
        originalAllowedAuthType = EnvUtil.getAllowedAuthType();
        originalIsFmrEnabled = EnvUtil.getIsFmrEnabled();
    }

    @AfterEach
    public void tearDown() {
        // Restore original values
        if (originalAllowedAuthType != null) {
            EnvUtil.setAllowedAuthType(originalAllowedAuthType);
        }
        if (originalIsFmrEnabled != null) {
            EnvUtil.setIsFmrEnabled(originalIsFmrEnabled);
        }
    }

    @Test
    public void testIsFingerAuthEnabledWhenFGRIMGIsInAllowedAuthTypeShouldReturnTrue() {
        String allowedAuthTypes = "bio-Finger,bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(false);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertTrue(result, "Finger auth should be enabled when FGR_IMG is in allowed auth types");
    }

    @Test
    public void testIsFingerAuthEnabledWhenFGRMINIsInAllowedAuthTypeAndFMRIsEnabledShouldReturnTrue() {
        String allowedAuthTypes = "bio-FMR,bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(true);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertTrue(result, "Finger auth should be enabled when FGR_MIN is in allowed auth types and FMR is enabled");
    }

    @Test
    public void testIsFingerAuthEnabledWhenFGRMINIsInAllowedAuthTypeButFMRIsDisabledShouldReturnFalse() {
        String allowedAuthTypes = "bio-FMR,bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(false);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertFalse(result, "Finger auth should not be enabled when FMR is disabled even if FGR_MIN is in allowed auth types");
    }

    @Test
    public void testIsFingerAuthEnabledWhenNeitherFGRIMGNorFGRMINIsInAllowedAuthTypeShouldReturnFalse() {
        String allowedAuthTypes = "bio-Iris,bio-FACE";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(true);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertFalse(result, "Finger auth should not be enabled when neither FGR_IMG nor FGR_MIN is in allowed auth types");
    }

    @Test
    public void testIsFingerAuthEnabledWhenBothFGRIMGAndFGRMINAreInAllowedAuthTypeAndFMRIsEnabledShouldReturnTrue() {
        String allowedAuthTypes = "bio-Finger,bio-FMR,bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(true);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertTrue(result, "Finger auth should be enabled when both FGR_IMG and FGR_MIN are in allowed auth types");
    }

    @Test
    public void testIsFingerAuthEnabledWhenAllowedAuthTypeIsEmptyShouldReturnFalse() {
        String allowedAuthTypes = "";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(true);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertFalse(result, "Finger auth should not be enabled when allowed auth types is empty");
    }

    @Test
    public void testIsFaceAuthEnabledWhenFACEIMGIsInAllowedAuthTypeShouldReturnTrue() {
        String allowedAuthTypes = "bio-FACE,bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFaceAuthEnabled");

        assertTrue(result, "Face auth should be enabled when FACE_IMG is in allowed auth types");
    }

    @Test
    public void testIsFaceAuthEnabledWhenFACEIMGIsNotInAllowedAuthTypeShouldReturnFalse() {
        String allowedAuthTypes = "bio-Finger,bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFaceAuthEnabled");

        assertFalse(result, "Face auth should not be enabled when FACE_IMG is not in allowed auth types");
    }

    @Test
    public void testIsFaceAuthEnabledWhenAllowedAuthTypeIsEmptyShouldReturnFalse() {
        String allowedAuthTypes = "";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFaceAuthEnabled");

        assertFalse(result, "Face auth should not be enabled when allowed auth types is empty");
    }

    @Test
    public void testIsIrisAuthEnabledWhenIRISIMGIsInAllowedAuthTypeShouldReturnTrue() {
        String allowedAuthTypes = "bio-Iris,bio-Finger";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isIrisAuthEnabled");

        assertTrue(result, "Iris auth should be enabled when IRIS_IMG is in allowed auth types");
    }

    @Test
    public void testIsIrisAuthEnabledWhenIRISIMGIsNotInAllowedAuthTypeShouldReturnFalse() {
        String allowedAuthTypes = "bio-Finger,bio-FACE";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isIrisAuthEnabled");

        assertFalse(result, "Iris auth should not be enabled when IRIS_IMG is not in allowed auth types");
    }

    @Test
    public void testIsIrisAuthEnabledWhenAllowedAuthTypeIsEmptyShouldReturnFalse() {
        String allowedAuthTypes = "";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isIrisAuthEnabled");

        assertFalse(result, "Iris auth should not be enabled when allowed auth types is empty");
    }

    @Test
    public void testIsIrisAuthEnabledWhenIRISIMGIsInAllowedAuthTypeWithOtherTypesShouldReturnTrue() {
        String allowedAuthTypes = "bio-Iris,bio-Finger,bio-FACE,bio-FMR";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isIrisAuthEnabled");

        assertTrue(result, "Iris auth should be enabled when IRIS_IMG is in allowed auth types even with other types");
    }

    @Test
    public void testIsFingerAuthEnabledWhenFGRIMGIsInAllowedAuthTypeWithNullFMRShouldReturnTrue() {
        String allowedAuthTypes = "bio-Finger";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(null);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertTrue(result, "Finger auth should be enabled when FGR_IMG is in allowed auth types regardless of FMR setting");
    }

    @Test
    public void testAllAuthTypesWhenAllAreEnabledShouldReturnTrue() {
        String allowedAuthTypes = "bio-Finger,bio-FMR,bio-FACE,bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(true);

        boolean fingerResult = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");
        boolean faceResult = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFaceAuthEnabled");
        boolean irisResult = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isIrisAuthEnabled");

        assertTrue(fingerResult, "Finger auth should be enabled");
        assertTrue(faceResult, "Face auth should be enabled");
        assertTrue(irisResult, "Iris auth should be enabled");
    }

    @Test
    public void testAllAuthTypesWhenNoneAreEnabledShouldReturnFalse() {
        String allowedAuthTypes = "demo,otp";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(false);

        boolean fingerResult = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");
        boolean faceResult = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFaceAuthEnabled");
        boolean irisResult = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isIrisAuthEnabled");

        assertFalse(fingerResult, "Finger auth should not be enabled");
        assertFalse(faceResult, "Face auth should not be enabled");
        assertFalse(irisResult, "Iris auth should not be enabled");
    }

    @Test
    public void testIsFingerAuthEnabledWhenFGRMINIsInAllowedAuthTypeAndFMRIsEnabledButFGRIMGIsNotShouldReturnTrue() {
        String allowedAuthTypes = "bio-FMR";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(true);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertTrue(result, "Finger auth should be enabled when only FGR_MIN is in allowed auth types and FMR is enabled");
    }

    @Test
    public void testIsFingerAuthEnabledWhenFGRIMGIsInAllowedAuthTypeWithSpacesShouldReturnTrue() {
        String allowedAuthTypes = "bio-Finger , bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(false);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertTrue(result, "Finger auth should be enabled when FGR_IMG is in allowed auth types even with spaces");
    }

    @Test
    public void testIsFaceAuthEnabledWhenFACEIMGIsInAllowedAuthTypeWithSpacesShouldReturnTrue() {
        String allowedAuthTypes = "bio-FACE , bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFaceAuthEnabled");

        assertTrue(result, "Face auth should be enabled when FACE_IMG is in allowed auth types even with spaces");
    }

    @Test
    public void testIsIrisAuthEnabledWhenIRISIMGIsInAllowedAuthTypeWithSpacesShouldReturnTrue() {
        String allowedAuthTypes = "bio-Iris , bio-Finger";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isIrisAuthEnabled");

        assertTrue(result, "Iris auth should be enabled when IRIS_IMG is in allowed auth types even with spaces");
    }
}
