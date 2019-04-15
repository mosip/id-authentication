package io.mosip.authentication.common.impl.face.provider;

import java.util.Map;

import org.springframework.core.env.Environment;

import io.mosip.authentication.core.spi.faceauth.provider.FaceProvider;


/**
 * @author Arun Bose S
 * 
 * The Class MorphoIrisProvider.
 */

public class MorphoFaceProvider extends FaceProvider {

	public MorphoFaceProvider(Environment environment) {
		super(environment);
	}

}
