package io.mosip.authentication.core.spi.irisauth.provider;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public abstract class IrisProvider implements MosipIrisProvider {
	
	@Autowired
	private Environment environment;
	
	
	@Override  //TODO subject to change 
	public double matchScoreCalculator(byte[] isoImage1, byte[] isoImage2) {
		return 0;
	}

	
	@Override   //TODO Subject to change 
	public double matchScoreCalculator(String fingerImage1, String fingerImage2) {
		return 0;
	}
	
	
	public double matchIrisImage( Map<String, String> reqInfo, Map<String, String> entityInfo) {
		double matchScore=0;
		return matchScore;
	}

	
}
