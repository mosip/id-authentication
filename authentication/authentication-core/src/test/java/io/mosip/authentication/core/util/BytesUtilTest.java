package io.mosip.authentication.core.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;


@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
public class BytesUtilTest {

    @InjectMocks
    private BytesUtil bytesUtil;

    /**
     * This class tests the prependZeros method
     */
    @Test
    public void prependZerosTest(){
        byte[] str = {(byte)0xe0, 0x4f, (byte)0xd0,
                0x20, (byte)0xea};
        int n=3;
        //[B@39dec536
        byte[] expected = {0, 0, 0, -32, 79, -48, 32, -22 };
        Assert.assertArrayEquals(expected, bytesUtil.prependZeros(str, n));
    }

    /**
     * This class tests the getXOR method
     */
    @Test
    public void getXORTest(){
        //length of b > leangth of a
        String a = "test", b = "sample";
        byte[] expected = {115, 97, 25, 21, 31, 17};
        Assert.assertArrayEquals(expected, ReflectionTestUtils.invokeMethod(bytesUtil, "getXOR", a, b));
        //length of a > leangth of b
        a = "sample";
        b = "test";
        Assert.assertArrayEquals(expected, ReflectionTestUtils.invokeMethod(bytesUtil, "getXOR", a, b));
//        length of a == length of b
        byte[] exp = {0, 0, 0, 0, 0, 0 };
        b="sample";
        Assert.assertArrayEquals(exp, ReflectionTestUtils.invokeMethod(bytesUtil, "getXOR", a, b));
    }

    /**
     * This class tests the getLastBytes method
     */
    @Test
    public void getLastBytesTest(){
        byte[] xorBytes = {(byte)0xe0, 0x4f, (byte)0xd0,
                0x20, (byte)0xea};
        int lastBytesNum = 1;
        byte[] ans = {-22};
        Assert.assertArrayEquals(ans, ReflectionTestUtils.invokeMethod(bytesUtil, "getLastBytes", xorBytes, lastBytesNum));
    }
}
