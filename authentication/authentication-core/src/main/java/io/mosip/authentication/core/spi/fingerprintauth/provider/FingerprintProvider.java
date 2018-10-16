package io.mosip.authentication.core.spi.fingerprintauth.provider;

import com.google.gson.JsonSyntaxException;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

/**
 * @author Manoj SP
 *
 */
public abstract class FingerprintProvider implements MosipFingerprintProvider {

	@Override
	public double scoreCalculator(byte[] isoImage1, byte[] isoImage2) {
		try {
			FingerprintTemplate template1 = new FingerprintTemplate().convert(isoImage1);
			FingerprintTemplate template2 = new FingerprintTemplate().convert(isoImage2);
			FingerprintMatcher matcher = new FingerprintMatcher();
			return matcher.index(template1).match(template2);
		} catch (IllegalArgumentException e) {
			//TODO need to create and add exception
			return 0;
		}
	}

	@Override
	public double scoreCalculator(String fingerImage1, String fingerImage2) {
		try {
			FingerprintTemplate template1 = new FingerprintTemplate().deserialize(fingerImage1);
			FingerprintTemplate template2 = new FingerprintTemplate().deserialize(fingerImage2);
			FingerprintMatcher matcher = new FingerprintMatcher();
			return matcher.index(template1).match(template2);
		} catch (IllegalArgumentException | JsonSyntaxException e) {
			//TODO need to create and add exception
			return 0;
		}
	}
}
