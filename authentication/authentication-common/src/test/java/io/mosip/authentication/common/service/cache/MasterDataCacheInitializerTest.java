package io.mosip.authentication.common.service.cache;

import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
public class MasterDataCacheInitializerTest {

    @InjectMocks
    private MasterDataCacheInitializer masterDataCacheInitializer;

    @Mock
    private MasterDataCache masterDataCache;

    /**
     * This class tests the loadMasterData method
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test
    public void loadMasterDataTest() throws IdAuthenticationBusinessException {
        masterDataCacheInitializer.loadMasterData();
    }

    /**
     * This class tests the onApplicationEvent method
     */
    @Test
    public void onApplicationEventTest(){
        SpringApplication application = new SpringApplication();
        ApplicationReadyEvent event = new ApplicationReadyEvent(application, new String[0], null);
        masterDataCacheInitializer.onApplicationEvent(event);
    }

    /**
     * This class tests the OnApplicationReadyEvent method
     *                         in case  IdAuthenticationBusinessException
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test(expected = IdAuthUncheckedException.class)
    public void OnApplicationReadyEventExceptionTest() throws IdAuthenticationBusinessException {
        SpringApplication application = new SpringApplication();
        ApplicationReadyEvent event = new ApplicationReadyEvent(application, new String[0], null);
        Mockito.doThrow(IdAuthenticationBusinessException.class).when(masterDataCache).getMasterDataTitles();
        masterDataCacheInitializer.onApplicationEvent(event);
    }
}
