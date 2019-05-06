package io.mosip.authentication.core.spi.provider.bio;

import java.util.Map;

import org.springframework.core.env.Environment;






/**
 * The Class CogentIrisProvider.
 * @author Arun Bose S
 */
public class CogentFaceProvider extends FaceProvider {

	
	public CogentFaceProvider(Environment environment) {
		super(environment);
	}
	
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider#createMinutiae(byte[])
	 */
	public String createMinutiae(byte[] inputImage) {
		return null;
	}

	@Override
	public double matchMinutiae(Object reqInfo, Object entityInfo) {
		return 0;
	}

	@Override
	public double matchMultiMinutae(Map<String, String> reqInfo, Map<String, String> entityInfo) {
		return 0;
	}


	@Override
	public double matchMultiImage(Object reqInfo, Object entityInfo) {
		return 0;
	}

	
}
