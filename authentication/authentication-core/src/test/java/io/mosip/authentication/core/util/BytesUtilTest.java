package io.mosip.authentication.core.util;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;


@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
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
        Assertions.assertArrayEquals(expected, bytesUtil.prependZeros(str, n));
    }

    /**
     * This class tests the getXOR method
     */
    @Test
    public void getXORTest(){
        //length of b > leangth of a
        String a = "test", b = "sample";
        byte[] expected = {115, 97, 25, 21, 31, 17};
        Assertions.assertArrayEquals(expected, ReflectionTestUtils.invokeMethod(bytesUtil, "getXOR", a, b));
        //length of a > leangth of b
        a = "sample";
        b = "test";
        Assertions.assertArrayEquals(expected, ReflectionTestUtils.invokeMethod(bytesUtil, "getXOR", a, b));
//        length of a == length of b
        byte[] exp = {0, 0, 0, 0, 0, 0 };
        b="sample";
        Assertions.assertArrayEquals(exp, ReflectionTestUtils.invokeMethod(bytesUtil, "getXOR", a, b));
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
        Assertions.assertArrayEquals(ans, ReflectionTestUtils.invokeMethod(bytesUtil, "getLastBytes", xorBytes, lastBytesNum));
    }
}
