package io.mosip.authentication.internal.service.controller;

import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.kernel.core.websub.model.EventModel;
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
public class PartnerServiceCallbackControllerTest {

    @Mock
    private PartnerServiceManager partnerManager;

    @Mock
    private IdAuthSecurityManager securityManager;

    @Mock
    private EventModel eventModel;
    
    @InjectMocks
    private PartnerServiceCallbackController partnerServiceCallbackController;


    /**
     * This class tests the handleApiKeyApprovedEvent method
     *                  and handlePartnerUpdated method
     *                  and handlePolicyUpdated method
     *                  and handlePartnerApiKeyUpdated method
     *                  and handleMispLicenseGeneratedEvent method
     *                  and handleMispUpdatedEvent
     */
    @Test
    public void handleEventTest(){
        partnerServiceCallbackController.handleApiKeyApprovedEvent(eventModel);
        partnerServiceCallbackController.handlePartnerUpdated(eventModel);
        partnerServiceCallbackController.handlePolicyUpdated(eventModel);
        partnerServiceCallbackController.handlePartnerApiKeyUpdated(eventModel);
        partnerServiceCallbackController.handleMispLicenseGeneratedEvent(eventModel);
        partnerServiceCallbackController.handleMispUpdatedEvent(eventModel);
    }

    /**
     * This class tests the handleApiKeyApprovedEvent method
     *                  and handlePartnerUpdated method
     *                  and handlePolicyUpdated method
     *                  and handlePartnerApiKeyUpdated method
     *                  and handleMispLicenseGeneratedEvent method
     *                  and handleMispUpdatedEvent
     *                  when Exception is thrown
     */
    @Test
    public void handleEventExceptionTest(){
        ReflectionTestUtils.setField(partnerServiceCallbackController, "partnerManager", null);
        partnerServiceCallbackController.handleApiKeyApprovedEvent(eventModel);
        partnerServiceCallbackController.handlePartnerUpdated(eventModel);
        partnerServiceCallbackController.handlePolicyUpdated(eventModel);
        partnerServiceCallbackController.handlePartnerApiKeyUpdated(eventModel);
        partnerServiceCallbackController.handleMispLicenseGeneratedEvent(eventModel);
        partnerServiceCallbackController.handleMispUpdatedEvent(eventModel);
    }
}
