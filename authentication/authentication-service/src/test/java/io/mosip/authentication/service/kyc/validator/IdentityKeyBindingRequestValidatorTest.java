package io.mosip.authentication.service.kyc.validator;


import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingRequestDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class IdentityKeyBindingRequestValidatorTest {

    @Mock
    IdInfoHelper idInfoHelper;

    @Mock
    Errors errors;

    private IdentityKeyBindingRequestValidator validator;

    @InjectMocks
    IdentityKeyBindingRequestValidator identityKeyBindingRequestValidator;

    @Before
    public void setUp() {
        validator = new IdentityKeyBindingRequestValidator();
    }

    @Test
    public void validateWithValidDetails_thenPass(){

        IdentityKeyBindingRequestDTO identityKeyBindingRequestDTO = new IdentityKeyBindingRequestDTO();
        identityKeyBindingRequestDTO.setIdentityKeyBinding(null);
        identityKeyBindingRequestDTO.setIndividualIdType("UIN");
        identityKeyBindingRequestDTO.setIndividualId("123456789012");
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setBiometrics(null);
        requestDTO.setOtp("123456");
        requestDTO.setTimestamp("2019-02-20T10:00:00.000Z");
        identityKeyBindingRequestDTO.setRequest(requestDTO);
        //Mockito.when(errors.hasErrors()).thenReturn(false);
        Errors errors = new BeanPropertyBindingResult(identityKeyBindingRequestDTO, "identityKeyBindingRequestDTO");
        //Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
        identityKeyBindingRequestValidator.validate(identityKeyBindingRequestDTO, errors);

    }

    @Test
    public void testValidateWithInvalidTarget() {
        IdentityKeyBindingRequestDTO identityKeyBindingRequestDTO = new IdentityKeyBindingRequestDTO();
        errors = new BeanPropertyBindingResult(identityKeyBindingRequestDTO, "target");
        identityKeyBindingRequestValidator.validate(null, errors);
        assertTrue(errors.hasErrors());
    }

    @Test
    public void testSupports_withCorrectClass() {
        assertTrue(validator.supports(IdentityKeyBindingRequestDTO.class));
    }

    @Test
    public void testSupports_withWrongClass() {
        assertFalse(validator.supports(String.class));
    }

    @Test
    public void testValidate_withNullIdentityKeyBinding() {
        IdentityKeyBindingRequestDTO request = new IdentityKeyBindingRequestDTO();
        Errors errors = new BeanPropertyBindingResult(request, "request");

        validator.validate(request, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    public void testValidate_withNullPublicKey() {
        IdentityKeyBindingRequestDTO request = new IdentityKeyBindingRequestDTO();
        IdentityKeyBindingDTO binding = new IdentityKeyBindingDTO();
        request.setIdentityKeyBinding(binding);

        Errors errors = new BeanPropertyBindingResult(request, "request");

        validator.validate(request, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    public void testValidate_withEmptyPublicKeyMap() {
        IdentityKeyBindingRequestDTO request = new IdentityKeyBindingRequestDTO();
        IdentityKeyBindingDTO binding = new IdentityKeyBindingDTO();
        binding.setPublicKeyJWK(new HashMap<>());
        request.setIdentityKeyBinding(binding);

        Errors errors = new BeanPropertyBindingResult(request, "request");

        validator.validate(request, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    public void testValidate_withMissingModulus() {
        IdentityKeyBindingRequestDTO request = new IdentityKeyBindingRequestDTO();
        IdentityKeyBindingDTO binding = new IdentityKeyBindingDTO();

        Map<String, Object> jwk = new HashMap<>();
        jwk.put("e", "AQAB"); // exponent present
        binding.setPublicKeyJWK(jwk);

        request.setIdentityKeyBinding(binding);

        Errors errors = new BeanPropertyBindingResult(request, "request");

        validator.validate(request, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    public void testValidate_withMissingExponent() {
        IdentityKeyBindingRequestDTO request = new IdentityKeyBindingRequestDTO();
        IdentityKeyBindingDTO binding = new IdentityKeyBindingDTO();

        Map<String, Object> jwk = new HashMap<>();
        jwk.put("n", "modulusValue"); // modulus present
        binding.setPublicKeyJWK(jwk);

        request.setIdentityKeyBinding(binding);

        Errors errors = new BeanPropertyBindingResult(request, "request");

        validator.validate(request, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    public void testValidate_withEmptyAuthFactorType() {
        IdentityKeyBindingRequestDTO request = new IdentityKeyBindingRequestDTO();
        IdentityKeyBindingDTO binding = new IdentityKeyBindingDTO();

        Map<String, Object> jwk = new HashMap<>();
        jwk.put("n", "modulusValue");
        jwk.put("e", "AQAB");

        binding.setPublicKeyJWK(jwk);
        binding.setAuthFactorType(""); // invalid
        request.setIdentityKeyBinding(binding);

        Errors errors = new BeanPropertyBindingResult(request, "request");

        validator.validate(request, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    public void testGetAllowedAuthTypeProperty() {
        validator.getAllowedAuthTypeProperty();
    }

    @Test
    public void testValidate_superValidationFails_shouldAddErrors() {
        IdentityKeyBindingRequestDTO request = new IdentityKeyBindingRequestDTO();

        Errors errors = new BeanPropertyBindingResult(request, "request");
        validator.validate(request, errors);

        assertTrue(errors.hasErrors());
    }
}
