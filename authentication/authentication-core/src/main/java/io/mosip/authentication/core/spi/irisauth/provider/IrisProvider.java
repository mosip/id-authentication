package io.mosip.authentication.core.spi.irisauth.provider;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


/**
 * 
 * @author Arun Bose S
 * The Class IrisProvider.
 */
public abstract class IrisProvider implements MosipIrisProvider {
	
	/** The environment. */
	@Autowired
	protected Environment environment;
	
     /** The Constant RIGHTEYE. */
     static final String RIGHTEYE="righteye";
	
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider#matchScoreCalculator(byte[], byte[])
	 */
	@Override  //TODO subject to change 
	public double matchScoreCalculator(byte[] isoImage1, byte[] isoImage2) {
		return 0;
	}

	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider#matchScoreCalculator(java.lang.String, java.lang.String)
	 */
	@Override   //TODO Subject to change 
	public double matchScoreCalculator(String fingerImage1, String fingerImage2) {
		return 0;
	}
	
	
	/**
	 * Match iris image.
	 *
	 * @param reqInfo the req info
	 * @param entityInfo the entity info
	 * @return the double
	 */
	public Double matchIrisImage( Map<String, String> reqInfo, Map<String, String> entityInfo) {
	 if(reqInfo.containsKey(IrisProvider.RIGHTEYE))
		 
	 {
		 System.err.println(environment.getProperty("irisimg.right.match.value",Double.class));
		 return environment.getProperty("irisimg.right.match.value",Double.class);
	 }
	 else
	 {	 
		 System.err.println(environment.getProperty("irisimg.left.match.value",Double.class));
		 return environment.getProperty("irisimg.left.match.value",Double.class);
	 }
		
	}

	
}
