package io.mosip.authentication.internal.service.listener;

import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.websub.impl.IdAuthFraudAnalysisEventPublisher;
import org.apache.commons.lang3.builder.ReflectionDiffBuilder;
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

@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
public class InternalAuthWebSubInitializerTest {

    @InjectMocks
    private InternalAuthWebSubInitializer internalAuthWebSubInitializer;

    @Mock
    protected WebSubHelper webSubHelper;

    @Mock
    private IdAuthFraudAnalysisEventPublisher fraudEventPublisher;

    /**
     * This class tests the doInitSubscriptions method
     */
    @Test
    public void doInitSubscriptionsTest(){
        internalAuthWebSubInitializer.doInitSubscriptions();
    }

    /**
     * This class tests the doRegisterTopics method
     */
    @Test
    public void doRegisterTopicsTest(){
        internalAuthWebSubInitializer.doRegisterTopics();
//        when fraudEventPublisher is null
        ReflectionTestUtils.setField(internalAuthWebSubInitializer, "fraudEventPublisher", null);
        internalAuthWebSubInitializer.doRegisterTopics();
    }
}
