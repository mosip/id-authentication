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
    private WebSubHelper webSubHelperMock;

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

    @Test
    public void initSubscriberTest(){
        webSubHelper.initSubscriber(subscriber);
        Supplier<Boolean> enableTester = null;
        webSubHelper.initSubscriber(subscriber, null);
        Mockito.doThrow(ResourceAccessException.class).when(subscriber).subscribe(enableTester);
        webSubHelper.initSubscriber(subscriber, enableTester);
    }

    @Test
    public void initRegistrarTest(){
        webSubHelper.initRegistrar(registrar);
        Supplier<Boolean> enableTester=null;
        webSubHelper.initRegistrar(registrar, enableTester);
    }

    @Test(expected = Exception.class)
    public void initRegistrarExceptionTest(){
        webSubHelper.initRegistrar(null);
    }

    @Test(expected = Exception.class)
    public void initSubscriberExceptionTest(){
        webSubHelper.initSubscriber(null);
    }

    @Test
    public <U> void publishEventTest(){
        String eventTopic="eventTopic";
        U eventModel = null;
        webSubHelper.publishEvent(eventTopic, eventModel);
    }


    @Test
    public  void createEventModelTest() {
        String topic="topic";
        ReflectionTestUtils.invokeMethod(webSubHelper, "createEventModel", topic);
    }

    @Test
    public void registerTopicTest(){
        String eventTopic = "eventTopic";
        webSubHelper.registerTopic(eventTopic);
    }

    @Test
    public void subscribeTest(){
        SubscriptionChangeRequest subscriptionRequest= new SubscriptionChangeRequest();
        SubscriptionChangeResponse subscriptionResponse=new SubscriptionChangeResponse();
        Mockito.when(subscriptionClient.subscribe(subscriptionRequest)).thenReturn(subscriptionResponse);
        webSubHelper.subscribe(subscriptionRequest);
    }

    @Test
    public<T extends EventInterface, S> void createEventModelTest1() throws IOException {
        String topic="topic";
        webSubHelper.createEventModel(topic, (T) eventInterface);
    }


    private String getEventJsonStr(){
        return "{\"id\":\"7aec032d-49c3-4565-af75-2cba91ec6c6d\",\"transactionId\":\"a49305fe-6152-4673-9318-95f03f8fe18b\",\"type\":{\"namespace\":\"mosip\",\"name\":\"mosip\"},\"timestamp\":\"2021-11-02T09:18:52.792Z\",\"dataShareUri\":\"http://datashare-service/v1/datashare/get/mpolicy-default-auth/mpartner-default-auth/mpartner-default-authmpolicy-default-auth20211102091851WmF7WFX3\",\"data\":{\"SALT\":\"Q74F5OnTZdw5qiOFp6h6Ww\",\"MODULO\":\"943\",\"expiry_timestamp\":null,\"transaction_limit\":null,\"id_hash\":\"9DCF43F9973826A8331209CAA22A8080995420D992D0BBEE2A3356077EA525E3\",\"TOKEN\":\"362737013453447806883457690320262449\",\"demoEncryptedRandomKey\":\"1v87FAFPnjRzKo8mF6K715e0EXv-rnqF9_aJoN5OUp5aa_ERg2GMJ-WRtVjv1ByIW2XYnBN-T2J1mWiRqEV3eqGoXwVNK097sJi9hpuk6xBijnZh9baEpmFqNWyogwA7h3s6B8KwXGQP5bvMn-rEGN5DNs_sJaBNvT2MM9wicnR9nwO_u1_OF6rWneQcJX0fx45D3hmWTvyKmxQz__tHJ9kC1OfjnChr-GxOlPguXHXmjiLhDgA9t1xJjCyVOO3QrwLiC-1cHgRQvlLGyRf1H3ZieMJNeLXm8SH8xuBvLA_V-00aouWtdOB0t81mD9YerQwrJ1D5PByPD0eggJHNnPEOERae9CnN4xO9zr8K2SiGJ85G3MWPvhRS1ScXvFrU\",\"demoRankomKeyIndex\":\"539\",\"bioEncryptedRandomKey\":\"1v87FAFPnjRzKo8mF6K715e0EXv-rnqF9_aJoN5OUp46CXrBkduJ3iyRSoNeFkkx7B5bH_yrFz15-iTb6vDYAmi8n5NaLBjTT3cgYsxFi3AGUr6kXcYGSHfuEFXsVM3qdOcHr5kfmSNJ361VLCAvMzSRsaNOeljUAfieNyCj_tn6CsVi55VrT6i01nJArQtN2OgRimHonXxGI-cSprpjYh1atIaKQHjAsVrwHsRPg6Kv6piEJfOtVq1kPj9LusBKAZFP3b4BdtrOuCKN_ZYqsOJq_Yj3lh-9-MIRcgx8dWJxt7ZaP6q4_zfLKRh5J1LWGl5a8mDKYbhLR83WHMR3Z9G-hSjreqRm2HSBDevEKgl5QMWRwQlyqkCZn8tasfRy\",\"bioRankomKeyIndex\":\"8835\",\"proof\":{\"signature\":\"eyJ4NWMiOlsiTUlJRHRUQ0NBcDJnQXdJQkFnSUlzNkphZWlpZE1va3dEUVlKS29aSWh2Y05BUUVMQlFBd2NERUxNQWtHQTFVRUJoTUNTVTR4Q3pBSkJnTlZCQWdNQWt0Qk1SSXdFQVlEVlFRSERBbENRVTVIUVV4UFVrVXhEVEFMQmdOVkJBb01CRWxKVkVJeEdqQVlCZ05WQkFzTUVVMVBVMGxRTFZSRlEwZ3RRMFZPVkVWU01SVXdFd1lEVlFRRERBeDNkM2N1Ylc5emFYQXVhVzh3SGhjTk1qRXhNREkyTVRRd05EVTRXaGNOTWpReE1ESTFNVFF3TkRVNFdqQitNUXN3Q1FZRFZRUUdFd0pKVGpFTE1Ba0dBMVVFQ0F3Q1MwRXhFakFRQmdOVkJBY01DVUpCVGtkQlRFOVNSVEVOTUFzR0ExVUVDZ3dFU1VsVVFqRWpNQ0VHQTFVRUN3d2FUVTlUU1ZBdFZFVkRTQzFEUlU1VVJWSWdLRXRGVWs1RlRDa3hHakFZQmdOVkJBTU1FWGQzZHk1dGIzTnBjQzVwYnkxVFNVZE9NSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXJYWDhYR2tscGxrOGMyRC8zTGhWelZucTVJVGJ1a21sV2ZORnRnWUNYY1Y3d2IzWUhhaUNudWI0STVkbDZmZGxLUEVUN2t6dTllYno3VTlVVUVXVzc5VWF0NHY0WFV0bEJ1ejJ6VmVjckZpcERtLytNTU5JNEMzSXpwbmVrVlB2NUl0VjBzSzVZcGVRck5HbFh3NFI4TzlTYk9NcUE4NElqQTZsanE2enFZZlpTRzZkSnJSbFVqQS9KczdweVV6Z0t4U0pBYXdmaVFWaThXK05WTnpPdjdUTEdESjhaR3BFT1lnUkdDbjVpU21UVldtVzF5UVNhTEQrT3BlTHhkUHJocE9ZbWtMbkd2alR6aHkxQm5RWVhmVGxyeVd1b2drVzM5NUZBTW9SOEI1ekZiK0FKbHlVVm9zMU5MUEU3cWM5UDIrSDZlbURmUHNhVDNRMjRrWXZCUUlEQVFBQm8wVXdRekFTQmdOVkhSTUJBZjhFQ0RBR0FRSC9BZ0VCTUIwR0ExVWREZ1FXQkJTOHpQL0lTNDlrOGsxblJIb05HMGZNUVQ5elJUQU9CZ05WSFE4QkFmOEVCQU1DQW9Rd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFIWWlqN3p4Sy84dGdrS1czMkhRejRUVFN2d0VqazZmY1dVMWFTbkJkVzFWS2w1TGlrTXZ6OC96M0hUOEpmOW4wdVgwdlFET0M5Mnh4SitNWmU3RTNoWUJ4SFNPRHVyL3FkQ1JGRFFYUnQyMzU4OWpmSXZBQjN6ejJyakFuZ1hzTUxQRldRV2tZdnU0clJFVWFVUHNQRmp0NkhUM3lZcUJrbHJXL0NaNGxDTW9vZ1VNOGRTS1drZ0ZlTHBKbXhIMVhhZGo0R0VCazhaSTZUaVU2cGZOQ0hRMnlLanVEdC85V1kwMzFsTTJmcGh6Mmw2OUJHV2Z5MWZQNEQ3Mjl2NVR0WEM3eGlvb2ZCV3hWL28wNm8xZXRxUTFKUVpmdS9zNWRRQjZ0UEZhbVlwOTBhTmFqYjMwLzI4bkJIdnN0bllhZjIzL0d1cnNYa1dYSzBOM1pJUXhINTA9Il0sIng1dCNTMjU2IjoiYmNaX3RKSm42SXFRVUhNVnNFTlNzS2JMOXlGUGxTTzVqempWdFpJa3AwOCIsImFsZyI6IlJTMjU2In0..QMzAt10HnVzLqX_TKVuGcgFEwgi_6ZOTgfjpQ9czVzZa14copsC3rVcpS9OpDgPKZVhdAxkNZX8bwjrCVPIM4urd6poS7TuGQlPKHRfnXEdcVreD7UNHDnrxkZcWQrXIhPUh2fXkghnZkH-yUYWnla8oM2T-vuNY-4RMAnOJWZAcQm4ufttxz9h_zk-WwwcreIavITYfl8VhiiSHNLKjLqjnjgBFN8m7DV6GdjdDaY3NbVeXlu9EzSivBUvHx217vsCh-Jnn0V1bnyg5gquclwKgqId1gU-PDfcOwELGr3K_Aa-tcNakKBE66wlj1o4R01cvEcX0nSkkJs83F-UK9A\"},\"credentialType\":\"auth\",\"protectionKey\":null}}";
    }
}

