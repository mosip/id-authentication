package io.mosip.authentication.common.service.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TokenEncoderUtilTest {



    @Test
    public void encodeBase58TestWithVaidDetails_thenPass(){
        String st="string";
        String result=TokenEncoderUtil.encodeBase58(st.getBytes());
        Assertions.assertEquals("zVbyBrMk",result);
    }

    @Test
    public void encodeBase58TestWithNull_thenFail(){
        String st="string";
        String result=TokenEncoderUtil.encodeBase58(null);
    }

}
