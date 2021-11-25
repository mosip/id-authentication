package io.mosip.authentication.common.service.helper;

import io.mosip.authentication.common.service.websub.WebSubEventSubcriber;
import io.mosip.authentication.common.service.websub.WebSubEventTopicRegistrar;
import io.mosip.authentication.common.service.websub.dto.EventInterface;
import io.mosip.kernel.core.websub.spi.PublisherClient;
import io.mosip.kernel.core.websub.spi.SubscriptionClient;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;
import io.mosip.kernel.websub.api.model.SubscriptionChangeResponse;
import io.mosip.kernel.websub.api.model.UnsubscriptionRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.function.Supplier;

@RunWith(SpringRunner.class)
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

    @Test(expected = Exception.class)
    public void initRegistrarExceptionTest(){
        webSubHelper.initRegistrar(null);
    }

    @Test(expected = Exception.class)
    public void initSubscriberExceptionTest(){
        webSubHelper.initSubscriber(null);
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

