package io.mosip.authentication.core.spi.irisauth.provider;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.env.Environment;

/**
 * The Class IrisProvider.
 *
 * @author Prem Kumar 
 * 
 * 
 */
public abstract class IrisProvider implements MosipIrisProvider {

	private static final String ODD_UIN = "odduin";

	private static final String EVEN_UIN = "evenuin";

	/** The environment. */
	private Environment environment;
	/**
	 * Constructor for IrisProvider
	 * 
	 * @param environment
	 */
	public IrisProvider(Environment environment) {
		this.environment = environment;
	}

	/** The Constant IRISIMG_LEFT_MATCH_VALUE. */
	private static final String IRISIMG_LEFT_MATCH_VALUE = ".irisimg.left.match.value";

	/** The Constant IRISIMG_RIGHT_MATCH_VALUE. */
	private static final String IRISIMG_RIGHT_MATCH_VALUE = ".irisimg.right.match.value";

	/** The Constant LEFTTEYE. */
	static final String LEFTTEYE = "leftEye";

	/** The Constant RIGHTEYE. */
	static final String RIGHTEYE = "rightEye";

	/** The Constant idvid. */
	private static final String IDVID = "idvid";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider#
	 * matchScoreCalculator(byte[], byte[])
	 */
	@Override // TODO subject to change
	public double matchScoreCalculator(byte[] isoImage1, byte[] isoImage2) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider#
	 * matchScoreCalculator(java.lang.String, java.lang.String)
	 */
	@Override // TODO Subject to change
	public double matchScoreCalculator(String fingerImage1, String fingerImage2) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider#
	 * matchImage(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double matchImage(Object reqInfo, Object entityInfo) {

		if (reqInfo instanceof Map) {
			Map<String, String> reqInfoMap = (Map<String, String>) reqInfo;
			String uin = (String) reqInfoMap.get(IDVID);
			String uinType = checkEvenOrOddUIN(uin);
			if (reqInfoMap.containsKey(IrisProvider.RIGHTEYE)) {
				return environment.getProperty(uinType + IRISIMG_RIGHT_MATCH_VALUE, Double.class);
			} else if (reqInfoMap.containsKey(IrisProvider.LEFTTEYE)) {
				return environment.getProperty(uinType + IRISIMG_LEFT_MATCH_VALUE, Double.class);
			}
		}

		return 0;
	}

	/**
	 * Temporary mocking for iris score calculation un-till integration with SDK.
	 * Even UIN - LeftEye   - Positive
	 * 			- RighetEye - Negative
	 *          - Composite - Positive
	 * Odd UIN - LeftEye   - Negative
	 * 		   - RighetEye - Positive
	 *         - Composite - Negative
	 * @param uin the UIN
	 * @return the uin is even or odd.
	 */
	private String checkEvenOrOddUIN(String uin) {
		boolean evenUin = Integer.valueOf(String.valueOf(uin.charAt(uin.length() - 1))) % 2 == 0;
		return evenUin ? EVEN_UIN : ODD_UIN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider#
	 * matchMultiImage(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double matchMultiImage(Object reqInfo, Object entityInfo) {
		double match = 0;
		if (reqInfo instanceof Map && entityInfo instanceof Map) {
			String uin = ((Map<String, String>) reqInfo).get(IDVID);
			Map<String, String> reqInfoMap = (Map<String, String>) reqInfo;
			if (reqInfoMap.containsKey(LEFTTEYE)) {
				Map<String, String> requestInfo = new HashMap<>();
				requestInfo.put(LEFTTEYE, reqInfoMap.get(LEFTTEYE));
				requestInfo.put(IDVID, uin);
				match += matchImage(requestInfo, entityInfo);
			}
			if (reqInfoMap.containsKey(RIGHTEYE)) {
				Map<String, String> requestInfo = new HashMap<>();
				requestInfo.put(RIGHTEYE, reqInfoMap.get(RIGHTEYE));
				requestInfo.put(IDVID, uin);
				match += matchImage(requestInfo, entityInfo);
			}
		}
		return match;
	}
}
