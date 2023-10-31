package io.mosip.authentication.service.kyc.controller;

import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.TestHttpServletRequest;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.VciCredentialsDefinitionRequestDTO;
import io.mosip.authentication.core.indauth.dto.VciExchangeRequestDTO;
import io.mosip.authentication.core.indauth.dto.VciExchangeResponseDTO;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.indauth.facade.VciFacade;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.service.kyc.controller.VCIController;
import io.mosip.authentication.service.kyc.validator.VciExchangeRequestValidator;
import org.apache.struts.mock.MockHttpServletRequest;
import org.junit.Assert;
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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;


@RunWith(SpringRunner.class)
@WebMvcTest(value = VCIController.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@Import(EnvUtil.class)
public class VCIControllerTest {

    @Mock
    private VciFacade vciFacade;

    @Mock
    private IdTypeUtil idTypeUtil;

    @Mock
    private AuthTransactionHelper authTransactionHelper;

    @Mock
    private PartnerService partnerService;

    @Mock
    private VciExchangeRequestValidator vciExchangeRequestValidator;

    @InjectMocks
    VCIController vciController;


    @Test
    public void delegatedVCExchange_withValidInput_thenPass() throws Exception {
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

        TestHttpServletRequest requestWithMetadata = new TestHttpServletRequest();
        requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_DATA, "identity data");
        requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_INFO, "identity info");

        VciExchangeResponseDTO vciExchangeResponseDTO = new VciExchangeResponseDTO();

        vciExchangeRequestDTO.setIndividualIdType(IdType.UIN.getType());
        Optional<PartnerDTO> partner = Optional.empty();
        Mockito.when(partnerService.getPartner("partnerId", vciExchangeRequestDTO.getMetadata())).thenReturn(partner);
        Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(vciExchangeRequestDTO, !false, partner))
                .thenReturn(AuthTransactionBuilder.newInstance());

        Mockito.when(vciFacade.processVciExchange(vciExchangeRequestDTO, "auth-partner-id",
                "oidc-client-id", vciExchangeRequestDTO.getMetadata(), requestWithMetadata)).thenReturn(vciExchangeResponseDTO);

        vciExchangeResponseDTO = vciController.vciExchange(vciExchangeRequestDTO, errors, "license-key", "auth-partner-id",
                "oidc-client-id", requestWithMetadata);

        Assert.assertNotNull(vciExchangeResponseDTO);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void delegatedVCExchange_withInvalidInput_thenFail() throws Exception {
        VciExchangeRequestDTO vciExchangeRequestDTO = new VciExchangeRequestDTO();
        Errors errors = new BeanPropertyBindingResult(vciExchangeRequestDTO, "vciExchangeRequestDTO");
        vciController.vciExchange(vciExchangeRequestDTO, errors, "license-key", "auth-partner-id",
                "oidc-client-id", new MockHttpServletRequest());
    }

    @Test(expected = IdAuthenticationAppException.class)
    public void delegatedVCExchange_withInternalError_thenFail() throws Exception {
        VciExchangeRequestDTO vciExchangeRequestDTO = new VciExchangeRequestDTO();
        Errors errors = new BeanPropertyBindingResult(vciExchangeRequestDTO, "vciExchangeRequestDTO");
        TestHttpServletRequest requestWithMetadata = new TestHttpServletRequest();
        requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_DATA, "identity data");
        requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_INFO, "identity info");

        vciExchangeRequestDTO.setIndividualIdType(IdType.UIN.getType());
        AuthTransactionBuilder authTxnBuilder = AuthTransactionBuilder.newInstance();
        Optional<PartnerDTO> partner = Optional.empty();
        Mockito.when(partnerService.getPartner("partnerId", vciExchangeRequestDTO.getMetadata())).thenReturn(partner);
        Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(vciExchangeRequestDTO, !false, partner))
                .thenReturn(authTxnBuilder);
        IDDataValidationException idDataValidationException = new IDDataValidationException("error-code","error-message");
        Mockito.when(authTransactionHelper.createDataValidationException(null, idDataValidationException, requestWithMetadata))
                .thenReturn(new IdAuthenticationAppException());

        Mockito.when(vciFacade.processVciExchange(vciExchangeRequestDTO, "auth-partner-id",
                "oidc-client-id", vciExchangeRequestDTO.getMetadata(), requestWithMetadata))
                .thenThrow(idDataValidationException);

        vciController.vciExchange(vciExchangeRequestDTO, errors, "license-key", "auth-partner-id",
                "oidc-client-id", requestWithMetadata);
    }

    @Test(expected = IdAuthenticationAppException.class)
    public void delegatedVCIExchange_withInternalError2_thenFail() throws Exception {
        VciExchangeRequestDTO vciExchangeRequestDTO = new VciExchangeRequestDTO();
        Errors errors = new BeanPropertyBindingResult(vciExchangeRequestDTO, "vciExchangeRequestDTO");
        TestHttpServletRequest requestWithMetadata = new TestHttpServletRequest();
        requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_DATA, "identity data");
        requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_INFO, "identity info");

        vciExchangeRequestDTO.setIndividualIdType(IdType.UIN.getType());
        AuthTransactionBuilder authTxnBuilder = AuthTransactionBuilder.newInstance();
        Optional<PartnerDTO> partner = Optional.empty();
        Mockito.when(partnerService.getPartner("partnerId", vciExchangeRequestDTO.getMetadata())).thenReturn(partner);
        Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(vciExchangeRequestDTO, !false, partner))
                .thenReturn(authTxnBuilder);
        IdAuthenticationBusinessException idAuthenticationBusinessException = new IdAuthenticationBusinessException("error-code","error-message");
        Mockito.when(vciFacade.processVciExchange(vciExchangeRequestDTO, "auth-partner-id",
                        "oidc-client-id", vciExchangeRequestDTO.getMetadata(), requestWithMetadata))
                .thenThrow(idAuthenticationBusinessException);

        vciController.vciExchange(vciExchangeRequestDTO, errors, "license-key", "auth-partner-id",
                "oidc-client-id", requestWithMetadata);
    }
}
