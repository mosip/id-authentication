package io.mosip.authentication.core.spi.irisauth.provider;

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
		this.environment=environment;
		}

	/** The Constant IRISIMG_LEFT_MATCH_VALUE. */
	private static final String IRISIMG_LEFT_MATCH_VALUE = ".irisimg.left.match.value";

	/** The Constant IRISIMG_RIGHT_MATCH_VALUE. */
	private static final String IRISIMG_RIGHT_MATCH_VALUE = ".irisimg.right.match.value";
	
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

	
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider#matchImage(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double matchImage(Object reqInfo, Object entityInfo) {
		
		if(reqInfo instanceof Map)
		{
		    Map<String,String> reqInfoMap=(Map<String,String>)  reqInfo;
		    Map<String, Object> reqProperties=(Map<String,Object>) entityInfo;
		    String uin = (String)reqProperties.get(IDVID);
			if (reqInfoMap.containsKey(IrisProvider.RIGHTEYE))

		{
			System.err.println(uin +environment.getProperty(IRISIMG_RIGHT_MATCH_VALUE, Double.class));
			return environment.getProperty(uin + IRISIMG_RIGHT_MATCH_VALUE, Double.class);
		} else {
			System.err.println(uin +environment.getProperty(IRISIMG_LEFT_MATCH_VALUE, Double.class));
			return environment.getProperty(uin + IRISIMG_LEFT_MATCH_VALUE, Double.class);
		}
		
		}
		
		else
			return 0;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider#matchMultiImage(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double matchMultiImage(Object reqInfo, Object props) {
		double match = 0;
		if(reqInfo instanceof Map && props instanceof Map) {
			String uin = ((Map<String,String>) props).get(IDVID);
			Map<String,String> reqInfoDetail = (Map<String,String>) reqInfo;
			if (reqInfoDetail.containsKey(LEFTTEYE)) {
				match += environment.getProperty(uin + IRISIMG_LEFT_MATCH_VALUE, Double.class);
			}
			if (reqInfoDetail.containsKey(RIGHTEYE)) {
				match += environment.getProperty(uin + IRISIMG_RIGHT_MATCH_VALUE, Double.class);
			}			
		}
		return match;
	}
}
