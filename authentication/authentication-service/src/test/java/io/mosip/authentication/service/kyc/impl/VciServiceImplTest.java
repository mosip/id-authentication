package io.mosip.authentication.service.kyc.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import foundation.identity.jsonld.ConfigurableDocumentLoader;
import foundation.identity.jsonld.JsonLDException;
import info.weboftrust.ldsignatures.LdProof;
import info.weboftrust.ldsignatures.canonicalizer.URDNA2015Canonicalizer;
import io.mosip.authentication.common.service.entity.CredSubjectIdStore;
import io.mosip.authentication.common.service.util.EntityInfoUtil;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.repository.CredSubjectIdStoreRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.VciCredentialsDefinitionRequestDTO;
import io.mosip.authentication.core.indauth.dto.VciExchangeRequestDTO;
import io.mosip.authentication.service.kyc.util.VCSchemaProviderUtil;
import io.mosip.kernel.biometrics.entities.BIR;
import io.mosip.kernel.biometrics.spi.CbeffUtil;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@Import(EnvUtil.class)
public class VciServiceImplTest {

    @Mock
    JSONObject vcContextJsonld;

    @Autowired
    EnvUtil envUtil;

    @Mock
    IdAuthSecurityManager securityManager;

    @Mock
    CredSubjectIdStoreRepository csidStoreRepo;

    @Mock
    LdProof.Builder builder;

    @Mock
    URDNA2015Canonicalizer urdna2015Canonicalizer;

    @Mock
    VCSchemaProviderUtil vcSchemaProviderUtil;

    /** The demo helper. */
    @Mock
    IdInfoHelper idInfoHelper;

    @Mock
    CbeffUtil cbeffUtil;

    @InjectMocks
    VciServiceImpl vciServiceImpl;

    VciCredentialsDefinitionRequestDTO vciCredentialsDefinitionRequestDTO;

    VciExchangeRequestDTO vciExchangeRequestDTO;

    IdentityInfoDTO identityInfoDTO;

    List<String> locale;

    Map<String, List<IdentityInfoDTO>> idInfo;

    String credSubjectId;

    @Mock
    private EntityInfoUtil entityInfoUtil;


    @Before
    public void beforeTest(){

        identityInfoDTO = new IdentityInfoDTO();
        identityInfoDTO.setLanguage("eng");
        identityInfoDTO.setValue("value");
        List<IdentityInfoDTO> list = new ArrayList<>();
        list.add(identityInfoDTO);
        idInfo = new HashMap<>();
        idInfo.put("name", list);

        locale = new ArrayList<>();
        locale.add("eng");

        vciExchangeRequestDTO = new VciExchangeRequestDTO();
        vciExchangeRequestDTO.setId("12345678901234567890123456789012");
        vciExchangeRequestDTO.setMetadata(new HashMap<>());
        vciExchangeRequestDTO.setRequestTime("2019-07-15T12:00:00.000Z");
        vciExchangeRequestDTO.setVcAuthToken("12345678901234567890123456789012");
        vciExchangeRequestDTO.setCredSubjectId("12345678901234567890123456789012");
        vciExchangeRequestDTO.setVcFormat("WLA");
        vciExchangeRequestDTO.setIndividualId("1234567890");
        vciExchangeRequestDTO.setIndividualIdType("UIN");

        vciCredentialsDefinitionRequestDTO = new VciCredentialsDefinitionRequestDTO();
        vciCredentialsDefinitionRequestDTO.setContext(List.of("https://www.w3.org/2018/credentials/v1"));
        vciCredentialsDefinitionRequestDTO.setType(List.of("VerifiableCredential"));
        vciCredentialsDefinitionRequestDTO.setCredentialSubject(new HashMap<>());
        credSubjectId = "12345:54321:ewogICAgImt0eSI6ICJSU0EiLAogICAgImUiOiAiQVFBQiIsCiAgICAidXNlIjogInNpZyIsCiAgICAia2lkIjogInBicy1GY1B6N1Jwbm42UUI1WG8xLWxrdVBEWHlxRlBZUzB5V296S0VpUjgiLAogICAgImFsZyI6ICJSUzI1NiIsCiAgICAibiI6ICJzSzhLTE55d0JoVVloYWhIREpTN0lPNkN2SkYxeTNmX0xsTEJvTV81eGFvcXVPckxDb084R0llaWJ1ai1YOWV0S3d1SkoycTdjdzRnTEJocXFOd2x3T2ZTOXZ2X1BnRTZkTTYtSDkxVVgtbGljQzh6YUFDSkdCV1N2TlVjSmtSNFJpOW5laGQ3NmRMSTJ5SDdlYVh3N0lRVERyMDVtSFFyR1ZaNVBVZTRMR3haZlVqcmxQUGttcTZfUTBIbk5RN1ZGTjVFLUxDejNvUWtKbHl4OTQyenhJdk5TV2V1enNMQU5xZWdSQzVWd3YtWlJtNmgxb1BNSWY1MThoZHdwaEhqeU5fRGs5djExYV8yT2VaNzd0T3Ria0RUaUxtamVLS1dTNXZldW1rOWMzTkw4OU00LS1yMFJMZ0Jrb3k1X0RMNHNmRXpSRnZYWVF0eHI4c3R1aURaRFEiCn0=";

    }

    @Test
    public void addCredSubjectIdTestWithInvalidCredSubjectId_thenFail() throws IdAuthenticationBusinessException {

        try{
            vciServiceImpl.addCredSubjectId("12345:54321:MTIzNDU2Nzg5MA==","hash","123456789","12");
        }catch (IdAuthenticationBusinessException e){
            Assert.assertEquals("IDA-MLC-007",e.getErrorCode());
        }
    }

    @Test
    public void addCredSubjectIdTestWithValidDetailsAndWithoutSubIdList_thenPass() throws IdAuthenticationBusinessException {
          vciServiceImpl.addCredSubjectId(credSubjectId,"hash","123456789","12");

    }

    @Test
    public void addCredSubjectIdTestWithValidDetailsWithSameVid_thenPass() throws IdAuthenticationBusinessException {

        List<CredSubjectIdStore> credSubjectIdList = new ArrayList<>();
        CredSubjectIdStore credSubjectIdStore=new CredSubjectIdStore();
        credSubjectIdStore.setCredSubjectId("12345");
        credSubjectIdStore.setId("12345");
        credSubjectIdStore.setIdVidHash("hash");
        credSubjectIdList.add(credSubjectIdStore);
        credSubjectIdStore.setTokenId("token");
        Mockito.when(csidStoreRepo.findAllByCsidKeyHash(Mockito.anyString())).thenReturn(credSubjectIdList);
        vciServiceImpl.addCredSubjectId(credSubjectId,"hash","token","12");

    }

    @Test
    public void addCredSubjectIdTestWithValidDetailsWithDiffSameVid_thenPass() throws IdAuthenticationBusinessException {

        List<CredSubjectIdStore> credSubjectIdList = new ArrayList<>();
        CredSubjectIdStore credSubjectIdStore=new CredSubjectIdStore();
        credSubjectIdStore.setCredSubjectId("12345");
        credSubjectIdStore.setId("12345");
        credSubjectIdStore.setIdVidHash("hash");
        credSubjectIdList.add(credSubjectIdStore);
        credSubjectIdStore.setTokenId("token");
        Mockito.when(csidStoreRepo.findAllByCsidKeyHash(Mockito.anyString())).thenReturn(credSubjectIdList);
        vciServiceImpl.addCredSubjectId(credSubjectId,"hashe","token","12");

    }

    //TODO builder need to be fixed
    @Test
    public void buildVerifiableCredentialsTest() throws IdAuthenticationBusinessException, JsonLDException, GeneralSecurityException, IOException {
        ReflectionTestUtils.setField(vciServiceImpl, "consentedIndividualAttributeName", "name");
        ReflectionTestUtils.setField(vciServiceImpl, "proofPurpose", "purpose");
        ReflectionTestUtils.setField(vciServiceImpl, "proofType", "proofType");
        ReflectionTestUtils.setField(vciServiceImpl,"verificationMethod","verificationMethod");
        ReflectionTestUtils.setField(vciServiceImpl,"confDocumentLoader",new ConfigurableDocumentLoader());
        Set<String> allowedAttribute =new HashSet<>();
        allowedAttribute.add("name");

        VciCredentialsDefinitionRequestDTO vciCredentialsDefinitionRequestDTO = new VciCredentialsDefinitionRequestDTO();
        vciCredentialsDefinitionRequestDTO.setContext(List.of("https://www.w3.org/2018/credentials/v1"));
        vciCredentialsDefinitionRequestDTO.setType(List.of("VerifiableCredential"));
        vciCredentialsDefinitionRequestDTO.setCredentialSubject(new HashMap<>());


        EnvUtil.setDateTimePattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        // Create a mock of the LdProof.Builder class
        LdProof.Builder builderMock = Mockito.mock(LdProof.Builder.class,"RETURNS_SELF");

        Mockito.when(builderMock.defaultContexts(Mockito.anyBoolean())).thenReturn(builderMock);
        Mockito.when(builderMock.defaultTypes(Mockito.anyBoolean())).thenReturn(builderMock);
        Mockito.when(builderMock.type(Mockito.anyString())).thenReturn(builderMock);
        Mockito.when(builderMock.created(Mockito.any())).thenReturn(builderMock);
        Mockito.when(builderMock.proofPurpose("purpose")).thenReturn(builderMock);
        Mockito.when(builderMock.verificationMethod(Mockito.any())).thenReturn(builderMock);


        LdProof ldProofMock = Mockito.mock(LdProof.class);
        Mockito.when(builderMock.build()).thenReturn(ldProofMock);

        vciExchangeRequestDTO.setCredentialsDefinition(vciCredentialsDefinitionRequestDTO);
        Mockito.when( vcContextJsonld.get("context")).thenReturn(new Object());
        Mockito.when(urdna2015Canonicalizer.canonicalize(Mockito.any(),Mockito.any())).thenReturn(new byte[4]);

        try{
            vciServiceImpl.buildVerifiableCredentials(credSubjectId,"ldp_vc" ,idInfo, locale, allowedAttribute, vciExchangeRequestDTO,"pusutokdn");
        }catch (Exception e){}

    }

    @Test
    public void buildVerifiableCredentialswithjwt_vc_jsonTest() throws IdAuthenticationBusinessException {

        Set<String> allowedAttribute =new HashSet<>();
        allowedAttribute.add("name");

        vciExchangeRequestDTO.setCredentialsDefinition(vciCredentialsDefinitionRequestDTO);
        Mockito.when( vcContextJsonld.get("context")).thenReturn(new Object());

        try{
            vciServiceImpl.buildVerifiableCredentials(credSubjectId,"jwt_vc_json" ,idInfo, locale, allowedAttribute, vciExchangeRequestDTO,"pusutokdn");
        }catch (Exception e){}

    }

    @Test
    public void buildVerifiableCredentialswithjwt_vc_jsonldTest() throws IdAuthenticationBusinessException {

        Set<String> allowedAttribute =new HashSet<>();
        allowedAttribute.add("name");
        vciExchangeRequestDTO.setCredentialsDefinition(vciCredentialsDefinitionRequestDTO);
        Mockito.when( vcContextJsonld.get("context")).thenReturn(new Object());

        try{
            vciServiceImpl.buildVerifiableCredentials(credSubjectId,"jwt_vc_json-ld" ,idInfo, locale, allowedAttribute, vciExchangeRequestDTO,"pusutokdn");
        }catch (Exception e){}

    }

    @Test
    public void buildVerifiableCredentialsWithFaceTest() throws Exception {
        ReflectionTestUtils.setField(vciServiceImpl, "consentedIndividualAttributeName", "name");
        ReflectionTestUtils.setField(vciServiceImpl, "proofPurpose", "purpose");
        ReflectionTestUtils.setField(vciServiceImpl, "proofType", "proofType");
        ReflectionTestUtils.setField(vciServiceImpl,"verificationMethod","verificationMethod");
        ReflectionTestUtils.setField(vciServiceImpl,"confDocumentLoader",new ConfigurableDocumentLoader());

        Set<String> allowedAttribute =new HashSet<>();
        allowedAttribute.add("face");

        EnvUtil.setDateTimePattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        vciExchangeRequestDTO.setCredentialsDefinition(vciCredentialsDefinitionRequestDTO);
        Mockito.when( vcContextJsonld.get("context")).thenReturn(new Object());
        Mockito.when(urdna2015Canonicalizer.canonicalize(Mockito.any(),Mockito.any())).thenReturn(new byte[4]);

        List<BIR> birDataFromXMLType =new ArrayList<>();
        BIR bir=new BIR();
        bir.setBdb(new byte[4]);
        birDataFromXMLType.add(bir);
        Mockito.when(cbeffUtil.getBIRDataFromXMLType(Mockito.any(),Mockito.anyString())).thenReturn(birDataFromXMLType);
        Map<String,String> faceEntityInfoMap = new HashMap<>();
        faceEntityInfoMap.put("Face","face");
        Mockito.when(entityInfoUtil.getIdEntityInfoMap(Mockito.any(),Mockito.anyMap(),Mockito.any())).thenReturn(faceEntityInfoMap);
        try{
            vciServiceImpl.buildVerifiableCredentials(credSubjectId,"ldp_vc" ,idInfo, locale, allowedAttribute, vciExchangeRequestDTO,"pusutokdn");
        }catch (Exception e){}

    }

    @Test
    public void buildVerifiableCredentialsWithIdScemaTest() throws Exception {
        ReflectionTestUtils.setField(vciServiceImpl, "consentedIndividualAttributeName", "name");
        ReflectionTestUtils.setField(vciServiceImpl, "proofPurpose", "purpose");
        ReflectionTestUtils.setField(vciServiceImpl, "proofType", "proofType");
        ReflectionTestUtils.setField(vciServiceImpl,"verificationMethod","verificationMethod");
        ReflectionTestUtils.setField(vciServiceImpl,"confDocumentLoader",new ConfigurableDocumentLoader());
        IdentityInfoDTO identityInfoDTO=new IdentityInfoDTO();
        identityInfoDTO.setLanguage("eng");
        identityInfoDTO.setValue("value");
        List<IdentityInfoDTO> list=new ArrayList<>();
        list.add(identityInfoDTO);
        Map<String, List<IdentityInfoDTO>> idInfo =new HashMap<>();
        idInfo.put("info",list);

        List<String> locale=new ArrayList<>();
        locale.add("eng");

        Set<String> allowedAttribute =new HashSet<>();
        allowedAttribute.add("id");

        EnvUtil.setDateTimePattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        vciExchangeRequestDTO.setCredentialsDefinition(vciCredentialsDefinitionRequestDTO);
        Mockito.when( vcContextJsonld.get("context")).thenReturn(new Object());
        Mockito.when(urdna2015Canonicalizer.canonicalize(Mockito.any(),Mockito.any())).thenReturn(new byte[4]);


        List<String> idInfoHelperList = new ArrayList<>();
        idInfoHelperList.add("info");
        Mockito.when(idInfoHelper.getIdentityAttributesForIdName(Mockito.anyString())).thenReturn(idInfoHelperList);
        try{
            vciServiceImpl.buildVerifiableCredentials(credSubjectId,"ldp_vc" ,idInfo, locale, allowedAttribute, vciExchangeRequestDTO,"pusutokdn");
        }catch (Exception e){}

    }


    @Test
    public void buildVerifiableCredentialsWithInfoListTest() throws Exception {
        ReflectionTestUtils.setField(vciServiceImpl, "consentedIndividualAttributeName", "name");
        ReflectionTestUtils.setField(vciServiceImpl, "proofPurpose", "purpose");
        ReflectionTestUtils.setField(vciServiceImpl, "proofType", "proofType");
        ReflectionTestUtils.setField(vciServiceImpl,"verificationMethod","verificationMethod");
        ReflectionTestUtils.setField(vciServiceImpl,"confDocumentLoader",new ConfigurableDocumentLoader());
        IdentityInfoDTO identityInfoDTO=new IdentityInfoDTO();
        identityInfoDTO.setLanguage("eng");
        identityInfoDTO.setValue("value");
        List<IdentityInfoDTO> list=new ArrayList<>();
        list.add(identityInfoDTO);
        identityInfoDTO=new IdentityInfoDTO();
        identityInfoDTO.setLanguage("hin");
        identityInfoDTO.setValue("value");
        list.add(identityInfoDTO);
        Map<String, List<IdentityInfoDTO>> idInfo =new HashMap<>();
        idInfo.put("info",list);

        List<String> locale=new ArrayList<>();
        locale.add("eng");

        Set<String> allowedAttribute =new HashSet<>();
        allowedAttribute.add("id");

        EnvUtil.setDateTimePattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        vciExchangeRequestDTO.setCredentialsDefinition(vciCredentialsDefinitionRequestDTO);
        Mockito.when( vcContextJsonld.get("context")).thenReturn(new Object());
        Mockito.when(urdna2015Canonicalizer.canonicalize(Mockito.any(),Mockito.any())).thenReturn(new byte[4]);


        List<String> idInfoHelperList = new ArrayList<>();
        idInfoHelperList.add("info");
        Mockito.when(idInfoHelper.getIdentityAttributesForIdName(anyString())).thenReturn(idInfoHelperList);
        try{
        vciServiceImpl.buildVerifiableCredentials(credSubjectId, "ldp_vc", idInfo, locale, allowedAttribute, vciExchangeRequestDTO, "pusutokdn");
        }catch(Exception ignored){
        }
    }

    }

    @Test
    public void addCredSubjectIdTestKeyAlreadyMappedThenFail() throws Exception {

        // Prepare existing entry mapped to different id/vid and different token
        List<CredSubjectIdStore> credSubjectIdList = new ArrayList<>();
        CredSubjectIdStore store = new CredSubjectIdStore();
        store.setCredSubjectId("abc");
        store.setIdVidHash("hash1");
        store.setTokenId("token1");
        credSubjectIdList.add(store);

        Mockito.when(csidStoreRepo.findAllByCsidKeyHash(anyString()))
                .thenReturn(credSubjectIdList);

        try {
            vciServiceImpl.addCredSubjectId(credSubjectId, "hash2", "token2", "12");
            Assert.fail("Exception expected!");
        } catch (IdAuthenticationBusinessException e) {
            assertEquals(
                    IdAuthenticationErrorConstants.KEY_ALREADY_MAPPED_ERROR.getErrorCode(),
                    e.getErrorCode()
            );
        }
    }

    @Test
    public void addCredSubjectIdNoExistingRecordShouldAddNew() throws Exception {
        Mockito.when(csidStoreRepo.findAllByCsidKeyHash(anyString()))
                .thenReturn(Collections.emptyList());

        vciServiceImpl.addCredSubjectId(credSubjectId, "hash", "token", "client");
    }

    @Test
    public void addCredSubjectIdSameIdVidSameTokenShouldReturn() throws Exception {
        CredSubjectIdStore store = new CredSubjectIdStore();
        store.setIdVidHash("hash");
        store.setTokenId("token");

        Mockito.when(csidStoreRepo.findAllByCsidKeyHash(anyString()))
                .thenReturn(List.of(store));

        vciServiceImpl.addCredSubjectId(credSubjectId, "hash", "token", "client");
    }

    @Test
    public void addCredSubjectIdDiffIdVidSameTokenShouldAddNew() throws Exception {
        CredSubjectIdStore store = new CredSubjectIdStore();
        store.setIdVidHash("oldHash");
        store.setTokenId("token");  // same token

        Mockito.when(csidStoreRepo.findAllByCsidKeyHash(anyString()))
                .thenReturn(List.of(store));

        vciServiceImpl.addCredSubjectId(credSubjectId, "newHash", "token", "client");
    }

    @Test
    public void addCredSubjectIdDiffIdVidDiffTokenShouldThrow() throws Exception {
        CredSubjectIdStore store = new CredSubjectIdStore();
        store.setIdVidHash("oldHash");
        store.setTokenId("oldToken");

        Mockito.when(csidStoreRepo.findAllByCsidKeyHash(anyString()))
                .thenReturn(List.of(store));

        try {
            vciServiceImpl.addCredSubjectId(credSubjectId, "newHash", "newToken", "client");
            Assert.fail("Exception expected");
        } catch (IdAuthenticationBusinessException ex) {
            assertEquals(
                    IdAuthenticationErrorConstants.KEY_ALREADY_MAPPED_ERROR.getErrorCode(),
                    ex.getErrorCode()
            );
        }
    }

    @Test
    public void testInitWhenVcContextUrlMapIsNullShouldInitializeDocumentLoader() throws Exception {

        ReflectionTestUtils.setField(vciServiceImpl, "vcContextUrlMap", null);

        // invoke private init() using reflection
        Method initMethod = VciServiceImpl.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(vciServiceImpl);

        Object loader = ReflectionTestUtils.getField(vciServiceImpl, "confDocumentLoader");
        assertNotNull("confDocumentLoader must be initialized", loader);
        assertTrue("Loader should be ConfigurableDocumentLoader",
                loader instanceof ConfigurableDocumentLoader);

        ConfigurableDocumentLoader cd = (ConfigurableDocumentLoader) loader;
        assertTrue(cd.isEnableHttps());
        assertTrue(cd.isEnableHttp());
        assertFalse(cd.isEnableFile());
    }

    @Test
    public void testAddCredSubjectIdWhenUnsupportedKeyType() throws Exception {
        // Arrange: create a JSON with unsupported key type
        JSONObject unsupportedKeyJwk = new JSONObject();
        unsupportedKeyJwk.put("kty", "unsupported"); // unsupported key type
        unsupportedKeyJwk.put("n", "dummy");          // avoid NPE
        unsupportedKeyJwk.put("e", "dummy");          // avoid NPE

        // Encode JSON to Base64 and construct credSubjectId
        String base64Jwk = Base64.getEncoder()
                .encodeToString(unsupportedKeyJwk.toJSONString().getBytes(StandardCharsets.UTF_8));
        String credSubjectId = "did:example:" + base64Jwk;

        // Act & Assert
        IdAuthenticationBusinessException thrown = assertThrows(
                IdAuthenticationBusinessException.class,
                () -> vciServiceImpl.addCredSubjectId(credSubjectId, "idVidHash", "tokenId", "oidcClientId")
        );

        // Validate exception
        assertTrue(thrown.getMessage().contains(
                IdAuthenticationErrorConstants.KEY_TYPE_NOT_SUPPORT.getErrorMessage()
        ));

        verify(csidStoreRepo, never()).findAllByCsidKeyHash(anyString());
    }

    @Test
    public void testGetPublicKeyHashWhenInvalidKeySpecThenThrowBusinessException() {
        JSONObject rsaJwk = new JSONObject();
        rsaJwk.put("kty", "RSA");
        rsaJwk.put("n", "invalid-modulus"); // invalid
        rsaJwk.put("e", "AQAB");

        String base64Jwk = Base64.getEncoder()
                .encodeToString(rsaJwk.toJSONString().getBytes(StandardCharsets.UTF_8));
        String credSubjectId = "did:example:" + base64Jwk;

        IdAuthenticationBusinessException ex = assertThrows(
                IdAuthenticationBusinessException.class,
                () -> vciServiceImpl.addCredSubjectId(credSubjectId, "hash", "token", "client")
        );

        assertEquals(
                IdAuthenticationErrorConstants.CREATE_VCI_PUBLIC_KEY_OBJECT_ERROR.getErrorCode(),
                ex.getErrorCode()
        );
    }

    @Test
    public void testBuildVerifiableCredentialsJwtVcJsonShouldThrowException() {
        // Act & Assert
        IdAuthenticationBusinessException ex = assertThrows(
                IdAuthenticationBusinessException.class,
                () -> vciServiceImpl.buildVerifiableCredentials(
                        credSubjectId, "jwt_vc_json", idInfo, locale, Set.of("name"), vciExchangeRequestDTO, "psuToken"
                )
        );

        assertEquals(IdAuthenticationErrorConstants.VCI_NOT_SUPPORTED_ERROR.getErrorCode(), ex.getErrorCode());
    }

    @Test
    public void testBuildVerifiableCredentialsJwtVcJsonLdShouldThrowException() {
        // Act & Assert
        IdAuthenticationBusinessException ex = assertThrows(
                IdAuthenticationBusinessException.class,
                () -> vciServiceImpl.buildVerifiableCredentials(
                        credSubjectId, "jwt_vc_json_ld", idInfo, locale, Set.of("name"), vciExchangeRequestDTO, "psuToken"
                )
        );

        assertEquals(IdAuthenticationErrorConstants.VCI_NOT_SUPPORTED_ERROR.getErrorCode(), ex.getErrorCode());
    }

}
