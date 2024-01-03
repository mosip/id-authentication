package io.mosip.authentication.esignet.integration.service;

import io.mosip.esignet.api.dto.AuditDTO;
import io.mosip.esignet.api.util.Action;
import io.mosip.esignet.api.util.ActionStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.authentication.esignet.integration.dto.AuditResponse;
import io.mosip.authentication.esignet.integration.helper.AuthTransactionHelper;
import io.mosip.kernel.core.http.ResponseWrapper;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class IdaAuditPluginImplTest {
    @InjectMocks
    private IdaAuditPluginImpl idaAuditPlugin;
    @Mock
    private AuthTransactionHelper authTransactionHelper;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RestTemplate restTemplate;
    @Test
    public void logAudit_WithValidDetails_ThenPass() {
        Action action = Action.AUTHENTICATE;
        ActionStatus status = ActionStatus.SUCCESS;
        AuditDTO auditDTO = new AuditDTO();
        try {
            idaAuditPlugin.logAudit(action, status, auditDTO, null);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail();
        }
    }
    @Test
    public void logAudit_WithThrowable_ThenPass() {
        Action action = Action.GENERATE_TOKEN;
        ActionStatus status = ActionStatus.SUCCESS;
        AuditDTO auditDTO = new AuditDTO();
        Throwable throwable = new RuntimeException("Test Exception");
        try {
            idaAuditPlugin.logAudit(action, status, auditDTO, throwable);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail();
        }
    }
    @Test
    public void logAudit_WithUsername_WithValidDetails_ThenPass() {
        String username = "username";
        Action action = Action.OIDC_CLIENT_UPDATE;
        ActionStatus status = ActionStatus.SUCCESS;
        AuditDTO auditDTO = new AuditDTO();
        try {
            idaAuditPlugin.logAudit(username, action, status, auditDTO, null);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void logAudit_WithUsername_WithThrowable() throws Exception {
        String username = "username";
        Action action = Action.GENERATE_TOKEN;
        ActionStatus status = ActionStatus.SUCCESS;
        AuditDTO auditDTO = new AuditDTO();
        Throwable throwable = new RuntimeException("Test Exception");
        try {
            idaAuditPlugin.logAudit(username,action, status, auditDTO, throwable);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail();
        }
    }
    @Test
    public void logAudit_WithValidStatus_ThenPass() throws Exception {
        ReflectionTestUtils.setField(idaAuditPlugin, "auditManagerUrl", "auditManagerUrl");
        String username = "username";
        Action action = Action.SAVE_CONSENT;
        ActionStatus status = ActionStatus.SUCCESS;
        AuditDTO auditDTO = new AuditDTO();
        ResponseWrapper<AuditResponse> mockresponseWrapper = new ResponseWrapper<>();
        ResponseEntity<ResponseWrapper> responseEntity = ResponseEntity.ok(mockresponseWrapper);
        ParameterizedTypeReference<ResponseWrapper> responseType =
                new ParameterizedTypeReference<ResponseWrapper>() {
                };
        Mockito.when(authTransactionHelper.getAuthToken()).thenReturn("authToken");
        Mockito.when(objectMapper.writeValueAsString(any())).thenReturn("requestBody");
        Mockito.when(restTemplate.exchange(
                Mockito.any(RequestEntity.class),
                Mockito.eq(responseType)
        )).thenReturn(responseEntity);
        try {
            idaAuditPlugin.logAudit(username,action, status, auditDTO, null);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail();
        }
    }
    @Test
    public void logAudit_WithUnauthorizedStatus_ThenPass() throws Exception {
        ReflectionTestUtils.setField(idaAuditPlugin, "auditManagerUrl", "auditManagerUrl");
        String username = "username";
        Action action = Action.SAVE_CONSENT;
        ActionStatus status = ActionStatus.SUCCESS;
        AuditDTO auditDTO = new AuditDTO();
        ResponseWrapper<AuditResponse> mockresponseWrapper = new ResponseWrapper<>();
        ResponseEntity<ResponseWrapper> responseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockresponseWrapper);
        ParameterizedTypeReference<ResponseWrapper> responseType =
                new ParameterizedTypeReference<ResponseWrapper>() {
                };
        Mockito.when(authTransactionHelper.getAuthToken()).thenReturn("authToken");
        Mockito.when(objectMapper.writeValueAsString(any())).thenReturn("requestBody");
        Mockito.when(restTemplate.exchange(
                Mockito.any(RequestEntity.class),
                Mockito.eq(responseType)
        )).thenReturn(responseEntity);
        try {
            idaAuditPlugin.logAudit(username,action, status, auditDTO, null);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail();
        }
    }
    @Test
    public void logAudit_WithForbiddenStatus_ThenPass() throws Exception {
        ReflectionTestUtils.setField(idaAuditPlugin, "auditManagerUrl", "auditManagerUrl");
        String username = "username";
        Action action = Action.SAVE_CONSENT;
        ActionStatus status = ActionStatus.SUCCESS;
        AuditDTO auditDTO = new AuditDTO();
        ResponseWrapper<AuditResponse> mockresponseWrapper = new ResponseWrapper<>();
        ResponseEntity<ResponseWrapper> responseEntity = ResponseEntity.status(HttpStatus.FORBIDDEN).body(mockresponseWrapper);
        ParameterizedTypeReference<ResponseWrapper> responseType =
                new ParameterizedTypeReference<ResponseWrapper>() {
                };
        Mockito.when(authTransactionHelper.getAuthToken()).thenReturn("authToken");
        Mockito.when(objectMapper.writeValueAsString(any())).thenReturn("requestBody");
        Mockito.when(restTemplate.exchange(
                Mockito.any(RequestEntity.class),
                Mockito.eq(responseType)
        )).thenReturn(responseEntity);
        try {
            idaAuditPlugin.logAudit(username,action, status, auditDTO, null);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}