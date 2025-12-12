package io.mosip.authentication.common.service.websub.impl;

import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.impl.idevent.RemoveIdStatusEvent;
import io.mosip.authentication.common.service.websub.dto.EventModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;  
import static org.junit.jupiter.api.Assertions.assertNotNull; 

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RemoveIdStatusEventPublisherTest {

    @InjectMocks
    private RemoveIdStatusEventPublisher publisher;

    @Mock
    private WebSubHelper webSubHelper;

    private final String removeTopic = "mock-remove-topic";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Inject the topic value since @Value does not work in unit tests
        ReflectionTestUtils.setField(publisher, "removeIdStatusTopic", "mock-remove-topic");
    }

    // -------------------------------
    //  TEST: doRegister → tryRegisterTopic() success
    // -------------------------------
    @Test
    void testDoRegister_success() {
        doNothing().when(webSubHelper).registerTopic("mock-remove-topic");

        publisher.doRegister();

        verify(webSubHelper, times(1)).registerTopic("mock-remove-topic");
    }

    // -------------------------------
    //  TEST: doRegister → tryRegisterTopic() exception path
    // -------------------------------
    @Test
    void testDoRegister_exception() {
        doThrow(new RuntimeException("ERROR"))
                .when(webSubHelper).registerTopic("mock-remove-topic");

        publisher.doRegister();

        verify(webSubHelper, times(1)).registerTopic("mock-remove-topic");
        // no exception should propagate
    }

    // -------------------------------
    //  TEST: publishRemoveIdStatusEvent()
    // -------------------------------
    @Test
    public void testPublishRemoveIdStatusEvent() {

        // Fake event model returned by WebSubHelper.createEventModel()
        EventModel<RemoveIdStatusEvent> fakeEventModel = new EventModel<>();

        // ⭐ createEventModel returns EventModel<T> → use when().thenReturn()
        when(webSubHelper.createEventModel(anyString(), any(RemoveIdStatusEvent.class)))
                .thenReturn(fakeEventModel);

        // ⭐ publishEvent is void → use doNothing()
        doNothing().when(webSubHelper)
                .publishEvent(eq(removeTopic), eq(fakeEventModel));

        // Execute
        publisher.publishRemoveIdStatusEvent("HASH123");

        // Verify event model creation
        verify(webSubHelper, times(1))
                .createEventModel(eq(removeTopic), any(RemoveIdStatusEvent.class));

        // Verify publish
        verify(webSubHelper, times(1))
                .publishEvent(eq(removeTopic), eq(fakeEventModel));
    }

    // -------------------------------
    //  TEST: Private method createRemoveIdStatusEvent (via reflection)
    // -------------------------------
    @Test
    void testCreateRemoveIdStatusEvent() throws Exception {

        RemoveIdStatusEvent event =
                (RemoveIdStatusEvent) ReflectionTestUtils.invokeMethod(
                        publisher, "createRemoveIdStatusEvent", "ABC123");

        assertEquals("ABC123", event.getData().get("id_hash"));
        assertNotNull(event.getTimestamp());
    }
}
