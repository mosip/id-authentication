package io.mosip.authentication.common.service.websub.impl;

import io.mosip.authentication.common.service.helper.WebSubHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
public class IdChangeEventsInitializerTest {

    @InjectMocks
    private IdChangeEventsInitializer idChangeEventsInitializer;

    @Mock
    private WebSubHelper webSubHelper;

    @Before
    public void Before(){
        ReflectionTestUtils.setField(idChangeEventsInitializer, "credentialIssueCallbackURL", "${ida-websub-credential-issue-callback-url}");
        ReflectionTestUtils.setField(idChangeEventsInitializer, "authPartherId" ,"${ida-auth-partner-id}");
    }

    /**
     * This class tests the doRegister method
     *                  and tryRegisterTopicCredentialIssueanceEvents method
     */
    @Test
    public void doRegisterTest(){
        String topPrefix = "topPrefix";
        ReflectionTestUtils.invokeMethod(idChangeEventsInitializer, "tryRegisterTopicCredentialIssueanceEvents", topPrefix);

        ReflectionTestUtils.setField(idChangeEventsInitializer, "webSubHelper", null);
        idChangeEventsInitializer.doRegister();
    }

    /**
     * This class tests the subscribeForCredentialIssueanceEvents method
     */
    @Test
    public void subscribeForCredentialIssueanceEventsTest(){
        String topPrefix = "topPrefix";
        ReflectionTestUtils.invokeMethod(idChangeEventsInitializer, "subscribeForCredentialIssueanceEvents", topPrefix);
    }

    /**
     * This class tests the doSubscribe method
     *                      when Exception is thrown
     */
    @Test(expected = Exception.class)
    public void doSubscribeExceptionTest(){
        String topPrefix = "topPrefix";
        ReflectionTestUtils.setField(idChangeEventsInitializer, "webSubHelper", null);
        idChangeEventsInitializer.doSubscribe();
    }
}
