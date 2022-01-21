package io.mosip.authentication.core.util;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
public class LanguageComparatorTest {

    @InjectMocks
    private LanguageComparator languageComparator;

    /**
     * This class tests the compare method
     */
    @Test
    public void compareTest(){
        String langCode1 = "11";
        String langCode2 = "22";
        List<String> sortOnLanguageCodes= new ArrayList<>();
        ReflectionTestUtils.setField(languageComparator, "sortOnLanguageCodes", sortOnLanguageCodes);
        Assert.assertEquals(0, languageComparator.compare(langCode1, langCode2));

        sortOnLanguageCodes.add(langCode1);
        Assert.assertEquals(-1, languageComparator.compare(langCode1, langCode2));

        sortOnLanguageCodes.clear();
        sortOnLanguageCodes.add(langCode2);
        Assert.assertEquals(1, languageComparator.compare(langCode1, langCode2));

        sortOnLanguageCodes.add(langCode1);
        Assert.assertEquals(1, languageComparator.compare(langCode1, langCode2));
    }
}
