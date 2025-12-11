package io.mosip.authentication.common.service.helper;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SeparatorHelperTest {

    @Mock
    private Environment env;

    @InjectMocks
    private SeparatorHelper separatorHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetSeparator_WhenPropertyExists() {
        String idName = "phone";
        String expectedSeparator = ":";

        when(env.getProperty(
                IdAuthConfigKeyConstants.IDA_ID_ATTRIBUTE_SEPARATOR_PREFIX + idName,
                IdAuthCommonConstants.DEFAULT_ID_ATTRIBUTE_SEPARATOR_VALUE))
                .thenReturn(expectedSeparator);

        String result = separatorHelper.getSeparator(idName);

        assertEquals(expectedSeparator, result);
    }

    @Test
    public void testGetSeparator_WhenPropertyMissing_UsesDefault() {
        String idName = "email";

        when(env.getProperty(
                IdAuthConfigKeyConstants.IDA_ID_ATTRIBUTE_SEPARATOR_PREFIX + idName,
                IdAuthCommonConstants.DEFAULT_ID_ATTRIBUTE_SEPARATOR_VALUE))
                .thenReturn(IdAuthCommonConstants.DEFAULT_ID_ATTRIBUTE_SEPARATOR_VALUE);

        String result = separatorHelper.getSeparator(idName);

        assertEquals(IdAuthCommonConstants.DEFAULT_ID_ATTRIBUTE_SEPARATOR_VALUE, result);
    }
}
