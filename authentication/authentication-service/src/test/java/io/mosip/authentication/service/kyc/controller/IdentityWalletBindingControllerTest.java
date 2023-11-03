package io.mosip.authentication.service.kyc.controller;


import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.util.TestHttpServletRequest;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.*;
import io.mosip.authentication.core.spi.indauth.facade.IdentityKeyBindingFacade;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.service.kyc.validator.IdentityKeyBindingRequestValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@RunWith(SpringRunner.class)
public class IdentityWalletBindingControllerTest {

    /** The auth facade. */
    @Mock
    IdentityKeyBindingFacade keyIdentityFacade;

    @Mock
    AuditHelper auditHelper;

    @Mock
    IdTypeUtil idTypeUtil;

    @Mock
    AuthTransactionHelper authTransactionHelper;

    @Mock
    PartnerService partnerService;

    /** The KycExchangeRequestValidator */
    @Mock
    IdentityKeyBindingRequestValidator identityKeyBindingRequestValidator;

    @InjectMocks
    IdentityWalletBindingController identityWalletBindingController;

    Errors errors = new BindException(IdentityKeyBindingRequestDTO.class, "identityKeyBindingRequestDTO");


    IdentityKeyBindingDTO identityKeyBindingDTO;

    IdentityKeyBindingRequestDTO identityKeyBindingRequestDTO;

    AuthResponseDTO authResponseDTO;

    IdentityKeyBindingResponseDto keyBindingResponseDto;

    IdentityKeyBindingRespDto identityKeyBindingRespDto;

    @Before
    public void before() {
        identityKeyBindingDTO = new IdentityKeyBindingDTO();
        identityKeyBindingDTO.setPublicKeyJWK(null);
        identityKeyBindingDTO.setAuthFactorType("WLA");

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setDemographics(null);
        requestDTO.setBiometrics(null);
        requestDTO.setOtp(null);
        requestDTO.setStaticPin(null);
        requestDTO.setTimestamp(null);

        identityKeyBindingRequestDTO = new IdentityKeyBindingRequestDTO();

        identityKeyBindingRequestDTO.setIdentityKeyBinding(identityKeyBindingDTO);

        identityKeyBindingRequestDTO.setRequest(requestDTO);
        identityKeyBindingRequestDTO.setConsentObtained(false);
        identityKeyBindingRequestDTO.setRequestHMAC(null);
        identityKeyBindingRequestDTO.setRequestSessionKey(null);
        identityKeyBindingRequestDTO.setMetadata(null);
        identityKeyBindingRequestDTO.setIndividualIdType("UIN");


        keyBindingResponseDto = new IdentityKeyBindingResponseDto();

        IdentityKeyBindingRespDto identityKeyBindingRespDto = new IdentityKeyBindingRespDto();
        identityKeyBindingRespDto.setIdentityCertificate(null);
        identityKeyBindingRespDto.setBindingAuthStatus(true);
        identityKeyBindingRespDto.setAuthToken("token");
        keyBindingResponseDto.setResponse(identityKeyBindingRespDto);

        authResponseDTO= new AuthResponseDTO();
        authResponseDTO.setId("123");
        authResponseDTO.setResponseTime("123");
        authResponseDTO.setResponse(null);

    }

    @Test
    public void processIdKeyBindingTest() throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IdAuthenticationAppException {


        Mockito.when(partnerService.getPartner(Mockito.anyString(),Mockito.anyMap())).thenReturn(null);
        Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(Mockito.any(),Mockito.anyBoolean(),Mockito.any())).thenReturn(null);

        TestHttpServletRequest requestWithMetadata = new TestHttpServletRequest();
        requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_DATA, "identity data");;
        requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_INFO, "identity info");

        Mockito.when(keyIdentityFacade.authenticateIndividual(Mockito.any(),Mockito.anyString(),Mockito.anyString(),Mockito.any())).thenReturn(authResponseDTO);

        Mockito.when(keyIdentityFacade.processIdentityKeyBinding(Mockito.any(),Mockito.any(),Mockito.anyString(),Mockito.anyString(),Mockito.any())).thenReturn(keyBindingResponseDto);
        IdentityKeyBindingResponseDto identityKeyBindingResponseDto = identityWalletBindingController.processIdKeyBinding(identityKeyBindingRequestDTO, errors, "123", "123", "123", requestWithMetadata);
        Assert.assertEquals(keyBindingResponseDto,identityKeyBindingResponseDto);
    }

    @Test
    public void processIdKeyBindingWithInvalidDetails_thenFail() throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IdAuthenticationAppException {

        Mockito.when(partnerService.getPartner(Mockito.anyString(),Mockito.anyMap())).thenReturn(null);
        Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(Mockito.any(),Mockito.anyBoolean(),Mockito.any())).thenReturn(null);
        //Mockito.when(identityKeyBindingRequestValidator.validateIdvId(Mockito.anyString(),Mockito.anyString(),errors)).thenReturn(null);

        TestHttpServletRequest requestWithMetadata = new TestHttpServletRequest();
        requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_DATA, "identity data");;
        requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_INFO, "identity info");

        Mockito.when(keyIdentityFacade.authenticateIndividual(Mockito.any(),Mockito.anyString(),Mockito.anyString(),Mockito.any())).thenThrow(IdAuthenticationBusinessException.class);

        keyBindingResponseDto.setResponse(identityKeyBindingRespDto);
        Mockito.when(keyIdentityFacade.processIdentityKeyBinding(Mockito.any(),Mockito.any(),Mockito.anyString(),Mockito.anyString(),Mockito.any())).thenReturn(keyBindingResponseDto);
        Mockito.when(authTransactionHelper.createDataValidationException(Mockito.any(),Mockito.any(),Mockito.any())).thenThrow(IdAuthenticationAppException.class);
        try{
            Errors errors = new BindException(identityKeyBindingRequestDTO, "identityKeyBindingRequestDTO");
            errors.rejectValue("id", "errorCode", "defaultMessage");
            IdentityKeyBindingResponseDto identityKeyBindingResponseDto = identityWalletBindingController.processIdKeyBinding(identityKeyBindingRequestDTO, errors, "123", "123", "123", requestWithMetadata);
            Assert.fail();
        }catch (Exception e){}

    }

    @Test
    public void processIdKeyBindingTest2() throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IdAuthenticationAppException {
        Mockito.when(partnerService.getPartner(Mockito.anyString(),Mockito.anyMap())).thenReturn(null);
        Mockito.when(authTransactionHelper.createAndSetAuthTxnBuilderMetadataToRequest(Mockito.any(),Mockito.anyBoolean(),Mockito.any())).thenReturn(null);

        TestHttpServletRequest requestWithMetadata = new TestHttpServletRequest();
        requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_DATA, "identity data");;
        requestWithMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_INFO, "identity info");

        Mockito.when(keyIdentityFacade.authenticateIndividual(Mockito.any(),Mockito.anyString(),Mockito.anyString(),Mockito.any())).thenThrow(new IdAuthenticationBusinessException("IDA-IKB-004","error"));
        Mockito.when(keyIdentityFacade.processIdentityKeyBinding(Mockito.any(),Mockito.any(),Mockito.anyString(),Mockito.anyString(),Mockito.any())).thenReturn(keyBindingResponseDto);
        try{
            IdentityKeyBindingResponseDto identityKeyBindingResponseDto = identityWalletBindingController.processIdKeyBinding(identityKeyBindingRequestDTO, errors, "123", "123", "123", requestWithMetadata);
            Assert.fail();
        }catch (Exception e){}
    }

}
