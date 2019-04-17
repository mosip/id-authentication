package io.mosip.authentication.core.spi.provider.bio;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.core.env.Environment;

/**
 * The Class IrisProvider.
 *
 * @author Prem Kumar
 * 
 * 
 */
public abstract class IrisProvider implements MosipIrisProvider {
	
	/** The constant ODD_UIN */
	private static final String ODD_UIN = "odduin";

	/** The constant EVEN_UIN */
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
	static final String LEFTTEYE = "LEFT"; //FIXME Hardcoded

	/** The Constant RIGHTEYE. */
	static final String RIGHTEYE = "RIGHT"; //FIXME Hardcoded
	
	/** The Constant UNKNOWNEYE. */
	static final String UNKNOWNEYE = "UNKNOWN"; //FIXME Hardcoded

	/** The Constant IDVID. */
	private static final String IDVID = "idvid";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider#
	 * matchScoreCalculator(byte[], byte[])
	 */
	@Override
	public double matchScoreCalculator(byte[] isoImage1, byte[] isoImage2) {
		// TODO subject to change on device integration.
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider#
	 * matchScoreCalculator(java.lang.String, java.lang.String)
	 */
	@Override
	public double matchScoreCalculator(String fingerImage1, String fingerImage2) {
		// TODO subject to change on device integration.
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider#
	 * matchImage(java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public double matchImage(Object reqInfo, Object entityInfo) {

		if (reqInfo instanceof Map) {
			Map<String, String> reqInfoMap = (Map<String, String>) reqInfo;
			String uin = reqInfoMap.get(IDVID);
			String uinType = checkEvenOrOddUIN(uin);
			
			Map<String, String> entityInfoMap = (Map<String, String>) entityInfo;

			if (entityInfoMap.containsKey(IrisProvider.RIGHTEYE)) {
				return environment.getProperty(uinType + IRISIMG_RIGHT_MATCH_VALUE, Double.class);
			} else if (entityInfoMap.containsKey(IrisProvider.LEFTTEYE)) {
				return environment.getProperty(uinType + IRISIMG_LEFT_MATCH_VALUE, Double.class);
			}
		}

		return 0;
	}

	/**
	 * Temporary mocking for iris score calculation un-till integration with SDK.
	 * Even UIN - LeftEye - Positive - RighetEye - Negative - Composite - Positive
	 * Odd UIN - LeftEye - Negative - RighetEye - Positive - Composite - Negative
	 * 
	 * @param uin
	 *            the UIN
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
	@SuppressWarnings("unchecked")
	@Override
	public double matchMultiImage(Object reqInfo, Object entityInfo) {
		double match = 0;
		if (reqInfo instanceof Map && entityInfo instanceof Map) {
			Map<String, String> reqInfoMap = (Map<String, String>) reqInfo;
			if (reqInfoMap.keySet().stream().noneMatch(key -> key.startsWith(UNKNOWNEYE))) {
				String uin = reqInfoMap.get(IDVID);
				for (Entry<String, String> entry : reqInfoMap.entrySet()) {
					if(!entry.getKey().equals(IDVID)) {
						Map<String, String> requestInfo = new HashMap<>();
						requestInfo.put(entry.getKey(), entry.getValue());
						requestInfo.put(IDVID, uin);
						match += matchImage(requestInfo, entityInfo);
					}
				} 
			} else {
				match = matchUnknownImage(entityInfo, match, reqInfoMap);
			}
		}
		return match;
	}

	/**
	 * Match unknown image.
	 *
	 * @param entityInfo the entity info
	 * @param match the match
	 * @param reqInfoMap the req info map
	 * @return the double
	 */
	@SuppressWarnings("unchecked")
	private double matchUnknownImage(Object entityInfo, double match, Map<String, String> reqInfoMap) {
		double score = 0;
		double individualScore=0;
		Map<String, String> entityInfoMap = (Map<String, String>) entityInfo;
		String uin = reqInfoMap.get(IDVID);
		for (Entry<String, String> reqEntry : reqInfoMap.entrySet()) {
			if (!reqEntry.getKey().equals(IDVID)) {
				for (Entry<String, String> entry : entityInfoMap.entrySet()) {
					Map<String, String> requestInfo = new HashMap<>();
					Map<String, String> entityMap = new HashMap<>();
					requestInfo.put(reqEntry.getKey(), reqEntry.getValue());
					requestInfo.put(IDVID, uin);
					entityMap.put(entry.getKey(), entry.getValue());
					score = matchImage(requestInfo, entityMap);
					if (score > individualScore) {
						individualScore = score;
					}
				} 
				match += individualScore;
				individualScore =0;
			}
		}
		return match;
	}
}
