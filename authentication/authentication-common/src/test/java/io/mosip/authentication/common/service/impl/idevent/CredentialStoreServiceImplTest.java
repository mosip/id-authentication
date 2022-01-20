package io.mosip.authentication.common.service.impl.idevent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.CredentialEventStore;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.integration.CredentialRequestManager;
import io.mosip.authentication.common.service.integration.DataShareManager;
import io.mosip.authentication.common.service.repository.CredentialEventStoreRepository;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.websub.impl.CredentialStoreStatusEventPublisher;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthRetryException;
import io.mosip.authentication.core.exception.IdAuthenticationBaseException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.CredentialRequestIdsDto;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.kernel.core.websub.model.EventModel;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest

public class CredentialStoreServiceImplTest {

    /** The Credential Store Service impl. */
    @InjectMocks
    private CredentialStoreServiceImpl credentialStoreServiceImpl;

    @Mock
    private CredentialStoreServiceImpl credentialStoreServiceImplMock;

    @Mock
    private AuditHelper auditHelper;

    @Mock
    private CredentialEventStoreRepository credentialEventRepo;

    @Mock
    private CredentialStoreStatusEventPublisher credentialStoreStatusEventPublisher;

    @Mock
    private WebSubHelper webSubHelper;

    @Mock
    private IdaUinHashSaltRepo uinHashSaltRepo;

    @Mock
    private IdAuthSecurityManager securityManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private DataShareManager dataShareManager;

    @Mock
    private IdentityCacheRepository identityCacheRepo;

    @Mock
    private CredentialRequestManager credentialRequestManager;


    /**
     * Before.
     */
    @Before
    public void before() {
        ReflectionTestUtils.setField(credentialStoreServiceImpl, "intervalExponentialMultiplier", 2);
        ReflectionTestUtils.setField(credentialStoreServiceImpl, "retryInterval", 3);
        ReflectionTestUtils.setField(credentialStoreServiceImpl, "maxRetryCount", 5);
        ReflectionTestUtils.setField(credentialStoreServiceImpl, "maxExponentialRetryIntervalLimitMillis", 4);
        ReflectionTestUtils.setField(credentialStoreServiceImpl, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(credentialStoreServiceImpl, "dataShareManager", dataShareManager);
        ReflectionTestUtils.setField(credentialStoreServiceImpl, "identityCacheRepo", identityCacheRepo);
    }

    /**
     * This class tests the processCredentialStoreEvent method
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     * @throws IdAuthenticationBusinessException
     * @throws SecurityException
     * @throws IOException
     */
    @Test
    public void ProcessCredentialStoreEventTest()
            throws RestServiceException, IOException, IdAuthenticationBusinessException {
        CredentialEventStore credentialEventStore = getCredentialEventStore();
        credentialEventStore.setStatusCode("FAILED");
        credentialEventStore.setRetryCount(1);
        Map<String, Object> credentialData = new HashMap<>();
        Map<String, String> map = objectMapper.readValue(getCredentialServiceJsonStr(), Map.class);
        credentialData.put("credentialSubject", map);
        Mockito.when(dataShareManager.downloadObject(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean())).thenReturn(credentialData);
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "processCredentialStoreEvent", credentialEventStore);
    }

    /**
     * This class tests the processCredentialStoreEvent method for RuntimeException case
     *
     */
    @Test(expected = RuntimeException.class)
    public void RuntimeExceptionProcessCredentialStoreEventTest() {
        CredentialEventStore credentialEventStore = new CredentialEventStore();
        credentialEventStore.setStatusCode("STORED");
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "processCredentialStoreEvent", credentialEventStore);
    }
    
    @Test
    public void ProcessCredentialStoreEventTest_exception()
            throws RestServiceException, IOException, IdAuthenticationBusinessException {
        CredentialEventStore credentialEventStore = getCredentialEventStore();
        credentialEventStore.setStatusCode("FAILED");
        credentialEventStore.setRetryCount(1);
        credentialEventStore.setEventObject("invalid json");
        Map<String, Object> credentialData = new HashMap<>();
        Map<String, String> map = objectMapper.readValue(getCredentialServiceJsonStr(), Map.class);
        credentialData.put("credentialSubject", map);
        Mockito.when(dataShareManager.downloadObject(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean())).thenReturn(credentialData);
        try {
            ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "processCredentialStoreEvent", credentialEventStore);
		} catch (UndeclaredThrowableException e) {
			if (e.getUndeclaredThrowable() instanceof IdAuthenticationBaseException) {
				IdAuthenticationBaseException idAuthenticationBaseException = (IdAuthenticationBaseException) e
						.getUndeclaredThrowable();
				assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						idAuthenticationBaseException.getErrorCode());
			}
		}
    }

    /**
     * This class tests the updateEventProcessingStatus method
     * When isSuccess =false
     */
    @Test
    public void UpdateEventProcessingStatusTest(){
        CredentialEventStore credentialEventStore = getCredentialEventStore();
        boolean isSuccess = false, isRecoverableException=true;
        credentialEventStore.setStatusCode("FAILED");
        credentialEventStore.setRetryCount(6);
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "updateEventProcessingStatus", credentialEventStore, isSuccess, isRecoverableException, credentialEventStore.getStatusCode());
        //
        //when retryCount < maxRetryCount and status_code = FAILED
        credentialEventStore.setRetryCount(2);
        credentialEventStore.setStatusCode("FAILED");
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "updateEventProcessingStatus", credentialEventStore, isSuccess, isRecoverableException, credentialEventStore.getStatusCode());
        //
        //when retryCount < maxRetryCount and status_code = NEW
        credentialEventStore.setStatusCode("NEW");
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "updateEventProcessingStatus", credentialEventStore, isSuccess, isRecoverableException, credentialEventStore.getStatusCode());
        //
        //when isRecoverableException=false
        isRecoverableException=false;
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "updateEventProcessingStatus", credentialEventStore, isSuccess, isRecoverableException, credentialEventStore.getStatusCode());
    }
    
    @Test
    public void UpdateEventProcessingStatusTest_auditError() throws IDDataValidationException{
        CredentialEventStore credentialEventStore = getCredentialEventStore();
        boolean isSuccess = false, isRecoverableException=true;
        credentialEventStore.setStatusCode("FAILED");
        credentialEventStore.setRetryCount(6);
        String requestId = "5b679189-6f3a-46b5-9979-b4ed8d3230c2";
		doThrow(new IDDataValidationException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS))
			.when(auditHelper).audit(AuditModules.CREDENTIAL_STORAGE, AuditEvents.CREDENTIAL_STORED_EVENT, requestId , "request-id", "FAILED");
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "updateEventProcessingStatus", credentialEventStore, isSuccess, isRecoverableException, credentialEventStore.getStatusCode());
        //
    }

    /**
     * This class tests the storeEventModel method
     *
     * @throws IOException
     */
    @Test
    public void storeEventModelTest() throws IOException {
        EventModel eventModel = objectMapper.readValue(getEventModelJsonStr(), EventModel.class);
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "storeEventModel", eventModel);
    }

    /**
     * This class tests the doProcessCredentialStoreEvent method when dataShareUri=null
     *
     * @throws IdAuthenticationBusinessException the id authentication business exception
     * @throws RestServiceException
     * @throws IOException
     */
    @Test
    public void doProcessCredentialStoreEventTest_datashareUriNull() throws IOException, RestServiceException, IdAuthenticationBusinessException {
        CredentialEventStore credentialEventStore = getCredentialEventStore();
        credentialEventStore.setEventObject("{\"publisher\":\"CREDENTIAL_SERVICE\",\"topic\":\"mpartner-default-auth/CREDENTIAL_ISSUED\",\"publishedOn\":\"2021-11-02T09:18:52.418Z\",\"event\":{\"id\":\"437ca068-702f-4b14-a21a-b3994c094d38\",\"transactionId\":\"a84f9af6-025d-4c68-a005-4da9cddb0874\",\"type\":{\"namespace\":\"mosip\",\"name\":\"mosip\"},\"timestamp\":\"2021-11-02T09:18:52.418Z\",\"dataShareUri\":null,\"data\":{\"SALT\":\"Q74F5OnTZdw5qiOFp6h6Ww\",\"MODULO\":\"943\",\"expiry_timestamp\":null,\"transaction_limit\":null,\"id_hash\":\"9DCF43F9973826A8331209CAA22A8080995420D992D0BBEE2A3356077EA525E3\",\"TOKEN\":\"362737013453447806883457690320262449\",\"demoEncryptedRandomKey\":\"1v87FAFPnjRzKo8mF6K715e0EXv-rnqF9_aJoN5OUp4GYkjXPjxZHR4MFHob_CKM9Wx0ZOpy0oA8Kcv-0kI1GEDfl070vppDaS0gG30P6QOUy5aEYY4nXffc0nqqrqdC4rzY4LdWbrxxkoyx1Q4BhNZHiA7Tm931-kjdC2YJkDFMHburu72N8CuI6wktAuhQPagWCGL2hGUkdbcFhvD8045Y9oggLfDYNH1Oaj9XIwEEvyAaHvH8mfJxQ5aiLp23mt6PA3QZ4uaVxMvprYBGR8CypGZIBKLfCCfamHsW01ae33mPFyQH1AKKIaJ1XaoeoIgJq9ocUZ292hl3rtgdOuj6eJmcrMOwpiOhHNMY0ndAuH1-RMWdY6CZ2FG-o5VI\",\"demoRankomKeyIndex\":\"4150\",\"bioEncryptedRandomKey\":\"1v87FAFPnjRzKo8mF6K715e0EXv-rnqF9_aJoN5OUp4U5vX_6IU_5l5gi_ATfr1cgICFAViR06A9LrDq8J01i_ICoHaCMqmKMBVk3B4lJ8zAyyYDbM4ztLYIDK95ozZArJuB4i3aJp7lh2XhEqn4xpSzLXJRIjKGuHckq-81IxQmozgo1eE07NlJ3-7tt9thjWujgEmJABpir893tZnxG3br9yAecqUTbnjxrXpvNRdCJm7SgwgJ-tQyE49QvDeXWw_ucXnx1SyQ99eeDafMzgc4JffrQeFCWwryo0q6TzgJ9qAm8SITAy0sc3Q3BJgddCnFH0s1GHjKPBsJBxknb_N3b5zB2yXomVn3bsKV7V-bXiC8Gf9CNj0yZf9xV-Ns\",\"bioRankomKeyIndex\":\"8678\",\"proof\":{\"signature\":\"eyJ4NWMiOlsiTUlJRHRUQ0NBcDJnQXdJQkFnSUlzNkphZWlpZE1va3dEUVlKS29aSWh2Y05BUUVMQlFBd2NERUxNQWtHQTFVRUJoTUNTVTR4Q3pBSkJnTlZCQWdNQWt0Qk1SSXdFQVlEVlFRSERBbENRVTVIUVV4UFVrVXhEVEFMQmdOVkJBb01CRWxKVkVJeEdqQVlCZ05WQkFzTUVVMVBVMGxRTFZSRlEwZ3RRMFZPVkVWU01SVXdFd1lEVlFRRERBeDNkM2N1Ylc5emFYQXVhVzh3SGhjTk1qRXhNREkyTVRRd05EVTRXaGNOTWpReE1ESTFNVFF3TkRVNFdqQitNUXN3Q1FZRFZRUUdFd0pKVGpFTE1Ba0dBMVVFQ0F3Q1MwRXhFakFRQmdOVkJBY01DVUpCVGtkQlRFOVNSVEVOTUFzR0ExVUVDZ3dFU1VsVVFqRWpNQ0VHQTFVRUN3d2FUVTlUU1ZBdFZFVkRTQzFEUlU1VVJWSWdLRXRGVWs1RlRDa3hHakFZQmdOVkJBTU1FWGQzZHk1dGIzTnBjQzVwYnkxVFNVZE9NSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXJYWDhYR2tscGxrOGMyRC8zTGhWelZucTVJVGJ1a21sV2ZORnRnWUNYY1Y3d2IzWUhhaUNudWI0STVkbDZmZGxLUEVUN2t6dTllYno3VTlVVUVXVzc5VWF0NHY0WFV0bEJ1ejJ6VmVjckZpcERtLytNTU5JNEMzSXpwbmVrVlB2NUl0VjBzSzVZcGVRck5HbFh3NFI4TzlTYk9NcUE4NElqQTZsanE2enFZZlpTRzZkSnJSbFVqQS9KczdweVV6Z0t4U0pBYXdmaVFWaThXK05WTnpPdjdUTEdESjhaR3BFT1lnUkdDbjVpU21UVldtVzF5UVNhTEQrT3BlTHhkUHJocE9ZbWtMbkd2alR6aHkxQm5RWVhmVGxyeVd1b2drVzM5NUZBTW9SOEI1ekZiK0FKbHlVVm9zMU5MUEU3cWM5UDIrSDZlbURmUHNhVDNRMjRrWXZCUUlEQVFBQm8wVXdRekFTQmdOVkhSTUJBZjhFQ0RBR0FRSC9BZ0VCTUIwR0ExVWREZ1FXQkJTOHpQL0lTNDlrOGsxblJIb05HMGZNUVQ5elJUQU9CZ05WSFE4QkFmOEVCQU1DQW9Rd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFIWWlqN3p4Sy84dGdrS1czMkhRejRUVFN2d0VqazZmY1dVMWFTbkJkVzFWS2w1TGlrTXZ6OC96M0hUOEpmOW4wdVgwdlFET0M5Mnh4SitNWmU3RTNoWUJ4SFNPRHVyL3FkQ1JGRFFYUnQyMzU4OWpmSXZBQjN6ejJyakFuZ1hzTUxQRldRV2tZdnU0clJFVWFVUHNQRmp0NkhUM3lZcUJrbHJXL0NaNGxDTW9vZ1VNOGRTS1drZ0ZlTHBKbXhIMVhhZGo0R0VCazhaSTZUaVU2cGZOQ0hRMnlLanVEdC85V1kwMzFsTTJmcGh6Mmw2OUJHV2Z5MWZQNEQ3Mjl2NVR0WEM3eGlvb2ZCV3hWL28wNm8xZXRxUTFKUVpmdS9zNWRRQjZ0UEZhbVlwOTBhTmFqYjMwLzI4bkJIdnN0bllhZjIzL0d1cnNYa1dYSzBOM1pJUXhINTA9Il0sIng1dCNTMjU2IjoiYmNaX3RKSm42SXFRVUhNVnNFTlNzS2JMOXlGUGxTTzVqempWdFpJa3AwOCIsImFsZyI6IlJTMjU2In0..muzYpaSxQyjIjOdloDxhMgWj5Ljs8MLPTgZiAtDiiBt_OuaCn3L939uJBREosodYCVlnQu54MDvUoeWPbBJYtJWpm6HceCfgLSzOBnI3gv7bblJ6fcK__HzSEL4gbRaysC3tkO5yx5C0v0JR3vk_pjZV2PFz9y3vpMdxiuaqxWhUpV9qLWM4Y7JVKbzEC2EOnIZAZtkLGxViczK29F9AK10r4Z5djZ4-FYXDXFO0m24a5n5-k7n5UMOL4YL9LslZX8Odd0H8orvAv7P_I3_AmnjKcJDuU7IlQGuD4Em-Id4eFi1pA4vNCr7pUMjb_rNDngnQ43XJbRh6yA2SrU_n_w\"},\"credentialType\":\"auth\",\"protectionKey\":null}}}");
        Map<String, Object> credentialData = new HashMap<>();
        Map<String, String> map = objectMapper.readValue(getCredentialServiceJsonStr(), Map.class);
        credentialData.put("credentialSubject", map);
        Mockito.when(dataShareManager.downloadObject(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean())).thenReturn(credentialData);
        try {
			ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "doProcessCredentialStoreEvent", credentialEventStore);
		} catch (UndeclaredThrowableException e) {
			if (e.getUndeclaredThrowable() instanceof IdAuthenticationBaseException) {
				IdAuthenticationBaseException idAuthenticationBaseException = (IdAuthenticationBaseException) e
						.getUndeclaredThrowable();
				assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						idAuthenticationBaseException.getErrorCode());
			}
		}
    }
    
    @Test
    public void doProcessCredentialStoreEventTest_parseError() throws IOException, RestServiceException, IdAuthenticationBusinessException {
        CredentialEventStore credentialEventStore = getCredentialEventStore();
        credentialEventStore.setEventObject("{\"publisher\":\"CREDENTIAL_SERVICE\" \"topic\":\"mpartner-default-auth/CREDENTIAL_ISSUED\",\"publishedOn\":\"2021-11-02T09:18:52.418Z\",\"event\":{\"id\":\"437ca068-702f-4b14-a21a-b3994c094d38\",\"transactionId\":\"a84f9af6-025d-4c68-a005-4da9cddb0874\",\"type\":{\"namespace\":\"mosip\",\"name\":\"mosip\"},\"timestamp\":\"2021-11-02T09:18:52.418Z\",\"dataShareUri\":null,\"data\":{\"SALT\":\"Q74F5OnTZdw5qiOFp6h6Ww\",\"MODULO\":\"943\",\"expiry_timestamp\":null,\"transaction_limit\":null,\"id_hash\":\"9DCF43F9973826A8331209CAA22A8080995420D992D0BBEE2A3356077EA525E3\",\"TOKEN\":\"362737013453447806883457690320262449\",\"demoEncryptedRandomKey\":\"1v87FAFPnjRzKo8mF6K715e0EXv-rnqF9_aJoN5OUp4GYkjXPjxZHR4MFHob_CKM9Wx0ZOpy0oA8Kcv-0kI1GEDfl070vppDaS0gG30P6QOUy5aEYY4nXffc0nqqrqdC4rzY4LdWbrxxkoyx1Q4BhNZHiA7Tm931-kjdC2YJkDFMHburu72N8CuI6wktAuhQPagWCGL2hGUkdbcFhvD8045Y9oggLfDYNH1Oaj9XIwEEvyAaHvH8mfJxQ5aiLp23mt6PA3QZ4uaVxMvprYBGR8CypGZIBKLfCCfamHsW01ae33mPFyQH1AKKIaJ1XaoeoIgJq9ocUZ292hl3rtgdOuj6eJmcrMOwpiOhHNMY0ndAuH1-RMWdY6CZ2FG-o5VI\",\"demoRankomKeyIndex\":\"4150\",\"bioEncryptedRandomKey\":\"1v87FAFPnjRzKo8mF6K715e0EXv-rnqF9_aJoN5OUp4U5vX_6IU_5l5gi_ATfr1cgICFAViR06A9LrDq8J01i_ICoHaCMqmKMBVk3B4lJ8zAyyYDbM4ztLYIDK95ozZArJuB4i3aJp7lh2XhEqn4xpSzLXJRIjKGuHckq-81IxQmozgo1eE07NlJ3-7tt9thjWujgEmJABpir893tZnxG3br9yAecqUTbnjxrXpvNRdCJm7SgwgJ-tQyE49QvDeXWw_ucXnx1SyQ99eeDafMzgc4JffrQeFCWwryo0q6TzgJ9qAm8SITAy0sc3Q3BJgddCnFH0s1GHjKPBsJBxknb_N3b5zB2yXomVn3bsKV7V-bXiC8Gf9CNj0yZf9xV-Ns\",\"bioRankomKeyIndex\":\"8678\",\"proof\":{\"signature\":\"eyJ4NWMiOlsiTUlJRHRUQ0NBcDJnQXdJQkFnSUlzNkphZWlpZE1va3dEUVlKS29aSWh2Y05BUUVMQlFBd2NERUxNQWtHQTFVRUJoTUNTVTR4Q3pBSkJnTlZCQWdNQWt0Qk1SSXdFQVlEVlFRSERBbENRVTVIUVV4UFVrVXhEVEFMQmdOVkJBb01CRWxKVkVJeEdqQVlCZ05WQkFzTUVVMVBVMGxRTFZSRlEwZ3RRMFZPVkVWU01SVXdFd1lEVlFRRERBeDNkM2N1Ylc5emFYQXVhVzh3SGhjTk1qRXhNREkyTVRRd05EVTRXaGNOTWpReE1ESTFNVFF3TkRVNFdqQitNUXN3Q1FZRFZRUUdFd0pKVGpFTE1Ba0dBMVVFQ0F3Q1MwRXhFakFRQmdOVkJBY01DVUpCVGtkQlRFOVNSVEVOTUFzR0ExVUVDZ3dFU1VsVVFqRWpNQ0VHQTFVRUN3d2FUVTlUU1ZBdFZFVkRTQzFEUlU1VVJWSWdLRXRGVWs1RlRDa3hHakFZQmdOVkJBTU1FWGQzZHk1dGIzTnBjQzVwYnkxVFNVZE9NSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXJYWDhYR2tscGxrOGMyRC8zTGhWelZucTVJVGJ1a21sV2ZORnRnWUNYY1Y3d2IzWUhhaUNudWI0STVkbDZmZGxLUEVUN2t6dTllYno3VTlVVUVXVzc5VWF0NHY0WFV0bEJ1ejJ6VmVjckZpcERtLytNTU5JNEMzSXpwbmVrVlB2NUl0VjBzSzVZcGVRck5HbFh3NFI4TzlTYk9NcUE4NElqQTZsanE2enFZZlpTRzZkSnJSbFVqQS9KczdweVV6Z0t4U0pBYXdmaVFWaThXK05WTnpPdjdUTEdESjhaR3BFT1lnUkdDbjVpU21UVldtVzF5UVNhTEQrT3BlTHhkUHJocE9ZbWtMbkd2alR6aHkxQm5RWVhmVGxyeVd1b2drVzM5NUZBTW9SOEI1ekZiK0FKbHlVVm9zMU5MUEU3cWM5UDIrSDZlbURmUHNhVDNRMjRrWXZCUUlEQVFBQm8wVXdRekFTQmdOVkhSTUJBZjhFQ0RBR0FRSC9BZ0VCTUIwR0ExVWREZ1FXQkJTOHpQL0lTNDlrOGsxblJIb05HMGZNUVQ5elJUQU9CZ05WSFE4QkFmOEVCQU1DQW9Rd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFIWWlqN3p4Sy84dGdrS1czMkhRejRUVFN2d0VqazZmY1dVMWFTbkJkVzFWS2w1TGlrTXZ6OC96M0hUOEpmOW4wdVgwdlFET0M5Mnh4SitNWmU3RTNoWUJ4SFNPRHVyL3FkQ1JGRFFYUnQyMzU4OWpmSXZBQjN6ejJyakFuZ1hzTUxQRldRV2tZdnU0clJFVWFVUHNQRmp0NkhUM3lZcUJrbHJXL0NaNGxDTW9vZ1VNOGRTS1drZ0ZlTHBKbXhIMVhhZGo0R0VCazhaSTZUaVU2cGZOQ0hRMnlLanVEdC85V1kwMzFsTTJmcGh6Mmw2OUJHV2Z5MWZQNEQ3Mjl2NVR0WEM3eGlvb2ZCV3hWL28wNm8xZXRxUTFKUVpmdS9zNWRRQjZ0UEZhbVlwOTBhTmFqYjMwLzI4bkJIdnN0bllhZjIzL0d1cnNYa1dYSzBOM1pJUXhINTA9Il0sIng1dCNTMjU2IjoiYmNaX3RKSm42SXFRVUhNVnNFTlNzS2JMOXlGUGxTTzVqempWdFpJa3AwOCIsImFsZyI6IlJTMjU2In0..muzYpaSxQyjIjOdloDxhMgWj5Ljs8MLPTgZiAtDiiBt_OuaCn3L939uJBREosodYCVlnQu54MDvUoeWPbBJYtJWpm6HceCfgLSzOBnI3gv7bblJ6fcK__HzSEL4gbRaysC3tkO5yx5C0v0JR3vk_pjZV2PFz9y3vpMdxiuaqxWhUpV9qLWM4Y7JVKbzEC2EOnIZAZtkLGxViczK29F9AK10r4Z5djZ4-FYXDXFO0m24a5n5-k7n5UMOL4YL9LslZX8Odd0H8orvAv7P_I3_AmnjKcJDuU7IlQGuD4Em-Id4eFi1pA4vNCr7pUMjb_rNDngnQ43XJbRh6yA2SrU_n_w\"},\"credentialType\":\"auth\",\"protectionKey\":null}}}");
        Map<String, Object> credentialData = new HashMap<>();
        Map<String, String> map = objectMapper.readValue(getCredentialServiceJsonStr(), Map.class);
        credentialData.put("credentialSubject", map);
        Mockito.when(dataShareManager.downloadObject(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean())).thenReturn(credentialData);
        try {
			ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "doProcessCredentialStoreEvent", credentialEventStore);
		} catch (UndeclaredThrowableException e) {
			if (e.getUndeclaredThrowable() instanceof IdAuthenticationBaseException) {
				IdAuthenticationBaseException idAuthenticationBaseException = (IdAuthenticationBaseException) e
						.getUndeclaredThrowable();
				assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						idAuthenticationBaseException.getErrorCode());
			}
		}
    }
    
    @Test
    public void doProcessCredentialStoreEventTest_dataShareDownloadError() throws IOException, RestServiceException, IdAuthenticationBusinessException {
        CredentialEventStore credentialEventStore = getCredentialEventStore();
        credentialEventStore.setEventObject("{\"publisher\":\"CREDENTIAL_SERVICE\", \"topic\":\"mpartner-default-auth/CREDENTIAL_ISSUED\",\"publishedOn\":\"2021-11-02T09:18:52.418Z\",\"event\":{\"id\":\"437ca068-702f-4b14-a21a-b3994c094d38\",\"transactionId\":\"a84f9af6-025d-4c68-a005-4da9cddb0874\",\"type\":{\"namespace\":\"mosip\",\"name\":\"mosip\"},\"timestamp\":\"2021-11-02T09:18:52.418Z\",\"dataShareUri\":\"http://test\",\"data\":{\"SALT\":\"Q74F5OnTZdw5qiOFp6h6Ww\",\"MODULO\":\"943\",\"expiry_timestamp\":null,\"transaction_limit\":null,\"id_hash\":\"9DCF43F9973826A8331209CAA22A8080995420D992D0BBEE2A3356077EA525E3\",\"TOKEN\":\"362737013453447806883457690320262449\",\"demoEncryptedRandomKey\":\"1v87FAFPnjRzKo8mF6K715e0EXv-rnqF9_aJoN5OUp4GYkjXPjxZHR4MFHob_CKM9Wx0ZOpy0oA8Kcv-0kI1GEDfl070vppDaS0gG30P6QOUy5aEYY4nXffc0nqqrqdC4rzY4LdWbrxxkoyx1Q4BhNZHiA7Tm931-kjdC2YJkDFMHburu72N8CuI6wktAuhQPagWCGL2hGUkdbcFhvD8045Y9oggLfDYNH1Oaj9XIwEEvyAaHvH8mfJxQ5aiLp23mt6PA3QZ4uaVxMvprYBGR8CypGZIBKLfCCfamHsW01ae33mPFyQH1AKKIaJ1XaoeoIgJq9ocUZ292hl3rtgdOuj6eJmcrMOwpiOhHNMY0ndAuH1-RMWdY6CZ2FG-o5VI\",\"demoRankomKeyIndex\":\"4150\",\"bioEncryptedRandomKey\":\"1v87FAFPnjRzKo8mF6K715e0EXv-rnqF9_aJoN5OUp4U5vX_6IU_5l5gi_ATfr1cgICFAViR06A9LrDq8J01i_ICoHaCMqmKMBVk3B4lJ8zAyyYDbM4ztLYIDK95ozZArJuB4i3aJp7lh2XhEqn4xpSzLXJRIjKGuHckq-81IxQmozgo1eE07NlJ3-7tt9thjWujgEmJABpir893tZnxG3br9yAecqUTbnjxrXpvNRdCJm7SgwgJ-tQyE49QvDeXWw_ucXnx1SyQ99eeDafMzgc4JffrQeFCWwryo0q6TzgJ9qAm8SITAy0sc3Q3BJgddCnFH0s1GHjKPBsJBxknb_N3b5zB2yXomVn3bsKV7V-bXiC8Gf9CNj0yZf9xV-Ns\",\"bioRankomKeyIndex\":\"8678\",\"proof\":{\"signature\":\"eyJ4NWMiOlsiTUlJRHRUQ0NBcDJnQXdJQkFnSUlzNkphZWlpZE1va3dEUVlKS29aSWh2Y05BUUVMQlFBd2NERUxNQWtHQTFVRUJoTUNTVTR4Q3pBSkJnTlZCQWdNQWt0Qk1SSXdFQVlEVlFRSERBbENRVTVIUVV4UFVrVXhEVEFMQmdOVkJBb01CRWxKVkVJeEdqQVlCZ05WQkFzTUVVMVBVMGxRTFZSRlEwZ3RRMFZPVkVWU01SVXdFd1lEVlFRRERBeDNkM2N1Ylc5emFYQXVhVzh3SGhjTk1qRXhNREkyTVRRd05EVTRXaGNOTWpReE1ESTFNVFF3TkRVNFdqQitNUXN3Q1FZRFZRUUdFd0pKVGpFTE1Ba0dBMVVFQ0F3Q1MwRXhFakFRQmdOVkJBY01DVUpCVGtkQlRFOVNSVEVOTUFzR0ExVUVDZ3dFU1VsVVFqRWpNQ0VHQTFVRUN3d2FUVTlUU1ZBdFZFVkRTQzFEUlU1VVJWSWdLRXRGVWs1RlRDa3hHakFZQmdOVkJBTU1FWGQzZHk1dGIzTnBjQzVwYnkxVFNVZE9NSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXJYWDhYR2tscGxrOGMyRC8zTGhWelZucTVJVGJ1a21sV2ZORnRnWUNYY1Y3d2IzWUhhaUNudWI0STVkbDZmZGxLUEVUN2t6dTllYno3VTlVVUVXVzc5VWF0NHY0WFV0bEJ1ejJ6VmVjckZpcERtLytNTU5JNEMzSXpwbmVrVlB2NUl0VjBzSzVZcGVRck5HbFh3NFI4TzlTYk9NcUE4NElqQTZsanE2enFZZlpTRzZkSnJSbFVqQS9KczdweVV6Z0t4U0pBYXdmaVFWaThXK05WTnpPdjdUTEdESjhaR3BFT1lnUkdDbjVpU21UVldtVzF5UVNhTEQrT3BlTHhkUHJocE9ZbWtMbkd2alR6aHkxQm5RWVhmVGxyeVd1b2drVzM5NUZBTW9SOEI1ekZiK0FKbHlVVm9zMU5MUEU3cWM5UDIrSDZlbURmUHNhVDNRMjRrWXZCUUlEQVFBQm8wVXdRekFTQmdOVkhSTUJBZjhFQ0RBR0FRSC9BZ0VCTUIwR0ExVWREZ1FXQkJTOHpQL0lTNDlrOGsxblJIb05HMGZNUVQ5elJUQU9CZ05WSFE4QkFmOEVCQU1DQW9Rd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFIWWlqN3p4Sy84dGdrS1czMkhRejRUVFN2d0VqazZmY1dVMWFTbkJkVzFWS2w1TGlrTXZ6OC96M0hUOEpmOW4wdVgwdlFET0M5Mnh4SitNWmU3RTNoWUJ4SFNPRHVyL3FkQ1JGRFFYUnQyMzU4OWpmSXZBQjN6ejJyakFuZ1hzTUxQRldRV2tZdnU0clJFVWFVUHNQRmp0NkhUM3lZcUJrbHJXL0NaNGxDTW9vZ1VNOGRTS1drZ0ZlTHBKbXhIMVhhZGo0R0VCazhaSTZUaVU2cGZOQ0hRMnlLanVEdC85V1kwMzFsTTJmcGh6Mmw2OUJHV2Z5MWZQNEQ3Mjl2NVR0WEM3eGlvb2ZCV3hWL28wNm8xZXRxUTFKUVpmdS9zNWRRQjZ0UEZhbVlwOTBhTmFqYjMwLzI4bkJIdnN0bllhZjIzL0d1cnNYa1dYSzBOM1pJUXhINTA9Il0sIng1dCNTMjU2IjoiYmNaX3RKSm42SXFRVUhNVnNFTlNzS2JMOXlGUGxTTzVqempWdFpJa3AwOCIsImFsZyI6IlJTMjU2In0..muzYpaSxQyjIjOdloDxhMgWj5Ljs8MLPTgZiAtDiiBt_OuaCn3L939uJBREosodYCVlnQu54MDvUoeWPbBJYtJWpm6HceCfgLSzOBnI3gv7bblJ6fcK__HzSEL4gbRaysC3tkO5yx5C0v0JR3vk_pjZV2PFz9y3vpMdxiuaqxWhUpV9qLWM4Y7JVKbzEC2EOnIZAZtkLGxViczK29F9AK10r4Z5djZ4-FYXDXFO0m24a5n5-k7n5UMOL4YL9LslZX8Odd0H8orvAv7P_I3_AmnjKcJDuU7IlQGuD4Em-Id4eFi1pA4vNCr7pUMjb_rNDngnQ43XJbRh6yA2SrU_n_w\"},\"credentialType\":\"auth\",\"protectionKey\":null}}}");
        Map<String, Object> credentialData = new HashMap<>();
        Map<String, String> map = objectMapper.readValue(getCredentialServiceJsonStr(), Map.class);
        credentialData.put("credentialSubject", map);
        Mockito.when(dataShareManager.downloadObject(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean())).thenThrow(new RestServiceException(IdRepoErrorConstants.AUTHENTICATION_FAILED));
        try {
			ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "doProcessCredentialStoreEvent", credentialEventStore);
		} catch (UndeclaredThrowableException e) {
			if (e.getUndeclaredThrowable() instanceof IdAuthenticationBaseException) {
				IdAuthenticationBaseException idAuthenticationBaseException = (IdAuthenticationBaseException) e
						.getUndeclaredThrowable();
				assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						idAuthenticationBaseException.getErrorCode());
			}
		}
    }
    
    @Test
    public void doProcessCredentialStoreEventTest_dataShareDownloadError_datavalidationError() throws IOException, RestServiceException, IdAuthenticationBusinessException {
        CredentialEventStore credentialEventStore = getCredentialEventStore();
        credentialEventStore.setEventObject("{\"publisher\":\"CREDENTIAL_SERVICE\", \"topic\":\"mpartner-default-auth/CREDENTIAL_ISSUED\",\"publishedOn\":\"2021-11-02T09:18:52.418Z\",\"event\":{\"id\":\"437ca068-702f-4b14-a21a-b3994c094d38\",\"transactionId\":\"a84f9af6-025d-4c68-a005-4da9cddb0874\",\"type\":{\"namespace\":\"mosip\",\"name\":\"mosip\"},\"timestamp\":\"2021-11-02T09:18:52.418Z\",\"dataShareUri\":\"http://test\",\"data\":{\"SALT\":\"Q74F5OnTZdw5qiOFp6h6Ww\",\"MODULO\":\"943\",\"expiry_timestamp\":null,\"transaction_limit\":null,\"id_hash\":\"9DCF43F9973826A8331209CAA22A8080995420D992D0BBEE2A3356077EA525E3\",\"TOKEN\":\"362737013453447806883457690320262449\",\"demoEncryptedRandomKey\":\"1v87FAFPnjRzKo8mF6K715e0EXv-rnqF9_aJoN5OUp4GYkjXPjxZHR4MFHob_CKM9Wx0ZOpy0oA8Kcv-0kI1GEDfl070vppDaS0gG30P6QOUy5aEYY4nXffc0nqqrqdC4rzY4LdWbrxxkoyx1Q4BhNZHiA7Tm931-kjdC2YJkDFMHburu72N8CuI6wktAuhQPagWCGL2hGUkdbcFhvD8045Y9oggLfDYNH1Oaj9XIwEEvyAaHvH8mfJxQ5aiLp23mt6PA3QZ4uaVxMvprYBGR8CypGZIBKLfCCfamHsW01ae33mPFyQH1AKKIaJ1XaoeoIgJq9ocUZ292hl3rtgdOuj6eJmcrMOwpiOhHNMY0ndAuH1-RMWdY6CZ2FG-o5VI\",\"demoRankomKeyIndex\":\"4150\",\"bioEncryptedRandomKey\":\"1v87FAFPnjRzKo8mF6K715e0EXv-rnqF9_aJoN5OUp4U5vX_6IU_5l5gi_ATfr1cgICFAViR06A9LrDq8J01i_ICoHaCMqmKMBVk3B4lJ8zAyyYDbM4ztLYIDK95ozZArJuB4i3aJp7lh2XhEqn4xpSzLXJRIjKGuHckq-81IxQmozgo1eE07NlJ3-7tt9thjWujgEmJABpir893tZnxG3br9yAecqUTbnjxrXpvNRdCJm7SgwgJ-tQyE49QvDeXWw_ucXnx1SyQ99eeDafMzgc4JffrQeFCWwryo0q6TzgJ9qAm8SITAy0sc3Q3BJgddCnFH0s1GHjKPBsJBxknb_N3b5zB2yXomVn3bsKV7V-bXiC8Gf9CNj0yZf9xV-Ns\",\"bioRankomKeyIndex\":\"8678\",\"proof\":{\"signature\":\"eyJ4NWMiOlsiTUlJRHRUQ0NBcDJnQXdJQkFnSUlzNkphZWlpZE1va3dEUVlKS29aSWh2Y05BUUVMQlFBd2NERUxNQWtHQTFVRUJoTUNTVTR4Q3pBSkJnTlZCQWdNQWt0Qk1SSXdFQVlEVlFRSERBbENRVTVIUVV4UFVrVXhEVEFMQmdOVkJBb01CRWxKVkVJeEdqQVlCZ05WQkFzTUVVMVBVMGxRTFZSRlEwZ3RRMFZPVkVWU01SVXdFd1lEVlFRRERBeDNkM2N1Ylc5emFYQXVhVzh3SGhjTk1qRXhNREkyTVRRd05EVTRXaGNOTWpReE1ESTFNVFF3TkRVNFdqQitNUXN3Q1FZRFZRUUdFd0pKVGpFTE1Ba0dBMVVFQ0F3Q1MwRXhFakFRQmdOVkJBY01DVUpCVGtkQlRFOVNSVEVOTUFzR0ExVUVDZ3dFU1VsVVFqRWpNQ0VHQTFVRUN3d2FUVTlUU1ZBdFZFVkRTQzFEUlU1VVJWSWdLRXRGVWs1RlRDa3hHakFZQmdOVkJBTU1FWGQzZHk1dGIzTnBjQzVwYnkxVFNVZE9NSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXJYWDhYR2tscGxrOGMyRC8zTGhWelZucTVJVGJ1a21sV2ZORnRnWUNYY1Y3d2IzWUhhaUNudWI0STVkbDZmZGxLUEVUN2t6dTllYno3VTlVVUVXVzc5VWF0NHY0WFV0bEJ1ejJ6VmVjckZpcERtLytNTU5JNEMzSXpwbmVrVlB2NUl0VjBzSzVZcGVRck5HbFh3NFI4TzlTYk9NcUE4NElqQTZsanE2enFZZlpTRzZkSnJSbFVqQS9KczdweVV6Z0t4U0pBYXdmaVFWaThXK05WTnpPdjdUTEdESjhaR3BFT1lnUkdDbjVpU21UVldtVzF5UVNhTEQrT3BlTHhkUHJocE9ZbWtMbkd2alR6aHkxQm5RWVhmVGxyeVd1b2drVzM5NUZBTW9SOEI1ekZiK0FKbHlVVm9zMU5MUEU3cWM5UDIrSDZlbURmUHNhVDNRMjRrWXZCUUlEQVFBQm8wVXdRekFTQmdOVkhSTUJBZjhFQ0RBR0FRSC9BZ0VCTUIwR0ExVWREZ1FXQkJTOHpQL0lTNDlrOGsxblJIb05HMGZNUVQ5elJUQU9CZ05WSFE4QkFmOEVCQU1DQW9Rd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFIWWlqN3p4Sy84dGdrS1czMkhRejRUVFN2d0VqazZmY1dVMWFTbkJkVzFWS2w1TGlrTXZ6OC96M0hUOEpmOW4wdVgwdlFET0M5Mnh4SitNWmU3RTNoWUJ4SFNPRHVyL3FkQ1JGRFFYUnQyMzU4OWpmSXZBQjN6ejJyakFuZ1hzTUxQRldRV2tZdnU0clJFVWFVUHNQRmp0NkhUM3lZcUJrbHJXL0NaNGxDTW9vZ1VNOGRTS1drZ0ZlTHBKbXhIMVhhZGo0R0VCazhaSTZUaVU2cGZOQ0hRMnlLanVEdC85V1kwMzFsTTJmcGh6Mmw2OUJHV2Z5MWZQNEQ3Mjl2NVR0WEM3eGlvb2ZCV3hWL28wNm8xZXRxUTFKUVpmdS9zNWRRQjZ0UEZhbVlwOTBhTmFqYjMwLzI4bkJIdnN0bllhZjIzL0d1cnNYa1dYSzBOM1pJUXhINTA9Il0sIng1dCNTMjU2IjoiYmNaX3RKSm42SXFRVUhNVnNFTlNzS2JMOXlGUGxTTzVqempWdFpJa3AwOCIsImFsZyI6IlJTMjU2In0..muzYpaSxQyjIjOdloDxhMgWj5Ljs8MLPTgZiAtDiiBt_OuaCn3L939uJBREosodYCVlnQu54MDvUoeWPbBJYtJWpm6HceCfgLSzOBnI3gv7bblJ6fcK__HzSEL4gbRaysC3tkO5yx5C0v0JR3vk_pjZV2PFz9y3vpMdxiuaqxWhUpV9qLWM4Y7JVKbzEC2EOnIZAZtkLGxViczK29F9AK10r4Z5djZ4-FYXDXFO0m24a5n5-k7n5UMOL4YL9LslZX8Odd0H8orvAv7P_I3_AmnjKcJDuU7IlQGuD4Em-Id4eFi1pA4vNCr7pUMjb_rNDngnQ43XJbRh6yA2SrU_n_w\"},\"credentialType\":\"auth\",\"protectionKey\":null}}}");
        Map<String, Object> credentialData = new HashMap<>();
        Map<String, String> map = objectMapper.readValue(getCredentialServiceJsonStr(), Map.class);
        credentialData.put("credentialSubject", map);
        Mockito.when(dataShareManager.downloadObject(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean())).thenThrow(new IDDataValidationException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
        try {
			ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "doProcessCredentialStoreEvent", credentialEventStore);
		} catch (UndeclaredThrowableException e) {
			if (e.getUndeclaredThrowable() instanceof IdAuthenticationBaseException) {
				IdAuthenticationBaseException idAuthenticationBaseException = (IdAuthenticationBaseException) e
						.getUndeclaredThrowable();
				assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						idAuthenticationBaseException.getErrorCode());
			}
		}
    }

    /**
     * This class tests the processMissingCredentialRequestId method
     */
    @Test
    public void processMissingCredentialRequestIdTest(){
        List<CredentialRequestIdsDto> dtoList=new ArrayList<CredentialRequestIdsDto>();
        CredentialRequestIdsDto dto= getCredentialRequestIdsDto();
        dtoList.add(dto);
        CredentialEventStore credentialEventStore = getCredentialEventStore();
        //
        //with Status_code = "STORED"
        credentialEventStore.setStatusCode("STORED");
        Optional<CredentialEventStore> eventOpt = Optional.of(credentialEventStore);
        Mockito.when(credentialEventRepo.findTop1ByCredentialTransactionIdOrderByCrDTimesDesc(dto.getRequestId())).thenReturn(eventOpt);
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "processMissingCredentialRequestId", dtoList);
        //
        //with status_code = "FAILED_WITH_MAX_RETRIES"
        credentialEventStore.setStatusCode("FAILED_WITH_MAX_RETRIES");
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "processMissingCredentialRequestId", dto);
        //
        //with status_code = "FAILED_WITH_MAX_RETRIES"
        credentialEventStore.setStatusCode("FAILED_NON_RECOVERABLE");
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "processMissingCredentialRequestId", dto);
        //
        //if eventOpt.isPresent()=false
        Optional<CredentialEventStore> eventOptNotPresent = Optional.empty();
        Mockito.when(credentialEventRepo.findTop1ByCredentialTransactionIdOrderByCrDTimesDesc(dto.getRequestId())).thenReturn(eventOptNotPresent);
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "processMissingCredentialRequestId", dto);
    }

    /**
     * This class tests the storeIdentityEntity method
     */
    @Test
    public void storeIdentityEntityTest(){
        List<IdentityEntity> idEntitites = new ArrayList<IdentityEntity>();
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "storeIdentityEntity", idEntitites);
    }

    @Test(expected = UndeclaredThrowableException.class)
    public void skipIfWaitingForRetryIntervalTest(){
        CredentialEventStore credentialEventStore = new CredentialEventStore();
        LocalDateTime now = LocalDateTime.now().plusYears(1).plusMonths(1).plusWeeks(1).plusDays(1);
        ReflectionTestUtils.setField(credentialEventStore, "updDTimes", now);
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "skipIfWaitingForRetryInterval", credentialEventStore);
    }

    /**
     * This class tests the createIdentityEntity method
     * when IdentityEntity isPresent()=true;
     */
    @Test
    public void createIdentityEntityTest() throws RestServiceException, IdAuthenticationBusinessException, IOException {
        String dataShareUri = "http://datashare-service/v1/datashare/get/mpolicy-default-auth/mpartner-default-auth/mpartner-default-authmpolicy-default-auth20211102091850tkeYCJWZ";
        String idHash = "9DCF43F9973826A8331209CAA22A8080995420D992D0BBEE2A3356077EA525E3";
        String token = "362737013453447806883457690320262449";
        Integer transactionLimit = null;
        String expiryTime = null;
        Map<String, Object> credentialData = new HashMap<>();
        Map<String, String> map = objectMapper.readValue(getCredentialServiceJsonStr(), Map.class);
        credentialData.put("credentialSubject", map);
        System.out.println(credentialData);
        IdentityEntity identityEntity = new IdentityEntity();
        Mockito.when(identityCacheRepo.findById(idHash)).thenReturn(Optional.of(identityEntity));
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "createIdentityEntity", idHash, token, transactionLimit, expiryTime, credentialData);
    }

    @Test
    public void retriggerCredentialIssuanceTest(){
        String requestId=null;
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "retriggerCredentialIssuance", requestId);
    }
    
    @Test(expected = IdAuthRetryException.class)
    public void retriggerCredentialIssuanceTest_exception() throws IDDataValidationException, RestServiceException{
        String requestId="abc";
    	doThrow(new RestServiceException(IdRepoErrorConstants.AUTHENTICATION_FAILED)).when(credentialRequestManager).retriggerCredentialIssuance(Mockito.anyString());
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "retriggerCredentialIssuance", requestId);
    }

    /**
     * This class tests the updateStatusAndRetryCount method
     */
    @Test
    public void updateStatusAndRetryCountTest(){
        CredentialEventStore credentialEventStore = getCredentialEventStore();
        Optional<String> status = Optional.of("Failed");
        OptionalInt retryCount = OptionalInt.of(1);
        ReflectionTestUtils.invokeMethod(credentialStoreServiceImpl, "updateStatusAndRetryCount", credentialEventStore, status, retryCount);
    }

    private CredentialEventStore getCredentialEventStore() {
        CredentialEventStore credentialEventStore = new CredentialEventStore();
        credentialEventStore.setEventId("437ca068-702f-4b14-a21a-b3994c094d38");
        credentialEventStore.setEventTopic("mpartner-default-auth/CREDENTIAL_ISSUED");
        credentialEventStore.setCredentialTransactionId("5b679189-6f3a-46b5-9979-b4ed8d3230c2");
        credentialEventStore.setPublisher("CREDENTIAL_SERVICE");
//        credentialEventStore.setRetryCount(1);
//        credentialEventStore.setStatusCode("FAILED");
        credentialEventStore.setEventObject(getEventModelJsonStr());
        credentialEventStore.setCrDTimes(LocalDateTime.of(2021, 11, 2, 9, 18, 52));
        credentialEventStore.setPublishedOnDtimes(LocalDateTime.of(2021, 11, 2, 9, 18, 52));
        credentialEventStore.setUpdDTimes(LocalDateTime.of(2021, 11, 2, 9 , 18, 54));
        credentialEventStore.setCrBy("IDA");
        credentialEventStore.setUpdBy("IDA");
        ReflectionTestUtils.setField(credentialEventStore, "isDeleted", false);
        credentialEventStore.setDelDTimes(null);
        return credentialEventStore;
    }

    private String getEventModelJsonStr(){
        return "{\"publisher\":\"CREDENTIAL_SERVICE\",\"topic\":\"mpartner-default-auth/CREDENTIAL_ISSUED\",\"publishedOn\":\"2021-11-02T09:18:52.418Z\",\"event\":{\"id\":\"437ca068-702f-4b14-a21a-b3994c094d38\",\"transactionId\":\"a84f9af6-025d-4c68-a005-4da9cddb0874\",\"type\":{\"namespace\":\"mosip\",\"name\":\"mosip\"},\"timestamp\":\"2021-11-02T09:18:52.418Z\",\"dataShareUri\":\"http://datashare-service/v1/datashare/get/mpolicy-default-auth/mpartner-default-auth/mpartner-default-authmpolicy-default-auth20211102091850tkeYCJWZ\",\"data\":{\"SALT\":\"Q74F5OnTZdw5qiOFp6h6Ww\",\"MODULO\":\"943\",\"expiry_timestamp\":null,\"transaction_limit\":null,\"id_hash\":\"9DCF43F9973826A8331209CAA22A8080995420D992D0BBEE2A3356077EA525E3\",\"TOKEN\":\"362737013453447806883457690320262449\",\"demoEncryptedRandomKey\":\"1v87FAFPnjRzKo8mF6K715e0EXv-rnqF9_aJoN5OUp4GYkjXPjxZHR4MFHob_CKM9Wx0ZOpy0oA8Kcv-0kI1GEDfl070vppDaS0gG30P6QOUy5aEYY4nXffc0nqqrqdC4rzY4LdWbrxxkoyx1Q4BhNZHiA7Tm931-kjdC2YJkDFMHburu72N8CuI6wktAuhQPagWCGL2hGUkdbcFhvD8045Y9oggLfDYNH1Oaj9XIwEEvyAaHvH8mfJxQ5aiLp23mt6PA3QZ4uaVxMvprYBGR8CypGZIBKLfCCfamHsW01ae33mPFyQH1AKKIaJ1XaoeoIgJq9ocUZ292hl3rtgdOuj6eJmcrMOwpiOhHNMY0ndAuH1-RMWdY6CZ2FG-o5VI\",\"demoRankomKeyIndex\":\"4150\",\"bioEncryptedRandomKey\":\"1v87FAFPnjRzKo8mF6K715e0EXv-rnqF9_aJoN5OUp4U5vX_6IU_5l5gi_ATfr1cgICFAViR06A9LrDq8J01i_ICoHaCMqmKMBVk3B4lJ8zAyyYDbM4ztLYIDK95ozZArJuB4i3aJp7lh2XhEqn4xpSzLXJRIjKGuHckq-81IxQmozgo1eE07NlJ3-7tt9thjWujgEmJABpir893tZnxG3br9yAecqUTbnjxrXpvNRdCJm7SgwgJ-tQyE49QvDeXWw_ucXnx1SyQ99eeDafMzgc4JffrQeFCWwryo0q6TzgJ9qAm8SITAy0sc3Q3BJgddCnFH0s1GHjKPBsJBxknb_N3b5zB2yXomVn3bsKV7V-bXiC8Gf9CNj0yZf9xV-Ns\",\"bioRankomKeyIndex\":\"8678\",\"proof\":{\"signature\":\"eyJ4NWMiOlsiTUlJRHRUQ0NBcDJnQXdJQkFnSUlzNkphZWlpZE1va3dEUVlKS29aSWh2Y05BUUVMQlFBd2NERUxNQWtHQTFVRUJoTUNTVTR4Q3pBSkJnTlZCQWdNQWt0Qk1SSXdFQVlEVlFRSERBbENRVTVIUVV4UFVrVXhEVEFMQmdOVkJBb01CRWxKVkVJeEdqQVlCZ05WQkFzTUVVMVBVMGxRTFZSRlEwZ3RRMFZPVkVWU01SVXdFd1lEVlFRRERBeDNkM2N1Ylc5emFYQXVhVzh3SGhjTk1qRXhNREkyTVRRd05EVTRXaGNOTWpReE1ESTFNVFF3TkRVNFdqQitNUXN3Q1FZRFZRUUdFd0pKVGpFTE1Ba0dBMVVFQ0F3Q1MwRXhFakFRQmdOVkJBY01DVUpCVGtkQlRFOVNSVEVOTUFzR0ExVUVDZ3dFU1VsVVFqRWpNQ0VHQTFVRUN3d2FUVTlUU1ZBdFZFVkRTQzFEUlU1VVJWSWdLRXRGVWs1RlRDa3hHakFZQmdOVkJBTU1FWGQzZHk1dGIzTnBjQzVwYnkxVFNVZE9NSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXJYWDhYR2tscGxrOGMyRC8zTGhWelZucTVJVGJ1a21sV2ZORnRnWUNYY1Y3d2IzWUhhaUNudWI0STVkbDZmZGxLUEVUN2t6dTllYno3VTlVVUVXVzc5VWF0NHY0WFV0bEJ1ejJ6VmVjckZpcERtLytNTU5JNEMzSXpwbmVrVlB2NUl0VjBzSzVZcGVRck5HbFh3NFI4TzlTYk9NcUE4NElqQTZsanE2enFZZlpTRzZkSnJSbFVqQS9KczdweVV6Z0t4U0pBYXdmaVFWaThXK05WTnpPdjdUTEdESjhaR3BFT1lnUkdDbjVpU21UVldtVzF5UVNhTEQrT3BlTHhkUHJocE9ZbWtMbkd2alR6aHkxQm5RWVhmVGxyeVd1b2drVzM5NUZBTW9SOEI1ekZiK0FKbHlVVm9zMU5MUEU3cWM5UDIrSDZlbURmUHNhVDNRMjRrWXZCUUlEQVFBQm8wVXdRekFTQmdOVkhSTUJBZjhFQ0RBR0FRSC9BZ0VCTUIwR0ExVWREZ1FXQkJTOHpQL0lTNDlrOGsxblJIb05HMGZNUVQ5elJUQU9CZ05WSFE4QkFmOEVCQU1DQW9Rd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFIWWlqN3p4Sy84dGdrS1czMkhRejRUVFN2d0VqazZmY1dVMWFTbkJkVzFWS2w1TGlrTXZ6OC96M0hUOEpmOW4wdVgwdlFET0M5Mnh4SitNWmU3RTNoWUJ4SFNPRHVyL3FkQ1JGRFFYUnQyMzU4OWpmSXZBQjN6ejJyakFuZ1hzTUxQRldRV2tZdnU0clJFVWFVUHNQRmp0NkhUM3lZcUJrbHJXL0NaNGxDTW9vZ1VNOGRTS1drZ0ZlTHBKbXhIMVhhZGo0R0VCazhaSTZUaVU2cGZOQ0hRMnlLanVEdC85V1kwMzFsTTJmcGh6Mmw2OUJHV2Z5MWZQNEQ3Mjl2NVR0WEM3eGlvb2ZCV3hWL28wNm8xZXRxUTFKUVpmdS9zNWRRQjZ0UEZhbVlwOTBhTmFqYjMwLzI4bkJIdnN0bllhZjIzL0d1cnNYa1dYSzBOM1pJUXhINTA9Il0sIng1dCNTMjU2IjoiYmNaX3RKSm42SXFRVUhNVnNFTlNzS2JMOXlGUGxTTzVqempWdFpJa3AwOCIsImFsZyI6IlJTMjU2In0..muzYpaSxQyjIjOdloDxhMgWj5Ljs8MLPTgZiAtDiiBt_OuaCn3L939uJBREosodYCVlnQu54MDvUoeWPbBJYtJWpm6HceCfgLSzOBnI3gv7bblJ6fcK__HzSEL4gbRaysC3tkO5yx5C0v0JR3vk_pjZV2PFz9y3vpMdxiuaqxWhUpV9qLWM4Y7JVKbzEC2EOnIZAZtkLGxViczK29F9AK10r4Z5djZ4-FYXDXFO0m24a5n5-k7n5UMOL4YL9LslZX8Odd0H8orvAv7P_I3_AmnjKcJDuU7IlQGuD4Em-Id4eFi1pA4vNCr7pUMjb_rNDngnQ43XJbRh6yA2SrU_n_w\"},\"credentialType\":\"auth\",\"protectionKey\":null}}}";
    }

    private CredentialRequestIdsDto getCredentialRequestIdsDto(){
        CredentialRequestIdsDto dto = new CredentialRequestIdsDto();
        dto.setRequestId("5b679189-6f3a-46b5-9979-b4ed8d3230c2");
        return dto;
    }

    private String getCredentialServiceJsonStr(){
        return "{\n" +
                "   \"Finger_Left IndexFinger\":\"abc\",\n" +
                "   \"Finger_Right IndexFinger\":\"def\",\n" +
                "   \"Finger_Right MiddleFinger\":\"xyz\",\n" +
                "   \"Iris_Left\":\"aaa\",\n" +
                "   \"Finger_Left Thumb\":\"bbb\",\n" +
                "   \"Face\":\"ccc\",\n" +
                "   \"Finger_Left RingFinger\":\"ddd\",\n" +
                "   \"Finger_Right Thumb\":\"eee\",\n" +
                "   \"Finger_Left MiddleFinger\":\"fff\",\n" +
                "   \"Finger_Right LittleFinger\":\"ggg\",\n" +
                "   \"Iris_Right\":\"hhh\",\n" +
                "   \"Finger_Right RingFinger\":\"iii\",\n" +
                "   \"Finger_Left LittleFinger\":\"jjj\",\n" +
                "   \"gender\":\"Male\",\n" +
                "   \"city\":\"Chennai\",\n" +
                "   \"postalCode\":\"600034\",\n" +
                "   \"fullName\":[\n" +
                "      {\n" +
                "         \"language\":\"eng\",\n" +
                "         \"value\":\"anusha\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"language\":\"ara\",\n" +
                "         \"value\":\"شىعسا\"\n" +
                "      }\n" +
                "   ],\n" +
                "   \"dateOfBirth\":\"1998/01/01\",\n" +
                "   \"province\":\"TN\",\n" +
                "   \"phone\":\"2333456678\",\n" +
                "   \"addressLine1\":\"High Street\",\n" +
                "   \"preferredLang\":[\n" +
                "      \"eng\",\n" +
                "      \"ara\"\n" +
                "   ],\n" +
                "   \"addressLine2\":\"Chennai One\",\n" +
                "   \"id\":\"3864059609201473\",\n" +
                "   \"addressLine3\":\"4th Block\",\n" +
                "   \"region\":\"region\",\n" +
                "   \"email\":\"loganathan.sekar@mindtree.com\",\n" +
                "   \"bloodType\":\"A+\",\n" +
                "   \"residenceStatus\":[\n" +
                "      {\n" +
                "         \"language\":\"eng\",\n" +
                "         \"value\":\"Non-Foreigner\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"language\":\"ara\",\n" +
                "         \"value\":\"غير أجنبي\"\n" +
                "      }\n" +
                "   ],\n" +
                "   \"street\":[\n" +
                "      {\n" +
                "         \"language\":\"eng\",\n" +
                "         \"value\":\"my street\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"language\":\"ara\",\n" +
                "         \"value\":\"غير أجنبي\"\n" +
                "      }\n" +
                "   ]\n" +
                "}";
    }
}


