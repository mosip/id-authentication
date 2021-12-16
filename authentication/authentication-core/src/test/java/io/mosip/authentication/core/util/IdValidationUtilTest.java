package io.mosip.authentication.core.util;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
public class IdValidationUtilTest {

    @InjectMocks
    private IdValidationUtil idValidationUtil;

    public void validateUinTest() throws IdAuthenticationBusinessException {
        String id="23";
        ReflectionTestUtils.setField(idValidationUtil, "uinLength", 2);
        idValidationUtil.validateUIN(id);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void validateUinTest1() throws IdAuthenticationBusinessException {
        String id=null;
        idValidationUtil.validateUIN(id);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void validateUinTest2() throws IdAuthenticationBusinessException {
        String id="id";
        ReflectionTestUtils.setField(idValidationUtil, "uinLength", 1);
        idValidationUtil.validateUIN(id);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void validateUinTest3() throws IdAuthenticationBusinessException {
        String id="111";
        ReflectionTestUtils.setField(idValidationUtil, "uinLength", 3);
        idValidationUtil.validateUIN(id);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void validateVidTest1() throws IdAuthenticationBusinessException {
        String id=null;
        idValidationUtil.validateVID(id);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void validateVidTest2() throws IdAuthenticationBusinessException {
        String id="id";
        ReflectionTestUtils.setField(idValidationUtil, "vidLength", 1);
        idValidationUtil.validateVID(id);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void validateVidTest3() throws IdAuthenticationBusinessException {
        String id="23";
        ReflectionTestUtils.setField(idValidationUtil, "vidLength", 2);
        idValidationUtil.validateVID(id);
    }
}
