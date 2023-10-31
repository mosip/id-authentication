package io.mosip.authentication.esignet.integration.helper;

import io.mosip.esignet.core.dto.OIDCTransaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class VCITransactionHelperTest {

    @Mock
    CacheManager cacheManager;

    @Mock
    Cache cache=new NoOpCache("test");

    @InjectMocks
    VCITransactionHelper vciTransactionHelper;

    @Test
    public void getOAuthTransactionWithValidDetails_thenPass() throws Exception {
        ReflectionTestUtils.setField(vciTransactionHelper, "userinfoCache", "test");
        OIDCTransaction oidcTransaction = new OIDCTransaction();
        oidcTransaction.setTransactionId("test");
        Mockito.when(cacheManager.getCache(Mockito.anyString())).thenReturn(cache);
        Mockito.when(cache.get("test",OIDCTransaction.class)).thenReturn(oidcTransaction);
        vciTransactionHelper.getOAuthTransaction("test");

    }

    @Test
    public void getOAuthTransactionWithInValidDetails_thenFail() {
        try{
            vciTransactionHelper.getOAuthTransaction("test");
        }catch (Exception e){
            assert(e.getMessage().equals("cache_missing"));
        }


    }

}
