package io.mosip.authentication.core.util;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Ref;

@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
public class IdTypeUtilTest {

    @InjectMocks
    private IdTypeUtil idTypeUtil;

    @Mock
    private IdValidationUtil idValidator;

    @Test
    public void validateUinTest() throws IdAuthenticationBusinessException {
        String uin= "1122";
        idTypeUtil.validateUin(uin);

        Mockito.doThrow(IdAuthenticationBusinessException.class).when(idValidator).validateUIN(uin);
        idTypeUtil.validateUin(uin);

        ReflectionTestUtils.setField(idTypeUtil, "idValidator", null);
        idTypeUtil.validateUin(uin);
    }

    @Test
    public void validateVidTest() throws IdAuthenticationBusinessException {
        String vid= "1122";
        idTypeUtil.validateVid(vid);

        Mockito.doThrow(IdAuthenticationBusinessException.class).when(idValidator).validateVID(vid);
        idTypeUtil.validateVid(vid);

        ReflectionTestUtils.setField(idTypeUtil, "idValidator", null);
        idTypeUtil.validateVid(vid);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void getIdTypeExceptionTest() throws IdAuthenticationBusinessException {
        String id ="1122";
        idTypeUtil.getIdType(id);
    }

    @Test
    public void getIdTypeTest() throws IdAuthenticationBusinessException {
        String id ="1122";
        Mockito.when(idValidator.validateVID(id)).thenReturn(true);
        idTypeUtil.getIdType(id);

        Mockito.when(idValidator.validateUIN(id)).thenReturn(true);
        idTypeUtil.getIdType(id);
    }
}
