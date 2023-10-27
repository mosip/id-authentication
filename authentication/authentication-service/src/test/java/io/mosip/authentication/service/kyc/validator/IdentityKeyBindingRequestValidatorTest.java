package io.mosip.authentication.service.kyc.validator;


import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingRequestDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
public class IdentityKeyBindingRequestValidatorTest {

    @Mock
    IdInfoHelper idInfoHelper;

    @Mock
    Errors errors;

    @InjectMocks
    IdentityKeyBindingRequestValidator identityKeyBindingRequestValidator;

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
        Assert.assertTrue(errors.hasErrors());
    }
}
