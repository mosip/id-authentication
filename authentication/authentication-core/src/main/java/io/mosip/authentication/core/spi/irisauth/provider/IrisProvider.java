package io.mosip.authentication.core.spi.irisauth.provider;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.env.Environment;

// TODO: Auto-generated Javadoc
/**
 * The Class IrisProvider.
 *
 * @author Prem Kumar The Class IrisProvider.
 */
public abstract class IrisProvider implements MosipIrisProvider {

	/** The environment. */
	private Environment environment;

	public IrisProvider(Environment environment) {
		this.environment = environment;
	}

	/** The Constant IRISIMG_LEFT_MATCH_VALUE. */
	private static final String IRISIMG_LEFT_MATCH_VALUE = ".irisimg.left.match.value";

	/** The Constant IRISIMG_RIGHT_MATCH_VALUE. */
	private static final String IRISIMG_RIGHT_MATCH_VALUE = ".irisimg.right.match.value";

	private static final String DEFAULT_LEFT_IRIS_MATCH_VALUE = "default.irisimg.left.match.value";

	private static final String DEFAULT_RIGHT_IRIS_MATCH_VALUE = "default.irisimg.right.match.value";

	/** The Constant LEFTTEYE. */
	static final String LEFTTEYE = "lefteye";

	/** The Constant RIGHTEYE. */
	static final String RIGHTEYE = "righteye";

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
			if (reqInfoMap.containsKey(IrisProvider.RIGHTEYE)) {
				Double irisValue = environment.getProperty(uin + IRISIMG_RIGHT_MATCH_VALUE, Double.class);
				if (null == irisValue)
                 {
					return environment.getProperty(DEFAULT_RIGHT_IRIS_MATCH_VALUE, Double.class);
				}
				return irisValue;
			} else if (reqInfoMap.containsKey(IrisProvider.LEFTTEYE)) {
				Double irisValue = environment.getProperty(uin + IRISIMG_LEFT_MATCH_VALUE, Double.class);
				if (null == irisValue)
				{
					return environment.getProperty(DEFAULT_LEFT_IRIS_MATCH_VALUE, Double.class);
				}
				return irisValue;
			}

		}

		return 0;
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
			String uin = ((Map<String, String>) entityInfo).get(IDVID);
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
