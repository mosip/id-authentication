package io.mosip.authentication.common.service.cache;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.helper.RestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
public class MasterDataCacheTest {

    @InjectMocks
    private MasterDataCache masterDataCache;

    @Mock
    private RestHelper restHelper;

    @Mock
    private RestRequestFactory restFactory;

    /**
     * This class tests the getMasterDataTitles method
     */
    @Test
    public void getMasterDataTitlesTest() throws IdAuthenticationBusinessException {
        masterDataCache.getMasterDataTitles();
    }

    /**
     * This class tests the getMasterDataTitlesException method
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test(expected = IdAuthenticationBusinessException.class)
    public void getMasterDataTitlesExceptionTest1() throws IdAuthenticationBusinessException {
        Object[] args = {};
        IDDataValidationException exception = new IDDataValidationException("kj","as",args);
        Mockito.doThrow(exception).when(restFactory).buildRequest(RestServicesConstants.TITLE_SERVICE, null, Map.class);
        masterDataCache.getMasterDataTitles();
    }

    /**
     * This class tests the getMasterDataTemplate method
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test
    public void getMasterDataTemplateTest() throws IdAuthenticationBusinessException {
        String template = "1122";
        RestRequestDTO request = new RestRequestDTO();
        request.setUri("aaaaa");
        Mockito.when(restFactory.buildRequest(RestServicesConstants.ID_MASTERDATA_TEMPLATE_SERVICE_MULTILANG, null, Map.class)).thenReturn(request);
        masterDataCache.getMasterDataTemplate(template);
    }

    /**
     * This class tests the getMasterDataTemplateException method
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test(expected = IdAuthenticationBusinessException.class)
    public void getMasterDataTemplateExceptionTest() throws IdAuthenticationBusinessException {
        String template = "1122";
        Object[] args = {};
        IDDataValidationException exception = new IDDataValidationException("kj","as",args);
        Mockito.doThrow(exception).when(restFactory).buildRequest(RestServicesConstants.ID_MASTERDATA_TEMPLATE_SERVICE_MULTILANG, null, Map.class);
        masterDataCache.getMasterDataTemplate(template);
    }

    /**
     * This class tests the clearMasterDataTemplateCache method
     * This class tests the clearMasterDataTitlesCache method
     */
    @Test
    public void clearMasterData_CacheTest(){
        masterDataCache.clearMasterDataTemplateCache("1122");
        masterDataCache.clearMasterDataTitlesCache();
    }

}
