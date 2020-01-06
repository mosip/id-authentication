package io.mosip.authentication.core.util;

import org.apache.commons.codec.EncoderException;
import org.junit.Test;

import io.mosip.authentication.core.dto.TextMatcherUtil;

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
