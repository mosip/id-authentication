package io.mosip.authentication.common.service.helper;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import io.mosip.authentication.common.service.websub.WebSubEventSubcriber;
import io.mosip.authentication.common.service.websub.WebSubEventTopicRegistrar;
import io.mosip.authentication.common.service.websub.dto.EventInterface;
import io.mosip.kernel.core.websub.spi.PublisherClient;
import io.mosip.kernel.core.websub.spi.SubscriptionClient;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;
import io.mosip.kernel.websub.api.model.SubscriptionChangeResponse;
import io.mosip.kernel.websub.api.model.UnsubscriptionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.function.Supplier;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
public class WebSubHelperTest {

    @InjectMocks
    private WebSubHelper webSubHelper;

    @Mock
    private WebSubEventSubcriber subscriber;

    @Mock
    WebSubEventTopicRegistrar registrar;

    @Mock
    private SubscriptionClient<SubscriptionChangeRequest, UnsubscriptionRequest, SubscriptionChangeResponse> subscriptionClient;

    @Mock
    private EventInterface eventInterface;

    @Mock
    private PublisherClient<String, Object, HttpHeaders> publisher;

    /**
     * This class tests the initSubscriber method
     */
    @Test
    public void initSubscriberTest(){
        webSubHelper.initSubscriber(subscriber);
        Supplier<Boolean> enableTester = null;
        webSubHelper.initSubscriber(subscriber, null);
        Mockito.doThrow(ResourceAccessException.class).when(subscriber).subscribe(enableTester);
        webSubHelper.initSubscriber(subscriber, enableTester);
    }

    /**
     * This class tests the initRegistrar method
     */
    @Test
    public void initRegistrarTest(){
        webSubHelper.initRegistrar(registrar);
        Supplier<Boolean> enableTester=null;
        webSubHelper.initRegistrar(registrar, enableTester);
        Mockito.doThrow(ResourceAccessException.class).when(registrar).register(enableTester);
        webSubHelper.initRegistrar(registrar);
    }

    @Test
    public void initRegistrarExceptionTest(){
		assertThrows(Exception.class, () -> {
        webSubHelper.initRegistrar(null);
    		});
	}

    @Test
    public void initSubscriberExceptionTest(){
		assertThrows(Exception.class, () -> {
        webSubHelper.initSubscriber(null);
    		});
	}

    /**
     * This class tests the publishEvent method
     */
    @Test
    public <U> void publishEventTest(){
        String eventTopic="eventTopic";
        U eventModel = null;
        webSubHelper.publishEvent(eventTopic, eventModel);
    }

    /**
     * This class tests the createEventModel method
     */
    @Test
    public<T extends EventInterface, S> void createEventModelTest() {
        String topic="topic";
        webSubHelper.createEventModel(topic);
        webSubHelper.createEventModel(topic, (T) eventInterface);
    }

    /**
     * This class tests the registerTopic method
     */
    @Test
    public void registerTopicTest(){
        String eventTopic = "eventTopic";
        webSubHelper.registerTopic(eventTopic);
    }

    /**
     * This class tests the subscribe method
     */
    @Test
    public void subscribeTest(){
        SubscriptionChangeRequest subscriptionRequest= new SubscriptionChangeRequest();
        SubscriptionChangeResponse subscriptionResponse=new SubscriptionChangeResponse();
        Mockito.when(subscriptionClient.subscribe(subscriptionRequest)).thenReturn(subscriptionResponse);
        webSubHelper.subscribe(subscriptionRequest);
    }
}

