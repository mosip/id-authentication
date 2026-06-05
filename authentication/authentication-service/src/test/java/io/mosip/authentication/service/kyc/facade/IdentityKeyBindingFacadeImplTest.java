package io.mosip.authentication.service.kyc.facade;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingResponseDto;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.service.IdentityKeyBindingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class IdentityKeyBindingFacadeImplTest {

    @InjectMocks
    private IdentityKeyBindingFacadeImpl facade;
    @Mock
    private IdService<AutnTxn> idService;
    @Mock
    private IdentityKeyBindingService keyBindingService;


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
}
