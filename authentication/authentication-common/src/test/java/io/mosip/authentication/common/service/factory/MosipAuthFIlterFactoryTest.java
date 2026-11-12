package io.mosip.authentication.common.service.factory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.authfilter.spi.IMosipAuthFilter;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebMvcTest
@ExtendWith(SpringExtension.class)
public class MosipAuthFIlterFactoryTest {

    @Mock
    private AutowireCapableBeanFactory beanFactory;

    @Mock
    private List<IMosipAuthFilter> authFilters;

    @BeforeEach
    public void Before(){
        MockitoAnnotations.openMocks(this);
    }

    //When getMosipAuthFilterClasses().length == 0
    @Test
    public void initTest0(){
        MosipAuthFilterFactory mosipAuthFilterFactorySpy = Mockito.spy(MosipAuthFilterFactory.class);
        String[] str = {};
        Mockito.doReturn(str).when(mosipAuthFilterFactorySpy).getMosipAuthFilterClasses();
        mosipAuthFilterFactorySpy.init();
    }

    //When getMosipAuthFilterClasses().length != 0
    @Test
    public void initTest(){
        MosipAuthFilterFactory mosipAuthFilterFactorySpy = Mockito.spy(MosipAuthFilterFactory.class);
        ReflectionTestUtils.setField(mosipAuthFilterFactorySpy, "beanFactory", beanFactory);
        String[] str = {"io.mosip.authentication.common.service.factory.IMosipAuthFilterTestImpl"};
        Mockito.doReturn(str).when(mosipAuthFilterFactorySpy).getMosipAuthFilterClasses();
        mosipAuthFilterFactorySpy.init();
    }

    @Test
    public void initExceptionTest(){
		assertThrows(IdAuthUncheckedException.class, () -> {
        MosipAuthFilterFactory mosipAuthFilterFactorySpy = Mockito.spy(MosipAuthFilterFactory.class);
        String[] str = {"io.mosip.authentication.common.service.factory.IMosipAuthFilterTestImpl"};
        Mockito.doReturn(str).when(mosipAuthFilterFactorySpy).getMosipAuthFilterClasses();
        mosipAuthFilterFactorySpy.init();
    		});
	}

    @Test
    public void getEnabledAuthFiltersTest(){
        MosipAuthFilterFactory mosipAuthFilterFactory = Mockito.mock(MosipAuthFilterFactory.class, Mockito.CALLS_REAL_METHODS);
        mosipAuthFilterFactory.getEnabledAuthFilters();
    }
}
