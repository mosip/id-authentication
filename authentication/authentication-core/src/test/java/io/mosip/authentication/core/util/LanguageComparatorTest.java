package io.mosip.authentication.core.util;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
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
        Assertions.assertEquals(0, languageComparator.compare(langCode1, langCode2));

        sortOnLanguageCodes.add(langCode1);
        Assertions.assertEquals(-1, languageComparator.compare(langCode1, langCode2));

        sortOnLanguageCodes.clear();
        sortOnLanguageCodes.add(langCode2);
        Assertions.assertEquals(1, languageComparator.compare(langCode1, langCode2));

        sortOnLanguageCodes.add(langCode1);
        Assertions.assertEquals(1, languageComparator.compare(langCode1, langCode2));
    }
}
