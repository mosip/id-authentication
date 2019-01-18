package io.mosip.kernel.idrepo.provider.impl;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

public class FingerprintProviderTest {

	FingerprintProvider fp = new FingerprintProvider();

	@Test
	public void testConvertFIRtoFMR() {
		assertEquals(fp.convertFIRtoFMR(Collections.singletonList(new String("abcd"))),
				Collections.singletonList(new String("abcd")));
	}
}
