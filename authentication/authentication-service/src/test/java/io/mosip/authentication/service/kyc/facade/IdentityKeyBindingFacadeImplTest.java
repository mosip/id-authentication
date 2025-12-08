package io.mosip.authentication.service.kyc.facade;

import io.mosip.authentication.common.manager.IdAuthFraudAnalysisEventManager;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.*;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.*;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.service.IdentityKeyBindingService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.idrepository.core.repository.UinHashSaltRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class IdentityKeyBindingFacadeImplTest {

    @InjectMocks
    private IdentityKeyBindingFacadeImpl facade;
    @Mock
    private IdService<AutnTxn> idService;
    @Mock
    private IdentityKeyBindingService keyBindingService;
    @Mock
    private AuthFacade authFacade;
    @Mock
    private UinHashSaltRepo uinHashSaltRepo;

    @Mock
    private TokenIdManager tokenIdManager;

    @Mock
    private EnvUtil env;

    @Mock
    private IdAuthSecurityManager securityManager;

    @Mock
    private PartnerService partnerService;

    @Mock
    private IdAuthFraudAnalysisEventManager fraudEventManager;

    @Mock
    private AuditHelper auditHelper;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        // Save original EnvUtil values
        originalAuthTokenRequired = EnvUtil.getAuthTokenRequired();
        originalDateTimePattern = EnvUtil.getDateTimePattern();
    }

    @AfterEach
    void tearDown() {
        // Restore original EnvUtil values
        if (originalAuthTokenRequired != null) {
            EnvUtil.setAuthTokenRequired(originalAuthTokenRequired);
        }
        if (originalDateTimePattern != null) {
            EnvUtil.setDateTimePattern(originalDateTimePattern);
        }
    }

    // Add these fields at the top of the class
    private Boolean originalAuthTokenRequired;
    private String originalDateTimePattern;

    @Test
    void testAuthenticateIndividualPublicKeyAlreadyBinded() throws Exception {
        AuthRequestDTO authRequest = mock(IdentityKeyBindingRequestDTO.class);
        when(authRequest.getIndividualId()).thenReturn("1234");
        when(authRequest.getIndividualIdType()).thenReturn(String.valueOf(IdType.UIN));
        IdentityKeyBindingRequestDTO keyBindingRequest = (IdentityKeyBindingRequestDTO) authRequest;
        IdentityKeyBindingDTO keyBindingDTO = mock(IdentityKeyBindingDTO.class);
        when(keyBindingRequest.getIdentityKeyBinding()).thenReturn(keyBindingDTO);
        Map<String, Object> pubKeyMap = new HashMap<>();
        pubKeyMap.put("kty", "RSA");
        when(keyBindingRequest.getIdentityKeyBinding()).thenReturn(keyBindingDTO);
        when(keyBindingDTO.getPublicKeyJWK()).thenReturn(pubKeyMap);
        doNothing().when(idService).checkIdKeyBindingPermitted(anyString(), anyString());
        when(keyBindingService.isPublicKeyBinded(anyString(), any())).thenReturn(true);

        ObjectWithMetadata meta = mock(ObjectWithMetadata.class);

        IdAuthenticationBusinessException ex = assertThrows(IdAuthenticationBusinessException.class, () ->
                facade.authenticateIndividual(authRequest, "pid", "apiKey", meta));
        assertEquals(IdAuthenticationErrorConstants.PUBLIC_KEY_BINDING_NOT_ALLOWED.getErrorCode(), ex.getErrorCode());
    }

    @Test
    void testInvokeDoProcessIdKeyBinding() throws Exception {
        IdentityKeyBindingRequestDTO req = mock(IdentityKeyBindingRequestDTO.class);
        AuthResponseDTO authResp = mock(AuthResponseDTO.class);
        ResponseDTO respDTO = mock(ResponseDTO.class);
        when(authResp.getResponse()).thenReturn(respDTO);
        when(authResp.getResponseTime()).thenReturn("2024-01-01T00:00:00Z");
        when(respDTO.isAuthStatus()).thenReturn(true);
        when(respDTO.getAuthToken()).thenReturn("token");

        String partnerId = "pid";
        String oidcClientId = "oidc";
        String token = "token";
        String idHash = "idHash";
        Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();

        java.lang.reflect.Method method = IdentityKeyBindingFacadeImpl.class.getDeclaredMethod(
                "doProcessIdKeyBinding",
                IdentityKeyBindingRequestDTO.class,
                AuthResponseDTO.class,
                String.class,
                String.class,
                String.class,
                String.class,
                Map.class
        );
        method.setAccessible(true);

        Map.Entry<IdentityKeyBindingResponseDto, Boolean> result =
                (Map.Entry<IdentityKeyBindingResponseDto, Boolean>) method.invoke(
                        facade, req, authResp, partnerId, oidcClientId, token, idHash, idInfo
                );

        assertNotNull(result);
    }

    @Test
    void testDoProcessIdKeyBinding() throws Exception {

        IdentityKeyBindingRequestDTO req = mock(IdentityKeyBindingRequestDTO.class);
        AuthResponseDTO authResp = mock(AuthResponseDTO.class);
        ResponseDTO respDTO = mock(ResponseDTO.class);
        when(authResp.getResponse()).thenReturn(respDTO);
        when(authResp.getResponseTime()).thenReturn("2024-01-01T00:00:00Z");
        when(respDTO.isAuthStatus()).thenReturn(true);
        when(respDTO.getAuthToken()).thenReturn("token");

        String partnerId = "pid";
        String oidcClientId = "oidc";
        String token = "token";
        String idHash = "idHash";
        Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();

        java.lang.reflect.Method method = IdentityKeyBindingFacadeImpl.class.getDeclaredMethod(
                "doProcessIdKeyBinding",
                IdentityKeyBindingRequestDTO.class,
                AuthResponseDTO.class,
                String.class,
                String.class,
                String.class,
                String.class,
                Map.class
        );
        method.setAccessible(true);

        Map.Entry<IdentityKeyBindingResponseDto, Boolean> result =
                (Map.Entry<IdentityKeyBindingResponseDto, Boolean>) method.invoke(
                        facade, req, authResp, partnerId, oidcClientId, token, idHash, idInfo
                );

        assertNotNull(result);
        assertNotNull(result.getKey());
        assertTrue(result.getValue());
        IdentityKeyBindingResponseDto dto = result.getKey();
        assertNotNull(dto.getResponse());
        assertEquals("token", dto.getResponse().getAuthToken());
        assertTrue(dto.getResponse().isBindingAuthStatus());
    }

    @Test
    void testSaveToTxnTable_TokenNull() throws Exception {
        AuthRequestDTO authRequest = mock(IdentityKeyBindingRequestDTO.class);
        AuthResponseDTO authResp = mock(AuthResponseDTO.class);
        BaseAuthResponseDTO baseResp = mock(BaseAuthResponseDTO.class);
        Map<String, Object> metadata = new HashMap<>();

        java.lang.reflect.Method method = IdentityKeyBindingFacadeImpl.class.getDeclaredMethod(
                "saveToTxnTable",
                AuthRequestDTO.class, boolean.class, String.class, String.class, AuthResponseDTO.class,
                BaseAuthResponseDTO.class, Map.class
        );
        method.setAccessible(true);

        method.invoke(facade, authRequest, true, "partner1", null, authResp, baseResp, metadata);
    }

    @Test
    void testSaveToTxnTable_WithBaseAuthResponseAndAuthResponse_WithAutnTxn_NullAuthTypeCode() throws Exception {
        AuthRequestDTO authRequest = mock(IdentityKeyBindingRequestDTO.class);
        when(authRequest.getMetadata()).thenReturn(new HashMap<>());
        AuthResponseDTO authResp = mock(AuthResponseDTO.class);
        BaseAuthResponseDTO baseResp = mock(BaseAuthResponseDTO.class);
        Map<String, Object> metadata = new HashMap<>();
        
        AutnTxn autnTxn = new AutnTxn();
        autnTxn.setAuthTypeCode(null);
        autnTxn.setStatusComment("Status comment");
        metadata.put(AutnTxn.class.getSimpleName(), autnTxn);
        
        EnvUtil.setAuthTokenRequired(true);
        when(tokenIdManager.generateTokenId(anyString(), anyString())).thenReturn("authTokenId");
        when(partnerService.getPartner(anyString(), any())).thenReturn(Optional.empty());
        
        Method method = IdentityKeyBindingFacadeImpl.class.getDeclaredMethod(
                "saveToTxnTable",
                AuthRequestDTO.class, boolean.class, String.class, String.class, AuthResponseDTO.class,
                BaseAuthResponseDTO.class, Map.class
        );
        method.setAccessible(true);
        
        method.invoke(facade, authRequest, true, "partner1", "token123", authResp, baseResp, metadata);
        
        AutnTxn updatedAutnTxn = (AutnTxn) metadata.get(AutnTxn.class.getSimpleName());
        assertNotNull(updatedAutnTxn);
        assertEquals(RequestType.IDENTITY_KEY_BINDING.getRequestType(), updatedAutnTxn.getAuthTypeCode());
        assertTrue(updatedAutnTxn.getStatusComment().contains(RequestType.IDENTITY_KEY_BINDING.getMessage()));
    }

    @Test
    void testSaveToTxnTable_WithBaseAuthResponseAndAuthResponse_WithAutnTxn_NullStatusComment() throws Exception {
        AuthRequestDTO authRequest = mock(IdentityKeyBindingRequestDTO.class);
        when(authRequest.getMetadata()).thenReturn(new HashMap<>());
        AuthResponseDTO authResp = mock(AuthResponseDTO.class);
        BaseAuthResponseDTO baseResp = mock(BaseAuthResponseDTO.class);
        Map<String, Object> metadata = new HashMap<>();
        
        AutnTxn autnTxn = new AutnTxn();
        autnTxn.setAuthTypeCode("AUTH_TYPE");
        autnTxn.setStatusComment(null);
        metadata.put(AutnTxn.class.getSimpleName(), autnTxn);
        
        EnvUtil.setAuthTokenRequired(true);
        when(tokenIdManager.generateTokenId(anyString(), anyString())).thenReturn("authTokenId");
        when(partnerService.getPartner(anyString(), any())).thenReturn(Optional.empty());
        
        Method method = IdentityKeyBindingFacadeImpl.class.getDeclaredMethod(
                "saveToTxnTable",
                AuthRequestDTO.class, boolean.class, String.class, String.class, AuthResponseDTO.class,
                BaseAuthResponseDTO.class, Map.class
        );
        method.setAccessible(true);
        
        method.invoke(facade, authRequest, true, "partner1", "token123", authResp, baseResp, metadata);
        
        AutnTxn updatedAutnTxn = (AutnTxn) metadata.get(AutnTxn.class.getSimpleName());
        assertNotNull(updatedAutnTxn);
        assertTrue(updatedAutnTxn.getAuthTypeCode().contains(RequestType.IDENTITY_KEY_BINDING.getRequestType()));
        assertEquals(RequestType.IDENTITY_KEY_BINDING.getMessage(), updatedAutnTxn.getStatusComment());
    }

    @Test
    void testSaveToTxnTable_WithBaseAuthResponseAndAuthResponse_WithAutnTxn_ContainsEkycAuthRequest() throws Exception {
        AuthRequestDTO authRequest = mock(IdentityKeyBindingRequestDTO.class);
        when(authRequest.getMetadata()).thenReturn(new HashMap<>());
        AuthResponseDTO authResp = mock(AuthResponseDTO.class);
        BaseAuthResponseDTO baseResp = mock(BaseAuthResponseDTO.class);
        Map<String, Object> metadata = new HashMap<>();
        
        AutnTxn autnTxn = new AutnTxn();
        autnTxn.setAuthTypeCode(RequestType.EKYC_AUTH_REQUEST.getRequestType());
        autnTxn.setStatusComment("Status comment");
        metadata.put(AutnTxn.class.getSimpleName(), autnTxn);
        
        EnvUtil.setAuthTokenRequired(true);
        when(tokenIdManager.generateTokenId(anyString(), anyString())).thenReturn("authTokenId");
        when(partnerService.getPartner(anyString(), any())).thenReturn(Optional.empty());
        
        Method method = IdentityKeyBindingFacadeImpl.class.getDeclaredMethod(
                "saveToTxnTable",
                AuthRequestDTO.class, boolean.class, String.class, String.class, AuthResponseDTO.class,
                BaseAuthResponseDTO.class, Map.class
        );
        method.setAccessible(true);
        
        method.invoke(facade, authRequest, true, "partner1", "token123", authResp, baseResp, metadata);
        
        AutnTxn updatedAutnTxn = (AutnTxn) metadata.get(AutnTxn.class.getSimpleName());
        assertNotNull(updatedAutnTxn);
        // Should not be updated because authTypeCode already contains EKYC_AUTH_REQUEST
        assertEquals(RequestType.EKYC_AUTH_REQUEST.getRequestType(), updatedAutnTxn.getAuthTypeCode());
        assertEquals("Status comment", updatedAutnTxn.getStatusComment());
    }

    @Test
    void testSaveToTxnTable_WithBaseAuthResponseAndAuthResponse_WithAutnTxn_BothNull() throws Exception {
        AuthRequestDTO authRequest = mock(IdentityKeyBindingRequestDTO.class);
        when(authRequest.getMetadata()).thenReturn(new HashMap<>());
        AuthResponseDTO authResp = mock(AuthResponseDTO.class);
        BaseAuthResponseDTO baseResp = mock(BaseAuthResponseDTO.class);
        Map<String, Object> metadata = new HashMap<>();
        
        AutnTxn autnTxn = new AutnTxn();
        autnTxn.setAuthTypeCode(null);
        autnTxn.setStatusComment(null);
        metadata.put(AutnTxn.class.getSimpleName(), autnTxn);
        
        EnvUtil.setAuthTokenRequired(true);
        when(tokenIdManager.generateTokenId(anyString(), anyString())).thenReturn("authTokenId");
        when(partnerService.getPartner(anyString(), any())).thenReturn(Optional.empty());
        
        Method method = IdentityKeyBindingFacadeImpl.class.getDeclaredMethod(
                "saveToTxnTable",
                AuthRequestDTO.class, boolean.class, String.class, String.class, AuthResponseDTO.class,
                BaseAuthResponseDTO.class, Map.class
        );
        method.setAccessible(true);
        
        method.invoke(facade, authRequest, true, "partner1", "token123", authResp, baseResp, metadata);
        
        AutnTxn updatedAutnTxn = (AutnTxn) metadata.get(AutnTxn.class.getSimpleName());
        assertNotNull(updatedAutnTxn);
        assertEquals(RequestType.IDENTITY_KEY_BINDING.getRequestType(), updatedAutnTxn.getAuthTypeCode());
        assertEquals(RequestType.IDENTITY_KEY_BINDING.getMessage(), updatedAutnTxn.getStatusComment());
    }

    @Test
    void testProcessIdentityKeyBindingBusinessExceptionPath() {
        IdentityKeyBindingRequestDTO requestDTO = mock(IdentityKeyBindingRequestDTO.class);
        AuthResponseDTO authResp = mock(AuthResponseDTO.class);
        Map<String, Object> metadata = new HashMap<>();
        Map<String, Object> idResDTO = new HashMap<>();
        metadata.put(IdAuthCommonConstants.IDENTITY_DATA, idResDTO);

        when(idService.getToken(anyMap())).thenThrow(new RuntimeException(new IdAuthenticationBusinessException("TOKEN_ERR", "Token extraction failed")));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                facade.processIdentityKeyBinding(requestDTO, authResp, "partner1", "oidc", metadata));

        assertTrue(ex.getCause() instanceof IdAuthenticationBusinessException);
        IdAuthenticationBusinessException cause = (IdAuthenticationBusinessException) ex.getCause();

        assertEquals("TOKEN_ERR", cause.getErrorCode());
    }

    @Test
    void testDoProcessIdKeyBindingNullRequestPath() throws Exception {
        String token = "token", idHash = "idhash";
        Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();

        java.lang.reflect.Method method = IdentityKeyBindingFacadeImpl.class
                .getDeclaredMethod("doProcessIdKeyBinding", IdentityKeyBindingRequestDTO.class, AuthResponseDTO.class,
                        String.class, String.class, String.class, String.class, Map.class);
        method.setAccessible(true);

        // Test NULL REQUEST path (line ~165: if (identityKeyBindingRequestDTO != null))
        @SuppressWarnings("unchecked")
        Map.Entry<IdentityKeyBindingResponseDto, Boolean> result =
                (Map.Entry<IdentityKeyBindingResponseDto, Boolean>) method.invoke(facade, null,
                        mock(AuthResponseDTO.class), "pid", "oidc", token, idHash, idInfo);

        assertNotNull(result.getKey());
        assertFalse(result.getValue()); // Returns false for null request
    }

    @Test
    void testDoProcessIdKeyBindingValidPath() throws Exception {
        IdentityKeyBindingRequestDTO req = mock(IdentityKeyBindingRequestDTO.class);
        AuthResponseDTO authResp = mock(AuthResponseDTO.class);
        ResponseDTO responseDTO = mock(ResponseDTO.class);

        when(authResp.getResponse()).thenReturn(responseDTO);
        when(authResp.getResponseTime()).thenReturn("2024-01-01T00:00:00Z");
        when(responseDTO.isAuthStatus()).thenReturn(true);
        when(responseDTO.getAuthToken()).thenReturn("authToken");

        java.lang.reflect.Method method = IdentityKeyBindingFacadeImpl.class
                .getDeclaredMethod("doProcessIdKeyBinding", IdentityKeyBindingRequestDTO.class, AuthResponseDTO.class,
                        String.class, String.class, String.class, String.class, Map.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map.Entry<IdentityKeyBindingResponseDto, Boolean> result =
                (Map.Entry<IdentityKeyBindingResponseDto, Boolean>) method.invoke(facade, req, authResp,
                        "pid", "oidc", "token", "idhash", new HashMap<>());

        assertTrue(result.getValue()); // Success path
        assertEquals("authToken", result.getKey().getResponse().getAuthToken());
    }

    @Test
    void testAuthenticateIndividualSuccess() throws Exception {
        AuthRequestDTO authRequest = mock(IdentityKeyBindingRequestDTO.class);
        when(authRequest.getIndividualId()).thenReturn("1234");
        when(authRequest.getIndividualIdType()).thenReturn("UIN");

        IdentityKeyBindingDTO keyBindingDTO = mock(IdentityKeyBindingDTO.class);
        when(((IdentityKeyBindingRequestDTO) authRequest).getIdentityKeyBinding()).thenReturn(keyBindingDTO);
        Map<String, Object> pubKeyMap = new HashMap<>();
        pubKeyMap.put("kty", "RSA");
        when(keyBindingDTO.getPublicKeyJWK()).thenReturn(pubKeyMap);

        doNothing().when(idService).checkIdKeyBindingPermitted(anyString(), anyString());
        when(keyBindingService.isPublicKeyBinded(anyString(), any())).thenReturn(false);

        AuthResponseDTO expectedResponse = new AuthResponseDTO();
        expectedResponse.setResponse(new ResponseDTO());
        expectedResponse.getResponse().setAuthStatus(true);

        ObjectWithMetadata meta = mock(ObjectWithMetadata.class);
        when(authFacade.authenticateIndividual(eq(authRequest), eq(true), anyString(), anyString(),
                eq(IdAuthCommonConstants.KEY_BINDING_CONSUME_VID_DEFAULT), eq(meta)))
                .thenReturn(expectedResponse);

        AuthResponseDTO result = facade.authenticateIndividual(authRequest, "pid", "apiKey", meta);

        assertNotNull(result);
        assertTrue(result.getResponse().isAuthStatus());
        verify(idService).checkIdKeyBindingPermitted(anyString(), anyString());
        verify(keyBindingService).isPublicKeyBinded(anyString(), any());
    }

    @Test
    void testAuthenticateIndividualWithIdAuthenticationDaoException() throws Exception {
        AuthRequestDTO authRequest = mock(IdentityKeyBindingRequestDTO.class);
        when(authRequest.getIndividualId()).thenReturn("1234");
        when(authRequest.getIndividualIdType()).thenReturn("UIN");

        IdentityKeyBindingDTO keyBindingDTO = mock(IdentityKeyBindingDTO.class);
        when(((IdentityKeyBindingRequestDTO) authRequest).getIdentityKeyBinding()).thenReturn(keyBindingDTO);
        Map<String, Object> pubKeyMap = new HashMap<>();
        pubKeyMap.put("kty", "RSA");
        when(keyBindingDTO.getPublicKeyJWK()).thenReturn(pubKeyMap);

        doNothing().when(idService).checkIdKeyBindingPermitted(anyString(), anyString());
        when(keyBindingService.isPublicKeyBinded(anyString(), any())).thenReturn(false);

        ObjectWithMetadata meta = mock(ObjectWithMetadata.class);
        when(authFacade.authenticateIndividual(eq(authRequest), eq(true), anyString(), anyString(),
                eq(IdAuthCommonConstants.KEY_BINDING_CONSUME_VID_DEFAULT), eq(meta)))
                .thenThrow(new IdAuthenticationDaoException("DAO_ERROR", "Database error"));

        assertThrows(IdAuthenticationDaoException.class, () ->
                facade.authenticateIndividual(authRequest, "pid", "apiKey", meta));
    }

    @Test
    void testAuthenticateIndividualCheckIdKeyBindingThrowsException() throws Exception {
        AuthRequestDTO authRequest = mock(AuthRequestDTO.class);
        when(authRequest.getIndividualId()).thenReturn("1234");
        when(authRequest.getIndividualIdType()).thenReturn("VID");

        doThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.KEY_BINDING_NOT_ALLOWED.getErrorCode(),
                "Key binding not allowed"))
                .when(idService).checkIdKeyBindingPermitted(anyString(), anyString());

        ObjectWithMetadata meta = mock(ObjectWithMetadata.class);

        assertThrows(IdAuthenticationBusinessException.class, () ->
                facade.authenticateIndividual(authRequest, "pid", "apiKey", meta));
    }

    @Test
    void testAuthenticateIndividualPublicKeyAlreadyBinded1() throws Exception {
        AuthRequestDTO authRequest = mock(IdentityKeyBindingRequestDTO.class);
        when(authRequest.getIndividualId()).thenReturn("1234");
        when(authRequest.getIndividualIdType()).thenReturn(String.valueOf(IdType.UIN));

        IdentityKeyBindingDTO keyBindingDTO = mock(IdentityKeyBindingDTO.class);
        Map<String, Object> pubKeyMap = new HashMap<>();
        pubKeyMap.put("kty", "RSA");
        when(keyBindingDTO.getPublicKeyJWK()).thenReturn(pubKeyMap);
        when(((IdentityKeyBindingRequestDTO) authRequest).getIdentityKeyBinding()).thenReturn(keyBindingDTO);

        doNothing().when(idService).checkIdKeyBindingPermitted(anyString(), anyString());
        when(keyBindingService.isPublicKeyBinded(anyString(), any())).thenReturn(true);

        ObjectWithMetadata meta = mock(ObjectWithMetadata.class);

        IdAuthenticationBusinessException ex = assertThrows(IdAuthenticationBusinessException.class, () ->
                facade.authenticateIndividual(authRequest, "pid", "apiKey", meta));
        assertEquals(IdAuthenticationErrorConstants.PUBLIC_KEY_BINDING_NOT_ALLOWED.getErrorCode(), ex.getErrorCode());
    }

    @Test
    void testDoProcessIdKeyBindingWithNullResponseDTO() throws Exception {
        IdentityKeyBindingRequestDTO req = mock(IdentityKeyBindingRequestDTO.class);
        AuthResponseDTO authResp = mock(AuthResponseDTO.class);
        when(authResp.getResponse()).thenReturn(null);
        when(authResp.getResponseTime()).thenReturn("2024-01-01T00:00:00Z");

        Method method = IdentityKeyBindingFacadeImpl.class.getDeclaredMethod(
                "doProcessIdKeyBinding",
                IdentityKeyBindingRequestDTO.class,
                AuthResponseDTO.class,
                String.class,
                String.class,
                String.class,
                String.class,
                Map.class
        );
        method.setAccessible(true);

        Map.Entry<IdentityKeyBindingResponseDto, Boolean> result = (Map.Entry<IdentityKeyBindingResponseDto, Boolean>)
                method.invoke(facade, req, authResp, "pid", "oidc", "token", "hash", new HashMap<>());

        assertNotNull(result);
        assertFalse(result.getValue());
    }

    @Test
    void testProcessIdentityKeyBindingSuccess_AllLinesCovered() throws Exception {
        IdentityKeyBindingRequestDTO requestDTO = mock(IdentityKeyBindingRequestDTO.class);
        when(requestDTO.getTransactionID()).thenReturn("txn123");
        when(requestDTO.getIndividualIdType()).thenReturn("UIN");
        lenient().when(requestDTO.getRequestTime()).thenReturn("2024-01-01T00:00:00Z");
        AuthResponseDTO authResp = mock(AuthResponseDTO.class);
        ResponseDTO responseDTO = mock(ResponseDTO.class);
        when(authResp.getResponse()).thenReturn(responseDTO);
        when(authResp.getResponseTime()).thenReturn("2024-01-01T00:00:00Z");
        when(authResp.getId()).thenReturn("id123");
        when(authResp.getTransactionID()).thenReturn("txn123");
        when(authResp.getVersion()).thenReturn("1.0");
        when(authResp.getErrors()).thenReturn(null);
        when(responseDTO.isAuthStatus()).thenReturn(true);
        when(responseDTO.getAuthToken()).thenReturn("authToken");
        
        Map<String, Object> metadata = new HashMap<>();
        Map<String, Object> idResDTO = new HashMap<>();
        metadata.put(IdAuthCommonConstants.IDENTITY_DATA, idResDTO);
        
        when(idService.getToken(anyMap())).thenReturn("token123");
        when(idService.getIdHash(anyMap())).thenReturn("hash123");
        when(keyBindingService.createAndSaveKeyBindingCertificate(any(), any(), anyString(), anyString()))
                .thenReturn("certificateData");
        EnvUtil.setAuthTokenRequired(true);
        when(tokenIdManager.generateTokenId(anyString(), anyString())).thenReturn("authTokenId");
        when(partnerService.getPartner(anyString(), any())).thenReturn(Optional.empty());
        
        IdentityKeyBindingResponseDto result = facade.processIdentityKeyBinding(requestDTO, authResp, "partner1", "oidc", metadata);
        
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertTrue(result.getResponse().isBindingAuthStatus());
        assertEquals("certificateData", result.getResponse().getIdentityCertificate());
        
        // Verify all lines are covered:
        verify(idService).getToken(idResDTO);
        verify(idService).getIdHash(idResDTO); // Line: idHash = idService.getIdHash(idResDTO);
        // IdInfoFetcher.getIdInfo(idResDTO) is called internally (static method, covered by execution)
        verify(keyBindingService).createAndSaveKeyBindingCertificate(any(), any(), eq("token123"), eq("partner1"));
        verify(tokenIdManager).generateTokenId("token123", "partner1"); // saveToTxnTable called
        verify(auditHelper).audit(eq(AuditModules.IDENTITY_KEY_BINDING), 
                eq(AuditEvents.KEY_BINDIN_REQUEST_RESPONSE),
                eq("txn123"), 
                eq(IdType.UIN),
                eq("Identity Key Binding status : true")); // Success audit message
    }

}
