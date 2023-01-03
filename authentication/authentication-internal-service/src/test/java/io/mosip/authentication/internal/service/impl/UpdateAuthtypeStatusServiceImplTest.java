package io.mosip.authentication.internal.service.impl;

import io.mosip.authentication.common.service.repository.AuthLockRepository;
import io.mosip.authentication.common.service.websub.impl.AuthTypeStatusEventPublisher;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.dto.AuthtypeStatus;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebMvcTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestContext. class, WebApplicationContext.class})
public class UpdateAuthtypeStatusServiceImplTest {

    @InjectMocks
    private UpdateAuthtypeStatusServiceImpl updateAuthtypeStatusService;

    @Mock
    private AuthLockRepository authLockRepository;

    @Mock
    private AuthTypeStatusEventPublisher authTypeStatusEventPublisherManager;

    /**
     * This class tests the putAuthTypeStatus method
     */
    @Test
    public void putAuthTypeStatusTest(){
        AuthtypeStatus authtypeStatus = new AuthtypeStatus();
        authtypeStatus.setAuthType("LOCKED");
        authtypeStatus.setAuthSubType("LOCKED");
        authtypeStatus.setLocked(true);
        String token = "11221122";
        ReflectionTestUtils.invokeMethod(updateAuthtypeStatusService, "putAuthTypeStatus", authtypeStatus, token);

        authtypeStatus.setAuthType("BIO");
        ReflectionTestUtils.invokeMethod(updateAuthtypeStatusService, "putAuthTypeStatus", authtypeStatus, token);

        Map<String, Object> metadata = new HashMap<>();
        authtypeStatus.setMetadata(metadata);
        ReflectionTestUtils.invokeMethod(updateAuthtypeStatusService, "putAuthTypeStatus", authtypeStatus, token);

        metadata.put("unlockExpiryTimestamp", "2018-12-30T19:34:50.63");
        authtypeStatus.setMetadata(metadata);
        ReflectionTestUtils.invokeMethod(updateAuthtypeStatusService, "putAuthTypeStatus", authtypeStatus, token);

        authtypeStatus.setAuthType("DEMO");
        ReflectionTestUtils.invokeMethod(updateAuthtypeStatusService, "putAuthTypeStatus", authtypeStatus, token);
    }

    /**
     * This class tests the updateAuthTypeStatus method
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test
    public void updateAuthTypeStatusTest() throws IdAuthenticationBusinessException {
        String tokenId = "11221122";
        List<AuthtypeStatus> authtypeStatusList = new ArrayList<>();
        AuthtypeStatus authtypeStatus1 = new AuthtypeStatus();
        authtypeStatus1.setAuthType("LOCKED");
        authtypeStatus1.setAuthSubType("LOCKED");
        authtypeStatus1.setLocked(true);
        authtypeStatusList.add(authtypeStatus1);
        updateAuthtypeStatusService.updateAuthTypeStatus(tokenId, authtypeStatusList);
    }
}
