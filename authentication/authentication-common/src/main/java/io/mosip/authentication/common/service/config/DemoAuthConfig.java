package io.mosip.authentication.common.service.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.ReflectionUtils;

import io.mosip.authentication.common.service.impl.IdServiceImpl;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.demographics.spi.IDemoApi;
import io.mosip.kernel.demographics.spi.IDemoNormalizer;

@Configuration
public class DemoAuthConfig {

	private static Logger logger = IdaLogger.getLogger(IdServiceImpl.class);

	@Value("${mosip.demographic.sdk.api.classname}")
	private String demosdkClassName;

	@Value("${mosip.normalizer.sdk.api.classname}")
	private String normalizerClassName;

	@Bean
	public IDemoApi getDemoApiSDKInstance() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> object = Class.forName(demosdkClassName);
		Object[] args = new Object[0];
		Optional<Constructor<?>> result = ReflectionUtils.findConstructor(object, args);
		if (result.isPresent()) {
			Constructor<?> constructor = result.get();
			constructor.setAccessible(true);
			IDemoApi newInstance = (IDemoApi) constructor.newInstance(args);
			newInstance.init();
			return newInstance;
		} else {
			logger.error(IdAuthCommonConstants.SESSION_ID, DemoAuthConfig.class.getSimpleName(),
					"Didn't find demo api instance", demosdkClassName);
			return null;
		}
	}

	@Bean
	public IDemoNormalizer getNormalizerSDKInstance() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> object = Class.forName("io.mosip.demosdk.client.impl.spec_1_0.Normalizer_V_1_0");
		Object[] args = new Object[0];
		Optional<Constructor<?>> result = ReflectionUtils.findConstructor(object, args);
		if (result.isPresent()) {
			Constructor<?> constructor = result.get();
			constructor.setAccessible(true);
			IDemoNormalizer newInstance = (IDemoNormalizer) constructor.newInstance(args);
			return newInstance;
		} else {
			logger.error(IdAuthCommonConstants.SESSION_ID, DemoAuthConfig.class.getSimpleName(),
					"Didn't find normalizer instance", normalizerClassName);
			return null;
		}
	}
}
