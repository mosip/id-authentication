package io.mosip.authentication.common.service.kafka;

import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.mosip.authentication.common.service.kafka.impl.AuthenticationErrorEventingPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import io.mosip.authentication.common.service.entity.PartnerData;
import io.mosip.authentication.common.service.repository.PartnerDataRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.BaseRequestDTO;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.kernel.core.websub.model.EventModel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AuthenticationErrorEventingPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private IdAuthSecurityManager securityManager;

    @Mock
    private PartnerDataRepository partnerDataRepo;

    private AuthenticationErrorEventingPublisher publisher;

    private BaseRequestDTO requestDTO;
    private PartnerData partnerData;
    private PartnerDTO partnerDTO;
    private AutoCloseable mocks;

    @Before
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);
        publisher = new AuthenticationErrorEventingPublisher();

        ReflectionTestUtils.setField(publisher, "kafkaTemplate", kafkaTemplate);
        ReflectionTestUtils.setField(publisher, "securityManager", securityManager);
        ReflectionTestUtils.setField(publisher, "partnerDataRepo", partnerDataRepo);

        ReflectionTestUtils.setField(publisher, "authenticationErrorEventingTopic", "auth-error-topic");
        ReflectionTestUtils.setField(publisher, "partnerId", "partner-123");

        requestDTO = new BaseRequestDTO();
        requestDTO.setIndividualId("123456");
        requestDTO.setIndividualIdType("UIN");

        partnerDTO = new PartnerDTO();
        partnerDTO.setPartnerId("PID001");
        partnerDTO.setPartnerName("TestPartner");

        partnerData = new PartnerData();
        partnerData.setCertificateData("CERTDATA");

        when(securityManager.getUser()).thenReturn("test-user");
    }

    @After  
    public void tearDown() throws Exception {  
    mocks.close();  
    }

    @Test
    public void testNotifyWhenPartnerDataExists() throws Exception {
        when(partnerDataRepo.findByPartnerId("partner-123"))
                .thenReturn(Optional.of(partnerData));

        when(securityManager.asymmetricEncryption(any(), any()))
                .thenReturn("ENCRYPTED-ID");

        IdAuthenticationBusinessException ex =
                new IdAuthenticationBusinessException("ERR-001", "Sample Error");

        Map<String, Object> metadata = new HashMap<>();

        publisher.notify(requestDTO, "SIGNATURE", Optional.of(partnerDTO), ex, metadata);

        ArgumentCaptor<EventModel> captor = ArgumentCaptor.forClass(EventModel.class);
        verify(kafkaTemplate, times(1)).send(eq("auth-error-topic"), captor.capture());

        EventModel eventSent = captor.getValue();
        assertEquals("IDA", eventSent.getPublisher());  
        assertEquals("ERR-001", eventSent.getEvent().getData().get("error_Code"));  
        assertEquals("ENCRYPTED-ID", eventSent.getEvent().getData().get("individualId")); 
    }

    @Test
    public void testNotifyWhenPartnerDataMissingNoKafkaSent() {
        when(partnerDataRepo.findByPartnerId("partner-123")).thenReturn(Optional.empty());

        IdAuthenticationBusinessException ex =
                new IdAuthenticationBusinessException("ERR-002", "Error2");

        publisher.notify(requestDTO, "SIGN", Optional.of(partnerDTO), ex, new HashMap<>());

        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    public void testEncryptIndividualIdThrowsException() throws Exception {
        when(partnerDataRepo.findByPartnerId("partner-123"))
                .thenReturn(Optional.of(partnerData));

        when(securityManager.asymmetricEncryption(any(), any()))
                .thenThrow(new IdAuthenticationBusinessException("ECODE", "Failed"));

        IdAuthenticationBusinessException ex =
                new IdAuthenticationBusinessException("ERR-003", "Error3");

        publisher.notify(requestDTO, "HEAD", Optional.of(partnerDTO), ex, new HashMap<>());

        ArgumentCaptor<EventModel> captor = ArgumentCaptor.forClass(EventModel.class);

        verify(kafkaTemplate, times(1)).send(eq("auth-error-topic"), captor.capture());
        EventModel model = captor.getValue();

        assertNull(model.getEvent().getData().get("individualId"));
    }
}

