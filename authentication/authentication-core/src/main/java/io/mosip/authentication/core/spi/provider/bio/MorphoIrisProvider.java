package io.mosip.authentication.core.spi.provider.bio;

import java.util.Map;

import org.springframework.core.env.Environment;

import io.mosip.authentication.core.spi.irisauth.provider.IrisProvider;


/**
 * @author Arun Bose S
 * 
 * The Class MorphoIrisProvider.
 */

public class MorphoIrisProvider extends IrisProvider {

	public MorphoIrisProvider(Environment environment) {
		super(environment);
	}

}
