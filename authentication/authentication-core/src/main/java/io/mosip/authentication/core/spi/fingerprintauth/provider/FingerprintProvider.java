package io.mosip.authentication.core.spi.fingerprintauth.provider;

import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

/**
 * @author Manoj SP
 *
 */
public abstract class FingerprintProvider implements MosipFingerprintProvider {

	@Override
	public double scoreCalculator(byte[] ISOImage1, byte[] ISOImage2) {
		try {
			FingerprintTemplate template1 = new FingerprintTemplate().convert(ISOImage1);
			FingerprintTemplate template2 = new FingerprintTemplate().convert(ISOImage2);
			FingerprintMatcher matcher = new FingerprintMatcher();
			return matcher.index(template1).match(template2);
		} catch (IllegalArgumentException e) {
			// FIXME add exception
			return 0;
		}
	}

	@Override
	public double scoreCalculator(String minutiae1, String minutiae2) {
		try {
			FingerprintTemplate template1 = new FingerprintTemplate().deserialize(minutiae1);
			FingerprintTemplate template2 = new FingerprintTemplate().deserialize(minutiae2);
			FingerprintMatcher matcher = new FingerprintMatcher();
			return matcher.index(template1).match(template2);
		} catch (IllegalArgumentException e) {
			// FIXME add exception
			return 0;
		}
	}
}
