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
import io.mosip.authentication.core.indauth.dto.VciExchangeResponseDTO;
import io.mosip.authentication.core.partner.dto.*;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.service.kyc.impl.VciServiceImpl;
import io.mosip.authentication.service.kyc.util.ExchangeDataAttributesUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.*;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.partner.dto.KYCAttributes;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.partner.dto.PolicyDTO;
import org.junit.Before;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.indauth.dto.IdType;

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

    @Mock private ExchangeDataAttributesUtil exchangeDataAttributesUtil;

    @Captor
    private ArgumentCaptor<List<String>> localesCaptor;

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

    private VciExchangeRequestDTO baseRequest() {
        VciExchangeRequestDTO dto = new VciExchangeRequestDTO();
        dto.setId("req-id-123");
        dto.setTransactionID("txn-001");
        dto.setVersion("1.0");
        dto.setRequestTime("2025-01-01T00:00:00.000Z");
        dto.setVcAuthToken("vc-auth-token");
        dto.setCredSubjectId("cred-subj-001");
        dto.setVcFormat("W3C");
        dto.setIndividualId("123456789012");
        dto.setIndividualIdType("UIN");
        dto.setMetadata(new HashMap<>());
        // leave locales null for the defaulting branch in main success test
        VciCredentialsDefinitionRequestDTO cd = new VciCredentialsDefinitionRequestDTO();
        cd.setContext(Collections.singletonList("https://www.w3.org/2018/credentials/v1"));
        cd.setType(Collections.singletonList("VerifiableCredential"));
        cd.setCredentialSubject(new HashMap<>());
        dto.setCredentialsDefinition(cd);
        return dto;
    }

    private PartnerPolicyResponseDTO policyAllowingPhoto() {
        KYCAttributes attr = new KYCAttributes();
        attr.setAttributeName(IdAuthCommonConstants.PHOTO.toLowerCase()); // ensures isBioRequired = true path
        PolicyDTO policy = new PolicyDTO();
        policy.setAllowedKycAttributes(Collections.singletonList(attr));
        PartnerPolicyResponseDTO resp = new PartnerPolicyResponseDTO();
        resp.setPolicy(policy);
        return resp;
    }

    @Before
    public void setUp() {
        // ensure VciFacadeImpl.env bean is present for AuthTransactionBuilder
        ReflectionTestUtils.setField(vciFacadeImpl, "env", env);
    }

    @Test
    public void processVciExchange_policyMissing_throwsAndAudits() throws Exception {
        VciExchangeRequestDTO req = baseRequest();
        Map<String, Object> metadata = new HashMap<>();

        when(securityManager.hash(anyString())).thenReturn("idvid-hash");
        // partnerService returns empty -> triggers PARTNER_POLICY_NOT_FOUND
        when(partnerService.getPolicyForPartner(anyString(), anyString(), anyMap()))
                .thenReturn(Optional.empty());

        try {
            vciFacadeImpl.processVciExchange(req, "partner-1", "oidc-1", metadata, new TestObjectWithMetadata());
            fail("Expected IdAuthenticationBusinessException");
        } catch (IdAuthenticationBusinessException ex) {
            // error code asserted in your original test; here we just ensure it propagated
            assertNotNull(ex.getErrorCode());
        }

        verify(auditHelper, times(1)).audit(
                any(AuditModules.class),
                any(AuditEvents.class),
                eq("txn-001"),
                any(IdType.class),
                any(IdAuthenticationBusinessException.class)
        );
    }

    @Test
    public void saveToTxnTable_tokenNull_noop() {
        VciExchangeRequestDTO req = baseRequest();
        VciExchangeResponseDTO resp = new VciExchangeResponseDTO();

        ReflectionTestUtils.invokeMethod(
                vciFacadeImpl, "saveToTxnTable",
                req, /*isInternal*/ false, /*status*/ true, "partner-X",
                /*token*/ null, resp, new TestObjectWithMetadata());

        verifyNoInteractions(tokenIdManager, partnerService, idService, fraudEventManager);
    }

}
