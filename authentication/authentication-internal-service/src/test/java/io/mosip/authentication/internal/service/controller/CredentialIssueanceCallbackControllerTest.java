package io.mosip.authentication.internal.service.controller;


import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.idevent.service.IdChangeEventHandlerService;
import io.mosip.kernel.core.http.ResponseWrapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.kernel.core.websub.model.EventModel;


@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
public class CredentialIssueanceCallbackControllerTest {

    @InjectMocks
    private CredentialIssueanceCallbackController credentialIssueanceCallbackController;

    @Mock
    private IdChangeEventHandlerService credentialStoreService;

    /**
     * This class tests the initBinder method
     */
    @Test
    public void initBinderTest(){
        Object test = new Object();
        WebDataBinder binder = new WebDataBinder(test);
        credentialIssueanceCallbackController.initBinder(binder);
    }

    /**
     * This class tests the handleCredentialIssuedEvent method
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     */
    @Test
    public void handleCredentialIssuedEventTest() throws IdAuthenticationBusinessException {
        String partnerId = "partnerId";
        EventModel eventModel = new EventModel();
        Errors error = new BindException(eventModel, "eventModel");
        ResponseWrapper  rWrapper= new ResponseWrapper<>();
        rWrapper = credentialIssueanceCallbackController.handleCredentialIssuedEvent(partnerId, eventModel, error);
        Assert.assertEquals(null, rWrapper.getId());
    }

    /**
     * This class tests the handleRemoveIdEvent method
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     */
    @Test
    public void handleRemoveIdEventTest() throws IdAuthenticationBusinessException {
        String partnerId = "partnerId";
        EventModel eventModel = new EventModel();
        Errors errors = new BindException(eventModel, "eventModel");
        ResponseWrapper  rWrapper= new ResponseWrapper<>();
        rWrapper = credentialIssueanceCallbackController.handleRemoveIdEvent(partnerId, eventModel, errors);
        Assert.assertEquals(null, rWrapper.getId());
    }

    /**
     * This class tests the handleDeactivateIdEvent method
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     */
    @Test
    public void handleDeactivateIdEventTest() throws IdAuthenticationBusinessException {
        String partnerId = "partnerId";
        EventModel eventModel = new EventModel();
        Errors errors = new BindException(eventModel, "eventModel");
        ResponseWrapper  rWrapper= new ResponseWrapper<>();
        rWrapper = credentialIssueanceCallbackController.handleDeactivateIdEvent(partnerId, eventModel, errors);
        Assert.assertEquals(null, rWrapper.getId());
    }

    /**
     * This class tests the handleActivateIdEvent method
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     */
    @Test
    public void handleActivateIdEventTest() throws IdAuthenticationBusinessException {
        String partnerId = "partnerId";
        EventModel eventModel = new EventModel();
        Errors errors = new BindException(eventModel, "eventModel");
        ResponseWrapper  rWrapper= new ResponseWrapper<>();
        rWrapper = credentialIssueanceCallbackController.handleActivateIdEvent(partnerId, eventModel, errors);
        Assert.assertEquals(null, rWrapper.getId());
    }
}
