package io.mosip.authentication.core.spi.faceauth.provider;

import java.util.Base64;
import java.util.Map;

import org.springframework.core.env.Environment;

import com.google.gson.JsonSyntaxException;

/**
 * The Class FingerprintProvider - An Abstract class which contains default
 * implementation for calculating score based on ISO Template and Fingerprint
 * minutiae in Json format and also provides support for adding new fingerprint
 * providers.
 *
 * @author Dinesh Karuppiah.T
 */
public abstract class FaceProvider implements MosipFaceProvider {

	/** The Constant UNKNOWN. */
	private static final String UNKNOWN = "UNKNOWN";
	/**
	 * The Odd Uin constant
	 */
	private static final String ODD_UIN = "odduin";
	/**
	 * The Even Uin constant
	 */
	private static final String EVEN_UIN = "evenuin";

	/** The Constant IRISIMG_RIGHT_MATCH_VALUE. */
	private static final String FACE_MATCH_VALUE = ".faceimg.match.value";

	/** The Constant LEFTTEYE. */
	static final String FACE = "FACE"; // FIXME Hardcoded

	/** The environment. */
	private Environment environment;

	/** The Constant idvid. */
	private static final String IDVID = "idvid";

	/**
	 * Constructor for IrisProvider
	 * 
	 * @param environment
	 */
	public FaceProvider(Environment environment) {
		this.environment = environment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#scoreCalculator(byte[], byte[])
	 */
	@Override
	public double matchScoreCalculator(byte[] isoImage1, byte[] isoImage2) {
		try {
			return 0;
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
	public double matchScoreCalculator(String fingerImage1, String fingerImage2) {
		try {
			return 0;
		} catch (IllegalArgumentException | JsonSyntaxException e) {
			throw e;
			// TODO need to create and add exception
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public double matchImage(Object reqInfo, Object entityInfo) {

		if (reqInfo instanceof Map) {
			Map<String, String> reqInfoMap = (Map<String, String>) reqInfo;
			String uin = reqInfoMap.get(IDVID);
			String uinType = checkEvenOrOddUIN(uin);
			if (reqInfoMap.containsKey(FaceProvider.FACE) || reqInfoMap.keySet().stream().anyMatch(key -> key.startsWith(UNKNOWN))) {
				return environment.getProperty(uinType + FACE_MATCH_VALUE, Double.class);
			}
		}

		return 0;
	}

	/**
	 * Temporary mocking for iris score calculation un-till integration with SDK.
	 * Even UIN - LeftEye - Positive - RighetEye - Negative - Composite - Positive
	 * Odd UIN - LeftEye - Negative - RighetEye - Positive - Composite - Negative
	 * 
	 * @param uin the UIN
	 * @return the uin is even or odd.
	 */
	private String checkEvenOrOddUIN(String uin) {
		boolean evenUin = Integer.valueOf(String.valueOf(uin.charAt(uin.length() - 1))) % 2 == 0;
		return evenUin ? EVEN_UIN : ODD_UIN;
	}

	/**
	 * Decode value.
	 *
	 * @param value the value
	 * @return the byte[]
	 */
	static byte[] decodeValue(String value) {
		return Base64.getDecoder().decode(value);
	}

}
