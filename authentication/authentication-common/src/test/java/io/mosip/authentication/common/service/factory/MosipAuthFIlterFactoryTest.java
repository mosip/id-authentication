package io.mosip.authentication.common.service.factory;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.authfilter.spi.IMosipAuthFilter;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebMvcTest
@RunWith(SpringRunner.class)
public class MosipAuthFIlterFactoryTest {

    @Mock
    private AutowireCapableBeanFactory beanFactory;

    @Mock
    private List<IMosipAuthFilter> authFilters;

    @Before
    public void Before(){
        MockitoAnnotations.initMocks(this);
    }

    //When getMosipAuthFilterClasses().length == 0
    @Test
    public void initTest0(){
        MosipAuthFilterFactory mosipAuthFilterFactory = Mockito.mock(MosipAuthFilterFactory.class, Mockito.CALLS_REAL_METHODS);
        MosipAuthFilterFactory mosipAuthFilterFactorySpy = Mockito.spy(mosipAuthFilterFactory);
        String[] str = {};
        Mockito.doReturn(str).when(mosipAuthFilterFactorySpy).getMosipAuthFilterClasses();
        mosipAuthFilterFactorySpy.init();
    }

    //When getMosipAuthFilterClasses().length != 0
    @Test
    public void initTest(){
        MosipAuthFilterFactory mosipAuthFilterFactory = Mockito.mock(MosipAuthFilterFactory.class, Mockito.CALLS_REAL_METHODS);
        MosipAuthFilterFactory mosipAuthFilterFactorySpy = Mockito.spy(mosipAuthFilterFactory);
        ReflectionTestUtils.setField(mosipAuthFilterFactorySpy, "beanFactory", beanFactory);
        String[] str = {"io.mosip.authentication.common.service.factory.IMosipAuthFilterTestImpl"};
        Mockito.doReturn(str).when(mosipAuthFilterFactorySpy).getMosipAuthFilterClasses();
        mosipAuthFilterFactorySpy.init();
    }

    @Test(expected = IdAuthUncheckedException.class)
    public void initExceptionTest(){
        MosipAuthFilterFactory mosipAuthFilterFactory = Mockito.mock(MosipAuthFilterFactory.class, Mockito.CALLS_REAL_METHODS);
        MosipAuthFilterFactory mosipAuthFilterFactorySpy = Mockito.spy(mosipAuthFilterFactory);
        String[] str = {"io.mosip.authentication.common.service.factory.IMosipAuthFilterTestImpl"};
        Mockito.doReturn(str).when(mosipAuthFilterFactorySpy).getMosipAuthFilterClasses();
        mosipAuthFilterFactorySpy.init();
    }

    @Test
    public void getEnabledAuthFiltersTest(){
        MosipAuthFilterFactory mosipAuthFilterFactory = Mockito.mock(MosipAuthFilterFactory.class, Mockito.CALLS_REAL_METHODS);
        mosipAuthFilterFactory.getEnabledAuthFilters();
    }
}
