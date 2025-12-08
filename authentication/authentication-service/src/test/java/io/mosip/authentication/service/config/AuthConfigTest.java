package io.mosip.authentication.service.config;

import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.util.EnvUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

/**
 * Test class for AuthConfig
 * 
 * @author Test
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthConfigTest {

    @InjectMocks
    private AuthConfig authConfig;

    private String originalAllowedAuthType;
    private Boolean originalIsFmrEnabled;

    @Before
    public void setUp() {
        // Save original values
        originalAllowedAuthType = EnvUtil.getAllowedAuthType();
        originalIsFmrEnabled = EnvUtil.getIsFmrEnabled();
    }

    @After
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
    public void testIsFingerAuthEnabled_WhenFGR_IMG_IsInAllowedAuthType_ShouldReturnTrue() {
        String allowedAuthTypes = "bio-Finger,bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(false);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertTrue("Finger auth should be enabled when FGR_IMG is in allowed auth types", result);
    }

    @Test
    public void testIsFingerAuthEnabled_WhenFGR_MIN_IsInAllowedAuthType_AndFMR_IsEnabled_ShouldReturnTrue() {
        String allowedAuthTypes = "bio-FMR,bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(true);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertTrue("Finger auth should be enabled when FGR_MIN is in allowed auth types and FMR is enabled", result);
    }

    @Test
    public void testIsFingerAuthEnabled_WhenFGR_MIN_IsInAllowedAuthType_ButFMR_IsDisabled_ShouldReturnFalse() {
        String allowedAuthTypes = "bio-FMR,bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(false);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertFalse("Finger auth should not be enabled when FMR is disabled even if FGR_MIN is in allowed auth types", result);
    }

    @Test
    public void testIsFingerAuthEnabled_WhenNeitherFGR_IMG_NorFGR_MIN_IsInAllowedAuthType_ShouldReturnFalse() {
        String allowedAuthTypes = "bio-Iris,bio-FACE";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(true);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertFalse("Finger auth should not be enabled when neither FGR_IMG nor FGR_MIN is in allowed auth types", result);
    }

    @Test
    public void testIsFingerAuthEnabled_WhenBothFGR_IMG_AndFGR_MIN_AreInAllowedAuthType_AndFMR_IsEnabled_ShouldReturnTrue() {
        String allowedAuthTypes = "bio-Finger,bio-FMR,bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(true);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertTrue("Finger auth should be enabled when both FGR_IMG and FGR_MIN are in allowed auth types", result);
    }

    @Test
    public void testIsFingerAuthEnabled_WhenAllowedAuthType_IsEmpty_ShouldReturnFalse() {
        String allowedAuthTypes = "";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(true);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertFalse("Finger auth should not be enabled when allowed auth types is empty", result);
    }

    @Test
    public void testIsFaceAuthEnabled_WhenFACE_IMG_IsInAllowedAuthType_ShouldReturnTrue() {
        String allowedAuthTypes = "bio-FACE,bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFaceAuthEnabled");

        assertTrue("Face auth should be enabled when FACE_IMG is in allowed auth types", result);
    }

    @Test
    public void testIsFaceAuthEnabled_WhenFACE_IMG_IsNotInAllowedAuthType_ShouldReturnFalse() {
        String allowedAuthTypes = "bio-Finger,bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFaceAuthEnabled");

        assertFalse("Face auth should not be enabled when FACE_IMG is not in allowed auth types", result);
    }

    @Test
    public void testIsFaceAuthEnabled_WhenAllowedAuthType_IsEmpty_ShouldReturnFalse() {
        String allowedAuthTypes = "";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFaceAuthEnabled");

        assertFalse("Face auth should not be enabled when allowed auth types is empty", result);
    }

    @Test
    public void testIsIrisAuthEnabled_WhenIRIS_IMG_IsInAllowedAuthType_ShouldReturnTrue() {
        String allowedAuthTypes = "bio-Iris,bio-Finger";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isIrisAuthEnabled");

        assertTrue("Iris auth should be enabled when IRIS_IMG is in allowed auth types", result);
    }

    @Test
    public void testIsIrisAuthEnabled_WhenIRIS_IMG_IsNotInAllowedAuthType_ShouldReturnFalse() {
        String allowedAuthTypes = "bio-Finger,bio-FACE";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isIrisAuthEnabled");

        assertFalse("Iris auth should not be enabled when IRIS_IMG is not in allowed auth types", result);
    }

    @Test
    public void testIsIrisAuthEnabled_WhenAllowedAuthType_IsEmpty_ShouldReturnFalse() {
        String allowedAuthTypes = "";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isIrisAuthEnabled");

        assertFalse("Iris auth should not be enabled when allowed auth types is empty", result);
    }

    @Test
    public void testIsIrisAuthEnabled_WhenIRIS_IMG_IsInAllowedAuthType_WithOtherTypes_ShouldReturnTrue() {
        String allowedAuthTypes = "bio-Iris,bio-Finger,bio-FACE,bio-FMR";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isIrisAuthEnabled");

        assertTrue("Iris auth should be enabled when IRIS_IMG is in allowed auth types even with other types", result);
    }

    @Test
    public void testIsFingerAuthEnabled_WhenFGR_IMG_IsInAllowedAuthType_WithNullFMR_ShouldReturnTrue() {
        String allowedAuthTypes = "bio-Finger";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(null);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertTrue("Finger auth should be enabled when FGR_IMG is in allowed auth types regardless of FMR setting", result);
    }

    @Test
    public void testAllAuthTypes_WhenAllAreEnabled_ShouldReturnTrue() {
        String allowedAuthTypes = "bio-Finger,bio-FMR,bio-FACE,bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(true);

        boolean fingerResult = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");
        boolean faceResult = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFaceAuthEnabled");
        boolean irisResult = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isIrisAuthEnabled");

        assertTrue("Finger auth should be enabled", fingerResult);
        assertTrue("Face auth should be enabled", faceResult);
        assertTrue("Iris auth should be enabled", irisResult);
    }

    @Test
    public void testAllAuthTypes_WhenNoneAreEnabled_ShouldReturnFalse() {
        String allowedAuthTypes = "demo,otp";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(false);

        boolean fingerResult = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");
        boolean faceResult = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFaceAuthEnabled");
        boolean irisResult = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isIrisAuthEnabled");

        assertFalse("Finger auth should not be enabled", fingerResult);
        assertFalse("Face auth should not be enabled", faceResult);
        assertFalse("Iris auth should not be enabled", irisResult);
    }

    @Test
    public void testIsFingerAuthEnabled_WhenFGR_MIN_IsInAllowedAuthType_AndFMR_IsEnabled_ButFGR_IMG_IsNot_ShouldReturnTrue() {
        String allowedAuthTypes = "bio-FMR";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(true);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        assertTrue("Finger auth should be enabled when only FGR_MIN is in allowed auth types and FMR is enabled", result);
    }

    @Test
    public void testIsFingerAuthEnabled_WhenFGR_IMG_IsInAllowedAuthType_WithSpaces_ShouldReturnTrue() {
        String allowedAuthTypes = "bio-Finger , bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);
        EnvUtil.setIsFmrEnabled(false);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFingerAuthEnabled");

        // Note: String.contains() will still find "bio-Finger" even with spaces
        assertTrue("Finger auth should be enabled when FGR_IMG is in allowed auth types even with spaces", result);
    }

    @Test
    public void testIsFaceAuthEnabled_WhenFACE_IMG_IsInAllowedAuthType_WithSpaces_ShouldReturnTrue() {
        String allowedAuthTypes = "bio-FACE , bio-Iris";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isFaceAuthEnabled");

        assertTrue("Face auth should be enabled when FACE_IMG is in allowed auth types even with spaces", result);
    }

    @Test
    public void testIsIrisAuthEnabled_WhenIRIS_IMG_IsInAllowedAuthType_WithSpaces_ShouldReturnTrue() {
        String allowedAuthTypes = "bio-Iris , bio-Finger";
        EnvUtil.setAllowedAuthType(allowedAuthTypes);

        boolean result = (boolean) ReflectionTestUtils.invokeMethod(authConfig, "isIrisAuthEnabled");

        assertTrue("Iris auth should be enabled when IRIS_IMG is in allowed auth types even with spaces", result);
    }
}
