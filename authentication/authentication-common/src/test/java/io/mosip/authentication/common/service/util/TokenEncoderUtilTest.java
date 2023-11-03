package io.mosip.authentication.common.service.util;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TokenEncoderUtilTest {



    @Test
    public void encodeBase58TestWithVaidDetails_thenPass(){
        String st="string";
        String result=TokenEncoderUtil.encodeBase58(st.getBytes());
        Assert.assertEquals("zVbyBrMk",result);
    }

    @Test
    public void encodeBase58TestWithNull_thenFail(){
        String st="string";
        String result=TokenEncoderUtil.encodeBase58(null);
    }

}
