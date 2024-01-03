package io.mosip.authentication.esignet.integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import foundation.identity.jsonld.JsonLDObject;
import io.mosip.authentication.esignet.integration.dto.IdaResponseWrapper;
import io.mosip.authentication.esignet.integration.dto.IdaVcExchangeRequest;
import io.mosip.authentication.esignet.integration.dto.IdaVcExchangeResponse;
import io.mosip.authentication.esignet.integration.helper.VCITransactionHelper;
import io.mosip.esignet.api.dto.VCRequestDto;
import io.mosip.esignet.api.dto.VCResult;
import io.mosip.esignet.core.constants.ErrorConstants;
import io.mosip.esignet.core.dto.OIDCTransaction;
import io.mosip.esignet.core.exception.EsignetException;
import io.mosip.esignet.core.util.IdentityProviderUtil;
import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.keymanagerservice.entity.KeyAlias;
import io.mosip.kernel.keymanagerservice.helper.KeymanagerDBHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.mosip.kernel.keymanagerservice.constant.KeymanagerConstant.CURRENTKEYALIAS;

@RunWith(MockitoJUnitRunner.class)
public class IdaVCIssuancePluginImplTest {

    @Mock
    VCITransactionHelper vciTransactionHelper;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    RestTemplate restTemplate;

    @Mock
    HelperService helperService;

    @Mock
    KeymanagerDBHelper keymanagerDBHelper;

    @Mock
    KeyStore keyStore;

    @InjectMocks
    IdaVCIssuancePluginImpl idaVCIssuancePlugin=new IdaVCIssuancePluginImpl();

    @Test
    public void getVerifiableCredentialWithLinkedDataProof_withValidDetails_thenPass() throws Exception {

        ReflectionTestUtils.setField(idaVCIssuancePlugin,"vciExchangeUrl","http://example.com");

        VCRequestDto vcRequestDto = new VCRequestDto();
        vcRequestDto.setFormat("ldp_vc");
        vcRequestDto.setContext(Arrays.asList("context1","context2"));
        vcRequestDto.setType(Arrays.asList("VerifiableCredential"));
        vcRequestDto.setCredentialSubject(Map.of("subject1","subject1","subject2","subject2"));

        OIDCTransaction oidcTransaction = new OIDCTransaction();
        oidcTransaction.setIndividualId("individualId");
        oidcTransaction.setKycToken("kycToken");
        oidcTransaction.setAuthTransactionId("authTransactionId");
        oidcTransaction.setRelyingPartyId("relyingPartyId");
        oidcTransaction.setClaimsLocales(new String[]{"en-US", "en", "en-CA", "fr-FR", "fr-CA"});

        IdaResponseWrapper<IdaVcExchangeResponse<JsonLDObject>> mockResponseWrapper = new IdaResponseWrapper<>();
        IdaVcExchangeResponse<JsonLDObject> mockResponse = new IdaVcExchangeResponse<>();
        JsonLDObject jsonLDObject = new JsonLDObject();
        jsonLDObject.setJsonObjectKeyValue("key", "value");
        mockResponse.setVerifiableCredentials(jsonLDObject);
        mockResponseWrapper.setResponse(mockResponse);
        mockResponseWrapper.setId("id");
        mockResponseWrapper.setVersion("version");
        mockResponseWrapper.setTransactionID("transactionID");

        ResponseEntity<IdaResponseWrapper<IdaVcExchangeResponse<JsonLDObject>>> mockResponseEntity = ResponseEntity.ok(mockResponseWrapper);
        ParameterizedTypeReference<IdaResponseWrapper<IdaVcExchangeResponse<JsonLDObject>>> responseType =
                new ParameterizedTypeReference<IdaResponseWrapper<IdaVcExchangeResponse<JsonLDObject>>>() {
                };

        Mockito.when(vciTransactionHelper.getOAuthTransaction(Mockito.any())).thenReturn(oidcTransaction);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any(IdaVcExchangeRequest.class))).thenReturn("jsonString");
        Mockito.when(restTemplate.exchange(
                Mockito.any(RequestEntity.class),
                Mockito.eq(responseType)
        )).thenReturn(mockResponseEntity);

        VCResult result=idaVCIssuancePlugin.getVerifiableCredentialWithLinkedDataProof(vcRequestDto,"holderId",Map.of("accessTokenHash","ACCESS_TOKEN_HASH","client_id","CLIENT_ID"));
        Assert.assertNotNull(result.getCredential());
        Assert.assertEquals(jsonLDObject,result.getCredential());
        Assert.assertEquals(result.getFormat(),"ldp_vc");
    }

    @Test
    public void getVerifiableCredentialWithLinkedDataProof_withValidDetailsAndStoreIndividualId_thenPass() throws Exception {

        ReflectionTestUtils.setField(idaVCIssuancePlugin,"vciExchangeUrl","http://example.com");
        ReflectionTestUtils.setField(idaVCIssuancePlugin,"storeIndividualId",true);
        ReflectionTestUtils.setField(idaVCIssuancePlugin,"secureIndividualId",true);
        ReflectionTestUtils.setField(idaVCIssuancePlugin,"aesECBTransformation","AES/ECB/PKCS5Padding");
        ReflectionTestUtils.setField(idaVCIssuancePlugin,"cacheSecretKeyRefId","cacheSecretKeyRefId");

        VCRequestDto vcRequestDto = new VCRequestDto();
        vcRequestDto.setFormat("ldp_vc");
        vcRequestDto.setContext(Arrays.asList("context1","context2"));
        vcRequestDto.setType(Arrays.asList("VerifiableCredential"));
        vcRequestDto.setCredentialSubject(Map.of("subject1","subject1","subject2","subject2"));

        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256);
        SecretKey key = generator.generateKey();
        String individualId = encryptIndividualId("individual-id",key);

        OIDCTransaction oidcTransaction = new OIDCTransaction();
        oidcTransaction.setIndividualId(individualId);
        oidcTransaction.setKycToken("kycToken");
        oidcTransaction.setAuthTransactionId("authTransactionId");
        oidcTransaction.setRelyingPartyId("relyingPartyId");

        Map<String, List<KeyAlias>> keyaliasesMap = new HashMap<>();
        KeyAlias keyAlias = new KeyAlias();
        keyAlias.setAlias("test");
        keyaliasesMap.put(CURRENTKEYALIAS, Arrays.asList(keyAlias));
        Mockito.when(keymanagerDBHelper.getKeyAliases(Mockito.anyString(), Mockito.anyString(), Mockito.any(LocalDateTime.class))).thenReturn(keyaliasesMap);
        Mockito.when(keyStore.getSymmetricKey(Mockito.anyString())).thenReturn(key, key);

        IdaResponseWrapper<IdaVcExchangeResponse<JsonLDObject>> mockResponseWrapper = new IdaResponseWrapper<>();
        IdaVcExchangeResponse<JsonLDObject> mockResponse = new IdaVcExchangeResponse<>();
        JsonLDObject jsonLDObject = new JsonLDObject();
        jsonLDObject.setJsonObjectKeyValue("key", "value");
        mockResponse.setVerifiableCredentials(jsonLDObject);
        mockResponseWrapper.setResponse(mockResponse);
        mockResponseWrapper.setId("id");
        mockResponseWrapper.setVersion("version");
        mockResponseWrapper.setTransactionID("transactionID");

        ResponseEntity<IdaResponseWrapper<IdaVcExchangeResponse<JsonLDObject>>> mockResponseEntity = ResponseEntity.ok(mockResponseWrapper);
        ParameterizedTypeReference<IdaResponseWrapper<IdaVcExchangeResponse<JsonLDObject>>> responseType =
                new ParameterizedTypeReference<IdaResponseWrapper<IdaVcExchangeResponse<JsonLDObject>>>() {
                };

        Mockito.when(vciTransactionHelper.getOAuthTransaction(Mockito.any())).thenReturn(oidcTransaction);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("jsonString");
        Mockito.when(restTemplate.exchange(
                Mockito.any(RequestEntity.class),
                Mockito.eq(responseType)
        )).thenReturn(mockResponseEntity);

        VCResult result=idaVCIssuancePlugin.getVerifiableCredentialWithLinkedDataProof(vcRequestDto,"holderId",Map.of("accessTokenHash","ACCESS_TOKEN_HASH","client_id","CLIENT_ID"));
        Assert.assertNotNull(result.getCredential());
        Assert.assertEquals(jsonLDObject,result.getCredential());
        Assert.assertEquals(result.getFormat(),"ldp_vc");
        Mockito.verify(keymanagerDBHelper).getKeyAliases(Mockito.anyString(), Mockito.anyString(), Mockito.any(LocalDateTime.class));
    }

    @Test
    public void getVerifiableCredentialWithLinkedDataProof_withInValidIndividualId_thenFail() throws Exception {

        ReflectionTestUtils.setField(idaVCIssuancePlugin,"vciExchangeUrl","http://example.com");
        ReflectionTestUtils.setField(idaVCIssuancePlugin,"storeIndividualId",true);
        ReflectionTestUtils.setField(idaVCIssuancePlugin,"secureIndividualId",true);
        ReflectionTestUtils.setField(idaVCIssuancePlugin,"aesECBTransformation","AES/ECB/PKCS5Padding");
        ReflectionTestUtils.setField(idaVCIssuancePlugin,"cacheSecretKeyRefId","cacheSecretKeyRefId");

        VCRequestDto vcRequestDto = new VCRequestDto();
        vcRequestDto.setFormat("ld_vc");
        vcRequestDto.setContext(Arrays.asList("context1","context2"));
        vcRequestDto.setType(Arrays.asList("VerifiableCredential"));
        vcRequestDto.setCredentialSubject(Map.of("subject1","subject1","subject2","subject2"));

        OIDCTransaction oidcTransaction = new OIDCTransaction();
        oidcTransaction.setIndividualId("individualId");
        oidcTransaction.setKycToken("kycToken");
        oidcTransaction.setAuthTransactionId("authTransactionId");
        oidcTransaction.setRelyingPartyId("relyingPartyId");

        Mockito.when(vciTransactionHelper.getOAuthTransaction(Mockito.any())).thenReturn(oidcTransaction);
        try{
            VCResult result=  idaVCIssuancePlugin.getVerifiableCredentialWithLinkedDataProof(vcRequestDto,"holderId",Map.of("accessTokenHash","ACCESS_TOKEN_HASH","client_id","CLIENT_ID"));
            Assert.fail();
        }catch (Exception e)
        {
            Assert.assertEquals("vci_exchange_failed",e.getMessage());
        }
    }

    @Test
    public void getVerifiableCredentialWithLinkedDataProof_withInVlidResponse_thenFail() throws Exception {

        ReflectionTestUtils.setField(idaVCIssuancePlugin,"vciExchangeUrl","http://example.com");
        ReflectionTestUtils.setField(idaVCIssuancePlugin,"storeIndividualId",true);
        ReflectionTestUtils.setField(idaVCIssuancePlugin,"secureIndividualId",true);
        ReflectionTestUtils.setField(idaVCIssuancePlugin,"aesECBTransformation","AES/ECB/PKCS5Padding");
        ReflectionTestUtils.setField(idaVCIssuancePlugin,"cacheSecretKeyRefId","cacheSecretKeyRefId");

        VCRequestDto vcRequestDto = new VCRequestDto();
        vcRequestDto.setFormat("ldp_vc");
        vcRequestDto.setContext(Arrays.asList("context1","context2"));
        vcRequestDto.setType(Arrays.asList("VerifiableCredential"));
        vcRequestDto.setCredentialSubject(Map.of("subject1","subject1","subject2","subject2"));

        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256);
        SecretKey key = generator.generateKey();
        String individualId = encryptIndividualId("individual-id",key);

        OIDCTransaction oidcTransaction = new OIDCTransaction();
        oidcTransaction.setIndividualId(individualId);
        oidcTransaction.setKycToken("kycToken");
        oidcTransaction.setAuthTransactionId("authTransactionId");
        oidcTransaction.setRelyingPartyId("relyingPartyId");

        Map<String, List<KeyAlias>> keyaliasesMap = new HashMap<>();
        KeyAlias keyAlias = new KeyAlias();
        keyAlias.setAlias("test");
        keyaliasesMap.put(CURRENTKEYALIAS, Arrays.asList(keyAlias));
        Mockito.when(vciTransactionHelper.getOAuthTransaction(Mockito.any())).thenReturn(oidcTransaction);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("jsonString");
        Mockito.when(keymanagerDBHelper.getKeyAliases(Mockito.anyString(), Mockito.anyString(), Mockito.any(LocalDateTime.class))).thenReturn(keyaliasesMap);
        Mockito.when(keyStore.getSymmetricKey(Mockito.anyString())).thenReturn(key, key);

        IdaResponseWrapper<IdaVcExchangeResponse<JsonLDObject>> mockResponseWrapper = new IdaResponseWrapper<>();
        IdaVcExchangeResponse<JsonLDObject> mockResponse = new IdaVcExchangeResponse<>();
        JsonLDObject jsonLDObject = new JsonLDObject();
        jsonLDObject.setJsonObjectKeyValue("key", "value");
        mockResponse.setVerifiableCredentials(jsonLDObject);
        mockResponseWrapper.setResponse(null);
        mockResponseWrapper.setId("id");
        mockResponseWrapper.setVersion("version");
        mockResponseWrapper.setTransactionID("transactionID");

        ResponseEntity<IdaResponseWrapper<IdaVcExchangeResponse<JsonLDObject>>> mockResponseEntity = ResponseEntity.ok(mockResponseWrapper);
        ParameterizedTypeReference<IdaResponseWrapper<IdaVcExchangeResponse<JsonLDObject>>> responseType =
                new ParameterizedTypeReference<IdaResponseWrapper<IdaVcExchangeResponse<JsonLDObject>>>() {
                };
        Mockito.when(restTemplate.exchange(
                Mockito.any(RequestEntity.class),
                Mockito.eq(responseType)
        )).thenReturn(mockResponseEntity);

        try{
            VCResult result=  idaVCIssuancePlugin.getVerifiableCredentialWithLinkedDataProof(vcRequestDto,"holderId",Map.of("accessTokenHash","ACCESS_TOKEN_HASH","client_id","CLIENT_ID"));
            Assert.fail();
        }catch (Exception e)
        {
            Assert.assertEquals("vci_exchange_failed",e.getMessage());
        }
    }

    private String encryptIndividualId(String individualId, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            byte[] secretDataBytes = individualId.getBytes(StandardCharsets.UTF_8);
            cipher.init(Cipher.ENCRYPT_MODE,key);
            return IdentityProviderUtil.b64Encode(cipher.doFinal(secretDataBytes, 0, secretDataBytes.length));
        } catch(Exception e) {
            throw new EsignetException(ErrorConstants.AES_CIPHER_FAILED);
        }
    }

}
