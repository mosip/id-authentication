package io.mosip.authentication.core.util;

import org.apache.commons.codec.EncoderException;
import org.junit.jupiter.api.Test;

public class TextMatcherUtilTest {

	@Test
	public void TestValidTextMatcher() throws EncoderException {
		TextMatcherUtil.phoneticsMatch("dinesh", "dinesh", "english");
	}

	@Test
	public void TestInValidTextMatcher() throws EncoderException {
		TextMatcherUtil.phoneticsMatch("dinesh", "esh", "english");
	}

}
