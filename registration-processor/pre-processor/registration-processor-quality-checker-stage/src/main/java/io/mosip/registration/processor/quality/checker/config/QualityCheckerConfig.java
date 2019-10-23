package io.mosip.registration.processor.quality.checker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;
import io.mosip.registration.processor.quality.checker.stage.QualityCheckerStage;

@PropertySource("classpath:bootstrap.properties")
@Configuration
public class QualityCheckerConfig {

	/** The environment. */
	@Autowired
	private Environment environment;

	private static final String FINGERPRINT_PROVIDER = "mosip.fingerprint.provider";
	private static final String FACE_PROVIDER = "mosip.face.provider";
	private static final String IRIS_PROVIDER = "mosip.iris.provider";

	@Bean
	public QualityCheckerStage getStage() {
		return new QualityCheckerStage();
	}

	@Bean("finger")
	public IBioApi getFingerProvider() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String bioApiProvider = environment.getProperty(FINGERPRINT_PROVIDER);
		if (bioApiProvider != null) {
			return (IBioApi) Class.forName(bioApiProvider).newInstance();
		} else {
			return null;
		}

	}

	@Bean("face")
	public IBioApi getFaceProvider() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String bioApiProvider = environment.getProperty(FACE_PROVIDER);
		if (bioApiProvider != null) {
			return (IBioApi) Class.forName(bioApiProvider).newInstance();
		} else {
			return null;
		}

	}

	@Bean("iris")
	public IBioApi getIrisProvider() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String bioApiProvider = environment.getProperty(IRIS_PROVIDER);
		if (bioApiProvider != null) {
			return (IBioApi) Class.forName(bioApiProvider).newInstance();
		} else {
			return null;
		}

	}

	@Bean
	public CbeffUtil getCbeffUtil() {
		return new CbeffImpl();
	}
}
