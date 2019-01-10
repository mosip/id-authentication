package io.mosip.authentication.service.impl.iris;

import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.spi.irisauth.provider.IrisProvider;


/**
 * @author Arun Bose S
 * 
 * The Class MorphoIrisProvider.
 */

public class MorphoIrisProvider extends IrisProvider {

	public MorphoIrisProvider(Environment environment) {
		super(environment);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider#createMinutiae(byte[])
	 */
	@Override
	public String createMinutiae(byte[] inputImage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double matchMinutiae(Object reqInfo, Object entityInfo) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double matchMultiMinutae(Map<String, String> reqInfo, Map<String, String> entityInfo) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double matchMultiImage(Object reqInfo, Object entityInfo) {
		// TODO Auto-generated method stub
		return 0;
	}

}
