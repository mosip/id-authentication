package io.mosip.authentication.core.util;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import org.junit.Assert;
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


@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
public class IdTypeUtilTest {

    @InjectMocks
    private IdTypeUtil idTypeUtil;

    @Mock
    private IdValidationUtil idValidator;

    /**
     * This class tests the validateUin method
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test
    public void validateUinTest() throws IdAuthenticationBusinessException {
        String uin= "1122";
        Mockito.doReturn(true).when(idValidator).validateUIN(uin);
        Assert.assertTrue(idTypeUtil.validateUin(uin));

        Mockito.doThrow(IdAuthenticationBusinessException.class).when(idValidator).validateUIN(uin);
        idTypeUtil.validateUin(uin);

        ReflectionTestUtils.setField(idTypeUtil, "idValidator", null);
        Assert.assertFalse(idTypeUtil.validateUin(uin));
    }

    /**
     * This class tests the validateVid method
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test
    public void validateVidTest() throws IdAuthenticationBusinessException {
        String vid= "1122";
        Mockito.doReturn(true).when(idValidator).validateVID(vid);
        Assert.assertTrue(idTypeUtil.validateVid(vid));

        Mockito.doThrow(IdAuthenticationBusinessException.class).when(idValidator).validateVID(vid);
        idTypeUtil.validateVid(vid);

        ReflectionTestUtils.setField(idTypeUtil, "idValidator", null);
        Assert.assertFalse(idTypeUtil.validateVid(vid));
    }

    /**
     * This class tests the getIdType method
     *                             in case of IdAuthenticationBusinessException
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test(expected = IdAuthenticationBusinessException.class)
    public void getIdTypeExceptionTest() throws IdAuthenticationBusinessException {
        String id ="1122";
        idTypeUtil.getIdType(id);
    }

    /**
     * This class tests the getIdType method
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test
    public void getIdTypeTest() throws IdAuthenticationBusinessException {
        String id ="1122";
        Mockito.when(idValidator.validateVID(id)).thenReturn(true);
        Assert.assertEquals(IdType.VID, idTypeUtil.getIdType(id));

        Mockito.when(idValidator.validateUIN(id)).thenReturn(true);
        Assert.assertEquals(IdType.UIN, idTypeUtil.getIdType(id));
    }
}
