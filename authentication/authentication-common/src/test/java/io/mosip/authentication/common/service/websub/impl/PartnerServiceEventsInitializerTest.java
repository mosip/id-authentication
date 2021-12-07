package io.mosip.authentication.common.service.websub.impl;

import io.mosip.authentication.common.service.helper.WebSubHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
public class PartnerServiceEventsInitializerTest {

    @InjectMocks
    private PartnerServiceEventsInitializer partnerServiceEventsInitializer;

    @Mock
    private Environment env;

    @Mock
    private WebSubHelper webSubHelper;

    /**
     * This class tests the doRegister method
     */
    @Test
    public void doRegisterTest(){
        partnerServiceEventsInitializer.doRegister();
    }

    /**
     * This class tests the tryRegisterTopicPartnerServiceEvents method
     *                      when Exception is thrown
     */
    @Test
    public void tryRegisterTopicPartnerServiceEventsTest(){
        ReflectionTestUtils.setField(partnerServiceEventsInitializer, "webSubHelper", null);
        ReflectionTestUtils.invokeMethod(partnerServiceEventsInitializer, "tryRegisterTopicPartnerServiceEvents");
    }

    /**
     * This class tests the doSubscribe method
     */
    @Test
    public void doSubscribeTest(){
        ReflectionTestUtils.setField(partnerServiceEventsInitializer, "partnerServiceCallbackURL", "ida-websub-partner-service-callback-url");
        partnerServiceEventsInitializer.doSubscribe();
    }

    /**
     * This class tests the subscribeForPartnerServiceEvents method
     *                      when Exception is thrown
     */
    @Test(expected = Exception.class)
    public void subscribeForPartnerServiceEventsTest(){
        ReflectionTestUtils.invokeMethod(partnerServiceEventsInitializer, "subscribeForPartnerServiceEvents");
    }

}
