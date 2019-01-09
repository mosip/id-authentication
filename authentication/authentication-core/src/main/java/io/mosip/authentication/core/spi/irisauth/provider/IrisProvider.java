package io.mosip.authentication.core.spi.irisauth.provider;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

// TODO: Auto-generated Javadoc
/**
 * The Class IrisProvider.
 *
 * @author Prem Kumar The Class IrisProvider.
 */
public abstract class IrisProvider implements MosipIrisProvider {

	/** The environment. */
	@Autowired
	protected Environment environment;

	/** The Constant IRISIMG_LEFT_MATCH_VALUE. */
	private static final String IRISIMG_LEFT_MATCH_VALUE = "irisimg.left.match.value";

	/** The Constant IRISIMG_RIGHT_MATCH_VALUE. */
	private static final String IRISIMG_RIGHT_MATCH_VALUE = "irisimg.right.match.value";
	
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

	
	
	@Override
	public double matchImage(Object reqInfo, Object entityInfo) {
		
		if(reqInfo instanceof Map)
		{
		    Map<String,String> reqInfoMap=(Map<String,String>)  reqInfo;
		    String uin = reqInfoMap.get(IDVID);
			if (reqInfoMap.containsKey(IrisProvider.RIGHTEYE))

		{
			System.err.println(environment.getProperty(IRISIMG_RIGHT_MATCH_VALUE, Double.class));
			return environment.getProperty(uin + IRISIMG_RIGHT_MATCH_VALUE, Double.class);
		} else {
			System.err.println(environment.getProperty(IRISIMG_LEFT_MATCH_VALUE, Double.class));
			return environment.getProperty(uin + IRISIMG_LEFT_MATCH_VALUE, Double.class);
		}
		
		}
		
		else
			return 0;
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
