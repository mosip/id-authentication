package io.mosip.authentication.core.spi.provider.bio;

import org.springframework.core.env.Environment;


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
