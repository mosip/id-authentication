package io.mosip.kerneel.transliteration.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.transliteration.spi.Transliteration;
import io.mosip.kernel.transliteration.TransliterationBootApplication;
import io.mosip.kernel.transliteration.exception.InvalidTransliterationException;

/**
 * This Unit test class contains test methods for transliteration
 * implementation.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@SpringBootTest(classes = { TransliterationBootApplication.class })
@RunWith(SpringRunner.class)
public class TransliterationImplTest {

	/**
	 * 
	 * Key for arabic language.
	 */
	@Value("${mosip.kernel.transliteration.arabic-language-code}")
	private String arabicLanguageCode;

	/**
	 * Key for french language.
	 */
	@Value("${mosip.kernel.transliteration.franch-language-code}")
	private String frenchLanguageCode;

	/**
	 * Reference to {@link Transliteration}.
	 */
	@Autowired
	private Transliteration<String> transliterateImpl;

	/**
	 * This method test successfull transliteration of provided string as mention by
	 * language code.
	 */
	@Test
	public void transliterateTest() {

		String frenchToArabic = transliterateImpl.transliterate(frenchLanguageCode, arabicLanguageCode, "Bienvenue");

		assertThat(frenchToArabic, isA(String.class));

	}

	/**
	 * This method test for invalid input language code provided by user.
	 */
	@Test(expected = InvalidTransliterationException.class)
	public void transliterateInvalidInputLanguageCodeExceptionTest() {
		transliterateImpl.transliterate("dnjksd", "ara", "Bienvenue");
	}

	/**
	 * This method test for invalid to language code provided by user.
	 */
	@Test(expected = InvalidTransliterationException.class)
	public void transliterateInvalidOutputLanguageCodeExceptionTest() {
		transliterateImpl.transliterate("fra", "aradkn", "Bienvenue");
	}
}
