package io.mosip.authentication.common.service.helper;

import io.mosip.authentication.common.service.entity.KycTokenData;
import io.mosip.authentication.common.service.entity.OIDCClientData;
import io.mosip.authentication.common.service.repository.KycTokenDataRepository;
import io.mosip.authentication.common.service.repository.OIDCClientDataRepository;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

@RunWith(SpringRunner.class)
public class TokenValidationHelperTest {

    /** The Kyc Service */
    @Mock
    private KycService kycService;

    @Mock
    private KycTokenDataRepository kycTokenDataRepo;

    @Mock
    private IdInfoHelper idInfoHelper;

    @Mock
    private OIDCClientDataRepository oidcClientDataRepo;

    @InjectMocks
    TokenValidationHelper tokenValidationHelper;


    @Test
    public void findAndValidateIssuedTokenTestWithValidDetails_thenPass() throws IdAuthenticationBusinessException {

        KycTokenData kycTokenData = new KycTokenData();
        kycTokenData.setPsuToken("1234567890");
        kycTokenData.setKycToken("1234567890");
        kycTokenData.setKycTokenStatus("ACTIVE");
        kycTokenData.setOidcClientId("12345");
        kycTokenData.setTokenIssuedDateTime(LocalDateTime.now());
        kycTokenData.setIdVidHash("1234567");
        kycTokenData.setRequestTransactionId("123456");
        Mockito.when(kycTokenDataRepo.findByKycToken(Mockito.anyString())).thenReturn(Optional.of(kycTokenData));
        Mockito.when( kycService.isKycTokenExpire(Mockito.any(),Mockito.anyString())).thenReturn(false);

        tokenValidationHelper.findAndValidateIssuedToken("1234567890", "12345", "123456", "1234567");

    }

    @Test
    public void findAndValidateIssuedTokenTestWithInValidTokenDetails_thenFail() throws IdAuthenticationBusinessException {

        Mockito.when(kycTokenDataRepo.findByKycToken(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when( kycService.isKycTokenExpire(Mockito.any(),Mockito.anyString())).thenReturn(false);

        try{
            tokenValidationHelper.findAndValidateIssuedToken("1234567890", "12346", "123456", "1234567");
        }catch (IdAuthenticationBusinessException e){
            assert(e.getErrorCode().equalsIgnoreCase("IDA-KYE-001"));
        }

    }

    @Test
    public void findAndValidateIssuedTokenTestWithExpiredTokenDetails_thenFail() throws IdAuthenticationBusinessException {

        KycTokenData kycTokenData = new KycTokenData();
        kycTokenData.setPsuToken("1234567890");
        kycTokenData.setKycToken("1234567890");
        kycTokenData.setKycTokenStatus("ACTIVE");
        kycTokenData.setOidcClientId("12345");
        kycTokenData.setTokenIssuedDateTime(LocalDateTime.now());
        kycTokenData.setIdVidHash("1234567");
        kycTokenData.setRequestTransactionId("123456");
        Mockito.when(kycTokenDataRepo.findByKycToken(Mockito.anyString())).thenReturn(Optional.of(kycTokenData));
        Mockito.when( kycService.isKycTokenExpire(Mockito.any(),Mockito.anyString())).thenReturn(true);

        try{
            tokenValidationHelper.findAndValidateIssuedToken("1234567890", "12345", "123456", "1234567");
        }catch (IdAuthenticationBusinessException e){
            assert(e.getErrorCode().equalsIgnoreCase("IDA-KYE-002"));
        }

    }

    @Test
    public void findAndValidateIssuedTokenTestWithInValidDetails_thenPass() throws IdAuthenticationBusinessException {

        KycTokenData kycTokenData = new KycTokenData();
        kycTokenData.setPsuToken("1234567890");
        kycTokenData.setKycToken("1234567890");
        kycTokenData.setKycTokenStatus("ACTIVE");
        kycTokenData.setOidcClientId("12345");
        kycTokenData.setTokenIssuedDateTime(LocalDateTime.now());
        kycTokenData.setIdVidHash("1234567");
        kycTokenData.setRequestTransactionId("123456");
        Mockito.when(kycTokenDataRepo.findByKycToken(Mockito.anyString())).thenReturn(Optional.of(kycTokenData));
        Mockito.when( kycService.isKycTokenExpire(Mockito.any(),Mockito.anyString())).thenReturn(false);


        try{
            tokenValidationHelper.findAndValidateIssuedToken("1234567890", "12346", "123456", "1234567");
        }catch (IdAuthenticationBusinessException e){
            assert(e.getErrorCode().equalsIgnoreCase("IDA-KYE-004"));
        }

        try{
            tokenValidationHelper.findAndValidateIssuedToken("1234567890", "12345", "123457", "1234567");
        }catch (IdAuthenticationBusinessException e){
            assert(e.getErrorCode().equalsIgnoreCase("IDA-KYE-005"));
        }

        try{
            tokenValidationHelper.findAndValidateIssuedToken("1234567890", "12345", "123456", "1234568");
        }catch (IdAuthenticationBusinessException e){
            assert(e.getErrorCode().equalsIgnoreCase("IDA-KYE-007"));
        }

        kycTokenData.setKycTokenStatus("EXPIRED");
        try{
            tokenValidationHelper.findAndValidateIssuedToken("1234567890", "12345", "123456", "1234567");
        }catch (IdAuthenticationBusinessException e){
            assert(e.getErrorCode().equalsIgnoreCase("IDA-KYE-002"));
        }

        kycTokenData.setKycTokenStatus("PROCESSED");
        try{
            tokenValidationHelper.findAndValidateIssuedToken("1234567890", "12345", "123456", "1234567");
        }catch (IdAuthenticationBusinessException e){
            assert(e.getErrorCode().equalsIgnoreCase("IDA-KYE-003"));
        }
    }

    @Test
    public void mapConsentedAttributesToIdSchemaAttributesTest() throws IdAuthenticationBusinessException {
        ReflectionTestUtils.setField(tokenValidationHelper, "consentedIndividualIdAttributeName", "consentedIndividualIdAttributeName");
        List<String> policyAllowedKycAttribs =new ArrayList<>();
        Set<String> filterAttributes = new HashSet<>();
        List<String> consentAttributes = new ArrayList<>();

        consentAttributes.add("name");
        tokenValidationHelper.mapConsentedAttributesToIdSchemaAttributes(consentAttributes, filterAttributes, policyAllowedKycAttribs);

    }

    @Test
    public void filterByPolicyAllowedAttributesTest() throws IdAuthenticationBusinessException {
        ReflectionTestUtils.setField(tokenValidationHelper, "consentedIndividualIdAttributeName", "consentedIndividualIdAttributeName");
        List<String> policyAllowedKycAttribs =new ArrayList<>();
        Set<String> filterAttributes = new HashSet<>();


        tokenValidationHelper.filterByPolicyAllowedAttributes(filterAttributes, policyAllowedKycAttribs);

    }

    @Test
    public void filterAllowedUserClaimsTestWithValidDetails_thenPass() throws IdAuthenticationBusinessException {
        ReflectionTestUtils.setField(tokenValidationHelper, "consentedIndividualIdAttributeName", "consentedIndividualIdAttributeName");
        List<String> consentAttributes =new ArrayList<>();

        consentAttributes.add("name");


        OIDCClientData oidcClientData = new OIDCClientData();
        oidcClientData.setUserClaims(new String[]{"name"});
        Mockito.when(oidcClientDataRepo.findByClientId(Mockito.anyString())).thenReturn(Optional.of(oidcClientData));


        tokenValidationHelper.filterAllowedUserClaims("oidcClientId", consentAttributes);

    }
}
