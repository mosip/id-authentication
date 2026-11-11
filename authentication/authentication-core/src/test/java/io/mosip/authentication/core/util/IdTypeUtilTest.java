package io.mosip.authentication.core.util;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        Assertions.assertTrue(idTypeUtil.validateUin(uin));

        Mockito.doThrow(IdAuthenticationBusinessException.class).when(idValidator).validateUIN(uin);
        idTypeUtil.validateUin(uin);

        ReflectionTestUtils.setField(idTypeUtil, "idValidator", null);
        Assertions.assertFalse(idTypeUtil.validateUin(uin));
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
        Assertions.assertTrue(idTypeUtil.validateVid(vid));

        Mockito.doThrow(IdAuthenticationBusinessException.class).when(idValidator).validateVID(vid);
        idTypeUtil.validateVid(vid);

        ReflectionTestUtils.setField(idTypeUtil, "idValidator", null);
        Assertions.assertFalse(idTypeUtil.validateVid(vid));
    }

    /**
     * This class tests the getIdType method
     *                             in case of IdAuthenticationBusinessException
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test
    public void getIdTypeExceptionTest() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
        String id ="1122";
        idTypeUtil.getIdType(id);
    		});
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
        Assertions.assertEquals(IdType.VID, idTypeUtil.getIdType(id));

        Mockito.when(idValidator.validateUIN(id)).thenReturn(true);
        Assertions.assertEquals(IdType.UIN, idTypeUtil.getIdType(id));
    }
}
