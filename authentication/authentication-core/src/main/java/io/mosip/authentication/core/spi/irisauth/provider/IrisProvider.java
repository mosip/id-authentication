package io.mosip.authentication.core.spi.irisauth.provider;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Prem Kumar 
 * 
 * The Class IrisProvider.
 */
public abstract class IrisProvider implements MosipIrisProvider {

	/** The environment. */
	@Autowired
	protected Environment environment;

	private static final String IRISIMG_LEFT_MATCH_VALUE = "irisimg.left.match.value";

	private static final String IRISIMG_RIGHT_MATCH_VALUE = "irisimg.right.match.value";
	static final String LEFTTEYE = "leftEye";
	static final String RIGHTEYE = "rightEye";

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

	/**
	 * Match iris image.
	 *
	 * @param reqInfo
	 *            the req info
	 * @param entityInfo
	 *            the entity info
	 * @return the double
	 */
	public Double matchIrisImage(Map<String, String> reqInfo, Map<String, String> entityInfo) {
		String uin = reqInfo.get(IDVID);
		if (reqInfo.containsKey(IrisProvider.RIGHTEYE))

		{
			System.err.println(environment.getProperty(IRISIMG_RIGHT_MATCH_VALUE, Double.class));
			return environment.getProperty(uin + IRISIMG_RIGHT_MATCH_VALUE, Double.class);
		} else {
			System.err.println(environment.getProperty(IRISIMG_LEFT_MATCH_VALUE, Double.class));
			return environment.getProperty(uin + IRISIMG_LEFT_MATCH_VALUE, Double.class);
		}

	}

	/**
	 * Match multiMatch Iris Image
	 * 
	 * @param reqInfo
	 * @param entityInfo
	 * @return the double
	 */
	public double matchMultiIrisImage(Map<String, String> reqInfo, Map<String, String> entityInfo) {
		double match = 0;
		String uin = reqInfo.get(IDVID);
		if (entityInfo.containsKey(LEFTTEYE)) {
			match += environment.getProperty(uin + IRISIMG_LEFT_MATCH_VALUE, Double.class);
		}
		if (entityInfo.containsKey(RIGHTEYE)) {
			match += environment.getProperty(uin + IRISIMG_RIGHT_MATCH_VALUE, Double.class);
		}
		return match;
	}

}
