package io.mosip.authentication.service.kyc.validator;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.VciCredentialsDefinitionRequestDTO;
import io.mosip.authentication.core.indauth.dto.VciExchangeRequestDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@Import(EnvUtil.class)
public class VciExchangeRequestValidatorTest {

    @InjectMocks
    VciExchangeRequestValidator vciExchangeRequestValidator;

    @Before
    public void before() {
        ReflectionTestUtils.setField(vciExchangeRequestValidator, "supportedCredTypes",
                Arrays.asList("VerifiableCredential","MOSIPVerifiableCredential"));
    }

    @Test
    public void test_supports_withValidInput_thenPass() {
        Assert.assertTrue(vciExchangeRequestValidator.supports(VciExchangeRequestDTO.class));
    }

    @Test
    public void test_supports_withInvalidInput_thenFail() {
        Assert.assertFalse(vciExchangeRequestValidator.supports(KycAuthRequestDTO.class));
    }

    @Test
    public void test_validate_withValidInput_thenPass() {
        VciExchangeRequestDTO vciExchangeRequestDTO = new VciExchangeRequestDTO();
        vciExchangeRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
        vciExchangeRequestDTO.setTransactionID("transactio");
        vciExchangeRequestDTO.setVcFormat("ldp_vc");
        vciExchangeRequestDTO.setVcAuthToken("vc-auth-token");
        vciExchangeRequestDTO.setCredSubjectId("did:jwk:eyJrdHkiOiJSU0EiLCJlIjoiQVFBQiIsInVzZSI6InNpZyIsImtpZCI6Inc4VUY3QnE0dDFSeVMxdFJTOHhvVllHUjMySVdiMFZyU3I4M0dEdno3d28iLCJhbGciOiJSUzI1NiIsIm4iOiJ5SGY1RjZYMFI5RDNxWm5WaUJORDZRV25pUmVnR2hjQ3NqakVJSENlTWp1UWJHek1LaFB6aFZVWGNtaTBMbGVQVWdUdlhjOWlrRmNnTXM3ckFhckI1dlJEcTh1Mjd2WHNBVjdiOUlZaVVGY3U1ZFZpdTd0Q0F1N0V5cXlLWVlUX20xMzhlZjQxVmU4X29LZVNvT0RRaGxyc0RJTmltX0JwWHBvc0xQVV96MXpfODNxX0ZRU05ydDE2dGhHa0hZeUZsRnhxZnNWZElPTkdoMzRFY3dubFZUY0lQUE5xZVY2RkJ3MENlR2NuaUlSRDZVMzVCbFNnT2loaHE2dl9LTll1aktJS2hmOERLY1AzWHY3Yy00ZUcwQ1Q2eFNGdDBpbzlvVGRQT0ZJNEt4RlJ0eGNIa3NxV2FsN1ZON3p5QUlNblJrMlJDbXRZLVUyVkVDSVgydzJOSlEifQ==");
        VciCredentialsDefinitionRequestDTO vciCredentialsDefinitionRequestDTO = new VciCredentialsDefinitionRequestDTO();
        vciCredentialsDefinitionRequestDTO.setType(Arrays.asList("VerifiableCredential", "MOSIPVerifiableCredential"));
        vciExchangeRequestDTO.setCredentialsDefinition(vciCredentialsDefinitionRequestDTO);
        Errors errors = new BeanPropertyBindingResult(vciExchangeRequestDTO, "vciExchangeRequestDTO");
        vciExchangeRequestValidator.validate(vciExchangeRequestDTO, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void test_validate_withInvalidDIDAsCredentialSubjectId_thenFail() {
        VciExchangeRequestDTO vciExchangeRequestDTO = new VciExchangeRequestDTO();
        vciExchangeRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
        vciExchangeRequestDTO.setTransactionID("transactio");
        vciExchangeRequestDTO.setVcFormat("ldp_vc");
        vciExchangeRequestDTO.setVcAuthToken("vc-auth-token");
        vciExchangeRequestDTO.setCredSubjectId("QUlNblJrMlJDbXRZLVUyVkVDSVgydzJOSlE");
        VciCredentialsDefinitionRequestDTO vciCredentialsDefinitionRequestDTO = new VciCredentialsDefinitionRequestDTO();
        vciCredentialsDefinitionRequestDTO.setType(Arrays.asList("VerifiableCredential", "MOSIPVerifiableCredential"));
        vciExchangeRequestDTO.setCredentialsDefinition(vciCredentialsDefinitionRequestDTO);
        Errors errors = new BeanPropertyBindingResult(vciExchangeRequestDTO, "vciExchangeRequestDTO");
        vciExchangeRequestValidator.validate(vciExchangeRequestDTO, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("credSubjectId"));
    }

    @Test
    public void test_validate_withInvalidRequestTime_thenFail() {
        VciExchangeRequestDTO vciExchangeRequestDTO = new VciExchangeRequestDTO();
        Errors errors = new BeanPropertyBindingResult(vciExchangeRequestDTO, "vciExchangeRequestDTO");
        vciExchangeRequestValidator.validate(vciExchangeRequestDTO, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("requestTime"));
    }

    @Test
    public void test_validate_withInvalidTxnId_thenFail() {
        VciExchangeRequestDTO vciExchangeRequestDTO = new VciExchangeRequestDTO();
        vciExchangeRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
        Errors errors = new BeanPropertyBindingResult(vciExchangeRequestDTO, "vciExchangeRequestDTO");
        vciExchangeRequestValidator.validate(vciExchangeRequestDTO, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("transactionID"));
    }

    @Test
    public void test_validate_withInvalidAuthToken_thenFail() {
        VciExchangeRequestDTO vciExchangeRequestDTO = new VciExchangeRequestDTO();
        vciExchangeRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
        vciExchangeRequestDTO.setTransactionID("transactio");
        Errors errors = new BeanPropertyBindingResult(vciExchangeRequestDTO, "vciExchangeRequestDTO");
        vciExchangeRequestValidator.validate(vciExchangeRequestDTO, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("vcAuthToken"));
    }

    @Test
    public void test_validate_withInvalidCredSubjectId_thenFail() {
        VciExchangeRequestDTO vciExchangeRequestDTO = new VciExchangeRequestDTO();
        vciExchangeRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
        vciExchangeRequestDTO.setTransactionID("transactio");
        vciExchangeRequestDTO.setVcAuthToken("vc-auth-token");
        Errors errors = new BeanPropertyBindingResult(vciExchangeRequestDTO, "vciExchangeRequestDTO");
        vciExchangeRequestValidator.validate(vciExchangeRequestDTO, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("credSubjectId"));
    }

    @Test
    public void test_validate_withInvalidPublicKeyComponentInDID_thenFail() {
        VciExchangeRequestDTO vciExchangeRequestDTO = new VciExchangeRequestDTO();
        vciExchangeRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
        vciExchangeRequestDTO.setTransactionID("transactio");
        vciExchangeRequestDTO.setVcAuthToken("vc-auth-token");
        vciExchangeRequestDTO.setCredSubjectId("did:jwk:eyJrdHkiOiJSU0EiLCJlIjoiQVFBQiIsInVzZSI6InNpZyIsImtpZCI6Inc4VUY3QnE0dDFSeVMxdFJTOHhvVllHUjMySVdiMFZyU3I4M0dEdno3d28iLCJhbGciOiJSUzI1NiJ9");
        Errors errors = new BeanPropertyBindingResult(vciExchangeRequestDTO, "vciExchangeRequestDTO");
        vciExchangeRequestValidator.validate(vciExchangeRequestDTO, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("credSubjectId"));
    }

    @Test
    public void test_validate_withInvalidCredentialFormat_thenFail() {
        VciExchangeRequestDTO vciExchangeRequestDTO = new VciExchangeRequestDTO();
        vciExchangeRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
        vciExchangeRequestDTO.setTransactionID("transactio");
        vciExchangeRequestDTO.setVcAuthToken("vc-auth-token");
        vciExchangeRequestDTO.setCredSubjectId("did:jwk:eyJrdHkiOiJSU0EiLCJlIjoiQVFBQiIsInVzZSI6InNpZyIsImtpZCI6Inc4VUY3QnE0dDFSeVMxdFJTOHhvVllHUjMySVdiMFZyU3I4M0dEdno3d28iLCJhbGciOiJSUzI1NiIsIm4iOiJ5SGY1RjZYMFI5RDNxWm5WaUJORDZRV25pUmVnR2hjQ3NqakVJSENlTWp1UWJHek1LaFB6aFZVWGNtaTBMbGVQVWdUdlhjOWlrRmNnTXM3ckFhckI1dlJEcTh1Mjd2WHNBVjdiOUlZaVVGY3U1ZFZpdTd0Q0F1N0V5cXlLWVlUX20xMzhlZjQxVmU4X29LZVNvT0RRaGxyc0RJTmltX0JwWHBvc0xQVV96MXpfODNxX0ZRU05ydDE2dGhHa0hZeUZsRnhxZnNWZElPTkdoMzRFY3dubFZUY0lQUE5xZVY2RkJ3MENlR2NuaUlSRDZVMzVCbFNnT2loaHE2dl9LTll1aktJS2hmOERLY1AzWHY3Yy00ZUcwQ1Q2eFNGdDBpbzlvVGRQT0ZJNEt4RlJ0eGNIa3NxV2FsN1ZON3p5QUlNblJrMlJDbXRZLVUyVkVDSVgydzJOSlEifQ==");
        Errors errors = new BeanPropertyBindingResult(vciExchangeRequestDTO, "vciExchangeRequestDTO");
        vciExchangeRequestValidator.validate(vciExchangeRequestDTO, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("vcFormat"));

        vciExchangeRequestDTO.setVcFormat("tt");
        errors = new BeanPropertyBindingResult(vciExchangeRequestDTO, "vciExchangeRequestDTO");
        vciExchangeRequestValidator.validate(vciExchangeRequestDTO, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("vcFormat"));
    }

    @Test
    public void test_validate_withInvalidCredentialType_thenFail() {
        VciExchangeRequestDTO vciExchangeRequestDTO = new VciExchangeRequestDTO();
        vciExchangeRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
        vciExchangeRequestDTO.setTransactionID("transactio");
        vciExchangeRequestDTO.setVcAuthToken("vc-auth-token");
        vciExchangeRequestDTO.setVcFormat("ldp_vc");
        vciExchangeRequestDTO.setCredSubjectId("did:jwk:eyJrdHkiOiJSU0EiLCJlIjoiQVFBQiIsInVzZSI6InNpZyIsImtpZCI6Inc4VUY3QnE0dDFSeVMxdFJTOHhvVllHUjMySVdiMFZyU3I4M0dEdno3d28iLCJhbGciOiJSUzI1NiIsIm4iOiJ5SGY1RjZYMFI5RDNxWm5WaUJORDZRV25pUmVnR2hjQ3NqakVJSENlTWp1UWJHek1LaFB6aFZVWGNtaTBMbGVQVWdUdlhjOWlrRmNnTXM3ckFhckI1dlJEcTh1Mjd2WHNBVjdiOUlZaVVGY3U1ZFZpdTd0Q0F1N0V5cXlLWVlUX20xMzhlZjQxVmU4X29LZVNvT0RRaGxyc0RJTmltX0JwWHBvc0xQVV96MXpfODNxX0ZRU05ydDE2dGhHa0hZeUZsRnhxZnNWZElPTkdoMzRFY3dubFZUY0lQUE5xZVY2RkJ3MENlR2NuaUlSRDZVMzVCbFNnT2loaHE2dl9LTll1aktJS2hmOERLY1AzWHY3Yy00ZUcwQ1Q2eFNGdDBpbzlvVGRQT0ZJNEt4RlJ0eGNIa3NxV2FsN1ZON3p5QUlNblJrMlJDbXRZLVUyVkVDSVgydzJOSlEifQ==");
        VciCredentialsDefinitionRequestDTO vciCredentialsDefinitionRequestDTO = new VciCredentialsDefinitionRequestDTO();
        vciExchangeRequestDTO.setCredentialsDefinition(vciCredentialsDefinitionRequestDTO);
        Errors errors = new BeanPropertyBindingResult(vciExchangeRequestDTO, "vciExchangeRequestDTO");
        vciExchangeRequestValidator.validate(vciExchangeRequestDTO, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("credentialsDefinition"));


        vciCredentialsDefinitionRequestDTO = new VciCredentialsDefinitionRequestDTO();
        vciCredentialsDefinitionRequestDTO.setType(Arrays.asList("VerifiableCredentialssss", "MOSIPVerifiableCredential"));
        vciExchangeRequestDTO.setCredentialsDefinition(vciCredentialsDefinitionRequestDTO);
        errors = new BeanPropertyBindingResult(vciExchangeRequestDTO, "vciExchangeRequestDTO");
        vciExchangeRequestValidator.validate(vciExchangeRequestDTO, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("credentialsDefinition"));
    }

}
