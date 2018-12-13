package io.mosip.authentication.core.spi.fingerprintauth.provider;

import java.util.Base64;

import com.google.gson.JsonSyntaxException;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

/**
 * The Class FingerprintProvider - An Abstract class which contains default
 * implementation for calculating score based on ISO Template and Fingerprint
 * minutiae in Json format and also provides support for adding new fingerprint
 * providers.
 *
 * @author Manoj SP
 */
public abstract class FingerprintProvider implements MosipFingerprintProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#scoreCalculator(byte[], byte[])
	 */
	@Override
	public double scoreCalculator(byte[] isoImage1, byte[] isoImage2) {
		try {
			FingerprintTemplate template1 = new FingerprintTemplate().convert(isoImage1);
			FingerprintTemplate template2 = new FingerprintTemplate().convert(isoImage2);
			FingerprintMatcher matcher = new FingerprintMatcher();
			System.err.println("Threshold Value >>>" + matcher.index(template1).match(template2));
			return matcher.index(template1).match(template2);
		} catch (IllegalArgumentException e) {
			throw e;
			// TODO need to create and add exception
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#scoreCalculator(java.lang.String, java.lang.String)
	 */
	@Override
	public double scoreCalculator(String fingerImage1, String fingerImage2) {
		try {
			FingerprintTemplate template1 = new FingerprintTemplate().deserialize(fingerImage1);
			FingerprintTemplate template2 = new FingerprintTemplate().deserialize(fingerImage2);
			FingerprintMatcher matcher = new FingerprintMatcher();
			System.err.println("Threshold Value >>>" + matcher.index(template1).match(template2));
			return matcher.index(template1).match(template2);
		} catch (IllegalArgumentException | JsonSyntaxException e) {
			throw e;
			// TODO need to create and add exception
		}
	}

	public double matchMinutiea(String reqInfo, String entityInfo) {
		byte[] decodedrefInfo = decodeValue(reqInfo);
		byte[] decodeEntityInfo = decodeValue(entityInfo);
		FingerprintTemplate template1 = new FingerprintTemplate().convert(decodedrefInfo);
		FingerprintTemplate template2 = new FingerprintTemplate().convert(decodeEntityInfo);
		return this.scoreCalculator(template1.serialize(), template2.serialize());
	}

	public double matchImage(String reqInfo, String entityInfo) {
		byte[] decodedrefInfo = decodeValue(reqInfo);
		byte[] decodeEntityInfo = decodeValue(entityInfo);
		return this.scoreCalculator(decodedrefInfo, decodeEntityInfo);
	}

	private static byte[] decodeValue(String value) {
		return Base64.getDecoder().decode(value);
	}

}
