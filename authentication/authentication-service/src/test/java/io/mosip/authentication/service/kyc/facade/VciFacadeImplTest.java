package io.mosip.authentication.service.kyc.facade;



import io.mosip.authentication.common.manager.IdAuthFraudAnalysisEventManager;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.entity.KycTokenData;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.TokenValidationHelper;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.repository.KycTokenDataRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.TestObjectWithMetadata;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.VciCredentialsDefinitionRequestDTO;
import io.mosip.authentication.core.indauth.dto.VciExchangeRequestDTO;
import io.mosip.authentication.core.partner.dto.*;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.service.kyc.impl.VciServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

@RunWith(SpringRunner.class)
@Import(EnvUtil.class)
public class VciFacadeImplTest {

    @Autowired
    EnvUtil env;

    @Mock
    EnvUtil envMock;

    @Mock
    IdService<AutnTxn> idService;

    /** The AuditHelper */
    @Mock
    AuditHelper auditHelper;

    @Mock
    IdaUinHashSaltRepo uinHashSaltRepo;

    @Mock
    TokenIdManager tokenIdManager;

    @Mock
    IdAuthSecurityManager securityManager;

    @Mock
    PartnerService partnerService;

    @Mock
    IdAuthFraudAnalysisEventManager fraudEventManager;

    @Mock
    VciServiceImpl vciServiceImpl;

    @Mock
    TokenValidationHelper tokenValidationHelper;

    @Mock
    KycTokenDataRepository kycTokenDataRepo;

    @InjectMocks
    VciFacadeImpl vciFacadeImpl;

    @Test
    public void processVciExchangeTestWithInvalidDetails_ThenFail() throws IdAuthenticationBusinessException {

        VciExchangeRequestDTO vciExchangeRequestDTO = new VciExchangeRequestDTO();
        vciExchangeRequestDTO.setId("12345678901234567890123456789012");
        vciExchangeRequestDTO.setMetadata(new HashMap<>()) ;
        vciExchangeRequestDTO.setRequestTime("2019-07-15T12:00:00.000Z");
        vciExchangeRequestDTO.setVcAuthToken("12345678901234567890123456789012");
        vciExchangeRequestDTO.setCredSubjectId("12345678901234567890123456789012");
        vciExchangeRequestDTO.setVcFormat("WLA");
        vciExchangeRequestDTO.setIndividualId("1234567890");
        vciExchangeRequestDTO.setIndividualIdType("UIN");

        VciCredentialsDefinitionRequestDTO vciCredentialsDefinitionRequestDTO = new VciCredentialsDefinitionRequestDTO();
        vciCredentialsDefinitionRequestDTO.setContext(List.of("https://www.w3.org/2018/credentials/v1"));
        vciCredentialsDefinitionRequestDTO.setType(List.of("VerifiableCredential"));
        vciCredentialsDefinitionRequestDTO.setCredentialSubject(new HashMap<>());

        vciExchangeRequestDTO.setCredentialsDefinition(vciCredentialsDefinitionRequestDTO);

        Map<String,Object> metaData=new HashMap<>();

        try{
            vciFacadeImpl.processVciExchange(vciExchangeRequestDTO,"1234567890","12345",metaData,new TestObjectWithMetadata());
        }catch(IdAuthenticationBusinessException e){
            Assert.assertEquals("IDA-KYE-006",e.getErrorCode());
        }
    }

    @Test
    public void processVciExchangeTestWithInValidTxnDetails_ThenFail() throws IdAuthenticationBusinessException {
        ReflectionTestUtils.setField(vciFacadeImpl, "tokenValidationHelper", tokenValidationHelper);

        VciExchangeRequestDTO vciExchangeRequestDTO = new VciExchangeRequestDTO();
        vciExchangeRequestDTO.setId("12345678901234567890123456789012");
        vciExchangeRequestDTO.setMetadata(new HashMap<>()) ;
        vciExchangeRequestDTO.setRequestTime("2019-07-15T12:00:00.000Z");
        vciExchangeRequestDTO.setVcAuthToken("12345678901234567890123456789012");
        vciExchangeRequestDTO.setCredSubjectId("12345678901234567890123456789012");
        vciExchangeRequestDTO.setVcFormat("WLA");
        vciExchangeRequestDTO.setIndividualId("1234567890");
        vciExchangeRequestDTO.setIndividualIdType("UIN");
        vciExchangeRequestDTO.setTransactionID("12345");

        VciCredentialsDefinitionRequestDTO vciCredentialsDefinitionRequestDTO = new VciCredentialsDefinitionRequestDTO();
        vciCredentialsDefinitionRequestDTO.setContext(List.of("https://www.w3.org/2018/credentials/v1"));
        vciCredentialsDefinitionRequestDTO.setType(List.of("VerifiableCredential"));
        vciCredentialsDefinitionRequestDTO.setCredentialSubject(new HashMap<>());

        vciExchangeRequestDTO.setCredentialsDefinition(vciCredentialsDefinitionRequestDTO);

        Map<String,Object> metaData=new HashMap<>();

        PartnerPolicyResponseDTO partnerPolicyResponseDTO = new PartnerPolicyResponseDTO();
        partnerPolicyResponseDTO.setMispPolicyId("1234567890");

        MispPolicyDTO mispPolicyDTO = new MispPolicyDTO();
        mispPolicyDTO.setAllowKeyBindingDelegation(true);
        mispPolicyDTO.setAllowKycRequestDelegation(true);
        mispPolicyDTO.setAllowOTPRequestDelegation(true);

        PolicyDTO policyDTO = new PolicyDTO();

        List<AuthPolicy> listOfPolicy=new ArrayList<>();

        AuthPolicy authPolicy=new AuthPolicy();
        authPolicy.setAuthType("OTP");
        authPolicy.setAuthSubType("OTP");
        authPolicy.setMandatory(false);

        listOfPolicy.add(authPolicy);

        policyDTO.setAllowedAuthTypes(listOfPolicy);
        policyDTO.setAuthTokenType("OTP");

        partnerPolicyResponseDTO.setPolicy(policyDTO);
        partnerPolicyResponseDTO.setMispPolicy(mispPolicyDTO);
        partnerPolicyResponseDTO.setPartnerId("1234567890");

        KycTokenData kycTokenData = new KycTokenData();
        kycTokenData.setPsuToken("1234567890");

        Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("1234567890");
        Mockito.when(tokenValidationHelper.findAndValidateIssuedToken("12345678901234567890123456789012","12345","12345","1234567890")).thenReturn(kycTokenData);
        Mockito.when(partnerService.getPolicyForPartner(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap())).thenReturn(Optional.of(partnerPolicyResponseDTO));
        Mockito.when(idService.getToken(Mockito.any())).thenReturn("token");
        Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(),Mockito.anyString())).thenReturn("1234567890");

        EnvUtil.setAuthTokenRequired(true);
        PartnerDTO partnerDTO= new PartnerDTO();
        partnerDTO.setPartnerId("12345");
        partnerDTO.setPartnerName("relyingPartyId");
        Mockito.when(partnerService.getPartner(Mockito.anyString(),Mockito.anyMap())).thenReturn(Optional.of(partnerDTO));
        try{
            vciFacadeImpl.processVciExchange(vciExchangeRequestDTO,"1234567890","12345",metaData,new TestObjectWithMetadata());
        }catch (Exception e){
        }

    }
}
