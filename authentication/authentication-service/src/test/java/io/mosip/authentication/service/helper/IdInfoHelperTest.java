package io.mosip.authentication.service.helper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;

@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
public class IdInfoHelperTest {

	@InjectMocks
	IdInfoHelper idInfoHelper;

	@Autowired
	private Environment environment;

	@Before
	public void before() {
		ReflectionTestUtils.setField(idInfoHelper, "environment", environment);
	}

	@Test
	public void TestgetLanguageName() {
		String langCode = "ar";
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) environment));
		mockenv.setProperty("mosip.phonetic.lang.".concat(langCode.toLowerCase()), "arabic-ar");
		mockenv.setProperty("mosip.phonetic.lang.ar", "arabic-ar");
		ReflectionTestUtils.setField(idInfoHelper, "environment", mockenv);
		Optional<String> languageName = idInfoHelper.getLanguageName(langCode);
		String value = languageName.get();
		assertEquals("arabic", value);
	}

	@Test
	public void TestgetLanguageCode() {
		String priLangCode = "mosip.primary.lang-code";
		String secLangCode = "mosip.secondary.lang-code";
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) environment));
		mockenv.setProperty(priLangCode, "AR");
		mockenv.setProperty(secLangCode, "FR");
		String languageCode = idInfoHelper.getLanguageCode(LanguageType.PRIMARY_LANG);
		assertEquals("AR", languageCode);
		String languageCode2 = idInfoHelper.getLanguageCode(LanguageType.SECONDARY_LANG);
		assertEquals("FR", languageCode2);
	}

}
