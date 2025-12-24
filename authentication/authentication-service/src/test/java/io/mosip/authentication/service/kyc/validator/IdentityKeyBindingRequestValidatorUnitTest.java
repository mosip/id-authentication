package io.mosip.authentication.service.kyc.validator;

import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingRequestDTO;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Focused unit tests to directly exercise the internal validation branches.
 * These branches are often skipped when base AuthRequest validation fails early.
 */
public class IdentityKeyBindingRequestValidatorUnitTest {

    private final IdentityKeyBindingRequestValidator validator = new IdentityKeyBindingRequestValidator();

    private static Errors errors() {
        return new BeanPropertyBindingResult(new IdentityKeyBindingRequestDTO(), "request");
    }

    @Test
    public void validateIdentityKeyBindingAddsErrorWhenBindingIsNull() {
        Errors errors = errors();
        ReflectionTestUtils.invokeMethod(validator, "validateIdentityKeyBinding", (IdentityKeyBindingDTO) null, errors);
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validateIdentityKeyBindingPublicKeyNoErrorWhenModulusAndExponentPresent() {
        Errors errors = errors();
        Map<String, Object> jwk = new HashMap<>();
        jwk.put("n", "modulusValue");
        jwk.put("e", "AQAB");
        ReflectionTestUtils.invokeMethod(validator, "validateIdentityKeyBindingPublicKey", jwk, errors);
        assertFalse(errors.hasErrors());
    }


    @Test
    public void validateIdentityKeyBindingAuthFactorTypeNoErrorWhenNonBlank() {
        Errors errors = errors();
        ReflectionTestUtils.invokeMethod(validator, "validateIdentityKeyBindingAuthFactorType", "OTP", errors);
        assertFalse(errors.hasErrors());
    }
}


