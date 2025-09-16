package io.mosip.authentication.service.kyc.validator;

import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.indauth.dto.KycExchangeRequestDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@Import(EnvUtil.class)
public class KycExchangeRequestValidatorTest {
    @InjectMocks
    KycExchangeRequestValidator kycExchangeRequestValidator;
    @Mock
    IdInfoHelper idInfoHelper;
    @InjectMocks
    AuthRequestValidator authRequestValidator;
    @Before
    public void before() {
        ReflectionTestUtils.setField(kycExchangeRequestValidator, "idInfoHelper", idInfoHelper);
        ReflectionTestUtils.setField(authRequestValidator, "idInfoHelper", idInfoHelper);
    }
    @Test
    public void testSupportTrue() {
        assertTrue(kycExchangeRequestValidator.supports(KycExchangeRequestDTO.class));
    }
    @Test
    public void testSupportFalse() {
        assertFalse(kycExchangeRequestValidator.supports(KycAuthRequestValidator.class));
    }
    @Test
    @Ignore
    public void testValidate_ValidRequest_NoErrors() {
        KycExchangeRequestDTO kycExchangeRequestDTO = new KycExchangeRequestDTO();
        kycExchangeRequestDTO.setId("id");
        kycExchangeRequestDTO.setRequestTime(ZonedDateTime.now()
                .format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
        kycExchangeRequestDTO.setKycToken("token");
        kycExchangeRequestDTO.setTransactionID("1234567890");
        List<String> consentObtained=new ArrayList<>();
        consentObtained.add("phone");
        consentObtained.add("email");
        kycExchangeRequestDTO.setConsentObtained(consentObtained);
        List<String> locales=new ArrayList<>();
        locales.add("en");
        kycExchangeRequestDTO.setLocales(locales);
        kycExchangeRequestDTO.setRespType("abc");
        Map<String,Object> metadata=new HashMap<>();
        kycExchangeRequestDTO.setMetadata(metadata);
        Errors errors = new BeanPropertyBindingResult(kycExchangeRequestDTO, "kycExchangeRequestDTO");
        Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
        kycExchangeRequestValidator.validate(kycExchangeRequestDTO, errors);
        Assert.assertFalse(errors.hasErrors());
    }
    @Test
    public void testValidate_NullRequest_InvalidInputParameterError() {
        KycExchangeRequestDTO kycExchangeRequestDTO = new KycExchangeRequestDTO();
        Errors errors = new BeanPropertyBindingResult(kycExchangeRequestDTO, "kycExchangeRequestDTO");
        kycExchangeRequestValidator.validate(kycExchangeRequestDTO, errors);
        Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("IDA-MLC-006", errors.getFieldError().getCode());
    }
    @Test
    public void testValidate_InvalidKycToken_MissingInputParameterError() {
        KycExchangeRequestDTO request = new KycExchangeRequestDTO();
        request.setRequestTime("2023-10-31 10:00:00");
        request.setKycToken(null);
        request.setTransactionID("1234567890");
        Errors errors = new BeanPropertyBindingResult(request, "kycExchangeRequestDTO");
        kycExchangeRequestValidator.validate(request, errors);
        Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("IDA-MLC-009", errors.getFieldError().getCode());
    }
    @Test
    public void testValidate_EmptyConsentObtainedList_MissingInputParameterError() {
        KycExchangeRequestDTO request = new KycExchangeRequestDTO();
        request.setRequestTime("2023-10-31 10:00:00");
        request.setKycToken("exampleToken");
        request.setTransactionID("exampleTransactionID");
        request.setConsentObtained(new ArrayList<>());
        Errors errors = new BeanPropertyBindingResult(request, "kycExchangeRequestDTO");
        Mockito.when(idInfoHelper.isMatchtypeEnabled(Mockito.any())).thenReturn(Boolean.TRUE);
        kycExchangeRequestValidator.validate(request, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals("IDA-MLC-009", errors.getFieldError().getCode());
    }
}