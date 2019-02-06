package io.mosip.kernel.core.transliteration.spi;

/**
 * This interface contains method that perform transliteration based on language
 * code provided.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 * @param <T>
 */
public interface Transliteration<T> {

	/**
	 * This method perform transliteration based on language code provided.
	 * 
	 * @param fromLanguage
	 *            the input language code.
	 * @param toLanguage
	 *            the output language code.
	 * @param text
	 *            the string to be transliterated.
	 * @return the transliterated string.
	 */
	public String transliterate(T fromLanguage, T toLanguage, String text);

}
